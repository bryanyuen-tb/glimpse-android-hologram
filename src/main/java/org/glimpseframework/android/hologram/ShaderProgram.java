package org.glimpseframework.android.hologram;

import android.opengl.GLES20;

/**
 * OpenGL shader program wrapper.
 *
 * @author Sławomir Czerwiński
 */
class ShaderProgram {

	ShaderProgram(Shader vertexShader, Shader fragmentShader) {
		programHandle = GLES20.glCreateProgram();
		if (programHandle != 0) {
			GLES20.glAttachShader(programHandle, vertexShader.getHandle());
			GLES20.glAttachShader(programHandle, fragmentShader.getHandle());
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

	void use() {
		GLES20.glUseProgram(programHandle);
	}

	void delete() {
		GLES20.glDeleteProgram(programHandle);
	}

	int getUniformLocation(String name) {
		return GLES20.glGetUniformLocation(programHandle, name);
	}

	int getAttribLocation(String name) {
		return GLES20.glGetAttribLocation(programHandle, name);
	}

	private int programHandle;
}
