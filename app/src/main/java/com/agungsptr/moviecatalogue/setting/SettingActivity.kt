package com.agungsptr.moviecatalogue.setting

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.agungsptr.moviecatalogue.R
import com.agungsptr.moviecatalogue.notification.NotificationReceiver

class SettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val notificationReceiver = NotificationReceiver()

            val btnLanguage =
                findPreference<Preference>(resources.getString(R.string.change_language))
            btnLanguage?.setOnPreferenceClickListener {
                val mIntent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(mIntent)
                true
            }

            val btnDaily = findPreference<Preference>(
                resources.getString(R.string.daily_remainder)
            )
            btnDaily?.setOnPreferenceChangeListener { _, newValue ->
                if (newValue == true) {
                    notificationReceiver.setRemainder(
                        requireContext(),
                        7,
                        0,
                        NotificationReceiver.ID_REMAINDER
                    )
                } else {
                    notificationReceiver.cancelRemainder(
                        requireContext(),
                        NotificationReceiver.ID_REMAINDER
                    )
                }
                true
            }

            val btnReleaseToday = findPreference<Preference>(
                resources.getString(R.string.release_today_remainder)
            )
            btnReleaseToday?.setOnPreferenceChangeListener { _, newValue ->
                if (newValue == true) {
                    notificationReceiver.setRemainder(
                        requireContext(),
                        8,
                        0,
                        NotificationReceiver.ID_RELEASE_TODAY
                    )
                } else {
                    notificationReceiver.cancelRemainder(
                        requireContext(),
                        NotificationReceiver.ID_RELEASE_TODAY
                    )
                }
                true
            }
        }
    }
}