package com.example.data.repository

import android.content.Context
import com.example.data.local.PujaDao
import com.example.data.local.PujaDatabase
import com.example.data.model.BookingDraft
import com.example.data.model.PaymentMethodOption
import com.example.data.model.PujaBooking
import com.example.data.model.UserProfile
import com.example.data.model.VaidikCatalog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlinx.coroutines.flow.Flow

class PujaRepository(
    private val dao: PujaDao
) {
    constructor(context: Context) : this(
        PujaDatabase.getInstance(context.applicationContext).pujaDao()
    )

    fun observeUserProfile(userId: String): Flow<UserProfile?> {
        return dao.observeUserProfile(userId)
    }

    suspend fun getUserProfile(userId: String): Result<UserProfile?> = runCatching {
        dao.getUserProfile(userId)
    }

    suspend fun saveUserProfile(
        userId: String,
        name: String,
        phone: String,
        email: String,
        address: String,
        gotra: String,
        role: String,
        preferredPuja: String,
        specialInstructions: String,
        isPanditOnline: Boolean,
        otpVerified: Boolean
    ): Result<UserProfile> = runCatching {
        val now = System.currentTimeMillis()
        val existing = dao.getUserProfile(userId)
        val profile = UserProfile(
            userId = userId,
            name = name.trim().ifEmpty { if (role == "PANDIT") "आचार्य विश्वनाथ शास्त्री" else "राजेश शर्मा" }.take(100),
            phone = phone.trim().ifEmpty { "9876543210" }.take(15),
            email = email.trim().take(120),
            address = address.trim().ifEmpty { "108, महाकाल मार्ग, उज्जैन (म.प्र.)" }.take(300),
            gotra = gotra.trim().ifEmpty { "कश्यप (Kashyap)" }.take(80),
            role = if (role == "PANDIT") "PANDIT" else "YAJMAN",
            preferredPuja = preferredPuja.trim().ifEmpty { "Bagalamukhi Havan Poojan" }.take(120),
            specialInstructions = specialInstructions.trim().take(500),
            isPanditOnline = isPanditOnline,
            otpVerified = otpVerified,
            createdAtMillis = existing?.createdAtMillis ?: now,
            updatedAtMillis = now
        )
        dao.upsertUserProfile(profile)
        profile
    }

    suspend fun togglePanditOnlineStatus(userId: String, isOnline: Boolean): Result<Unit> = runCatching {
        val now = System.currentTimeMillis()
        val existing = dao.getUserProfile(userId)
        if (existing != null) {
            dao.updatePanditOnlineStatus(userId, isOnline, now)
        } else {
            saveUserProfile(
                userId = userId,
                name = "आचार्य विश्वनाथ शास्त्री",
                phone = userId.removePrefix("mobile_").ifEmpty { "9876543210" },
                email = "acharya.vishwanath@vaidikpuja.org",
                address = "महाकाल मार्ग, उज्जैन (म.प्र.)",
                gotra = "कश्यप (Kashyap)",
                role = "PANDIT",
                preferredPuja = "Bagalamukhi Havan Poojan",
                specialInstructions = "वैदिक एवं तंत्रोक्त अनुष्ठान",
                isPanditOnline = isOnline,
                otpVerified = true
            ).getOrThrow()
        }
    }

    fun observeUserBookings(): Flow<List<PujaBooking>> {
        return dao.observeAllBookings()
    }

    suspend fun getUserBookings(): Result<List<PujaBooking>> = runCatching {
        dao.getAllBookings()
    }

    suspend fun getBookingById(bookingId: String): Result<PujaBooking> = runCatching {
        dao.getBookingById(bookingId) ?: throw NoSuchElementException("Booking not found: $bookingId")
    }

    suspend fun createBooking(userId: String, draft: BookingDraft): Result<String> = runCatching {
        val now = System.currentTimeMillis()
        val bookingId = "puja_${UUID.randomUUID().toString().replace("-", "").take(12)}"
        val booking = PujaBooking(
            id = bookingId,
            userId = userId,
            yajmanName = draft.yajmanName.trim().ifEmpty { "राजेश शर्मा" }.take(100),
            yajmanPhone = draft.yajmanPhone.trim().ifEmpty { "9876543210" }.take(15),
            yajmanEmail = draft.yajmanEmail.trim().take(120),
            yajmanAddress = draft.yajmanAddress.trim().ifEmpty { "108 महाकाल मार्ग, उज्जैन" }.take(300),
            gotra = draft.gotra.trim().ifEmpty { "कश्यप (Kashyap)" }.take(80),
            pujaId = draft.pujaService.id.take(80),
            pujaTitle = draft.pujaService.titleEn.take(120),
            pujaDate = draft.pujaDate.take(40),
            timeSlot = draft.timeSlot.take(40),
            includeSamagri = draft.includeSamagri,
            specialInstructions = draft.specialInstructions.trim().take(500),
            basePrice = draft.basePrice,
            samagriPrice = draft.samagriPrice,
            totalAmount = draft.totalAmount,
            platformCommission = draft.platformCommission,
            panditPayout = draft.panditPayout,
            paymentMethod = draft.paymentMethod.code,
            paymentStatus = "PAID",
            bookingStatus = "UPCOMING",
            panditName = draft.pujaService.assignedPandit.take(100),
            createdAtMillis = now,
            updatedAtMillis = now
        )
        dao.insertBooking(booking)
        bookingId
    }

    suspend fun updateBookingStatus(bookingId: String, newStatus: String): Result<Unit> = runCatching {
        dao.updateBookingStatus(bookingId, newStatus, System.currentTimeMillis())
    }

    suspend fun seedDemoBookingsIfEmpty(userId: String): Result<Unit> = runCatching {
        val existing = dao.getAllBookings()
        if (existing.isNotEmpty()) return@runCatching

        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        val bagalamukhi = VaidikCatalog.pujaServices[0]
        val rudrabhishek = VaidikCatalog.pujaServices[1]

        createBooking(
            userId = userId,
            draft = BookingDraft(
                yajmanName = "विक्रम सिंह चौहान (Vikram Singh Chauhan)",
                yajmanPhone = "9826012345",
                yajmanEmail = "vikram.chauhan@example.com",
                yajmanAddress = "42, अरेरा कॉलोनी, भोपाल (म.प्र.)",
                gotra = "भारद्वाज (Bharadwaj)",
                pujaService = bagalamukhi,
                pujaDate = todayStr,
                timeSlot = "08:00 AM",
                includeSamagri = true,
                specialInstructions = "कोर्ट केस विजय एवं पारिवारिक रक्षा संकल्प, पीली सरसों एवं हल्दी माला सहित।",
                paymentMethod = PaymentMethodOption.UPI_GPAY
            )
        ).getOrThrow()

        val secondId = createBooking(
            userId = userId,
            draft = BookingDraft(
                yajmanName = "श्रीमती अनुराधा तिवारी (Anuradha Tiwari)",
                yajmanPhone = "9425067890",
                yajmanEmail = "anuradha.tiwari@example.com",
                yajmanAddress = "15, फ्रीगंज, उज्जैन (म.प्र.)",
                gotra = "कश्यप (Kashyap)",
                pujaService = rudrabhishek,
                pujaDate = todayStr,
                timeSlot = "06:00 AM",
                includeSamagri = true,
                specialInstructions = "गन्ने के रस एवं पंचामृत से रुद्राभिषेक, परिवार के स्वास्थ्य लाभ हेतु।",
                paymentMethod = PaymentMethodOption.UPI_PHONEPE
            )
        ).getOrThrow()

        updateBookingStatus(secondId, "COMPLETED").getOrThrow()
    }
}
