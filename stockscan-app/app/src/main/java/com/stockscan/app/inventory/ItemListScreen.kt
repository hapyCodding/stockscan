package com.stockscan.app.inventory

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stockscan.app.ServiceLocator
import com.stockscan.app.data.api.ItemDto
import com.stockscan.app.viewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemListScreen(
    onScan: () -> Unit,
    onItemClick: (String) -> Unit,
) {
    val viewModel: ItemListViewModel =
        viewModel(factory = viewModelFactory { ItemListViewModel(ServiceLocator.repository) })
    val state by viewModel.state.collectAsStateWithLifecycle()

    LifecycleResumeEffect(Unit) {
        viewModel.refresh()
        onPauseOrDispose { }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("재고 목록") }) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onScan,
                icon = { Icon(Icons.Default.QrCodeScanner, contentDescription = null) },
                text = { Text("스캔") },
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center,
        ) {
            when {
                state.loading && state.items.isEmpty() -> CircularProgressIndicator()
                state.error != null && state.items.isEmpty() -> Text(state.error!!)
                state.items.isEmpty() -> Text("등록된 품목이 없습니다")
                else ->
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(state.items, key = { it.barcode }) { item ->
                            ItemRow(item, onClick = { onItemClick(item.barcode) })
                            HorizontalDivider()
                        }
                    }
            }
        }
    }
}

@Composable
private fun ItemRow(
    item: ItemDto,
    onClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = item.barcode,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline,
            )
        }
        Text(
            text = "${item.quantity}",
            style = MaterialTheme.typography.headlineSmall,
        )
    }
}
