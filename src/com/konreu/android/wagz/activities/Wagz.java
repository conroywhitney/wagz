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
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.konreu.android.wagz.AppState;
import com.konreu.android.wagz.PedometerSettings;
import com.konreu.android.wagz.R;
import com.konreu.android.wagz.StepService;

public class Wagz extends Activity {
	private static String TAG = "Wagz";	
	
	private final int MIN_PERCENT_FOR_LOYALTY_GAIN = 10;
	
    private StepService mService;
        
    private SeekBar mHappinessBar;
    private TextView mHappinessView;
    private TextView mLoyaltyView;    
    
    private long mElapsedTime;
    private int mLoyalty;
    
    static final int DIALOG_ABOUT = 1;

    private boolean isRunning() {
    	Log.v(TAG + ".isRunning", "StepService.isRunning = " + StepService.isRunning());
		return StepService.isRunning();
    }
            
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                
        setContentView(R.layout.main);
                
        mHappinessBar = (SeekBar) findViewById(R.id.happiness_bar);
        mHappinessBar.setEnabled(false);
        mHappinessBar.setFocusable(false);
        
    	Button btnStartWalk = (Button) findViewById(R.id.btn_start_walk);
    	btnStartWalk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startWalk();
            }
        });
    	
    	Button btnStopWalk = (Button) findViewById(R.id.btn_stop_walk);
    	btnStopWalk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stopWalk();
            }
        });
    	
    	mHappinessView = (TextView) findViewById(R.id.happiness_value);
    	mLoyaltyView = (TextView) findViewById(R.id.loyalty_value);
    	    	    	
    	setButtonStartWalk();
    	
    	// Take away hearts
    	double dblNumDaysSinceLastActivity = getNumDaysSinceLastActivity();
    	Log.i(TAG, "dblNumDaysSinceLastActivity: " + Double.toString(dblNumDaysSinceLastActivity));
    	int iNumDays = (int)(Math.floor(dblNumDaysSinceLastActivity));
    	Log.i(TAG, "iNumDays: " + Integer.toString(iNumDays));
    	if (dblNumDaysSinceLastActivity > 1.5) {	// give them 36 hour window
    		Log.i(TAG, "it has been too long...");
    		mLoyalty = AppState.getInstance(this).getLoyalty();
    		Log.i(TAG, "before loyalty: " + mLoyalty);
    		if (iNumDays > mLoyalty) {
    			Log.i(TAG, "it has been a very long time ... no more loyalty");
    			// It has been too long ... they have no more loyalty   =(
    			mLoyalty = 0;
    		} else {
    			Log.i(TAG, "reducing loyalty");
    			// Take away how long it's been since they have last had activity
    			mLoyalty -= iNumDays;
    		}
    		
    		AppState.getInstance(this).setLoyalty(mLoyalty);
    	} else {
    		Log.i(TAG, "they are still within the threshold...");
    	}
    }
        
    private double getNumDaysSinceLastActivity() {
    	long elapsedMS = System.currentTimeMillis() - AppState.getInstance(this).getLastWalkDate();
    	return (double) (elapsedMS / 86400000.0);	// 129600000 = 36hrs
    }
    

    @Override
    protected void onResume() {
        super.onResume();
        
        mElapsedTime = AppState.getInstance(this).getElapsedTime();
        mLoyalty = AppState.getInstance(this).getLoyalty();
        
        if (this.isRunning()) {
        	bindStepService();
        	
        	// If already running, want to show "Stop" button
        	setButtonStopWalk();
        } else {
        	// If not yet running, show "Start" button
        	setButtonStartWalk();
        }
        
        // Do this the very last thing ...
        updateUI();
    }
        
    private void updateUI() {
        // These have to be *after* we get our settings ...
    	int percentDone = getPercentDone();
    	
        mHappinessView.setText(percentDone + "%");
    	mHappinessBar.setProgress(percentDone);
    	
    	if (percentDone >= MIN_PERCENT_FOR_LOYALTY_GAIN && !AppState.getInstance(this).hasUpdatedLoyaltyLately()) {
    		Log.i(TAG, "greater than 90% and not updated since last activity: updating loyalty from " + mLoyalty + " to " + (mLoyalty+1));
    		// Add hearts !
    		mLoyalty += 1;
    		
    		// We have made our dog happy -- this date will live in infamy !
    		AppState.getInstance(this).setLastWalkDate(System.currentTimeMillis());
    		AppState.getInstance(this).setLoyalty(mLoyalty);
    		AppState.getInstance(this).setHasUpdatedLoyaltyLately(true);
    	}
    	
    	mLoyaltyView.setText(Integer.toString(mLoyalty));
    }
    
    /***
     * Show Start button, hide Stop button
     */
    protected void setButtonStartWalk() {
    	((Button) findViewById(R.id.btn_start_walk)).setVisibility(View.VISIBLE);
    	((Button) findViewById(R.id.btn_stop_walk)).setVisibility(View.GONE);
    }
    
    /***
     * Hide Start button, show Stop button
     */
    protected void setButtonStopWalk() {
    	((Button) findViewById(R.id.btn_start_walk)).setVisibility(View.GONE);
    	((Button) findViewById(R.id.btn_stop_walk)).setVisibility(View.VISIBLE);
    }
    
    /***
     * Bind to StepService and show "Stop" button
     */
    protected void startWalk() {
    	startStepService();
        bindStepService();
        setButtonStopWalk();
    }
    
    /***
     * Unbind from StepService and show "Start" button
     */
    protected void stopWalk() {
        unbindStepService();
        stopStepService();
        setButtonStartWalk();
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
        	AppState.getInstance(this).clear();
        }
    }

    private static final int MENU_SETTINGS = 8;
    private static final int MENU_QUIT = 9;
    private static final int MENU_PAUSE = 1;
    private static final int MENU_RESUME = 2;
    private static final int MENU_DETAILS = 3;
    private static final int MENU_ABOUT = 4;
    
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
	        .setShortcut('7', 's')
	        .setIntent(new Intent(this, Settingz.class));
        menu.add(0, MENU_ABOUT, 0, R.string.menu_about)
        	.setIcon(android.R.drawable.ic_menu_help)
        	.setShortcut('8', 'a');
        menu.add(0, MENU_QUIT, 0, R.string.quit)
	        .setIcon(android.R.drawable.ic_lock_power_off)
	        .setShortcut('9', 'q');
        return true;
    }

    /* Handles item selections */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_PAUSE:
                stopWalk();
                return true;
            case MENU_RESUME:
                startWalk();
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
            case MENU_ABOUT:
            	showDialog(DIALOG_ABOUT);
            	return true;
        }
        return false;
    }  
    
    // TODO: unite all into 1 type of message
    private StepService.ICallback mCallback = new StepService.ICallback() {
        public void distanceChanged(float value) {
        	// do nothing
        }
        public void elapsedTimeChanged(long value) {
        	Log.v(TAG, "timeChanged: " + Long.toString(value));
            mHandler.sendMessage(mHandler.obtainMessage(ELAPSED_TIME_MSG, value));
        }        
    };
    
    private static final int ELAPSED_TIME_MSG = 1;
    
    private Handler mHandler = new Handler() {
        @Override public void handleMessage(Message msg) {
            switch (msg.what) {
                case ELAPSED_TIME_MSG:
                	mElapsedTime = (Long)msg.obj;
                	if (mElapsedTime <= 0) { mElapsedTime = 0; }
                	updateUI();
                default:
                    super.handleMessage(msg);
            }
        }
    };  
    
    private int getPercentDone() {
    	float fPercentDone = (float)(mElapsedTime / (PedometerSettings.getInstance(this).getWalkLength() * 60000.0));
    	if (fPercentDone > 1) {
    		return 100;
    	} else if (fPercentDone < 0) {
    		return 0;
    	} else {
    		return (int)(fPercentDone * 100);
    	}
    }
    
    protected Dialog onCreateDialog(int id) {
    	Dialog dialog;
    	switch(id) {
    		case DIALOG_ABOUT:
    			dialog = new Dialog(this);
    	    	dialog.setContentView(R.layout.about);
    	    	dialog.setTitle("About " + getString(R.string.app_name));
    	    	
    	    	// Set the application version
    	    	// HOLY SHIT THIS IS ALOT OF WORK FOR ONE NUMBER
    	    	PackageManager pm = getPackageManager();
    	    	PackageInfo pi = null;
    	        try {
    	        	pi = pm.getPackageInfo("com.konreu.android.wagz", 0);
    	        } catch (NameNotFoundException nnfe) {
    	        	pi = null;
    	        	Log.e(TAG, "error getting package info: " + nnfe.getMessage());
    	        }
    	        TextView text = (TextView) dialog.findViewById(R.id.app_version);
    	        if (pi != null) {
    	        	text.setText(pi.versionName);
    	        } else {
    	        	text.setVisibility(View.GONE);
    	        }
    	    	
    	    	break;
	        default:
	            dialog = null;
        }
        return dialog;
    }    
}