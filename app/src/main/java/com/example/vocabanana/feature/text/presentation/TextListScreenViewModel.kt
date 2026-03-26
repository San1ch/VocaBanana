package com.example.vocabanana.feature.text.presentation

import androidx.lifecycle.ViewModel
import com.example.vocabanana.feature.text.domain.TextRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TextListScreenViewModel @Inject constructor(
    private val textRepository: TextRepository
) : ViewModel() {

}