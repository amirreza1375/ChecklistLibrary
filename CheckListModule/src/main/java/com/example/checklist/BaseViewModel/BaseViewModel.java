package com.example.checklist.BaseViewModel;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.checklist.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import static com.example.checklist.GlobalFuncs.conf_disableOthers;
import static com.example.checklist.GlobalFuncs.conf_html;
import static com.example.checklist.GlobalFuncs.conf_id;
import static com.example.checklist.GlobalFuncs.conf_isRequired;
import static com.example.checklist.GlobalFuncs.conf_maxLength;
import static com.example.checklist.GlobalFuncs.conf_name;
import static com.example.checklist.GlobalFuncs.conf_position;
import static com.example.checklist.GlobalFuncs.conf_rangeMax;
import static com.example.checklist.GlobalFuncs.conf_rangeMin;
import static com.example.checklist.GlobalFuncs.conf_required;
import static com.example.checklist.GlobalFuncs.conf_systemId;
import static com.example.checklist.GlobalFuncs.conf_tipo;
import static com.example.checklist.GlobalFuncs.conf_title;
import static com.example.checklist.GlobalFuncs.conf_type;
import static com.example.checklist.GlobalFuncs.conf_visibileSi;

public abstract class BaseViewModel extends LinearLayout {

    private static final String TAG = "BaseViewModel";

    protected boolean isConditionary = false;

    protected int choosenIndex = -1;//Is only used for RadioGroup

    protected boolean isJustView = true;

    protected View baseView;
    protected LinearLayout parentView;

    protected boolean isShown = true;
    protected boolean isViewAnswered;
    protected boolean isMandatory;
    protected JSONObject viewAnswer;

    protected ArrayList<File> imageFiles;
    protected ArrayList<String> names;
    protected ArrayList<String> priorities;

    protected int pagePosition;
    protected int viewPosition;

    protected JSONObject element;
    protected String elementType;
    protected String elementName;
    protected String elementId;
    protected boolean elementEnabled = true;
    protected boolean elementIsVisibleSi;
    protected String elementVisibleSiName;
    protected String elementVisibleSiValue;
    protected String elementVisibleSi;
    protected int elementDisableOthers = -1;
    protected String elemetTitle;
    protected int elementMin;
    protected int elementMax;
    protected boolean elementIsMinMaxExist;
    protected String elementTipo;
    protected int elementMaxLength;
    protected boolean elementIsRequired;
    protected String elementSysyemId;

    protected TextView titleText;

    protected ElemetActionListener callBack;
    protected IConditionChangeListener conditionChangeListener;

    protected Context context;

    //region constructors
    public BaseViewModel(Context context,JSONObject element,ElemetActionListener callBack,JSONObject viewAnswer,boolean isEnabled,int pagePosition,int viewPosition) {
        super(context);
        this.elementEnabled = isEnabled;
        this.context = context;
        this.element = element;
        this.viewAnswer = viewAnswer;
        this.callBack = callBack;
        this.pagePosition = pagePosition;
        baseView = getViewFromRes(getView());
        getDataFromElement();
        getAnswer(viewAnswer);
        parentView = InitilizeView(context);
        InitilizeBaseView();
    }

    public BaseViewModel(Context context,JSONObject element,ElemetActionListener callBack,JSONObject viewAnswer,boolean isEnabled,IConditionChangeListener conditionChangeListener,int pagePosition,int viewPosition) {
        super(context);
        this.conditionChangeListener = conditionChangeListener;
        this.elementEnabled = isEnabled;
        this.context = context;
        this.element = element;
        this.viewAnswer = viewAnswer;
        this.callBack = callBack;
        this.pagePosition = pagePosition;
        baseView = getViewFromRes(getView());
        getDataFromElement();
        getAnswer(viewAnswer);
        parentView = InitilizeView(context);
        InitilizeBaseView();
    }

    public BaseViewModel(Context context,JSONObject element,ElemetActionListener callBack,JSONObject viewAnswer,boolean isEnabled
            , ArrayList<File> imageFiles, ArrayList<String> priorities
            , ArrayList<String> names,int pagePosition,int viewPosition) {
        super(context);
        this.elementEnabled = isEnabled;
        this.context = context;
        this.element = element;
        this.viewAnswer = viewAnswer;
        this.callBack = callBack;
        this.imageFiles = imageFiles;
        this.names = names;
        this.priorities = priorities;
        this.pagePosition = pagePosition;
        baseView = getViewFromRes(getView());
        getDataFromElement();
        getAnswer(viewAnswer);
        parentView = InitilizeView(context);
        InitilizeBaseView();

    }



    public BaseViewModel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseViewModel(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //endregion

    //region general functions

    private View getViewFromRes(int view) {
        return LayoutInflater.from(context).inflate(view,this,false);
    }


    private void InitilizeBaseView() {
        if (baseView != null) {
            titleText = baseView.findViewById(R.id.titleText);
            if (titleText != null) {
                titleText.setText(elemetTitle);
                if (!elementSysyemId.equals("")){
                    titleText.setText(elementSysyemId + " . "+titleText.getText().toString());
                }
                if (elementIsRequired)
                    titleText.setText(titleText.getText().toString() + "*");
            }
            parentView.addView(baseView);
        }
    }

    public void setMandatoryError(){
        this.setBackground(context.getResources().getDrawable(R.drawable.is_requiered));
    }

    public void removeMandatoryError() {
        this.setBackground(null);
    }

    public boolean isMandatoryAnswered(){
        if (!isMandatory)
            return true;
        return isViewAnswered;
    }

    public void getDataFromElement() {
        try {
            elementType = element.has(conf_type) ? element.getString(conf_type) : "No Type";
            elemetTitle = element.has(conf_title) ? element.getString(conf_title) : "";
            if (elemetTitle.equals("")){
                elemetTitle = element.has(conf_html) ? element.getString(conf_html) : "No Title";
            }
            elementId = element.has(conf_id) ? element.getString(conf_id) : "No Id";
            elementName = element.has(conf_name) ? element.getString(conf_name) : "No name";
            elementIsVisibleSi = hasVisibleSi(element);
            if (elementIsVisibleSi){

                elementVisibleSi = element.getString(conf_visibileSi);
                String[] elementVisibleSiArray = elementVisibleSi.split("contains");
                if (elementVisibleSiArray.length == 2){
                    elementVisibleSiName = elementVisibleSiArray[0].substring(1,elementVisibleSiArray[0].length()-2);
                    elementVisibleSiValue = elementVisibleSiArray[1];
                    callBack.isHiddenView();
                }
            }
            elementDisableOthers = element.has(conf_disableOthers) ? element.getInt(conf_disableOthers) : -1;
            isMandatory = element.has(conf_required) && element.getBoolean(conf_required);
            elementMax = element.has(conf_rangeMax) ? element.getInt(conf_rangeMax) : 100;
            elementMin = element.has(conf_rangeMin) ? element.getInt(conf_rangeMin) : 0;
            elementIsMinMaxExist = isMaxMinExist(element);
            elementTipo = element.has(conf_tipo) ? element.getString(conf_tipo) : "";
            elementMaxLength = element.has(conf_maxLength) ? element.getInt(conf_maxLength) : 999;
            elementIsRequired = element.has(conf_isRequired) && element.getBoolean(conf_isRequired);
            elementSysyemId = element.has(conf_systemId) ? element.getString(conf_systemId) : "";

        } catch (JSONException e) {
            e.printStackTrace();
        }
        getElementProps();
    }

    public boolean hasVisibleSi(JSONObject element) {
        if (!element.has(conf_visibileSi))
            return false;

        try {
            String visibleSi = element.getString(conf_visibileSi);
            if (visibleSi.equals(""))
                return false;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean isMaxMinExist(JSONObject element) {
        return element.has(conf_rangeMin);
    }

    public JSONObject getGeneralValues(){
        JSONObject answer = new JSONObject();
        try {
            answer.put(conf_type,elementType);
            answer.put(conf_id,elementId);
            answer.put(conf_name,elementName);
            answer.put(conf_tipo,elementTipo);
            answer.put(conf_position, pagePosition);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return answer;
    }

    public void viewAnswered(){
        isViewAnswered = true;
    }

    public void viewAnswerRemoved(){
        isViewAnswered = false;
    }

    //endregion

    //region abstract functions

    public abstract JSONObject getValue();
    public abstract LinearLayout InitilizeView(Context context);//TODO pass view here instead of declearing globally
    public abstract void clearData();
    public abstract void getElementProps();
    public abstract int getView();
    public abstract void getAnswer(JSONObject answer);

    //endregion

    //region setter getter

    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    public String getElementName() {
        return elementName;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementEnabled(boolean elementEnabled) {
        this.elementEnabled = elementEnabled;
    }

    public boolean isElementEnabled() {
        return elementEnabled;
    }

    public int getPagePosition() {
        return pagePosition;
    }

    public void setPagePosition(int pagePosition) {
        this.pagePosition = pagePosition;
    }

    public boolean isElementIsVisibleSi() {
        return elementIsVisibleSi;
    }

    public String getElementVisibleSiName() {
        return elementVisibleSiName;
    }

    public void setElementVisibleSiName(String elementVisibleSiName) {
        this.elementVisibleSiName = elementVisibleSiName;
    }

    public void setElementVisibleSiValue(String elementVisibleSiValue) {
        this.elementVisibleSiValue = elementVisibleSiValue;
    }

    public String getElementVisibleSiValue() {
        return elementVisibleSiValue;
    }

    public String getElementVisibleSi() {
        return elementVisibleSi;
    }

    public int getElementDisableOthers() {
        return elementDisableOthers;
    }

    public boolean isShowen() {
        return isShown;
    }

    public void setShown(boolean isShown){
        this.isShown = isShown;
    }

    public boolean isViewAnswered() {
        return isViewAnswered;
    }

    public void setViewAnswered(boolean viewAnswered) {
        isViewAnswered = viewAnswered;
    }

    public void setConditionChangeListener(IConditionChangeListener conditionChangeListener){
        this.conditionChangeListener = conditionChangeListener;
    }

    public boolean isJustView() {
        return isJustView;
    }

    public void setJustView(boolean justView) {
        isJustView = justView;
    }

    //endregion
}
