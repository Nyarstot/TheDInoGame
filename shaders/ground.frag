#version 330 core

layout (location = 0) out vec4 color;

in DATA
{
    vec2 tc;
} fs_in;

uniform sampler2D ground_texture;

void main()
{
    color = texture(ground_texture, fs_in.tc);
    if (color.w < 1.0) {
                discard;
    }
}