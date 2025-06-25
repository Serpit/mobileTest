package com.test.interview.demo.data

import android.util.Log
import com.test.interview.demo.data.cache.BookingCache
import com.test.interview.demo.data.model.BookingResponse
import com.test.interview.demo.data.service.BookingService
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BookingDataManager(
    private val bookingService: BookingService,
    private val bookingCache: BookingCache
) {
    private val TAG = "【BookingDataManager】"
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val _bookingData = MutableStateFlow<Result<BookingResponse>?>(null)
    val bookingData: StateFlow<Result<BookingResponse>?> = _bookingData.asStateFlow()

    // 首次拉取标记
    private var firstFetchDone = false

    init {
        // 启动自动刷新机制
        startAutoRefresh()
    }

    private fun startAutoRefresh() {
        coroutineScope.launch {
            while (isActive) {
                if (firstFetchDone && bookingCache.isExpired()) {
                    refreshData()
                }
                delay(30000) // 每30秒检查一次是否需要刷新
            }
        }
    }

    suspend fun getData(): Result<BookingResponse> {
        // 1. 首先返回缓存数据（如果有的话）
        bookingCache.getData()?.let {
            Log.d(TAG, "Returning cached data")
            return Result.success(it)
        }

        // 2. 如果没有缓存数据，从服务器获取
        val result = refreshData()
        firstFetchDone = true
        return result
    }

    private suspend fun refreshData(): Result<BookingResponse> {
        Log.i(TAG, "Refreshing data")
        return try {
            val result = bookingService.getBookings()
            result.onSuccess { response ->
                bookingCache.saveData(response)
                _bookingData.value = Result.success(response)
                Log.i(TAG, "Success Refreshing data")
            }.onFailure { error ->
                Log.e(TAG, "Error refreshing data", error)
                _bookingData.value = Result.failure(error)
            }
            result
        } catch (e: Exception) {
            Log.e(TAG, "Error refreshing data", e)
            Result.failure(e)
        }
    }

    fun clear() {
        bookingCache.clear()
        _bookingData.value = null
    }

    fun onDestroy() {
        coroutineScope.cancel()
    }
} 