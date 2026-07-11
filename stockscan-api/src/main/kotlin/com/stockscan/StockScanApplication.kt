package com.stockscan

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class StockScanApplication

fun main(args: Array<String>) {
    runApplication<StockScanApplication>(*args)
}
