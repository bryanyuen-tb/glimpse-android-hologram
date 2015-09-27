package org.glimpseframework.android.hologram;

import android.opengl.GLES20;

/**
 * OpenGL shader wrapper.
 *
 * @author Sławomir Czerwiński
 */
class Shader {

	enum ShaderType {
		VERTEX_SHADER(GLES20.GL_VERTEX_SHADER),
		FRAGMENT_SHADER(GLES20.GL_FRAGMENT_SHADER),
		;

		ShaderType(int type) {
			this.type = type;
		}

		private final int type;
	}

	Shader(ShaderType shaderType, String source) {
		shaderHandle = GLES20.glCreateShader(shaderType.type);
		if (shaderHandle != 0) {
			GLES20.glShaderSource(shaderHandle, source);
			GLES20.glCompileShader(shaderHandle);
			final int[] compileStatus = new int[1];
			GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
			if (compileStatus[0] == 0) {
				String error = GLES20.glGetShaderInfoLog(shaderHandle);
				delete();
				throw new IllegalStateException("Could not compile a shader: " + error);
			}
		}
	}

	void delete() {
		GLES20.glDeleteShader(shaderHandle);
	}

	int getHandle() {
		return shaderHandle;
	}

	private int shaderHandle;
}
