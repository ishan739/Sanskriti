package com.example.richculture.utility

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

const val ANIMATION_DURATION = 500 // 500ms for a smooth rotation

// Function to create the ENTER transition for the cube effect
fun cubeEnterTransition() = slideInHorizontally(
    initialOffsetX = { it },
    animationSpec = tween(ANIMATION_DURATION)
) + fadeIn(animationSpec = tween(ANIMATION_DURATION))

// Function to create the EXIT transition for the cube effect
fun cubeExitTransition() = slideOutHorizontally(
    targetOffsetX = { -it },
    animationSpec = tween(ANIMATION_DURATION)
) + fadeOut(animationSpec = tween(ANIMATION_DURATION))

// Function to create the POP ENTER transition (reverse of exit)
fun cubePopEnterTransition() = slideInHorizontally(
    initialOffsetX = { -it },
    animationSpec = tween(ANIMATION_DURATION)
) + fadeIn(animationSpec = tween(ANIMATION_DURATION))

// Function to create the POP EXIT transition (reverse of enter)
fun cubePopExitTransition() = slideOutHorizontally(
    targetOffsetX = { it },
    animationSpec = tween(ANIMATION_DURATION)
) + fadeOut(animationSpec = tween(ANIMATION_DURATION))

// Apply the 3D rotation effect based on the animation progress
fun Modifier.cubeAnimation(progress: Float, isEnter: Boolean): Modifier {
    return this.graphicsLayer {
        val rotation = if (isEnter) 90f * (1f - progress) else -90f * progress
        rotationY = rotation
        cameraDistance = 12f * density
    }
}
