package com.example.vocabanana.core.resource

import android.annotation.SuppressLint
import android.content.Context
import javax.inject.Inject

class AndroidStringProvider @Inject constructor(private val context: Context) : StringProvider {

    @SuppressLint("DiscouragedApi")
    override fun getString(resId: String): String {
        return context.getString(
            context.resources.getIdentifier(
                resId,
                "string",
                context.packageName
            )
        )
    }
}