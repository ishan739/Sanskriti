package com.example.richculture.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.richculture.R

@Composable
fun LoginScreen(onLoginClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.ic_arts),
                contentDescription = "App Icon",
                modifier = Modifier.size(100.dp)
            )
            Spacer(Modifier.height(32.dp))
            Button(onClick = onLoginClick) {
                Text("Login with Google")
            }
            Spacer(Modifier.height(16.dp))
            Button(onClick = onLoginClick) {
                Text("Signup with Google")
            }
        }
    }
}
