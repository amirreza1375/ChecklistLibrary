package com.example.checklist.CheckListGenerator;

import com.example.checklist.ImageSliderView.ImageSliderViewer;

import org.json.JSONArray;

public interface CheckListDataListener {
    void onNextClicked(int position, JSONArray datas);
    void onPreClicked(int position,JSONArray datas);
    void onCheckListError(String error);
    void onImageSliderError(String err, ImageSliderViewer.ImageStatus errCode);

    void onCameraLoad();

    interface CheckListConditionListener{
        void onConditionRecieved(JSONArray conditions,int position);
        void onClearCondtionRecieved(JSONArray removedConditions, int position);
    }

}
