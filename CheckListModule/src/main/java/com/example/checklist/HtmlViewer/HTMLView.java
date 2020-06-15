package com.example.checklist.HtmlViewer;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.example.checklist.BaseViewModel.BaseViewModel;
import com.example.checklist.BaseViewModel.ElemetActionListener;
import com.example.checklist.R;

import org.json.JSONObject;

public class HTMLView extends BaseViewModel {
    public HTMLView(Context context, JSONObject element, ElemetActionListener callBack, JSONObject viewAnswer, boolean isEnabled,int elementPosition,int viewPosition) {
        super(context, element, callBack, viewAnswer, isEnabled,elementPosition,viewPosition);
    }

    public HTMLView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HTMLView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public JSONObject getValue() {
        return null;
    }

    @Override
    public LinearLayout InitilizeView(Context context) {
        return this;
    }

    @Override
    public void clearData() {

    }

    @Override
    public void getElementProps() {

    }

    @Override
    public int getView() {
        return R.layout.layout_html_view;
    }

    @Override
    public void getAnswer(JSONObject answer) {

    }

    @Override
    public void viewAnswered() {

    }

    @Override
    public void viewAnswerRemoved() {

    }
}
