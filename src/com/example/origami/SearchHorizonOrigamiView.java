package com.example.origami;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.util.Log;

/**
 * Created with IntelliJ IDEA.
 * User: marshal
 * Date: 13-4-27
 * Time: 下午7:39
 * To change this template use File | Settings | File Templates.
 */
public class SearchHorizonOrigamiView extends SearchOrigamiView {
    public SearchHorizonOrigamiView(Context context, long duration, StartEndCallback startEndCallback) {
        super(context, duration, startEndCallback);
    }

    protected void generateAnimateFrame(float factor, Bitmap[] titleBitmaps, Bitmap content) {

        Bitmap bitmap = titleBitmaps[0];

        float h = 2f * bitmap.getHeight() / height;
        float w = 2f * ratio * bitmap.getWidth() / width;

        float angle = 90f * factor;
        //左部分
        float left, top, right, bottom;
        left = -ratio;
        top = 1;
        right = -ratio + w;
        bottom = 1 - h;
        RectF rect = new RectF(left, top, right, bottom);
        Vertex[] topVertexArray = rotateAngle(rect, angle, true);

        //右半部分
        float dx = topVertexArray[0].positionX - topVertexArray[2].positionX;
        rect = new RectF(left, top, right, bottom);

        Vertex[] bottomVertexArray = rotateAngle(rect, angle, false);

        //所有下半部分顶点，右移dx
        for (Vertex v : bottomVertexArray) {
            v.positionX -= dx;
        }

        //如果有下面的图，生成下面内容的部分

        h = 2f * content.getHeight() / height;
        w = 2f * ratio * content.getWidth() / width;

        Vertex[] contentArray = new Vertex[]{
                new Vertex(left, 1, 0),
                new Vertex(left, 1 - h, 0),
                new Vertex(left + w, 1, 0),
                new Vertex(left + w, 1 - h, 0)
        };

        for (Vertex v : contentArray) {
            v.positionX += Math.abs(dx) * 2f;
        }

        queueAndRender(new Object[]{
                new Object[]{
                        topVertexArray, titleBitmaps[0]
                },
                new Object[]{
                        bottomVertexArray, titleBitmaps[1]
                }
                ,
                new Object[]{
                        contentArray, content
                }
        }, bottomVertexArray, factor);
    }

    protected Vertex[] rotateAngle(RectF rect, float angle, boolean isLeft) {
        float x, y, nx, ny, nz, radian, dx, dy;
        radian = (float) Math.PI / 180 * angle;
        x = Math.abs(rect.width());
        y = Math.abs(rect.height());
        nx = (float) Math.sin(radian) * x;
        nz = (float) Math.cos(radian) * x;
        ny = nz / (distance - nz) * 2 / y;

//        Log.d("origami", ">>>>>>x: " + x + ", ratio: " + ratio+", rect.width: "+rect.width());

        dx = Math.abs(nx);
        dy = Math.abs(ny);

//        Log.d("origami", "--->x: " + x + ", dx: " + dx + ", left: " + rect.left + ", ratio: " + ratio);

        if (isLeft) {
            return new Vertex[]{
                    new Vertex(rect.left, rect.top, 0),
                    new Vertex(rect.left, rect.bottom, 0),
                    new Vertex(rect.left + dx, rect.top - dy, 0),
                    new Vertex(rect.left + dx, rect.bottom + dy, 0)
            };
        } else {
            return new Vertex[]{
                    new Vertex(rect.left, rect.top - dy, 0),
                    new Vertex(rect.left, rect.bottom + dy, 0),
                    new Vertex(rect.left + dx, rect.top, 0),
                    new Vertex(rect.left + dx, rect.bottom, 0)
            };
        }
    }
}
