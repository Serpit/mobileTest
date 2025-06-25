package com.test.interview.demo.data.service

import android.content.Context
import com.google.gson.Gson
import com.test.interview.demo.data.model.Booking
import com.test.interview.demo.data.model.BookingResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

interface BookingService {
    suspend fun getBookings(): Result<BookingResponse>
}

class MockBookingService(private val context: Context) : BookingService {


    override suspend fun getBookings(): Result<BookingResponse> = withContext(Dispatchers.IO) {
        try {
            // 模拟网络延迟
            delay(1000)
            val json = context.assets.open("booking.json").use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).readText()
            }
            val booking = Gson().fromJson(json, Booking::class.java)
            val response = BookingResponse(booking = booking)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 