package org.glimpseframework.android.hologram;

import android.content.Context;

import java.util.EnumMap;
import java.util.Map;

/**
 * OpenGL textures store.
 *
 * @author Sławomir Czerwiński
 */
class TexturesStore {

	public TexturesStore(Context context, Map<Texture.TextureType, Integer> textureResources) {
		for (Texture.TextureType textureType : Texture.TextureType.values()) {
			textures.put(textureType, new Texture(context, textureType, textureResources.get(textureType)));
		}
	}

	public void generateTextures() {
		for (Texture.TextureType textureType : Texture.TextureType.values()) {
			textures.get(textureType).generate();
		}
	}

	public void bindTextures(ShaderProgram program) {
		for (Texture.TextureType textureType : Texture.TextureType.values()) {
			textures.get(textureType).bind(program);
		}
	}

	private final Map<Texture.TextureType, Texture> textures =
			new EnumMap<Texture.TextureType, Texture>(Texture.TextureType.class);
}
