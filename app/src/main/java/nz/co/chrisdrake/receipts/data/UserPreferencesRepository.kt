package nz.co.chrisdrake.receipts.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(
    private val context: Context,
    private val dataStore: DataStore<Preferences> = context.dataStore,
) {
    private val lastSyncTimeKey = longPreferencesKey("last_sync_time")

    fun getLastSyncTime(): Flow<Long> = dataStore.data.map { preferences ->
        preferences[lastSyncTimeKey] ?: 0L
    }

    suspend fun saveLastSyncTime(timestamp: Long) {
        dataStore.edit { preferences ->
            preferences[lastSyncTimeKey] = timestamp
        }
    }
}
