package com.san1ch.vocabanana.core.essentials.database.resources

sealed class StringId {
    data class Android(val resId: Int): StringId()
    data class Named(val name: String): StringId()

}