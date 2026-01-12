package com.example.conferenceregistration.data

class ParticipantRepository(private val participantDao: ParticipantDao) {


    suspend fun insert(participant: Participant) {
        participantDao.insert(participant)
    }


    suspend fun getParticipantById(id: Int): Participant? {
        return participantDao.getParticipant(id)
    }
}