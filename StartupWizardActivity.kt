package com.example.full_logger_v1

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class StartupWizardActivity : AppCompatActivity() {

    private lateinit var gpsIntervalHours: Spinner
    private lateinit var gpsIntervalMinutes: Spinner
    private lateinit var temperatureIntervalHours: Spinner
    private lateinit var temperatureIntervalMinutes: Spinner
    private lateinit var lowerTemperature: EditText
    private lateinit var upperTemperature: EditText
    private var gpsLogging = false
    private var temperatureLogging = false
    private var shockLogging = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_startup_wizard)

        // GPS Section
        val gpsRadioGroup = findViewById<RadioGroup>(R.id.gps_radio_group)
        val gpsIntervalQuestion = findViewById<TextView>(R.id.gps_interval_question)
        gpsIntervalHours = findViewById(R.id.gps_interval_hours)
        gpsIntervalMinutes = findViewById(R.id.gps_interval_minutes)

        setupSpinner(gpsIntervalHours, 0, 24)
        setupSpinner(gpsIntervalMinutes, 0, 60)

        gpsRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            gpsLogging = checkedId == R.id.gps_yes
            if (gpsLogging) {
                gpsIntervalQuestion.visibility = View.VISIBLE
                gpsIntervalHours.visibility = View.VISIBLE
                gpsIntervalMinutes.visibility = View.VISIBLE
            } else {
                gpsIntervalQuestion.visibility = View.GONE
                gpsIntervalHours.visibility = View.GONE
                gpsIntervalMinutes.visibility = View.GONE
            }
        }

        // Temperature Section
        val temperatureRadioGroup = findViewById<RadioGroup>(R.id.temperature_radio_group)
        val temperatureIntervalQuestion = findViewById<TextView>(R.id.temperature_interval_question)
        temperatureIntervalHours = findViewById(R.id.temperature_interval_hours)
        temperatureIntervalMinutes = findViewById(R.id.temperature_interval_minutes)
        val temperatureBoundariesQuestion = findViewById<TextView>(R.id.temperature_boundaries_question)
        lowerTemperature = findViewById(R.id.lower_temperature)
        upperTemperature = findViewById(R.id.upper_temperature)

        setupSpinner(temperatureIntervalHours, 0, 24)
        setupSpinner(temperatureIntervalMinutes, 0, 60)

        temperatureRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            temperatureLogging = checkedId == R.id.temperature_yes
            if (temperatureLogging) {
                temperatureIntervalQuestion.visibility = View.VISIBLE
                temperatureIntervalHours.visibility = View.VISIBLE
                temperatureIntervalMinutes.visibility = View.VISIBLE
                temperatureBoundariesQuestion.visibility = View.VISIBLE
                lowerTemperature.visibility = View.VISIBLE
                upperTemperature.visibility = View.VISIBLE
            } else {
                temperatureIntervalQuestion.visibility = View.GONE
                temperatureIntervalHours.visibility = View.GONE
                temperatureIntervalMinutes.visibility = View.GONE
                temperatureBoundariesQuestion.visibility = View.GONE
                lowerTemperature.visibility = View.GONE
                upperTemperature.visibility = View.GONE
            }
        }

        // Shock Section
        val shockRadioGroup = findViewById<RadioGroup>(R.id.shock_radio_group)
        shockRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            shockLogging = checkedId == R.id.shock_yes
        }

        // "Click to Review" Button (only navigates to confirmation page)
        val confirmButton = findViewById<Button>(R.id.confirm_button)
        confirmButton.setOnClickListener {
            val intent = Intent(this, ConfirmationActivity::class.java)

            // Pass the full interval values (hours + minutes)
            val gpsInterval = getGPSIntervalFromUser()
            val temperatureInterval = getTemperatureIntervalFromUser()

            intent.putExtra("gpsLogging", gpsLogging)
            intent.putExtra("temperatureLogging", temperatureLogging)
            intent.putExtra("shockLogging", shockLogging)
            intent.putExtra("gpsInterval", gpsInterval)
            intent.putExtra("temperatureInterval", temperatureInterval)
            startActivity(intent)
        }
    }

    private fun setupSpinner(spinner: Spinner, start: Int, end: Int) {
        val items = (start..end).toList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun getGPSIntervalFromUser(): Long {
        val hours = gpsIntervalHours.selectedItem.toString().toLong()
        val minutes = gpsIntervalMinutes.selectedItem.toString().toLong()
        return (hours * 60) + minutes // Total in minutes
    }

    private fun getTemperatureIntervalFromUser(): Long {
        val hours = temperatureIntervalHours.selectedItem.toString().toLong()
        val minutes = temperatureIntervalMinutes.selectedItem.toString().toLong()
        return (hours * 60) + minutes // Total in minutes
    }
}