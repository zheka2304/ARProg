precision mediump float;


varying vec4 color;
varying vec4 localPos;

void main() {
    if (localPos.z < 0.0)
        discard;

    gl_FragColor = color;
}