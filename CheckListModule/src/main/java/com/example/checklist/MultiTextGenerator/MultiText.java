package com.example.checklist.MultiTextGenerator;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.checklist.BaseViewModel.BaseView;
import com.example.checklist.BaseViewModel.ElemetActionListener;
import com.example.checklist.BaseViewModel.MandatoryListener;
import com.example.checklist.Config;
import com.example.checklist.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static com.example.checklist.GlobalFuncs.dpToPx;
import static com.example.checklist.GlobalFuncs.log;
import static com.example.checklist.PageGenerator.CheckListPager.setMandatories;

public class MultiText extends BaseView implements TextWatcher {


    //region json keys
    private String conf_placeHolder = "placeHolder";
    private String conf_title = "title";
    private String conf_id = "id";
    private String conf_name = "name";
    private String conf_items = "items";
    private String conf_isRequired = "isRequired";
    private String conf_inputType = "inputType";
    private String conf_maxLength = "maxLength";
    private String conf_preview = "Preview";
    private boolean isVertical = false;
    //endregion

    //region used variable
    private MandatoryListener listener;
    private ArrayList<EditText> editTexts;
    private ArrayList<String> names;
    private String title;
    private JSONArray items;
    private int TYPE;
    private int maxLength;
    //endregion

    //region variable
    private JSONObject element;
    private boolean enabled;
    private int position;
    private JSONArray answers;
    private Context context;
    //endregion

    //region costructors
    public MultiText(Context context, JSONObject element, boolean enabled
            , JSONArray answers, int position, MandatoryListener listener, ElemetActionListener callBack) {
        super(context,callBack);
        this.context = context;
        this.answers = answers;
        this.listener = listener;
        setOrientation(VERTICAL);
        this.element = element;
        this.enabled = enabled;
        this.position = position;
        editTexts = new ArrayList<>();
        names = new ArrayList<>();
        init(context);
    }


    //endregion

    private void init(Context context) {
        try {
            visibleSi = element.getString("visibleIf");
            isVisibleSi = true;
            name = element.getString(conf_name);
            isVertical = element.getString(conf_preview).equals("Vertical");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //region org props
        LayoutParams orgParams = new LayoutParams(LayoutParams.MATCH_PARENT
                , LayoutParams.WRAP_CONTENT);
        orgParams.setMargins(dpToPx(8, context), dpToPx(8, context)
                , dpToPx(8, context), dpToPx(8, context));
        setOrientation(LinearLayout.VERTICAL);
        setLayoutParams(orgParams);
        //endregion

        getVariablesFromElement(element);


        //region title props
        LayoutParams titleParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                , LayoutParams.WRAP_CONTENT);
        titleParams.setMargins(dpToPx(8, context), dpToPx(8, context)
                , dpToPx(8, context), dpToPx(0, context));


        TextView titleTxt = new TextView(context);
        titleTxt.setTextSize(16);
        titleTxt.setTextColor(Color.BLACK);
        titleTxt.setLayoutParams(titleParams);
        if (isMandatory)
            titleTxt.setText(getTitleFromElement(element) + "*");
        else
            titleTxt.setText(getTitleFromElement(element));

        //endregion


        //region horizontal view

        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(context);
        LayoutParams horizontalParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//        horizontalParams.setMargins(dpToPx(8, context), dpToPx(8, context)
//                , dpToPx(8, context), dpToPx(8, context));

        horizontalScrollView.setLayoutParams(horizontalParams);

        //endregion

        //region holder
        LayoutParams holderParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        holderParams.setMargins(dpToPx(8, context), dpToPx(0, context)
                , dpToPx(8, context), dpToPx(8, context));
        setOrientation(isVertical ? VERTICAL : HORIZONTAL);
        LinearLayout holder = new LinearLayout(context);
        holder.setLayoutParams(holderParams);
        //endregion

        addMultiViews(holder, context, false);
//
        setOrientation(VERTICAL);
        //region add views
        addView(titleTxt);

        //edd holder to H
        horizontalScrollView.addView(holder);

        addView(horizontalScrollView);

        //endregion

    }

    public void setMandatoryError() {
        if (enabled && setMandatories)
            this.setBackground(context.getResources().getDrawable(R.drawable.is_requiered));
    }

    public void removeMandatoryError() {
        setBackground(null);
    }

    private void handleEmpryMandatory() {
        if (isMandatory) {
            for (int i = 0; i < editTexts.size(); i++) {

                if (editTexts.get(i).getText().toString().equals("")) {
                    setMandatoryError();
                    return;
                }

            }
        }
        removeMandatoryError();
    }

    private void addMultiViews(LinearLayout holder, Context context, boolean isFromFinish) {

        for (int i = 0; i < items.length(); i++) {


            View comment = LayoutInflater.from(context).inflate(R.layout.layout_item_multi_edt_library, this, false);
            try {
                JSONObject item = items.getJSONObject(i);
                JSONObject answer = null;
                if (answers.length() > i) {
                    answer = answers.getJSONObject(i);
                }

                TextView title = comment.findViewById(R.id.title);
                title.setText(getTitleFromElement(item));

                EditText edt = comment.findViewById(R.id.comment);
                editTexts.add(edt);
                String hint = item.has(conf_placeHolder) ? item.getString(conf_placeHolder) : "Guardar";
                edt.setHint(hint);
                if (answer != null) {

                    edt.setText(answer.getString("value"));

                }
                edt.addTextChangedListener(this);
                edt.setEnabled(enabled);

                edt.setInputType(getTypeFromString(item));

                TextView maxLengthTxt = comment.findViewById(R.id.max_length);
                InputFilter[] filterArray = new InputFilter[1];
                filterArray[0] = new InputFilter.LengthFilter(maxLength);
                edt.setFilters(filterArray);
                maxLengthTxt.setText("Max : " + maxLength);

                names.add(item.getString("name"));

                holder.addView(comment);

//                handleEmpryMandatory();

            } catch (JSONException e) {
                e.printStackTrace();
                log(e.getMessage());
            }


        }

    }

    private void getVariablesFromElement(JSONObject element) {
        try {
            viewID = element.has(conf_id) ? element.getString(conf_id) : "";
            title = element.has(conf_title) ? element.getString(conf_title) : "no title";
            name = element.has(conf_name) ? element.getString(conf_name) : "no name";
            maxLength = element.has(conf_maxLength) ? element.getInt(conf_maxLength) : 200;
            isMandatory = element.has(conf_isRequired) && element.getBoolean(conf_isRequired);
            items = element.has(conf_items) ? element.getJSONArray(conf_items) : new JSONArray();
        } catch (JSONException e) {
            e.printStackTrace();
            log(e.getMessage());
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

    public JSONArray getValue() {


        JSONArray array = new JSONArray();

        if (isMandatoryAnswered2()) {

            for (int i = 0; i < editTexts.size(); i++) {
                if (editTexts.get(i) != null) {

                    JSONObject object = new JSONObject();
                    try {
                        object.put("id", viewID);
                        object.put("name", names.get(i));
                        object.put("value", editTexts.get(i).getText().toString());

                        array.put(object);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        log(e.getMessage());
                    }

                }
            }
        }

        callBack.onAction("MultiText",getElementId(),array.toString(),position);

        return array;
    }

    private boolean isMandatoryAnswered2() {
//        if (isMandatory) {
//            for (int i = 0; i < editTexts.size(); i++) {
//                if (editTexts.get(i).getText().toString().equals("")) {
//                    if (listener != null)
//                        listener.onMandatoryStatusError();
//                    return false;
//                }
//            }
//        }
        return true;
    }

    private String getTitleFromElement(JSONObject element) {

        try {
            return element.getString(conf_title);
        } catch (JSONException e) {
            e.printStackTrace();
            log(e.getMessage());
            return "no title";
        }
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (listener != null)
            listener.onElementStatusChanged(false);

        checkMandatory();
//        handleEmpryMandatory();
    }

    private void checkMandatory() {
        if (editTexts.size() > 0) {
            boolean FLAG_NOT_ANSWERED = false;
            for (int i = 0; i < editTexts.size(); i++) {

                if (editTexts.get(i).getText().toString().trim().length() == 0) {
                    isViewAnswered = false;
                    FLAG_NOT_ANSWERED = true;
                }

            }
            if (!FLAG_NOT_ANSWERED)
                isViewAnswered = true;
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public MandatoryListener getListener() {
        return listener;
    }

    public void setListener(MandatoryListener listener) {
        this.listener = listener;
    }


}
