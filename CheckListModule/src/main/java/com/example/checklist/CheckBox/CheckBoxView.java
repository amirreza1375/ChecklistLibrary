package com.example.checklist.CheckBox;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

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

import static com.example.checklist.GlobalFuncs.conf_answer;
import static com.example.checklist.GlobalFuncs.conf_id;
import static com.example.checklist.GlobalFuncs.conf_text;
import static com.example.checklist.GlobalFuncs.conf_value;
import static com.example.checklist.GlobalFuncs.log;

public class CheckBoxView extends BaseViewModel {

    private String conf_choices = "choices";

    private int min=-1,max=-1;
    private int disableOthers = -1;

    private ArrayList<String> answers;
    private JSONArray choices;
    private ArrayList<CheckBox> checkBoxes;
    private HashMap<Integer, Boolean> checkBoxStatuses;

    private LinearLayout footer,holder;
    private TextView minTxt,maxTxt;
    private HashMap<Integer, String> valueByIndex;

    public CheckBoxView(Context context, JSONObject element, ElemetActionListener callBack, JSONObject viewAnswer, boolean isEnabled
            , IConditionChangeListener conditionChangeListener,int elementPosition,int viewPosition) {
        super(context, element, callBack, viewAnswer, isEnabled,conditionChangeListener,elementPosition,viewPosition);
    }

    public CheckBoxView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckBoxView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        isConditionary = true;
    }

    @Override
    public JSONObject getValue() {
        JSONObject answer = getGeneralValues();
        try {
            JSONArray temp = convert_HashMap_to_JSONArray(checkBoxStatuses);
            answer.put(conf_value,temp);
            if (conditionChangeListener != null)
                conditionChangeListener.onCheckBoxConditionChanged(temp, pagePosition);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setAnsweredStatus();
        return answer;
    }

    private void setAnsweredStatus() {//TODO min max
        boolean isAnswered = false;
        for (CheckBox checkBox : checkBoxes){
            if (checkBox.isChecked()){
                viewAnswered();
                isAnswered = true;
                break;
            }
        }
        if (!isAnswered){
            viewAnswerRemoved();
        }
    }

    @Override
    public LinearLayout InitilizeView(Context context) {

        valueByIndex = new HashMap<>();
        titleText = baseView.findViewById(R.id.titleText);
        footer = baseView.findViewById(R.id.footer);
        holder = baseView.findViewById(R.id.holder);
        minTxt = baseView.findViewById(R.id.minTxt);
        maxTxt = baseView.findViewById(R.id.maxTxt);

        if (!elementIsMinMaxExist){
            footer.setVisibility(GONE);
        }

        generateCheckBoxes(holder,context);

        return this;

    }

    @Override
    public void clearData() {
        for (CheckBox checkBox : checkBoxes){
            if (checkBox.isChecked()){
                checkBox.setChecked(false);
            }
        }
    }

    @Override
    public void getElementProps() {
        try {
            choices = element.has("choices") ? element.getJSONArray("choices") : new JSONArray();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getView() {
        return R.layout.layout_check_box_view;
    }

    @Override
    public void getAnswer(JSONObject answer) {
        answers = new ArrayList<>();
        try {
            JSONArray answersArr = answer.getJSONArray(conf_value);
            for (int i = 0 ; i < answersArr.length() ; i++){
                JSONObject answerObj = answersArr.getJSONObject(i);
                if (answerObj.getBoolean("status")){
                    answers.add(answerObj.getString("value"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void viewAnswered() {
        isViewAnswered = true;
    }

    @Override
    public void viewAnswerRemoved() {
        isViewAnswered = false;
    }

    private void generateCheckBoxes(LinearLayout checkHolder, Context context) {
        checkBoxes = new ArrayList<>();
        checkBoxStatuses = new HashMap<>();

        for (int i = 0; i < choices.length(); i++) {

            try {

                JSONObject checkBoxObj = choices.getJSONObject(i);

                final CheckBox checkBox = new CheckBox(context);
                checkBox.setEnabled(elementEnabled);
                checkBox.setText(checkBoxObj.getString(conf_text));
                checkBox.setId(i);
                valueByIndex.put(i,checkBoxObj.getString(conf_id));
                checkBoxStatuses.put(checkBox.getId(), false);
                setAnswer(answers, checkBox, checkBoxObj.getString(conf_value));
                checkBoxes.add(checkBox);
                setMinMaxId(checkBox.getId());

                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        checkBoxStatuses.put(checkBox.getId(), isChecked);
                        if (checkBox.getId() == elementDisableOthers) {
                            disableOthersById(valueByIndex.get(checkBox.getId()), isChecked);
                        }
                        checkMandatory();
                        removeMandatoryError();
                        callBack.onConditionaryDataChanged(elementName,checkBox.getId()+"",isChecked, ViewTypeKey.CHECK_BOX);
                    }
                });

                checkHolder.addView(checkBox);

            } catch (JSONException e) {
                e.printStackTrace();
                log(e.getMessage());
            }

        }

    }

    private void setAnswer(ArrayList<String> answers, CheckBox checkBox, String value) {

        for (int i = 0; i < answers.size(); i++) {
            if (value.equals(answers.get(i))) {
                checkBox.setChecked(true);
                isViewAnswered = true;
                checkBoxStatuses.put(checkBox.getId(), true);
                if (disableOthers != -1)
                    if (disableOthers == checkBox.getId())
                        disableOthersById(valueByIndex.get(checkBox.getId()), true);
                break;
            }
        }
    }

    private void checkMandatory() {
        boolean FLAG_ANSWERED = false;
        for (int i = elementMin ; i <= elementMax ; i++){
            if (checkBoxStatuses.get(i) != null){
                if (checkBoxStatuses.get(i)){
                    FLAG_ANSWERED = true;
                    break;
                }
            }
        }
        isViewAnswered = FLAG_ANSWERED;
    }
    private JSONArray convert_HashMap_to_JSONArray(HashMap<Integer, Boolean> hashMap) {
        JSONArray array = new JSONArray();
        for (int i = 0; i <= 10; i++) {

            if (hashMap.get(i) != null) {
                JSONObject object = new JSONObject();
                try {
                    object.put("value", valueByIndex.get(i));
                    object.put("index", i);
                    object.put("status", hashMap.get(i));

                    array.put(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                    log(e.getMessage());
                }

            }
        }

        return array;
    }


    private void setMinMaxId(int id) {
        //first check if assigned before
        if (min == -1) {//not assigned
            min = id;
        }
        if (max == -1) {//not assigned
            max = id;
        }
        //if assigned and lower that min assign to min
        if (min > id) {
            min = id;
        }
        //if assigned and higher than max assign to max
        if (max < id) {
            max = id;
        }
    }
    private void disableOthersById(String id, boolean isChecked) {
        for (int i = 0; i < checkBoxes.size(); i++) {
            String checkBoxId = valueByIndex.get(checkBoxes.get(i).getId());
            if (checkBoxId.equals(id)) {
                checkBoxes.get(i).setEnabled(!isChecked);
                checkBoxes.get(i).setChecked(false);
                checkBoxStatuses.put(checkBoxes.get(i).getId(), false);
            }
        }
    }
}
