package com.stockscan.app.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stockscan.app.data.InventoryRepository
import com.stockscan.app.data.api.ItemDto
import com.stockscan.app.data.toUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface DetailState {
    data object Loading : DetailState

    data class Registered(val item: ItemDto) : DetailState

    data class Unregistered(val barcode: String) : DetailState

    data class Failed(val message: String) : DetailState
}

class ItemDetailViewModel(
    private val barcode: String,
    private val repository: InventoryRepository,
) : ViewModel() {
    private val _state = MutableStateFlow<DetailState>(DetailState.Loading)
    val state: StateFlow<DetailState> = _state.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = DetailState.Loading
            runCatching { repository.find(barcode) }
                .onSuccess { item ->
                    _state.value =
                        if (item == null) DetailState.Unregistered(barcode) else DetailState.Registered(item)
                }
                .onFailure { _state.value = DetailState.Failed(it.toUserMessage()) }
        }
    }

    fun register(
        name: String,
        quantity: Int,
    ) = mutate("등록했습니다") { repository.register(barcode, name, quantity) }

    fun receive(quantity: Int) = mutate("입고 완료") { repository.receive(barcode, quantity) }

    fun release(quantity: Int) = mutate("출고 완료") { repository.release(barcode, quantity) }

    fun consumeMessage() {
        _message.value = null
    }

    private fun mutate(
        success: String,
        block: suspend () -> ItemDto,
    ) {
        viewModelScope.launch {
            runCatching { block() }
                .onSuccess {
                    _state.value = DetailState.Registered(it)
                    _message.value = success
                }
                .onFailure { _message.value = it.toUserMessage() }
        }
    }
}
