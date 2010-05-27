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

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.konreu.android.wagz.R;

/**
 * Activity for Daily Notification of a Walk
 * @author Conroy Whitney
 */
public class Alarmz extends BroadcastReceiver {
	private static final String TAG = "Alarmz";
    
    @Override
    public void onReceive(Context context, Intent intent) {
    	Log.v(TAG + ".onReceive", "In here!");
    	
    	NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    	Notification notification = new Notification(R.drawable.icon, context.getString(R.string.notification_walk_title), System.currentTimeMillis());
    	PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, Wagz.class), 0);
    	notification.setLatestEventInfo(context, context.getString(R.string.notification_walk_title), context.getString(R.string.notification_walk_subtitle), contentIntent);
    	notification.flags = Notification.FLAG_ONLY_ALERT_ONCE | Notification.FLAG_AUTO_CANCEL | Notification.FLAG_SHOW_LIGHTS;
    	notification.ledOnMS = 1;
    	notification.ledOffMS = 0;
    	notification.defaults = Notification.DEFAULT_ALL;
    	notification.icon = R.drawable.ic_notification;
    	 
    	// The PendingIntent to launch our activity if the user selects this notification
    	manager.notify(R.string.app_name, notification);   	   	
    	
    	Log.v(TAG + ".onReceive", "Setting up subsequent alarm");
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(context, Alarmz.class), 0);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + AlarmManager.INTERVAL_DAY, pendingIntent);
    }
}
