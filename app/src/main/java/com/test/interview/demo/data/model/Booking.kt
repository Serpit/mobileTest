package com.test.interview.demo.data.model

import com.google.gson.annotations.SerializedName

// 地点信息
data class Place(
    @SerializedName("code")
    val code: String,
    @SerializedName("displayName")
    val displayName: String,
    @SerializedName("url")
    val url: String
)

// 起止点信息

data class OriginAndDestinationPair(
    @SerializedName("destination")
    val destination: Place,
    @SerializedName("destinationCity")
    val destinationCity: String,
    @SerializedName("origin")
    val origin: Place,
    @SerializedName("originCity")
    val originCity: String
)

// 航段

data class Segment(
    @SerializedName("id")
    val id: Int,
    @SerializedName("originAndDestinationPair")
    val originAndDestinationPair: OriginAndDestinationPair
)

// 预订主信息

data class Booking(
    @SerializedName("shipReference")
    val shipReference: String,
    @SerializedName("shipToken")
    val shipToken: String,
    @SerializedName("canIssueTicketChecking")
    val canIssueTicketChecking: Boolean,
    @SerializedName("expiryTime")
    val expiryTime: String,
    @SerializedName("duration")
    val duration: Int,
    @SerializedName("segments")
    val segments: List<Segment>
)

// 缓存用响应体

data class BookingResponse(
    @SerializedName("booking")
    val booking: Booking,
    @SerializedName("timestamp")
    val timestamp: Long = System.currentTimeMillis()
) 