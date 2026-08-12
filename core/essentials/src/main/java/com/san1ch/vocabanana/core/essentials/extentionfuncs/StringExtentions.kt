package com.san1ch.vocabanana.core.essentials.extentionfuncs

import kotlin.system.measureTimeMillis

/**
 * Converts a raw text string into a list of safe, manageable paragraphs.
 * It measures execution time in debug builds for performance tracking.
 */
fun String.toParagraphs(): List<String> {
    var finalParagraphs: List<String> = emptyList()

    // Measure execution time to make sure parsing massive texts doesn't lag
    val executionTime = measureTimeMillis {
        val rawParagraphs = this.split(Regex("\\n\\s*\\n"))
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        val paragraphsToProcess = if (rawParagraphs.size <= 1 && this.contains("\n")) {
            this.split("\n").map { it.trim() }.filter { it.isNotEmpty() }
        } else {
            rawParagraphs
        }

        val resultList = mutableListOf<String>()
        val maxParagraphLength = 700

        for (paragraph in paragraphsToProcess) {
            if (paragraph.length <= maxParagraphLength) {
                resultList.add(paragraph)
            } else {
                var remaining = paragraph
                while (remaining.length > maxParagraphLength) {
                    val sliceIndex = remaining.lastIndexOf(". ", maxParagraphLength)
                    val splitAt = if (sliceIndex in 200..maxParagraphLength) {
                        sliceIndex + 1
                    } else {
                        val spaceIndex = remaining.lastIndexOf(' ', maxParagraphLength)
                        if (spaceIndex > 0) spaceIndex else maxParagraphLength
                    }

                    resultList.add(remaining.take(splitAt).trim())
                    remaining = remaining.substring(splitAt).trim()
                }
                if (remaining.isNotEmpty()) {
                    resultList.add(remaining)
                }
            }
        }
        finalParagraphs = resultList
    }

    // Print or log performance metrics (Visible only in Logcat during debug)
    println("TEXT_PARSER: Parsed text into ${finalParagraphs.size} paragraphs in ${executionTime}ms")

    return finalParagraphs
}
