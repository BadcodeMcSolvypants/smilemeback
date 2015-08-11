/*
 This file is part of SmileMeBack.

 SmileMeBack is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 SmileMeBack is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with SmileMeBack.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.smilemeback.activities;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;

import com.smilemeback.R;

/**
 * Activity for displaying user preferences.
 */
public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        initiateDefaultSettings();
        addPreferencesFromResource(R.xml.settings);
    }

    protected void initiateDefaultSettings() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final String greeting = getString(R.string.prefs_show_greeting);
        final String show_lock = getString(R.string.prefs_show_lock_help);
        final String show_edit = getString(R.string.prefs_show_editmode);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean(greeting, prefs.getBoolean(greeting, true));
        edit.putBoolean(show_lock, prefs.getBoolean(show_lock, true));
        edit.putBoolean(show_edit, prefs.getBoolean(show_edit, true));
        if (!edit.commit()) {
            Log.d("Settings", "Could not commit default settings!");
        }
    }

    private void setupActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}