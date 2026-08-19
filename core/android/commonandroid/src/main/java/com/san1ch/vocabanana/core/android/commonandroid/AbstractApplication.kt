package com.san1ch.vocabanana.core.android.commonandroid

import android.app.Application
import com.san1ch.vocabanana.core.essentials.exceptions.mapper.DefaultExceptionToMessageMapper
import com.san1ch.vocabanana.core.essentials.exceptions.mapper.ExceptionToMessageMapper
import timber.log.Timber
import javax.inject.Inject

abstract class AbstractApplication : Application() {
    @Inject
    lateinit var exceptionToMessageMapper: DefaultExceptionToMessageMapper

    override fun onCreate() {
        super.onCreate()
        ExceptionToMessageMapper.setInstance(exceptionToMessageMapper)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
