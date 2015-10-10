package org.glimpseframework.android.hologram;

import android.opengl.GLES20;
import android.util.Log;

/**
 * OpenGL shader wrapper.
 *
 * @author Sławomir Czerwiński
 */
class Shader {

	enum ShaderType {
		VERTEX_SHADER(GLES20.GL_VERTEX_SHADER),
		FRAGMENT_SHADER(GLES20.GL_FRAGMENT_SHADER);

		ShaderType(int type) {
			this.type = type;
		}

		private final int type;
	}

	public Shader(ShaderType shaderType, String source) {
		this.shaderType = shaderType;
		this.source = source;
	}

	public void compile() {
		shaderHandle = GLES20.glCreateShader(shaderType.type);
		if (shaderHandle != 0) {
			GLES20.glShaderSource(shaderHandle, source);
			GLES20.glCompileShader(shaderHandle);
			verifyCompileStatus();
		}
	}

	private void verifyCompileStatus() {
		final int[] compileStatus = new int[1];
		GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
		if (compileStatus[0] == 0) {
			String errorMessage = GLES20.glGetShaderInfoLog(shaderHandle);
			delete();
			Log.e(LOG_TAG, "Could not compile a shader: " + errorMessage);
			throw new IllegalStateException("Could not compile a shader: " + errorMessage);
		}
	}

	public void delete() {
		GLES20.glDeleteShader(shaderHandle);
	}

	public int getHandle() {
		return shaderHandle;
	}

	private static final String LOG_TAG = "Shader";

	private final ShaderType shaderType;
	private final String source;

	private int shaderHandle;
}
