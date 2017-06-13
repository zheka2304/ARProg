attribute vec4 vPosition;
attribute vec4 vColor;
attribute vec2 vUV;

uniform mat4 mvpMatrix;

varying vec4 color;
varying vec2 uv;

void main() {
    gl_Position = mvpMatrix * vPosition;
    color = vColor;
    uv = vUV;
}