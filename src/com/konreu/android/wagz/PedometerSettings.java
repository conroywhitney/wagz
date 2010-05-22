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

package com.konreu.android.wagz;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Wrapper for {@link SharedPreferences}, handles preferences-related tasks.
 * @author Conroy Whitney
 */
public class PedometerSettings {
	private final String TAG = "PedometerSettings";

	private final String SETTING_STEP_LENGTH = "step_length";
	private final String SETTING_WALK_LENGTH = "walk_length";
	private final String SETTING_SENSITIVITY = "sensitivity";
	private final String SETTING_WALK_PERCENT_COMPLETE = "walk_percent_complete";
	private final String SETTING_WALK_FREQUENCY = "walk_frequency";
	private final String SETTING_DOG_NAME = "dog_name";
	private final String SETTING_SHOULD_SET_REMINDER = "reminder_on";
	private final String SETTING_REMINDER_TIME = "preference_time";
	
	private final float DEFAULT_STEP_LENGTH = (float) 20.0;		// 20cm step
	private final int DEFAULT_WALK_LENGTH = 15;		// 15 min
	private final int DEFAULT_SENSITIVITY = 30;		// medium
	private final int DEFAULT_WALK_PERCENT_COMPLETE = 90;	// 90% complete
	private final int DEFAULT_WALK_FREQUENCY = 1440;	// once a day
	private final String DEFAULT_DOG_NAME = "Wagz";
	private final boolean DEFAULT_SHOULD_SET_REMINDER = false;
	private final String DEFAULT_REMINDER_TIME = "18:00";
	
    SharedPreferences mSettings;
    
    private static PedometerSettings instance;
    
    public static PedometerSettings getInstance(Context c) {
    	if (instance == null) {
    		instance = new PedometerSettings(c);
    	}
    	return instance;
    }
    
    public static void reloadSettings(Context c) {
    	instance = new PedometerSettings(c);
    }
    
    public PedometerSettings(Context c) {
        mSettings = PreferenceManager.getDefaultSharedPreferences(c);
    }
        
    public boolean isMetric() {
        return mSettings.getString("units", "imperial").equals("metric");
    }
    
    public float getStepLength() {
    	return getFloat(SETTING_STEP_LENGTH, DEFAULT_STEP_LENGTH);
    }
    
    public int getWalkLength() {
    	return getInt(SETTING_WALK_LENGTH, DEFAULT_WALK_LENGTH);
    }
    
    public int getSensitivity() {
    	return getInt(SETTING_SENSITIVITY, DEFAULT_SENSITIVITY);
    }
    
    public int getWalkPercentComplete() {
    	return getInt(SETTING_WALK_PERCENT_COMPLETE, DEFAULT_WALK_PERCENT_COMPLETE);
    }
    
    public int getWalkFrequency() {
    	return getInt(SETTING_WALK_FREQUENCY, DEFAULT_WALK_FREQUENCY);
    }
    
    public String getDogName() {
    	return getString(SETTING_DOG_NAME, DEFAULT_DOG_NAME);
    }
    
    public boolean shouldSetReminder() {
    	return mSettings.getBoolean(SETTING_SHOULD_SET_REMINDER, DEFAULT_SHOULD_SET_REMINDER);
    }
    
    public String getReminderTime() {
    	return getString(SETTING_REMINDER_TIME, DEFAULT_REMINDER_TIME);
    }
    
    public String getString(String sSettingKey, String sDefaultVal) {
    	return mSettings.getString(sSettingKey, sDefaultVal);  	
    }
    
//    public boolean getBoolean(String sSettingKey, boolean bDefaultVal) {
//    	try {
//    		return Boolean.valueOf(mSettings.getString(sSettingKey, Boolean.toString(bDefaultVal)));
//    	} catch (NumberFormatException nfe) {
//    		Log.e(TAG + ".getBoolean", "error getting boolean preference " + sSettingKey + ": " + nfe.getMessage());
//    		return bDefaultVal;
//    	}    	
//    }    
    
    public int getInt(String sSettingKey, int iDefaultVal) {
    	try {
    		return Integer.valueOf(mSettings.getString(sSettingKey, Integer.toString(iDefaultVal)));
    	} catch (NumberFormatException nfe) {
    		Log.e(TAG + ".getInt", "error getting int preference " + sSettingKey + ": " + nfe.getMessage());
    		return iDefaultVal;
    	}    	
    }
    
    public float getFloat(String sSettingKey, float fDefaultVal) {
        try {
            return Float.valueOf(mSettings.getString(sSettingKey, Float.toString(fDefaultVal)).trim());
        } catch (NumberFormatException nfe) {
        	Log.e(TAG + ".getFloat", "error getting float preference " + sSettingKey + ": " + nfe.getMessage());
            return fDefaultVal;
        }
    }    
    
}
