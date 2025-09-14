package com.example.richculture.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
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
import org.koin.androidx.compose.koinViewModel


@Composable
fun AuthScreen(
    navController: NavController,
    userViewModel: UserViewModel = koinViewModel()
) {
    var isSignUp by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // ✅ NEW: State to manage the loading indicator on buttons
    var isLoading by remember { mutableStateOf(false) }

    val primaryColor = Color(0xFFF4511E)
    val backgroundColor = Color(0xFFFFF8F5)

    // --- OBSERVE VIEWMODEL STATE ---
    val currentUser by userViewModel.currentUser.collectAsState()
    val error by userViewModel.error.observeAsState()

    // --- HANDLE NAVIGATION AND TOASTS ---
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            isLoading = false // Stop loading on success
            Toast.makeText(context, "Welcome, ${currentUser?.name}!", Toast.LENGTH_SHORT).show()
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Auth.route) { inclusive = true }
            }
        }
    }

    LaunchedEffect(error) {
        error?.let {
            isLoading = false // Stop loading on error
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.5f))

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Brush.verticalGradient(colors = listOf(Color(0xFFFFA726), primaryColor))),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_main),
                    contentDescription = "Logo",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            AnimatedContent(
                targetState = isSignUp,
                transitionSpec = {
                    if (targetState) {
                        (slideInVertically(tween(400)) { it } + fadeIn(tween(400)))
                            .togetherWith(slideOutVertically(tween(400)) { -it } + fadeOut(tween(400)))
                    } else {
                        (slideInVertically(tween(400)) { -it } + fadeIn(tween(400)))
                            .togetherWith(slideOutVertically(tween(400)) { it } + fadeOut(tween(400)))
                    }
                },
                label = "AuthFormAnimation"
            ) { isSigningUp ->
                if (isSigningUp) {
                    SignUpForm(
                        onSignInClick = { isSignUp = false },
                        primaryColor = primaryColor,
                        isLoading = isLoading, // ✅ Pass loading state
                        onSignUp = { name, email, password ->
                            isLoading = true // ✅ Start loading
                            userViewModel.signup(name, email, password)
                        }
                    )
                } else {
                    SignInForm(
                        onSignUpClick = { isSignUp = true },
                        primaryColor = primaryColor,
                        isLoading = isLoading, // ✅ Pass loading state
                        onSignIn = { email, password ->
                            isLoading = true // ✅ Start loading
                            userViewModel.login(email, password)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Skip for now",
                color = Color.Gray,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "By joining, you agree to explore and respect our cultural heritage",
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
fun SignInForm(
    onSignUpClick: () -> Unit,
    primaryColor: Color,
    isLoading: Boolean, // ✅ Receive loading state
    onSignIn: (String, String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Welcome Back", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = primaryColor)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Sign in to continue your journey", fontSize = 16.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(40.dp))

        OutlinedTextField(
            value = email, onValueChange = { email = it }, label = { Text("Enter your email") },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryColor,
                focusedLabelColor = primaryColor,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password, onValueChange = { password = it }, label = { Text("Enter password") },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryColor,
                focusedLabelColor = primaryColor,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedButton(
            onClick = { onSignIn(email, password) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(colors = listOf(primaryColor, primaryColor))),
            enabled = !isLoading // ✅ Disable button when loading
        ) {
            // ✅ Show indicator or text based on loading state
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = primaryColor
                )
            } else {
                Text(text = "Sign In", color = primaryColor, fontSize = 18.sp)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        AuthToggleText(
            nonClickableText = "Don't have an account? ",
            clickableText = "Sign Up",
            onClick = onSignUpClick
        )
    }
}

@Composable
fun SignUpForm(
    onSignInClick: () -> Unit,
    primaryColor: Color,
    isLoading: Boolean, // ✅ Receive loading state
    onSignUp: (String, String, String) -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
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
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor, focusedLabelColor = primaryColor, focusedTextColor = Color.Black, unfocusedTextColor = Color.Black)
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
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor, focusedLabelColor = primaryColor, errorBorderColor = Color.Red, focusedTextColor = Color.Black, unfocusedTextColor = Color.Black)
        )
        if (passwordError) {
            Text(text = "Passwords do not match", color = Color.Red, fontSize = 12.sp, modifier = Modifier.align(Alignment.Start))
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (!passwordError && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                    onSignUp(fullName, email, password)
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(),
            enabled = !passwordError && password.isNotEmpty() && confirmPassword.isNotEmpty() && !isLoading // ✅ Disable button when loading
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(colors = listOf(Color(0xFFFF7043), Color(0xFFF44336))),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                // ✅ Show indicator or text based on loading state
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
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
        withStyle(style = SpanStyle(color = Color.Gray, fontSize = 14.sp)) {
            append(nonClickableText)
        }
        pushStringAnnotation(tag = "action", annotation = "action")
        withStyle(style = SpanStyle(color = Color(0xFFF4511E), fontWeight = FontWeight.Bold, fontSize = 14.sp)) {
            append(clickableText)
        }
        pop()
    }

    ClickableText(
        text = annotatedString,
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = "action", start = offset, end = offset)
                .firstOrNull()?.let {
                    onClick()
                }
        }
    )
}

