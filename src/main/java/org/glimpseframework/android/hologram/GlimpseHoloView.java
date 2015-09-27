package org.glimpseframework.android.hologram;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.content.res.TypedArray;
import android.graphics.PixelFormat;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.AttributeSet;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.EnumMap;
import java.util.Map;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Glimpse Hologram view for Android application.
 *
 * @author Sławomir Czerwiński
 */
public class GlimpseHoloView extends GLSurfaceView {

	private class GlimpseHoloRenderer implements Renderer {

		public GlimpseHoloRenderer() {
			final float[] vertices = {
					-1.0f, -1.0f, 0.0f,
					1.0f, -1.0f, 0.0f,
					-1.0f, 1.0f, 0.0f,
					-1.0f, 1.0f, 0.0f,
					1.0f, -1.0f, 0.0f,
					1.0f, 1.0f, 0.0f,
			};
			final float[] textureCoordinates = {
					0.0f, 1.0f,
					1.0f, 1.0f,
					0.0f, 0.0f,
					0.0f, 0.0f,
					1.0f, 1.0f,
					1.0f, 0.0f,
			};
			verticesBuffer = ByteBuffer
					.allocateDirect(vertices.length * BYTES_PER_FLOAT)
					.order(ByteOrder.nativeOrder())
					.asFloatBuffer()
					.put(vertices);
			textureCoordinatesBuffer = ByteBuffer
					.allocateDirect(textureCoordinates.length * BYTES_PER_FLOAT)
					.order(ByteOrder.nativeOrder())
					.asFloatBuffer()
					.put(textureCoordinates);
		}

		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			GLES20.glEnable(GLES20.GL_BLEND);
			GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

			GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
			Matrix.orthoM(mvpMatrix, 0, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f);

			initShaders();
			initTextures();
		}

		private void initShaders() {
			Map<Shader.ShaderType, String> shaderSources =
					new EnumMap<Shader.ShaderType, String>(Shader.ShaderType.class);
			shaderSources.put(Shader.ShaderType.VERTEX_SHADER, vertexShaderSource);
			shaderSources.put(Shader.ShaderType.FRAGMENT_SHADER, fragmentShaderSource);

			program = new ShaderProgram(shaderSources);
			program.build();

			mvpMatrixHandle = program.getUniformLocation("u_MVPMatrix");
			vertexPositionHandle = program.getAttribLocation("a_VertexPosition");
			textureCoordinatesHandle = program.getAttribLocation("a_TextureCoordinates");
			accelerometerCoordinatesHandle = program.getUniformLocation("u_AccelerometerCoordinates");

			program.use();
		}

		private void initTextures() {
			Map<Texture.TextureType, Integer> textureResources =
					new EnumMap<Texture.TextureType, Integer>(Texture.TextureType.class);
			textureResources.put(Texture.TextureType.BACKGROUND_TEXTURE, R.drawable.background);
			textureResources.put(Texture.TextureType.HOLOGRAM_TEXTURE, R.drawable.hologram);
			textureResources.put(Texture.TextureType.HOLO_MAP_TEXTURE, R.drawable.holo_map);

			for (Texture.TextureType textureType : Texture.TextureType.values()) {
				textures.put(textureType, new Texture(context, textureType, textureResources.get(textureType)));
			}

			for (Texture.TextureType textureType : Texture.TextureType.values()) {
				textures.get(textureType).generate();
			}

			for (Texture.TextureType textureType : Texture.TextureType.values()) {
				textures.get(textureType).bind(program);
			}
		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
			GLES20.glViewport(0, 0, width, height);
		}

		@Override
		public void onDrawFrame(GL10 gl) {
			GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

			verticesBuffer.position(0);
			textureCoordinatesBuffer.position(0);
			GLES20.glUniform3fv(accelerometerCoordinatesHandle, 1, accelerometer.getVector(), 0);
			GLES20.glVertexAttribPointer(vertexPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * BYTES_PER_FLOAT, verticesBuffer);
			GLES20.glEnableVertexAttribArray(vertexPositionHandle);
			GLES20.glVertexAttribPointer(textureCoordinatesHandle, 2, GLES20.GL_FLOAT, false, 2 * BYTES_PER_FLOAT, textureCoordinatesBuffer);
			GLES20.glEnableVertexAttribArray(textureCoordinatesHandle);
			GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);
			GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, verticesBuffer.remaining());
		}

		private static final int BYTES_PER_FLOAT = 4;

		private final FloatBuffer verticesBuffer;
		private final FloatBuffer textureCoordinatesBuffer;

		private float[] mvpMatrix = new float[16];
		private ShaderProgram program;
		private Map<Texture.TextureType, Texture> textures =
				new EnumMap<Texture.TextureType, Texture>(Texture.TextureType.class);

		private int mvpMatrixHandle;
		private int vertexPositionHandle;
		private int textureCoordinatesHandle;
		private int accelerometerCoordinatesHandle;
	}

	public GlimpseHoloView(Context context) {
		super(context);
		applyDefaultAttributes(context);
		init(context);
	}

	private void applyDefaultAttributes(Context context) {
		RawResourceLoader loader = new RawResourceLoader(context);
		vertexShaderSource = loader.loadResource(R.raw.glimpse_holo_vertex);
		fragmentShaderSource = loader.loadResource(R.raw.glimpse_holo_fragment);
	}

	private void init(Context context) {
		this.context = context;
		if (isInEditMode()) return;
		final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
		final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;
		if (supportsEs2) {
			setZOrderOnTop(true);
			setEGLContextClientVersion(2);
			setEGLConfigChooser(8, 8, 8, 8, 16, 0);
			getHolder().setFormat(PixelFormat.TRANSLUCENT);
			setRenderer(new GlimpseHoloRenderer());
		} else {
			throw new UnsupportedOperationException("OpenGL ES 2.0 not supported.");
		}
		accelerometer = new Accelerometer(context);
	}

	public GlimpseHoloView(Context context, AttributeSet attrs) {
		super(context, attrs);
		applyAttributes(context, attrs);
		init(context);
	}

	private void applyAttributes(Context context, AttributeSet attrs) {
		TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.GlimpseHoloView, 0, 0);
		RawResourceLoader loader = new RawResourceLoader(context);
		vertexShaderSource = loader.loadResource(attributes.getResourceId(R.styleable.GlimpseHoloView_vertexShader, R.raw.glimpse_holo_vertex));
		fragmentShaderSource = loader.loadResource(attributes.getResourceId(R.styleable.GlimpseHoloView_fragmentShader, R.raw.glimpse_holo_fragment));
		attributes.recycle();
	}

	@Override
	public void onResume() {
		super.onResume();
		accelerometer.register();
	}

	@Override
	public void onPause() {
		super.onPause();
		accelerometer.unregister();
	}

	private Context context;

	private String vertexShaderSource;
	private String fragmentShaderSource;

	private Accelerometer accelerometer;
}
