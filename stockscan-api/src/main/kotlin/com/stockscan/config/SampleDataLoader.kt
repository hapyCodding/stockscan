package com.stockscan.config

import com.stockscan.inventory.InventoryService
import com.stockscan.inventory.StockItemRepository
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SampleDataLoader {
    @Bean
    fun seedSampleData(
        items: StockItemRepository,
        inventory: InventoryService,
    ) = ApplicationRunner {
        if (items.count() > 0) return@ApplicationRunner

        inventory.register("8801234567890", "생수 500ml", 48)
        inventory.register("8809876543210", "종이컵 200개입", 30)
        inventory.register("8801111222233", "A4 복사용지 1박스", 12)
        inventory.register("8802222333344", "물티슈 100매", 25)

        inventory.release("8801234567890", 6, "회의실 비치")
        inventory.receive("8801111222233", 8, "정기 입고")
    }
}
