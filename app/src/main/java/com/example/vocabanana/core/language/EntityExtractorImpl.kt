package com.example.vocabanana.core.language

import com.google.mlkit.nl.entityextraction.Entity
import com.google.mlkit.nl.entityextraction.EntityExtraction
import com.google.mlkit.nl.entityextraction.EntityExtractionParams
import com.google.mlkit.nl.entityextraction.EntityExtractorOptions
import javax.inject.Inject


class EntityExtractorImpl @Inject constructor() {
    fun extract(text: String) {
        val entityExtractor = EntityExtraction.getClient(
            EntityExtractorOptions.Builder(EntityExtractorOptions.ENGLISH).build()
        )

        entityExtractor.downloadModelIfNeeded()
            .addOnSuccessListener {
                val params = EntityExtractionParams.Builder(text).build()
                entityExtractor.annotate(params)
                    .addOnSuccessListener { entityAnnotations ->
                        for (annotation in entityAnnotations) {
                            val entities = annotation.entities
                            for (entity in entities) {
                                // This is where you find your "Trash"
                                when (entity.type) {
                                    Entity.TYPE_URL -> {}
                                    Entity.TYPE_EMAIL -> {}
                                    Entity.TYPE_PHONE -> {}
                                    Entity.TYPE_ADDRESS -> {}
                                    Entity.TYPE_IBAN -> {}
                                    Entity.TYPE_DATE_TIME -> {}
                                    Entity.TYPE_FLIGHT_NUMBER -> {}
                                    Entity.TYPE_MONEY -> {}
                                    Entity.TYPE_PAYMENT_CARD -> {}
                                    Entity.TYPE_ISBN -> {}
                                    Entity.TYPE_TRACKING_NUMBER -> {}
                                }
                            }
                        }
                    }
            }
    }
}
