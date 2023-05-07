package com.jinvita.mykakaomap.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jinvita.mykakaomap.model.repository.MapRepository

class MapViewModelFactory(private val repository: MapRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(MapRepository::class.java).newInstance(repository)
    }
}