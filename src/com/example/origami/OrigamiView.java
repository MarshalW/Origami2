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
import android.os.HandlerThread;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static android.opengl.GLES20.*;

/**
 * Created with IntelliJ IDEA.
 * User: marshal
 * Date: 13-4-10
 * Time: 上午11:28
 * To change this template use File | Settings | File Templates.
 */
public class OrigamiView extends GLSurfaceView implements GLSurfaceView.Renderer {

    final static Object[] EMPTY_ARRAY = {};

    private long duration = 400;

    private boolean touchBlocked;

    float[] projectionMatrix = new float[16];

    Mesh mesh;

    ShadowMesh shadowMesh;

    int width, height;

    float ratio;

    VertexArray vertexArrayList;

    Handler handler = new Handler();

    public long getDuration() {
        return duration;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return touchBlocked;
    }

    public OrigamiView(Context context) {
        super(context);
        this.init();
    }

    public OrigamiView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    private void init() {
        this.setEGLContextClientVersion(2);

        this.setEGLConfigChooser(8, 8, 8, 8, 0, 0);
        this.setZOrderOnTop(true);
        this.getHolder().setFormat(PixelFormat.TRANSPARENT);

        this.setRenderer(this);
        this.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        this.vertexArrayList = new VertexArray();
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        glClearColor(0, 0, 0, 0);
        mesh = new Mesh(getContext());
        shadowMesh = new ShadowMesh(getContext());
    }

    private void logVertexs(Vertex[] vertexes){
        StringBuilder builder=new StringBuilder();

        for (Vertex v:vertexes){
            builder.append(v).append("\n");
        }

//        Log.d("origami","vertexs: \n"+builder.toString());
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        this.width = width;
        this.height = height;

        glViewport(0, 0, width, height);

        ratio = width / (float) height;
        Matrix.orthoM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, -10f, 10f);
    }

    Object[] vertexArray = EMPTY_ARRAY;

    @Override
    public void onDrawFrame(GL10 gl10) {
        glClear(GL_COLOR_BUFFER_BIT);

        for (int i = 0; i < vertexArray.length; i++) {
            Object[] array = (Object[]) vertexArray[i];
//            logVertexs((Vertex[]) array[0]);
            mesh.draw(projectionMatrix, (Vertex[]) array[0], (Bitmap) array[1]);
        }

        if(this.shadowPolygon!=null){
            shadowMesh.draw(projectionMatrix,this.shadowPolygon,factor);
        }
    }

    /**
     * 获取动画前的target view快照并显示
     *
     * @param viewUnits
     */
    public void snapTargetView(List<ViewUnit> viewUnits) {
        touchBlocked = true;

        List<ViewUnit> units = new ArrayList<ViewUnit>(viewUnits);
        Collections.reverse(units);

        vertexArrayList.clear();

        for (ViewUnit unit : units) {
            if (unit.contentView.getVisibility() == VISIBLE) {
                RectF[] rects = generateTowHalfRects(getViewRect(unit.contentView));
                vertexArrayList.add(new Object[]{
                        rect2VertexArray(rects[1]), unit.contentViewBottomBitmap
                });
                vertexArrayList.add(new Object[]{
                        rect2VertexArray(rects[0]), unit.contentViewTopBitmap
                });
            }
            vertexArrayList.add(new Object[]{
                    rect2VertexArray(getViewRect(unit.titleView)), unit.titleViewBitmap
            });
        }

        queueAndRender(vertexArrayList.toArray());
    }

    /**
     * 生成当前动画帧
     *
     * @param viewUnits
     * @param viewUnit
     * @param factor
     */
    private void generateAnimateTargetView(List<ViewUnit> viewUnits, ViewUnit viewUnit, float factor) {
        //创建倒排序的ViewUnit列表
        List<ViewUnit> units = new ArrayList<ViewUnit>(viewUnits);
        Collections.reverse(units);

        vertexArrayList.clear();

        Vertex[] shadowPolygon = null;

        //遍历ViewUnit列表
        for (ViewUnit unit : units) {
            //如当前列表中unit是选中的
            if (unit == viewUnit) {
                float angle = 90f * factor;
                RectF viewRect = getViewRect(unit.contentView);

                shadowPolygon = rotateAngle(viewRect, angle, true);
                vertexArrayList.add(new Object[]{
                        shadowPolygon, unit.contentViewBottomBitmap
                });
                vertexArrayList.add(new Object[]{
                        rotateAngle(viewRect, angle, false), unit.contentViewTopBitmap
                });
            }
            vertexArrayList.add(new Object[]{
                    rect2VertexArray(getViewRect(unit.titleView)), unit.titleViewBitmap
            });
        }

        queueAndRender(vertexArrayList.toArray(), shadowPolygon, factor);
    }

    private Vertex[] shadowPolygon;

    private float factor;

    /**
     * 绘制数据排到队列绘制
     *
     * @param array
     */
    private void queueAndRender(final Object[] array) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                shadowPolygon = null;
                vertexArray = array;
                requestRender();
            }
        });
    }

    /**
     * 绘制数据排到队列绘制
     *
     * @param array
     * @param shadowPolygon
     */
    private void queueAndRender(final Object[] array, final Vertex[] shadowPolygon, final float factor) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                OrigamiView.this.shadowPolygon = shadowPolygon;
                OrigamiView.this.factor = factor;
                vertexArray = array;
                requestRender();
            }
        });
    }

    public void clear() {
        this.touchBlocked = false;
        this.vertexArrayList.clear();
        queueAndRender(this.vertexArrayList.toArray());
    }

    /**
     * 对给出的矩形，做角度旋转（分上半部分或者下半部分），然后输出顶点数组
     *
     * @param rect
     * @param angle
     * @param isBottom
     * @return
     */
    private Vertex[] rotateAngle(RectF rect, float angle, boolean isBottom) {
        float x, y, nx, ny, nz, radian, distance, dx, dy, topLeft, topRight, top, bottom;
        distance = 5;
        radian = (float) Math.PI / 180 * angle;
        x = Math.abs(rect.width());
        y = Math.abs(rect.height() / 2);
        ny = (float) Math.sin(radian) * y;
        nz = (float) Math.cos(radian) * y;
        nx = nz / (distance - nz) * 2 / x;

        dx = Math.abs(nx);
        dy = Math.abs(y - ny);

        top = -1 + y - dy;
        bottom = -1;
        topLeft = rect.left + dx;
        topRight = rect.right - dx;

        if (isBottom) {
            return new Vertex[]{
                    new Vertex(topLeft, top, 0),
                    new Vertex(rect.left, bottom, 0),
                    new Vertex(topRight, top, 0),
                    new Vertex(rect.right, bottom, 0)
            };
        } else {
            return new Vertex[]{
                    new Vertex(rect.left, top, 0),
                    new Vertex(topLeft, bottom, 0),
                    new Vertex(rect.right, top, 0),
                    new Vertex(topRight, bottom, 0)
            };
        }
    }

    /**
     * 将给定矩形输出上下两部分矩形
     *
     * @param rect
     * @return
     */
    private RectF[] generateTowHalfRects(RectF rect) {
        float halfY = rect.top - ((rect.top - rect.bottom) / 2);
        return new RectF[]{
                new RectF(rect.left, rect.top, rect.right, halfY),
                new RectF(rect.left, halfY, rect.right, rect.bottom)
        };
    }

    /**
     * 获取view的rect
     *
     * @param view
     * @return
     */
    private RectF getViewRect(View view) {
        float left, top, right, bottom;

        left = -ratio;
        top = -(1 - (view.getHeight() / (height / 2.0f)));
        right = ratio;
        bottom = -1;

        return new RectF(left, top, right, bottom);
    }

    /**
     * 将rect转为vertex数组
     *
     * @param rect
     * @return
     */
    private Vertex[] rect2VertexArray(RectF rect) {
        return new Vertex[]{
                new Vertex(rect.left, rect.top, 0),
                new Vertex(rect.left, rect.bottom, 0),
                new Vertex(rect.right, rect.top, 0),
                new Vertex(rect.right, rect.bottom, 0)
        };
    }

    private ViewUnit getCurrentOpenViewUnit(List<ViewUnit> viewUnits) {
        for (ViewUnit unit : viewUnits) {
            if (unit.contentView.getVisibility() == View.VISIBLE) {
                return unit;
            }
        }
        return null;
    }

    private ViewUnit findViewUnit(List<ViewUnit> viewUnits, View titleView) {
        for (ViewUnit unit : viewUnits) {
            if (unit.titleView == titleView) {
                return unit;
            }
        }
        throw new RuntimeException("Could not find view unit.");
    }

    public void startAnimation(final List<ViewUnit> viewUnits,
                               final View chooseTitleView) {
        ValueAnimator animator = null;
        ViewUnit currentViewUnit = null;
        currentViewUnit = getCurrentOpenViewUnit(viewUnits);

        //是否执行多次动画，如果已有打开但不是选择的，则会执行多次动画
        //即，先关闭再展开
        final boolean oneAnim;
        if (currentViewUnit == null || currentViewUnit.titleView == chooseTitleView) {
            animator = null;
            if (currentViewUnit == null) {//关闭状态下，动画打开
//                Log.d("origami", "单个动画，打开");
                currentViewUnit = findViewUnit(viewUnits, chooseTitleView);
                animator = ValueAnimator.ofFloat(0, 1);
            } else {//选择的title view的content view是打开的，动画关闭
//                Log.d("origami", "单个动画，关闭");
                animator = ValueAnimator.ofFloat(1, 0);
            }
            oneAnim = true;
        } else {//需要关闭再打开的动画
            animator = ValueAnimator.ofFloat(-1, 1);
            oneAnim = false;
        }

        animator.setDuration(duration);
//        animator.setInterpolator(new DecelerateInterpolator());

        final ViewUnit _currentViewUnit = currentViewUnit;
        final ViewUnit _secondViewUnit = oneAnim ? null : findViewUnit(viewUnits, chooseTitleView);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float factor = (Float) valueAnimator.getAnimatedValue();
                ViewUnit viewUnit = null;

                //针对关闭和打开动画，第二次动画要切换viewunit对象
                if (!oneAnim && factor > 0) {
                    viewUnit = _secondViewUnit;
                } else {
                    viewUnit = _currentViewUnit;
                }

                generateAnimateTargetView(viewUnits, viewUnit, Math.abs(factor));
            }
        });

        final View targetView = (View) chooseTitleView.getParent();

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                targetView.setVisibility(INVISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                targetView.setVisibility(VISIBLE);
                touchBlocked = false;

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        vertexArrayList.clear();
                        queueAndRender(vertexArrayList.toArray());

                    }
                }, 10);
            }
        });

        animator.start();
    }

    class VertexArray extends ArrayList {

        private float currentTop = -1;

        @Override
        public boolean add(Object object) {
            Object[] array = (Object[]) object;
            Vertex[] vertexes = (Vertex[]) array[0];
            if (currentTop > -1) {
                float dy = Math.abs(vertexes[1].positionY - currentTop);
//                Log.d("origami", "dy:" + dy + ", vertex[0]: " + vertexes[0]);
                for (Vertex v : vertexes) {
                    v.translate(0, dy);
                }
            }
            currentTop = vertexes[0].positionY;
//            Log.d("origami", ">>>" + vertexes[0] + ", currentTop: " + currentTop);
            return super.add(object);
        }

        @Override
        public void clear() {
            currentTop = -1;
            super.clear();
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();

            Object[] array = this.toArray();

            for (int i = 0; i < array.length; i++) {
                Vertex[] vertexes = (Vertex[]) (((Object[]) array[i])[0]);
                builder.append("positionY: ").append(vertexes[0].positionY).append("\n");

            }

            return "VertexArray{\n" +
                    "currentTop=" + currentTop +
                    "\n" + builder.toString() +
                    "\n}";
        }
    }
}
