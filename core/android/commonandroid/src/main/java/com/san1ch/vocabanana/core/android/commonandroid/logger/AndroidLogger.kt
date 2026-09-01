package com.san1ch.vocabanana.core.android.commonandroid.logger

import com.san1ch.vocabanana.core.essentials.Logger
import timber.log.Timber
import javax.inject.Inject

class AndroidLogger @Inject constructor() : Logger {

    override fun d(message: String) {
        Timber.d(message)
    }

    override fun d(tag: String, message: String) {
        Timber.d("$tag: $message")
    }

    override fun e(tag: String, message: String, error: Throwable) {
        Timber.e(error, message)
    }
}
