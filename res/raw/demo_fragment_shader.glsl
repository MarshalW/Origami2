precision mediump float;
varying vec2 vTextureCoord;
uniform sampler2D sTexture;
void main() {
    //vec4 color =vec4(255,0,0,0);
    gl_FragColor = texture2D(sTexture, vTextureCoord);
    //gl_FragColor = mix(color, gl_FragColor, 0.9);
}