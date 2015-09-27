package org.glimpseframework.android.hologram;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

/**
 * OpenGL texture wrapper.
 *
 * @author Sławomir Czerwiński
 */
class Texture {

	enum TextureType {
		BACKGROUND_TEXTURE(GLES20.GL_TEXTURE0, 0),
		HOLOGRAM_TEXTURE(GLES20.GL_TEXTURE1, 1),
		HOLO_MAP_TEXTURE(GLES20.GL_TEXTURE2, 2),
		;

		TextureType(int texture, int index) {
			this.texture = texture;
			this.index = index;
		}

		private final int texture;
		private final int index;
	}

	Texture(final Context context, final int resourceId) {
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
			throw new IllegalStateException("Could not create a texture");
		}
	}

	void bind(TextureType textureType, int handle) {
		GLES20.glActiveTexture(textureType.texture);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
		GLES20.glUniform1i(handle, textureType.index);
	}

	private final int[] textureHandle = new int[1];
}
