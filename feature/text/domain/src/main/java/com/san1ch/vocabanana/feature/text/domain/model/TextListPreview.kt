package com.san1ch.vocabanana.feature.text.domain.model

import com.san1ch.vocabanana.core.essentials.model.text.TextPreview

data class TextListPreview(
    val textPreview: TextPreview,
    val lastReadTime: Long,
) {
    val id: Int = textPreview.id
    val title: String = textPreview.name
}
