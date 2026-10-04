package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE role = 'MISTRI'")
    fun getAllMistris(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE role = 'MISTRI' AND categoryId = :categoryId")
    fun getMistrisByCategory(categoryId: String): Flow<List<User>>

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): User?

    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserFlow(userId: String): Flow<User?>

    @Query("SELECT * FROM users WHERE role = 'MISTRI' AND kycStatus = 'PENDING'")
    fun getPendingKycMistris(): Flow<List<User>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<User>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("UPDATE users SET isOnline = :isOnline WHERE id = :mistriId")
    suspend fun updateOnlineStatus(mistriId: String, isOnline: Boolean)

    @Query("UPDATE users SET kycStatus = :status, isVerified = :isVerified WHERE id = :userId")
    suspend fun updateKycStatus(userId: String, status: KycStatus, isVerified: Boolean)
}

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY displayOrder ASC")
    fun getAllCategories(): Flow<List<Category>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<Category>)
}

@Dao
interface JobDao {
    @Query("SELECT * FROM jobs ORDER BY createdAt DESC")
    fun getAllJobs(): Flow<List<Job>>

    @Query("SELECT * FROM jobs WHERE customerId = :customerId ORDER BY createdAt DESC")
    fun getJobsForCustomer(customerId: String): Flow<List<Job>>

    @Query("SELECT * FROM jobs WHERE mistriId = :mistriId ORDER BY createdAt DESC")
    fun getJobsForMistri(mistriId: String): Flow<List<Job>>

    @Query("SELECT * FROM jobs WHERE id = :jobId")
    suspend fun getJobById(jobId: String): Job?

    @Query("SELECT * FROM jobs WHERE id = :jobId")
    fun getJobFlow(jobId: String): Flow<Job?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJob(job: Job)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJobs(jobs: List<Job>)

    @Update
    suspend fun updateJob(job: Job)

    @Query("UPDATE jobs SET status = :status WHERE id = :jobId")
    suspend fun updateJobStatus(jobId: String, status: JobStatus)

    @Query("UPDATE jobs SET status = :status, completedAt = :completedAt WHERE id = :jobId")
    suspend fun markJobCompleted(jobId: String, status: JobStatus, completedAt: Long)

    @Query("UPDATE jobs SET partsCost = :partsCost, totalAmount = :totalAmount, platformFeeAmount = :platformFeeAmount WHERE id = :jobId")
    suspend fun updateJobCosts(jobId: String, partsCost: Double, platformFeeAmount: Double, totalAmount: Double)

    @Query("UPDATE jobs SET isPaid = :isPaid, paymentMethod = :paymentMethod WHERE id = :jobId")
    suspend fun updatePayment(jobId: String, isPaid: Boolean, paymentMethod: PaymentMethod)
}

@Dao
interface ReviewDao {
    @Query("SELECT * FROM reviews WHERE mistriId = :mistriId ORDER BY createdAt DESC")
    fun getReviewsForMistri(mistriId: String): Flow<List<Review>>

    @Query("SELECT * FROM reviews ORDER BY createdAt DESC")
    fun getAllReviews(): Flow<List<Review>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: Review)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReviews(reviews: List<Review>)
}

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_messages WHERE jobId = :jobId ORDER BY timestamp ASC")
    fun getMessagesForJob(jobId: String): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<ChatMessage>)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY timestamp DESC")
    fun getNotificationsForUser(userId: String): Flow<List<AppNotification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: AppNotification)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<AppNotification>)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: String)
}
