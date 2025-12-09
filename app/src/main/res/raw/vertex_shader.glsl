// Vertex Shader for CloudPaper Live Wallpaper
// Simple pass-through shader for rendering a full-screen quad

attribute vec4 aPosition;
attribute vec2 aTexCoord;

varying vec2 vTexCoord;

void main() {
    // Pass texture coordinates to fragment shader
    vTexCoord = aTexCoord;

    // Output vertex position
    gl_Position = aPosition;
}
