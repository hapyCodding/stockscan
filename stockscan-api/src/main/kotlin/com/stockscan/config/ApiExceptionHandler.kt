package com.stockscan.config

import com.stockscan.inventory.DuplicateBarcodeException
import com.stockscan.inventory.InsufficientStockException
import com.stockscan.inventory.ItemNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.Instant

data class ApiError(
    val status: Int,
    val message: String,
    val timestamp: Instant = Instant.now(),
)

@RestControllerAdvice
class ApiExceptionHandler {
    @ExceptionHandler(ItemNotFoundException::class)
    fun handleNotFound(e: ItemNotFoundException) = error(HttpStatus.NOT_FOUND, e.message)

    @ExceptionHandler(DuplicateBarcodeException::class)
    fun handleDuplicate(e: DuplicateBarcodeException) = error(HttpStatus.CONFLICT, e.message)

    @ExceptionHandler(InsufficientStockException::class)
    fun handleInsufficient(e: InsufficientStockException) = error(HttpStatus.BAD_REQUEST, e.message)

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(e: IllegalArgumentException) = error(HttpStatus.BAD_REQUEST, e.message)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(e: MethodArgumentNotValidException): ResponseEntity<ApiError> {
        val message =
            e.bindingResult.fieldErrors
                .joinToString(", ") { "${it.field}: ${it.defaultMessage}" }
        return error(HttpStatus.BAD_REQUEST, message)
    }

    private fun error(
        status: HttpStatus,
        message: String?,
    ): ResponseEntity<ApiError> = ResponseEntity.status(status).body(ApiError(status.value(), message ?: status.reasonPhrase))
}
