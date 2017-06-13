precision mediump float;

uniform sampler2D TEXTURE;

varying vec4 color;
varying vec2 uv;

void main() {
    gl_FragColor = texture2D(TEXTURE, uv);
}