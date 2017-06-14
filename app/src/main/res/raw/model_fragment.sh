precision mediump float;

varying vec4 color;
varying vec3 normal;


void main() {
    vec3 up = vec3(0.0, 0.0, 1.0);
    gl_FragColor = color;
    gl_FragColor.rgb *= dot(up, normal);
}