package com.example.origami;

/**
 * Created with IntelliJ IDEA.
 * User: marshal
 * Date: 13-4-11
 * Time: 下午6:05
 * To change this template use File | Settings | File Templates.
 */
public class Vertex {
    public float positionX, positionY, positionZ;

    public Vertex(float positionX, float positionY, float positionZ) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.positionZ = positionZ;
    }

    public float[] getPosition() {
        return new float[]{positionX, positionY, positionZ};
    }

    public void translate(float dx, float dy) {
        positionX += dx;
        positionY += dy;
    }

    @Override
    public String toString() {
        return "Vertex{" +
                "positionX=" + positionX +
                ", positionY=" + positionY +
                ", positionZ=" + positionZ +
                '}';
    }
}
