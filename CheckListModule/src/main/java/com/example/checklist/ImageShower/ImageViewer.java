package com.example.checklist.ImageShower;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.checklist.BaseViewModel.BaseView;
import com.example.checklist.BaseViewModel.ElemetActionListener;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.checklist.GlobalFuncs.createTitle;
import static com.example.checklist.GlobalFuncs.setOrgProps;

public class ImageViewer extends BaseView {


    private JSONObject element;

    public ImageViewer(Context context, JSONObject element, ElemetActionListener callBack) {
        super(context,callBack);
        this.element = element;
        init(context);
    }

    public ImageViewer(Context context,  AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ImageViewer(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context){
        try {
            visibleSi = element.getString("visibleIf");
            isVisibleSi = true;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        setOrgProps(context,this);

        TextView titleTxt = createTitle(context,false,element);

        ImageView imageView = new ImageView(context);



        addView(titleTxt);

        addView(imageView);

    }

}
