#version 430

layout (location=0) in vec3 position;
layout (location=1) in vec2 texCoord;
layout (location=2) in vec3 normal;
layout (location=3) in vec3 tangent;
layout (location=4) in vec3 bitangent;

out vec2 varyingTexCoord;
out mat3 TBN;

uniform mat4 m_matrix;
uniform mat4 v_matrix;
uniform mat4 p_matrix;

void main(void) {
    vec3 T = normalize(mat3(v_matrix * m_matrix) * tangent);
    vec3 B = normalize(mat3(v_matrix * m_matrix) * bitangent);
    vec3 N = normalize(mat3(v_matrix * m_matrix) * normal);
    TBN = mat3(T, B, N);

    varyingTexCoord = texCoord;
    gl_Position = p_matrix * v_matrix * m_matrix * vec4(position, 1.0);
}
