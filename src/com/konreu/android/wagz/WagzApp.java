package com.konreu.android.wagz;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;

import com.github.droidfu.DroidFuApplication;
import com.konreu.android.wagz.activities.Alarmz;

public class WagzApp extends DroidFuApplication {

	@Override
	public void onCreate() {
		super.onCreate();
		
		// Set alarm for next time
//		PedometerSettings pedometerSettings = PedometerSettings.getInstance(this);
		
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(this, Alarmz.class), 0);
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		// For now, just set it to notify me in 24 hours from when I started this app for the first time
		alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + AlarmManager.INTERVAL_DAY, pendingIntent);
		
//		alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10000, pendingIntent);
	}

	
	
}
