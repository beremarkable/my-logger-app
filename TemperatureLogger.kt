package com.example.full_logger_v1

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class TemperatureLogger(private val context: Context, private val intervalMinutes: Long) {

    private lateinit var dbHelper: LoggerDatabaseHelper
    private var handler: Handler = Handler(Looper.getMainLooper())
    private lateinit var temperatureRunnable: Runnable

    fun startLogging() {
        dbHelper = LoggerDatabaseHelper(context)

        Log.d("TemperatureLogger", "Started temperature logging with interval: $intervalMinutes minutes")
        temperatureRunnable = object : Runnable {
            override fun run() {
                Log.d("TemperatureLogger", "Attempting to log temperature data")

                // Generate fake temperature data
                val fakeTemperature = generateFakeTemperature()

                if (fakeTemperature != 0.0) {
                    dbHelper.insertTemperatureData(fakeTemperature)
                    Log.d("TemperatureLogger", "Logged temperature: $fakeTemperature°C")
                    Toast.makeText(context, "Temperature logged: $fakeTemperature°C", Toast.LENGTH_SHORT).show()
                } else {
                    Log.d("TemperatureLogger", "Invalid temperature reading")
                    Toast.makeText(context, "Invalid temperature reading", Toast.LENGTH_SHORT).show()
                }

                handler.postDelayed(this, TimeUnit.MINUTES.toMillis(intervalMinutes))
            }
        }

        handler.post(temperatureRunnable)
    }

    fun stopLogging() {
        handler.removeCallbacks(temperatureRunnable)
        Log.d("TemperatureLogger", "Stopped temperature logging")
    }

    private fun generateFakeTemperature(): Double {
        return Random.nextDouble(-10.0, 40.0)
    }
}