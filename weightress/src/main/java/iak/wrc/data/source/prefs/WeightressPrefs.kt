package iak.wrc.data.source.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import timber.log.Timber
import java.lang.Exception

class WeightressPrefs private constructor(context: Context) {

    private val preferences: SharedPreferences
    private val editor: SharedPreferences.Editor

    private val prefName = "wpf"

    companion object {
        @Volatile
        private var instance: WeightressPrefs? = null

        fun instance(context: Context): WeightressPrefs {
            return instance ?: synchronized(this) {
                instance ?: WeightressPrefs(context).also { instance = it }
            }
        }
    }

    init {
        val masterKey = MasterKey.Builder(context.applicationContext)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        preferences = EncryptedSharedPreferences.create(
            context.applicationContext,
            prefName,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        editor = preferences.edit()
    }

    private val keyFirstNotificationShown = "fns"

    var isFirstNotificationShown: Boolean
        get() {
            return try {
                preferences.getBoolean(keyFirstNotificationShown, false)
            } catch (e: Exception) {
                Timber.e(e)
                false
            }
        }
        set(value) {
            try {
                editor.putBoolean(keyFirstNotificationShown, value).apply()
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
}