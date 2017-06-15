attribute vec4 vPosition;
attribute vec4 vColor;
attribute vec2 vUV;

uniform mat4 mvpMatrix;
uniform mat4 localMatrix;

varying vec4 color;
varying vec4 localPos;

void main() {
    localPos = localMatrix * vPosition;

    gl_Position = mvpMatrix * localPos;
    color = vColor;
}