package org.glimpseframework.android.hologram;

import android.opengl.GLES20;

import java.util.EnumMap;
import java.util.Map;

/**
 * OpenGL shader program wrapper.
 *
 * @author Sławomir Czerwiński
 */
class ShaderProgram {

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
			final int[] linkStatus = new int[1];
			GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);
			if (linkStatus[0] == 0) {
				String error = GLES20.glGetProgramInfoLog(programHandle);
				delete();
				throw new IllegalStateException("Could not link a shader program: " + error);
			}
		}
	}

	public void use() {
		GLES20.glUseProgram(programHandle);
	}

	public void delete() {
		GLES20.glDeleteProgram(programHandle);
	}

	public int getUniformLocation(String name) {
		return GLES20.glGetUniformLocation(programHandle, name);
	}

	public int getAttribLocation(String name) {
		return GLES20.glGetAttribLocation(programHandle, name);
	}

	private final Map<Shader.ShaderType, Shader> shaders =
			new EnumMap<Shader.ShaderType, Shader>(Shader.ShaderType.class);

	private int programHandle;
}
