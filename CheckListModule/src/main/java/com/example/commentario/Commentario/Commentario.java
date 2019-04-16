package com.example.commentario.Commentario;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.commentario.MultiTextGenerator.MultiText;
import com.example.commentario.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.commentario.GlobalFuncs.conf_id;
import static com.example.commentario.GlobalFuncs.conf_isRequired;
import static com.example.commentario.GlobalFuncs.conf_name;
import static com.example.commentario.GlobalFuncs.createTitle;
import static com.example.commentario.PageGenerator.CheckListPager.setMandatories;


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

public class Commentario extends LinearLayout implements TextWatcher {

    //region used variables
    private MultiText.MandatoryListener listener;
    private String requiredStr = "*";
    private int TYPE = 0;
    private boolean isFirstTime = true;
    private EditText comment;
//    private LinearLayout commentario;
    //endregion

    //region variables
    private String id;
    private Context context;
    private String name;
    private String item_id;
    private String title;
    private int maxLength;
    private boolean isFromFinish;
    private String pre_content;
    private String tipo;
    private boolean isRequired;
    private boolean status;
    private JSONObject element;
    private String visible_id;
    //endregion

    //region constructors
    public Commentario(Context context, boolean isFromFinish, String pre_content
            , boolean status, JSONObject element, MultiText.MandatoryListener listener) {
        super(context);
        this.context = context;
        this.isFromFinish = isFromFinish;
        this.pre_content = pre_content;
        this.status = status;
        this.element = element;
        this.listener = listener;
        init();
    }


    public Commentario(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Commentario(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    //endregion

    private void init() {

        getVariablesFromElement(element);
        handleInputType(tipo);

//        commentario = new LinearLayout(context);
        comment = new EditText(context);

        //region local views

        TextView maxLengthTxt = new TextView(context);
        //endregion

        //region set title props
        TextView titleText = createTitle(context, isRequired, element);
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

        comment.setBackground(context.getDrawable(R.drawable.ticket_edt));

        comment.setText(pre_content);

        comment.addTextChangedListener(this);

        maxLengthTxt.setText(context.getString(R.string.max_length_msg) + maxLength);

        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(maxLength);
        comment.setFilters(fArray);

        comment.setInputType(TYPE);

        comment.setEnabled(isFromFinish);
        //endregion

        addView(titleText);
        addView(comment);
        addView(maxLengthTxt);


    }

    private void getVariablesFromElement(JSONObject element) {
        try {
            tipo = element.has("tipo") ? element.getString("tipo") : "";
            id = element.has(conf_id) ? element.getString(conf_id) : "";
            name = element.has(conf_name) ? element.getString(conf_name) : "";
            maxLength = element.has("maxLength") ? element.getInt("maxLength") : 100;
            isRequired = element.has(conf_isRequired) ? element.getBoolean(conf_isRequired) : false;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getCommentValue() {
        isMandatoryAnswered();
        return comment.getText().toString();
    }

    private void isMandatoryAnswered() {
        if (comment.getText().toString().equals("")) {
            setMandatoryError();
            if (listener != null)
                listener.onMandatoryStatusError();
        }
    }

    public void setMandatoryError() {
        if (getView() != null)
            if (setMandatories)
                getView().setBackground(context.getDrawable(R.drawable.is_requiered));
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
            case "Integer":
                this.TYPE = InputType.TYPE_NUMBER_FLAG_DECIMAL;
                break;
            case "Float":
                this.TYPE = InputType.TYPE_CLASS_NUMBER;
                break;
            default:
                this.TYPE = InputType.TYPE_CLASS_TEXT;
                break;
        }
    }

    public static int dpToPx(float dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    //region text watcher
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        if (!isFirstTime)
            listener.onElementStatusChanged();

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

    public String getName() {
        return name;
    }

    public String getElementId() {
        return id;
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

    public void setListener(MultiText.MandatoryListener listener) {
        this.listener = listener;
    }
    //endregion
}
