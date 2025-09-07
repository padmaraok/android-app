package com.todo.shared.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.todo.shared.data.model.TodoItem
import com.todo.shared.data.repository.ItemsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TodoListViewModel(
    private val itemsRepository: ItemsRepository
) : ViewModel() {

    private val _items = MutableStateFlow<List<TodoItem>>(emptyList())
    val items: StateFlow<List<TodoItem>> = _items

    fun loadList(shortId: String) {
        viewModelScope.launch {
            itemsRepository.observeItems(shortId)
                .collect { items ->
                    _items.value = items.filter { it.text.isNotBlank() }
                }
        }
    }

    suspend fun toggleItem(shortId: String, itemId: String, done: Boolean, writeKey: String) {
        itemsRepository.updateItem(shortId, itemId, "", done, writeKey)
    }

    suspend fun deleteItem(shortId: String, itemId: String, writeKey: String) {
        itemsRepository.deleteItem(shortId, itemId, writeKey)
    }
}
