package com.example.vocabanana.feature.<low_feature>.data

@ConsistentCopyVisibility
data class <feature>Domain private constructor(
    val id: Int,
){
    companion object {
        /*
         * Creates a <feature>Domain object with validation.
         * Use this for any data coming from users or external sources.
         * Returns [ValidateResult.Success] with <feature>Domain if valid,
         * or [ValidateResult.Error] if validation fails.
         */
        fun create(
            id: Int = 0
        ): ValidateResult<<feature>Domain, <feature>ValidateError> {

        }
        /*
         * Creates a <feature>Domain object without validation.
         * Use this only when you are 100% sure the data is already valid.
         * Faster than [create], but unsafe if the data might be invalid.
         */
        fun unsafeCreate(
            id: Int = 0
        ): <feature>Domain {

        }
    }
}



sealed class <feature>ValidateError {

}
