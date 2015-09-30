package org.glimpseframework.android.hologram;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import java.util.EnumMap;
import java.util.Map;

/**
 * Glimpse Hologram view configuration.
 *
 * @author Sławomir Czerwiński
 */
class GlimpseHoloConfig {

	public GlimpseHoloConfig(Context context, AttributeSet attrs) {
		this.context = context;
		attributes = (attrs != null)
				? context.getTheme().obtainStyledAttributes(attrs, R.styleable.GlimpseHoloView, 0, 0)
				: null;
	}

	public ShaderProgram createShaderProgram() {
		RawResourceLoader loader = new RawResourceLoader(context);

		Map<Shader.ShaderType, String> shaderSources =
				new EnumMap<Shader.ShaderType, String>(Shader.ShaderType.class);

		shaderSources.put(Shader.ShaderType.VERTEX_SHADER, loader.loadResource(
				getResourceId(R.styleable.GlimpseHoloView_vertexShader, R.raw.glimpse_holo_vertex)));
		shaderSources.put(Shader.ShaderType.FRAGMENT_SHADER, loader.loadResource(
				getResourceId(R.styleable.GlimpseHoloView_fragmentShader, R.raw.glimpse_holo_fragment)));

		return new ShaderProgram(shaderSources);
	}

	private int getResourceId(int styleableId, int defaultId) {
		return (attributes != null)
				? attributes.getResourceId(styleableId, defaultId)
				: defaultId;
	}

	public TexturesStore createTexturesStore() {
		Map<Texture.TextureType, Integer> textureResources =
				new EnumMap<Texture.TextureType, Integer>(Texture.TextureType.class);

		textureResources.put(Texture.TextureType.BACKGROUND_TEXTURE,
				getResourceId(R.styleable.GlimpseHoloView_backgroundTexture, R.drawable.background));
		textureResources.put(Texture.TextureType.HOLOGRAM_TEXTURE,
				getResourceId(R.styleable.GlimpseHoloView_hologramTexture, R.drawable.hologram));
		textureResources.put(Texture.TextureType.HOLO_MAP_TEXTURE,
				getResourceId(R.styleable.GlimpseHoloView_holoMapTexture, R.drawable.holo_map));

		return new TexturesStore(context, textureResources);
	}

	private Context context;
	private TypedArray attributes;
}
