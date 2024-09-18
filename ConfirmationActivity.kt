package com.example.full_logger_v1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ConfirmationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmation)

        // Retrieve data passed from StartupWizardActivity
        val gpsLogging = intent.getBooleanExtra("gpsLogging", false)
        val temperatureLogging = intent.getBooleanExtra("temperatureLogging", false)
        val shockLogging = intent.getBooleanExtra("shockLogging", false)
        val gpsInterval = intent.getLongExtra("gpsInterval", 0L)
        val temperatureInterval = intent.getLongExtra("temperatureInterval", 0L)

        // Display the collected data for review
        val reviewTextView = findViewById<TextView>(R.id.reviewTextView)
        val gpsHours = gpsInterval / 60
        val gpsMinutes = gpsInterval % 60
        val temperatureHours = temperatureInterval / 60
        val temperatureMinutes = temperatureInterval % 60
        reviewTextView.text = """
            GPS Logging: $gpsLogging
            GPS Interval: $gpsHours hours, $gpsMinutes minutes
            Temperature Logging: $temperatureLogging
            Temperature Interval: $temperatureHours hours, $temperatureMinutes minutes
            Shock Logging: $shockLogging
        """.trimIndent()

        // "Confirm" Button to start logging
        val confirmButton = findViewById<Button>(R.id.confirm_button)
        confirmButton.setOnClickListener {
            val intent = Intent(this, LoggingActivity::class.java)
            intent.putExtra("gpsLogging", gpsLogging)
            intent.putExtra("temperatureLogging", temperatureLogging)
            intent.putExtra("shockLogging", shockLogging)
            intent.putExtra("gpsInterval", gpsInterval)
            intent.putExtra("temperatureInterval", temperatureInterval)
            startActivity(intent)
        }
    }
}