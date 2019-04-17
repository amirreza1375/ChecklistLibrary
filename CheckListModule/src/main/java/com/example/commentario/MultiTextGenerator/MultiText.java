package com.example.commentario.MultiTextGenerator;

import android.content.Context;
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

import com.example.commentario.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.commentario.GlobalFuncs.dpToPx;
import static com.example.commentario.PageGenerator.CheckListPager.setMandatories;

public class MultiText extends LinearLayout implements TextWatcher {


    //region json keys
    private String conf_title = "title";
    private String conf_id = "id";
    private String conf_name = "name";
    private String conf_items = "items";
    private String conf_isRequired = "isRequired";
    private String conf_inputType = "inputType";
    private String conf_maxLength = "maxLength";
    //endregion

    //region used variable
    private MandatoryListener listener;
    private ArrayList<EditText> editTexts;
    private ArrayList<String> names;
    private String title;
    private String id;
    private String name;
    private JSONArray items;
    private boolean isRequired;
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
            , JSONArray answers, int position,MandatoryListener listener) {
        super(context);
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

    public MultiText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MultiText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MultiText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    //endregion

    private void init(Context context) {

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
        if (isRequired)
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
        holderParams.setMargins(dpToPx(8, context), dpToPx(8, context)
                , dpToPx(8, context), dpToPx(8, context));
        setOrientation(HORIZONTAL);
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
        if (isRequired) {
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
                title.setText(item.getString(conf_name));

                EditText edt = comment.findViewById(R.id.comment);
                edt.setHint("Guardar");
                if (answer != null) {

                    edt.setText(answer.getString("value"));

                }
                edt.addTextChangedListener(this);
                edt.setEnabled(enabled);

                edt.setInputType(getTypeFromString(item));

                TextView maxLengthTxt = comment.findViewById(R.id.max_length);
                int itemMaxLength = item.has(conf_maxLength) ? item.getInt(conf_maxLength) : 20;
                InputFilter[] filterArray = new InputFilter[1];
                filterArray[0] = new InputFilter.LengthFilter(itemMaxLength);
                edt.setFilters(filterArray);
                maxLengthTxt.setText("Max : " + itemMaxLength);

                editTexts.add(edt);
                names.add(item.getString("name"));

                holder.addView(comment);

                handleEmpryMandatory();

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }

    private void getVariablesFromElement(JSONObject element) {
        try {
            id = element.has(conf_id) ? element.getString(conf_id) : "";
            title = element.has(conf_title) ? element.getString(conf_title) : "no title";
            name = element.has(conf_name) ? element.getString(conf_name) : "no name";
            maxLength = element.has(conf_maxLength) ? element.getInt(conf_maxLength) : 200;
            isRequired = element.has(conf_isRequired) && element.getBoolean(conf_isRequired);
            items = element.has(conf_items) ? element.getJSONArray(conf_items) : new JSONArray();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private int getTypeFromString(JSONObject item) {
        String type = "";
        try {
            type = item.getString(conf_inputType);
        } catch (JSONException e) {
            e.printStackTrace();
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

        if (isMandatoryAnswered()) {

            for (int i = 0; i < editTexts.size(); i++) {
                if (editTexts.get(i) != null) {

                    JSONObject object = new JSONObject();
                    try {
                        object.put("id", id);
                        object.put("name", names.get(i));
                        object.put("value", editTexts.get(i).getText().toString());

                        array.put(object);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }

        return array;
    }

    private boolean isMandatoryAnswered() {
        for (int i = 0; i < editTexts.size(); i++) {
            if (editTexts.get(i).getText().toString().equals("")) {
                if (listener != null)
                    listener.onMandatoryStatusError();
                return false;
            }
        }
        return true;
    }

    private String getTitleFromElement(JSONObject element) {

        try {
            return element.getString(conf_title);
        } catch (JSONException e) {
            e.printStackTrace();
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
            listener.onElementStatusChanged();
        handleEmpryMandatory();
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

    public interface MandatoryListener {
        void onMandatoryStatusError();

        void onElementStatusChanged();
    }

    public String getElementId() {
        return id;
    }
}
