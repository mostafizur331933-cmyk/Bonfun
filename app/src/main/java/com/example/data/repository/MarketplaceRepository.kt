package com.example.data.repository

import com.example.data.local.*
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.random.Random

class MarketplaceRepository(private val database: MistriShebaDatabase) {

    private val userDao = database.userDao()
    private val categoryDao = database.categoryDao()
    private val jobDao = database.jobDao()
    private val reviewDao = database.reviewDao()
    private val chatDao = database.chatDao()
    private val notificationDao = database.notificationDao()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedInitialDataIfNeeded()
        }
    }

    val categories: Flow<List<Category>> = categoryDao.getAllCategories()
    val allMistris: Flow<List<User>> = userDao.getAllMistris()
    val allJobs: Flow<List<Job>> = jobDao.getAllJobs()
    val pendingKycMistris: Flow<List<User>> = userDao.getPendingKycMistris()

    fun getJobsForCustomer(customerId: String): Flow<List<Job>> = jobDao.getJobsForCustomer(customerId)
    fun getJobsForMistri(mistriId: String): Flow<List<Job>> = jobDao.getJobsForMistri(mistriId)
    fun getJobFlow(jobId: String): Flow<Job?> = jobDao.getJobFlow(jobId)
    fun getReviewsForMistri(mistriId: String): Flow<List<Review>> = reviewDao.getReviewsForMistri(mistriId)
    fun getChatMessages(jobId: String): Flow<List<ChatMessage>> = chatDao.getMessagesForJob(jobId)
    fun getNotifications(userId: String): Flow<List<AppNotification>> = notificationDao.getNotificationsForUser(userId)
    fun getUserFlow(userId: String): Flow<User?> = userDao.getUserFlow(userId)

    suspend fun getMistriById(id: String): User? = userDao.getUserById(id)
    suspend fun getJobById(jobId: String): Job? = jobDao.getJobById(jobId)

    suspend fun setMistriOnline(mistriId: String, isOnline: Boolean) {
        userDao.updateOnlineStatus(mistriId, isOnline)
    }

    suspend fun createServiceRequest(
        customerId: String,
        customerName: String,
        customerPhone: String,
        customerAddress: String,
        customerArea: String,
        mistri: User,
        serviceTitleEn: String,
        serviceTitleBn: String,
        description: String,
        urgency: JobUrgency,
        scheduledDate: String,
        scheduledTimeSlot: String
    ): Job {
        val randomSuffix = Random.nextInt(10000, 99999).toString()
        val jobId = "MS-$randomSuffix"
        val startOtp = Random.nextInt(1000, 9999).toString()
        val finishOtp = Random.nextInt(1000, 9999).toString()

        val visitFee = mistri.visitFee
        val laborCost = mistri.hourlyRate
        val platformFee = (visitFee + laborCost) * 0.08
        val total = visitFee + laborCost + platformFee

        val newJob = Job(
            id = jobId,
            customerId = customerId,
            customerName = customerName,
            customerPhone = customerPhone,
            customerAddress = customerAddress,
            customerArea = customerArea,
            mistriId = mistri.id,
            mistriName = mistri.name,
            mistriPhone = mistri.phone,
            categoryId = mistri.categoryId,
            serviceTitleEn = serviceTitleEn,
            serviceTitleBn = serviceTitleBn,
            description = description,
            urgency = urgency,
            scheduledDate = scheduledDate,
            scheduledTimeSlot = scheduledTimeSlot,
            status = JobStatus.REQUESTED,
            startOtp = startOtp,
            finishOtp = finishOtp,
            visitFee = visitFee,
            laborCost = laborCost,
            partsCost = 0.0,
            platformFeePercent = 8.0,
            platformFeeAmount = platformFee,
            totalAmount = total
        )

        jobDao.insertJob(newJob)

        // Add notification for mistri
        notificationDao.insertNotification(
            AppNotification(
                id = "NOTIF-${System.currentTimeMillis()}-1",
                userId = mistri.id,
                titleEn = "New Job Request! ($jobId)",
                titleBn = "নতুন কাজের অনুরোধ! ($jobId)",
                bodyEn = "$customerName requested $serviceTitleEn in $customerArea",
                bodyBn = "$customerName $customerArea তে $serviceTitleBn চেয়েছেন",
                relatedJobId = jobId
            )
        )

        // Seed initial greeting message in chat
        chatDao.insertMessage(
            ChatMessage(
                id = "MSG-${System.currentTimeMillis()}",
                jobId = jobId,
                senderId = customerId,
                senderName = customerName,
                senderRole = UserRole.CUSTOMER,
                messageText = "Hello! I booked your service. Please accept my request."
            )
        )

        return newJob
    }

    suspend fun updateJobStatus(jobId: String, status: JobStatus) {
        val job = jobDao.getJobById(jobId) ?: return
        if (status == JobStatus.COMPLETED) {
            jobDao.markJobCompleted(jobId, status, System.currentTimeMillis())
        } else {
            jobDao.updateJobStatus(jobId, status)
        }

        // Send notifications based on status
        val titleEn: String
        val titleBn: String
        val bodyEn: String
        val bodyBn: String
        val targetUserId: String

        when (status) {
            JobStatus.ACCEPTED -> {
                titleEn = "Job Accepted! ($jobId)"
                titleBn = "কাজের অনুরোধ গৃহীত হয়েছে! ($jobId)"
                bodyEn = "${job.mistriName} has accepted your request."
                bodyBn = "${job.mistriName} আপনার অনুরোধ গ্রহণ করেছেন।"
                targetUserId = job.customerId
            }
            JobStatus.ON_THE_WAY -> {
                titleEn = "Mistri is On the Way ($jobId)"
                titleBn = "মিস্ত্রি রওনা হয়েছেন ($jobId)"
                bodyEn = "${job.mistriName} is heading to your location."
                bodyBn = "${job.mistriName} আপনার ঠিকানার দিকে রওনা হয়েছেন।"
                targetUserId = job.customerId
            }
            JobStatus.IN_PROGRESS -> {
                titleEn = "Job Started ($jobId)"
                titleBn = "কাজ শুরু হয়েছে ($jobId)"
                bodyEn = "Work in progress. Verify start OTP."
                bodyBn = "কাজ চলছে। কাজের স্টার্ট কোড ভেরিফাইড।"
                targetUserId = job.customerId
            }
            JobStatus.COMPLETED -> {
                titleEn = "Job Finished! ($jobId)"
                titleBn = "কাজ সম্পন্ন হয়েছে! ($jobId)"
                bodyEn = "Mistri has finished the work. Please confirm and review."
                bodyBn = "মিস্ত্রি কাজ শেষ করেছেন। দয়া করে নিশ্চিত করুন এবং রেটিং দিন।"
                targetUserId = job.customerId
            }
            JobStatus.CONFIRMED -> {
                titleEn = "Payment Confirmed! ($jobId)"
                titleBn = "পেমেন্ট নিশ্চিত হয়েছে! ($jobId)"
                bodyEn = "Customer confirmed completion and paid."
                bodyBn = "গ্রাহক কাজ নিশ্চিত করে পেমেন্ট পরিশোধ করেছেন।"
                targetUserId = job.mistriId
            }
            JobStatus.CANCELLED -> {
                titleEn = "Job Cancelled ($jobId)"
                titleBn = "কাজ বাতিল করা হয়েছে ($jobId)"
                bodyEn = "The job request has been cancelled."
                bodyBn = "কাজের অনুরোধটি বাতিল করা হয়েছে।"
                targetUserId = job.mistriId
            }
            else -> return
        }

        notificationDao.insertNotification(
            AppNotification(
                id = "NOTIF-${System.currentTimeMillis()}",
                userId = targetUserId,
                titleEn = titleEn,
                titleBn = titleBn,
                bodyEn = bodyEn,
                bodyBn = bodyBn,
                relatedJobId = jobId
            )
        )
    }

    suspend fun addSparePartsBill(jobId: String, partsCost: Double) {
        val job = jobDao.getJobById(jobId) ?: return
        val newPartsCost = job.partsCost + partsCost
        val subtotal = job.visitFee + job.laborCost + newPartsCost
        val platformFee = subtotal * (job.platformFeePercent / 100.0)
        val total = subtotal + platformFee
        jobDao.updateJobCosts(jobId, newPartsCost, platformFee, total)
    }

    suspend fun confirmAndPayJob(jobId: String, method: PaymentMethod) {
        jobDao.updatePayment(jobId, isPaid = true, paymentMethod = method)
        jobDao.updateJobStatus(jobId, JobStatus.CONFIRMED)
    }

    suspend fun submitReview(
        jobId: String,
        customerId: String,
        customerName: String,
        mistriId: String,
        rating: Int,
        comment: String,
        tags: String
    ) {
        val review = Review(
            id = "REV-${System.currentTimeMillis()}",
            jobId = jobId,
            customerId = customerId,
            customerName = customerName,
            mistriId = mistriId,
            rating = rating,
            comment = comment,
            tags = tags
        )
        reviewDao.insertReview(review)
        jobDao.updateJobStatus(jobId, JobStatus.REVIEWED)

        // Update mistri rating
        val mistri = userDao.getUserById(mistriId)
        if (mistri != null) {
            val updatedCount = mistri.reviewCount + 1
            val updatedRating = ((mistri.rating * mistri.reviewCount) + rating) / updatedCount
            val updatedJobs = mistri.completedJobs + 1
            userDao.updateUser(
                mistri.copy(
                    rating = (updatedRating * 10).toInt() / 10.0,
                    reviewCount = updatedCount,
                    completedJobs = updatedJobs
                )
            )
        }
    }

    suspend fun sendMessage(jobId: String, senderId: String, senderName: String, senderRole: UserRole, text: String) {
        val msg = ChatMessage(
            id = "MSG-${System.currentTimeMillis()}-${Random.nextInt(100, 999)}",
            jobId = jobId,
            senderId = senderId,
            senderName = senderName,
            senderRole = senderRole,
            messageText = text
        )
        chatDao.insertMessage(msg)
    }

    suspend fun reviewKyc(userId: String, isApproved: Boolean) {
        userDao.updateKycStatus(
            userId = userId,
            status = if (isApproved) KycStatus.VERIFIED else KycStatus.REJECTED,
            isVerified = isApproved
        )
    }

    private suspend fun seedInitialDataIfNeeded() {
        val existingCats = categoryDao.getAllCategories().first()
        if (existingCats.isNotEmpty()) return

        // 1. Seed Categories
        val seedCategories = listOf(
            Category(
                id = "cat_ac",
                titleEn = "AC Technician",
                titleBn = "এসি টেকনিশিয়ান",
                descriptionEn = "Installation, gas refill, servicing & repair",
                descriptionBn = "এসি ইন্সটলেশন, গ্যাস রিফিল, সার্ভিসিং ও মেরামত",
                iconName = "ac_unit",
                baseRate = 600.0,
                isPopular = true,
                displayOrder = 1
            ),
            Category(
                id = "cat_electrician",
                titleEn = "Electrician",
                titleBn = "ইলেকট্রিশিয়ান",
                descriptionEn = "Wiring, switch board, fan, circuit breaker",
                descriptionBn = "ওয়্যারিং, সুইচ বোর্ড, ফ্যান মেরামত, সার্কিট ব্রেকার",
                iconName = "bolt",
                baseRate = 350.0,
                isPopular = true,
                displayOrder = 2
            ),
            Category(
                id = "cat_plumber",
                titleEn = "Plumber",
                titleBn = "প্লাম্বার ও স্যানিটারি",
                descriptionEn = "Pipe leaks, water pump, commode, taps",
                descriptionBn = "পাইপ লিকেজ, মোটর পাম্প, কমোড, পানির কল",
                iconName = "plumbing",
                baseRate = 400.0,
                isPopular = true,
                displayOrder = 3
            ),
            Category(
                id = "cat_refrigerator",
                titleEn = "Refrigerator Tech",
                titleBn = "ফ্রিজ টেকনিশিয়ান",
                descriptionEn = "Cooling problem, compressor, thermostat",
                descriptionBn = "ঠান্ডা না হওয়া, গ্যাস চার্জ, কম্প্রেসার মেরামত",
                iconName = "kitchen",
                baseRate = 500.0,
                isPopular = true,
                displayOrder = 4
            ),
            Category(
                id = "cat_carpenter",
                titleEn = "Carpenter",
                titleBn = "কাঠমিস্ত্রি",
                descriptionEn = "Door locks, furniture repair, hinges, cabinet",
                descriptionBn = "দরজার লক, ফার্নিচার মেরামত, কব্জা, আলমারি",
                iconName = "handyman",
                baseRate = 450.0,
                isPopular = true,
                displayOrder = 5
            ),
            Category(
                id = "cat_painter",
                titleEn = "Painter",
                titleBn = "রং মিস্ত্রি",
                descriptionEn = "Interior/exterior wall painting, putty, polish",
                descriptionBn = "ওয়াল পুটিং, ডিস্টেম্পার, ওয়েদার কোট, বার্নিশ",
                iconName = "format_paint",
                baseRate = 400.0,
                isPopular = false,
                displayOrder = 6
            ),
            Category(
                id = "cat_appliance",
                titleEn = "Appliance Repair",
                titleBn = "হোম অ্যাপ্লায়েন্স",
                descriptionEn = "Washing machine, microwave oven, geyser",
                descriptionBn = "ওয়াশিং মেশিন, ওভেন, গিজার মেরামত",
                iconName = "microwave",
                baseRate = 450.0,
                isPopular = false,
                displayOrder = 7
            ),
            Category(
                id = "cat_mason",
                titleEn = "Mason",
                titleBn = "রাজমিস্ত্রি",
                descriptionEn = "Brick work, plaster, tiles installation, crack fix",
                descriptionBn = "গাঁথুনি, প্লাস্টার, টাইলস বসানো, ফাটল মেরামত",
                iconName = "foundation",
                baseRate = 550.0,
                isPopular = false,
                displayOrder = 8
            ),
            Category(
                id = "cat_cleaner",
                titleEn = "Cleaner",
                titleBn = "ডিপ ক্লিনার",
                descriptionEn = "Bathroom deep clean, sofa, water tank cleaning",
                descriptionBn = "বাথরুম ডিপ ক্লিন, সোফা ওয়াশ, পানির ট্যাঙ্কি পরিষ্কার",
                iconName = "cleaning_services",
                baseRate = 400.0,
                isPopular = false,
                displayOrder = 9
            ),
            Category(
                id = "cat_welder",
                titleEn = "Welder",
                titleBn = "ওয়েল্ডার (লোহার কাজ)",
                descriptionEn = "Grill repair, gate welding, steel frame",
                descriptionBn = "জানালা গ্রিল, গেট ওয়েল্ডিং, লোহার ফ্রেম",
                iconName = "precision_manufacturing",
                baseRate = 500.0,
                isPopular = false,
                displayOrder = 10
            ),
            Category(
                id = "cat_mechanic",
                titleEn = "Mechanic",
                titleBn = "মেকানিক",
                descriptionEn = "Generator, bike, emergency automobile fix",
                descriptionBn = "জেনারেটর সার্ভিস, জরুরি মেকানিক সেবা",
                iconName = "build",
                baseRate = 500.0,
                isPopular = false,
                displayOrder = 11
            ),
            Category(
                id = "cat_construction",
                titleEn = "Construction",
                titleBn = "নির্মাণ কর্মী",
                descriptionEn = "Renovation, labor contractor, demolition",
                descriptionBn = "বাড়ি সংস্কার, লেবার কন্ট্রাক্ট, ছাদ মেরামত",
                iconName = "engineering",
                baseRate = 600.0,
                isPopular = false,
                displayOrder = 12
            )
        )
        categoryDao.insertCategories(seedCategories)

        // 2. Seed Users
        val seedUsers = listOf(
            User(
                id = "cust_1",
                name = "Sidratul Muntaha",
                phone = "01711234567",
                email = "customer@mistrisheba.bd",
                role = UserRole.CUSTOMER,
                address = "House 42, Road 9/A, Dhanmondi",
                area = "Dhanmondi, Dhaka",
                isVerified = true,
                kycStatus = KycStatus.VERIFIED,
                nidNumber = "19942691234567890"
            ),
            User(
                id = "mistri_1",
                name = "Rafiqul Islam",
                phone = "01812987654",
                email = "rafiq.mistri@gmail.com",
                role = UserRole.MISTRI,
                address = "Block C, Section 10, Mirpur",
                area = "Mirpur, Dhaka",
                rating = 4.9,
                reviewCount = 38,
                isVerified = true,
                kycStatus = KycStatus.VERIFIED,
                nidNumber = "19882698765432109",
                tradeLicense = "TL-DCC-2023-8912",
                categoryId = "cat_ac",
                categoryTitleEn = "AC Technician",
                categoryTitleBn = "এসি টেকনিশিয়ান",
                hourlyRate = 500.0,
                visitFee = 200.0,
                experienceYears = 8,
                completedJobs = 142,
                isOnline = true,
                skills = "Inverter AC, Gas Charge, Leak repair, Jet wash servicing, Split & Window",
                bio = "৮ বছরের অভিজ্ঞ এসি টেকনিশিয়ান। ডিলার সার্টিফিকেট প্রাপ্ত। সৎ এবং যত্নশীল সেবা নিশ্চিত করি।"
            ),
            User(
                id = "mistri_2",
                name = "Dulal Hossain",
                phone = "01913445566",
                email = "dulal.electric@gmail.com",
                role = UserRole.MISTRI,
                address = "Kazi Nazrul Islam Road, Mohammadpur",
                area = "Mohammadpur, Dhaka",
                rating = 4.8,
                reviewCount = 52,
                isVerified = true,
                kycStatus = KycStatus.VERIFIED,
                nidNumber = "19852694455667788",
                tradeLicense = "TL-DCC-2022-4411",
                categoryId = "cat_electrician",
                categoryTitleEn = "Electrician",
                categoryTitleBn = "ইলেকট্রিশিয়ান",
                hourlyRate = 350.0,
                visitFee = 150.0,
                experienceYears = 12,
                completedJobs = 215,
                isOnline = true,
                skills = "Short circuit repair, Main DB board, Concealed wiring, IPS install",
                bio = "১২ বছর ধরে ঢাকা শহরের বিভিন্ন এলাকায় নিখুঁত ও নিরাপদ বৈদ্যুতিক কাজ করে আসছি।"
            ),
            User(
                id = "mistri_3",
                name = "Jahangir Alam",
                phone = "01614778899",
                email = "jahangir.plumber@gmail.com",
                role = UserRole.MISTRI,
                address = "Road 11, Block D, Banani",
                area = "Banani, Dhaka",
                rating = 4.7,
                reviewCount = 29,
                isVerified = true,
                kycStatus = KycStatus.VERIFIED,
                nidNumber = "19902697788991122",
                tradeLicense = "TL-DNCC-2024-1188",
                categoryId = "cat_plumber",
                categoryTitleEn = "Plumber",
                categoryTitleBn = "প্লাম্বার ও স্যানিটারি",
                hourlyRate = 400.0,
                visitFee = 150.0,
                experienceYears = 6,
                completedJobs = 94,
                isOnline = true,
                skills = "PPR Pipe fitting, Concealed shower mixer, Motor pump, Water tank cleaning",
                bio = "প্লাম্বিং ও স্যানিটারি কাজের নির্ভুল সমাধান। কোনো ধরনের পানি লিকেজের স্থায়ী ব্যবস্থা।"
            ),
            User(
                id = "mistri_4",
                name = "Nurul Haque",
                phone = "01515332211",
                email = "nurul.carpenter@gmail.com",
                role = UserRole.MISTRI,
                address = "Sector 7, Uttara",
                area = "Uttara, Dhaka",
                rating = 4.9,
                reviewCount = 44,
                isVerified = true,
                kycStatus = KycStatus.VERIFIED,
                nidNumber = "19832693322114455",
                categoryId = "cat_carpenter",
                categoryTitleEn = "Carpenter",
                categoryTitleBn = "কাঠমিস্ত্রি",
                hourlyRate = 450.0,
                visitFee = 200.0,
                experienceYears = 15,
                completedJobs = 188,
                isOnline = true,
                skills = "Door lock fitting, Modular kitchen, Wardrobe repair, Wood polishing",
                bio = "১৫ বছরের অভিজ্ঞ কাঠমিস্ত্রি। যেকোনো আধুনিক ও ক্লাসিক ফার্নিচারের নিখুঁত কাজ।"
            ),
            User(
                id = "mistri_5",
                name = "Shahidul Islam (Pending KYC)",
                phone = "01788990011",
                email = "shahidul.ac@gmail.com",
                role = UserRole.MISTRI,
                address = "Gulshan 1, Dhaka",
                area = "Gulshan, Dhaka",
                rating = 4.5,
                reviewCount = 3,
                isVerified = false,
                kycStatus = KycStatus.PENDING,
                nidNumber = "19972690011223344",
                tradeLicense = "Applied - TL 2026-90",
                categoryId = "cat_refrigerator",
                categoryTitleEn = "Refrigerator Tech",
                categoryTitleBn = "ফ্রিজ টেকনিশিয়ান",
                hourlyRate = 450.0,
                visitFee = 150.0,
                experienceYears = 3,
                completedJobs = 8,
                isOnline = false,
                skills = "Deep fridge, Gas filling, thermostat replacement",
                bio = "নতুন যোগ দিয়েছি। সব ধরনের রেফ্রিজারেটর যত্ন সহকারে ঠিক করি।"
            ),
            User(
                id = "admin_1",
                name = "Platform Administrator",
                phone = "01700000000",
                email = "admin@mistrisheba.bd",
                role = UserRole.ADMIN,
                address = "Dhaka, Bangladesh",
                area = "Motijheel, Dhaka",
                isVerified = true,
                kycStatus = KycStatus.VERIFIED
            )
        )
        userDao.insertUsers(seedUsers)

        // 3. Seed an Active Job for instant tracking experience
        val seedJob = Job(
            id = "MS-84912",
            customerId = "cust_1",
            customerName = "Sidratul Muntaha",
            customerPhone = "01711234567",
            customerAddress = "House 42, Road 9/A, Dhanmondi",
            customerArea = "Dhanmondi, Dhaka",
            mistriId = "mistri_1",
            mistriName = "Rafiqul Islam",
            mistriPhone = "01812987654",
            categoryId = "cat_ac",
            serviceTitleEn = "Master AC Servicing & Jet Wash",
            serviceTitleBn = "মাস্টার এসি সার্ভিসিং ও জেট ওয়াশ",
            description = "1.5 Ton Gree Split AC not cooling properly and making buzzing sound.",
            urgency = JobUrgency.EMERGENCY,
            scheduledDate = "Today / আজ",
            scheduledTimeSlot = "11:00 AM - 01:00 PM",
            status = JobStatus.ON_THE_WAY,
            startOtp = "4821",
            finishOtp = "7392",
            visitFee = 200.0,
            laborCost = 500.0,
            partsCost = 0.0,
            platformFeePercent = 8.0,
            platformFeeAmount = 56.0,
            totalAmount = 756.0,
            paymentMethod = PaymentMethod.PENDING,
            isPaid = false,
            createdAt = System.currentTimeMillis() - 1000 * 60 * 45 // 45 mins ago
        )
        jobDao.insertJob(seedJob)

        // 4. Seed Reviews
        val seedReviews = listOf(
            Review(
                id = "rev_1",
                jobId = "MS-81001",
                customerId = "cust_1",
                customerName = "Tanvir Ahmed",
                mistriId = "mistri_1",
                rating = 5,
                comment = "খুব ভালো সার্ভিস! রফিকুল ভাই ঠিক সময়ে এসে এসি ক্লিন করে দিয়েছেন। এখন পুরো বরফের মত ঠান্ডা হচ্ছে।",
                tags = "সময়নিষ্ঠ, পরিচ্ছন্ন কাজ, দক্ষ টেকনিশিয়ান",
                createdAt = System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 3
            ),
            Review(
                id = "rev_2",
                jobId = "MS-81002",
                customerId = "cust_2",
                customerName = "Nusrat Jahan",
                mistriId = "mistri_1",
                rating = 5,
                comment = "Very polite and professional. Gas charge and coil repair done within 1 hour. Highly recommended!",
                tags = "On-time, Professional, Fair Price",
                createdAt = System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 7
            ),
            Review(
                id = "rev_3",
                jobId = "MS-81003",
                customerId = "cust_3",
                customerName = "Mahmudul Hasan",
                mistriId = "mistri_2",
                rating = 5,
                comment = "দুলাল ভাই অনেক পুরনো দক্ষ ইলেকট্রিশিয়ান। পুরো বাসার শর্ট সার্কিট ১০ মিনিটে বের করে ঠিক করেছেন।",
                tags = "অত্যন্ত অভিজ্ঞ, সৎ মানুষ",
                createdAt = System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 5
            )
        )
        reviewDao.insertReviews(seedReviews)

        // 5. Seed Chat Messages for the active job
        val seedChat = listOf(
            ChatMessage(
                id = "msg_1",
                jobId = "MS-84912",
                senderId = "cust_1",
                senderName = "Sidratul Muntaha",
                senderRole = UserRole.CUSTOMER,
                messageText = "আসসালামু আলাইকুম রফিক ভাই, ধানমন্ডি ৯/এ তে কখন পৌঁছাবেন?",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 25
            ),
            ChatMessage(
                id = "msg_2",
                jobId = "MS-84912",
                senderId = "mistri_1",
                senderName = "Rafiqul Islam",
                senderRole = UserRole.MISTRI,
                messageText = "ওয়ালাইকুম আসসালাম স্যার। আমি ধানমন্ডি ২৭ নম্বর পার হয়েছি। আর ১০-১৫ মিনিটের মধ্যে পৌঁছে যাব ইনশাআল্লাহ।",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 20
            ),
            ChatMessage(
                id = "msg_3",
                jobId = "MS-84912",
                senderId = "cust_1",
                senderName = "Sidratul Muntaha",
                senderRole = UserRole.CUSTOMER,
                messageText = "ঠিক আছে, লিফটের ৪ তলায় চলে আসবেন।",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 15
            )
        )
        chatDao.insertMessages(seedChat)

        // 6. Seed Notifications
        val seedNotifs = listOf(
            AppNotification(
                id = "notif_1",
                userId = "cust_1",
                titleEn = "Mistri is On the Way! (#MS-84912)",
                titleBn = "মিস্ত্রি রওনা হয়েছেন! (#MS-84912)",
                bodyEn = "Rafiqul Islam is heading towards your location in Dhanmondi.",
                bodyBn = "রফিকুল ইসলাম ধানমন্ডিতে আপনার ঠিকানার দিকে রওনা হয়েছেন।",
                relatedJobId = "MS-84912",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 30
            )
        )
        notificationDao.insertNotifications(seedNotifs)
    }
}
