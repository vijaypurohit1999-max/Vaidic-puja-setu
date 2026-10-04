package com.example.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.BookingDraft
import com.example.data.model.PaymentMethodOption
import com.example.data.model.PujaBooking
import com.example.data.model.PujaServiceItem
import com.example.data.model.ReferralApplyResult
import com.example.data.model.UserProfile
import com.example.data.model.UserRole
import com.example.data.model.VaidikCatalog
import com.example.data.repository.PujaRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

class PujaViewModel(
    private val repository: PujaRepository,
    private val currentUserId: String,
    initialPhone: String,
    initialRole: UserRole = UserRole.YAJMAN
) : ViewModel() {

    val userProfileState: StateFlow<UiState<UserProfile?>> = repository.observeUserProfile(currentUserId)
        .map<UserProfile?, UiState<UserProfile?>> { profile ->
            if (profile != null) {
                syncFormWithProfile(profile)
            }
            UiState.Success(profile)
        }
        .catch { error ->
            Log.w(TAG, "Error observing user profile", error)
            emit(UiState.Error(error.message ?: "Failed to load user profile"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = UiState.Loading
        )

    val bookingsState: StateFlow<UiState<List<PujaBooking>>> = repository.observeUserBookings()
        .map<List<PujaBooking>, UiState<List<PujaBooking>>> { UiState.Success(it) }
        .catch { error ->
            Log.w(TAG, "Error observing bookings", error)
            emit(UiState.Error(error.message ?: "Failed to load bookings"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = UiState.Loading
        )

    private val _activeRole = MutableStateFlow(initialRole)
    val activeRole: StateFlow<UserRole> = _activeRole.asStateFlow()

    // Registration & Profile Details Form State (including Email ID moved from Login to Registration/Profile)
    private val _yajmanName = MutableStateFlow("राजेश शर्मा (Rajesh Sharma)")
    val yajmanName: StateFlow<String> = _yajmanName.asStateFlow()

    private val _yajmanPhone = MutableStateFlow(initialPhone.ifBlank { "9876543210" })
    val yajmanPhone: StateFlow<String> = _yajmanPhone.asStateFlow()

    private val _yajmanEmail = MutableStateFlow("rajesh.sharma@vaidikpuja.org")
    val yajmanEmail: StateFlow<String> = _yajmanEmail.asStateFlow()

    private val _yajmanAddress = MutableStateFlow("108, महाकाल मार्ग, उज्जैन (मध्य प्रदेश)")
    val yajmanAddress: StateFlow<String> = _yajmanAddress.asStateFlow()

    private val _selectedGotra = MutableStateFlow(VaidikCatalog.gotraOptions.first())
    val selectedGotra: StateFlow<String> = _selectedGotra.asStateFlow()

    private val _selectedPuja = MutableStateFlow(VaidikCatalog.pujaServices.first())
    val selectedPuja: StateFlow<PujaServiceItem> = _selectedPuja.asStateFlow()

    private val _selectedDate = MutableStateFlow(getUpcomingDates().first().first)
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _selectedTimeSlot = MutableStateFlow("08:00 AM")
    val selectedTimeSlot: StateFlow<String> = _selectedTimeSlot.asStateFlow()

    private val _includeSamagri = MutableStateFlow(true)
    val includeSamagri: StateFlow<Boolean> = _includeSamagri.asStateFlow()

    private val _specialInstructions = MutableStateFlow("परिवार की सुख-शांति, शत्रु बाधा निवारण एवं व्यापार वृद्धि हेतु विशेष संकल्प।")
    val specialInstructions: StateFlow<String> = _specialInstructions.asStateFlow()

    private val _selectedPaymentMethod = MutableStateFlow(PaymentMethodOption.UPI_GPAY)
    val selectedPaymentMethod: StateFlow<PaymentMethodOption> = _selectedPaymentMethod.asStateFlow()

    private val _otpVerified = MutableStateFlow(true)
    val otpVerified: StateFlow<Boolean> = _otpVerified.asStateFlow()

    private val _statusBannerMessage = MutableStateFlow<String?>(null)
    val statusBannerMessage: StateFlow<String?> = _statusBannerMessage.asStateFlow()

    private val _referralCodeInput = MutableStateFlow("VAIDIK50")
    val referralCodeInput: StateFlow<String> = _referralCodeInput.asStateFlow()

    private val _referralResult = MutableStateFlow<ReferralApplyResult?>(null)
    val referralResult: StateFlow<ReferralApplyResult?> = _referralResult.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private var profileSyncedOnce = false

    init {
        viewModelScope.launch {
            repository.seedDemoBookingsIfEmpty(currentUserId)
            val existing = repository.getUserProfile(currentUserId).getOrNull()
            if (existing == null) {
                repository.saveUserProfile(
                    userId = currentUserId,
                    name = _yajmanName.value,
                    phone = _yajmanPhone.value,
                    email = _yajmanEmail.value,
                    address = _yajmanAddress.value,
                    gotra = _selectedGotra.value,
                    role = initialRole.code,
                    preferredPuja = _selectedPuja.value.titleEn,
                    specialInstructions = _specialInstructions.value,
                    isPanditOnline = true,
                    otpVerified = true
                )
            }
        }
    }

    private fun syncFormWithProfile(profile: UserProfile) {
        if (profileSyncedOnce) return
        profileSyncedOnce = true
        if (profile.name.isNotBlank()) _yajmanName.value = profile.name
        if (profile.phone.isNotBlank()) _yajmanPhone.value = profile.phone
        if (profile.email.isNotBlank()) _yajmanEmail.value = profile.email
        if (profile.address.isNotBlank()) _yajmanAddress.value = profile.address
        if (profile.gotra.isNotBlank()) _selectedGotra.value = profile.gotra
        if (profile.specialInstructions.isNotBlank()) _specialInstructions.value = profile.specialInstructions
        _otpVerified.value = profile.otpVerified
        val matchingPuja = VaidikCatalog.pujaServices.find { it.titleEn == profile.preferredPuja }
        if (matchingPuja != null) {
            _selectedPuja.value = matchingPuja
        }
    }

    fun setActiveRole(role: UserRole) {
        _activeRole.value = role
    }

    fun updateYajmanName(value: String) {
        _yajmanName.value = value
    }

    fun updateYajmanPhone(value: String) {
        val digitsOnly = value.filter { it.isDigit() }.take(10)
        _yajmanPhone.value = digitsOnly
    }

    fun updateYajmanEmail(value: String) {
        _yajmanEmail.value = value
    }

    fun updateYajmanAddress(value: String) {
        _yajmanAddress.value = value
    }

    fun updateSelectedGotra(value: String) {
        _selectedGotra.value = value
    }

    fun selectPujaService(service: PujaServiceItem) {
        _selectedPuja.value = service
    }

    fun updateSelectedDate(dateStr: String) {
        _selectedDate.value = dateStr
    }

    fun updateSelectedTimeSlot(slot: String) {
        _selectedTimeSlot.value = slot
    }

    fun updateIncludeSamagri(include: Boolean) {
        _includeSamagri.value = include
    }

    fun updateSpecialInstructions(notes: String) {
        _specialInstructions.value = notes
    }

    fun updatePaymentMethod(method: PaymentMethodOption) {
        _selectedPaymentMethod.value = method
    }

    fun clearBannerMessage() {
        _statusBannerMessage.value = null
    }

    fun updateReferralCodeInput(value: String) {
        _referralCodeInput.value = value.uppercase(Locale.US).take(20)
        _referralResult.value = null
    }

    fun applyReferralCode() {
        val code = _referralCodeInput.value.trim()
        viewModelScope.launch {
            val result = repository.applyReferral(currentUserId, code)
            _referralResult.value = result
            _statusBannerMessage.value = result.message
        }
    }

    fun saveRegistrationProfile(onSuccess: () -> Unit = {}) {
        val name = _yajmanName.value.trim()
        val phone = _yajmanPhone.value.trim()
        val email = _yajmanEmail.value.trim()
        val address = _yajmanAddress.value.trim()

        if (name.isBlank() || phone.length < 10 || address.isBlank()) {
            _statusBannerMessage.value = "कृपया नाम, 10 अंकों का मोबाइल नंबर एवं पता दर्ज करें।"
            return
        }
        if (email.isNotBlank() && (!email.contains("@") || !email.contains("."))) {
            _statusBannerMessage.value = "कृपया मान्य ईमेल आईडी (Valid Email ID) दर्ज करें।"
            return
        }

        _isSubmitting.value = true
        viewModelScope.launch {
            val currentOnline = (userProfileState.value as? UiState.Success)?.data?.isPanditOnline ?: true
            val result = repository.saveUserProfile(
                userId = currentUserId,
                name = name,
                phone = phone,
                email = email,
                address = address,
                gotra = _selectedGotra.value,
                role = _activeRole.value.code,
                preferredPuja = _selectedPuja.value.titleEn,
                specialInstructions = _specialInstructions.value,
                isPanditOnline = currentOnline,
                otpVerified = true
            )
            _isSubmitting.value = false
            result.onSuccess {
                _otpVerified.value = true
                _statusBannerMessage.value = "प्रोफाइल एवं पंजीकरण विवरण (ईमेल: ${email.ifEmpty { "N/A" }}) सफलतापूर्वक सुरक्षित किया गया!"
                onSuccess()
            }.onFailure { err ->
                _statusBannerMessage.value = "त्रुटि: ${err.localizedMessage ?: "Failed to save profile"}"
            }
        }
    }

    fun buildCurrentBookingDraft(): BookingDraft {
        return BookingDraft(
            yajmanName = _yajmanName.value,
            yajmanPhone = _yajmanPhone.value,
            yajmanEmail = _yajmanEmail.value,
            yajmanAddress = _yajmanAddress.value,
            gotra = _selectedGotra.value,
            pujaService = _selectedPuja.value,
            pujaDate = _selectedDate.value,
            timeSlot = _selectedTimeSlot.value,
            includeSamagri = _includeSamagri.value,
            specialInstructions = _specialInstructions.value,
            paymentMethod = _selectedPaymentMethod.value
        )
    }

    fun confirmPaymentAndCreateBooking(onSuccess: (String) -> Unit) {
        val draft = buildCurrentBookingDraft()
        if (draft.yajmanName.isBlank() || draft.yajmanPhone.length < 10 || draft.yajmanAddress.isBlank()) {
            _statusBannerMessage.value = "कृपया यजमान का नाम, 10 अंकों का मोबाइल नंबर एवं पूजा स्थान का पता पूर्ण करें।"
            return
        }
        if (draft.yajmanEmail.isNotBlank() && (!draft.yajmanEmail.contains("@") || !draft.yajmanEmail.contains("."))) {
            _statusBannerMessage.value = "कृपया पंजीकरण फॉर्म में मान्य ईमेल आईडी (Valid Email ID) दर्ज करें।"
            return
        }

        _isSubmitting.value = true
        viewModelScope.launch {
            val currentOnline = (userProfileState.value as? UiState.Success)?.data?.isPanditOnline ?: true
            repository.saveUserProfile(
                userId = currentUserId,
                name = draft.yajmanName,
                phone = draft.yajmanPhone,
                email = draft.yajmanEmail,
                address = draft.yajmanAddress,
                gotra = draft.gotra,
                role = _activeRole.value.code,
                preferredPuja = draft.pujaService.titleEn,
                specialInstructions = draft.specialInstructions,
                isPanditOnline = currentOnline,
                otpVerified = true
            )

            val bookingResult = repository.createBooking(currentUserId, draft)
            _isSubmitting.value = false
            bookingResult.onSuccess { bookingId ->
                _statusBannerMessage.value = "॥ शुभम् भवतु ॥ आपकी ${draft.pujaService.titleHi} बुकिंग (#${bookingId.takeLast(6).uppercase()}) सफलतापूर्वक संपन्न हुई!"
                onSuccess(bookingId)
            }.onFailure { err ->
                _statusBannerMessage.value = "बुकिंग त्रुटि: ${err.localizedMessage ?: "Failed to create booking"}"
            }
        }
    }

    fun togglePanditOnline(isOnline: Boolean) {
        viewModelScope.launch {
            val result = repository.togglePanditOnlineStatus(currentUserId, isOnline)
            result.onSuccess {
                _statusBannerMessage.value = if (isOnline) {
                    "आचार्य स्थिति: ONLINE (नई पूजा बुकिंग हेतु उपलब्ध)"
                } else {
                    "आचार्य स्थिति: OFFLINE (विश्राम / अनुष्ठान में व्यस्त)"
                }
            }
        }
    }

    fun markBookingCompleted(bookingId: String) {
        viewModelScope.launch {
            val result = repository.updateBookingStatus(bookingId, "COMPLETED")
            result.onSuccess {
                _statusBannerMessage.value = "पूजा सफलतापूर्वक 'संपन्न (Completed)' चिह्नित की गई एवं दक्षिणा राशि खाते में जोड़ी गई!"
            }.onFailure { err ->
                _statusBannerMessage.value = "त्रुटि: ${err.localizedMessage}"
            }
        }
    }

    fun getUpcomingDates(): List<Pair<String, String>> {
        val list = mutableListOf<Pair<String, String>>()
        val isoFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val displayFormat = SimpleDateFormat("dd MMM, EEE", Locale.US)
        val tithiLabels = listOf(
            "आज • सर्वार्थ सिद्धि",
            "कल • अमृत सिद्धि योग",
            "शुभ अष्टमी मुहूर्त",
            "नवमी विशेष हवन",
            "दशमी विजय मुहूर्त",
            "एकादशी शुभ योग",
            "प्रदोष विशेष पूजन"
        )
        val cal = Calendar.getInstance()
        for (i in 0..6) {
            val date = cal.time
            val iso = isoFormat.format(date)
            val label = "${displayFormat.format(date)} • ${tithiLabels[i % tithiLabels.size]}"
            list.add(iso to label)
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        return list
    }

    fun getTodayIsoDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    }

    private companion object {
        const val TAG = "PujaViewModel"
        const val STOP_TIMEOUT_MILLIS = 5000L
    }
}
