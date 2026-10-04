package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class JobStatus {
    REQUESTED,
    ACCEPTED,
    ON_THE_WAY,
    IN_PROGRESS,
    COMPLETED,
    CONFIRMED,
    REVIEWED,
    CANCELLED
}

enum class JobUrgency {
    NORMAL, EMERGENCY
}

enum class PaymentMethod {
    PENDING, BKASH, NAGAD, CASH
}

@Entity(tableName = "jobs")
data class Job(
    @PrimaryKey
    val id: String, // e.g. "MS-84912"
    val customerId: String,
    val customerName: String,
    val customerPhone: String,
    val customerAddress: String,
    val customerArea: String,
    val mistriId: String,
    val mistriName: String,
    val mistriPhone: String,
    val categoryId: String,
    val serviceTitleEn: String,
    val serviceTitleBn: String,
    val description: String,
    val urgency: JobUrgency = JobUrgency.NORMAL,
    val scheduledDate: String,
    val scheduledTimeSlot: String,
    val status: JobStatus = JobStatus.REQUESTED,
    val startOtp: String,
    val finishOtp: String,
    val visitFee: Double = 150.0,
    val laborCost: Double = 350.0,
    val partsCost: Double = 0.0,
    val platformFeePercent: Double = 8.0, // 8% commission
    val platformFeeAmount: Double = 40.0,
    val totalAmount: Double = 540.0,
    val paymentMethod: PaymentMethod = PaymentMethod.PENDING,
    val isPaid: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val cancellationReason: String? = null
)
