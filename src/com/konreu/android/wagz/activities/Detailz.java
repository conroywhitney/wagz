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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.konreu.android.wagz.PedometerSettings;
import com.konreu.android.wagz.R;
import com.konreu.android.wagz.StepService;
import com.snaptic.integration.IntentIntegrator;

public class Detailz extends Activity {
	private static String TAG = "Detailz";	
   
	private PedometerSettings mPedometerSettings;
    
    private IntentIntegrator _notesIntent;
    
    private TextView mDistanceValueView;
    private TextView mTimeValueView;
    
    private float mDistanceValue;
    private long mElapsedTime;

    private boolean mIsMetric;
    
    private boolean isRunning() {
    	Log.v(TAG + ".isRunning", "StepService.isRunning = " + StepService.isRunning());
		return StepService.isRunning();
    }
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.detailz);
        
        if (_notesIntent == null) {
        	_notesIntent = new IntentIntegrator(this);	
        }
        
        ((Button)findViewById(R.id.create_quick_note_button)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				_notesIntent.createNote("I walked " + getFormattedDistance() + " " + 
							getDistanceUnits() + 
							" in " + getFormattedTime() + " minutes with my virtual dog " + 
							"" + "\n\n#wagz");
			}        	
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        if (this.isRunning()) {
        	bindStepService();	
        }
        
        mPedometerSettings = new PedometerSettings(PreferenceManager.getDefaultSharedPreferences(this));
        
        mDistanceValueView = (TextView) findViewById(R.id.distance_value);
        mTimeValueView = (TextView) findViewById(R.id.time_value);

        mIsMetric = mPedometerSettings.isMetric();
        ((TextView) findViewById(R.id.distance_units)).setText(getDistanceUnits());
    }
    
    protected String getDistanceUnits() {
    	 mIsMetric = mPedometerSettings.isMetric();
    	 return getString(
    	    mIsMetric
         	? R.string.kilometers
         	: R.string.miles
         );
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
        _notesIntent = null;
    }

    private StepService mService;
    
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
        startService(new Intent(Detailz.this, StepService.class));
    }
    
    private void bindStepService() {
        bindService(new Intent(Detailz.this, 
                StepService.class), mConnection, Context.BIND_AUTO_CREATE);
    }

    private void unbindStepService() {
        unbindService(mConnection);
    }
    
    private void stopStepService() {
        if (mService != null) {
            stopService(new Intent(Detailz.this,
                  StepService.class));
        }
    }
    
    private void resetValues(boolean updateDisplay) {
        if (this.isRunning()) {
            mService.resetValues();                    
        } else {
            mDistanceValueView.setText("0");
            mTimeValueView.setText("0");
            
            SharedPreferences state = getSharedPreferences(StepService.STATE_KEY, 0);
            SharedPreferences.Editor stateEditor = state.edit();
            if (updateDisplay) {
                stateEditor.putFloat(StepService.STATE_DISTANCE, 0);
                stateEditor.putLong(StepService.STATE_ELAPSED_TIME, 0);
                stateEditor.commit();
            }
        }
    }

    private static final int MENU_SETTINGS = 8;
    private static final int MENU_QUIT = 9;
    private static final int MENU_PAUSE = 1;
    private static final int MENU_RESUME = 2;
    private static final int MENU_RESET = 3;
    
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
        menu.add(0, MENU_RESET, 0, R.string.reset)
	        .setIcon(android.R.drawable.ic_menu_close_clear_cancel)
	        .setShortcut('2', 'r');
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
            case MENU_RESET:
            	// Alert the user that they are about to lose their values
                new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.really_reset_values)
                .setMessage(R.string.warning_reset_values)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    	resetValues(true);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();

                return true;            	
            case MENU_QUIT:
            	// Alert the user that they are about to lose their values
                new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.really_reset_values)
                .setMessage(R.string.warning_reset_values)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resetValues(false);
                        stopStepService();
                        finish();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
                return true;
        }
        return false;
    }
 
    // TODO: unite all into 1 type of message
    private StepService.ICallback mCallback = new StepService.ICallback() {
        public void distanceChanged(float value) {
        	Log.v(TAG, "distanceChanged: " + Float.toString(value));
            mHandler.sendMessage(mHandler.obtainMessage(DISTANCE_MSG, (int)(value*1000)));
        }
        public void elapsedTimeChanged(long value) {
        	Log.v(TAG, "timeChanged: " + Long.toString(value));
            mHandler.sendMessage(mHandler.obtainMessage(ELAPSED_TIME_MSG, value));
        }        
    };
    
    private static final int DISTANCE_MSG = 1;
    private static final int ELAPSED_TIME_MSG = 2;
    
    private Handler mHandler = new Handler() {
        @Override public void handleMessage(Message msg) {
            switch (msg.what) {
                case DISTANCE_MSG:
                    mDistanceValue = ((Integer)msg.obj)/1000f;
                    if (mDistanceValue <= 0) { mDistanceValue = 0; }
                    mDistanceValueView.setText(getFormattedDistance());
                    break;
                case ELAPSED_TIME_MSG:
                	mElapsedTime = (Long)msg.obj;
                	if (mElapsedTime <= 0) { mElapsedTime = 0; }
                	mTimeValueView.setText(getFormattedTime());
                default:
                    super.handleMessage(msg);
            }
        }
        
    };
    
    private String getFormattedTime() {
    	Date d = new Date(mElapsedTime);
    	DateFormat formatter = new SimpleDateFormat("mm:ss");
    	return formatter.format(d);
    }
    
    private String getFormattedDistance() {
    	return ("" + (mDistanceValue + 0.000001f)).substring(0, 4);
    }
    
}