package com.example.landmarkremark.models

data class Notes(
    var id: String = "",
    val userName: String = "",
    val dateTime: String = "",
    val title: String = "",
    val description: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
)
