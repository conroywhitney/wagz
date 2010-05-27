package com.konreu.android.wagz;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import com.github.droidfu.DroidFuApplication;
import com.konreu.android.wagz.activities.Alarmz;

public class WagzApp extends DroidFuApplication {
	private static final String TAG = "WagzApp";
	
	@Override
	public void onCreate() {
		super.onCreate();		
		updateAlarm();
	}
	
	public void updateAlarm() {
		String sTAG = TAG + ".updateAlarm";
		
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(this, Alarmz.class), 0);
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		
		alarmManager.cancel(pendingIntent);
		
		// Set alarm for next time
		PedometerSettings pedometerSettings = PedometerSettings.getInstance(this);
		
		if (pedometerSettings.shouldSetReminder()) {
			Log.i(sTAG, "Going to set a reminder because preference was true");
			
			String sAlarmTime = pedometerSettings.getReminderTime();

			Log.v(sTAG, "alarm string: " + sAlarmTime);
			
			int iHour = -1, iMin = -1;
			if (sAlarmTime.contains(":")) {
				String[] arrAlarmParts = sAlarmTime.split(":");
				if (arrAlarmParts.length == 2) {
					iHour = Integer.parseInt(arrAlarmParts[0]);
					iMin = Integer.parseInt(arrAlarmParts[1]);
					
					Log.i(sTAG, "split alarm into hours[" + iHour + "] and minutes [" + iMin + "]");
				} else {
					Log.w(sTAG, "alarm string not split into two parts like expected");
				}
			} else {
				Log.w(sTAG, "alarm string not contain : like expected");
			}
			
			if (iHour > -1 && iMin > -1) {				
				Calendar now = Calendar.getInstance();
				
				Calendar cAlarmDate = Calendar.getInstance();
				cAlarmDate.set(Calendar.HOUR_OF_DAY, iHour);
				cAlarmDate.set(Calendar.MINUTE, iMin);
				cAlarmDate.set(Calendar.SECOND, 0);
				
				Log.v(sTAG, now.get(Calendar.HOUR_OF_DAY) + " vs. " + iHour + " vs. " + cAlarmDate.get(Calendar.HOUR_OF_DAY));
				Log.v(sTAG, now.get(Calendar.MINUTE) + " vs. " + iMin + " vs. " + cAlarmDate.get(Calendar.MINUTE));
				
				if ((now.get(Calendar.HOUR_OF_DAY) > iHour) || 
						(now.get(Calendar.HOUR_OF_DAY) >= iHour && now.get(Calendar.MINUTE) >= iMin)) {
					// We've already passed it ! Set it for tomorrow
					Log.v(sTAG, "Passed the time today; will do reminder for tomorrow");
					cAlarmDate.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH) + 1);
				} else {
					Log.v(sTAG, "Can still do alarm for today!");
				}
				
				Log.i(sTAG, System.currentTimeMillis() + " vs. " + cAlarmDate.getTimeInMillis() + " (" + (cAlarmDate.getTimeInMillis() - System.currentTimeMillis()) + ") ms from now");		
								
				// For now, just set it to notify me in 24 hours from when I started this app for the first time
				alarmManager.set(AlarmManager.RTC_WAKEUP, cAlarmDate.getTimeInMillis(), pendingIntent);		
			} else {
				Log.w(sTAG, "Not setting alarm because not have proper hour and minute");
			}
		} else {
			Log.i(sTAG, "Not setting alarm because preference was false");
		}
	}

	
	
}
