package org.glimpseframework.android.hologram;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.FloatBuffer;
import java.util.EnumMap;
import java.util.Map;

/**
 * OpenGL shader program wrapper.
 *
 * @author Sławomir Czerwiński
 */
class ShaderProgram {

	public enum Parameter {
		MVP_MATRIX("u_MVPMatrix"),
		VERTEX_POSITION("a_VertexPosition"),
		TEXTURE_COORDINATES("a_TextureCoordinates"),
		ACCELEROMETER_COORDINATES("u_AccelerometerCoordinates");

		Parameter(String name) {
			this.name = name;
		}

		private String name;
	}

	public ShaderProgram(Map<Shader.ShaderType, String> shaderSources) {
		for (Shader.ShaderType shaderType : Shader.ShaderType.values()) {
			shaders.put(shaderType, new Shader(shaderType, shaderSources.get(shaderType)));
		}
	}

	public void build() {
		for (Shader.ShaderType shaderType : Shader.ShaderType.values()) {
			shaders.get(shaderType).compile();
		}
		link();
	}

	private void link() {
		programHandle = GLES20.glCreateProgram();
		if (programHandle != 0) {
			GLES20.glAttachShader(programHandle, shaders.get(Shader.ShaderType.VERTEX_SHADER).getHandle());
			GLES20.glAttachShader(programHandle, shaders.get(Shader.ShaderType.FRAGMENT_SHADER).getHandle());
			GLES20.glLinkProgram(programHandle);
			verifyLinkStatus();
		}
	}

	private void verifyLinkStatus() {
		final int[] linkStatus = new int[1];
		GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);
		if (linkStatus[0] == 0) {
			String errorMessage = GLES20.glGetProgramInfoLog(programHandle);
			delete();
			Log.e(LOG_TAG, "Could not link shaders into a program: " + errorMessage);
			throw new IllegalStateException("Could not link a shader program: " + errorMessage);
		}
	}

	public void use() {
		GLES20.glUseProgram(programHandle);
	}

	public void delete() {
		GLES20.glDeleteProgram(programHandle);
		for (Shader.ShaderType shaderType : Shader.ShaderType.values()) {
			shaders.get(shaderType).delete();
		}
	}

	public int getUniformLocation(String name) {
		return GLES20.glGetUniformLocation(programHandle, name);
	}

	public int getAttribLocation(String name) {
		return GLES20.glGetAttribLocation(programHandle, name);
	}

	public void pushVector(Parameter parameter, Vector vector) {
		GLES20.glUniform3fv(getUniformLocation(parameter.name), 1, vector.getCoordinates(), 0);
	}

	public void pushMatrix(Parameter parameter, float[] matrix) {
		GLES20.glUniformMatrix4fv(getUniformLocation(parameter.name), 1, false, matrix, 0);
	}

	public void pushVertexAttribArray(Parameter parameter, FloatBuffer buffer, int vectorSize) {
		int handle = getAttribLocation(parameter.name);
		buffer.position(0);
		GLES20.glVertexAttribPointer(handle, vectorSize, GLES20.GL_FLOAT, false, vectorSize * BYTES_PER_FLOAT, buffer);
		GLES20.glEnableVertexAttribArray(handle);
	}

	public void drawVertices(FloatBuffer buffer) {
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, buffer.remaining());
	}

	private static final String LOG_TAG = "ShaderProgram";

	private static final int BYTES_PER_FLOAT = 4;

	private final Map<Shader.ShaderType, Shader> shaders =
			new EnumMap<Shader.ShaderType, Shader>(Shader.ShaderType.class);

	private int programHandle;
}
