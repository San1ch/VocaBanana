package com.san1ch.vocabanana.core.essentials.exceptions.mapper

interface ExceptionToMessageMapper {
    fun getLocalizedMessage(exception: Exception): String

    companion object {
        private var instance: ExceptionToMessageMapper = EmptyExceptionToMessageMapper()

        fun getLocalizedMessage(exception: Exception): String {
            return instance.getLocalizedMessage(exception)
        }

        fun setInstance(instance: ExceptionToMessageMapper) {
            this.instance = instance
        }

    }
}