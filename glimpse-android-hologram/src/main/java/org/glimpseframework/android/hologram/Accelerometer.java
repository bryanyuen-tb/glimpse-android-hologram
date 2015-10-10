package org.glimpseframework.android.hologram;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.Arrays;

/**
 * Android accelerometer wrapper.
 *
 * @author Sławomir Czerwiński
 */
class Accelerometer implements SensorEventListener {

	public Accelerometer(Context context) {
		sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		Arrays.fill(vectorHistory, Vector.NULL_VECTOR);
	}

	public void register() {
		sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
	}

	public void unregister() {
		sensorManager.unregisterListener(this);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			index %= SMOOTHNESS;
			vectorHistory[index++] = new Vector(event.values[0], event.values[1], event.values[2]);
			vector = Vector.sum(vectorHistory);
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	public Vector getVector() {
		return vector;
	}

	private static final int SMOOTHNESS = 20;

	private SensorManager sensorManager;
	private Sensor accelerometer;

	private Vector[] vectorHistory = new Vector[SMOOTHNESS];
	private int index;

	private Vector vector;
}
