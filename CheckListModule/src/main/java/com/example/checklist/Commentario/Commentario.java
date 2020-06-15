package com.example.checklist.Commentario;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.checklist.BaseViewModel.BaseView;
import com.example.checklist.BaseViewModel.ElemetActionListener;
import com.example.checklist.BaseViewModel.MandatoryListener;
import com.example.checklist.Config;
import com.example.checklist.MultiTextGenerator.MultiText;
import com.example.checklist.R;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.checklist.Config.TIME_TRACKER_TIPO;
import static com.example.checklist.GlobalFuncs.conf_id;
import static com.example.checklist.GlobalFuncs.conf_isRequired;
import static com.example.checklist.GlobalFuncs.conf_name;
import static com.example.checklist.GlobalFuncs.createTitle;
import static com.example.checklist.GlobalFuncs.log;
import static com.example.checklist.PageGenerator.CheckListPager.setMandatories;


/**
 * this class is custom {@link EditText}
 * that can handle every parameteres that passed to it
 * {@link String name} -> not used here , the name of the componnent in server
 * {@link String item_id} -> also not used , the id of componnent in server
 * {@link String title} -> title of componnent and set on top of that
 * {@link Integer maxLength} -> max number of characters in edittext
 * {@link Boolean isFromFinish} -> Flag that tells we are in edit mode(DRAFT) or preview(FINISHED)
 * {@link String preContent} -> in Draft mode we have value that user inserted before
 * {@link String tipo} -> input type of edittext
 * {@link Boolean isRequired} -> set status as mandatory that user could not pass with out
 * inserting some string
 * {@link Boolean status} ->
 * {@link String visible_id} -> id that shows or hides componnent as condition
 */

public class Commentario extends BaseView implements TextWatcher {
    private static final String TAG = "Commentario";
    //region used variables
    private boolean IsTextChanged = true;
    private MandatoryListener listener;
    private String requiredStr = "*";
    private int TYPE = 0;
    private boolean isFirstTime = true;
    private EditText comment;
//    private LinearLayout commentario;
    //endregion

    private Config.tipo commentTipo;

    //region variables
    private Context context;
    private String item_id;
    private String title;
    private int maxLength;
    private boolean isFromFinish;
    private String pre_content;
    private String tipo;
    private boolean status;
    private JSONObject element;
    private String visible_id;
    //endregion

    //region constructors
    public Commentario(Context context, boolean isFromFinish, String pre_content
            , boolean status, JSONObject element, MandatoryListener listener, ElemetActionListener callBack) {
        super(context,callBack);
        this.context = context;
        this.isFromFinish = isFromFinish;
        this.pre_content = pre_content;
        this.status = status;
        this.element = element;
        this.listener = listener;
        init();
        checkMandatory(comment.getText().toString());
    }


    public Commentario(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Commentario(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    //endregion

    private void init() {

        try {
            visibleSi = element.getString("visibleIf");
            isVisibleSi = true;
            setVisibility(INVISIBLE);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        getVariablesFromElement(element);


//        commentario = new LinearLayout(context);
        comment = new EditText(context);
        handleInputType(tipo);

        //region local views

        TextView maxLengthTxt = new TextView(context);
        //endregion

        //region set title props
        TextView titleText = createTitle(context, isMandatory, element);
        //endregion

        //region set maxLength props
        LinearLayout.LayoutParams maxLengthParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                        , LinearLayout.LayoutParams.WRAP_CONTENT);
        maxLengthTxt.setLayoutParams(maxLengthParams);
        maxLengthTxt.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
        //endregion

        //region set commentario props
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                        , LinearLayout.LayoutParams.WRAP_CONTENT);

        params.setMargins(dpToPx(8, context), dpToPx(8, context), dpToPx(8, context), dpToPx(8, context));
        setPadding(dpToPx(8, context), dpToPx(8, context), dpToPx(8, context), dpToPx(8, context));
        setLayoutParams(params);
        setOrientation(LinearLayout.VERTICAL);
        //endregion


        //region set edittext props

        comment.setPadding(16, 0, 16, 0);

        LinearLayout.LayoutParams commentParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                        , dpToPx(42, context));
        comment.setLayoutParams(commentParams);

        //region edt holder

        LinearLayout edtHolder = new LinearLayout(context);
        edtHolder.setOrientation(HORIZONTAL);

        if (tipo.equals("Price")) {
            LinearLayout.LayoutParams dollarParams =
                    new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT
                            , ViewGroup.LayoutParams.WRAP_CONTENT);
            dollarParams.setMargins(dpToPx(4, context), dpToPx(0, context), dpToPx(0, context), dpToPx(0, context));
            TextView dollar = new TextView(context);

            dollar.setTextColor(Color.BLACK);
            dollar.setTextSize(16f);
            dollar.setLayoutParams(dollarParams);
            dollar.setText("$");
            edtHolder.addView(dollar);
        }

        edtHolder.addView(comment);

        //endregion
        edtHolder.setBackground(context.getResources().getDrawable(R.drawable.ticket_edt));
//        comment.setBackground(context.getResources().getDrawable(android.R.drawable.screen_background_light_transparent));

        comment.setText(pre_content);

        comment.addTextChangedListener(this);

        maxLengthTxt.setText(context.getString(R.string.max_length_msg) + maxLength);

        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(maxLength);
        comment.setFilters(fArray);

//        comment.setInputType(TYPE);

        comment.setEnabled(isFromFinish);


        //endregion

        setCommentTipoStatus();

        addView(titleText);
        addView(edtHolder);
        addView(maxLengthTxt);


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

    private void getVariablesFromElement(JSONObject element) {
        try {

            tipo = element.has("Tipo") ? element.getString("Tipo") : "";
            viewID = element.has(conf_id) ? element.getString(conf_id) : "";
            name = element.has(conf_name) ? element.getString(conf_name) : "";
            maxLength = element.has("maxLength") ? element.getInt("maxLength") : 100;
            isMandatory = element.has(conf_isRequired) ? element.getBoolean(conf_isRequired) : false;
        } catch (JSONException e) {
            e.printStackTrace();
            log(e.getMessage());
        }
    }

    public String getCommentValue(boolean isNextClicked) {
        isMandatoryAnswered(isNextClicked);
//        callBack.onAction("getCommentValue",getElementId(),comment.getText().toString(),-1);
        return comment.getText().toString();
    }

    private void isMandatoryAnswered(boolean isNextClicked) {
        if (isMandatory) {
            if (comment.getText().toString().equals("")) {
                if (isNextClicked)
                    setMandatoryError();
                if (listener != null)
                    listener.onMandatoryStatusError();
            }
        }
    }

    public void setMandatoryError() {
        if (getView() != null)
            if (setMandatories)
                getView().setBackground(context.getResources().getDrawable(R.drawable.is_requiered));
    }

    public void removeMandatoryError() {
        if (getView() != null)
            getView().setBackground(null);
    }

    public LinearLayout getView() {
        return this;
    }

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

    public static int dpToPx(float dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    private void setCommentTipoStatus() {
        if (tipo.equals(TIME_TRACKER_TIPO)) {
            commentTipo = Config.tipo.TIME_TRACKER;
            comment.setEnabled(false);

        }
    }


    //region text watcher
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        if (tipo.equals("Price")) {
            if (IsTextChanged) {
                IsTextChanged = false;
                comment.setText(getPriceWithValidates(s));
                comment.setSelection(comment.getText().length());
            } else {
                IsTextChanged = true;
            }

        }
        checkMandatory(comment.getText().toString());

        if (!isFirstTime) {
            listener.onElementStatusChanged(false);
        }

        if (isMandatory) {
            if (s.length() == 0 && !isFirstTime) {
                setMandatoryError();
            } else {
                removeMandatoryError();
            }
        }
        isFirstTime = false;
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
    public void afterTextChanged(Editable s) {
        Log.i(TAG, "afterTextChanged: ");
    }

    public void setCommentValue(String value) {
        comment.setText(value);
    }

    public Config.tipo getCommentTipo() {
        return commentTipo;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getVisible_id() {
        return visible_id;
    }

    public void setVisible_id(String visible_id) {
        this.visible_id = visible_id;
    }

    public void setListener(MandatoryListener listener) {
        this.listener = listener;
    }
    //endregion
}
