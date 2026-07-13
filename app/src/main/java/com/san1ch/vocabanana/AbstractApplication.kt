package com.san1ch.vocabanana

import android.app.Application
import com.san1ch.vocabanana.core.essentials.exceptions.mapper.DefaultExceptionToMessageMapper
import com.san1ch.vocabanana.core.essentials.exceptions.mapper.ExceptionToMessageMapper
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class AbstractApplication : Application() {
    @Inject
    lateinit var exceptionToMessageMapper: DefaultExceptionToMessageMapper

    override fun onCreate() {
        super.onCreate()
        ExceptionToMessageMapper.setInstance(exceptionToMessageMapper)
    }
}
