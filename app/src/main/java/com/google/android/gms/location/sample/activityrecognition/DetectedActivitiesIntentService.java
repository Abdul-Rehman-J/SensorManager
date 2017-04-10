/**
 * Copyright 2014 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.gms.location.sample.activityrecognition;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * IntentService for handling incoming intents that are generated as a result of requesting
 * activity updates using
 * {@link com.google.android.gms.location.ActivityRecognitionApi#requestActivityUpdates}.
 */
    public class DetectedActivitiesIntentService extends IntentService {

    protected static final String TAG = "DetectedActivitiesIS";
    String detected_Activities;
    String android_id;
    String mydate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());


    /**
     * This constructor is required, and calls the super IntentService(String)
     * constructor with the name for a worker thread.
     */
    public DetectedActivitiesIntentService() {
        // Use the TAG to name the worker thread.
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * Handles incoming intents.
     *
     * @param intent The Intent is provided (inside a PendingIntent) when requestActivityUpdates()
     *               is called.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
        Intent localIntent = new Intent(Constants.BROADCAST_ACTION);

        // Get the list of the probable activities associated with the current state of the
        // device. Each activity is associated with a confidence level, which is an int between
        // 0 and 100.
        ArrayList<DetectedActivity> detectedActivities = (ArrayList) result.getProbableActivities();
        String mydate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        // Broadcast the list of detected activities.
        localIntent.putExtra(Constants.ACTIVITY_EXTRA, detectedActivities);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
        // Log each activity.
        Log.i(TAG, "activities detected");
        for (DetectedActivity da : detectedActivities) {
            Log.i(TAG, Constants.getActivityString(
                    getApplicationContext(),
                    da.getType()) + " " + da.getConfidence() + "%"
            );
            android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            detected_Activities = Constants.getActivityString(getApplicationContext(), da.getType()) + " " + da.getConfidence() + "%";
            try {
                    String dir = Environment.getExternalStorageDirectory() + File.separator + "myDirectory";
                    //create folder
                    File folder = new File(dir); //folder name
                    folder.mkdirs();
                    //create file
                   File file = new File(dir, "Activity Recognition.txt");
                   FileWriter fw = new FileWriter(file, true);
                    //BufferedWriter writer give better performance
                    BufferedWriter bw = new BufferedWriter(fw);
                    PrintWriter pw = new PrintWriter(bw);
                Long tsLong = System.currentTimeMillis() / 1000;
                String ts = tsLong.toString();
                pw.write(android_id + "," + detected_Activities + "," + mydate + "," + ts);
                    pw.println("");
                    //Closing BufferedWriter Stream
                    bw.close();
                    System.out.println("Data successfully appended at the end of file");
                }
                catch (IOException ioe) {
                    System.out.println("Exception occurred:");
                    ioe.printStackTrace();}
            }
        }


    }