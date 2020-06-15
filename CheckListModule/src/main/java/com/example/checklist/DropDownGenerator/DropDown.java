package com.example.checklist.DropDownGenerator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.checklist.BaseViewModel.BaseView;
import com.example.checklist.BaseViewModel.ElemetActionListener;
import com.example.checklist.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.checklist.GlobalFuncs.conf_dropDown_choices;
import static com.example.checklist.GlobalFuncs.conf_id;
import static com.example.checklist.GlobalFuncs.conf_isRequired;
import static com.example.checklist.GlobalFuncs.conf_name;
import static com.example.checklist.GlobalFuncs.conf_title;
import static com.example.checklist.GlobalFuncs.createTitle;
import static com.example.checklist.GlobalFuncs.log;
import static com.example.checklist.GlobalFuncs.setOrgProps;

public class DropDown extends BaseView {

    //region used variables
    private boolean isRequired;
    private String title;
    private String name;
    private JSONArray choices;
    private Spinner spinner;
    //endregion

    //region variables
    private Context context;
    private JSONObject element;
    private boolean enable;
    private String answer;
    //endregion

    //region constructors
    public DropDown(Context context, JSONObject element,boolean enable
    ,String answer, ElemetActionListener callBack) {
        super(context,callBack);
        this.context = context;
        this.element = element;
        this.enable = enable;
        this.answer = answer;
        init(context);
    }

    public DropDown(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DropDown(Context context, AttributeSet attrs, int defStyleAttr) {
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

        getElemtVariables(element);
        //region org props
        setOrgProps(context,this);
        //endregion

        //region title props
        TextView titleTxt = createTitle(context,isRequired,element);
        //endregion

        //region spinner
        spinner = new Spinner(context);
        setupSpinner(context,spinner);
        spinner.setEnabled(enable);
        //endregion

        //region add views
        addView(titleTxt);
        addView(spinner);
        //endregion

    }

    public void setMandatoryError(){
        if (enable)
        this.setBackground(context.getResources()
        .getDrawable(R.drawable.is_requiered));
    }
    public void removeMandatoryError(){
        this.setBackground(null);
    }

    private void setupSpinner(final Context context, Spinner spinner) {
        try {
            final ArrayList<String> items = getSpinnerData(context);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context
                    , android.R.layout.simple_list_item_1, items);
            spinner.setAdapter(adapter);
            setAnswer(spinner,items);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                   // Toast.makeText(context, items.get(position), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }catch (Exception e){
            e.printStackTrace();
            log(e.getMessage());
        }
    }

    private void setAnswer(Spinner spinner,ArrayList<String> items) {
        int answerIndex = -1;
        for (int i = 0 ; i < items.size() ; i++){

            if (items.get(i).equals(answer)) {
                answerIndex = i;
                break;
            }

        }
        spinner.setSelection(answerIndex);
    }

    private ArrayList<String> getSpinnerData(Context context) {
        ArrayList<String> items = new ArrayList<>();
        for (int i = 0 ; i < 10 ; i ++){
            items.add("Item "+i);
        }
        return items;
    }

    public String getValue(){
        return String.valueOf(spinner.getSelectedItem());
    }

    private void getElemtVariables(JSONObject element) {
        try {
            choices = element.has(conf_dropDown_choices) ? element.getJSONArray(conf_dropDown_choices) : new JSONArray();
            viewID = element.has(conf_id) ? element.getString(conf_id) : "no title";
            title = element.has(conf_title) ? element.getString(conf_title) : "no title";
            name = element.has(conf_name) ? element.getString(conf_name) : "no name";
            isRequired = element.has(conf_isRequired) && element.getBoolean(conf_isRequired);
        } catch (JSONException e) {
            e.printStackTrace();
            log(e.getMessage());

        }
    }


}
