package org.glimpseframework.android.hologram;

/**
 * 3-dimensional vector.
 *
 * @author Sławomir Czerwiński
 */
final class Vector {

	public Vector(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public static Vector sum(Vector... vectors) {
		float x = 0.0f;
		float y = 0.0f;
		float z = 0.0f;
		for (Vector vector : vectors) {
			x += vector.x;
			y += vector.y;
			z += vector.z;
		}
		return new Vector(x, y, z);
	}

	public float[] getCoordinates() {
		return new float[] { x, y, z };
	}

	public static final Vector NULL_VECTOR = new Vector(0.0f, 0.0f, 0.0f);

	private final float x;
	private final float y;
	private final float z;
}
