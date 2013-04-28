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
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;

/**
 * Created with IntelliJ IDEA.
 * User: marshal
 * Date: 13-4-27
 * Time: 上午9:09
 * To change this template use File | Settings | File Templates.
 */
public class SearchOrigamiView extends GLSurfaceView implements GLSurfaceView.Renderer {

    protected int width, height;

    protected float ratio, factor, distance = 5;

    private long duration;

    private Mesh mesh;

    private ShadowMesh shadowMesh;

    private Object[] vertexArray;

    private Vertex[] shadowArray;

    private float[] projectionMatrix = new float[16];

    private StartEndCallback startEndCallback;

    public SearchOrigamiView(Context context, long duration, StartEndCallback startEndCallback) {
        super(context);
        this.duration = duration;
        this.startEndCallback = startEndCallback;
        this.init();
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    private void init() {
        this.setEGLContextClientVersion(2);

        this.setEGLConfigChooser(8, 8, 8, 8, 0, 0);
        this.setZOrderOnTop(true);
        this.getHolder().setFormat(PixelFormat.TRANSPARENT);

        this.setRenderer(this);
        this.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
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

    public void startAnimation(boolean openIt, final Bitmap[] titleBitmaps, final Bitmap content) {
        float[] between = openIt ? new float[]{0, 1} : new float[]{1, 0};
        ValueAnimator animator = ValueAnimator.ofFloat(between[0], between[1]);
        animator.setDuration(duration);

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startEndCallback.start();
                    }
                }, 100);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                startEndCallback.end();
                getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        queueAndRender(null, null, 0);
                    }
                }, 0);
            }
        });

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                generateAnimateFrame((Float) valueAnimator.getAnimatedValue(), titleBitmaps, content);
            }
        });

        animator.start();
    }

    protected void generateAnimateFrame(float factor, Bitmap[] titleBitmaps, Bitmap content) {
        Bitmap bitmap = titleBitmaps[0];
        float h = bitmap.getHeight();
        float _h = 2 * h / height;

        float angle = 90f * factor;
        //上半部分
        float left, top, right, bottom;
        left = -ratio;
        top = 1;
        right = ratio;
//        bottom = 0;
        bottom = 1 - _h;
        RectF rect = new RectF(left, top, right, bottom);
        Vertex[] topVertexArray = rotateAngle(rect, angle, true);

        //下半部分
        float dy = topVertexArray[0].positionY - topVertexArray[1].positionY;
        rect = new RectF(left, top, right, bottom);

        Vertex[] bottomVertexArray = rotateAngle(rect, angle, false);

        //所有下半部分顶点，下移dy
        for (Vertex v : bottomVertexArray) {
            v.positionY -= dy;
        }

        //如果有下面的图，生成下面的部分
        _h = 2f * content.getHeight() / height;

        Vertex[] contentArray = new Vertex[]{
                new Vertex(left, 1, 0),
                new Vertex(left, 1 - _h, 0),
                new Vertex(right, 1, 0),
                new Vertex(right, 1 - _h, 0)
        };

        for (Vertex v : contentArray) {
            v.positionY -= dy * 2;
        }

        queueAndRender(new Object[]{
                new Object[]{
                        topVertexArray, titleBitmaps[0]
                },
                new Object[]{
                        bottomVertexArray, titleBitmaps[1]
                },
                new Object[]{
                        contentArray, content
                }
        }, bottomVertexArray, factor);
    }

    protected void queueAndRender(final Object[] vertexArray, final Vertex[] shadowArray, final float factor) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                if (vertexArray == null) {
                    Object[] array = (Object[]) SearchOrigamiView.this.vertexArray[0];
                    Bitmap bitmap = (Bitmap) array[1];
                    bitmap.recycle();

                    array = (Object[]) SearchOrigamiView.this.vertexArray[1];
                    bitmap = (Bitmap) array[1];
                    bitmap.recycle();

                    array = (Object[]) SearchOrigamiView.this.vertexArray[2];
                    bitmap = (Bitmap) array[1];
                    bitmap.recycle();
                }
                SearchOrigamiView.this.vertexArray = vertexArray;
                SearchOrigamiView.this.shadowArray = shadowArray;
                SearchOrigamiView.this.factor = factor;
                requestRender();
            }
        });
    }

    protected Vertex[] rotateAngle(RectF rect, float angle, boolean isTop) {
        float x, y, nx, ny, nz, radian, dx, dy, topLeft, topRight, top, bottom;
//        distance = 5;
        radian = (float) Math.PI / 180 * angle;
        x = Math.abs(rect.width());
        y = Math.abs(rect.height());
        ny = (float) Math.sin(radian) * y;//ny即delta y
        nz = (float) Math.cos(radian) * y;
        nx = nz / (distance - nz) * 2 / x;

        dx = Math.abs(nx);
        dy = Math.abs(y - ny);

        top = 1;
        bottom = 1 - ny;
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

    interface StartEndCallback {
        void start();

        void end();
    }
}
