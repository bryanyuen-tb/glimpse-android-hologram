uniform mat4 u_MVPMatrix;

attribute vec4 a_VertexPosition;
attribute vec2 a_TextureCoordinates;

uniform float u_AccelerometerX;
uniform float u_AccelerometerY;

varying vec2 v_TextureCoordinates;
varying vec2 v_AccelerometerCoordinates;

void main() {
	gl_Position = u_MVPMatrix * a_VertexPosition;
	v_TextureCoordinates = a_TextureCoordinates;
	v_AccelerometerCoordinates = vec2(u_AccelerometerX, u_AccelerometerY);
}
