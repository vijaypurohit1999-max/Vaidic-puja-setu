package com.example.data.model

import androidx.annotation.DrawableRes
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.R
import kotlin.math.roundToInt

enum class UserRole(val code: String, val titleHi: String, val subtitleHi: String) {
    YAJMAN("YAJMAN", "यजमान (Yajman / Devotee)", "पूजा बुकिंग, मुहूर्त चयन एवं संकल्प"),
    PANDIT("PANDIT", "पंडित जी (Pandit Ji / Acharya)", "बुकिंग प्रबंधन, आय-कमीशन एवं यजमान संपर्क");

    companion object {
        fun fromCode(code: String): UserRole = entries.find { it.code == code } ?: YAJMAN
    }
}

enum class PaymentMethodOption(
    val code: String,
    val title: String,
    val subtitle: String,
    val badge: String
) {
    UPI_GPAY("UPI_GPAY", "Google Pay (GPay UPI)", "Instant Vaidik Sankalp UPI Transfer", "Zero Fee • Instant"),
    UPI_PHONEPE("UPI_PHONEPE", "PhonePe UPI", "Auto-Verified UPI Collect / Intent", "Popular in MP/UP"),
    UPI_PAYTM("UPI_PAYTM", "Paytm UPI & Wallet", "Direct UPI QR & VPA Payment", "Fast Checkout"),
    CARD("CARD", "Credit / Debit Card", "RuPay, Visa, Mastercard (3D Secure OTP)", "All Banks"),
    NET_BANKING("NET_BANKING", "Net Banking (NEFT/IMPS)", "SBI, HDFC, ICICI, Axis, PNB", "Corporate / Bulk Puja");

    companion object {
        fun fromCode(code: String): PaymentMethodOption = entries.find { it.code == code } ?: UPI_GPAY
    }
}

@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey val userId: String = "",
    val name: String = "",
    val phone: String = "",
    val email: String = "",
    val address: String = "",
    val gotra: String = "कश्यप (Kashyap)",
    val role: String = "YAJMAN",
    val preferredPuja: String = "Bagalamukhi Havan Poojan",
    val specialInstructions: String = "",
    val isPanditOnline: Boolean = true,
    val otpVerified: Boolean = false,
    val createdAtMillis: Long = System.currentTimeMillis(),
    val updatedAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "puja_bookings")
data class PujaBooking(
    @PrimaryKey val id: String = "",
    val userId: String = "",
    val yajmanName: String = "",
    val yajmanPhone: String = "",
    val yajmanEmail: String = "",
    val yajmanAddress: String = "",
    val gotra: String = "",
    val pujaId: String = "",
    val pujaTitle: String = "",
    val pujaDate: String = "",
    val timeSlot: String = "",
    val includeSamagri: Boolean = true,
    val specialInstructions: String = "",
    val basePrice: Int = 0,
    val samagriPrice: Int = 0,
    val totalAmount: Int = 0,
    val platformCommission: Int = 0,
    val panditPayout: Int = 0,
    val paymentMethod: String = "UPI_GPAY",
    val paymentStatus: String = "PAID",
    val bookingStatus: String = "UPCOMING",
    val panditName: String = "आचार्य विश्वनाथ शास्त्री (Acharya Vishwanath Shastri)",
    val createdAtMillis: Long = System.currentTimeMillis(),
    val updatedAtMillis: Long = System.currentTimeMillis()
)

data class BookingDraft(
    val yajmanName: String,
    val yajmanPhone: String,
    val yajmanEmail: String,
    val yajmanAddress: String,
    val gotra: String,
    val pujaService: PujaServiceItem,
    val pujaDate: String,
    val timeSlot: String,
    val includeSamagri: Boolean,
    val specialInstructions: String,
    val paymentMethod: PaymentMethodOption
) {
    val basePrice: Int get() = pujaService.baseDakshina
    val samagriPrice: Int get() = if (includeSamagri) pujaService.samagriCost else 0
    val totalAmount: Int get() = basePrice + samagriPrice
    val platformCommission: Int get() = (totalAmount * PLATFORM_COMMISSION_RATE).roundToInt()
    val panditPayout: Int get() = totalAmount - platformCommission

    companion object {
        const val PLATFORM_COMMISSION_RATE = 0.15 // 15% Platform Service Commission
    }
}

data class PujaServiceItem(
    val id: String,
    val titleEn: String,
    val titleHi: String,
    val categoryTag: String,
    val duration: String,
    val baseDakshina: Int,
    val samagriCost: Int,
    val assignedPandit: String,
    val panditExperience: String,
    val rating: String,
    val shortDescHi: String,
    val vidhiHighlights: List<String>,
    @param:DrawableRes val imageRes: Int,
    val isFeatured: Boolean = false
)

data class TimeSlotOption(
    val time: String,
    val muhuratNameHi: String,
    val isAuspicious: Boolean = true
)

object VaidikCatalog {
    val gotraOptions = listOf(
        "कश्यप (Kashyap)",
        "भारद्वाज (Bharadwaj)",
        "वशिष्ठ (Vashishtha)",
        "गौतम (Gautam)",
        "अत्रि (Atri)",
        "विश्वामित्र (Vishwamitra)",
        "जमदग्नि (Jamadagni)",
        "अगस्त्य (Agastya)",
        "शांडिल्य (Shandilya)",
        "गर्ग (Garga)"
    )

    val timeSlots = listOf(
        TimeSlotOption("06:00 AM", "ब्रह्म मुहूर्त (Brahma Muhurat)", true),
        TimeSlotOption("08:00 AM", "प्रातः शुभ चौघड़िया (Shubh Choghadiya)", true),
        TimeSlotOption("10:30 AM", "अभिजित मुहूर्त (Abhijit Muhurat - श्रेष्ठ)", true),
        TimeSlotOption("01:30 PM", "मध्याह्न चर चौघड़िया (Madhyahna Kaal)", false),
        TimeSlotOption("04:30 PM", "सायंकाल लाभ चौघड़िया (Sayam Kaal)", true),
        TimeSlotOption("07:30 PM", "रात्रि विशेष हवन (Bagalamukhi Vishesh)", true)
    )

    val pujaServices = listOf(
        PujaServiceItem(
            id = "bagalamukhi_havan",
            titleEn = "Bagalamukhi Havan Poojan",
            titleHi = "माँ बगलामुखी पीताम्बरा विशेष हवन पूजन",
            categoryTag = "शत्रु बाधा एवं कोर्ट-कचहरी विजय • तंत्रोक्त/वैदिक",
            duration = "3.5 - 4 घंटे (Hours)",
            baseDakshina = 3100,
            samagriCost = 900,
            assignedPandit = "आचार्य विश्वनाथ शास्त्री (नलखेड़ा/उज्जैन)",
            panditExperience = "18+ वर्ष अनुभव • पीताम्बरा पीठ परंपरा",
            rating = "4.9 ★ (420+ यजमान)",
            shortDescHi = "शत्रु पराजय, कोर्ट-कचहरी विजय, राजनीतिक सफलता एवं नकारात्मक ऊर्जा निवारण हेतु हल्दी की माला व पीली सरसों से विशेष हवन।",
            vidhiHighlights = listOf(
                "गौरी-गणेश, कलश एवं नवग्रह पीठ स्थापना",
                "माँ बगलामुखी 36 अक्षरी मंत्र जाप (11,000 आहुति विकल्प)",
                "पीली सरसों, हल्दी गांठ व शुद्ध गाय के घी से विशेष हवन",
                "रक्षा सूत्र (पीताम्बरा कवच) एवं पूर्णाहुति आरती"
            ),
            imageRes = R.drawable.img_bagalamukhi_havan,
            isFeatured = true
        ),
        PujaServiceItem(
            id = "rudrabhishek_puja",
            titleEn = "Laghu Rudrabhishek & Mahamrityunjaya",
            titleHi = "वैदिक रुद्राभिषेक एवं महामृत्युंजय अनुष्ठान",
            categoryTag = "स्वास्थ्य, दीर्घायु एवं ग्रह शांति • शुक्ल यजुर्वेद",
            duration = "2.5 - 3 घंटे (Hours)",
            baseDakshina = 2100,
            samagriCost = 650,
            assignedPandit = "पं. आशुतोष त्रिपाठी (काशी विद्वत परंपरा)",
            panditExperience = "14+ वर्ष अनुभव • रुद्राष्टाध्यायी विशेषज्ञ",
            rating = "4.9 ★ (610+ यजमान)",
            shortDescHi = "पंचामृत, गन्ने के रस, कुशोदक एवं बिल्वपत्र द्वारा रुद्राष्टाध्यायी के सस्वर पाठ के साथ भगवान आशुतोष का अभिषेक।",
            vidhiHighlights = listOf(
                "संकल्प, पार्थिव या नर्मदेश्वर शिवलिंग स्थापना",
                "रुद्राष्टाध्यायी के 8 अध्यायों द्वारा अखंड अभिषेक",
                "महामृत्युंजय मंत्र संपुटित हवन एवं भस्म आरती"
            ),
            imageRes = R.drawable.img_rudrabhishek,
            isFeatured = false
        ),
        PujaServiceItem(
            id = "griha_pravesh",
            titleEn = "Griha Pravesh & Vastu Shanti Poojan",
            titleHi = "गृह प्रवेश, वास्तु शांति एवं नवग्रह हवन",
            categoryTag = "नूतन गृह मंगल प्रवेश • संपूर्ण वास्तु विधान",
            duration = "4 - 5 घंटे (Hours)",
            baseDakshina = 4100,
            samagriCost = 1100,
            assignedPandit = "आचार्य राघवेंद्र द्विवेदी (ज्योतिष एवं वास्तु आचार्य)",
            panditExperience = "20+ वर्ष अनुभव • वास्तु शास्त्र विशेषज्ञ",
            rating = "5.0 ★ (340+ यजमान)",
            shortDescHi = "नवीन भवन में सुख-समृद्धि, वास्तु दोष निवारण, चौखट पूजन, कलश प्रवेश एवं चतुःषष्टि योगिनी-क्षेत्रपाल पूजन।",
            vidhiHighlights = listOf(
                "द्वार पूजन, तोरण बंधन एवं मंगल कलश सिर पर रखकर प्रवेश",
                "वास्तु पुरुष 81 पद पूजन एवं दसों दिशाओं का दिग्बंधन",
                "नवग्रह शांति हवन, दुग्ध उबालना एवं कन्या/ब्राह्मण आशीर्वाद"
            ),
            imageRes = R.drawable.img_griha_pravesh,
            isFeatured = false
        ),
        PujaServiceItem(
            id = "satyanarayan_katha",
            titleEn = "Shree Satyanarayan Vrat Katha & Havan",
            titleHi = "श्री सत्यनारायण व्रत कथा एवं पारिवारिक हवन",
            categoryTag = "पारिवारिक सुख-समृद्धि एवं मनोकामना पूर्ति",
            duration = "2 घंटे (Hours)",
            baseDakshina = 1500,
            samagriCost = 500,
            assignedPandit = "पं. देवेश्वर पांडेय (प्रयागराज वैदिक गुरुकुल)",
            panditExperience = "12+ वर्ष अनुभव • पुराण कथा वाचक",
            rating = "4.8 ★ (890+ यजमान)",
            shortDescHi = "स्कंद पुराण के रेवाखंड में वर्णित भगवान श्री सत्यनारायण के 5 अध्यायों की कथा, शालिग्राम अभिषेक व पंचामृत प्रसाद।",
            vidhiHighlights = listOf(
                "गणेश-अम्बिका, वरुण कलश एवं नवग्रह पूजन",
                "श्री सत्यनारायण के 5 अध्यायों की सस्वर हिंदी/संस्कृत कथा",
                "दशांश हवन, पंजीरी-पंचामृत भोग एवं परिवार आरती"
            ),
            imageRes = R.drawable.img_griha_pravesh,
            isFeatured = false
        ),
        PujaServiceItem(
            id = "navagraha_shanti",
            titleEn = "Navagraha & Kaal Sarp Dosh Shanti",
            titleHi = "नवग्रह शांति एवं कालसर्प/राहु-केतु दोष निवारण",
            categoryTag = "कुंडली दोष निवारण • नव समिधा हवन",
            duration = "3 घंटे (Hours)",
            baseDakshina = 2500,
            samagriCost = 800,
            assignedPandit = "आचार्य विश्वनाथ शास्त्री (उज्जैन ज्योतिष पीठ)",
            panditExperience = "18+ वर्ष अनुभव • ग्रह शांति विशेषज्ञ",
            rating = "4.9 ★ (275+ यजमान)",
            shortDescHi = "जन्म कुंडली में राहु-केतु, शनि की साढ़ेसाती, मंगल दोष एवं नवग्रह पीड़ा निवारण हेतु नौ प्रकार की वैदिक समिधाओं से हवन।",
            vidhiHighlights = listOf(
                "नवग्रह मंडल रचना (अर्क, पलाश, खदिर, अपामार्ग आदि 9 समिधा)",
                "बीज मंत्रों से ग्रह शांति जाप एवं दशांश हवन",
                "चांदी के नाग-नागिन पूजन एवं गोदान संकल्प"
            ),
            imageRes = R.drawable.img_bagalamukhi_havan,
            isFeatured = false
        )
    )
}
