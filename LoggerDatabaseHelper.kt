package com.example.full_logger_v1

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*
import android.database.Cursor
import android.util.Log

class LoggerDatabaseHelper(private val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "trips.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "trip_data"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TIMESTAMP = "timestamp"
        private const val COLUMN_GPS_LAT = "gps_lat"
        private const val COLUMN_GPS_LON = "gps_lon"
        private const val COLUMN_TEMPERATURE = "temperature"
        private const val COLUMN_SHOCK_EVENT = "shock_event"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TIMESTAMP TEXT,
                $COLUMN_GPS_LAT REAL,
                $COLUMN_GPS_LON REAL,
                $COLUMN_TEMPERATURE REAL,
                $COLUMN_SHOCK_EVENT REAL
            )
        """
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // Method to insert GPS data into the database
    fun insertGPSData(gpsLat: Double, gpsLon: Double) {
        val db = this.writableDatabase
        val contentValues = ContentValues()

        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        contentValues.put(COLUMN_TIMESTAMP, timestamp)
        contentValues.put(COLUMN_GPS_LAT, gpsLat)
        contentValues.put(COLUMN_GPS_LON, gpsLon)

        db.insert(TABLE_NAME, null, contentValues)
        db.close()
    }

    // Method to insert temperature data into the database
    fun insertTemperatureData(temperature: Double) {
        val db = this.writableDatabase
        val contentValues = ContentValues()

        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        contentValues.put(COLUMN_TIMESTAMP, timestamp)
        contentValues.put(COLUMN_TEMPERATURE, temperature)

        db.insert(TABLE_NAME, null, contentValues)
        db.close()
    }

    // Method to insert shock event data into the database
    fun insertShockData(shockEvent: Double) {
        val db = this.writableDatabase
        val contentValues = ContentValues()

        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        contentValues.put(COLUMN_TIMESTAMP, timestamp)
        contentValues.put(COLUMN_SHOCK_EVENT, shockEvent)

        db.insert(TABLE_NAME, null, contentValues)
        db.close()
    }

    // Method to fetch all trips
    fun getAllTrips(): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME ORDER BY id DESC", null)
    }

    // Method to export data to CSV
    fun exportDataToCSV(): String {
        return try {
            val csvFile = File(context.getExternalFilesDir(null), "trip_data.csv")
            val fileWriter = FileWriter(csvFile)
            val cursor = getAllTrips()

            if (cursor.moveToFirst()) {
                // Write column headers
                fileWriter.append("ID,Timestamp,GPS_Lat,GPS_Lon,Temperature,ShockEvent\n")

                do {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                    val timestamp = cursor.getString(cursor.getColumnIndexOrThrow("timestamp"))
                    val gpsLat = cursor.getDouble(cursor.getColumnIndexOrThrow("gps_lat"))
                    val gpsLon = cursor.getDouble(cursor.getColumnIndexOrThrow("gps_lon"))
                    val temperature = cursor.getDouble(cursor.getColumnIndexOrThrow("temperature"))
                    val shockEvent = cursor.getDouble(cursor.getColumnIndexOrThrow("shock_event"))

                    // Write data row
                    fileWriter.append("$id,$timestamp,$gpsLat,$gpsLon,$temperature,$shockEvent\n")
                } while (cursor.moveToNext())
            }

            fileWriter.flush()
            fileWriter.close()
            cursor.close()

            // Return the path to the CSV file
            csvFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            "Error: ${e.message}"
        }
    }
}