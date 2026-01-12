package com.example.conferenceregistration.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.conferenceregistration.data.Participant
import com.example.conferenceregistration.data.ParticipantRepository
import kotlinx.coroutines.launch

class ParticipantViewModel(private val repository: ParticipantRepository) : ViewModel() {


    private val _searchResult = MutableLiveData<Participant?>()
    val searchResult: LiveData<Participant?> get() = _searchResult


    fun registerParticipant(participant: Participant) {

        viewModelScope.launch {
            repository.insert(participant)
        }
    }


    fun verifyParticipant(id: Int) {
        viewModelScope.launch {

            val user = repository.getParticipantById(id)

            _searchResult.value = user
        }
    }
}