package org.glimpseframework.android.hologram;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Glimpse Hologram renderer.
 *
 * @author Sławomir Czerwiński
 */
class GlimpseHoloRenderer implements GLSurfaceView.Renderer {

	public GlimpseHoloRenderer(GlimpseHoloConfig config, Accelerometer accelerometer) {
		this.config = config;
		this.accelerometer = accelerometer;
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		initGLSettings();
		initShaders();
		initTextures();
	}

	private void initGLSettings() {
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
	}

	private void initShaders() {
		program = config.createShaderProgram();
		program.build();
		program.use();
	}

	private void initTextures() {
		texturesStore = config.createTexturesStore();
		texturesStore.generateTextures();
		texturesStore.bindTextures(program);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		GLES20.glViewport(0, 0, width, height);
		Matrix.orthoM(mvpMatrix, 0, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f);
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

		program.pushVector(ShaderProgram.Parameter.ACCELEROMETER_COORDINATES, accelerometer.getVector());
		program.pushMatrix(ShaderProgram.Parameter.MVP_MATRIX, mvpMatrix);

		scene.draw(program);
	}

	private final GlimpseHoloScene scene = new GlimpseHoloScene();

	private GlimpseHoloConfig config;

	private ShaderProgram program;
	private TexturesStore texturesStore;

	private Accelerometer accelerometer;

	private float[] mvpMatrix = new float[16];
}
