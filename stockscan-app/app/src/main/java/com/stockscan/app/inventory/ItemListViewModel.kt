package com.stockscan.app.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stockscan.app.data.InventoryRepository
import com.stockscan.app.data.api.ItemDto
import com.stockscan.app.data.toUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ItemListState(
    val loading: Boolean = false,
    val items: List<ItemDto> = emptyList(),
    val error: String? = null,
)

class ItemListViewModel(
    private val repository: InventoryRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(ItemListState())
    val state: StateFlow<ItemListState> = _state.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            runCatching { repository.items() }
                .onSuccess { items -> _state.update { it.copy(loading = false, items = items) } }
                .onFailure { e -> _state.update { it.copy(loading = false, error = e.toUserMessage()) } }
        }
    }
}
