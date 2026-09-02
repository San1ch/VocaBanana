package com.san1ch.vocabanana.core.essentials

interface Logger {
    fun d(message: String)
    fun d(tag: String, message: String)
    fun e(tag: String, message: String = "Error!", error: Throwable)
}
