package com.example.full_logger_v1

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import java.util.concurrent.TimeUnit


class GPSLogger(private val context: Context, private val intervalMinutes: Long) {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var handler: Handler
    private lateinit var gpsIntervalRunnable: Runnable
    private lateinit var dbHelper: LoggerDatabaseHelper

    fun startLogging() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        dbHelper = LoggerDatabaseHelper(context)
        handler = Handler(Looper.getMainLooper())

        setupGPSIntervalLogging(intervalMinutes)
    }

    private fun setupGPSIntervalLogging(intervalMinutes: Long) {
        gpsIntervalRunnable = object : Runnable {
            override fun run() {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        (context as AppCompatActivity),
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        LOCATION_REQUEST_CODE
                    )
                    return
                }

                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        dbHelper.insertGPSData(location.latitude, location.longitude)
                        Toast.makeText(
                            context,
                            "GPS logged: Lat=${location.latitude}, Lon=${location.longitude}",
                            Toast.LENGTH_LONG
                        ).show()
                        Log.d("GPSLogger", "GPS logged at: ${location.latitude}, ${location.longitude}")
                    } else {
                        Log.d("GPSLogger", "GPS location is null")
                    }
                }.addOnFailureListener {
                    Log.d("GPSLogger", "Failed to retrieve location: ${it.message}")
                }

                // Schedule the next GPS log after the interval
                handler.postDelayed(this, TimeUnit.MINUTES.toMillis(intervalMinutes))
            }
        }

        // Start the logging immediately
        handler.post(gpsIntervalRunnable)
        Log.d("GPSLogger", "Started GPS logging with interval: $intervalMinutes minutes")
    }

    fun stopLogging() {
        handler.removeCallbacks(gpsIntervalRunnable)
        Toast.makeText(context, "GPS logging stopped", Toast.LENGTH_SHORT).show()
        Log.d("GPSLogger", "Stopped GPS logging")
    }

    companion object {
        private const val LOCATION_REQUEST_CODE = 1000
    }
}