#version 430

//this is being called from the position bind, it doesn't have a string name
layout (location=0) in vec3 position;
//same thing here, we are using the variable number to determine which one we are referencing 
layout (location=1) in vec2 texCoord; // Texture coordinate input

uniform mat4 mv_matrix;
uniform mat4 p_matrix;

out vec2 pass_texCoord; // Pass texture coordinate to fragment shader

void main(void) {
    gl_Position = p_matrix * mv_matrix * vec4(position, 1.0);
    pass_texCoord = texCoord; // Pass texture coordinates
}
