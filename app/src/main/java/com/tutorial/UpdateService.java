package com.tutorial;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Calendar;

/**
 * Created by abdul on 13/03/2017.
 */

public class UpdateService extends Service {

    @Nullable
    public void onCreate() {
        super.onCreate();
        // register receiver that handles screen on and screen off logic

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        BroadcastReceiver mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);
    }
    public int onStartCommand(Intent intent, int startId) {
        boolean screenOn = intent.getBooleanExtra("screen_state", false);
        if (!screenOn) {
            //  System.out.println("service Screen is off");
            String mydate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
            Long tsLong = System.currentTimeMillis() / 1000;
            String ts = tsLong.toString();
            String screenOff = "Off" + mydate + "," + ts;
            generateNoteOnSD(getApplicationContext(), screenOff);
        } else {
            // System.out.println("service Screen is on");
            String mydate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
            Long tsLong = System.currentTimeMillis() / 1000;
            String ts = tsLong.toString();
            String screenIsOn = "On" + mydate + "," + ts;
            generateNoteOnSD(getApplicationContext(), screenIsOn);
        }
        return START_STICKY;
    }

    public void generateNoteOnSD(Context context, String sBody) {
        try {
            String content = sBody;
            String dir = Environment.getExternalStorageDirectory() + File.separator + "myDirectory";
            //create folder
            File folder = new File(dir); //folder name
            folder.mkdirs();
            //create file
            File file = new File(dir, "ScreenData.txt");
            FileWriter fw = new FileWriter(file, true);
            //BufferedWriter writer give better performance
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);
            pw.write(content);
            pw.println("");
            //Closing BufferedWriter Stream
            bw.close();
            System.out.println("Data successfully appended at the end of file");

        } catch (IOException ioe) {
            System.out.println("Exception occurred:");
            ioe.printStackTrace();
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}