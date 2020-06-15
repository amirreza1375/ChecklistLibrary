package com.example.checklist.MultiTextGenerator;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.checklist.BaseViewModel.BaseViewModel;
import com.example.checklist.BaseViewModel.ElemetActionListener;
import com.example.checklist.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.checklist.GlobalFuncs.conf_inputType;
import static com.example.checklist.GlobalFuncs.conf_items;
import static com.example.checklist.GlobalFuncs.conf_name;
import static com.example.checklist.GlobalFuncs.conf_value;
import static com.example.checklist.GlobalFuncs.getTitleFromElement;
import static com.example.checklist.GlobalFuncs.log;

public class MultiTextView extends BaseViewModel implements TextWatcher {

    private String conf_placeHolder = "placeHolder";

    private boolean isVertical;
    private ArrayList<EditText> editTexts;
    private ArrayList<String> names;
    private JSONArray items;
    private JSONArray answers;

    public MultiTextView(Context context, JSONObject element, ElemetActionListener callBack, JSONObject viewAnswer, boolean isEnabled,int elementPosition,int viewPosition) {
        super(context, element, callBack, viewAnswer, isEnabled,elementPosition,viewPosition);
    }

    public MultiTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MultiTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public JSONObject getValue() {
        JSONObject answer = getGeneralValues();

        JSONArray values = new JSONArray();

        for (int i = 0; i < editTexts.size(); i++) {

            JSONObject value = new JSONObject();
            try {
                value.put(conf_name, names.get(i));
                value.put(conf_value, editTexts.get(i).getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            values.put(value);

        }

        try {
            answer.put(conf_value, values);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        setAnsweredStatus();

        return answer;
    }

    private boolean setAnsweredStatus() {
        boolean isAnswered_temp = true;
        for (EditText edt : editTexts) {
            if (edt != null) {
                if (edt.getText().toString().equals("")) {
                    isAnswered_temp = false;
                    break;
                }
            }
        }
        if (isAnswered_temp)
            viewAnswered();
        else
            viewAnswerRemoved();
        return isAnswered_temp;
    }

    @Override
    public LinearLayout InitilizeView(Context context) {
        LinearLayout holder = baseView.findViewById(R.id.holder);

        if (isVertical)
            holder.setOrientation(VERTICAL);

        addMultiViews(holder);

        return this;
    }

    @Override
    public void clearData() {

    }

    @Override
    public void getElementProps() {
        try {
            isVertical = element.has("Vertical") && element.getBoolean("Vertical");
            items = element.has(conf_items) ? element.getJSONArray(conf_items) : new JSONArray();
            //TODO get answers from Object
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getView() {
        return R.layout.layout_multitext_view;
    }

    @Override
    public void getAnswer(JSONObject answer) {
        try {
            answers = answer.has(conf_value) ? answer.getJSONArray(conf_value) : new JSONArray();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void addMultiViews(LinearLayout holder) {
        names = new ArrayList<>();
        editTexts = new ArrayList<>();
        for (int i = 0; i < items.length(); i++) {


            View comment = LayoutInflater.from(context).inflate(R.layout.layout_item_multi_edt_library, this, false);
            try {
                JSONObject item = items.getJSONObject(i);
                JSONObject answer = null;
                if (answers.length() > i) {
                    answer = answers.getJSONObject(i);
                }

                TextView title = comment.findViewById(R.id.title);
                title.setText(getTitleFromElement(item, false));

                EditText edt = comment.findViewById(R.id.comment);
                editTexts.add(edt);

                String hint = item.has(conf_placeHolder) ? item.getString(conf_placeHolder) : "Guardar";
                edt.setHint(hint);
                if (answer != null) {

                    edt.setText(answer.getString("value"));

                }
                edt.addTextChangedListener(this);
                edt.setEnabled(elementEnabled);

                edt.setInputType(getTypeFromString(item));

                TextView maxLengthTxt = comment.findViewById(R.id.max_length);
                InputFilter[] filterArray = new InputFilter[1];
                filterArray[0] = new InputFilter.LengthFilter(elementMaxLength);
                edt.setFilters(filterArray);
                maxLengthTxt.setText("Max : " + elementMaxLength);

                names.add(item.getString("name"));

                holder.addView(comment);

//                handleEmpryMandatory();

            } catch (JSONException e) {
                e.printStackTrace();
                log(e.getMessage());
            }


        }

    }

    private int getTypeFromString(JSONObject item) {
        String type = "";
        try {
            type = item.getString(conf_inputType);
        } catch (JSONException e) {
            e.printStackTrace();
            log(e.getMessage());
        }
        switch (type) {
            case "number":
                return InputType.TYPE_CLASS_NUMBER;
            case "password":
                return InputType.TYPE_TEXT_VARIATION_PASSWORD;
            default:
                return InputType.TYPE_CLASS_TEXT;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (setAnsweredStatus())
            removeMandatoryError();
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
