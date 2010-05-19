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

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.konreu.android.wagz.R;
import com.konreu.android.wagz.StepService;

public class Wagz extends Activity {
	private static String TAG = "Wagz";	
        
    private StepService mService;

    private boolean isRunning() {
    	Log.v(TAG + ".isRunning", "StepService.isRunning = " + StepService.isRunning());
		return StepService.isRunning();
    }
        
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (this.isRunning()) {
        	bindStepService();	
        }
    }
    
    @Override
    protected void onPause() {
        if (this.isRunning()) {
            unbindStepService();
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    protected void onDestroy() {
        super.onDestroy();
    }
    
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
        	mService = ((StepService.StepBinder) service).getService();
        	mService.registerCallback(mCallback);
        	mService.reloadSettings();
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }
    };
    
    private void startStepService() {
        startService(new Intent(Wagz.this, StepService.class));
    }
    
    private void bindStepService() {
        bindService(new Intent(Wagz.this, StepService.class), 
        		mConnection, Context.BIND_AUTO_CREATE);
    }

    private void unbindStepService() {
        unbindService(mConnection);
    }
    
    private void stopStepService() {
        if (mService != null) {
            stopService(new Intent(Wagz.this,
                  StepService.class));
        }
    }
    
    private void resetValues(boolean updateDisplay) {
        if (this.isRunning()) {
            mService.resetValues();                    
        } else {
            SharedPreferences state = getSharedPreferences("state", 0);
            SharedPreferences.Editor stateEditor = state.edit();
            if (updateDisplay) {
                stateEditor.putInt("steps", 0);
                stateEditor.putInt("pace", 0);
                stateEditor.putFloat("distance", 0);
                stateEditor.putFloat("speed", 0);
                stateEditor.putFloat("calories", 0);
                stateEditor.commit();
            }
        }
    }

    private static final int MENU_SETTINGS = 8;
    private static final int MENU_QUIT     = 9;
    private static final int MENU_PAUSE = 1;
    private static final int MENU_RESUME = 2;
    private static final int MENU_DETAILS = 3;
    
    /* Creates the menu items */
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if (this.isRunning()) {
            menu.add(0, MENU_PAUSE, 0, R.string.pause)
            .setIcon(android.R.drawable.ic_media_pause)
            .setShortcut('1', 'p');
        } else {
            menu.add(0, MENU_RESUME, 0, R.string.resume)
            .setIcon(android.R.drawable.ic_media_play)
            .setShortcut('1', 'p');
        }
        menu.add(0, MENU_DETAILS, 0, R.string.menu_details)
	        .setIcon(android.R.drawable.ic_menu_info_details)
	        .setShortcut('2', 'd')
        	.setIntent(new Intent(this, Detailz.class));
        menu.add(0, MENU_SETTINGS, 0, R.string.settings)
	        .setIcon(android.R.drawable.ic_menu_preferences)
	        .setShortcut('8', 's')
	        .setIntent(new Intent(this, Settingz.class));
        menu.add(0, MENU_QUIT, 0, R.string.quit)
	        .setIcon(android.R.drawable.ic_lock_power_off)
	        .setShortcut('9', 'q');
        return true;
    }

    /* Handles item selections */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_PAUSE:
                unbindStepService();
                stopStepService();
                return true;
            case MENU_RESUME:
                startStepService();
                bindStepService();
                return true;
            case MENU_QUIT:
                resetValues(false);
                stopStepService();
                finish();
                return true;
        }
        return false;
    }
 
    // TODO: unite all into 1 type of message
    private StepService.ICallback mCallback = new StepService.ICallback() {
        public void stepsChanged(int value) {
            mHandler.sendMessage(mHandler.obtainMessage(STEPS_MSG, value, 0));
        }
        public void paceChanged(int value) {
            // do nothing
        }
        public void distanceChanged(float value) {
            // do nothing
        }
        public void speedChanged(float value) {
            // do nothing
        }
        public void caloriesChanged(float value) {
            // do nothing
        }
    };
    
    private static final int STEPS_MSG = 1;
    
    private Handler mHandler = new Handler() {
        @Override public void handleMessage(Message msg) {
            switch (msg.what) {
                case STEPS_MSG:
//                	mTimeValue = mStartTime;
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
        
    };
    
}