package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey
    val id: String,
    val jobId: String,
    val senderId: String,
    val senderName: String,
    val senderRole: UserRole,
    val messageText: String,
    val timestamp: Long = System.currentTimeMillis()
)
