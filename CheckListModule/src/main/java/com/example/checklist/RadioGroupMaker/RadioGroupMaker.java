package com.example.checklist.RadioGroupMaker;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.checklist.BaseViewModel.BaseView;
import com.example.checklist.BaseViewModel.BaseViewModel;
import com.example.checklist.BaseViewModel.ElemetActionListener;
import com.example.checklist.BaseViewModel.MandatoryListener;
import com.example.checklist.GlobalFuncs;
import com.example.checklist.MultiTextGenerator.MultiText;
import com.example.checklist.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.checklist.GlobalFuncs.conf_choices;
import static com.example.checklist.GlobalFuncs.conf_disableOthers;
import static com.example.checklist.GlobalFuncs.conf_id;
import static com.example.checklist.GlobalFuncs.conf_isRequired;
import static com.example.checklist.GlobalFuncs.conf_name;
import static com.example.checklist.GlobalFuncs.conf_text;
import static com.example.checklist.GlobalFuncs.conf_title;
import static com.example.checklist.GlobalFuncs.conf_value;
import static com.example.checklist.GlobalFuncs.log;
import static com.example.checklist.GlobalFuncs.setOrgProps;
import static com.example.checklist.PageGenerator.CheckListPager.setMandatories;

public class RadioGroupMaker extends BaseView {

    private static final String TAG = "RadioGroupMaker";
    //region variables
    private Context context;
    private JSONObject element;
    private boolean enabled;
    private JSONObject answer;
    private int position;
    //endregion

    //region used variables
    private ArrayList<RadioButton> btns;
    private MandatoryListener listener;
    private int choosenIndex = -1;
    private ArrayList<JSONObject> values;
    private int disableOthers = -1;
    private HashMap<Integer,String> strIdByIntId;
    //endregion


    public RadioGroupMaker(Context context
            , JSONObject element, boolean isRequired
            , boolean enabled, JSONObject answer, int position, ElemetActionListener callBack) {
        super(context,callBack);
        log("after super");
        this.context = context;
        this.element = element;
        this.enabled = enabled;
        this.answer = answer;
        this.position = position;
        values = new ArrayList<>();
        btns = new ArrayList<>();
        strIdByIntId = new HashMap<>();
        init(context);
    }

    public RadioGroupMaker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RadioGroupMaker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(Context context) {
        try {
            visibleSi = element.getString("visibleIf");
            isVisibleSi = true;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        parsJSON(element);

        //region set commentario props
        setOrgProps(context, this);
        //endregion

        //region title
        TextView titleTxt = new TextView(context);

        if (isMandatory)
            titleTxt.setText(getTitleFromElement(element) + "*");
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
        addRadioButtons(element, context);
        //endregion

    }


    private void getVariablesFromElement(JSONObject element) {
        try {
            name = element.has(GlobalFuncs.conf_name) ? element.getString(GlobalFuncs.conf_name) : "no name";
            isMandatory = element.has(conf_isRequired) && element.getBoolean(conf_isRequired);
        } catch (JSONException e) {
            e.printStackTrace();
            log(e.getMessage());
        }
    }

    public JSONObject getValue(boolean isNextClicked) {
        if (choosenIndex == -1) {
//            isMandatoryAnswered(isNextClicked);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(conf_name, name);
                jsonObject.put(conf_id, viewID);
                jsonObject.put(conf_value, "");
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            Log.i(TAG, "getValue: " + getTitleFromElement(element) + " -> " + jsonObject);
            return jsonObject;
        } else {
//            Log.i(TAG, "getValue: " + getTitleFromElement(element) + " -> " + values.get(choosenIndex));
            return values.get(choosenIndex);
        }
    }

    private void addRadioButtons(JSONObject element, Context context) {
        try {
            JSONArray choices = element.getJSONArray(conf_choices);
            RadioGroup radioGroup = new RadioGroup(context);
            for (int i = 0; i < choices.length(); i++) {
                JSONObject object = choices.getJSONObject(i);
                RadioButton btn = new RadioButton(context);
                String text = object.getString(conf_text);
                btn.setText(text);
                btn.setId(i);
                strIdByIntId.put(i,object.getString(conf_id));
                addAnswer(btn, answer);
                btn.setEnabled(enabled);
                radioGroup.addView(btn);
                btns.add(btn);
                addToValue(object, i);
                Log.i(TAG, "addRadioButtons: ");

            }

            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    callBack.onAction("RadioGroup",getElementId(),"id = "+checkedId+" Text = "+btns.get(checkedId).getText().toString(),position);
                    choosenIndex = checkedId;
                    removeMandatoryError();
                    btns.get(checkedId).setChecked(true);
                    listener.onElementStatusChanged(true);
                    Log.i(TAG, "onCheckedChanged: " + checkedId);
                    checkMandatory();
                }
            });

            addView(radioGroup);
        } catch (JSONException e) {
            e.printStackTrace();
            log(e.getMessage());
        }
    }

    private void checkMandatory() {
        for (int i = 0; i < btns.size(); i++) {
            if (btns.get(i) != null) {
                if (btns.get(i).isChecked()) {
                    isViewAnswered = true;
                    return;
                }
            }
        }
        isViewAnswered = false;
    }

    private void addAnswer(RadioButton btn
            , JSONObject answer) {
        try {
            if (answer.getInt("index")
                    ==
                    btn.getId()) {
                choosenIndex = btn.getId();
                btn.setChecked(true);
                isViewAnswered = true;
                Log.i(TAG, "addAnswer: " + choosenIndex);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            log(e.getMessage());
        }

    }

    private void addToValue(JSONObject object, int item) {
        JSONObject value = new JSONObject();
        try {
            value.put("name", name);
            value.put("id", viewID);
            value.put("status", true);
            value.put("index", item);
            value.put("value", object.getInt("value"));

            values.add(value);
        } catch (JSONException e) {
            e.printStackTrace();
            log(e.getMessage());
        }
    }

    private void isMandatoryAnswered(boolean isNextClicked) {
        if (isMandatory) {
            if (choosenIndex == -1) {
                if (isNextClicked)
                    setMandatoryError();
                if (listener != null) {
                    listener.onMandatoryStatusError();
                }
            }
        }
    }

    public void setMandatoryError() {
        this.setBackground(context.getResources()
                .getDrawable(R.drawable.is_requiered));
    }

    public void removeMandatoryError() {
        this.setBackground(null);
    }


    private void parsJSON(JSONObject element) {
        try {
            viewID = element.has(conf_id) ? element.getString(conf_id) : "";
            name = element.has(conf_name) ? element.getString(conf_name) : "";
            disableOthers = element.has(conf_disableOthers) ? element.getInt(conf_disableOthers) : -1;
            isMandatory = element.has(conf_isRequired) ? element.getBoolean(conf_isRequired) : false;
        } catch (JSONException e) {
            e.printStackTrace();
            log(e.getMessage());
        }
    }

    private String getTitleFromElement(JSONObject element) {
        try {
            return element.getString(conf_title);
        } catch (JSONException e) {
            e.printStackTrace();
            log(e.getMessage());
            return "";
        }
    }

    public void clearData() {
        for (RadioButton btn : btns) {
            if (btn.isChecked()) {
                btn.setChecked(false);
                Log.i(TAG, "clearData: " + btn.getId());
            }
        }
        choosenIndex = -1;
    }


    public MandatoryListener getListener() {
        return listener;
    }

    public void setListener(MandatoryListener listener) {
        this.listener = listener;
    }


}
