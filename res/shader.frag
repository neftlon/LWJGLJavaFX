// OpenGL/GLSL version 3
#version 330 core

// Set the precision to float.
precision mediump float;

// Pass out the final pixel color.
layout (location = 0) out vec4 o_Color;

// Get int the pass struct...
in PASS {
    // ... with the vec2 for the texture coordinate.
    vec2 p_TextureCoordinate;
} fs_In;

// Take in the texture.
uniform sampler2D u_Texture;

// The main function.
void main() {
    // Swizzle it to make the color loading error null.
    o_Color = texture(u_Texture, fs_In.p_TextureCoordinate).gbar;
}
