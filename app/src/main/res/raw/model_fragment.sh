precision mediump float;

uniform sampler2D TEXTURE;

varying vec4 color;
varying vec3 normal;
varying vec2 uv;


void main() {
    vec3 up = vec3(0.0, 0.0, 1.0);
    gl_FragColor = color * texture2D(TEXTURE, uv);
    gl_FragColor.rgb *= dot(up, normal) * 0.4 + 0.8;
}