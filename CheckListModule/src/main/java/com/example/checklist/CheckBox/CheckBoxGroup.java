package com.example.checklist.CheckBox;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.checklist.BaseViewModel.BaseView;
import com.example.checklist.BaseViewModel.ElemetActionListener;
import com.example.checklist.BaseViewModel.MandatoryListener;
import com.example.checklist.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.checklist.GlobalFuncs.conf_rangeMax;
import static com.example.checklist.GlobalFuncs.conf_rangeMin;
import static com.example.checklist.GlobalFuncs.conf_required;
import static com.example.checklist.GlobalFuncs.createTitle;
import static com.example.checklist.GlobalFuncs.dpToPx;
import static com.example.checklist.GlobalFuncs.log;
import static com.example.checklist.GlobalFuncs.setOrgProps;
import static com.example.checklist.PageGenerator.CheckListPager.setMandatories;

public class CheckBoxGroup extends BaseView {

    //region element keys
    private String conf_disableOthers = "disableOther";
    private String conf_title = "title";


    private String conf_name = "name";
    private String conf_id = "id";
    private String conf_choices = "choices";
    private String conf_value = "value";
    private String conf_text = "text";
    //endregion


    //region variables
    private JSONObject element;
    private boolean enabled;
    //endregion

    //region used variables
    private Context context;
    private MandatoryListener listener;
    private boolean isMaxMinExist = false;
    private int choosenCount = 0;
    private int min;
    private int max;
    private int disableOthers = -1;
    private String title;
    private ArrayList<String> answers;
    private int position;
    private JSONArray choices;
    private ArrayList<CheckBox> checkBoxes;
    private HashMap<Integer, Boolean> checkBoxStatuses;
    private int maxId = -1;
    private int minId = -1;
    private TextView titleTxt;
    private TextView guidTxt;
    //endregion

    //region constructor
    public CheckBoxGroup(Context context, JSONObject element
            , boolean enabled, ArrayList<String> answers, int position
            , MandatoryListener listener, ElemetActionListener callBack) {
        super(context,callBack);

        this.context = context;
        this.element = element;
        this.enabled = enabled;
        this.answers = answers;
        this.position = position;
        this.listener = listener;
        checkBoxes = new ArrayList<>();
        checkBoxStatuses = new HashMap<>();
        init(context);
    }

    public CheckBoxGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckBoxGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    //endregion

    private void init(Context context) {

        try {
            visibleSi = element.getString("visibleIf");
            isVisibleSi = true;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        isMaxMinExist = isMaxMinExist(element);
        getSeekBarPropsFromElement(element);

        //region org props
        setOrgProps(context, this);
        //endregion

        //region guide text
        guidTxt = new TextView(context);
        guidTxt.setText(context.getString(R.string.sub_title_m));
        guidTxt.setTextColor(Color.RED);
        guidTxt.setTextSize(14);
        //endregion

        //region title props

        titleTxt = createTitle(context, isMandatory, element);
        //endregion

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
        maxTxt.setText("max : " + max);
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
        minTxt.setText("min : " + min);
        //endregion

        //endregion

        //region checkbox holder
        LinearLayout checkHolder = new LinearLayout(context);
        LayoutParams hodlerParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        hodlerParams.setMargins(dpToPx(8, context), dpToPx(8, context)
                , dpToPx(8, context), dpToPx(8, context));
        checkHolder.setOrientation(VERTICAL);
        checkHolder.setLayoutParams(hodlerParams);
        generateCheckBoxes(checkHolder, context);
        //endregion

        //region add views
        addView(titleTxt);
        addView(guidTxt);
        addView(checkHolder);
        if (isMaxMinExist) {
            footer.addView(minTxt);
            footer.addView(maxTxt);
        }
        addView(footer);
        //endregion

    }

    private void generateCheckBoxes(LinearLayout checkHolder, Context context) {

        for (int i = 0; i < choices.length(); i++) {

            try {

                JSONObject checkBoxObj = choices.getJSONObject(i);

                final CheckBox checkBox = new CheckBox(context);
                checkBox.setEnabled(enabled);
                checkBox.setText(checkBoxObj.getString(conf_text));
                checkBox.setId(checkBoxObj.getInt(conf_value));
                checkBoxStatuses.put(checkBox.getId(), false);
                setAnswer(answers, checkBox, String.valueOf(checkBoxObj.getInt(conf_value)));
                checkBoxes.add(checkBox);
                setMinMaxId(checkBox.getId());

                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        checkBoxStatuses.put(checkBox.getId(), isChecked);
                        if (checkBox.getId() == disableOthers) {
                            disableOthersById(checkBox.getId(), isChecked);
                        }
                        checkMandatory();
                        listener.onElementStatusChanged(true);
                        removeMandatoryError();
                    }
                });

                checkHolder.addView(checkBox);

            } catch (JSONException e) {
                e.printStackTrace();
                log(e.getMessage());
            }

        }

    }

    private void checkMandatory() {
        boolean FLAG_ANSWERED = false;
        for (int i = minId ; i <= maxId ; i++){
            if (checkBoxStatuses.get(i) != null){
                if (checkBoxStatuses.get(i)){
                    FLAG_ANSWERED = true;
                    break;
                }
            }
        }
        isViewAnswered = FLAG_ANSWERED;
    }


    private void setAnswer(ArrayList<String> answers, CheckBox checkBox, String value) {

        for (int i = 0; i < answers.size(); i++) {
            if (value.equals(answers.get(i))) {
                checkBox.setChecked(true);
                isViewAnswered = true;
                checkBoxStatuses.put(checkBox.getId(), true);
                if (disableOthers != -1)
                    if (disableOthers == checkBox.getId())
                        disableOthersById(checkBox.getId(), true);
                break;
            }
        }
    }

    private void setMinMaxId(int id) {
        //first check if assigned before
        if (minId == -1) {//not assigned
            minId = id;
        }
        if (maxId == -1) {//not assigned
            maxId = id;
        }
        //if assigned and lower that min assign to min
        if (minId > id) {
            minId = id;
        }
        //if assigned and higher than max assign to max
        if (maxId < id) {
            maxId = id;
        }
    }

    private void disableOthersById(int id, boolean isChecked) {
        for (int i = 0; i < checkBoxes.size(); i++) {
            long checkBoxId = checkBoxes.get(i).getId();
            if (checkBoxId != id) {
                checkBoxes.get(i).setEnabled(!isChecked);
                checkBoxes.get(i).setChecked(false);
                checkBoxStatuses.put(checkBoxes.get(i).getId(), false);
            }
        }
    }

    private boolean isMaxMinExist(JSONObject element) {
        return element.has(conf_rangeMin);
    }

    public JSONArray getValues(boolean isNextClicked) {
        if (!isShowen)
            isMandatoriesAnswered(isNextClicked);
        return convert_HashMap_to_JSONArray(checkBoxStatuses);
    }

    private boolean isMandatoriesAnswered(boolean isNextClicked) {
        if (isMandatory) {
            boolean FLAG = false;
            for (int i = 0; i < checkBoxes.size(); i++) {
                if (checkBoxes.get(i).isChecked()) {
                    FLAG = true;
                }
            }

            if (!FLAG) {
                if (isNextClicked)
                    setMandatoryError();
                if (listener != null)
                    listener.onMandatoryStatusError();
            }

        }
        return true;
    }

    public void setMandatoryError() {
        if (setMandatories)
            this.setBackground(context.getResources().getDrawable(R.drawable.is_requiered));
    }

    public void removeMandatoryError() {
        this.setBackground(null);
    }

    private JSONArray convert_HashMap_to_JSONArray(HashMap<Integer, Boolean> hashMap) {
        JSONArray array = new JSONArray();
        for (int i = minId; i <= maxId; i++) {

            if (hashMap.get(i) != null) {
                JSONObject object = new JSONObject();
                try {
                    object.put("name", name);
                    object.put("id", viewID);
                    object.put("name", name);
                    object.put("value", i + "");
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

    private void getSeekBarPropsFromElement(JSONObject element) {
        try {
            choices = element.has(conf_choices) ? element.getJSONArray(conf_choices) : new JSONArray();
            max = element.has(conf_rangeMax) ? element.getInt(conf_rangeMax) : choices.length();
            min = element.has(conf_rangeMin) ? element.getInt(conf_rangeMin) : 0;
        } catch (JSONException e) {
            e.printStackTrace();
            log(e.getMessage());
        }
    }

    public void clearData(){
        for (CheckBox checkBox : checkBoxes){
            if (checkBox.isChecked()){
                checkBox.setChecked(false);
            }
        }
    }
    public TextView getTitle(){
        return titleTxt;
    }
    public TextView getGuidTxt(){
        return guidTxt;
    }

    public MandatoryListener getListener() {
        return listener;
    }

    public void setListener(MandatoryListener listener) {
        this.listener = listener;
    }

}

