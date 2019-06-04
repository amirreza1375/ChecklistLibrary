package com.example.checklist.ProductCounter;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.example.checklist.MultiTextGenerator.MultiText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.checklist.GlobalFuncs.conf_id;
import static com.example.checklist.GlobalFuncs.conf_isAnswered;
import static com.example.checklist.GlobalFuncs.conf_name;
import static com.example.checklist.GlobalFuncs.conf_position;
import static com.example.checklist.GlobalFuncs.conf_productCount;
import static com.example.checklist.GlobalFuncs.conf_productId;
import static com.example.checklist.GlobalFuncs.conf_title;
import static com.example.checklist.GlobalFuncs.conf_type;
import static com.example.checklist.GlobalFuncs.conf_value;
import static com.example.checklist.GlobalFuncs.log;
import static com.example.checklist.GlobalFuncs.setOrgProps;

public class ProductCounterMaker extends LinearLayout {

    private String negativeBtnTitle = "ProductNegativeButton";
    private String positiveBtnTitle = "ProductPositiveButton";
    private String blankHint = "productQuestionCount";

    private ArrayList<ProductCounter> productCounters;
    private Context context;
    private JSONObject element;
    private JSONObject answer;
    private boolean enabled;
    private ArrayList<ProductModel> productModels;
    private MultiText.MandatoryListener listener;
    private String shopId;
    private int position;
    private String negativeBtnTxt;
    private String positiveBtnTxt;
    private String titleTxt;
    private String elementId;
    private String elementName;

    public ProductCounterMaker(Context context, JSONObject element, JSONObject answer, boolean enabled
            , ArrayList<ProductModel> productModels
            , MultiText.MandatoryListener listener, String shopId
            ,int position) {
        super(context);
        this.context = context;
        this.element = element;
        this.answer = answer;
        this.enabled = enabled;
        this.productModels = productModels;
        this.listener = listener;
        this.shopId = shopId;
        this.position = position;
        productCounters = new ArrayList<>();
        init(context);
    }

    public ProductCounterMaker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProductCounterMaker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(Context context) {

        setOrgProps(context, this);

        getButtonsTexts();

        setTitle(element);

        setElementId(element);

        setElementName(element);

        createsElements();

    }

    private void setElementName(JSONObject element) {
        try {
            this.elementName = element.getString(conf_name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setElementId(JSONObject element) {
        try {
            this.elementId = element.getString(conf_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setTitle(JSONObject element) {
        try {
            this.titleTxt = element.has(conf_title) ? element.getString(conf_title) : "No Title";
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void createsElements() {
        log("create elements");
        for (int i = 0; i < productModels.size(); i++) {
            ProductModel model = productModels.get(i);
            if (isShopExist(model.getShopId())) {
                log(model.getShopId());
                createElement(model);
            }

        }
    }

    private void createElement(ProductModel model) {
        ProductCounter productCounter = new ProductCounter(context,model.getProductName(), titleTxt
                , negativeBtnTxt, positiveBtnTxt, model.getStock()
                , model.getProductId(), listener, getAnswer(model)
                ,getBlankHintText(element), enabled);
        productCounters.add(productCounter);
        addView(productCounter);
        log("product id = "+productCounter.getProductId());
        log("products size = "+productCounters.size());
    }

    private String getBlankHintText(JSONObject element) {
        try {
            return element.getString(blankHint);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "countados";
    }

    private JSONObject getAnswer(ProductModel model) {
        try {
            JSONArray answers = answer.getJSONArray(conf_value);
            for (int i = 0 ; i < answers.length() ; i++){
                JSONObject answer = answers.getJSONObject(i);
                if (answer.getLong(conf_productId) == model.getProductId())
                    return answer;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    private boolean isShopExist(String shop) {
        if (shop == null)
            return true;
        if (shop.equals(""))
            return true;
        String shops[] = shop.split(",");
        for (int i = 0; i < shops.length; i++) {
            String s = shops[i];
            if (s.equals(shopId))
                return true;
        }
        return false;
    }

    private void getButtonsTexts() {
        try {
            this.negativeBtnTxt = element.has(negativeBtnTitle) ? element.getString(negativeBtnTitle) : "Not Ok";
            this.positiveBtnTxt = element.has(positiveBtnTitle) ? element.getString(positiveBtnTitle) : "Ok";
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getValue(boolean isNextClicked) {
        JSONObject elementAnswer = new JSONObject();
        try {

            elementAnswer.put(conf_type, conf_productCount);
            elementAnswer.put(conf_id, elementId);
            elementAnswer.put(conf_name, elementName);
            elementAnswer.put(conf_position,position);

            JSONArray values = new JSONArray();

            for (int i = 0; i < productCounters.size(); i++) {

                ProductCounter productCounter = productCounters.get(i);
                JSONObject value = new JSONObject();

                value.put(conf_type, conf_productCount);
                value.put(conf_id, elementId);
                value.put(conf_name, elementName);
                value.put(conf_position,position);
                value.put(conf_isAnswered, productCounter.getIsAnswered());
                value.put(conf_productId, productCounter.getProductId());
                value.put(conf_value, productCounter.getValue(isNextClicked));

                values.put(value);

            }

            elementAnswer.put(conf_value,values);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return elementAnswer;

    }

}
