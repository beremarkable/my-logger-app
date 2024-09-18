package com.example.full_logger_v1

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class LoggingActivity : AppCompatActivity() {

    private var temperatureLogger: TemperatureLogger? = null
    private var shockLogger: ShockLogger? = null

    private lateinit var wakeLock: PowerManager.WakeLock

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logging)

        // Acquire a wake lock to keep the CPU running
        try {
            val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "full_logger_v1:WakeLock")
            wakeLock.acquire()
            Log.d("LoggingActivity", "WakeLock acquired")
        } catch (e: Exception) {
            Log.e("LoggingActivity", "Error acquiring wake lock", e)
        }

        // Retrieve data from the intent
        val gpsLogging = intent.getBooleanExtra("gpsLogging", false)
        val temperatureLogging = intent.getBooleanExtra("temperatureLogging", false)
        val shockLogging = intent.getBooleanExtra("shockLogging", false)
        val gpsInterval = intent.getLongExtra("gpsInterval", 0L)
        val temperatureInterval = intent.getLongExtra("temperatureInterval", 0L)

        // Initialize loggers if required
        try {
            // Use LocationLoggingService for GPS logging
            if (gpsLogging) {
                startLocationLoggingService()
            }

            if (temperatureLogging) {
                temperatureLogger = TemperatureLogger(this, temperatureInterval)
                temperatureLogger?.startLogging()
            }

            if (shockLogging) {
                shockLogger = ShockLogger(this)
                shockLogger?.startLogging()
            }

            // Request battery optimization exclusion
            requestBatteryOptimization()

            // Stop Logging button with long press
            val stopButton = findViewById<Button>(R.id.stop_logging_button)
            stopButton.setOnLongClickListener {
                stopLogging() // Stop the logging
                navigateToSavedTrips() // Navigate to the Saved Trips page
                true
            }
        } catch (e: Exception) {
            Log.e("LoggingActivity", "Error during logging setup", e)
        }
    }

    private fun stopLogging() {
        try {
            stopLocationLoggingService()

            temperatureLogger?.stopLogging()
            shockLogger?.stopLogging()

            if (wakeLock.isHeld) {
                wakeLock.release() // Release the wake lock
                Log.d("LoggingActivity", "WakeLock released")
            }

            Toast.makeText(this, "Logging stopped", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("LoggingActivity", "Error during stop logging", e)
        }
    }

    private fun startLocationLoggingService() {
        try {
            val intent = Intent(this, LocationLoggingService::class.java)
            startService(intent) // Start the service to handle GPS logging in the background
        } catch (e: Exception) {
            Log.e("LoggingActivity", "Error starting LocationLoggingService", e)
        }
    }

    private fun stopLocationLoggingService() {
        try {
            val intent = Intent(this, LocationLoggingService::class.java)
            stopService(intent) // Stop the service when logging stops
        } catch (e: Exception) {
            Log.e("LoggingActivity", "Error stopping LocationLoggingService", e)
        }
    }

    private fun navigateToSavedTrips() {
        try {
            // Navigate to the SavedTripsActivity
            val intent = Intent(this, SavedTripsActivity::class.java)
            startActivity(intent)
            finish() // Close the current activity
        } catch (e: Exception) {
            Log.e("LoggingActivity", "Error navigating to SavedTrips", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (wakeLock.isHeld) {
            wakeLock.release() // Ensure wake lock is released if activity is destroyed
            Log.d("LoggingActivity", "WakeLock released onDestroy")
        }
    }

    // Function to request the user to exclude the app from battery optimization
    private fun requestBatteryOptimization() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
                if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
                    AlertDialog.Builder(this)
                        .setTitle("Battery Optimization")
                        .setMessage("To ensure data logging works correctly, please exclude this app from battery optimization.")
                        .setPositiveButton("Allow") { _, _ ->
                            val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                            startActivity(intent)
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
                }
            }
        } catch (e: Exception) {
            Log.e("LoggingActivity", "Error requesting battery optimization", e)
        }
    }
}