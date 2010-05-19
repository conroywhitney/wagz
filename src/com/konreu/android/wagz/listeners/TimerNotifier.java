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
 * Calculates and displays the approximate calories.  
 * @author Levente Bagi
 */
public class TimerNotifier implements StepListener {

    public interface Listener {
        public void valueChanged(float value);
        public void passValue();
    }
    private Listener mListener;
    
    PedometerSettings mSettings;

    public TimerNotifier(Listener listener, PedometerSettings settings) {
        reloadSettings();
    }

    public void reloadSettings() {
        notifyListener();
    }
    
    public void resetValues() {
//        mCalories = 0;
    }

    public void onStep() {
        notifyListener();
    }
    
    private void notifyListener() {
        mListener.valueChanged((float)0);
    }
    
    public void passValue() {
        
    }    

}

