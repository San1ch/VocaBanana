package com.san1ch.vocabanana.core.essentials.resources.network

interface GoogleApiStringProvider {
    val authSuccessMessage: String
    val signOutSuccessMessage: String
    val authErrorMessage: String
    fun getGoogleClientIdByBuildType(isDebug: Boolean): String
}
