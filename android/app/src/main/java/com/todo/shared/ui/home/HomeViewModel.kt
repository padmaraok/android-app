package com.todo.shared.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todo.shared.data.model.CreateListResponse
import com.todo.shared.data.repository.CreateListRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class HomeUiState {
    object Idle : HomeUiState()
    object Loading : HomeUiState()
    data class Success(val shortId: String, val writeKey: String) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

class HomeViewModel(
    private val createListRepository: CreateListRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Idle)
    val uiState: StateFlow<HomeUiState> = _uiState

    fun createList() {
        if (_uiState.value is HomeUiState.Loading) return

        _uiState.value = HomeUiState.Loading

        viewModelScope.launch {
            createListRepository.createList()
                .onSuccess { response ->
                    _uiState.value = HomeUiState.Success(response.shortId, response.writeKey)
                }
                .onFailure { error ->
                    _uiState.value = HomeUiState.Error(error.localizedMessage ?: "Unknown error")
                }
        }
    }

    fun resetState() {
        _uiState.value = HomeUiState.Idle
    }
}
