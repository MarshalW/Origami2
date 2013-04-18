package com.example.origami;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;
import static com.example.origami.OrigamiUtils.loadBitmapFromView;

/**
 * Created with IntelliJ IDEA.
 * User: marshal
 * Date: 13-4-18
 * Time: 下午5:44
 * To change this template use File | Settings | File Templates.
 */
public class ResultOrigamiView extends GLSurfaceView implements GLSurfaceView.Renderer {

    int width, height;

    float ratio;

    float[] projectionMatrix = new float[16];

    View targetView;

    Bitmap targetViewTopBitmap, targetViewBottomBitmap;

    long duration = 400;

    Mesh mesh;

    ShadowMesh shadowMesh;

    Object[] vertexArray;

    Vertex[] shadowArray;

    float factor;

    Handler handler = new Handler();

    public ResultOrigamiView(Context context, View targetView) {
        super(context);
        this.targetView = targetView;
        this.init();
    }

    private void init() {
        this.setEGLContextClientVersion(2);

        this.setEGLConfigChooser(8, 8, 8, 8, 0, 0);
        this.setZOrderOnTop(true);
        this.getHolder().setFormat(PixelFormat.TRANSPARENT);

        this.setRenderer(this);
        this.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        this.targetView.addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                targetView.removeOnLayoutChangeListener(this);
                Bitmap targetViewBitmap = loadBitmapFromView(targetView, targetView.getWidth(), targetView.getHeight());
                setTowHalfBitmap(targetViewBitmap);
                targetViewBitmap.recycle();
            }
        });
    }

    private void setTowHalfBitmap(Bitmap bitmap) {
        if (targetViewTopBitmap != null) {
            targetViewTopBitmap.recycle();
            targetViewBottomBitmap.recycle();
        }
        targetViewTopBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight() / 2);
        targetViewBottomBitmap = Bitmap.createBitmap(bitmap, 0, bitmap.getHeight() / 2,
                bitmap.getWidth(), bitmap.getHeight() / 2);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        glClearColor(0, 0, 0, 0);

        this.mesh = new Mesh(getContext());
        this.shadowMesh = new ShadowMesh(getContext());
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        this.width = width;
        this.height = height;

        glViewport(0, 0, width, height);

        ratio = width / (float) height;
        Matrix.orthoM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, -10f, 10f);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        glClear(GL_COLOR_BUFFER_BIT);

        if (vertexArray != null) {
            for (int i = 0; i < vertexArray.length; i++) {
                Object[] array = (Object[]) vertexArray[i];
                mesh.draw(projectionMatrix, (Vertex[]) array[0], (Bitmap) array[1]);
            }
            if (shadowArray != null) {
                shadowMesh.draw(projectionMatrix, shadowArray, factor);
            }
        }
    }

    public void startAnimation(final boolean openIt,
                               final ResultViewController.ResultCallback callback) {
        //当target view已经打开，不再次打开
        if (openIt && targetView.getVisibility() == View.VISIBLE) {
            return;
        }

        //当target view已关闭，不再次关闭
        if (!openIt && targetView.getVisibility() == View.INVISIBLE) {
            return;
        }

        ValueAnimator animator = null;

        if (openIt) {
            animator = ValueAnimator.ofFloat(0, 1);
        } else {
            //重新截图
            targetView.setDrawingCacheEnabled(true);
            Bitmap targetViewBitmap = Bitmap.createBitmap(targetView.getDrawingCache());
            targetView.setDrawingCacheEnabled(false);
            setTowHalfBitmap(targetViewBitmap);
            targetViewBitmap.recycle();

            animator = ValueAnimator.ofFloat(1, 0);
        }

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (!openIt) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            targetView.setVisibility(View.INVISIBLE);
                        }
                    }, 50);

                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (openIt) {
                    targetView.setVisibility(View.VISIBLE);
                    callback.opened();
                } else {
                    callback.closed();
                }

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        queueAndRender(null, null, 0);
                    }
                }, 10);
            }
        });

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                generateAnimateFrame((Float) valueAnimator.getAnimatedValue());
            }
        });

        animator.setDuration(duration);
        animator.start();
    }

    private void generateAnimateFrame(float factor) {
        float angle = 90f * factor;
        //上半部分
        float left, top, right, bottom;
        left = -ratio;
        top = 1;
        right = ratio;
        bottom = 0;
        RectF rect = new RectF(left, top, right, bottom);
        Vertex[] topVertexArray = rotateAngle(rect, angle, true);

        float dy = topVertexArray[0].positionY - topVertexArray[1].positionY;

        rect = new RectF(left, top, right, bottom);
        Vertex[] bottomVertexArray = rotateAngle(rect, angle, false);

        //所有下半部分顶点，下移dy
        for (Vertex v : bottomVertexArray) {
            v.positionY -= dy;
        }

        queueAndRender(new Object[]{
                new Object[]{
                        topVertexArray, targetViewTopBitmap
                },
                new Object[]{
                        bottomVertexArray, targetViewBottomBitmap
                }
        }, bottomVertexArray, factor);
    }

    private Vertex[] rotateAngle(RectF rect, float angle, boolean isTop) {
        float x, y, nx, ny, nz, radian, distance, dx, dy, topLeft, topRight, top, bottom;
        distance = 5;
        radian = (float) Math.PI / 180 * angle;
        x = Math.abs(rect.width());
        y = Math.abs(rect.height());
        ny = (float) Math.sin(radian) * y;
        nz = (float) Math.cos(radian) * y;
        nx = nz / (distance - nz) * 2 / x;

        dx = Math.abs(nx);
        dy = Math.abs(y - ny);

        top = 1;
        bottom = dy;
        topLeft = rect.left + dx;
        topRight = rect.right - dx;

        if (isTop) {
            return new Vertex[]{
                    new Vertex(rect.left, top, 0),
                    new Vertex(topLeft, bottom, 0),
                    new Vertex(rect.right, top, 0),
                    new Vertex(topRight, bottom, 0)
            };
        } else {
            return new Vertex[]{
                    new Vertex(topLeft, top, 0),
                    new Vertex(rect.left, bottom, 0),
                    new Vertex(topRight, top, 0),
                    new Vertex(rect.right, bottom, 0)
            };
        }
    }

    private void queueAndRender(final Object[] vertexArray, final Vertex[] shadowArray, final float factor) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                ResultOrigamiView.this.vertexArray = vertexArray;
                ResultOrigamiView.this.shadowArray = shadowArray;
                ResultOrigamiView.this.factor = factor;
                requestRender();
            }
        });
    }
}
