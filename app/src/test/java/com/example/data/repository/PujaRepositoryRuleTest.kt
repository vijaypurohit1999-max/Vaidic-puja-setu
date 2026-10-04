package com.example.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.PujaDatabase
import com.example.data.model.BookingDraft
import com.example.data.model.PaymentMethodOption
import com.example.data.model.VaidikCatalog
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class PujaRepositoryRuleTest {

    private lateinit var database: PujaDatabase
    private lateinit var repository: PujaRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, PujaDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = PujaRepository(database.pujaDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    private fun sampleDraft() = BookingDraft(
        yajmanName = "Rajesh Sharma",
        yajmanPhone = "9876543210",
        yajmanEmail = "rajesh.sharma@vaidikpuja.org",
        yajmanAddress = "108 Mahakal Marg, Ujjain, MP",
        gotra = "Kashyap",
        pujaService = VaidikCatalog.pujaServices.first(),
        pujaDate = "2026-10-15",
        timeSlot = "08:00 AM",
        includeSamagri = true,
        specialInstructions = "Sankalp for family prosperity",
        paymentMethod = PaymentMethodOption.UPI_GPAY
    )

    @Test
    fun createAndGetBooking_withMobileOtpSessionAndEmail_succeeds() = runBlocking {
        val userId = "mobile_9876543210"
        val createResult = repository.createBooking(userId, sampleDraft())
        assertTrue(createResult.isSuccess)
        val bookingId = createResult.getOrThrow()

        val bookings = repository.observeUserBookings().first()
        val saved = bookings.find { it.id == bookingId }
        assertNotNull(saved)
        assertEquals("rajesh.sharma@vaidikpuja.org", saved?.yajmanEmail)
        assertEquals("9876543210", saved?.yajmanPhone)
    }

    @Test
    fun saveAndUpdateUserProfile_withEmailInRegistration_succeeds() = runBlocking {
        val userId = "mobile_9876543210"
        val saveResult = repository.saveUserProfile(
            userId = userId,
            name = "Rajesh Sharma",
            phone = "9876543210",
            email = "rajesh.sharma@vaidikpuja.org",
            address = "108 Mahakal Marg, Ujjain",
            gotra = "Kashyap",
            role = "YAJMAN",
            preferredPuja = "Bagalamukhi Havan Poojan",
            specialInstructions = "Sankalp Vidhi",
            isPanditOnline = true,
            otpVerified = true
        )
        assertTrue(saveResult.isSuccess)

        repository.togglePanditOnlineStatus(userId, false)
        val profile = repository.getUserProfile(userId).getOrThrow()
        assertNotNull(profile)
        assertEquals("rajesh.sharma@vaidikpuja.org", profile?.email)
        assertEquals(false, profile?.isPanditOnline)
    }

    @Test
    fun applyReferral_validAndInvalidCode_awards50VedicCoinsToBothUsers() = runBlocking {
        val newUserId = "mobile_9876543210"
        repository.seedDemoBookingsIfEmpty(newUserId)
        repository.saveUserProfile(
            userId = newUserId,
            name = "Rajesh Sharma",
            phone = "9876543210",
            email = "rajesh.sharma@vaidikpuja.org",
            address = "108 Mahakal Marg, Ujjain",
            gotra = "Kashyap",
            role = "YAJMAN",
            preferredPuja = "Bagalamukhi Havan Poojan",
            specialInstructions = "Sankalp",
            isPanditOnline = true,
            otpVerified = true
        )

        // 1. Invalid code should return "अमान्य रेफ़रल कोड"
        val invalidResult = repository.applyReferral(newUserId, "INVALID999")
        assertEquals(false, invalidResult.success)
        assertEquals("अमान्य रेफ़रल कोड", invalidResult.message)

        // 2. Valid code ("VAIDIK50") should award 50 Vedic Coins to both referrer and newUserId
        val validResult = repository.applyReferral(newUserId, "VAIDIK50")
        assertTrue(validResult.success)
        assertEquals("बधाई हो! आपको और आपके मित्र को 50 वैदिक कॉइन्स मिले।", validResult.message)
        assertEquals(150, validResult.newUserUpdatedCoins)
        assertEquals(150, validResult.referrerUpdatedCoins)
    }
}
