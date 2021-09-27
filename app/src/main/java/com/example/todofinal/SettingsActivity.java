package com.example.todofinal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import com.google.android.material.appbar.AppBarLayout;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

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

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            SwitchPreferenceCompat darkModeSwitch = findPreference("darkmode");
            if (darkModeSwitch != null) {
                darkModeSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        boolean isNightModeOn = (Boolean) newValue;
                        if (isNightModeOn){
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        } else {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        }
                        return true;
                    }
                });
            }

            EditTextPreference username = findPreference("signature");
            if (username != null) {
                username.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        sendUsername(username.getText());
                        return true;
                    }
                });
            }
            //ha metoden som skickar värdet utanför klassen static
//            String u = username.getText();
//            Intent intent = new Intent(getContext(), MainActivity.class);
//            intent.putExtra("USERNAME", u);
//            startActivity(intent);


            ListPreference themePreference = findPreference("theme");
            themePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    testar(newValue.toString());
                    return true;
                }
            });
        }

        public void sendUsername(String user){
            Intent intent = new Intent(getContext(), MainActivity.class);
            intent.putExtra("USERNAME", user);
            startActivity(intent);
        }

        public void testar(String color){
            Intent intent = new Intent(getContext(), MainActivity.class);
            intent.putExtra("THEME_COLOR", color);
            startActivity(intent);
        }
    }
}