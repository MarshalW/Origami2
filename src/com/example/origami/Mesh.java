package com.example.origami;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.List;

import static android.opengl.GLES20.*;

/**
 * Created with IntelliJ IDEA.
 * User: marshal
 * Date: 13-4-11
 * Time: 下午2:53
 * To change this template use File | Settings | File Templates.
 */
public class Mesh {

    private Shader shader;

    private FloatBuffer vertexBuffer, textureCoordBuffer;

    private int[] textureId;

    public Mesh(Context context) {
        shader = new Shader();
        shader.setProgram(context, R.raw.demo_vertex_shader, R.raw.demo_fragment_shader);

        //一个四边形所需顶点的空间: 4个点（x,y,z），float是4字节
        vertexBuffer = ByteBuffer.allocateDirect(3 * 4 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();

        //纹理坐标，针对四边形，4个点（x,y），float是4字节
        textureCoordBuffer = ByteBuffer.allocateDirect(2 * 4 * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureCoordBuffer.put(new float[]{
                0, 0,
                0, 1,
                1, 0,
                1, 1
        });
    }

    public void draw(float[] projectionMatrix, Vertex[] vertexArray, Bitmap bitmap) {
        this.setVertex(vertexArray);
        this.setTextureCood();

        this.shader.useProgram();

        glUniformMatrix4fv(shader.getHandle("uProjectionM"), 1, false, projectionMatrix, 0);

        int aPosition = this.shader.getHandle("aPosition");
        glVertexAttribPointer(aPosition, 3, GL_FLOAT, false,
                3 * 4, vertexBuffer);
        glEnableVertexAttribArray(aPosition);

        int aTextureCoord = this.shader.getHandle("aTextureCoord");

        if (textureId == null) {
            textureId = new int[1];
            glGenTextures(1, textureId, 0);

            glBindTexture(GL_TEXTURE_2D, textureId[0]);
            glTexParameterf(GL_TEXTURE_2D,
                    GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameterf(GL_TEXTURE_2D,
                    GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameterf(GL_TEXTURE_2D,
                    GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameterf(GL_TEXTURE_2D,
                    GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        }

        glVertexAttribPointer(aTextureCoord, 2, GL_FLOAT, false,
                0, textureCoordBuffer);
        glEnableVertexAttribArray(aTextureCoord);

        glEnable(GL_TEXTURE_2D);
        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
        glDisable(GL_TEXTURE_2D);

        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
    }

    private void setVertex(Vertex[] vertexArray) {
        this.vertexBuffer.clear();

        for(Vertex v:vertexArray){
            this.vertexBuffer.put(v.getPosition());
        }

        this.vertexBuffer.position(0);
    }

    private void setTextureCood(){
        textureCoordBuffer.position(0);
    }
}
