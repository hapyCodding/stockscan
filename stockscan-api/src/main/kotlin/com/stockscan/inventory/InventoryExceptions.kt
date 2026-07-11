package com.stockscan.inventory

class ItemNotFoundException(barcode: String) :
    RuntimeException("품목을 찾을 수 없습니다: $barcode")

class DuplicateBarcodeException(barcode: String) :
    RuntimeException("이미 등록된 바코드입니다: $barcode")

class InsufficientStockException(barcode: String, current: Int, requested: Int) :
    RuntimeException("재고가 부족합니다: $barcode (현재 $current, 요청 $requested)")
