package com.example.checklist.ProductCounter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.example.checklist.BaseViewModel.BaseViewModel;
import com.example.checklist.BaseViewModel.ElemetActionListener;
import com.example.checklist.BaseViewModel.MandatoryListener;
import com.example.checklist.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProductView extends BaseViewModel {

    private String negativeBtnTitle = "ProductNegativeButton";
    private String positiveBtnTitle = "ProductPositiveButton";
    private String blankHintKey = "productQuestionCount";

    private ArrayList<ProductModel> productModels;
    private String shopId;
    private String negativeBtnTxt;
    private String positiveBtnTxt;
    private long productId;
    private boolean isFirstTime = true;
    private String blankHint;
    private boolean isOk = false;
    private int stockCount;

    private RadioGroup choicesGrp;
    private EditText counterEdt;

    public ProductView(Context context, JSONObject element, ElemetActionListener callBack, JSONObject viewAnswer,
                       ArrayList<ProductModel> productModels
            , String shopId, boolean isEnabled,int elementPosition,int viewPosition) {
        super(context, element, callBack, viewAnswer, isEnabled,elementPosition,viewPosition);
        this.productModels = productModels;
        this.shopId = shopId;
    }

    public ProductView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ProductView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public JSONObject getValue() {
        return null;
    }

    @Override
    public LinearLayout InitilizeView(Context context) {
        return this;
    }

    @Override
    public void clearData() {

    }

    @Override
    public void getElementProps() {
        try {
            this.negativeBtnTxt = element.has(negativeBtnTitle) ? element.getString(negativeBtnTitle) : "Not Ok";
            this.positiveBtnTxt = element.has(positiveBtnTitle) ? element.getString(positiveBtnTitle) : "Ok";
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getView() {
        return R.layout.layout_product_view;
    }

    @Override
    public void getAnswer(JSONObject answer) {

    }

    @Override
    public void viewAnswered() {

    }

    @Override
    public void viewAnswerRemoved() {

    }
}
