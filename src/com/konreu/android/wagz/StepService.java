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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.konreu.android.wagz.activities.Wagz;
import com.konreu.android.wagz.listeners.DistanceNotifier;

/**
 * This is an example of implementing an application service that runs locally
 * in the same process as the application.  The {@link StepServiceController}
 * and {@link StepServiceBinding} classes show how to interact with the
 * service.
 *
 * <p>Notice the use of the {@link NotificationManager} when interesting things
 * happen in the service.  This is generally how background services should
 * interact with the user, rather than doing something more disruptive such as
 * calling startActivity().
 */
public class StepService extends Service {

    private SharedPreferences mSettings;
    private PedometerSettings mPedometerSettings;
    
    private SharedPreferences mState;
    private SharedPreferences.Editor mStateEditor;
    
    private SensorManager mSensorManager;
    private StepDetector mStepDetector;
    // private StepBuzzer mStepBuzzer; // used for debugging
//    private TimerNotifier mTimerNotifier;
    private DistanceNotifier mDistanceNotifier;
    
    private PowerManager.WakeLock wakeLock;
    private NotificationManager mNM;

    private long mBeginTime;
    private float mDistance;
    
    private long getElapsedTime() {
    	long now = System.currentTimeMillis();
        return (now - mBeginTime);
    }
    
    private static boolean bRunning;
    public static boolean isRunning() {
    	return bRunning;
    }
        
    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class StepBinder extends Binder {
        public StepService getService() {
            return StepService.this;
        }
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        bRunning = false;
        
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        showNotification();
        
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "StepService");
        wakeLock.acquire();
        
        // Load settings
        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        mPedometerSettings = new PedometerSettings(mSettings);
        mState = getSharedPreferences("state", 0);
        
        // Start detecting
        mStepDetector = new StepDetector();
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorManager.registerListener(mStepDetector, 
                SensorManager.SENSOR_ACCELEROMETER | 
                SensorManager.SENSOR_MAGNETIC_FIELD | 
                SensorManager.SENSOR_ORIENTATION,
                SensorManager.SENSOR_DELAY_FASTEST);

        mDistanceNotifier = new DistanceNotifier(mDistanceListener, mPedometerSettings);
        mDistanceNotifier.setDistance(mDistance = mState.getFloat("distance", 0));
        mStepDetector.addStepListener(mDistanceNotifier);
        
//        mTimeNotifier = new TimeNotifier(mTimeListener, mPedometerSettings);
//        mTimeNotifier.setTime(getElapsedTime());
//        mStepDetector.addStepListener(mTimeNotifier);
        
        // Used when debugging:
        // mStepBuzzer = new StepBuzzer(this);
        // mStepDetector.addStepListener(mStepBuzzer);

        // Start voice
        reloadSettings();
        
    }
    
    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        // Tell the user we started.
        Toast.makeText(this, getText(R.string.started), Toast.LENGTH_SHORT).show();
        bRunning = true;
        mBeginTime = System.currentTimeMillis();
        
        this.reloadSettings();
    }

    @Override
    public void onDestroy() {
        
        mStateEditor = mState.edit();
        mStateEditor.putFloat("distance", mDistance);
        mStateEditor.putLong("time", getElapsedTime());
        mStateEditor.commit();
        
        mNM.cancel(R.string.app_name);

        wakeLock.release();
        
        super.onDestroy();
        
        // Stop detecting
        mSensorManager.unregisterListener(mStepDetector);
        
        // Tell the user we stopped.
        Toast.makeText(this, getText(R.string.stopped), Toast.LENGTH_LONG).show();
        bRunning = false;
        mBeginTime = -1;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * Receives messages from activity.
     */
    private final IBinder mBinder = new StepBinder();

    public interface ICallback {
        public void distanceChanged(float value);
    }
    
    private ICallback mCallback;

    public void registerCallback(ICallback cb) {
        mCallback = cb;
        //mStepDisplayerarg1.passValue();
        //mPaceListener.passValue();
    }
    
    public void reloadSettings() {
        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        
        if (mStepDetector != null) { 
            mStepDetector.setSensitivity(
                    Integer.valueOf(mSettings.getString("sensitivity", "30"))
            );
        }
        
        if (mDistanceNotifier != null) mDistanceNotifier.reloadSettings();
//        if (mTimeNotifier != null) mTimeNotifier.reloadSettings();
    }
    
    public void resetValues() {
        mDistanceNotifier.setDistance(0);
//        mTimeNotifier.setElapsedTime(0);
    }
    
    /**
     * Forwards distance values from DistanceNotifier to the activity. 
     */
    private DistanceNotifier.Listener mDistanceListener = new DistanceNotifier.Listener() {
        public void valueChanged(float value) {
            mDistance = value;
            passValue();
        }
        public void passValue() {
            if (mCallback != null) {
                mCallback.distanceChanged(mDistance);
            }
        }
    };

//    /**
//     * Forwards calories values from CaloriesNotifier to the activity. 
//     */
//    private TimerNotifier.Listener mTimerListener = new TimerNotifier.Listener() {
//        public void valueChanged(float value) {
////            mCalories = value;
//            passValue();
//        }
//        public void passValue() {
//            if (mCallback != null) {
////                mCallback.caloriesChanged(mCalories);
//            }
//        }
//    };
    
    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        CharSequence text = getText(R.string.app_name);
        Notification notification = new Notification(R.drawable.ic_notification, null,
                System.currentTimeMillis());
        notification.flags = Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, Wagz.class), 0);
        notification.setLatestEventInfo(this, text,
                getText(R.string.notification_subtitle), contentIntent);

        mNM.notify(R.string.app_name, notification);
    }
}

