package com.example.checklist.Database;

import org.json.JSONArray;

public interface LogMonitorDataListener {
    void onDataRecieved(JSONArray data);
    void onDataEmpty();
}
