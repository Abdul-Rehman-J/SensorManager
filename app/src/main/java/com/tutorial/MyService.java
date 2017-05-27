package com.tutorial;

/**
 * Created by abdul on 30/03/2017.
 */

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by roberto on 9/29/16.
 */

public class MyService extends Service {
    private static final String TAG = "MyLocationService";
    private static final int LOCATION_INTERVAL = 1000 * 60;// 1000 * 60 * 10 ten minutes
    private static final float LOCATION_DISTANCE = 0f;
    public static double accuracy = 0;
    private static double lat = 0;
    private static double lon = 0;
    RequestQueue requestQueue;
    /*
   LocationListener[] mLocationListeners = new LocationListener[]{
           new LocationListener(LocationManager.GPS_PROVIDER),
           new LocationListener(LocationManager.NETWORK_PROVIDER)
   };
   */
    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.PASSIVE_PROVIDER)
    };
    private LocationManager mLocationManager = null;

    public static void generateNoteOnSD(long statTime, SimpleDateFormat date, String name) {
        try {
            String dir = Environment.getExternalStorageDirectory() + File.separator + "myDirectory";
            //create folder
            File folder = new File(dir); //folder name
            folder.mkdirs();
            //create file
            File file = new File(dir, "Recent Apps info.txt");
            FileWriter fw = new FileWriter(file, true);
            //BufferedWriter writer give better performance
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);
            pw.write(name + "," + "," + statTime + "," + date);
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
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        //Log.e(TAG, "onCreate");
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.PASSIVE_PROVIDER,
                    LOCATION_INTERVAL,
                    LOCATION_DISTANCE,
                    mLocationListeners[0]
            );
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }

    }
    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listener, ignore", ex);
                }
            }
        }
    }
    private void initializeLocationManager() {
        Log.w(TAG, "initializeLocationManager - LOCATION_INTERVAL: " + LOCATION_INTERVAL + " LOCATION_DISTANCE: " + LOCATION_DISTANCE);
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }


    //    private Spanned getProcessInfo(AndroidAppProcess process) {
//        HtmlBuilder html = new HtmlBuilder();
//
//        html.p().strong("NAME: ").append(process.name).close();
//        Log.i("asd", String.valueOf(process.name));
//        html.p().strong("POLICY: ").append(process.foreground ? "fg" : "bg").close();
//        html.p().strong("PID: ").append(process.pid).close();
//
//        try {
//            Status status = process.status();
//            html.p().strong("UID/GID: ").append(status.getUid()).append('/').append(status.getGid()).close();
//        } catch (IOException e) {
//            Log.d(TAG, String.format("Error reading /proc/%d/status.", process.pid));
//        }
//
//        // should probably be run in a background thread.
//        long startTime = 0;
//        SimpleDateFormat sdf = null;
//        try {
//            Stat stat = process.stat();
//            html.p().strong("PPID: ").append(stat.ppid()).close();
//            long bootTime = System.currentTimeMillis() - SystemClock.elapsedRealtime();
//            startTime = bootTime + (10 * stat.starttime());
//            Log.i("asd", String.valueOf(startTime));
//            sdf = new SimpleDateFormat("MMM d, yyyy KK:mm:ss a", Locale.getDefault());
//            html.p().strong("START TIME: ").append(sdf.format(startTime)).close();
//            html.p().strong("CPU TIME: ").append((stat.stime() + stat.utime()) / 100).close();
//            html.p().strong("NICE: ").append(stat.nice()).close();
//            int rtPriority = stat.rt_priority();
//            if (rtPriority == 0) {
//                html.p().strong("SCHEDULING PRIORITY: ").append("non-real-time").close();
//            } else if (rtPriority >= 1 && rtPriority <= 99) {
//                html.p().strong("SCHEDULING PRIORITY: ").append("real-time").close();
//            }
//            long userModeTicks = stat.utime();
//            long kernelModeTicks = stat.stime();
//            long percentOfTimeUserMode;
//            long percentOfTimeKernelMode;
//            if ((kernelModeTicks + userModeTicks) > 0) {
//                percentOfTimeUserMode = (userModeTicks * 100) / (userModeTicks + kernelModeTicks);
//                percentOfTimeKernelMode = (kernelModeTicks * 100) / (userModeTicks + kernelModeTicks);
//                html.p().strong("TIME EXECUTED IN USER MODE: ").append(percentOfTimeUserMode + "%").close();
//                html.p().strong("TIME EXECUTED IN KERNEL MODE: ").append(percentOfTimeKernelMode + "%").close();
//            }
//        } catch (IOException e) {
//            Log.d(TAG, String.format("Error reading /proc/%d/stat.", process.pid));
//        }
//
//        try {
//            Statm statm = process.statm();
//            html.p().strong("SIZE: ").append(Formatter.formatFileSize(getApplicationContext(), statm.getSize())).close();
//            html.p().strong("RSS: ").append(Formatter.formatFileSize(getApplicationContext(), statm.getResidentSetSize())).close();
//        } catch (IOException e) {
//            Log.d(TAG, String.format("Error reading /proc/%d/statm.", process.pid));
//        }
//
//        try {
//            html.p().strong("OOM SCORE: ").append(process.oom_score()).close();
//        } catch (IOException e) {
//            Log.d(TAG, String.format("Error reading /proc/%d/oom_score.", process.pid));
//        }
//
//        try {
//            html.p().strong("OOM ADJ: ").append(process.oom_adj()).close();
//        } catch (IOException e) {
//            Log.d(TAG, String.format("Error reading /proc/%d/oom_adj.", process.pid));
//        }
//
//        try {
//            html.p().strong("OOM SCORE ADJ: ").append(process.oom_score_adj()).close();
//        } catch (IOException e) {
//            Log.d(TAG, String.format("Error reading /proc/%d/oom_score_adj.", process.pid));
//        }
//        generateNoteOnSD(startTime, sdf,process.name.toString());
//        return html.toSpan();
//    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE, final double accuracy) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder(" ");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i));
                    if (i + 1 < returnedAddress.getMaxAddressLineIndex()) {
                        strReturnedAddress.append(",");
                    }
                }

                //    Log.w("My Current loction address", "" + strReturnedAddress.toString());
                generateOnSD(getApplicationContext(), strReturnedAddress.toString(), accuracy);
                g(getApplicationContext(), strReturnedAddress.toString(), accuracy);
            } else {
                Log.w("My Current loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current loction address", "Canont get Address!");
        }
        return strAdd;
    }

    public void generateOnSD(Context context, String sBody, double accuracy) {
        try {
            String content = sBody;
            Log.d("s", sBody);
            String dir = Environment.getExternalStorageDirectory() + File.separator + "myDirectory";
            //create folder
            File folder = new File(dir); //folder name
            folder.mkdirs();
            //create file
            File file = new File(dir, "Location Trace.txt");
            FileWriter fw = new FileWriter(file, true);
            //BufferedWriter writer give better performance
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);
            String mydate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
            Long tsLong = System.currentTimeMillis() / 1000;
            String ts = tsLong.toString();
            pw.write(content + "," + mydate + "," + accuracy + "," + ts);
            pw.println("");
            //Cosing BufferedWriter Stream
            bw.close();
            System.out.println("Data successfully appended at the end of file");
        } catch (IOException ioe) {
            System.out.println("Exception occurred:");
            ioe.printStackTrace();
        }
    }

    public void g(Context context, String sBody, double accuracy) {
        try {
            String content = sBody;
            Log.d("s", sBody);
            String dir = Environment.getExternalStorageDirectory() + File.separator + "myDirectory";
            //create folder
            File folder = new File(dir); //folder name
            folder.mkdirs();
            //create file
            File file = new File(dir, "Location_New.txt");
            FileWriter fw = new FileWriter(file, true);
            //BufferedWriter writer give better performance
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);
            String mydate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
            Long tsLong = System.currentTimeMillis() / 1000;
            String ts = tsLong.toString();
            pw.write(content + "," + mydate + "," + accuracy + "," + ts);
            pw.println("");
            //Cosing BufferedWriter Stream
            bw.close();
            System.out.println("Data successfully appended at the end of file");
        } catch (IOException ioe) {
            System.out.println("Exception occurred:");
            ioe.printStackTrace();
        }
    }

    public class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);
            lat = location.getLatitude();
            lon = location.getLongitude();
            accuracy = location.getAccuracy();
            //   Toast.makeText(getApplicationContext(), "location detected", Toast.LENGTH_LONG).show();
            Log.w("asd", String.valueOf(lat));
            getCompleteAddressString(lat, lon, accuracy);
            mLastLocation.set(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }


}