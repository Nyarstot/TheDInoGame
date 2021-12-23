#version 330 core

layout (location = 0) out vec4 color;

in DATA
{
    vec2 tc;
} fs_in;

uniform sampler2D bg_texture;

void main()
{
    color = texture(bg_texture, fs_in.tc);
}