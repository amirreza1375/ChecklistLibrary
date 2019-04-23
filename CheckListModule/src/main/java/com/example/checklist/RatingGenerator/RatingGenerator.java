package com.example.checklist.RatingGenerator;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.checklist.MultiTextGenerator.MultiText;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.checklist.GlobalFuncs.conf_id;
import static com.example.checklist.GlobalFuncs.conf_isRequired;
import static com.example.checklist.GlobalFuncs.conf_name;
import static com.example.checklist.GlobalFuncs.conf_title;
import static com.example.checklist.GlobalFuncs.createTitle;
import static com.example.checklist.GlobalFuncs.dpToPx;
import static com.example.checklist.GlobalFuncs.setOrgProps;

public class RatingGenerator extends LinearLayout implements RatingBar.OnRatingBarChangeListener {

    //region variables
    private JSONObject element;
    private boolean enable;
    private Context context;
    private MultiText.MandatoryListener listener;
    //endregion

    //region user variables
    private boolean isRequired;
    private int starNumbers = 3;
    private String title;
    private String id;
    private String name;
    private int answer;
    private RatingBar ratingBar;
    //endregion

    //region constructors
    public RatingGenerator(Context context, JSONObject element
    ,boolean enable,int answer) {
        super(context);
        this.context = context;
        this.element = element;
        this.enable = enable;
        this.answer = answer;
        init(context);
    }

    public RatingGenerator(Context context,  AttributeSet attrs) {
        super(context, attrs);
    }

    public RatingGenerator(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RatingGenerator(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    //endregion

    private void init(Context context){

        getElementVariables(element);

        //region org porps
        setOrgProps(context,this);
        //endregion

        //region title
        TextView titleTxt = createTitle(context,isRequired,element);
        //endregion

        //region rating
        LayoutParams ratingParams = new LayoutParams(LayoutParams.WRAP_CONTENT
        ,LayoutParams.WRAP_CONTENT);
        ratingParams.setMargins(dpToPx(8, context), dpToPx(8, context)
                , dpToPx(8, context), dpToPx(0, context));
        ratingBar = new RatingBar(context);
        ratingBar.setRating(answer);
        ratingBar.setStepSize(1.0f);
        ratingBar.setEnabled(enable);
        ratingBar.setOnRatingBarChangeListener(this);
        //endregion

        //region add views
        addView(titleTxt);
        addView(ratingBar);
        //endregion

    }

    public int getValue(){
        return ratingBar.getProgress();
    }

    private void getElementVariables(JSONObject element) {
        try {
            id = element.has(conf_id) ? element.getString(conf_id) : "no title";
            title = element.has(conf_title) ? element.getString(conf_title) : "no title";
            name = element.has(conf_name) ? element.getString(conf_name) : "no name";
            isRequired = element.has(conf_isRequired) && element.getBoolean(conf_isRequired);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getElementId(){
        return id;
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        listener.onElementStatusChanged();
    }
}
