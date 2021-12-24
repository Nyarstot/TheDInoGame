#version 330 core

layout (location = 0) out vec4 color;

in DATA
{
    vec2 tc;
} fs_in;

uniform sampler2D fir_texture;
uniform int texture_sample;

void main()
{
    color = texture(fir_texture, fs_in.tc);
    if (color.w < 0.1) {
        discard;
    }
}