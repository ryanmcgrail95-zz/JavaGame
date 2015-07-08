uniform vec2 iResolution;
uniform float iGlobalTime;
uniform float iRadius;
uniform sampler2D tex;


void main()
{
    vec4 color = texture2D(tex,gl_TexCoord[0].st);
    float value = .21*color.r + .72*color.g + .07*color.b;

    gl_FragColor = vec4(value, value, value, color.a);
}