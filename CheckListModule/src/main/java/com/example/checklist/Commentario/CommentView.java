package com.example.checklist.Commentario;

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
import com.example.checklist.Config;
import com.example.checklist.GlobalFuncs;
import com.example.checklist.R;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.checklist.Config.TIME_TRACKER_TIPO;
import static com.example.checklist.GlobalFuncs.conf_value;

public class CommentView  extends BaseViewModel implements TextWatcher {

    private Config.tipo commentTipo;
    private EditText comment;
    private String commentAnswer;
    private TextView dollarSignTxt,maxLengthTxt;
    private boolean isFirstTime = true;
    private boolean IsTextChanged = true;

    public CommentView(Context context, JSONObject element, ElemetActionListener callBack, JSONObject viewAnswer, boolean isEnabled,int elementPosition,int viewPosition) {
        super(context, element, callBack, viewAnswer, isEnabled,elementPosition,viewPosition);
    }

    public CommentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CommentView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public JSONObject getValue() {
        JSONObject answer = getGeneralValues();

        try {
            answer.put(conf_value,comment.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setansweredStatus(comment.getText().toString());
        return answer;
    }

    private void setansweredStatus(String answer) {
        if (answer.equals(""))
            viewAnswerRemoved();
        else
            viewAnswered();
    }

    @Override
    public LinearLayout InitilizeView(Context context) {



        titleText = baseView.findViewById(R.id.titleText);

        comment = baseView.findViewById(R.id.commentEdt);
        dollarSignTxt = baseView.findViewById(R.id.dollarSign);
        maxLengthTxt = baseView.findViewById(R.id.maxLengthTxt);

        if (elementTipo.equals("Price"))
            dollarSignTxt.setVisibility(VISIBLE);

        handleInputType(elementTipo);

        maxLengthTxt.setText(elementMaxLength+"");
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(elementMaxLength);
        comment.setFilters(fArray);

        setCommentTipoStatus();

        comment.setEnabled(elementEnabled);

        comment.addTextChangedListener(this);

        if (commentAnswer != null) {
            if (!commentAnswer.equals(""))
                comment.setText(commentAnswer);
        }

        return this;

    }

    @Override
    public void clearData() {

    }

    @Override
    public void getElementProps() {

    }

    @Override
    public int getView() {
        return R.layout.layout_comment_view;
    }

    @Override
    public void getAnswer(JSONObject answer) {
        try {
            this.commentAnswer = answer.getString(conf_value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void viewAnswered() {

    }

    @Override
    public void viewAnswerRemoved() {

    }


    //region price validator
    private String getPriceWithValidates(CharSequence s) {

        String price = s.toString();

        if (price.length() < 4) {
            return price;
        }

        //so price is greater than 3

        String newPrice = removeDotsFromString(price);

        char priceChars[] = newPrice.toCharArray();

        String finalPrice = "";

        int counter = 0;

        for (int i = priceChars.length - 1; i >= 0; i--) {

            finalPrice += priceChars[i];

            counter++;

            if (counter == 3 && i != 0) {
                finalPrice += ".";
                counter = 0;
            }

        }

        return reverseString(finalPrice);
    }

    private String removeDotsFromString(String price) {
        return price.replaceAll("\\.", "");
    }

    private String reverseString(String str) {
        char chars[] = str.toCharArray();
        String finalStr = "";
        for (int i = chars.length - 1; i >= 0; i--) {
            finalStr += chars[i];
        }
        return finalStr;
    }

    //endregion

    private void handleInputType(String tiop) {
        switch (tiop) {
            case "number":
            case "Price":
                comment.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            case "Float":
                comment.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                break;
            default:
                comment.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
        }
    }


    private void setCommentTipoStatus() {
        if (elementTipo.equals(TIME_TRACKER_TIPO)) {
            commentTipo = Config.tipo.TIME_TRACKER;
            comment.setEnabled(false);

        }
    }

    public Config.tipo getCommentTipo() {
        return commentTipo;
    }

    public void setCommentTipo(Config.tipo commentTipo) {
        this.commentTipo = commentTipo;
    }

    public void setCommentValue(String value) {
        comment.setText(value);
    }
    public String getCommentValue() {
        isMandatoryAnswered();
//        callBack.onAction("getCommentValue",getElementId(),comment.getText().toString(),-1);
        return comment.getText().toString();
    }

    private void checkMandatory(String s) {
        if (s.trim().length() > 0) {
            isViewAnswered = true;
            removeMandatoryError();
        }
        else {
            isViewAnswered = false;
        }


    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (elementTipo.equals("Price")) {
            if (IsTextChanged) {
                IsTextChanged = false;
                comment.setText(getPriceWithValidates(charSequence));
                comment.setSelection(comment.getText().length());
            } else {
                IsTextChanged = true;
            }

        }
        checkMandatory(comment.getText().toString());

        if (!isFirstTime) {
//            mandatoryListener.onElementStatusChanged(false);
        }

        if (isMandatory) {
            if (charSequence.length() == 0 && !isFirstTime) {
                setMandatoryError();
            } else {
                removeMandatoryError();
            }
        }
        isFirstTime = false;
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
