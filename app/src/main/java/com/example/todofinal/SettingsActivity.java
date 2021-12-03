package com.example.todofinal;

import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

/**
 * Settings activity where user can change username, enable dark mode and change color theme (WIP)
 * @author Jennifer McCarthy
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Inner class with settings fragments
     */
    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            changeLightMode();
            changeUsername();
            changeTheme();
        }

        /**
         * Changes to dark mode if light mode is enabled and vice versa
         */
        public void changeLightMode(){
            SwitchPreferenceCompat darkModeSwitch = findPreference("darkmode");
            if (darkModeSwitch != null) {
                darkModeSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
                    boolean isNightModeOn = (Boolean) newValue;
                    if (isNightModeOn){
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    }
                    return true;
                });
            }
        }

        /**
         * Changes the users local username
         */
        public void changeUsername(){
            EditTextPreference username = findPreference("signature");
            if (username != null) {
                username.setOnPreferenceChangeListener((preference, newValue) -> true);
            }
        }

        /**
         * Changes color theme (WIP)
         */
        public void changeTheme(){
            ListPreference themePreference = findPreference("theme");
            if (themePreference != null) {
                themePreference.setOnPreferenceChangeListener((preference, newValue) -> true);
            }
        }
    }
}