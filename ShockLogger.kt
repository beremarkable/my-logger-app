package com.example.full_logger_v1

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.os.Looper
import android.widget.Toast

class ShockLogger(private val context: Context) : SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private lateinit var dbHelper: LoggerDatabaseHelper
    private var handler: Handler = Handler(Looper.getMainLooper())

    private var isLogging = false

    fun startLogging() {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        dbHelper = LoggerDatabaseHelper(context)

        if (accelerometer != null) {
            // Register the listener only if the accelerometer is available
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
            isLogging = true
            Toast.makeText(context, "Shock logging started", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Accelerometer not available on this device", Toast.LENGTH_LONG).show()
        }
    }

    fun stopLogging() {
        if (isLogging) {
            sensorManager.unregisterListener(this)
            isLogging = false
            Toast.makeText(context, "Shock logging stopped", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null && isLogging) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val accelerationMagnitude = Math.sqrt((x * x + y * y + z * z).toDouble())

            // Threshold to detect shock events (you can adjust this value)
            if (accelerationMagnitude > 15) {
                // Log shock event to the database
                dbHelper.insertShockData(accelerationMagnitude)
                Toast.makeText(context, "Shock detected! Magnitude: $accelerationMagnitude", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // We don't need to handle this for shock detection
    }
}