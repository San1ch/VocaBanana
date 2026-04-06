package com.example.vocabanana.core.utilities.logs

interface Logger {
    fun d(message: String, tag: String? = null)
    fun i(message: String, tag: String? = null)
    fun w(message: String, tag: String? = null)
    fun e(throwable: Throwable? = null, message: String? = null, tag: String? = null)
}