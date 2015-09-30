package org.glimpseframework.android.hologram;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Glimpse Hologram scene.
 *
 * @author Sławomir Czerwiński
 */
class GlimpseHoloScene {

	public GlimpseHoloScene() {
		verticesBuffer = ByteBuffer
				.allocateDirect(VERTICES.length * BYTES_PER_FLOAT)
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer()
				.put(VERTICES);
		textureCoordinatesBuffer = ByteBuffer
				.allocateDirect(TEXTURE_COORDINATES.length * BYTES_PER_FLOAT)
				.order(ByteOrder.nativeOrder())
				.asFloatBuffer()
				.put(TEXTURE_COORDINATES);
	}

	public void draw(ShaderProgram program) {
		program.pushVertexAttribArray(ShaderProgram.Parameter.VERTEX_POSITION, verticesBuffer, 3);
		program.pushVertexAttribArray(ShaderProgram.Parameter.TEXTURE_COORDINATES, textureCoordinatesBuffer, 2);
		program.drawVertices(verticesBuffer);
	}

	private static final int BYTES_PER_FLOAT = 4;

	private static final float[] VERTICES = {
			-1.0f, -1.0f, 0.0f,
			1.0f, -1.0f, 0.0f,
			-1.0f, 1.0f, 0.0f,
			-1.0f, 1.0f, 0.0f,
			1.0f, -1.0f, 0.0f,
			1.0f, 1.0f, 0.0f,
	};
	private static final float[] TEXTURE_COORDINATES = {
			0.0f, 1.0f,
			1.0f, 1.0f,
			0.0f, 0.0f,
			0.0f, 0.0f,
			1.0f, 1.0f,
			1.0f, 0.0f,
	};

	private FloatBuffer verticesBuffer;
	private FloatBuffer textureCoordinatesBuffer;
}
