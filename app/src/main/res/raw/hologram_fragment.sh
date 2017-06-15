precision mediump float;

varying vec4 localPos;
varying vec4 color;
varying vec3 normal;

vec4 hologram_filter(vec4 color, float light) {
    float brightness = max(color.r + color.g, max(color.g + color.b, color.r + color.b)) * .5;
    vec3 filtered = brightness * vec3(0.5, 0.5, 1.0) + clamp(light, 0.0, 1.0);
    return vec4(filtered, 0.75);
}

void main() {
    vec3 up = vec3(0.0, 0.0, 1.0);
    vec3 normalchange = vec3(0.0, 0.0, sin(1000.0 * localPos.z) * .15f);
    gl_FragColor = hologram_filter(color, dot(up, normalize(normal + normalchange)) * .6);
}