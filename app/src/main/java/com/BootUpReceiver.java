package com;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tutorial.MyService;
import com.tutorial.ScreenReceiver;
import com.tutorial.UpdateService;


public class BootUpReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        /****** For Start Activity *****/
//        Intent i = new Intent(context, MainActivity.class);
//        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(i);

        /***** For start Service  ****/
        Intent myIntent = new Intent(context, MyService.class);
        context.startService(myIntent);
        Intent myIntents = new Intent(context, UpdateService.class);
        context.startService(myIntents);

        Intent myIntentss = new Intent(context, ScreenReceiver.class);
        context.startService(myIntentss);
    }
}