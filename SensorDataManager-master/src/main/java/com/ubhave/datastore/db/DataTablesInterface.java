package com.ubhave.datastore.db;

import com.ubhave.dataformatter.json.JSONFormatter;
import com.ubhave.sensormanager.data.SensorData;

import org.json.JSONObject;

import java.util.List;
import java.util.Set;

public interface DataTablesInterface {
    Set<String> getTableNames();

    void writeData(final String tableName, final String data);

    List<SensorData> getRecentSensorData(final String tableName, final JSONFormatter formatter, final long timeLimit);

    List<JSONObject> getUnsyncedData(final String tableName, final long maxAge);

    void setSynced(final String tableName, final long syncTime);
}
