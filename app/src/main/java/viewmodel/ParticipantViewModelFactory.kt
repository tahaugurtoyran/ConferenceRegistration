package com.example.conferenceregistration.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.conferenceregistration.data.ParticipantRepository

class ParticipantViewModelFactory(private val repository: ParticipantRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ParticipantViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ParticipantViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}