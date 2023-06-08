package com.muzo.mysportapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.muzo.mysportapp.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    val mainRepository: MainRepository
):ViewModel() {
}