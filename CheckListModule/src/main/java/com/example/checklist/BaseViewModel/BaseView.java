package com.example.checklist.BaseViewModel;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

import com.example.checklist.R;


public abstract class BaseView extends LinearLayout {

    private static final String TAG = "BaseView";

    protected String viewID;
    protected boolean isVisibleSi;
    protected String visibleSiName;
    protected String visibleSiValue;
    protected String visibleSi;
    protected boolean isShowen = true;
    protected String name;
    protected boolean isViewAnswered;
    protected boolean isMandatory;
    protected ElemetActionListener callBack;

    public BaseView(Context context,ElemetActionListener callBack) {
        super(context);
        this.callBack = callBack;
        Log.i(TAG, "BaseView: ");
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
        } else {
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
        String[] VS = getVisibleSiInArray(visibleSi);
        if (VS.length > 0)
            if (VS[0].length() > 2)
                return VS[0].trim().substring(1, VS[0].length() - 2);
        return "";
    }

    public void setVisibleSiName(String visibleSiName) {
        this.visibleSiName = visibleSiName;
    }

    public String getVisibleSiValue() {
        String[] VS = getVisibleSiInArray(visibleSi);
        if (VS.length > 0)
            return VS[1];
        return "";
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isViewAnswered() {
        return isViewAnswered;
    }

    public void setViewAnswered(boolean viewAnswered) {
        isViewAnswered = viewAnswered;
    }

    public boolean isMandatory() {
        return isMandatory;
    }

    public void setMandatory(boolean mandatory) {
        isMandatory = mandatory;
    }

    public  void checkMandatoryAndSetStatus(Context context){
        if (isMandatory) {
            if (!isViewAnswered) {
                setmandatoryError(context);
            }
        }
    }

    private void setmandatoryError(Context context) {
        this.setBackground(context.getResources().getDrawable(R.drawable.is_requiered));
    }
}
