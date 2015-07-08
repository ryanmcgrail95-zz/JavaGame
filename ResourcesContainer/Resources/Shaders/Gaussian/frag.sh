uniform vec2 iResolution;
uniform float iGlobalTime;
uniform float iRadius;
uniform sampler2D tex;

float SCurve (float value) {
    // How to do this without if-then-else?
    
    if (value < 0.5)
    {
        return value * value * value * value * value * 16.0; 
    }
    
    else
    {
    	value -= 1.0;
    
    	return value * value * value * value * value * 16.0 + 1.0;
    }

    return value; 
}

vec4 BlurH (sampler2D source, vec2 size, vec2 uv, float radius) {

	if (radius >= 1.0)
	{
		vec4 A = vec4(0.0); 
		vec4 C = vec4(0.0); 

		float width = 1.0 / size.x;

		float divisor = 0.0; 
        float weight = 0.0;

        // Hardcoded for radius 20 (normally we input the radius
        // in there), needs to be literal here
        
		for (float x = -radius; x <= radius; x++)
		{
			A = texture2D(source, uv + vec2(x * width, 0.0));
            
            		weight = SCurve(1.0 - (abs(x) / radius)); //20.0 
            
            		C += A * weight; 
            
			divisor += weight; 
		}

		return vec4(C.r / divisor, C.g / divisor, C.b / divisor, C.a / divisor);
	}

	return texture2D(source, uv);
}

vec4 BlurV (sampler2D source, vec2 size, vec2 uv, float radius) {

	if (radius >= 1.0)
	{
		vec4 A = vec4(0.0); 
		vec4 C = vec4(0.0); 

		float height = 1.0 / size.y;

		float divisor = 0.0; 
        float weight = 0.0;

       for (float y = -radius; y <= radius; y++)
		{
			A = texture2D(source, uv + vec2(0.0, y * height));
            	
            	weight = SCurve(1.0 - (abs(y) / radius)); 
            
            	C += A * weight; 
            
			divisor += weight; 
		}

		return vec4(C.r / divisor, C.g / divisor, C.b / divisor, 1.0);
	}

	return texture2D(source, uv);
}

void main()
{
    ivec2 size = textureSize2D(tex,0);    
    vec4 color = texture2D(tex,gl_TexCoord[0].st);

    //vec2 uv = ;//gl_FragCoord.xy / iResolution;
    
	vec4 A = BlurH(tex, size.xy, gl_TexCoord[0].st, iRadius);
    
    // second pass (How do I render to an intermediate texture
    // in Shadertoy? Is it possible? Or is it just the wind, 
    // whistling by the castle window?
    //vec4 A = BlurV(iChannel0, iResolution.xy, uv, 6.0);	
    
	gl_FragColor = A;//vec4(A.rgb, color.a);
}