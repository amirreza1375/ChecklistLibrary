package com.example.checklist.ProductCounter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.checklist.BaseViewModel.BaseView;
import com.example.checklist.BaseViewModel.ElemetActionListener;
import com.example.checklist.BaseViewModel.MandatoryListener;
import com.example.checklist.MultiTextGenerator.MultiText;
import com.example.checklist.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.checklist.GlobalFuncs.conf_id;
import static com.example.checklist.GlobalFuncs.conf_isAnswered;
import static com.example.checklist.GlobalFuncs.conf_isRequired;
import static com.example.checklist.GlobalFuncs.conf_productId;
import static com.example.checklist.GlobalFuncs.conf_title;
import static com.example.checklist.GlobalFuncs.conf_value;
import static com.example.checklist.GlobalFuncs.setOrgProps;
import static com.example.checklist.PageGenerator.CheckListPager.setMandatories;

public class ProductCounter extends BaseView implements TextWatcher {


    private Context context;
    private boolean isRequired;
    private boolean isOk = false;
    private int stockCount;
    private JSONObject answer;
    private boolean enabled;
    private ArrayList<ProductModel> productModels;
    private String name;
    private String title;
    private String negativeBtnTxt;
    private String positiveBtnTxt;
    private long productId;
    private boolean isFirstTime = true;
    private MandatoryListener listener;
    private String blankHint;

    private View mainView;
    private RadioGroup choicesGrp;
    private EditText counterEdt;
    private TextView titleTxt;
    private boolean isAnswered = false;

    private int appearElemet;

    public ProductCounter(Context context, String name, String title, String negativeBtnTxt, String positiveBtnTxt
            , int stockCount, long productId, MandatoryListener listener
            , JSONObject answer,String blankHint, boolean enabled, ElemetActionListener callBack) {
        super(context,callBack);
        this.name = name;
        this.title = title;
        this.negativeBtnTxt = negativeBtnTxt;
        this.positiveBtnTxt = positiveBtnTxt;
        this.stockCount = stockCount;
        this.productId = productId;
        this.listener = listener;
        this.blankHint = blankHint;
        this.answer = answer;
        this.enabled = enabled;
        this.context = context;
        init(context);
    }

    public ProductCounter(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProductCounter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(Context context) {

        setOrgProps(context,this);

        mainView = LayoutInflater.from(context).inflate(R.layout.layout_product_counter, this, false);
        choicesGrp = mainView.findViewById(R.id.choicesGrp);
        counterEdt = mainView.findViewById(R.id.countEdt);
        counterEdt.setHint(blankHint);
        counterEdt.addTextChangedListener(this);
        RadioButton okBtn = mainView.findViewById(R.id.okBtn);
        RadioButton notOkBtn = mainView.findViewById(R.id.notOkBtn);
        titleTxt = mainView.findViewById(R.id.titleTxt);

        setTitle(titleTxt, name);
        setIsRequired(true);
        setButtonsTitle(okBtn, notOkBtn);
        setProductCount(okBtn, stockCount);
        setAnswer(answer, okBtn, notOkBtn);
        setEnabled(okBtn,notOkBtn,counterEdt);

        choicesGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                listener.onElementStatusChanged(false);
                isAnswered = true;
                if (checkedId == R.id.okBtn) {
                    hiddenCounter();
                } else {
                    showCounter();
                }
            }
        });

        addView(mainView);

    }

    private void setEnabled(RadioButton okBtn, RadioButton notOkBtn, EditText counterEdt) {
        okBtn.setEnabled(enabled);
        notOkBtn.setEnabled(enabled);
        counterEdt.setEnabled(enabled);
    }

    private void setAnswer(JSONObject answer, RadioButton isOk, RadioButton notOk) {
        try {
            if (answer != null) {
                if (answer.has(conf_isAnswered)) {
                    if (answer.getBoolean(conf_isAnswered)) {
                        isAnswered = true;
                        int value = answer.getInt(conf_value);
                        if (value > 0) {
                            isOk.setChecked(true);
                            hiddenCounter();
                        } else {
                            notOk.setChecked(true);
                            showCounter();
                            counterEdt.setText((value * -1) + "");
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setIsRequired(boolean isRequired) {
        this.isRequired = isRequired;
    }

    private void setTitle(TextView titleTxt, String name) {
        titleTxt.setText(title + " " + name + " ? ");
    }

    private void setProductCount(Button productCountTxt, int stock) {
        productCountTxt.setText(productCountTxt.getText().toString() + " (" + stock + ") ");

    }

    private void setButtonsTitle(RadioButton okBtn, RadioButton notOkBtn) {

        okBtn.setText(positiveBtnTxt);
        notOkBtn.setText(negativeBtnTxt);

    }

    private boolean isMandatoriesAnswered(boolean isNextClicked) {
        if (isRequired) {
            if (isAnswered) {
                if (isOk) {
                    return true;
                } else {
                    if (counterEdt.getText().toString().equals("")) {
                        if (isNextClicked)
                            setMandatoryError();
                        listener.onMandatoryStatusError();
                        return false;
                    }
                }
            }else{
                listener.onMandatoryStatusError();
                if (isNextClicked)
                    setMandatoryError();
                return false;
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


    public int getValue(boolean isNextClicked) {
        isMandatoriesAnswered(isNextClicked);
        if (isOk)
            return stockCount;
        else {
            try {
                String countStr = counterEdt.getText().toString();
                int count = Integer.parseInt(countStr);
                return count * -1;
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }
    }

    private void showCounter() {
        this.isOk = false;
        if (counterEdt.getText().toString().equals("") && !isFirstTime)
            setMandatoryError();
        counterEdt.setVisibility(VISIBLE);
    }

    private void hiddenCounter() {
        removeMandatoryError();
        this.isOk = true;
        counterEdt.setVisibility(GONE);
    }

    public boolean getIsAnswered() {
        return isAnswered;
    }

    public long getProductId() {
        return productId;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!isFirstTime)
            listener.onElementStatusChanged(false);

        if (isRequired) {
            if (s.length() == 0 && !isFirstTime) {
                setMandatoryError();
            } else {
                removeMandatoryError();
            }
        }
        isFirstTime = false;
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
