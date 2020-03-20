package com.kds.gold.acebird.apps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.kds.gold.acebird.activities.SplashActivity;

public class BootCompletedIntentReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Intent pushIntent = new Intent(context, SplashActivity.class);
            pushIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(pushIntent);
        }
    }
}