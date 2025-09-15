package com.example.richculture.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.richculture.R
import com.example.richculture.ViewModels.UserViewModel
import com.example.richculture.navigate.Screen
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

private enum class AuthState {
    SIGN_IN, SIGN_UP, FORGOT_PASSWORD
}

@Composable
fun AuthScreen(
    navController: NavController,
    userViewModel: UserViewModel = koinViewModel()
) {
    var authState by remember { mutableStateOf(AuthState.SIGN_IN) }
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }
    var apiError by remember { mutableStateOf<String?>(null) }
    var emailForReset by remember { mutableStateOf("") }

    val primaryColor = Color(0xFFF4511E)
    val backgroundColor = Color(0xFFFFF8F5)

    // --- OBSERVE VIEWMODEL STATE ---
    val currentUser by userViewModel.currentUser.collectAsState()
    val error by userViewModel.error.observeAsState()
    val otpSent by userViewModel.otpSent.observeAsState(false)
    val otpVerified by userViewModel.otpVerified.observeAsState(false)
    val passwordResetSuccess by userViewModel.passwordResetSuccess.observeAsState(false)


    // --- HANDLE NAVIGATION AND STATE CHANGES ---
    LaunchedEffect(authState) { apiError = null }

    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            isLoading = false
            Toast.makeText(context, "Welcome, ${currentUser?.name}!", Toast.LENGTH_SHORT).show()
            navController.navigate(Screen.Home.route) { popUpTo(Screen.Auth.route) { inclusive = true } }
        }
    }

    LaunchedEffect(error) {
        error?.let { isLoading = false; apiError = it }
    }

    // ✅ CRITICAL FIX: Stop loading indicator on success for OTP flow
    LaunchedEffect(otpSent) {
        if (otpSent == true) isLoading = false
    }
    LaunchedEffect(otpVerified) {
        if (otpVerified == true) isLoading = false
    }
    LaunchedEffect(passwordResetSuccess) {
        if (passwordResetSuccess == true) {
            isLoading = false
            Toast.makeText(context, "Password reset successfully! Please sign in.", Toast.LENGTH_LONG).show()
            authState = AuthState.SIGN_IN
            userViewModel.clearResetStates()
        }
    }


    Box(modifier = Modifier
        .fillMaxSize()
        .background(backgroundColor)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.5f))
            Box(
                contentAlignment = Alignment.Center
            ) {
                Image(painter = painterResource(id = R.drawable.ic_main),
                    contentDescription = "Logo",
                    modifier = Modifier.size(80.dp).clip(CircleShape).background(Color.White)
                    )
            }
            Spacer(modifier = Modifier.height(16.dp))

            AnimatedContent(
                targetState = authState,
                transitionSpec = {
                    (slideInVertically(tween(400)) { it } + fadeIn(tween(400))).togetherWith(slideOutVertically(tween(400)) { -it } + fadeOut(tween(400)))
                },
                label = "AuthFormAnimation"
            ) { state ->
                when (state) {
                    AuthState.SIGN_IN -> SignInForm(
                        onSignUpClick = { authState = AuthState.SIGN_UP },
                        onForgotPasswordClick = { authState = AuthState.FORGOT_PASSWORD },
                        primaryColor = primaryColor, isLoading = isLoading, apiError = apiError,
                        onSignIn = { email, password -> apiError = null; isLoading = true; userViewModel.login(email, password) }
                    )
                    AuthState.SIGN_UP -> SignUpForm(
                        onSignInClick = { authState = AuthState.SIGN_IN },
                        primaryColor = primaryColor, isLoading = isLoading, apiError = apiError,
                        onSignUp = { name, email, password -> apiError = null; isLoading = true; userViewModel.signup(name, email, password) }
                    )
                    AuthState.FORGOT_PASSWORD -> ForgotPasswordForm(
                        onBackToSignInClick = { authState = AuthState.SIGN_IN; userViewModel.clearResetStates() },
                        primaryColor = primaryColor, isLoading = isLoading, apiError = apiError,
                        otpSent = otpSent == true, otpVerified = otpVerified == true,
                        onSendOtp = { email -> apiError = null; isLoading = true; emailForReset = email; userViewModel.sendOtp(email) },
                        onVerifyOtp = { otp -> apiError = null; isLoading = true; userViewModel.verifyOtp(emailForReset, otp) },
                        onResetPassword = { newPassword -> apiError = null; isLoading = true; userViewModel.resetPassword(emailForReset, newPassword) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "Skip for now", color = Color.Gray, textDecoration = TextDecoration.Underline, modifier = Modifier.clickable {
                navController.navigate(Screen.Home.route) { popUpTo(Screen.Auth.route) { inclusive = true } }
            })
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "By joining, you agree to explore and respect our cultural heritage", fontSize = 12.sp, color = Color.Gray, textAlign = TextAlign.Center, modifier = Modifier.padding(bottom = 16.dp))
        }
    }
}

@Composable
fun SignInForm(
    onSignUpClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    primaryColor: Color,
    isLoading: Boolean,
    apiError: String?,
    onSignIn: (String, String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Welcome Back", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = primaryColor)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Sign in to continue your journey", fontSize = 16.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(40.dp))

        OutlinedTextField(value = email,
            onValueChange = { email = it },
            label = { Text("Enter your email") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedTextColor = Color.Black,
                focusedTextColor = Color.Black,
                focusedBorderColor = primaryColor, focusedLabelColor = primaryColor))
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = password,
            onValueChange = { password = it },
            label = { Text("Enter password") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true, visualTransformation = PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedTextColor = Color.Black,
                focusedTextColor = Color.Black,
                focusedBorderColor = primaryColor,
                focusedLabelColor = primaryColor))

        Text(text = "Forgot Password?", color = primaryColor, modifier = Modifier
            .align(Alignment.End)
            .padding(top = 8.dp)
            .clickable(onClick = onForgotPasswordClick), fontSize = 14.sp)
        Spacer(modifier = Modifier.height(24.dp))

        AnimatedVisibility(visible = apiError != null) { Text(text = "* $apiError" ?: "", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 8.dp), textAlign = TextAlign.Center) }

        OutlinedButton(
            onClick = { onSignIn(email, password) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp), shape = RoundedCornerShape(12.dp),
            border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(colors = listOf(primaryColor, primaryColor))),
            enabled = !isLoading
        ) {
            if (isLoading) { CircularProgressIndicator(modifier = Modifier.size(24.dp), color = primaryColor) } else { Text(text = "Sign In", color = primaryColor, fontSize = 18.sp) }
        }
        Spacer(modifier = Modifier.height(16.dp))
        AuthToggleText(nonClickableText = "Don't have an account? ", clickableText = "Sign Up", onClick = onSignUpClick)
    }
}


@Composable
fun ForgotPasswordForm(
    onBackToSignInClick: () -> Unit,
    primaryColor: Color,
    isLoading: Boolean,
    apiError: String?,
    otpSent: Boolean,
    otpVerified: Boolean,
    onSendOtp: (String) -> Unit,
    onVerifyOtp: (String) -> Unit,
    onResetPassword: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Reset Password", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = primaryColor)
        Spacer(modifier = Modifier.height(8.dp))

        AnimatedContent(targetState = when { !otpSent -> 0; !otpVerified -> 1; else -> 2 }, label = "ResetPasswordStep") { step ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                when (step) {
                    0 -> {
                        Text(text = "Enter your email to receive an OTP", fontSize = 16.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(40.dp))
                        OutlinedTextField(value = email,
                            onValueChange = { email = it },
                            label = { Text("Enter your email") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true, colors = OutlinedTextFieldDefaults.colors(
                                unfocusedTextColor = Color.Black,
                                focusedTextColor = Color.Black,
                                focusedBorderColor = primaryColor,
                                focusedLabelColor = primaryColor))
                        Spacer(modifier = Modifier.height(32.dp))
                        AuthButton(text = "Send OTP", isLoading = isLoading, primaryColor = primaryColor, onClick = { onSendOtp(email) })
                    }
                    1 -> {
                        Text(text = "An OTP has been sent to $email", fontSize = 16.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(40.dp))
                        OtpTextField(otpText = otp, onOtpTextChange = { value, _ -> otp = value })
                        Spacer(modifier = Modifier.height(32.dp))
                        AuthButton(text = "Verify OTP", isLoading = isLoading, primaryColor = primaryColor, onClick = { onVerifyOtp(otp) })
                    }
                    2 -> {
                        Text(text = "OTP verified. Set your new password.", fontSize = 16.sp, color = Color(0xFF388E3C))
                        Spacer(modifier = Modifier.height(40.dp))
                        OutlinedTextField(value = newPassword, onValueChange = { newPassword = it }, label = { Text("New Password") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true, visualTransformation = PasswordVisualTransformation(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor, focusedLabelColor = primaryColor))
                        Spacer(modifier = Modifier.height(32.dp))
                        AuthButton(text = "Reset Password", isLoading = isLoading, primaryColor = primaryColor, onClick = { onResetPassword(newPassword) })
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        AnimatedVisibility(visible = apiError != null) { Text(text = apiError ?: "", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 8.dp), textAlign = TextAlign.Center) }
        Spacer(modifier = Modifier.height(16.dp))
        AuthToggleText(nonClickableText = "Remembered your password? ", clickableText = "Sign In", onClick = onBackToSignInClick)
    }
}

// ✅ NEW: FULLY RESPONSIVE OTP TEXT FIELD
@Composable
fun OtpTextField(
    modifier: Modifier = Modifier,
    otpText: String,
    otpCount: Int = 6,
    onOtpTextChange: (String, Boolean) -> Unit
) {
    val scope = rememberCoroutineScope()
    val animatables = remember { List(otpCount) { Animatable(1f) } }

    LaunchedEffect(otpText) {
        if (otpText.length <= otpCount) {
            val index = otpText.length - 1
            if (index >= 0) {
                scope.launch {
                    animatables[index].animateTo(1.2f, tween(200))
                    animatables[index].animateTo(1f, tween(200))
                }
            }
        }
    }

    BasicTextField(
        modifier = modifier,
        value = otpText,
        onValueChange = {
            if (it.length <= otpCount) { onOtpTextChange.invoke(it, it.length == otpCount) }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        decorationBox = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(otpCount) { index ->
                    val char = otpText.getOrNull(index)?.toString() ?: ""
                    val isFocused = otpText.length == index
                    val boxModifier = if (isFocused) {
                        Modifier
                            .scale(animatables[index].value)
                            .border(2.dp, Color(0xFFF4511E), RoundedCornerShape(8.dp))
                    } else {
                        Modifier
                            .scale(animatables[index].value)
                            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f) // ✅ This makes the boxes flexible
                            .aspectRatio(1f) // ✅ This keeps them square
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .then(boxModifier),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = char, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    )
}

// --- Reusable button for auth forms ---
@Composable
fun AuthButton(text: String, isLoading: Boolean, primaryColor: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
        enabled = !isLoading
    ) {
        if (isLoading) { CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White) } else { Text(text = text, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold) }
    }
}

@Composable
fun SignUpForm(
    onSignInClick: () -> Unit,
    primaryColor: Color,
    isLoading: Boolean,
    apiError: String?, // ✅ Receive the error
    onSignUp: (String, String, String) -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Join Sanskriti", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = primaryColor)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Connect with India's heritage", fontSize = 16.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(40.dp))

        OutlinedTextField(
            value = fullName, onValueChange = { fullName = it }, label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor, focusedLabelColor = primaryColor, focusedTextColor = Color.Black, unfocusedTextColor = Color.Black)
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = email, onValueChange = { email = it }, label = { Text("Enter your email") },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryColor, focusedLabelColor = primaryColor, focusedTextColor = Color.Black, unfocusedTextColor = Color.Black)
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = confirmPassword.isNotEmpty() && confirmPassword != password
            },
            label = { Text("Create password") },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    val iconRes = if (passwordVisible) R.drawable.ic_eye_off else R.drawable.ic_eye
                    Icon(painter = painterResource(id = iconRes), contentDescription = if (passwordVisible) "Hide password" else "Show password")
                }
            },
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor, focusedLabelColor = primaryColor, focusedTextColor = Color.Black, unfocusedTextColor = Color.Black)
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                passwordError = confirmPassword != password
            },
            label = { Text("Confirm password") },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true,
            isError = passwordError,
            visualTransformation = PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedTextColor = Color.Black,
                focusedTextColor = Color.Black,
                focusedBorderColor = primaryColor,
                focusedLabelColor = primaryColor,
                errorBorderColor = Color.Red)
        )
        if (passwordError) {
            Text(text = "Passwords do not match", color = Color.Red, fontSize = 12.sp, modifier = Modifier.align(Alignment.Start))
        }
        Spacer(modifier = Modifier.height(16.dp))

        // ✅ Display API error message
        AnimatedVisibility(visible = apiError != null) {
            Text(text = "* $apiError" ?: "", color = Color.Red, modifier = Modifier.padding(bottom = 8.dp), textAlign = TextAlign.Center)
        }

        Button(
            onClick = { if (!passwordError) onSignUp(fullName, email, password) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(),
            enabled = !passwordError && !isLoading
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFFF7043),
                                Color(0xFFF44336)
                            )
                        ), shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text(text = "Sign Up", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        AuthToggleText(
            nonClickableText = "Already have an account? ",
            clickableText = "Sign In",
            onClick = onSignInClick
        )
    }
}

@Composable
private fun AuthToggleText(nonClickableText: String, clickableText: String, onClick: () -> Unit) {
    val annotatedString = buildAnnotatedString {
        withStyle(style = SpanStyle(color = Color.Gray, fontSize = 14.sp)) { append(nonClickableText) }
        pushStringAnnotation(tag = "action", annotation = "action")
        withStyle(style = SpanStyle(color = Color(0xFFF4511E), fontWeight = FontWeight.Bold, fontSize = 14.sp)) { append(clickableText) }
        pop()
    }
    ClickableText(text = annotatedString, onClick = { offset ->
        annotatedString.getStringAnnotations(tag = "action", start = offset, end = offset).firstOrNull()?.let { onClick() }
    })
}

