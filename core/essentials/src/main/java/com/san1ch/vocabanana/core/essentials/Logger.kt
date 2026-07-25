package com.san1ch.vocabanana.core.essentials

interface Logger {
    fun d(message: String)
    fun e(error: Throwable, message: String  = "Error!")
}