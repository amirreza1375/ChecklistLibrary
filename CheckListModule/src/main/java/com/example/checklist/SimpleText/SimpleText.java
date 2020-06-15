package com.example.checklist.SimpleText;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.checklist.BaseViewModel.BaseView;
import com.example.checklist.BaseViewModel.ElemetActionListener;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.checklist.GlobalFuncs.conf_html;
import static com.example.checklist.GlobalFuncs.dpToPx;
import static com.example.checklist.GlobalFuncs.setOrgProps;

public class SimpleText extends BaseView {
    private  Context context;
    private  JSONObject element;

    public SimpleText(Context context, JSONObject element, ElemetActionListener callBack) {
        super(context,callBack);
        this.context = context;
        this.element = element;
        init();
    }

    public SimpleText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SimpleText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    private void init(){
        setOrgProps(context,this);

        TextView textView = new TextView(context);
        textView.setTextColor(Color.BLACK);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(dpToPx(8,context),dpToPx(8,context),dpToPx(8,context),dpToPx(8,context));
        textView.setLayoutParams(params);
        try {
            textView.setText(element.getString(conf_html));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        addView(textView);
    }
}
