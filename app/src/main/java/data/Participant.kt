package com.example.conferenceregistration.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "participants")
data class Participant(

    @PrimaryKey(autoGenerate = false)
    val userId: Int,

    val fullName: String,
    val title: String,
    val registrationType: Int,
    val photoPath: String
)