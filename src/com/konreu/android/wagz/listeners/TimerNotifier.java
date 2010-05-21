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

package com.konreu.android.wagz.listeners;

import com.konreu.android.wagz.PedometerSettings;
import com.konreu.android.wagz.listeners.StepListener;

/**
 * Calculates and displays the elapsed time.  
 * @author Conroy Whitney
 */
public class TimerNotifier implements StepListener {

	private final long TIME_RANGE_MIN = 250;	// 1/4 seconds
	private final long TIME_RANGE_MAX = 2000;	// 2 seconds
	
    public interface Listener {
        public void valueChanged(long value);
        public void passValue();
    }
    private Listener mListener;
    
    private long mElapsedTime = 0;
    private long mLastLogged = 0;
    
    PedometerSettings mSettings;

    public TimerNotifier(Listener listener, PedometerSettings settings) {
    	mListener = listener;
    	mSettings = settings;
        reloadSettings();
    }
    
    public void setElapsedTime(long elapsedTime) {
    	mElapsedTime = elapsedTime;
    	notifyListener();
    }

    public void reloadSettings() {
        notifyListener();
    }
    
    public void resetValues() {
    	mElapsedTime = 0;
    	mLastLogged = System.currentTimeMillis();
    }

    public void onStep() {
    	long timeSinceLastStep = System.currentTimeMillis() - mLastLogged;
    	if (timeSinceLastStep > TIME_RANGE_MIN && timeSinceLastStep < TIME_RANGE_MAX) {
    		mElapsedTime += timeSinceLastStep;
    	}
    	mLastLogged = System.currentTimeMillis();
        notifyListener();
    }
    
    private void notifyListener() {
        mListener.valueChanged(mElapsedTime);
    }
    
    public void passValue() {
        
    }    

}

