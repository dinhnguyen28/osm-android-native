package com.example.landmarkremark.utils

import android.annotation.SuppressLint
import android.util.Log
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object MyDateTime {

    fun getCurrentTimeUTC(): String {
        val currTime = Instant.now()
        return currTime.toString()
    }

    @SuppressLint("SimpleDateFormat")
    fun convertUTCtoLocalTime(
        utcTime: String,
    ): String {
        try {

            val instant = Instant.parse(utcTime)

            val localDateTime =
                instant.atZone(ZoneId.systemDefault())

            val formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val formattedDateTime =
                localDateTime.format(formatter)

            return formattedDateTime

        } catch (e: Exception) {
            Log.d("time_error", e.stackTraceToString())
            return "0:0"
        }

    }
}