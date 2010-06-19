/*
 *  Wagz - Android App
 *  Copyright (C) 2010 Konreu (Conroy Whitney)
 *  Based on the Pedometer Android App by Levente Bagi (http://code.google.com/p/pedometer/)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.konreu.android.wagz.activities;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;

import com.konreu.android.wagz.AppState;
import com.konreu.android.wagz.Dog;
import com.konreu.android.wagz.PedometerSettings;
import com.konreu.android.wagz.R;
import com.konreu.android.wagz.WagzApp;

/**
 * Activity for Pedometer settings.
 * Started when the user click Settings from the main menu.
 * @author Conroy Whitney
 */
public class Settingz extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	private static final String TAG = "Settingz";
	
	 static final int TIME_DIALOG_ID = 0;
	 public final static String SHOULD_SHOW_INTRO = "SHOULD_SHOW_INTRO";
	 
	 private boolean mShouldShowIntro = false;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        addPreferencesFromResource(R.xml.preferences);
        
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
        	mShouldShowIntro = extras.getBoolean(SHOULD_SHOW_INTRO);
        }
        
        if (mShouldShowIntro) {
        	new AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle(R.string.dialog_title_choose_settings)
            .setMessage(R.string.dialog_message_choose_settings)
            .setIcon(android.R.drawable.ic_dialog_info)
            .setPositiveButton(R.string.ok, null)
            .show();
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
    	getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }
    
    @Override
    protected void onPause() {
        super.onPause();       
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);    
    }

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		// Clear everything if they changed a preference ...
		Log.v(TAG + "onSharedPreferenceChanged", "resetting AppState and Dog");
		AppState.getInstance(this).clear();
		Dog.resetInstance(this);
		
		Log.v(TAG, PedometerSettings.getInstance(this).getReminderTime());
		
		try {
			((WagzApp) this.getApplication()).updateAlarm();	
		} catch (Exception e) {
			Log.e(TAG + "onSharedPreferenceChanged", "error updating alarm");			
		}
		
	}
}
