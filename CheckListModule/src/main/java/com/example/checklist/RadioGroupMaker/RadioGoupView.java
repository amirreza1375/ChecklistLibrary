package com.example.checklist.RadioGroupMaker;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.checklist.BaseViewModel.BaseViewModel;
import com.example.checklist.BaseViewModel.ElemetActionListener;
import com.example.checklist.BaseViewModel.IConditionChangeListener;
import com.example.checklist.BaseViewModel.ViewTypeKey;
import com.example.checklist.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.checklist.GlobalFuncs.conf_choices;
import static com.example.checklist.GlobalFuncs.conf_index;
import static com.example.checklist.GlobalFuncs.conf_text;
import static com.example.checklist.GlobalFuncs.conf_value;
import static com.example.checklist.GlobalFuncs.log;

public class RadioGoupView extends BaseViewModel {

    private static final String TAG = "RadioGoupView";
    private HashMap<Integer, RadioButton> btns;
    private RadioGroup radioGroup;
    private JSONArray choices;
    private HashMap<Integer, Integer> valueByIndex;

    public RadioGoupView(Context context, JSONObject element, ElemetActionListener callBack, JSONObject viewAnswer
            , boolean isEnabled, IConditionChangeListener conditionChangeListener, int elementPosition, int viewPosition) {
        super(context, element, callBack, viewAnswer, isEnabled, conditionChangeListener, elementPosition, viewPosition);
        isConditionary = true;
        Log.i(TAG, "RadioGoupView: "+elementName+" -> "+choosenIndex);
    }

    public RadioGoupView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RadioGoupView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public JSONObject getValue() {
        if (choosenIndex >= 0) {
            JSONObject answer = getGeneralValues();

            try {
                answer.put(conf_index, choosenIndex+"");
                answer.put(conf_value, valueByIndex.get(choosenIndex)+"");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (isConditionary)
                conditionChangeListener.onRadioGroupConditionChanged(answer, pagePosition);

            return answer;
        }
        return null;

    }


    @Override
    public LinearLayout InitilizeView(Context context) {
        valueByIndex = new HashMap<>();
        btns = new HashMap<>();
        titleText = baseView.findViewById(R.id.titleText);
        radioGroup = baseView.findViewById(R.id.group);

        addRadioButtons(context);

        return this;

    }

    @Override
    public void clearData() {
        radioGroup.removeAllViews();
        btns.clear();
        valueByIndex.clear();
        choosenIndex = -1;
        addRadioButtons(context);
//        radioGroup.clearCheck();
//        for (RadioButton btn : btns) {
//            if (btn.isChecked()) {
//                btn.setChecked(false);
//                Log.i(TAG, "clearData: " + btn.getId());
//            }
//        }

    }

    @Override
    public void getElementProps() {
        try {
            choices = element.getJSONArray(conf_choices);
        } catch (JSONException e) {
            e.printStackTrace();
            log(e.getMessage());
        }
    }

    @Override
    public int getView() {
        return R.layout.layout_radio_group_view;
    }

    @Override
    public void getAnswer(JSONObject answer) {
        try {
            int value = answer.getInt(conf_index);
            if (value != -1)
                choosenIndex = value;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void addRadioButtons(Context context) {
        try {
            for (int i = 0; i < choices.length(); i++) {
                JSONObject object = choices.getJSONObject(i);
                RadioButton btn = new RadioButton(context);
                String text = object.getString(conf_text);
                btn.setText(text);
                btn.setId(i);
                btn.setEnabled(elementEnabled);
                radioGroup.addView(btn);
                btns.put(btn.getId(), btn);
                addToValue(object, i);
                addAnswer(btn, viewAnswer);

            }

            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (checkedId != -1) {
                        callBack.onAction("RadioGroup", getElementId(), "id = " + checkedId + " Text = " + btns.get(checkedId).getText().toString(), pagePosition);
                        choosenIndex = checkedId;
                        removeMandatoryError();
                        btns.get(checkedId).setChecked(true);
                        Log.i(TAG, "onCheckedChanged: " + checkedId);
                        checkMandatory();
                        viewAnswered();
                        callBack.onConditionaryDataChanged(elementName, valueByIndex.get(checkedId) + "", true, ViewTypeKey.RADIO_GROUP);
                    }

                }
            });
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
                int value = Integer.valueOf(answer.getString(conf_index));
                if (value >= 0)
                    choosenIndex = value;
                btn.setChecked(true);
                isViewAnswered = true;
                conditionChangeListener.onRadioGroupConditionChanged(getGeneralValues().put(conf_value, valueByIndex.get(choosenIndex)), pagePosition);
                Log.i(TAG, "addAnswer: " + choosenIndex);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            log(e.getMessage());
        }

    }

    private void addToValue(JSONObject object, int index) {
        try {
            valueByIndex.put(index, Integer.valueOf(object.getString(conf_value)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
