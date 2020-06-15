package com.example.checklist.BaseViewModel;

public interface ElemetActionListener {
    void onAction(String name,String id,String data,int pagePosition);
    void onConditionaryDataChanged(String id,String value,boolean isChecked,String type);
    void isHiddenView();
}
