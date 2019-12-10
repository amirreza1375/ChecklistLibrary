package com.example.checklist.BaseViewModel;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class BaseView extends LinearLayout {

    protected String viewID;
    protected boolean isVisibleSi;
    protected String visibleSiName;
    protected String visibleSiValue;
    protected String visibleSi;
    protected boolean isShowen;

    public BaseView(Context context) {
        super(context);
    }

    public BaseView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private String[] getVisibleSiInArray(String visibleSi) {
        if (visibleSi != null) {
            String[] visibleSies = visibleSi.split("contains");
            return visibleSies;
        }else{
            return new String[]{};
        }
    }

    private void setVisibleSiData(String[] visibleSiData) {
        if (visibleSiData.length >= 2) {
            this.visibleSiName = visibleSiData[0].trim().substring(1, visibleSiData[0].length() - 2);
            this.visibleSiValue = visibleSiData[1];
        }
    }


    public String getElementId() {
        return viewID;
    }

    public void setId(String viewID) {
        this.viewID = viewID;
    }

    public boolean isVisibleSi() {
        return isVisibleSi;
    }

    public void setVisibleSi(boolean visibleSi) {
        isVisibleSi = visibleSi;
    }

    public String getVisibleSiName() {
        setVisibleSiData(getVisibleSiInArray(visibleSi));
        return visibleSiName;
    }

    public void setVisibleSiName(String visibleSiName) {
        this.visibleSiName = visibleSiName;
    }

    public String getVisibleSiValue() {
        setVisibleSiData(getVisibleSiInArray(visibleSi));
        return visibleSiValue;
    }

    public void setVisibleSiValue(String visibleSiValue) {
        this.visibleSiValue = visibleSiValue;
    }

    public void setVisibleSi(String visibleSi) {
        this.visibleSi = visibleSi;
    }

    public String getVisibleSi() {
        return visibleSi;
    }

    public boolean isShowen() {
        return isShowen;
    }

    public void setShowen(boolean showen) {
        isShowen = showen;
    }
}
