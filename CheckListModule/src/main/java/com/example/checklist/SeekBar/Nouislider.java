package com.example.checklist.SeekBar;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.checklist.MultiTextGenerator.MultiText;
import com.example.checklist.R;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.checklist.GlobalFuncs.dpToPx;
import static com.example.checklist.PageGenerator.CheckListPager.setMandatories;

public class Nouislider extends LinearLayout {

    //region element keys
    private String conf_title = "title";
    private String conf_rangeMax = "rangeMax";
    private String conf_rangeMin = "rangeMin";
    private String conf_required = "isRequired";
    private String conf_name = "name";
    private String conf_id = "id";
    //endregion

    //region variables
    private JSONObject element;
    private int answer;
    private boolean enable;
    private Context context;
    private MultiText.MandatoryListener listener;
    //endregion

    //region used variables
    private String name ;
    private String id;
    private boolean isRequired;
    private int rangMin = 0;
    private int rangeMax = 0;
    private SeekBar seekBar;
    private TextView counterTxt;
    //endregion

    //region constructors
    public Nouislider(Context context, JSONObject element,int answer,boolean enable) {
        super(context);
        this.context = context;
        this.element = element;
        this.answer = answer;
        this.enable = enable;
        init(context);
    }

    public Nouislider(Context context,  AttributeSet attrs) {
        super(context, attrs);
    }

    public Nouislider(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public Nouislider(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    //endregion

    private void init(Context context) {
        try {

            getSeekBarPropsFromElement(element);

            //region org props
            LayoutParams orgParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            orgParams.setMargins(dpToPx(8, context), dpToPx(8, context)
                    , dpToPx(8, context), dpToPx(8, context));
            setOrientation(VERTICAL);
            setLayoutParams(orgParams);
            //endregion

            //region seekbar props
            LayoutParams seekParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            seekParams.setMargins(dpToPx(8, context), dpToPx(8, context)
                    , dpToPx(8, context), dpToPx(8, context));
            seekBar = new SeekBar(context);
            seekBar.setLayoutParams(seekParams);
            seekBar.setProgress(answer == -1 ? rangMin : answer);
            seekBar.setMax(rangeMax);
            //endregion

            //region title props
            LayoutParams titleParams = new LayoutParams(0, LayoutParams.WRAP_CONTENT);
            titleParams.setMargins(dpToPx(8, context), dpToPx(8, context)
                    , dpToPx(8, context), dpToPx(8, context));
            titleParams.weight = 1.0f;
            titleParams.gravity = Gravity.START;

            TextView titleTxt = new TextView(context);
            titleTxt.setTextSize(16);
            titleTxt.setTextColor(Color.BLACK);
            titleTxt.setLayoutParams(titleParams);
            if (isRequired)
                titleTxt.setText(getTitleFromElement(element)+"*");
            else
                titleTxt.setText(getTitleFromElement(element));
            //endregion

            //region counter props
            LayoutParams counterParams = new LayoutParams(0, LayoutParams.WRAP_CONTENT);
            counterParams.setMargins(dpToPx(8, context), dpToPx(8, context)
                    , dpToPx(8, context), dpToPx(8, context));
            counterParams.weight = 0.2f;
            titleParams.gravity = Gravity.END;

            counterTxt = new TextView(context);
            counterTxt.setTextSize(16);
            counterTxt.setTextAlignment(TextView.TEXT_ALIGNMENT_TEXT_END);
            counterTxt.setTextColor(Color.BLACK);
            counterTxt.setLayoutParams(counterParams);

            if (answer <= 0)
                counterTxt.setText(rangMin+"");
            else
                counterTxt.setText(answer+"");
            //endregion

            //region title counter holder props
            LinearLayout holder = new LinearLayout(context);
            holder.setOrientation(HORIZONTAL);
            LayoutParams holderParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            holderParams.setMargins(dpToPx(8, context), dpToPx(8, context), dpToPx(8, context), dpToPx(0, context));
            holder.setLayoutParams(holderParams);
            //endregion

            handleSeekMinValue(context,seekBar,counterTxt);

            //region footer props
            LinearLayout footer = new LinearLayout(context);
            footer.setOrientation(HORIZONTAL);

            LayoutParams footerParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            footerParams.setMargins(dpToPx(8, context), dpToPx(8, context), dpToPx(8, context), dpToPx(0, context));
            footer.setLayoutParams(footerParams);

            //region footer max text
            LayoutParams maxParams = new LayoutParams(0, LayoutParams.WRAP_CONTENT);
            maxParams.setMargins(dpToPx(8, context), dpToPx(8, context)
                    , dpToPx(8, context), dpToPx(8, context));
            maxParams.weight = 1;
            maxParams.gravity = Gravity.END;

            TextView maxTxt = new TextView(context);
            maxTxt.setTextSize(16);
            maxTxt.setTextAlignment(TextView.TEXT_ALIGNMENT_TEXT_END);
            maxTxt.setTextColor(Color.BLACK);
            maxTxt.setLayoutParams(maxParams);
            maxTxt.setText("max : "+rangeMax);
            //endregion

            //region footer min text
            LayoutParams minParams = new LayoutParams(0, LayoutParams.WRAP_CONTENT);
            minParams.setMargins(dpToPx(8, context), dpToPx(8, context)
                    , dpToPx(8, context), dpToPx(8, context));
            minParams.weight = 1;
            minParams.gravity = Gravity.START;

            TextView minTxt = new TextView(context);
            minTxt.setTextSize(16);
            minTxt.setTextColor(Color.BLACK);
            minTxt.setLayoutParams(minParams);
            minTxt.setText("min : "+rangMin);
            //endregion

            //endregion



            //region add view
            footer.addView(minTxt);
            footer.addView(maxTxt);
            holder.addView(titleTxt);
            holder.addView(counterTxt);
            addView(holder);
            addView(seekBar);
            addView(footer);
            //endregion

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public int getValue(){
        isMandatoryAnswered();
        return seekBar.getProgress();
    }

    private void isMandatoryAnswered() {
        //not useful fot here
    }

    public void setMandatoryError(){
        if (setMandatories)
            setBackground(context.getResources().getDrawable(R.drawable.is_requiered));
    }

    private void handleSeekMinValue(final Context context, SeekBar seekBar , final TextView prgrssTxt){
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                listener.onElementStatusChanged();
                seekBar.setBackground(null);

                if (progress < rangMin){
                    seekBar.setProgress(rangMin);
                    prgrssTxt.setText(rangMin+"");
                }else {
                    prgrssTxt.setText(progress+"");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }



    private void getSeekBarPropsFromElement(JSONObject element) {
        try {
            id = element.has(conf_id) ? element.getString(conf_id) : "";
            name = element.has(conf_name) ? element.getString(conf_name) : "";
            rangeMax = element.has(conf_rangeMax) ? element.getInt(conf_rangeMax) : 100;
            rangMin = element.has(conf_rangeMin) ? element.getInt(conf_rangeMin) : 0;
            isRequired = element.has(conf_required) ? element.getBoolean(conf_required) : false;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String  getTitleFromElement(JSONObject element) {

        try {
            return element.getString(conf_title);
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public int getAnswer() {
        return answer;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public JSONObject getElement() {
        return element;
    }

    public String getElementId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setListener(MultiText.MandatoryListener listener) {
        this.listener = listener;
    }
}
