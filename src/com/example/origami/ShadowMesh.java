package com.example.origami;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.*;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;

/**
 * Created with IntelliJ IDEA.
 * User: marshal
 * Date: 13-4-15
 * Time: 下午6:42
 * To change this template use File | Settings | File Templates.
 */
public class ShadowMesh {
    private Shader shader;

    private FloatBuffer vertexBuffer, shadowColorBuffer;

    public ShadowMesh(Context context) {
        shader = new Shader();
        shader.setProgram(context, R.raw.shadow_vertex_shader, R.raw.shadow_fragment_shader);

        //一个四边形所需顶点的空间: 4个点（x,y,z），float是4字节
        vertexBuffer = ByteBuffer.allocateDirect(3 * 4 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();

        //顶点颜色，每个顶点4个颜色值，4个顶点，float是4字节
        shadowColorBuffer = ByteBuffer.allocateDirect(4 * 4 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
    }

    public void draw(float[] projectionMatrix, Vertex[] vertexArray,float factor) {
        this.setVertex(vertexArray);
        this.setShadowColor(factor);

        this.shader.useProgram();

        glUniformMatrix4fv(shader.getHandle("uProjectionM"), 1, false, projectionMatrix, 0);

        int aPosition = this.shader.getHandle("aPosition");
        glVertexAttribPointer(aPosition, 3, GL_FLOAT, false,
                3 * 4, vertexBuffer);
        glEnableVertexAttribArray(aPosition);

        //设置顶点颜色
        glVertexAttribPointer(shader.getHandle("aColor"), 4, GL_FLOAT, false, 0,
                this.shadowColorBuffer);
        glEnableVertexAttribArray(shader.getHandle("aColor"));

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
    }

    private void setVertex(Vertex[] vertexArray) {
        this.vertexBuffer.clear();

        for (Vertex v : vertexArray) {
            this.vertexBuffer.put(v.getPosition());
        }

        this.vertexBuffer.position(0);
    }

    private void setShadowColor(float factor) {
        shadowColorBuffer.clear();

        shadowColorBuffer.put(new float[]{
                0, 0, 0, 1 - factor,
                0, 0, 0, 0,
                0, 0, 0, 1 - factor,
                0, 0, 0, 0
        });

        shadowColorBuffer.position(0);
    }
}
