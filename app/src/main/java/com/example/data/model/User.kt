package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole {
    CUSTOMER, MISTRI, ADMIN
}

enum class KycStatus {
    NOT_SUBMITTED, PENDING, VERIFIED, REJECTED
}

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val id: String,
    val name: String,
    val phone: String,
    val email: String,
    val role: UserRole,
    val address: String,
    val area: String,
    val profileImageUrl: String = "",
    val rating: Double = 4.8,
    val reviewCount: Int = 0,
    val isVerified: Boolean = false,
    val kycStatus: KycStatus = KycStatus.NOT_SUBMITTED,
    val nidNumber: String = "",
    val tradeLicense: String = "",
    
    // Mistri specific fields
    val categoryId: String = "",
    val categoryTitleEn: String = "",
    val categoryTitleBn: String = "",
    val hourlyRate: Double = 300.0,
    val visitFee: Double = 150.0,
    val experienceYears: Int = 5,
    val completedJobs: Int = 0,
    val isOnline: Boolean = true,
    val skills: String = "", // comma-separated
    val bio: String = ""
)
