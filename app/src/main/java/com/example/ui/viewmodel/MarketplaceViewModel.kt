package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.MistriShebaDatabase
import com.example.data.model.*
import com.example.data.repository.MarketplaceRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AppScreen {
    // Customer
    CUSTOMER_HOME,
    MISTRI_DETAIL,
    CUSTOMER_BOOKINGS,
    JOB_TRACKING,
    
    // Mistri
    MISTRI_DASHBOARD,
    MISTRI_EARNINGS,
    MISTRI_PROFILE,
    
    // Admin
    ADMIN_PORTAL,
    
    // Shared
    CHAT_SCREEN,
    NOTIFICATIONS
}

data class UiState(
    val language: AppLanguage = AppLanguage.BN,
    val activeRole: UserRole = UserRole.CUSTOMER,
    val currentScreen: AppScreen = AppScreen.CUSTOMER_HOME,
    val navigationStack: List<AppScreen> = listOf(AppScreen.CUSTOMER_HOME),
    
    // Current logged in user context based on role
    val currentUserId: String = "cust_1",
    val currentUserName: String = "Sidratul Muntaha",
    val currentUserPhone: String = "01711234567",
    
    // Mistri role active context
    val currentMistriId: String = "mistri_1",
    
    // Selection state
    val selectedCategoryId: String? = null,
    val searchQuery: String = "",
    val selectedArea: String = "All",
    val selectedMistri: User? = null,
    val selectedJobId: String? = null,
    
    // Dialog states
    val isBookingDialogOpen: Boolean = false,
    val isReviewDialogOpen: Boolean = false,
    val isAddPartsDialogOpen: Boolean = false,
    val isCallingDialogOpen: Boolean = false,
    val callingTargetName: String = "",
    val callingTargetPhone: String = "",
    
    // Temporary banner message or toast
    val toastMessage: String? = null
)

class MarketplaceViewModel(application: Application) : AndroidViewModel(application) {

    private val database = MistriShebaDatabase.getDatabase(application)
    val repository = MarketplaceRepository(database)

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    val categories: StateFlow<List<Category>> = repository.categories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMistris: StateFlow<List<User>> = repository.allMistris
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allJobs: StateFlow<List<Job>> = repository.allJobs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingKycMistris: StateFlow<List<User>> = repository.pendingKycMistris
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active customer jobs
    val customerJobs: StateFlow<List<Job>> = _uiState
        .flatMapLatest { state ->
            repository.getJobsForCustomer(state.currentUserId)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active mistri jobs
    val mistriJobs: StateFlow<List<Job>> = _uiState
        .flatMapLatest { state ->
            repository.getJobsForMistri(state.currentMistriId)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected job flow
    val currentJob: StateFlow<Job?> = _uiState
        .flatMapLatest { state ->
            state.selectedJobId?.let { repository.getJobFlow(it) } ?: flowOf(null)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Current mistri flow
    val currentMistriProfile: StateFlow<User?> = _uiState
        .flatMapLatest { state ->
            repository.getUserFlow(state.currentMistriId)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Reviews for selected mistri
    val mistriReviews: StateFlow<List<Review>> = _uiState
        .flatMapLatest { state ->
            state.selectedMistri?.let { repository.getReviewsForMistri(it.id) } ?: flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Chat messages for active job
    val chatMessages: StateFlow<List<ChatMessage>> = _uiState
        .flatMapLatest { state ->
            state.selectedJobId?.let { repository.getChatMessages(it) } ?: flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Notifications for current user
    val notifications: StateFlow<List<AppNotification>> = _uiState
        .flatMapLatest { state ->
            val userId = if (state.activeRole == UserRole.CUSTOMER) state.currentUserId else state.currentMistriId
            repository.getNotifications(userId)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleLanguage() {
        _uiState.update {
            it.copy(language = if (it.language == AppLanguage.BN) AppLanguage.EN else AppLanguage.BN)
        }
    }

    fun setLanguage(lang: AppLanguage) {
        _uiState.update { it.copy(language = lang) }
    }

    fun switchRole(role: UserRole) {
        _uiState.update { state ->
            val targetScreen = when (role) {
                UserRole.CUSTOMER -> AppScreen.CUSTOMER_HOME
                UserRole.MISTRI -> AppScreen.MISTRI_DASHBOARD
                UserRole.ADMIN -> AppScreen.ADMIN_PORTAL
            }
            state.copy(
                activeRole = role,
                currentScreen = targetScreen,
                navigationStack = listOf(targetScreen)
            )
        }
    }

    fun navigateTo(screen: AppScreen) {
        _uiState.update { state ->
            state.copy(
                currentScreen = screen,
                navigationStack = state.navigationStack + screen
            )
        }
    }

    fun navigateBack(): Boolean {
        var handled = false
        _uiState.update { state ->
            if (state.navigationStack.size > 1) {
                val newStack = state.navigationStack.dropLast(1)
                handled = true
                state.copy(
                    currentScreen = newStack.last(),
                    navigationStack = newStack
                )
            } else {
                state
            }
        }
        return handled
    }

    fun selectCategory(categoryId: String?) {
        _uiState.update {
            it.copy(selectedCategoryId = if (it.selectedCategoryId == categoryId) null else categoryId)
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setSelectedArea(area: String) {
        _uiState.update { it.copy(selectedArea = area) }
    }

    fun openMistriDetail(mistri: User) {
        _uiState.update {
            it.copy(
                selectedMistri = mistri,
                currentScreen = AppScreen.MISTRI_DETAIL,
                navigationStack = it.navigationStack + AppScreen.MISTRI_DETAIL
            )
        }
    }

    fun openJobTracking(jobId: String) {
        _uiState.update {
            it.copy(
                selectedJobId = jobId,
                currentScreen = AppScreen.JOB_TRACKING,
                navigationStack = it.navigationStack + AppScreen.JOB_TRACKING
            )
        }
    }

    fun openChat(jobId: String) {
        _uiState.update {
            it.copy(
                selectedJobId = jobId,
                currentScreen = AppScreen.CHAT_SCREEN,
                navigationStack = it.navigationStack + AppScreen.CHAT_SCREEN
            )
        }
    }

    fun showBookingDialog(show: Boolean) {
        _uiState.update { it.copy(isBookingDialogOpen = show) }
    }

    fun showReviewDialog(show: Boolean) {
        _uiState.update { it.copy(isReviewDialogOpen = show) }
    }

    fun showAddPartsDialog(show: Boolean) {
        _uiState.update { it.copy(isAddPartsDialogOpen = show) }
    }

    fun triggerCall(name: String, phone: String) {
        _uiState.update {
            it.copy(
                isCallingDialogOpen = true,
                callingTargetName = name,
                callingTargetPhone = phone
            )
        }
    }

    fun dismissCall() {
        _uiState.update { it.copy(isCallingDialogOpen = false) }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    // Actions
    fun requestService(
        serviceTitleEn: String,
        serviceTitleBn: String,
        description: String,
        address: String,
        area: String,
        urgency: JobUrgency,
        scheduledDate: String,
        scheduledTimeSlot: String
    ) {
        val mistri = _uiState.value.selectedMistri ?: return
        viewModelScope.launch {
            val job = repository.createServiceRequest(
                customerId = _uiState.value.currentUserId,
                customerName = _uiState.value.currentUserName,
                customerPhone = _uiState.value.currentUserPhone,
                customerAddress = address.ifBlank { "House 42, Road 9/A, Dhanmondi" },
                customerArea = area.ifBlank { "Dhanmondi, Dhaka" },
                mistri = mistri,
                serviceTitleEn = serviceTitleEn,
                serviceTitleBn = serviceTitleBn,
                description = description,
                urgency = urgency,
                scheduledDate = scheduledDate,
                scheduledTimeSlot = scheduledTimeSlot
            )
            _uiState.update {
                it.copy(
                    isBookingDialogOpen = false,
                    selectedJobId = job.id,
                    currentScreen = AppScreen.JOB_TRACKING,
                    navigationStack = it.navigationStack + AppScreen.JOB_TRACKING,
                    toastMessage = if (it.language == AppLanguage.BN) "সার্ভিস বুকিং সফল হয়েছে!" else "Service booked successfully!"
                )
            }
        }
    }

    fun updateJobStatus(jobId: String, status: JobStatus) {
        viewModelScope.launch {
            repository.updateJobStatus(jobId, status)
        }
    }

    fun addSparePartsCost(jobId: String, cost: Double) {
        viewModelScope.launch {
            repository.addSparePartsBill(jobId, cost)
            _uiState.update {
                it.copy(
                    isAddPartsDialogOpen = false,
                    toastMessage = if (it.language == AppLanguage.BN) "যন্ত্রাংশের খরচ যোগ করা হয়েছে" else "Parts cost added"
                )
            }
        }
    }

    fun confirmAndPay(jobId: String, method: PaymentMethod) {
        viewModelScope.launch {
            repository.confirmAndPayJob(jobId, method)
            _uiState.update {
                it.copy(
                    toastMessage = if (it.language == AppLanguage.BN) "পেমেন্ট সফলভাবে সম্পন্ন হয়েছে!" else "Payment completed successfully!"
                )
            }
        }
    }

    fun submitReview(jobId: String, mistriId: String, rating: Int, comment: String, tags: String) {
        viewModelScope.launch {
            repository.submitReview(
                jobId = jobId,
                customerId = _uiState.value.currentUserId,
                customerName = _uiState.value.currentUserName,
                mistriId = mistriId,
                rating = rating,
                comment = comment,
                tags = tags
            )
            _uiState.update {
                it.copy(
                    isReviewDialogOpen = false,
                    toastMessage = if (it.language == AppLanguage.BN) "রিভিউ প্রদানের জন্য ধন্যবাদ!" else "Thank you for your review!"
                )
            }
        }
    }

    fun sendChatMessage(text: String) {
        val jobId = _uiState.value.selectedJobId ?: return
        if (text.isBlank()) return
        val isCustomer = _uiState.value.activeRole == UserRole.CUSTOMER
        val senderId = if (isCustomer) _uiState.value.currentUserId else _uiState.value.currentMistriId
        val senderName = if (isCustomer) _uiState.value.currentUserName else "Rafiqul Islam"
        val role = if (isCustomer) UserRole.CUSTOMER else UserRole.MISTRI

        viewModelScope.launch {
            repository.sendMessage(jobId, senderId, senderName, role, text)
        }
    }

    fun setMistriOnline(isOnline: Boolean) {
        viewModelScope.launch {
            repository.setMistriOnline(_uiState.value.currentMistriId, isOnline)
        }
    }

    fun approveOrRejectKyc(userId: String, isApproved: Boolean) {
        viewModelScope.launch {
            repository.reviewKyc(userId, isApproved)
            _uiState.update {
                it.copy(
                    toastMessage = if (isApproved) "KYC Approved" else "KYC Rejected"
                )
            }
        }
    }
}
