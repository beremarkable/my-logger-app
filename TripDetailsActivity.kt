package com.example.full_logger_v1

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TripDetailsActivity : AppCompatActivity() {

    private lateinit var dbHelper: LoggerDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_details)

        // Get the trip ID passed from SavedTripsActivity
        val tripId = intent.getIntExtra("tripId", -1)

        dbHelper = LoggerDatabaseHelper(this)

        // Query the trip data from the database
        val tripCursor = dbHelper.getAllTrips()
        tripCursor.moveToPosition(tripId)

        // Display the trip details
        val gpsLat = tripCursor.getDouble(tripCursor.getColumnIndexOrThrow("gps_lat"))
        val gpsLon = tripCursor.getDouble(tripCursor.getColumnIndexOrThrow("gps_lon"))
        val temperature = tripCursor.getDouble(tripCursor.getColumnIndexOrThrow("temperature"))
        val shockEvent = tripCursor.getDouble(tripCursor.getColumnIndexOrThrow("shock_event"))
        val timestamp = tripCursor.getString(tripCursor.getColumnIndexOrThrow("timestamp"))

        findViewById<TextView>(R.id.trip_details_text).text = """
            Trip Time: $timestamp
            GPS: $gpsLat, $gpsLon
            Temperature: $temperature
            Shock: $shockEvent
        """.trimIndent()
    }
}