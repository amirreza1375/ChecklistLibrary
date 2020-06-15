package com.example.checklist.BaseViewModel;

import org.json.JSONArray;
import org.json.JSONObject;

public interface IConditionChangeListener {
    void onCheckBoxConditionChanged(JSONArray data,int position);
    void onRadioGroupConditionChanged(JSONObject data, int position);
}
