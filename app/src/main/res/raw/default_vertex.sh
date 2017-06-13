attribute vec4 vPosition;
attribute vec4 vColor;

uniform mat4 mvpMatrix;

varying vec4 color;

void main() {
    gl_Position = mvpMatrix * vPosition;
    color = vColor;
}