package com.san1ch.vocabanana.core.essentials.database.model



/**
 *  An object that contains words and sentence
 *  That needs for AI processing where I need to send words with context
 *  If it is sent without context, word might be identified incorrectly
 */
data class SentenceWithItsWords(
    val words: List<String>,
    val sentence: String
){
    fun wordsCount(): Int {
        return words.size
    }
}