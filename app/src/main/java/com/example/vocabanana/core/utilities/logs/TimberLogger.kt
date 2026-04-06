package com.example.vocabanana.core.utilities.logs

import timber.log.Timber
import javax.inject.Inject

class TimberLogger @Inject constructor() : Logger {

    override fun d(message: String, tag: String?) {
        prepare(tag).d(message)
    }

    override fun i(message: String, tag: String?) {
        prepare(tag).i(message)
    }

    override fun w(message: String, tag: String?) {
        prepare(tag).w(message)
    }

    override fun e(throwable: Throwable?, message: String?, tag: String?) {
        prepare(tag).e(throwable, message)
    }

    // if tag != null, use it, else use class name
    private fun prepare(tag: String?): Timber.Tree {
        return tag?.let { Timber.tag(it) } ?: Timber.asTree()
    }
}