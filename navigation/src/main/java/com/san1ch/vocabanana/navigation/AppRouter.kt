package com.san1ch.vocabanana.navigation

interface AppRouter{
    fun navigateTo(route: Route)
    fun resetTo(route: Route)
    fun navigateBack()
}