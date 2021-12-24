#version 330 core

layout (location = 0) out vec4 color;

in DATA
{
    vec2 tc;
} fs_in;

uniform sampler2D player_texture;

void main()
{
    color = texture(player_texture, fs_in.tc);
    if (color.w < 0.5) {
            discard;
        }
}