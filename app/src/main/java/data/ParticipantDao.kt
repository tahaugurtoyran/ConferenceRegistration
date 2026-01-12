package com.example.conferenceregistration.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ParticipantDao {


    @Insert
    suspend fun insert(participant: Participant)


    @Query("SELECT * FROM participants WHERE userId = :id")
    suspend fun getParticipant(id: Int): Participant?
}