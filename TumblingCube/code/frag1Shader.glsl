#version 430

uniform vec4 uColors[3];  // Color presets or gradient colors
uniform int colorIndex;   // Determines solid color vs. gradient
uniform float t;          // Controls gradient interpolation

out vec4 fragColor;

void main() {
    vec3 blendedColor;

    if (colorIndex < 2) {
        // Solid color mode (Yellow or Purple)
        blendedColor = uColors[colorIndex].rgb;
    } else {
        // Gradient mode (Red -> Blue -> Green)
        if (t < 0.5) {
            blendedColor = mix(uColors[0].rgb, uColors[1].rgb, t * 2.0);  // Red to Blue
        } else {
            blendedColor = mix(uColors[1].rgb, uColors[2].rgb, (t - 0.5) * 2.0);  // Blue to Green
        }
    }

    fragColor = vec4(blendedColor, 1.0);
}

