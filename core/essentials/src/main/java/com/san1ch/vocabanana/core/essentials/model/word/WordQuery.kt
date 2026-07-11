package com.san1ch.vocabanana.core.essentials.model.word

/**
 * Represents a query configuration for retrieving words from the database.
 * Each field supports advanced filtering using [FilterType].
 */
data class WordQuery(
    /** Filter by the learning progress state of the word. Defaults to [FilterType.All]. */
    val states: FilterType<WordState> = FilterType.All,

    /** Filter words based on their association with specific texts. Defaults to [FilterType.All]. */
    val textIds: FilterType<Int> = FilterType.All,

    /** Direct filter for specific word IDs. Defaults to [FilterType.All]. */
    val wordIds: FilterType<Int> = FilterType.All
)

/**
 * Defines a generic filtering strategy for query parameters.
 * Allows specifying inclusive or exclusive sets of items.
 */
sealed interface FilterType<out T> {
    /** Represents no filter (retrieve all items). */
    object All : FilterType<Nothing>

    /** Include only the provided items in the result set. */
    data class Include<T>(val items: List<T>) : FilterType<T>

    /** Include all items except the ones provided in this list. */
    data class Exclude<T>(val items: List<T>) : FilterType<T>
}