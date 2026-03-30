package com.example.vocabanana.ui.composable

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AnimatedTitle(
    targetText: String,
    modifier: Modifier = Modifier,
) {
    AnimatedContent(
        targetState = targetText,
        transitionSpec = {
            if (targetState != initialState) {
                (slideInVertically { height -> height } + fadeIn(animationSpec = tween(300)))
                    .togetherWith(
                        slideOutVertically { height -> -height } + fadeOut(animationSpec = tween(300))
                    )
            } else {
                EnterTransition.None togetherWith ExitTransition.None
            }
        },
        modifier = modifier,
        label = "TitleAnimation"
    ) { textToDisplay ->
        Text(
            text = textToDisplay,
            maxLines = 1
        )
    }
}