package com.example.commentario.ImageShower;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import static com.example.commentario.GlobalFuncs.createTitle;
import static com.example.commentario.GlobalFuncs.setOrgProps;

public class ImageViewer extends LinearLayout {


    private JSONObject element;

    public ImageViewer(Context context, JSONObject element) {
        super(context);
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

    public ImageViewer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context){

        setOrgProps(context,this);

        TextView titleTxt = createTitle(context,false,element);

        ImageView imageView = new ImageView(context);



        addView(titleTxt);

        addView(imageView);

    }

}
