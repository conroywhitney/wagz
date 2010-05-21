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
import android.content.SharedPreferences.Editor;
import android.util.Log;

/**
 * Wrapper for {@link SharedPreferences}, handles preferences-related tasks.
 * @author Conroy Whitney
 */
public class AppState {
	private final String TAG = "AppState";

	private final String PREFERENCE_KEY = "state";
	
	private final String STATE_DISTANCE = "distance";
	private final String STATE_ELAPSED_TIME = "elapsed_time";
	private final String STATE_LAST_WALK_DATE = "last_walk_date";
	private final String STATE_LOYALTY = "loyalty";
	
	private final float DEFAULT_DISTANCE = (float) 0.0;
	private final long DEFAULT_ELAPSED_TIME = 0;
	private final long DEFAULT_LAST_WALK_DATE = System.currentTimeMillis();
	private final int DEFAULT_LOYALTY = 1;
	
    SharedPreferences mState;
    Editor mStateEditor;
    
    private static AppState instance;
    
    private AppState(Context c) {
    	mState = c.getSharedPreferences(PREFERENCE_KEY, 0);
    	mStateEditor = mState.edit();
    }
    
    public static AppState getInstance(Context c) {
    	if (instance == null) {
    		instance = new AppState(c);
    	}
    	return instance;
    }
    
    public void clear() {
    	mStateEditor.remove(STATE_DISTANCE);
    	mStateEditor.remove(STATE_ELAPSED_TIME);
    	Log.i(TAG, "clearing state");
    	this.commit();
    }
        
    public float getDistance() {
//        try {
//            return Float.valueOf(mState.getString(STATE_DISTANCE, Float.toString(DEFAULT_DISTANCE)).trim());
//        } catch (NumberFormatException nfe) {
//        	Log.e(TAG, "error getting state " + STATE_DISTANCE + ": " + nfe.getMessage());
//            return DEFAULT_DISTANCE;
//        }
    	return mState.getFloat(STATE_DISTANCE, DEFAULT_DISTANCE);
    }
    public void setDistance(float distance) {
    	mStateEditor.putFloat(STATE_DISTANCE, distance);
    	this.commit();
    }
    
    public long getElapsedTime() {
//    	try {
//    		return Long.valueOf(mState.getString(STATE_ELAPSED_TIME, Long.toString(DEFAULT_ELAPSED_TIME)));
//    	} catch (NumberFormatException nfe) {
//    		Log.e(TAG, "error getting preference " + STATE_ELAPSED_TIME + ": " + nfe.getMessage());
//    		return DEFAULT_ELAPSED_TIME;
//    	}
    	return mState.getLong(STATE_ELAPSED_TIME, DEFAULT_ELAPSED_TIME);
    }
    public void setElapsedTime(long elapsedTime) {
    	mStateEditor.putLong(STATE_ELAPSED_TIME, elapsedTime);
    	this.commit();
    }
    
    public long getLastWalkDate() {
//    	try {
//    		return Long.valueOf(mState.getString(STATE_LAST_WALK_DATE, Long.toString(DEFAULT_LAST_WALK_DATE)));
//    	} catch (NumberFormatException nfe) {
//    		Log.e(TAG, "error getting preference " + STATE_LAST_WALK_DATE + ": " + nfe.getMessage());
//    		return DEFAULT_LAST_WALK_DATE;
//    	}
    	return mState.getLong(STATE_LAST_WALK_DATE, DEFAULT_LAST_WALK_DATE);
    }
    public void setLastWalkDate(long dateMS) {
    	mStateEditor.putLong(STATE_LAST_WALK_DATE, dateMS);
    	this.commit();
    }
    
    public int getLoyalty() {
//    	try {
//    		return Integer.valueOf(mState.getString(STATE_LOYALTY, Integer.toString(DEFAULT_LOYALTY)));
//    	} catch (NumberFormatException nfe) {
//    		Log.e(TAG, "error getting preference " + STATE_LOYALTY + ": " + nfe.getMessage());
//    		return DEFAULT_LOYALTY;
//    	}
    	return mState.getInt(STATE_LOYALTY, DEFAULT_LOYALTY);
    }
    public void setLoyalty(int loyalty) {
    	mStateEditor.putInt(STATE_LOYALTY, loyalty);
    	mStateEditor.commit();
    }
    
    private void commit() {
    	Log.i(TAG, "committing");
    	mStateEditor.commit();
    }
    
}
