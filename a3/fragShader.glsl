#version 430

in vec3 varyingNormal;
in vec3 varyingLightDir;
in vec3 varyingVertPos;
in vec3 varyingHalfVector;
in vec2 varyingTexCoord;

out vec4 fragColor;

uniform sampler2D texture1;
uniform vec4 globalAmbient;

struct PositionalLight {
	vec4 ambient;
	vec4 diffuse;
	vec4 specular;
	vec3 position;
};

struct Material {
	vec4 ambient;
	vec4 diffuse;
	vec4 specular;
	float shininess;
};

uniform PositionalLight light;
uniform PositionalLight light2; 
uniform Material material;

void main(void)
{
	vec3 N = normalize(varyingNormal);
	vec3 L = normalize(varyingLightDir);
	vec3 H = normalize(varyingHalfVector);

	// === Light 1 ===
	float diff1 = max(dot(N, L), 0.0);
	float spec1 = pow(max(dot(N, H), 0.0), material.shininess);

	vec4 ambient1 = light.ambient * material.ambient;
	vec4 diffuse1 = light.diffuse * material.diffuse * diff1;
	vec4 specular1 = light.specular * material.specular * spec1;

	// === Light 2 ===
	vec3 L2 = normalize(light2.position - varyingVertPos); // assumes varyingVertPos is in world space
	vec3 H2 = normalize(L2 + normalize(-varyingVertPos));  // assumes camera is at origin (adjust later if needed)

	float diff2 = max(dot(N, L2), 0.0);
	float spec2 = pow(max(dot(N, H2), 0.0), material.shininess);

	vec4 ambient2 = light2.ambient * material.ambient;
	vec4 diffuse2 = light2.diffuse * material.diffuse * diff2;
	vec4 specular2 = light2.specular * material.specular * spec2;

	// Combine both lights
	vec4 texColor = texture(texture1, varyingTexCoord);
	vec4 lighting = globalAmbient * material.ambient
	              + ambient1 + diffuse1 + specular1
	              + ambient2 + diffuse2 + specular2;

	fragColor = texColor * lighting;
}