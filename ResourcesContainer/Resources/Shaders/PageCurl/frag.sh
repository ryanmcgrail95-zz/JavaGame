uniform float iGlobalTime;
uniform vec2 tRes;
uniform sampler2D tex0;
uniform sampler2D tex1;

float curlExtent = 0.;
    
const float minAmount = -0.16;
const float maxAmount = 1.3;
const float PI = 3.14159; //2653589793;
float scale = 512;
const float sharpness = 3.0;

vec4 bgColor;

float amount;
float cylinderCenter;
float cylinderAngle;
const float cylinderRadius = 1.0 / PI / 2.0;

vec3 hitPoint(float hitAngle, float yc, vec3 point, mat3 rrotation) {
    float hitPoint = hitAngle / (2.0 * PI);
    point.y = hitPoint;
    return rrotation * point;
}

vec4 antiAlias(vec4 color1, vec4 color2, float distance) {
    distance *= scale;
    if (distance < 0.0) return color2;
    if (distance > 2.0) return color1;
    float dd = pow(1.0 - distance / 2.0, sharpness);
    return ((color2 - color1) * dd) + color1;
}

float distanceToEdge(vec3 point)
{
    float dx = abs(point.x > 0.5 ? 1.0 - point.x : point.x);
    float dy = abs(point.y > 0.5 ? 1.0 - point.y : point.y);
    if (point.x < 0.0) dx = -point.x;
    if (point.x > 1.0) dx = point.x - 1.0;
    if (point.y < 0.0) dy = -point.y;
    if (point.y > 1.0) dy = point.y - 1.0;
    if ((point.x < 0.0 || point.x > 1.0) && (point.y < 0.0 || point.y > 1.0)) return sqrt(dx * dx + dy * dy);
    return min(dx, dy);
}

vec4 seeThrough(float yc, vec2 p, mat3 rotation, mat3 rrotation)
{
    float hitAngle = PI - (acos(yc / cylinderRadius) - cylinderAngle);
    vec3 point = hitPoint(hitAngle, yc, rotation * vec3(p, 1.0), rrotation);
    if (yc <= 0.0 && (point.x < 0.0 || point.y < 0.0 || point.x > 1.0 || point.y > 1.0))
        return bgColor;
    if (yc > 0.0)
        return texture2D(tex0, p);
    vec4 color = texture2D(tex0, point.xy);
    vec4 tcolor = vec4(0.0);
    return antiAlias(color, tcolor, distanceToEdge(point));
}

vec4 seeThroughWithShadow(float yc, vec2 p, vec3 point, mat3 rotation, mat3 rrotation)
{
    float shadow = distanceToEdge(point) * 30.0;
    shadow = (1.0 - shadow) / 3.0;
    if (shadow < 0.0)
        shadow = 0.0;
    else
        shadow *= amount;
    vec4 shadowColor = seeThrough(yc, p, rotation, rrotation);
    shadowColor.r -= shadow;
    shadowColor.g -= shadow;
    shadowColor.b -= shadow;
    return shadowColor;
}

vec4 backside(float yc, vec3 point) {
    vec4 color = vec4(1.,1.,1.,1.);
    float gray = 0.;
    gray += (8.0 / 10.0) * (pow(1.0 - abs(yc / cylinderRadius), 2.0 / 10.0) / 2.0 + (5.0 / 10.0));
    color.rgb = 1.15*vec3(gray);
    return color;
}

void main() {
    vec2 uv = gl_TexCoord[0].st;
    
    bgColor = texture2D(tex1, uv);
    
    curlExtent = .5 + -cos(iGlobalTime*3.14159)*.5;
            
	amount = curlExtent * (maxAmount - minAmount) + minAmount;
	cylinderCenter = amount;
	cylinderAngle = 2.0 * PI * amount;

    const float angle = 30.0 * PI / 180.0; //30.
    float c = cos(-angle);
    float s = sin(-angle);
    
    float rx1, ry1;
    rx1 = .12;
    ry1 = .258;
    
    mat3 rotation = mat3(c, s, 0, -s, c, 0, rx1, ry1, 1);
    c = cos(angle);
    s = sin(angle);
    
    float rx, ry;
    rx = .1495; //.15	//left-right
    ry = .5; //.5		//up-down
    
    mat3 rrotation = mat3(c, s, 0, -s, c, 0, rx, -ry, 1);
    vec3 point = rotation * vec3(uv, 1.0); //.8
    float yc = point.y - cylinderCenter;
    vec4 color = vec4(1.0, 0.0, 0.0, 1.0);
    if (yc < -cylinderRadius) // See through to background
    {
        color = bgColor;
    } 
    else if (yc > cylinderRadius) { // Flat surface
        color = texture2D(tex0, uv);
    } 
    else 
    {
        float hitAngle = (acos(yc / cylinderRadius) + cylinderAngle) - PI;
        float hitAngleMod = mod(hitAngle, 2.0 * PI);
        if ((hitAngleMod > PI && amount < 0.5) || (hitAngleMod > PI/2.0 && amount < 0.0)) 
        {
            color = seeThrough(yc, uv, rotation, rrotation);
        } 
        else 
        {
            point = hitPoint(hitAngle, yc, point, rrotation);
            if (point.x < 0.0 || point.y < 0.0 || point.x > 1.0 || point.y > 1.0) 
            {
                color = seeThroughWithShadow(yc, uv, point, rotation, rrotation);
            } 
            else 
            {
                color = backside(yc, point);
                vec4 otherColor;
                if (yc < 0.0) 
                {
                    float shado = 1.0 - (sqrt(pow(point.x - 0.5, 2.0) + pow(point.y - 0.5, 2.0)) / 0.71);
                    shado *= pow(-yc / cylinderRadius, 3.0);
                    shado *= 0.5;
                    otherColor = vec4(0.0, 0.0, 0.0, shado);
                } 
                else 
                {
                    otherColor = texture2D(tex0, uv);
                }
                color = antiAlias(color, otherColor, cylinderRadius - abs(yc));
            }
        }
    }
    gl_FragColor = color;
}