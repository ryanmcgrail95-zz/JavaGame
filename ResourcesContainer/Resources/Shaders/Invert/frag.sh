uniform vec2 iResolution;
uniform float iGlobalTime;
uniform float iRadius;
uniform sampler2D tex;


void main()
{
    vec4 color = texture2D(tex,gl_TexCoord[0].st);
    
    gl_FragColor = vec4(1-color.r, 1-color.g, 1-color.b, color.a);
}