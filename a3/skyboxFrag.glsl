#version 430

in vec2 tc;
out vec4 fragColor;

uniform sampler2D tex;

void main(void) {
    fragColor = texture(tex, tc);
}
