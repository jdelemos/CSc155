#version 430

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 texCoord;

out vec2 tc;

uniform mat4 mv_matrix;
uniform mat4 p_matrix;

void main(void) {
    tc = texCoord;
    gl_Position = p_matrix * mv_matrix * vec4(position, 1.0);
}
