package com.test.interview.demo.data.cache

import android.content.Context
import com.google.gson.Gson
import com.test.interview.demo.data.model.BookingResponse
import java.io.File
import java.util.concurrent.TimeUnit

class BookingCache(private val context: Context) {
    private var cachedData: BookingResponse? = null
    private val cacheExpirationTime = TimeUnit.MINUTES.toMillis(5) // 5分钟过期时间
    private val cacheFileName = "booking_cache.json"

    fun saveData(data: BookingResponse) {
        cachedData = BookingResponse(
            booking = data.booking,
            timestamp = System.currentTimeMillis()
        )
        saveToDisk(cachedData!!)
    }

    fun getData(): BookingResponse? {
        // 优先返回内存缓存
        if (cachedData != null) return cachedData
        // 内存没有则尝试从磁盘恢复
        val diskCache = readFromDisk()
        cachedData = diskCache
        return diskCache
    }

    fun isExpired(): Boolean {
        val currentTime = System.currentTimeMillis()
        return getData()?.let {
            currentTime - it.timestamp >= cacheExpirationTime
        } ?: true
    }

    fun clear() {
        cachedData = null
        context.deleteFile(cacheFileName)
    }

    private fun saveToDisk(data: BookingResponse) {
        try {
            val json = Gson().toJson(data)
            context.openFileOutput(cacheFileName, Context.MODE_PRIVATE).use {
                it.write(json.toByteArray())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun readFromDisk(): BookingResponse? {
        return try {
            val file = File(context.filesDir, cacheFileName)
            if (!file.exists()) return null
            val json = file.readText()
            Gson().fromJson(json, BookingResponse::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}