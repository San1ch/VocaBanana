package com.san1ch.vocabanana.core.essentials.exceptions.mapper

class EmptyExceptionToMessageMapper : ExceptionToMessageMapper {
    override fun getLocalizedMessage(exception: Exception): String = exception.message ?: "Unknown error"
}
