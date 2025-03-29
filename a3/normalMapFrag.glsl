#version 430

in vec2 varyingTexCoord;
in mat3 TBN;

uniform sampler2D tex;
uniform sampler2D normalMapSampler;

out vec4 fragColor;

void main(void) {
    vec3 normalFromMap = texture(normalMapSampler, varyingTexCoord).rgb;
    normalFromMap = normalize(normalFromMap * 2.0 - 1.0); // from [0,1] to [-1,1]
    vec3 N = normalize(TBN * normalFromMap);              // transform to view space

    // Simple directional light for testing
    vec3 L = normalize(vec3(0.5, 0.8, 1.0));
    float diff = max(dot(N, L), 0.0);

    vec3 color = texture(tex, varyingTexCoord).rgb;
    fragColor = vec4(color * diff, 1.0);
}
