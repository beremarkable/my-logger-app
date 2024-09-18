package com.example.full_logger_v1

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class SavedTripsActivity : AppCompatActivity() {

    private lateinit var dbHelper: LoggerDatabaseHelper
    private lateinit var tripsAdapter: TripsAdapter
    private val STORAGE_PERMISSION_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_trips)

        // Initialize the database helper
        dbHelper = LoggerDatabaseHelper(this)

        // Set up the RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.trips_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Fetch all saved trips from the database
        val tripsCursor = dbHelper.getAllTrips()

        if (tripsCursor.count == 0) {
            Toast.makeText(this, "No trips found.", Toast.LENGTH_SHORT).show()
            return
        }

        // Set up the adapter for the RecyclerView
        tripsAdapter = TripsAdapter(tripsCursor) { tripId ->
            // Handle click to open trip details
            val intent = Intent(this, TripDetailsActivity::class.java)
            intent.putExtra("tripId", tripId)
            startActivity(intent)
        }
        recyclerView.adapter = tripsAdapter

        // Export CSV button
        val exportButton = findViewById<Button>(R.id.export_csv_button)
        exportButton.setOnClickListener {
            if (checkStoragePermission()) {
                val csvFilePath = dbHelper.exportDataToCSV()

                if (csvFilePath.contains("Error")) {
                    Toast.makeText(this, "Failed to export data.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Data exported to CSV.", Toast.LENGTH_SHORT).show()
                    shareCSV(csvFilePath)
                }
            }
        }
    }

    // Function to share the CSV file via email or other apps
    private fun shareCSV(csvFilePath: String) {
        val csvFile = File(csvFilePath)
        val csvUri = Uri.fromFile(csvFile)

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/csv"
        intent.putExtra(Intent.EXTRA_STREAM, csvUri)
        intent.putExtra(Intent.EXTRA_SUBJECT, "Exported Trip Data")
        intent.putExtra(Intent.EXTRA_TEXT, "Here is the exported trip data.")

        startActivity(Intent.createChooser(intent, "Share CSV"))
    }

    // Request storage permission if it's not granted
    private fun checkStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
                false
            } else {
                true
            }
        } else {
            // No need for permission on Android 10+ for app-specific external storage
            true
        }
    }

    // Handle the permission result for storage access
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Storage Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Storage Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}