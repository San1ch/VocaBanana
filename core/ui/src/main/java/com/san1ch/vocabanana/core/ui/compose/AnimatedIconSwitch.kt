package com.san1ch.vocabanana.core.ui.compose

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun AnimatedIconSwitch(
    checked: Boolean,
    firstIcon: ImageVector,
    secondIcon: ImageVector,
    onFirstClick: () -> Unit,
    onSecondClick: () -> Unit,
    modifier: Modifier = Modifier,
    firstTint: Color = LocalContentColor.current,
    secondTint: Color = LocalContentColor.current,
    firstContentDescription: String? = null,
    secondContentDescription: String? = null,
    animationDuration: Int = 300,
) {
    Box(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) {
                if (checked) onSecondClick() else onFirstClick()
            },
        contentAlignment = Alignment.Center,
    ) {
        val firstAlpha by animateFloatAsState(
            targetValue = if (!checked) 1f else 0f,
            animationSpec = tween(durationMillis = animationDuration),
            label = "FirstIconAlpha",
        )
        val firstRotation by animateFloatAsState(
            targetValue = if (!checked) 0f else 90f,
            animationSpec = tween(durationMillis = animationDuration),
            label = "FirstIconRotation",
        )

        val secondAlpha by animateFloatAsState(
            targetValue = if (checked) 1f else 0f,
            animationSpec = tween(durationMillis = animationDuration),
            label = "SecondIconAlpha",
        )
        val secondRotation by animateFloatAsState(
            targetValue = if (checked) 0f else -90f,
            animationSpec = tween(durationMillis = animationDuration),
            label = "SecondIconRotation",
        )

        if (firstAlpha > 0f) {
            Icon(
                imageVector = firstIcon,
                contentDescription = firstContentDescription,
                tint = firstTint,
                modifier = Modifier
                    .graphicsLayer {
                        alpha = firstAlpha
                        rotationZ = firstRotation
                    },
            )
        }

        if (secondAlpha > 0f) {
            Icon(
                imageVector = secondIcon,
                contentDescription = secondContentDescription,
                tint = secondTint,
                modifier = Modifier
                    .graphicsLayer {
                        alpha = secondAlpha
                        rotationZ = secondRotation
                    },
            )
        }
    }
}
