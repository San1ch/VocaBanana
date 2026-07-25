package com.san1ch.vocabanana.core.ui.compose

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedLockIcon(isLocked: Boolean, isSwipeAttempted: Boolean) {
    val targetColor = when {
        isSwipeAttempted -> Color.Red
        isLocked -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val animatedColor by animateColorAsState(
        targetValue = targetColor,
    )
    val iconPainter =
        rememberVectorPainter(if (isLocked) Icons.Default.Lock else Icons.Default.LockOpen)

    val scale by animateFloatAsState(
        targetValue = if (isSwipeAttempted) 1.5f else 1.0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 300f),
        label = "lock_scale",
    )
    Spacer(
        modifier = Modifier
            .size(24.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .drawWithCache {
                onDrawBehind {
                    with(iconPainter) {
                        draw(
                            size = size,
                            colorFilter = ColorFilter.tint(animatedColor),
                        )
                    }
                }
            },
    )
}

@Composable
fun AnimatedLockIconButton(
    isLocked: Boolean,
    isSwipeAttempted: Boolean,
    onLockClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onLockClick,
        modifier = modifier,
    ) {
        AnimatedLockIcon(
            isLocked = isLocked,
            isSwipeAttempted = isSwipeAttempted,
        )
    }
}
