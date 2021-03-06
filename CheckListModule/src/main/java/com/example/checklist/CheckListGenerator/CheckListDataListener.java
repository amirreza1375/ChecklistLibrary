package com.example.checklist.CheckListGenerator;

import com.example.checklist.ImageSliderView.ImagesViewer;

import org.json.JSONArray;

public interface CheckListDataListener {
    void onNextClicked(int position, JSONArray datas);
    void onPreClicked(int position,JSONArray datas);
    void onCheckListError(String error);
    void onImageSliderError(String err, ImagesViewer.ImageStatus errCode);

    void onCameraLoad();

    void onChecklistLoadStarted();
    void onChecklistLoadFinished();

    void onConditionaryDataChanged(String name, String value, boolean isChecked, String type);
    void onViewHidden(String name,  String type);

    interface CheckListConditionListener{
        void onConditionRecieved(JSONArray conditions,int position);
        void onClearCondtionRecieved(JSONArray removedConditions, int position);
    }

}
