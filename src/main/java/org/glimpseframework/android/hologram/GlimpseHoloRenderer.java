package org.glimpseframework.android.hologram;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Glimpse Hologram renderer.
 *
 * @author Sławomir Czerwiński
 */
class GlimpseHoloRenderer implements GLSurfaceView.Renderer {

	public GlimpseHoloRenderer(ShaderProgram program, TexturesStore texturesStore, Accelerometer accelerometer) {
		this.program = program;
		this.texturesStore = texturesStore;
		this.accelerometer = accelerometer;
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
		program.build();

		mvpMatrixHandle = program.getUniformLocation("u_MVPMatrix");
		vertexPositionHandle = program.getAttribLocation("a_VertexPosition");
		textureCoordinatesHandle = program.getAttribLocation("a_TextureCoordinates");
		accelerometerCoordinatesHandle = program.getUniformLocation("u_AccelerometerCoordinates");

		program.use();
	}

	private void initTextures() {
		texturesStore.generateTextures();
		texturesStore.bindTextures(program);
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
	private TexturesStore texturesStore;

	private int mvpMatrixHandle;
	private int vertexPositionHandle;
	private int textureCoordinatesHandle;
	private int accelerometerCoordinatesHandle;

	private Accelerometer accelerometer;
}
