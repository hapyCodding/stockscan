package com.stockscan.app.data

import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

fun Throwable.toUserMessage(): String =
    when (this) {
        is HttpException -> serverMessage() ?: "서버 오류가 발생했습니다 (${code()})"
        is IOException -> "서버에 연결할 수 없습니다"
        else -> message ?: "알 수 없는 오류가 발생했습니다"
    }

private fun HttpException.serverMessage(): String? =
    runCatching {
        val raw = response()?.errorBody()?.string().orEmpty()
        JSONObject(raw).optString("message").ifBlank { null }
    }.getOrNull()
