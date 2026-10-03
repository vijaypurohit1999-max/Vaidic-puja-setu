package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.KumkumDark
import com.example.ui.theme.KumkumMaroon
import com.example.ui.theme.PitambaraGold
import com.example.ui.theme.PitambaraLight

data class SpecSectionBlock(
    val badge: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val points: List<String>,
    val codeSnippet: String? = null
)

private val hinglishSpecSections = listOf(
    SpecSectionBlock(
        badge = "STEP 1 • APP LOGIC FLOW",
        title = "Step-by-Step App Logic (स्टेप-बाय-स्टेप ऐप लॉजिक - Hinglish)",
        subtitle = "Yajman (Devotee) aur Pandit Ji ke beech Mobile OTP Auth & Puja Booking workflow",
        icon = Icons.Default.AccountTree,
        points = listOf(
            "1. Mobile Number + OTP Authentication (मोबाइल OTP लॉगिन): Yajman aur Pandit Ji dono ke liye Email/Google login hata kar keval 10-digit Mobile Number + 6-digit OTP verification primary authentication rakha gaya hai.",
            "2. Home & Service Discovery (पूजा चयन): Yajman Home Screen par 'Bagalamukhi Havan Poojan', 'Rudrabhishek', 'Griha Pravesh' aadi services dekhta hai, jisme Vidhi, Duration, Samagri aur Dakshina clearly dikhti hai.",
            "3. Registration & Profile Form with Email ID (पंजीकरण एवं प्रोफाइल फॉर्म): Yajman ya Pandit Ji apna Name, OTP Verified Phone Number, Email ID (ईमेल आईडी), Puja Venue Address, Vedic Gotra (जैसे कश्यप, भारद्वाज), Puja Type aur Special Instructions fill karke profile save karte hain.",
            "4. Muhurat Calendar & Time Slot (मुहूर्त व समय स्लॉट): Yajman Shubh Tithi (Calendar) aur Time Slot (06:00 AM Brahma Muhurat, 08:00 AM Shubh Choghadiya, 10:30 AM Abhijit Muhurat, 07:30 PM Ratri Havan) select karta hai.",
            "5. Dynamic Price & Commission Split (मूल्य व कमीशन गणना): Total Amount = Base Dakshina + Havan Samagri (optional). Platform Commission (15%) aur Pandit Ji Net Payout (85%) real-time calculate hota hai.",
            "6. Multi-Mode Payment Gateway (भुगतान): Yajman UPI (Google Pay, PhonePe, Paytm), Credit/Debit Card ya Net Banking se payment complete karta hai. Booking status 'UPCOMING' aur paymentStatus 'PAID' set hota hai.",
            "7. Pandit Ji Live Dashboard & Execution (पंडित डैशबोर्ड): Pandit Ji apna status Online/Offline rakhte hain, Today's Bookings, Upcoming List aur Earnings dekhte hain, 'Call Yajman' button se seedha sampark karte hain aur Puja sampann hone par 'Mark Completed' karte hain."
        )
    ),
    SpecSectionBlock(
        badge = "STEP 2 • DATABASE SCHEMA",
        title = "Database Schema (डेटाबेस स्कीमा • UserProfile & PujaBooking)",
        subtitle = "Mobile OTP Auth + Registration Email ID + Booking & Commission Tables",
        icon = Icons.Default.Storage,
        points = listOf(
            "Table 1: user_profiles — Yajman aur Pandit Ji ki OTP-verified mobile profile, Email ID (registration form me), Gotra, Address, aur Pandit Ji ka Live Online/Offline toggle store karta hai.",
            "Table 2: puja_bookings — Har Puja booking ka complete record, Yajman Name/Phone/Email/Gotra, Muhurat slot, Samagri flag, Total Dakshina, 15% Platform Commission, 85% Pandit Payout aur Booking Status ('UPCOMING' -> 'COMPLETED') store karta hai."
        ),
        codeSnippet = """
// 1. user_profiles (Registration & Profile Schema)
{
  "userId": "mobile_9876543210",
  "name": "Rajesh Sharma",
  "phone": "9876543210",          // Primary Mobile OTP Login Key
  "email": "rajesh.sharma@vaidikpuja.org", // Captured in Registration Form
  "address": "108 Mahakal Marg, Ujjain, MP",
  "gotra": "कश्यप (Kashyap)",
  "role": "YAJMAN",               // Enum: "YAJMAN" | "PANDIT"
  "preferredPuja": "Bagalamukhi Havan Poojan",
  "specialInstructions": "पीली सरसों व हल्दी माला से हवन",
  "isPanditOnline": true,
  "otpVerified": true
}

// 2. puja_bookings (PujaBooking Schema)
{
  "id": "puja_9f8a7b6c5d4e",
  "userId": "mobile_9876543210",
  "yajmanName": "Rajesh Sharma",
  "yajmanPhone": "9876543210",
  "yajmanEmail": "rajesh.sharma@vaidikpuja.org",
  "yajmanAddress": "108 Mahakal Marg, Ujjain",
  "gotra": "कश्यप (Kashyap)",
  "pujaId": "bagalamukhi_havan",
  "pujaTitle": "Bagalamukhi Havan Poojan",
  "pujaDate": "2026-10-15",
  "timeSlot": "08:00 AM",
  "includeSamagri": true,
  "specialInstructions": "शत्रु बाधा निवारण संकल्प",
  "basePrice": 3100,
  "samagriPrice": 900,
  "totalAmount": 4000,
  "platformCommission": 600, // 15% Platform Fee
  "panditPayout": 3400,      // 85% Acharya Net Payout
  "paymentMethod": "UPI_GPAY",
  "paymentStatus": "PAID",
  "bookingStatus": "UPCOMING", // -> "COMPLETED"
  "panditName": "आचार्य विश्वनाथ शास्त्री"
}
        """.trimIndent()
    ),
    SpecSectionBlock(
        badge = "STEP 3 • COMMISSION MODEL",
        title = "Platform Commission & Payout Model (कमीशन व भुगतान गणित)",
        subtitle = "Transparent 85% Pandit Ji Payout & 15% Platform Service Fee",
        icon = Icons.Default.Calculate,
        points = listOf(
            "Base Puja Dakshina (उदा. माँ बगलामुखी हवन): ₹3,100",
            "Shuddh Havan Samagri Kit (पीली सरसों, हल्दी माला, गाय का घी, समिधा): + ₹900",
            "Gross Total Paid by Yajman (कुल राशि): ₹4,000 (UPI / Card / NetBanking)",
            "Platform Commission (15% Tech, Verification & Support Fee): ₹600",
            "Pandit Ji Net Payout (85% Direct Bank / UPI Settlement): ₹3,400",
            "Settlement Cycle: Puja 'COMPLETED' mark hote hi Pandit Ji ke dashboard me Completed Earnings me instant credit."
        )
    ),
    SpecSectionBlock(
        badge = "STEP 4 • UI DESIGN GUIDELINES",
        title = "Frontend UI/UX Design Guidelines (फ्रंटएंड डिज़ाइन गाइडलाइंस)",
        subtitle = "Sacred Vaidik Aesthetic + Modern Material 3 Accessibility",
        icon = Icons.Default.Palette,
        points = listOf(
            "1. Color Palette (वैदिक रंग संयोजन): Kumkum Maroon (#8B1E1E) primary trust ke liye, Kesariya Saffron (#D9531E) auspicious energy ke liye, Pitambara Haldi Gold (#F59E0B) Maa Bagalamukhi aur highlights ke liye, aur Chandan Cream (#FFFBF5) soothing background ke liye.",
            "2. Typography Pairing (फॉन्ट सिस्टम): Headings ke liye 'Playfair Display' (Royal Serif look) aur Hindi/Hinglish body text, forms aur price tables ke liye 'Poppins' (clean readable sans-serif).",
            "3. Dual-Language Clarity (हिंदी + Hinglish): Sabhi labels me Devnagari Hindi ke saath English/Hinglish terms diye gaye hain (उदा. 'ईमेल आईडी / Email ID', 'गोत्र / Gotra', 'मुहूर्त समय / Time Slot') taaki har umra ke Yajman aur Pandit Ji aasani se use kar sakein.",
            "4. One-Tap Action Affordances: Login par सीधा Mobile OTP flow, Home screen par prominent 'पूजा बुक करें (Book Puja)' CTA, aur Pandit Dashboard par har booking card me 48dp+ touch target wala हरा 'Call Yajman (यजमान को कॉल करें)' button."
        )
    )
)

@Composable
fun SpecsAndGuidelinesScreen(
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = KumkumMaroon)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(PitambaraGold),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Architecture,
                                contentDescription = "Architecture Blueprint",
                                tint = KumkumDark
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "APP SPECIFICATIONS & DESIGN STRUCTURE",
                                style = MaterialTheme.typography.labelSmall,
                                color = PitambaraLight
                            )
                            Text(
                                text = "संपूर्ण ऐप ब्लूप्रिंट, डेटाबेस स्कीमा व UI गाइड (Hindi/Hinglish)",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "यहाँ यजमान (Devotee) और पंडित जी (Acharya) पूजा बुकिंग ऐप का मोबाइल OTP लॉगिन लॉजिक, डेटाबेस स्कीमा, कमीशन मॉडल और UI डिज़ाइन गाइडलाइंस विस्तार से दिए गए हैं।",
                        style = MaterialTheme.typography.bodyMedium,
                        color = PitambaraLight
                    )
                }
            }
        }

        items(hinglishSpecSections) { section ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = section.badge,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = section.icon,
                            contentDescription = section.title,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = section.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = section.subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        section.points.forEach { point ->
                            Text(
                                text = point,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    if (section.codeSnippet != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            color = Color(0xFF1E1210),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = section.codeSnippet,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = PitambaraLight,
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
