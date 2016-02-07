// OpenGL/GLSL version 3
#version 330 core

// Get in the vertex position as vec4.
layout (location = 0) in vec4 a_Position;
// Get in the texture coordinate as vec2.
layout (location = 1) in vec2 a_TextureCoordinate;

// Pass out the struct with the texture coordinate to
// the fragment shader.
out PASS {
    // The texture coordinate to pass.
    vec2 p_TextureCoordinate;
} vs_Out;

// The main function.
void main() {
    // Set the current vertex position to the
    // vertex position from the vao.
    gl_Position = a_Position;

    // Set the texture coordinate from the
    // struct to the texture cooridinate
    // from the vao.
    vs_Out.p_TextureCoordinate = a_TextureCoordinate;
}