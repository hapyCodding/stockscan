package com.stockscan.app.inventory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stockscan.app.ServiceLocator
import com.stockscan.app.data.api.ItemDto
import com.stockscan.app.viewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailScreen(
    barcode: String,
    onBack: () -> Unit,
) {
    val viewModel: ItemDetailViewModel =
        viewModel(factory = viewModelFactory { ItemDetailViewModel(barcode, ServiceLocator.repository) })
    val state by viewModel.state.collectAsStateWithLifecycle()
    val message by viewModel.message.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.consumeMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("품목 상세") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로")
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center,
        ) {
            when (val current = state) {
                is DetailState.Loading -> CircularProgressIndicator()
                is DetailState.Failed ->
                    RetryMessage(current.message, onRetry = viewModel::load)
                is DetailState.Unregistered ->
                    RegisterForm(
                        barcode = current.barcode,
                        onRegister = viewModel::register,
                    )
                is DetailState.Registered ->
                    RegisteredItem(
                        item = current.item,
                        onReceive = viewModel::receive,
                        onRelease = viewModel::release,
                    )
            }
        }
    }
}

@Composable
private fun RetryMessage(
    message: String,
    onRetry: () -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(message)
        Spacer(Modifier.height(12.dp))
        OutlinedButton(onClick = onRetry) { Text("다시 시도") }
    }
}

@Composable
private fun RegisteredItem(
    item: ItemDto,
    onReceive: (Int) -> Unit,
    onRelease: (Int) -> Unit,
) {
    var amount by rememberSaveable { mutableStateOf("1") }

    Column(
        modifier = Modifier.fillMaxWidth().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(item.name, style = MaterialTheme.typography.headlineSmall)
        Text(item.barcode, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
        Spacer(Modifier.height(24.dp))
        Text("현재 재고", style = MaterialTheme.typography.labelLarge)
        Text("${item.quantity}", style = MaterialTheme.typography.displayMedium)
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = amount,
            onValueChange = { input -> amount = input.filter { it.isDigit() }.take(5) },
            label = { Text("수량") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = { amount.toIntOrNull()?.takeIf { it > 0 }?.let(onRelease) },
                modifier = Modifier.weight(1f),
            ) {
                Text("출고 −")
            }
            Button(
                onClick = { amount.toIntOrNull()?.takeIf { it > 0 }?.let(onReceive) },
                modifier = Modifier.weight(1f),
            ) {
                Text("입고 +")
            }
        }
    }
}

@Composable
private fun RegisterForm(
    barcode: String,
    onRegister: (String, Int) -> Unit,
) {
    var name by rememberSaveable { mutableStateOf("") }
    var quantity by rememberSaveable { mutableStateOf("0") }

    Column(
        modifier = Modifier.fillMaxWidth().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("신규 품목", style = MaterialTheme.typography.headlineSmall)
        Text(barcode, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("품목명") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = quantity,
            onValueChange = { input -> quantity = input.filter { it.isDigit() }.take(5) },
            label = { Text("초기 수량") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = { onRegister(name.trim(), quantity.toIntOrNull() ?: 0) },
            enabled = name.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("등록")
        }
    }
}
