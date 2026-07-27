package com.san1ch.vocabanana.core.android.commonandroid.logger

import com.san1ch.vocabanana.core.essentials.Logger
import timber.log.Timber
import javax.inject.Inject

class AndroidLogger @Inject constructor() : Logger {

    override fun d(message: String) {
        Timber.d(message)
    }

    override fun e(error: Throwable, message: String) {
        Timber.e(error, message)
    }
}