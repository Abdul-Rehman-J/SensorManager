package com.ubhave.sensormanager.config.pull;

import com.ubhave.sensormanager.config.SensorConfig;

/**
 * Created by abdul on 19/03/2017.
 */

public class AccelerometerConfig {
    /*
     * Config Keys
	 */
    public static final String ACC_FILES_DIRECTORY = "ACC_FILES_DIRECTORY";
    public static final String SAMPLING_RATE = "SAMPLING_RATE";
    public static final String SOUND_THRESHOLD = "SOUND_THRESHOLD";
    public static final long DEFAULT_SLEEP_INTERVAL = 2 * 60 * 1000;
    public static final int DEFAULT_SAMPLING_RATE = 20000;
    /*
     * Default values
     */
    private static final long DEFAULT_SAMPLING_WINDOW_SIZE_MILLIS = 5000;
    private static final int DEFAULT_MOTION_THRESHOLD = 800;

    public static SensorConfig getDefault() {
        SensorConfig sensorConfig = new SensorConfig();
        sensorConfig.setParameter(PullSensorConfig.POST_SENSE_SLEEP_LENGTH_MILLIS, DEFAULT_SLEEP_INTERVAL);
        sensorConfig.setParameter(PullSensorConfig.SENSE_WINDOW_LENGTH_MILLIS, DEFAULT_SAMPLING_WINDOW_SIZE_MILLIS);
        sensorConfig.setParameter(SAMPLING_RATE, DEFAULT_SAMPLING_RATE);
        sensorConfig.setParameter(MotionSensorConfig.MOTION_THRESHOLD, DEFAULT_MOTION_THRESHOLD);
        sensorConfig.setParameter(ACC_FILES_DIRECTORY, null);
        return sensorConfig;
    }
}
