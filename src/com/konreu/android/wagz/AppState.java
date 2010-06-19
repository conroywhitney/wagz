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
	
	private final String STATE_IS_FIRST_TIME = "is_first_time";	
	private final String STATE_DISTANCE = "distance";
	private final String STATE_ELAPSED_TIME = "elapsed_time";
	private final String STATE_LAST_UPDATE_LOYALTY_DATE = "last_walk_date";
	private final String STATE_LOYALTY = "loyalty";
	
	private final boolean DEFAULT_IS_FIRST_TIME = true;
	private final float DEFAULT_DISTANCE = (float) 0.0;
	private final long DEFAULT_ELAPSED_TIME = 0;
	private final long DEFAULT_LAST_UPDATE_LOYTALTY_DATE = System.currentTimeMillis();
	private final int DEFAULT_LOYALTY = 3;
	
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
    
    public boolean isFirstTime() {
    	boolean bReturn = mState.getBoolean(STATE_IS_FIRST_TIME, DEFAULT_IS_FIRST_TIME);
    	mStateEditor.putBoolean(STATE_IS_FIRST_TIME, false);
    	this.commit();
    	return bReturn;
    }
        
    public float getDistance() {
    	return mState.getFloat(STATE_DISTANCE, DEFAULT_DISTANCE);
    }
    public void setDistance(float distance) {
    	Log.v(TAG, "setting distance: " + distance);
    	mStateEditor.putFloat(STATE_DISTANCE, distance);
    	this.commit();
    }
    
    public long getElapsedTime() {
    	return mState.getLong(STATE_ELAPSED_TIME, DEFAULT_ELAPSED_TIME);
    }
    public void setElapsedTime(long elapsedTime) {
    	Log.v(TAG, "setting elapsed time: " + elapsedTime);
    	mStateEditor.putLong(STATE_ELAPSED_TIME, elapsedTime);
    	this.commit();
    }
    
    public long getLastUpdateLoyaltyDate() {
    	return mState.getLong(STATE_LAST_UPDATE_LOYALTY_DATE, DEFAULT_LAST_UPDATE_LOYTALTY_DATE);
    }
    public void setLastLoyaltyUpdateDate(long dateMS) {
    	Log.v(TAG, "setting last walking date: " + dateMS);
    	mStateEditor.putLong(STATE_LAST_UPDATE_LOYALTY_DATE, dateMS);
    	this.commit();
    }
    
    public int getLoyalty() {
    	return mState.getInt(STATE_LOYALTY, DEFAULT_LOYALTY);
    }
    public void setLoyalty(int loyalty) {
    	Log.v(TAG, "setting loyalty: " + loyalty);
    	mStateEditor.putInt(STATE_LOYALTY, loyalty);
    	mStateEditor.commit();
    }
    
    private void commit() {
    	Log.i(TAG, "committing");
    	mStateEditor.commit();
    }
    
}
