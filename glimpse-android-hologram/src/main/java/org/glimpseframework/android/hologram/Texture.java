package org.glimpseframework.android.hologram;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

/**
 * OpenGL texture wrapper.
 *
 * @author Sławomir Czerwiński
 */
class Texture {

	public enum TextureType {
		BACKGROUND_TEXTURE(GLES20.GL_TEXTURE0, "u_BackgroundTexture"),
		HOLOGRAM_TEXTURE(GLES20.GL_TEXTURE1, "u_HologramTexture"),
		HOLO_MAP_TEXTURE(GLES20.GL_TEXTURE2, "u_HoloMapTexture");

		TextureType(int texture, String name) {
			this.texture = texture;
			this.name = name;
		}

		private final int texture;
		private final String name;
	}

	public Texture(Context context, TextureType textureType, int resourceId) {
		this.context = context;
		this.textureType = textureType;
		this.resourceId = resourceId;
	}

	public void generate() {
		Log.d(LOG_TAG, "Loading " + textureType.name());
		GLES20.glGenTextures(1, textureHandle, 0);
		if (textureHandle[0] != 0) {
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inScaled = false;
			final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
			GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, bitmap, 0);
			GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
			bitmap.recycle();
		} else {
			Log.e(LOG_TAG, "Could not create a texture");
			throw new IllegalStateException("Could not create a texture");
		}
	}

	public void bind(ShaderProgram shaderProgram) {
		Log.d(LOG_TAG, String.format("Binding texture %s to %s", textureType.name(), textureType.name));
		GLES20.glActiveTexture(textureType.texture);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
		GLES20.glUniform1i(shaderProgram.getUniformLocation(textureType.name), textureType.ordinal());
	}

	private static final String LOG_TAG = "Texture";

	private final TextureType textureType;
	private final Context context;
	private final int resourceId;

	private final int[] textureHandle = new int[1];
}
