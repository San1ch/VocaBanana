package com.san1ch.vocabanana.core.essentials.model

sealed class GoogleAuthState {
    class SignedIn(val email: String) : GoogleAuthState()
    object SignedOut : GoogleAuthState()

    fun fold(onSignedIn: (SignedIn) -> Unit, onSignedOut: () -> Unit) = when (this) {
        is SignedIn -> onSignedIn(this)
        is SignedOut -> onSignedOut()
    }
}
