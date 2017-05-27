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

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.emotionsense.demo.data.SenseOnceThread;
import com.emotionsense.demo.data.SubscribeThread;
import com.emotionsense.demo.data.loggers.StoreOnlyUnencryptedFiles;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.places.Places;
import com.tutorial.MyService;
import com.tutorial.ScreenReceiver;
import com.tutorial.UpdateService;
import com.ubhave.datahandler.ESDataManager;
import com.ubhave.datahandler.except.DataHandlerException;
import com.ubhave.datahandler.loggertypes.AbstractDataLogger;
import com.ubhave.datahandler.transfer.DataUploadCallback;
import com.ubhave.example.basicsensordataexample.R;
import com.ubhave.example.basicsensordataexample.SenseFromAllEnvSensorsTask;
import com.ubhave.example.basicsensordataexample.SenseFromAllPullSensorsTask;
import com.ubhave.example.basicsensordataexample.SenseFromAllPushSensorsTask;
import com.ubhave.sensormanager.ESSensorManager;
import com.ubhave.sensormanager.data.SensorData;
import com.ubhave.sensormanager.sensors.SensorUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * This sample demonstrates use of the
 * {@link com.google.android.gms.location.ActivityRecognitionApi} to recognize a user's current
 * activity, such as walking, driving, or standing still. It uses an
 * {@link android.app.IntentService} to broadcast detected activities through a
 * {@link BroadcastReceiver}. See the {@link DetectedActivity} class for a list of DetectedActivity
 * types.
 * <p/>
 * Note that this activity implements
 * {@link ResultCallback<R extends com.google.android.gms.common.api.Result>}.
 * Requesting activity detection updates using
 * {@link com.google.android.gms.location.ActivityRecognitionApi#requestActivityUpdates}
 * and stopping updates using
 * {@link com.google.android.gms.location.ActivityRecognitionApi#removeActivityUpdates}
 * returns a {@link com.google.android.gms.common.api.PendingResult}, whose result
 * object is processed by the {@code onResult} callback.
 */
public class MainActivity extends AppCompatActivity implements ConnectionCallbacks, OnConnectionFailedListener, DataUploadCallback, ResultCallback<Status> {
////Main activity from demo data manger start


    //Main activity from demo data manger end..............................
    protected static final String LOG_TAG = "NewMainActivity";
    // TODO: add push sensors you want to sense from here
    private final int[] Battery = {SensorUtils.SENSOR_TYPE_BATTERY};
    private final int[] ConState = {SensorUtils.SENSOR_TYPE_CONNECTION_STATE};
    private final int[] ConStrength = {SensorUtils.SENSOR_TYPE_CONNECTION_STRENGTH};
    private final int[] PassiveLocation = {SensorUtils.SENSOR_TYPE_PASSIVE_LOCATION};
    private final int[] PhoneState = {SensorUtils.SENSOR_TYPE_PHONE_STATE};
    private final int[] pushSensors = {SensorUtils.SENSOR_TYPE_PROXIMITY};
    private final int[] Screen = {SensorUtils.SENSOR_TYPE_SCREEN};
    private final int[] Sms = {SensorUtils.SENSOR_TYPE_SMS};
    // TODO: add pull sensors you want to sense once from here
    private final int[] pullSensors = {SensorUtils.SENSOR_TYPE_ACCELEROMETER};
    private final int[] Bluetooth = {SensorUtils.SENSOR_TYPE_BLUETOOTH};
    private final int[] CallReader = {SensorUtils.SENSOR_TYPE_CALL_CONTENT_READER};
    //private final int[] Gyroscope = {SensorUtils.SENSOR_TYPE_GYROSCOPE};
    private final int[] Location = {SensorUtils.SENSOR_TYPE_LOCATION};
    //private final int[] Magnetic = {SensorUtils.SENSOR_TYPE_MAGNETIC_FIELD};
    private final int[] MicroPhone = {SensorUtils.SENSOR_TYPE_MICROPHONE};
    private final int[] PhoneRadio = {SensorUtils.SENSOR_TYPE_PHONE_RADIO};
    private final int[] SMSReader = {SensorUtils.SENSOR_TYPE_SMS_CONTENT_READER};
    // private final int[] StepCounter = {SensorUtils.SENSOR_TYPE_STEP_COUNTER};
    private final int[] Wifi = {SensorUtils.SENSOR_TYPE_WIFI};
    protected ActivityDetectionBroadcastReceiver mBroadcastReceiver;
    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;
    private AbstractDataLogger logger;
    //    private final int[] Ambient = {SensorUtils.SENSOR_TYPE_AMBIENT_TEMPERATURE};
//    private final int[] Humidity = {SensorUtils.SENSOR_TYPE_HUMIDITY};
//    private final int[] Light = {SensorUtils.SENSOR_TYPE_LIGHT};
//    private final int[] Pressure = {SensorUtils.SENSOR_TYPE_PRESSURE};
    private ESSensorManager sensorManager;
    private SubscribeThread[] subscribeThreads;
    private SenseOnceThread[] pullThreads;
    // UI elements.
    private Button mRequestActivityUpdatesButton;
    private Button mRemoveActivityUpdatesButton;
    private ListView mDetectedActivitiesListView;
    /**
     * Adapter backed by a list of DetectedActivity objects.
     */
    private DetectedActivitiesAdapter mAdapter;
    /**
     * The DetectedActivities that we track in this sample. We use this for initializing the
     * {@code DetectedActivitiesAdapter}. We also use this for persisting state in
     * {@code onSaveInstanceState()} and restoring it in {@code onCreate()}. This ensures that each
     * activity is displayed with the correct confidence level upon orientation changes.
     */
    private ArrayList<DetectedActivity> mDetectedActivities;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        //Main activity from demodatamanger start........................................................
        try {
            // TODO: change this line of code to change the type of data logger
            // Note: you shouldn't have more than one logger!
//			logger = AsyncEncryptedDatabase.getInstance();
//			logger = AsyncWiFiOnlyEncryptedDatabase.getInstance();
//			logger = AsyncEncryptedFiles.getInstance();
//			logger = AsyncUnencryptedDatabase.getInstance();
//			logger = AsyncUnencryptedFiles.getInstance();
//			logger = StoreOnlyEncryptedDatabase.getInstance();
//			logger = StoreOnlyEncryptedFiles.getInstance();
//			logger = StoreOnlyUnencryptedDatabase.getInstance();
            logger = StoreOnlyUnencryptedFiles.getInstance();
            sensorManager = ESSensorManager.getSensorManager(this);

            // Example of starting some sensing in onCreate()
            // Collect a single sample from the listed pull sensors
            pullThreads = new SenseOnceThread[pullSensors.length];
            for (int i = 0; i < pullSensors.length; i++) {
                pullThreads[i] = new SenseOnceThread(this, sensorManager, logger, pullSensors[i]);
                pullThreads[i].start();
            }
//            pullThreads = new SenseOnceThread[StepCounter.length];
//            for (int i = 0; i < StepCounter.length; i++)
//           {
//               pullThreads[i] = new SenseOnceThread(this, sensorManager, logger, StepCounter[i]);
//               pullThreads[i].start();
//            }
        } catch (Exception e) {
            Toast.makeText(this, "" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            Log.d(LOG_TAG, e.getLocalizedMessage());
            e.printStackTrace();
        }
        //Demo data manger activity end...........................................................................
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        //BroadcastReceiver mReceiver = new ScreenReceiver();
        // registerReceiver(mReceiver, filter);
        System.out.println("onCreate ");
        startPull();
        //locaiton trace
        startService(new Intent(getApplicationContext(), MyService.class));
        if (!isNetworkAvailable()) {
            WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            wifi.setWifiEnabled(true);
        } else if (!isLocationServiceEnabled()) {
            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(myIntent);
        } else {
            startService(new Intent(getApplicationContext(), UpdateService.class));
        }
        // Get the UI widgets.
        mRequestActivityUpdatesButton = (Button) findViewById(R.id.request_activity_updates_button);
        mRemoveActivityUpdatesButton = (Button) findViewById(R.id.remove_activity_updates_button);
        mDetectedActivitiesListView = (ListView) findViewById(R.id.detected_activities_listview);
        // Get a receiver for broadcasts from ActivityDetectionIntentService.
        mBroadcastReceiver = new ActivityDetectionBroadcastReceiver();
        // Enable either the Request Updates button or the Remove Updates button depending on
        // whether activity updates have been requested.
        setButtonsEnabledState();
        // Reuse the value of mDetectedActivities from the bundle if possible. This maintains state
        // across device orientation changes. If mDetectedActivities is not stored in the bundle,
        // populate it with DetectedActivity objects whose confidence is set to 0. Doing this
        // ensures that the bar graphs for only only the most recently detected activities are
        // filled in.
        if (savedInstanceState != null && savedInstanceState.containsKey(
                Constants.DETECTED_ACTIVITIES)) {
            mDetectedActivities = (ArrayList<DetectedActivity>) savedInstanceState.getSerializable(
                    Constants.DETECTED_ACTIVITIES);
        } else {
            mDetectedActivities = new ArrayList<DetectedActivity>();

            // Set the confidence level of each monitored activity to zero.
            for (int i = 0; i < Constants.MONITORED_ACTIVITIES.length; i++) {
                mDetectedActivities.add(new DetectedActivity(Constants.MONITORED_ACTIVITIES[i], 0));
            }
        }
        // Bind the adapter to the ListView responsible for display data for detected activities.
        mAdapter = new DetectedActivitiesAdapter(this, mDetectedActivities);
        mDetectedActivitiesListView.setAdapter(mAdapter);
        // Kick off the request to build GoogleApiClient.
        buildGoogleApiClient();
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * ActivityRecognition API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(ActivityRecognition.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
    }
    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }
    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }
    @Override
    protected void onResume() {
        // Register the broadcast receiver that informs this activity of the DetectedActivity
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,
                new IntentFilter(Constants.BROADCAST_ACTION));
        // object broadcast sent by the intent service.
        if (!ScreenReceiver.screenOff) {
            // this is when onResume() is called due to a screen state change
            //  System.out.println("SCREEN TURNED ON");
            Long tsLong = System.currentTimeMillis() / 1000;
            String ts = tsLong.toString();
            String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
            String screenOff = "App on" + "," + mydate + "," + ts;
            //Toast.makeText(getApplicationContext(), "Activity screen is on", Toast.LENGTH_LONG).show();
            generateNoteOnSD(getApplicationContext(), screenOff);
        } else {
            // this is when onResume() is called when the screen state has not changed
            System.out.println(" this is when onResume() is called when the screen state has not changed ");
        }
        //demodata maneger start in resume ....................
        // Example of starting some sensing in onResume()
        // Collect a single sample from the listed push sensors
        subscribeThreads = new SubscribeThread[pushSensors.length];
        for (int i = 0; i < pushSensors.length; i++) {
            subscribeThreads[i] = new SubscribeThread(this, sensorManager, logger, pushSensors[i]);
            subscribeThreads[i].start();
        }
//        subscribeThreads = new SubscribeThread[Humidity.length];
//        for (int i = 0; i < Humidity.length; i++)
//        {
//            subscribeThreads[i] = new SubscribeThread(this, sensorManager, logger, Humidity[i]);
//            subscribeThreads[i].start();
//        }
//        subscribeThreads = new SubscribeThread[Light.length];
//        for (int i = 0; i < Light.length; i++)
//        {
//            subscribeThreads[i] = new SubscribeThread(this, sensorManager, logger, Light[i]);
//            subscribeThreads[i].start();
//        }
//        subscribeThreads = new SubscribeThread[Ambient.length];
//        for (int i = 0; i < Ambient.length; i++)
//        {
//            subscribeThreads[i] = new SubscribeThread(this, sensorManager, logger, Ambient[i]);
//            subscribeThreads[i].start();
//        }
//        subscribeThreads = new SubscribeThread[Pressure.length];
//        for (int i = 0; i < Pressure.length; i++)
//        {
//            subscribeThreads[i] = new SubscribeThread(this, sensorManager, logger, Pressure[i]);
//            subscribeThreads[i].start();
//        }
        subscribeThreads = new SubscribeThread[CallReader.length];
        for (int i = 0; i < CallReader.length; i++) {
            subscribeThreads[i] = new SubscribeThread(this, sensorManager, logger, CallReader[i]);
            subscribeThreads[i].start();
        }
        subscribeThreads = new SubscribeThread[SMSReader.length];
        for (int i = 0; i < SMSReader.length; i++) {
            subscribeThreads[i] = new SubscribeThread(this, sensorManager, logger, SMSReader[i]);
            subscribeThreads[i].start();
        }
        subscribeThreads = new SubscribeThread[Location.length];
        for (int i = 0; i < Location.length; i++) {
            subscribeThreads[i] = new SubscribeThread(this, sensorManager, logger, Location[i]);
            subscribeThreads[i].start();
        }
//       subscribeThreads = new SubscribeThread[Magnetic.length];
//       for (int i = 0; i < Magnetic.length; i++)
//       {
//            subscribeThreads[i] = new SubscribeThread(this, sensorManager, logger, Magnetic[i]);
//            subscribeThreads[i].start();
//       }

//        subscribeThreads = new SubscribeThread[Sms.length];
//        for (int i = 0; i < Sms.length; i++)
//        {
//            subscribeThreads[i] = new SubscribeThread(this, sensorManager, logger, Sms[i]);
//            subscribeThreads[i].start();
//        }
        pullThreads = new SenseOnceThread[Bluetooth.length];
        for (int i = 0; i < Bluetooth.length; i++) {
            pullThreads[i] = new SenseOnceThread(this, sensorManager, logger, Bluetooth[i]);
            pullThreads[i].start();
        }
        pullThreads = new SenseOnceThread[CallReader.length];
        for (int i = 0; i < CallReader.length; i++) {
            pullThreads[i] = new SenseOnceThread(this, sensorManager, logger, CallReader[i]);
            pullThreads[i].start();
        }
//       pullThreads = new SenseOnceThread[Gyroscope.length];
//        for (int i = 0; i < Gyroscope.length; i++)
//        {
//            pullThreads[i] = new SenseOnceThread(this, sensorManager, logger, Gyroscope[i]);
//            pullThreads[i].start();
//        }
        pullThreads = new SenseOnceThread[Location.length];
        for (int i = 0; i < Location.length; i++) {
            pullThreads[i] = new SenseOnceThread(this, sensorManager, logger, Location[i]);
            pullThreads[i].start();
        }
//        pullThreads = new SenseOnceThread[Magnetic.length];
//        for (int i = 0; i < Magnetic.length; i++)
//        {
//            pullThreads[i] = new SenseOnceThread(this, sensorManager, logger, Magnetic[i]);
//            pullThreads[i].start();
//        }
        pullThreads = new SenseOnceThread[MicroPhone.length];
        for (int i = 0; i < MicroPhone.length; i++) {
            pullThreads[i] = new SenseOnceThread(this, sensorManager, logger, MicroPhone[i]);
            pullThreads[i].start();
        }
        pullThreads = new SenseOnceThread[PhoneRadio.length];
        for (int i = 0; i < PhoneRadio.length; i++) {
            pullThreads[i] = new SenseOnceThread(this, sensorManager, logger, PhoneRadio[i]);
            pullThreads[i].start();
        }
        pullThreads = new SenseOnceThread[Sms.length];
        for (int i = 0; i < Sms.length; i++) {
            pullThreads[i] = new SenseOnceThread(this, sensorManager, logger, Sms[i]);
            pullThreads[i].start();
        }
        pullThreads = new SenseOnceThread[Wifi.length];
        for (int i = 0; i < Wifi.length; i++) {
            pullThreads[i] = new SenseOnceThread(this, sensorManager, logger, Wifi[i]);
            pullThreads[i].start();
        }

        //demo datamanager end in resume...........................................................


        super.onResume();
    }
    @Override
    protected void onPause() {
        // Unregister the broadcast receiver that was registered during onResume().
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
        //screen on off
        if (ScreenReceiver.screenOff) {
            // this is the case when onPause() is called by the system due to a screen state change
            System.out.println("SCREEN TURNED OFF");
            Long tsLong = System.currentTimeMillis() / 1000;
            String ts = tsLong.toString();
            String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
            // Toast.makeText(getApplicationContext(), " Activity screen is oFF", Toast.LENGTH_LONG).show();
            String screenOff = "App off" + "," + mydate + "," + ts;
            generateNoteOnSD(getApplicationContext(), screenOff);
        } else {
            // this is when onPause() is called when the screen state has not changed
            String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
            //  Toast.makeText(getApplicationContext(), " Activty screen is oFF", Toast.LENGTH_LONG).show();
            Long tsLong = System.currentTimeMillis() / 1000;
            String ts = tsLong.toString();
            String screenOff = "APP off" + "," + mydate + "," + ts;
            generateNoteOnSD(getApplicationContext(), screenOff);
            System.out.println("this is when onPause() is called when the screen state has not changed ");
        }
        // Don't forget to stop sensing when the app pauses......................................
        for (SubscribeThread thread : subscribeThreads) {
            thread.stopSensing();
        }
        super.onPause();
        // Don't forget to stop sensing when the app pauses......................................
    }
    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(LOG_TAG, "Connected to GoogleApiClient");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(LOG_TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(LOG_TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }
    /**
     * Registers for activity recognition updates using
     * {@link com.google.android.gms.location.ActivityRecognitionApi#requestActivityUpdates} which
     * returns a {@link com.google.android.gms.common.api.PendingResult}. Since this activity
     * implements the PendingResult interface, the activity itself receives the callback, and the
     * code within {@code onResult} executes. Note: once {@code requestActivityUpdates()} completes
     * successfully, the {@code DetectedActivitiesIntentService} starts receiving callbacks when
     * activities are detected.
     */
    public void requestActivityUpdatesButtonHandler(View view) {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(
                mGoogleApiClient,
                Constants.DETECTION_INTERVAL_IN_MILLISECONDS,
                getActivityDetectionPendingIntent()
        ).setResultCallback(this);
    }

    /**
     * Removes activity recognition updates using
     * {@link com.google.android.gms.location.ActivityRecognitionApi#removeActivityUpdates} which
     * returns a {@link com.google.android.gms.common.api.PendingResult}. Since this activity
     * implements the PendingResult interface, the activity itself receives the callback, and the
     * code within {@code onResult} executes. Note: once {@code removeActivityUpdates()} completes
     * successfully, the {@code DetectedActivitiesIntentService} stops receiving callbacks about
     * detected activities.
     */
    public void removeActivityUpdatesButtonHandler(View view) {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }
        // Remove all activity updates for the PendingIntent that was used to request activity
        // updates.
        ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(
                mGoogleApiClient,
                getActivityDetectionPendingIntent()
        ).setResultCallback(this);
    }

    /**
     * Runs when the result of calling requestActivityUpdates() and removeActivityUpdates() becomes
     * available. Either method can complete successfully or with an error.
     *
     * @param status The Status returned through a PendingIntent when requestActivityUpdates()
     *               or removeActivityUpdates() are called.
     */
    public void onResult(Status status) {
        if (status.isSuccess()) {
            // Toggle the status of activity updates requested, and save in shared preferences.
            boolean requestingUpdates = !getUpdatesRequestedState();
            setUpdatesRequestedState(requestingUpdates);

            // Update the UI. Requesting activity updates enables the Remove Activity Updates
            // button, and removing activity updates enables the Add Activity Updates button.
            setButtonsEnabledState();

            Toast.makeText(this, getString(requestingUpdates ? R.string.activity_updates_added : R.string.activity_updates_removed), Toast.LENGTH_SHORT).show();
        } else {
            Log.e(LOG_TAG, "Error adding or removing activity detection: " + status.getStatusMessage());
        }
    }

    /**
     * Gets a PendingIntent to be sent for each activity detection.
     */
    private PendingIntent getActivityDetectionPendingIntent() {
        Intent intent = new Intent(this, DetectedActivitiesIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // requestActivityUpdates() and removeActivityUpdates().
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
    /**
     * Ensures that only one button is enabled at any time. The Request Activity Updates button is
     * enabled if the user hasn't yet requested activity updates. The Remove Activity Updates button
     * is enabled if the user has requested activity updates.
     */
    private void setButtonsEnabledState() {
        if (getUpdatesRequestedState()) {
            mRequestActivityUpdatesButton.setEnabled(false);
            mRemoveActivityUpdatesButton.setEnabled(true);
        } else {
            mRequestActivityUpdatesButton.setEnabled(true);
            mRemoveActivityUpdatesButton.setEnabled(false);
        }
    }

    /**
     * Retrieves a SharedPreference object used to store or read values in this app. If a
     * preferences file passed as the first argument to {@link #getSharedPreferences}
     * does not exist, it is created when {@link SharedPreferences.Editor} is used to commit
     * data.
     */
    private SharedPreferences getSharedPreferencesInstance() {
        return getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
    }

    /**
     * Retrieves the boolean from SharedPreferences that tracks whether we are requesting activity
     * updates.
     */
    private boolean getUpdatesRequestedState() {
        return getSharedPreferencesInstance()
                .getBoolean(Constants.ACTIVITY_UPDATES_REQUESTED_KEY, false);
    }

    /**
     * Sets the boolean in SharedPreferences that tracks whether we are requesting activity
     * updates.
     */
    private void setUpdatesRequestedState(boolean requestingUpdates) {
        getSharedPreferencesInstance()
                .edit()
                .putBoolean(Constants.ACTIVITY_UPDATES_REQUESTED_KEY, requestingUpdates)
                .commit();
    }

    /**
     * Stores the list of detected activities in the Bundle.
     */
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable(Constants.DETECTED_ACTIVITIES, mDetectedActivities);
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Processes the list of freshly detected activities. Asks the adapter to update its list of
     * DetectedActivities with new {@code DetectedActivity} objects reflecting the latest detected
     * activities.
     */
    protected void updateDetectedActivitiesList(ArrayList<DetectedActivity> detectedActivities) {
        mAdapter.updateActivities(detectedActivities);
    }

    //Emotion sensing main class copied here
    private void startPull() {
        new SenseFromAllPullSensorsTask(this) {
            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                startEnvironment();
            }
        }.execute();
    }

    private void startEnvironment() {
        new SenseFromAllEnvSensorsTask(this) {
            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                startPush();
            }
        }.execute();
    }

    private void startPush() {
        new SenseFromAllPushSensorsTask(this).execute();
    }

    //    ///////////////////////////////////////////////////////
//    @Override
//    public void unregisterReceiver(BroadcastReceiver receiver) {
//        super.unregisterReceiver(mBroadcastReceiver);
//    }
    public void generateNoteOnSD(Context context, String sBody) {
        try {
            String content = sBody;
            String dir = Environment.getExternalStorageDirectory() + File.separator + "myDirectory";
            //create folder
            File folder = new File(dir); //folder name
            folder.mkdirs();
            //create file
            File file = new File(dir, "App_state.txt");
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


    //netwoek availability for location trace
    private boolean isNetworkAvailable() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public boolean isLocationServiceEnabled() {
        LocationManager locationManager = null;
        boolean gps_enabled = false, network_enabled = false;

        if (locationManager == null)
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            //do nothing...
        }
        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            //do nothing...
        }
        return gps_enabled || network_enabled;
    }

    public void onSearchClicked(final View view) {
        // Counts the number of sensor events from the last 60 seconds
        //long startTime = System.currentTimeMillis() - (1000L * 60);
        try {
            long startTime = System.currentTimeMillis() - (15000);
            ESDataManager dataManager = logger.getDataManager();

            for (int pushSensor : pushSensors) {
                List<SensorData> recentData = dataManager.getRecentSensorData(pushSensor, startTime);
                Toast.makeText(this, "Recent " + SensorUtils.getSensorName(pushSensor) + ": " + recentData.size(), Toast.LENGTH_LONG).show();
            }
            for (int pushSensor : Battery) {
                List<SensorData> recentData = dataManager.getRecentSensorData(pushSensor, startTime);
                Toast.makeText(this, "Recent " + SensorUtils.getSensorName(pushSensor) + ": " + recentData.size(), Toast.LENGTH_LONG).show();
            }

            for (int pushSensor : pullSensors) {
                List<SensorData> recentData = dataManager.getRecentSensorData(pushSensor, startTime);
                Toast.makeText(this, "Recent " + SensorUtils.getSensorName(pushSensor) + ": " + recentData.size(), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error retrieving sensor data", Toast.LENGTH_LONG).show();
            Log.d(LOG_TAG, e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public void onFlushClicked(final View view) {
        // Tries to POST all of the stored sensor data to the server
        try {
            ESDataManager dataManager = logger.getDataManager();
            dataManager.postAllStoredData(this);
        } catch (DataHandlerException e) {
            Toast.makeText(this, "Exception: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            Log.d(LOG_TAG, "" + e.getLocalizedMessage());
        }
    }

    @Override
    public void onDataUploaded() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // Callback method: the data has been successfully posted
                Toast.makeText(MainActivity.this, "Data transferred.", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDataUploadFailed() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // Callback method: the data has not been successfully posted
                Toast.makeText(MainActivity.this, "Error transferring data", Toast.LENGTH_LONG).show();
            }
        });
    }
    /**
     * Receiver for intents sent by DetectedActivitiesIntentService via a sendBroadcast().
     * Receives a list of one or more DetectedActivity objects associated with the current state of
     * the device.
     */
    public class ActivityDetectionBroadcastReceiver extends BroadcastReceiver {
        protected static final String TAG = "activity-detection-response-receiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<DetectedActivity> updatedActivities =
                    intent.getParcelableArrayListExtra(Constants.ACTIVITY_EXTRA);
            updateDetectedActivitiesList(updatedActivities);
        }
    }
}

