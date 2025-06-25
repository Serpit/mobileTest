package com.test.interview.demo.data.cache

import com.test.interview.demo.data.model.BookingResponse
import java.util.concurrent.TimeUnit

class BookingCache {
    private var cachedData: BookingResponse? = null
    private val cacheExpirationTime = TimeUnit.MINUTES.toMillis(5) // 5分钟过期时间

    fun saveData(data: BookingResponse) {
        cachedData = BookingResponse(
            booking = data.booking,
            timestamp = System.currentTimeMillis()
        )
    }

    fun getData(): BookingResponse? = cachedData

    fun isExpired(): Boolean {
        val currentTime = System.currentTimeMillis()
        return cachedData?.let {
            currentTime - it.timestamp >= cacheExpirationTime
        } ?: true
    }

    fun clear() {
        cachedData = null
    }
}