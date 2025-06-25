package com.test.interview.demo.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.test.interview.demo.data.BookingDataManager
import com.test.interview.demo.data.cache.BookingCache
import com.test.interview.demo.data.service.MockBookingService
import kotlinx.coroutines.launch

class BookingDemoActivity : ComponentActivity() {
    private val TAG = "【BookingDemoActivity】"
    private lateinit var bookingDataManager: BookingDataManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 初始化数据管理器
        bookingDataManager = BookingDataManager(
            bookingService = MockBookingService(this),
            bookingCache = BookingCache(this)
        )
        
        // 监听数据流，自动打印数据变化
        lifecycleScope.launch {
            bookingDataManager.bookingData.collect { result ->
                result?.onSuccess { response ->
                    Log.d(TAG, "Received booking data: ${response.booking}")
                }?.onFailure { error ->
                    Log.e(TAG, "Error fetching booking data", error)
                }
            }
        }
    }

    private fun fetchBookingData() {
        Log.d(TAG, "Do Fetch booking data")
        lifecycleScope.launch {
            bookingDataManager.getData().onSuccess { response ->
                Log.d(TAG, "Received booking data: ${response.booking}")
            }.onFailure { error ->
                Log.e(TAG, "Error fetching booking data", error)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        fetchBookingData()
    }

    override fun onDestroy() {
        super.onDestroy()
        bookingDataManager.clear()
        bookingDataManager.onDestroy()
    }
} 