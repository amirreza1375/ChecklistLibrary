package com.example.commentario.RadioGroup;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.commentario.GlobalFuncs;
import com.example.commentario.MultiTextGenerator.MultiText;
import com.example.commentario.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.commentario.GlobalFuncs.conf_choices;
import static com.example.commentario.GlobalFuncs.conf_disableOthers;
import static com.example.commentario.GlobalFuncs.conf_id;
import static com.example.commentario.GlobalFuncs.conf_isRequired;
import static com.example.commentario.GlobalFuncs.conf_name;
import static com.example.commentario.GlobalFuncs.conf_text;
import static com.example.commentario.GlobalFuncs.conf_title;
import static com.example.commentario.GlobalFuncs.setOrgProps;
import static com.example.commentario.PageGenerator.CheckListPager.setMandatories;

public class RadioGroupGenerator extends LinearLayout {

    //region variables
    private Context context;
    private JSONObject element;
    private boolean isRequired = false ;
    private boolean enabled;
    private JSONObject answer;
    private int position;
    //endregion

    //region used variables
    private MultiText.MandatoryListener listener;
    private int choosenIndex = -1;
    private String id;
    private String name;
    private ArrayList<JSONObject> values;
    private int disableOthers = -1;
    //endregion


    public RadioGroupGenerator(Context context
            , JSONObject element,boolean isRequired
    ,boolean enabled,JSONObject answer,int position) {
        super(context);
        this.context = context;
        this.element = element;
        this.isRequired = isRequired;
        this.enabled = enabled;
        this.answer = answer;
        this.position = position;
        values = new ArrayList<>();
        init(context);
    }

    public RadioGroupGenerator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RadioGroupGenerator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RadioGroupGenerator(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    private void init(Context context){

        parsJSON(element);

        //region set commentario props
        setOrgProps(context,this);
        //endregion

        //region title
        TextView titleTxt = new TextView(context);

        if (isRequired)
            titleTxt.setText(getTitleFromElement(element)+"*");
        else
            titleTxt.setText(getTitleFromElement(element));

        titleTxt.setTextSize(16);
        titleTxt.setTextColor(Color.BLACK);
        //endregion

        //region guide text
        TextView guidTxt = new TextView(context);
        guidTxt.setText(context.getString(R.string.sub_title));
        guidTxt.setTextColor(Color.RED);
        guidTxt.setTextSize(14);
        //endregion

        //region add view
        addView(titleTxt);
        addView(guidTxt);
        addRadioButtons(element,context);
        //endregion

    }


    private void getVariablesFromElement(JSONObject element) {
        try {

            name = element.has(GlobalFuncs.conf_name) ? element.getString(GlobalFuncs.conf_name) : "no name";
            isRequired = element.has(conf_isRequired) && element.getBoolean(conf_isRequired);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getValue(){
        if (choosenIndex == -1){
            isMandatoryAnswered();
            return new JSONObject();
        }
        else
            return values.get(choosenIndex);
    }

    private void addRadioButtons(JSONObject element,Context context) {
        try {
            JSONArray choices = element.getJSONArray(conf_choices);
            RadioGroup radioGroup = new RadioGroup(context);
            for (int i = 0 ; i < choices.length() ; i++){
                JSONObject object = choices.getJSONObject(i);
                RadioButton btn = new RadioButton(context);
                btn.setText(object.getString(conf_text));
                btn.setId(i);
                addAnswer(btn,answer);
                btn.setEnabled(enabled);
                radioGroup.addView(btn);
                addToValue(object,i);

            }

            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    choosenIndex = checkedId;
                    removeMandatoryError();
                    listener.onElementStatusChanged();
                }
            });

            addView(radioGroup);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addAnswer(RadioButton btn
            , JSONObject answer) {
            try {
                if (answer.getInt("index")
                        ==
                        btn.getId()){
                    choosenIndex = btn.getId();
                    btn.setChecked(true);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

    }

    private void addToValue(JSONObject object,int item) {
        JSONObject value = new JSONObject();
        try {
            value.put("name",name);
            value.put("id",id);
            value.put("status",true);
            value.put("index",item);
            value.put("value",object.getInt("value"));

            values.add(value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void isMandatoryAnswered() {
        if (choosenIndex == -1){
            setMandatoryError();
            if (listener != null) {
                listener.onMandatoryStatusError();
            }
        }
    }

    public void setMandatoryError() {
        if (setMandatories)
             this.setBackground(context.getResources()
                .getDrawable(R.drawable.is_requiered));
    }

    public void removeMandatoryError(){
        this.setBackground(null);
    }


    private void parsJSON(JSONObject element) {
        try {
            id = element.has(conf_id) ? element.getString(conf_id) : "";
            name = element.has(conf_name) ? element.getString(conf_name) : "";
            disableOthers = element.has(conf_disableOthers) ? element.getInt(conf_disableOthers) : -1;
            isRequired = element.has(conf_isRequired) ? element.getBoolean(conf_isRequired) : false;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getTitleFromElement(JSONObject element) {
        try {
            return element.getString(conf_title);
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }
    public static int dpToPx(float dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public MultiText.MandatoryListener getListener() {
        return listener;
    }

    public void setListener(MultiText.MandatoryListener listener) {
        this.listener = listener;
    }

    public String getElementId(){
        return id;
    }
}
