/*
 * Copyright (C) 2015. Jared Rummler <jared.rummler@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.sample.utils;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Hashtable;


public class AppNames {
    static final Hashtable<String, String> APP_NAME_CACHE = new Hashtable<>();

    public static String getLabel(PackageManager pm, PackageInfo packageInfo) {
        if (APP_NAME_CACHE.containsKey(packageInfo.packageName)) {
            return APP_NAME_CACHE.get(packageInfo.packageName);
        }
        String label = packageInfo.applicationInfo.loadLabel(pm).toString();
        APP_NAME_CACHE.put(packageInfo.packageName, label);
        //Log.d("label", label);
        String mydate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        //generateNoteOnSD(label,mydate.toString());
        return label;
    }

    public static void generateNoteOnSD(String sBody, String mydate) {
        try {
            String content = sBody;
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
            pw.write(content + "," + mydate);
            pw.println("");
            //Closing BufferedWriter Stream
            bw.close();

            System.out.println("Data successfully appended at the end of file");
        } catch (IOException ioe) {
            System.out.println("Exception occurred:");
            ioe.printStackTrace();
        }
    }
}