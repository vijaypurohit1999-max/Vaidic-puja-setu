package com.example.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.UserRole
import com.example.ui.theme.KesariyaSaffron
import com.example.ui.theme.KumkumDark
import com.example.ui.theme.KumkumMaroon
import com.example.ui.theme.PitambaraGold
import com.example.ui.theme.PitambaraLight
import com.example.ui.theme.SacredGreen

@Composable
fun AuthScreen(
    selectedRole: UserRole,
    onRoleSelected: (UserRole) -> Unit,
    onMobileOtpLoginSuccess: (phone: String, role: UserRole) -> Unit,
    onBypassToHome: () -> Unit = { onMobileOtpLoginSuccess("9876543210", selectedRole) }
) {
    var mobileNumber by rememberSaveable { mutableStateOf("9876543210") }
    var generatedOtp by rememberSaveable { mutableStateOf<String?>("1234") }
    var enteredOtp by rememberSaveable { mutableStateOf("1234") }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var otpInfoMessage by rememberSaveable {
        mutableStateOf<String?>("डेमो OTP तैयार है: 1234 (कोई भी 4-6 अंकों का OTP मान्य है)")
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.widthIn(max = 560.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Hero Devotional Header Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Image(
                            painter = painterResource(id = R.drawable.img_bagalamukhi_havan),
                            contentDescription = "माँ बगलामुखी पीताम्बरा हवन पूजन",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(190.dp),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(190.dp)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Black.copy(alpha = 0.25f),
                                            KumkumDark.copy(alpha = 0.90f)
                                        )
                                    )
                                )
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(18.dp)
                        ) {
                            Surface(
                                color = PitambaraGold,
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(
                                    text = "॥ ॐ ह्लीं बगलामुखि सर्वदुष्टानां वाचं मुखं पदं स्तम्भय ॥",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = KumkumDark,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Vaidik Puja • मोबाइल OTP लॉगिन",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.White
                            )
                            Text(
                                text = "यजमान (Devotees) एवं वैदिक पंडित जी के लिए सरल मोबाइल OTP प्रवेश",
                                style = MaterialTheme.typography.bodySmall,
                                color = PitambaraLight
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Step 1: Role Selector (Yajman vs Pandit Ji)
                Text(
                    text = "1. अपनी भूमिका चुनें (Select Your Role)",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    UserRole.entries.forEach { role ->
                        val isSelected = selectedRole == role
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .testTag("role_card_${role.code.lowercase()}")
                                .clickable {
                                    onRoleSelected(role)
                                    errorMessage = null
                                }
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                    shape = RoundedCornerShape(18.dp)
                                ),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.surface
                                }
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isSelected) MaterialTheme.colorScheme.primary
                                                else MaterialTheme.colorScheme.surfaceVariant
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (role == UserRole.YAJMAN) Icons.Default.Person else Icons.Default.SelfImprovement,
                                            contentDescription = role.titleHi,
                                            tint = if (isSelected) Color.White else MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Selected",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = if (role == UserRole.YAJMAN) "यजमान (Yajman)" else "पंडित जी (Pandit Ji)",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = role.subtitleHi,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Step 2: Dummy Mobile OTP Card (Zero System / Google SDK Calls)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhoneAndroid,
                                contentDescription = "Dummy Mobile OTP Login",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (selectedRole == UserRole.YAJMAN) {
                                        "2. यजमान मोबाइल OTP लॉगिन (Dummy OTP)"
                                    } else {
                                        "2. पंडित जी मोबाइल OTP लॉगिन (Dummy OTP)"
                                    },
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "मोबाइल नंबर और डेमो OTP (1234) से तुरंत प्रवेश करें",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = mobileNumber,
                            onValueChange = { input ->
                                mobileNumber = input.filter { it.isDigit() }.take(10)
                                errorMessage = null
                            },
                            label = { Text("मोबाइल नंबर (10-Digit Mobile Number)") },
                            prefix = { Text("+91 ", fontWeight = FontWeight.SemiBold) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.PhoneAndroid,
                                    contentDescription = null
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_mobile_input")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                val cleaned = mobileNumber.trim().ifEmpty { "9876543210" }
                                mobileNumber = cleaned
                                generatedOtp = "1234"
                                enteredOtp = "1234"
                                errorMessage = null
                                otpInfoMessage = "+91-$cleaned पर डेमो OTP भेजा गया है: 1234"
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("send_login_otp_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = KesariyaSaffron,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sms,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "डेमो OTP प्राप्त करें (Get Dummy OTP: 1234)",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }

                        AnimatedVisibility(visible = generatedOtp != null) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (otpInfoMessage != null) {
                                    Surface(
                                        color = PitambaraLight,
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = KumkumMaroon,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = otpInfoMessage ?: "",
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.SemiBold,
                                                color = KumkumDark
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                }

                                OutlinedTextField(
                                    value = enteredOtp,
                                    onValueChange = { input ->
                                        enteredOtp = input.filter { it.isDigit() }.take(6)
                                        errorMessage = null
                                    },
                                    label = { Text("डेमो OTP दर्ज करें (Enter Dummy OTP: 1234)") },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = null
                                        )
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("login_otp_input")
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                Button(
                                    onClick = {
                                        val cleanedPhone = mobileNumber.trim().ifEmpty { "9876543210" }
                                        onMobileOtpLoginSuccess(cleanedPhone, selectedRole)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(54.dp)
                                        .testTag("verify_otp_login_button"),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = SacredGreen,
                                        contentColor = Color.White
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VerifiedUser,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "OTP सत्यापित करें व आगे बढ़ें (${if (selectedRole == UserRole.YAJMAN) "यजमान" else "पंडित जी"})",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedButton(
                            onClick = onBypassToHome,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("bypass_login_home_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("सीधे होम स्क्रीन पर जाएं (Skip / Go to Home)")
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        if (errorMessage != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.errorContainer,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = errorMessage ?: "",
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(12.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
