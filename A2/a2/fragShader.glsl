#version 430

in vec2 pass_texCoord;
out vec4 color;

uniform sampler2D textureSampler; // Texture sampler

void main(void) {
    color = texture(textureSampler, pass_texCoord); // Apply the texture
}
