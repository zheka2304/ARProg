attribute vec4 vPosition;
attribute vec4 vColor;
attribute vec2 vUV;

uniform mat4 mvpMatrix;
uniform mat4 localMatrix;

varying vec4 color;
varying vec2 uv;

void main() {
    gl_Position = mvpMatrix * (localMatrix * vPosition);
    color = vColor;
    uv = vUV;
}