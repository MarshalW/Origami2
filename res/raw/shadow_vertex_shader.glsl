uniform mat4 uProjectionM;
attribute vec4 aColor;
varying vec4 vColor;
attribute vec4 aPosition;
void main() {
    vColor = aColor;
    gl_Position = uProjectionM * aPosition;
}