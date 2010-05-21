package com.konreu.android.wagz;

import android.content.Context;
import android.util.Log;

public class Dog {
	private static String TAG = "Dog";
	private static Dog instance;
	
	private double PREF_MIN_HAPPINESS_FOR_LOYALTY_GAIN;
//	private int PREF_DESIRED_WALK_FREQUENCY;
	private long PREF_DESIRED_WALK_DURATION;
//	private final double GRACE_MULTIPLIER = 1.5;
	
	private double mHappiness;
	private int mLoyalty;	
//	private long mLastWalkedDate;
	
	private boolean mHasUpdatedLoyaltyRecently;
	
	private AppState mAppState;
	
	private Dog(Context c) {
		PREF_MIN_HAPPINESS_FOR_LOYALTY_GAIN = (double) PedometerSettings.getInstance(c).getWalkPercentComplete() / 100.0;
//		PREF_DESIRED_WALK_FREQUENCY = PedometerSettings.getInstance(c).getWalkFrequency();
		PREF_DESIRED_WALK_DURATION = PedometerSettings.getInstance(c).getWalkLength();
		
		mAppState = AppState.getInstance(c);
		
		updateHappiness(mAppState.getElapsedTime());
		mLoyalty = mAppState.getLoyalty();
//		mLastWalkedDate = mAppState.getLastWalkDate();
		
		mHasUpdatedLoyaltyRecently = false;
		
//		updateLoyaltyOnStartup();
	}
	
//	private void updateLoyaltyOnStartup() {
//		String sTAG = TAG + ".getLoyalty";
//		
//		// Take away hearts
//		double dblNumMinsSinceLastActivity = getNumMinsSinceLastActivity();
//		Log.i(sTAG, "dblNumMinsSinceLastActivity: " + Double.toString(dblNumMinsSinceLastActivity));
//		int iNumMinsSinceLastActivity = (int)(Math.floor(dblNumMinsSinceLastActivity));
//		Log.i(sTAG, "iNumMinsSinceLastActivity: " + Integer.toString(iNumMinsSinceLastActivity));
//		if (dblNumMinsSinceLastActivity > (PREF_DESIRED_WALK_FREQUENCY * GRACE_MULTIPLIER)) {	// give them a 1.5 grace window
//			Log.i(sTAG, "it has been too long... " + dblNumMinsSinceLastActivity + " > " + PREF_DESIRED_WALK_FREQUENCY);
//			Log.i(sTAG, "before loyalty: " + mLoyalty);
//			if (iNumMinsSinceLastActivity > mLoyalty) {
//				Log.i(sTAG, "it has been a very long time ... no more loyalty");
//				// It has been too long ... they have no more loyalty   =(
//				mLoyalty = 0;
//			} else {
//				Log.i(sTAG, "reducing loyalty");
//				// Take away how long it's been since they have last had activity
//				mLoyalty -= iNumMinsSinceLastActivity;
//			}
//			
//			//AppState.getInstance(this).setLoyalty(mLoyalty);
//		} else {
//			Log.i(sTAG, "they are still within the threshold...");
//		}
//	}
	
	public static Dog getInstance(Context c) {
		if (instance == null) {
			instance = new Dog(c);
		}
		return instance;
	}
	
	public int getLoyalty() {
		return mLoyalty;
	}
	
	public int getHappiness() {
		int iHappinessPercentage = 0;
		
    	if (mHappiness > 1) {
    		iHappinessPercentage = 100;
    	} else if (mHappiness < 0) {
    		iHappinessPercentage = 0;
    	} else {
    		iHappinessPercentage = (int)(mHappiness * 100);
    	}
    	
    	Log.v(TAG + ".getHappiness", "percentage: " + iHappinessPercentage);
    	return iHappinessPercentage;
	}
	
	public void updateHappiness(long elapsedTime) {
		if (elapsedTime <= 0) { elapsedTime = 0; }
		mHappiness = (float)(elapsedTime / (PREF_DESIRED_WALK_DURATION * 60000.0));
		Log.v(TAG + ".updateHappiness", "new happiness: " + mHappiness);
		updateLoyalty();
	}
	
//    private double getNumMinsSinceLastActivity() {
//    	long elapsedMS = System.currentTimeMillis() - mLastWalkedDate;
//    	return (double) (elapsedMS / 60000.0);
//    }
    	
	public void updateLoyalty() {
		String sTAG = TAG + ".updateLoyalty";
		
    	Log.v(sTAG, mHappiness + " vs. " + PREF_MIN_HAPPINESS_FOR_LOYALTY_GAIN);
    	
    	if (mHappiness >= PREF_MIN_HAPPINESS_FOR_LOYALTY_GAIN && !mHasUpdatedLoyaltyRecently) {
    		Log.i(sTAG, mHappiness + "% >= " + PREF_MIN_HAPPINESS_FOR_LOYALTY_GAIN + "% and not updated since last activity: updating loyalty from " + mLoyalty + " to " + (mLoyalty+1));

    		// Add loyalty !
    		mLoyalty += 1;
    		
    		// We have made our dog happy -- this date will live in infamy !
    		mAppState.setLastWalkDate(System.currentTimeMillis());
    		mAppState.setLoyalty(mLoyalty);
    		
    		// Make sure we don't keep updating loyalty all day .......
    		mHasUpdatedLoyaltyRecently = true;
    	}
	}
	
}
