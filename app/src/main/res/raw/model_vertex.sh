attribute vec4 vPosition;
attribute vec4 vColor;
attribute vec3 vNormal;

uniform mat4 mvpMatrix;
uniform mat4 localMatrix;

varying vec4 color;
varying vec3 normal;

void main() {
    gl_Position = mvpMatrix * (localMatrix * vPosition);
    color = vColor;
    normal = vNormal;
}