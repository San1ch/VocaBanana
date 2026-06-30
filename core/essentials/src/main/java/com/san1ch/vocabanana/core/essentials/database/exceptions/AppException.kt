package com.san1ch.vocabanana.core.essentials.database.exceptions

abstract class AppException(
    override val message: String,
    cause: Throwable? = null
) : Exception(message, cause){
}

