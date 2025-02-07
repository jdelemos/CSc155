#version 430

uniform float offset;
uniform float vertex0width;
uniform float vertex1width;
uniform float vertex2width; 
uniform float vertex0height; 
uniform float vertex1height; 
uniform float vertex2height; 
uniform float tanValue;
uniform int colorIndex; // Determines color mode

uniform vec4 uColors[2]; // Stores solid colors (yellow, purple)

out vec4 vertexColor; // Pass color to the fragment shader

void main(void) {
    if (gl_VertexID == 0) {
        gl_Position = vec4(vertex0width + offset, vertex0height + tanValue, 0.0, 1.0);
        if (colorIndex == 0) {
            vertexColor = uColors[0]; // Yellow fill
        } else if (colorIndex == 1) {
            vertexColor = uColors[1]; // Purple fill
        } else {
            vertexColor = vec4(1.0, 0.0, 0.0, 1.0); // Red for gradient mode
        }
    } 
    else if (gl_VertexID == 1) {
        gl_Position = vec4(vertex1width + offset, vertex1height + tanValue, 0.0, 1.0);
        if (colorIndex == 0) {
            vertexColor = uColors[0]; // Yellow fill
        } else if (colorIndex == 1) {
            vertexColor = uColors[1]; // Purple fill
        } else {
            vertexColor = vec4(0.0, 0.0, 1.0, 1.0); // Blue for gradient mode
        }
    } 
    else {
        gl_Position = vec4(vertex2width + offset, vertex2height + tanValue, 0.0, 1.0);
        if (colorIndex == 0) {
            vertexColor = uColors[0]; // Yellow fill
        } else if (colorIndex == 1) {
            vertexColor = uColors[1]; // Purple fill
        } else {
            vertexColor = vec4(0.0, 1.0, 0.0, 1.0); // Green for gradient mode
        }
    }
}

