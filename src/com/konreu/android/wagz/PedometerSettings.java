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
	
	private final float DEFAULT_STEP_LENGTH = (float) 20.0;
	private final int DEFAULT_WALK_LENGTH = 15;
	private final int DEFAULT_SENSITIVITY = 30;
	
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
        try {
            return Float.valueOf(mSettings.getString(SETTING_STEP_LENGTH, Float.toString(DEFAULT_STEP_LENGTH)).trim());
        } catch (NumberFormatException nfe) {
        	Log.e(TAG, "error getting preference " + SETTING_STEP_LENGTH + ": " + nfe.getMessage());
            return DEFAULT_STEP_LENGTH;
        }
//        return mSettings.getFloat(SETTING_STEP_LENGTH, DEFAULT_STEP_LENGTH);
    }
    
    public int getWalkLength() {
    	try {
    		return Integer.valueOf(mSettings.getString(SETTING_WALK_LENGTH, Integer.toString(DEFAULT_WALK_LENGTH)));
    	} catch (NumberFormatException nfe) {
    		Log.e(TAG, "error getting preference " + SETTING_WALK_LENGTH + ": " + nfe.getMessage());
    		return DEFAULT_WALK_LENGTH;
    	}
//    	return mSettings.getInt(SETTING_WALK_LENGTH, DEFAULT_WALK_LENGTH);
    }
    
    public int getSensitivity() {
    	try {
    		return Integer.valueOf(mSettings.getString(SETTING_SENSITIVITY, Integer.toString(DEFAULT_SENSITIVITY)));
    	} catch (NumberFormatException nfe) {
    		Log.e(TAG, "error getting preference " + SETTING_SENSITIVITY + ": " + nfe.getMessage());
    		return DEFAULT_SENSITIVITY;
    	}
//    	return mSettings.getInt(SETTING_SENSITIVITY, DEFAULT_SENSITIVITY);
    }
    
}
