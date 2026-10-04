package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reviews")
data class Review(
    @PrimaryKey
    val id: String,
    val jobId: String,
    val customerId: String,
    val customerName: String,
    val mistriId: String,
    val rating: Int,
    val comment: String,
    val tags: String = "", // e.g. "On Time, Fair Price, Polite"
    val createdAt: Long = System.currentTimeMillis()
)
