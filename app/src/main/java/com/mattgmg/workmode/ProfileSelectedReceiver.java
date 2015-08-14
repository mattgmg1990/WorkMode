package com.mattgmg.workmode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import cyanogenmod.app.ProfileManager;

public class ProfileSelectedReceiver extends BroadcastReceiver {
    private static final String WORK_PROFILE_NAME = "work";

    public ProfileSelectedReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String profileName = intent.getStringExtra(ProfileManager.EXTRA_PROFILE_NAME);
        if (WORK_PROFILE_NAME.equalsIgnoreCase(profileName)) {
            EmailIntentService.startActionSendEmail(context);
        }
    }
}
