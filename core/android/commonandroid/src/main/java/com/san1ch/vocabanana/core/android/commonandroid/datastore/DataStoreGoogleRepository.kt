package com.san1ch.vocabanana.core.android.commonandroid.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.san1ch.vocabanana.core.essentials.model.GoogleAuthState
import com.san1ch.vocabanana.core.essentials.repositories.GoogleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DataStoreGoogleRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : GoogleRepository {
    object Keys {
        val CLOUD_USER_EMAIL = stringPreferencesKey("cloud_user_email")
    }

    override fun getActiveAccountState(): Flow<GoogleAuthState> = dataStore.data.map {
        val email: String? = it[Keys.CLOUD_USER_EMAIL]
        if (!email.isNullOrEmpty()) {
            GoogleAuthState.SignedIn(email)
        } else {
            GoogleAuthState.SignedOut
        }
    }

    override suspend fun setActiveAccountState(state: GoogleAuthState) {
        when (state) {
            GoogleAuthState.SignedOut -> dataStore.edit { it[Keys.CLOUD_USER_EMAIL] = "" }
            is GoogleAuthState.SignedIn -> dataStore.edit { it[Keys.CLOUD_USER_EMAIL] = state.email }
        }
    }
}
