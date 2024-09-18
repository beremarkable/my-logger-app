package com.example.full_logger_v1

import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TripsAdapter(
    private val cursor: Cursor,
    private val onTripClick: (Int) -> Unit
) : RecyclerView.Adapter<TripsAdapter.TripViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.trip_item, parent, false)
        return TripViewHolder(view)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        if (cursor.moveToPosition(position)) {
            val tripId = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val timestamp = cursor.getString(cursor.getColumnIndexOrThrow("timestamp"))
            val gpsLat = cursor.getDouble(cursor.getColumnIndexOrThrow("gps_lat"))
            val gpsLon = cursor.getDouble(cursor.getColumnIndexOrThrow("gps_lon"))
            val temperature = cursor.getDouble(cursor.getColumnIndexOrThrow("temperature"))
            val shockEvent = cursor.getDouble(cursor.getColumnIndexOrThrow("shock_event"))

            holder.bind(tripId, timestamp, gpsLat, gpsLon, temperature, shockEvent)
        }
    }

    override fun getItemCount(): Int = cursor.count

    inner class TripViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tripInfo: TextView = itemView.findViewById(R.id.trip_info)

        fun bind(tripId: Int, timestamp: String, gpsLat: Double, gpsLon: Double, temperature: Double, shockEvent: Double) {
            tripInfo.text = "Trip Time: $timestamp\nGPS: Lat=$gpsLat, Lon=$gpsLon\nTemperature: $temperature\nShock: $shockEvent"

            itemView.setOnClickListener {
                onTripClick(tripId)
            }
        }
    }
}