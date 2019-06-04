package com.example.checklist.ImageFile;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.checklist.Config;
import com.example.checklist.MultiTextGenerator.MultiText;
import com.example.checklist.PictureElement.PicturePickerItemModel;
import com.example.checklist.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.checklist.GlobalFuncs.conf_id;
import static com.example.checklist.GlobalFuncs.createTitle;
import static com.example.checklist.GlobalFuncs.dpToPx;
import static com.example.checklist.GlobalFuncs.log;
import static com.example.checklist.PageGenerator.CheckListPager.setMandatories;

public class ImageFileConcept extends LinearLayout  {

    //region used variables
    private Button button;
    private ButtonPressedCallBack callBack;
    private boolean isRequired;
    private boolean isRequiredEach;
    //endregion

    //region variables
    private String title;
    private JSONObject element;
    private String btnText;
    private boolean enable;
    private boolean hasPic;
    private String elementId;
    private MultiText.MandatoryListener listener;
    private Context context;
    //endregion

    //region constructors
    public ImageFileConcept(Context context
            , JSONObject element, String btnText,boolean enable,boolean hasPic) {
        super(context);
        this.context = context;
        this.element = element;
        this.btnText = btnText;
        this.enable = enable;
        this.hasPic = hasPic;
        init(context);
    }

    public ImageFileConcept(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageFileConcept(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //endregion

    private void init(Context context){

        getVariablesFromElement(element);

        //region parent props
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(dpToPx(8,context),dpToPx(8,context),dpToPx(8,context),dpToPx(8,context));
        setLayoutParams(layoutParams);
        //endregion


        CardView cardView = new CardView(context);
        //region cardView props
        LayoutParams cardParams = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(dpToPx(8,context),dpToPx(8,context),dpToPx(8,context),dpToPx(8,context));
        cardView.setLayoutParams(cardParams);
        cardView.setCardElevation(4.0f);
        cardView.setRadius(16.0f);
        //endregion

        LinearLayout cardInside = new LinearLayout(context);
        //region inner layout props
        LayoutParams innerLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
        cardInside.setLayoutParams(innerLayoutParams);
        cardInside.setOrientation(LinearLayout.VERTICAL);
        //endregion

        TextView txt ;//= new TextView(context);
        //region txt props
        txt = createTitle(context,isRequired,element);

        //endregion

        button = new Button(context);
        //region button props
        LayoutParams btnParams = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
        btnParams.setMargins(dpToPx(32,context),dpToPx(8,context),dpToPx(32,context),dpToPx(8,context));
        button.setLayoutParams(btnParams);

        if (!hasPic) {
            button.setBackground(context.getResources().getDrawable(R.drawable.picture_btn_back));
            makeMeBlink(button);
        } else {
            button.setBackground(context.getResources().getDrawable(R.drawable.picture_btn_has_pic));
            button.clearAnimation();
        }

        button.setTextColor(Color.WHITE);
        button.setText(btnText);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                removeMandatoryError();
                if (callBack != null)
                callBack.onButtonClicked(element);
            }
        });
        //endregion


        //region add views
        if (enable) {//check list mode must show
            cardView.addView(cardInside);
            cardInside.addView(txt);
            cardInside.addView(button);
            addView(cardView);

        }else {
            if (hasPic){//preview mode with picture must show
                cardView.addView(cardInside);
                cardInside.addView(txt);
                cardInside.addView(button);
                addView(cardView);
            }
        }
        //endregion

    }

    private void makeMeBlink(View view) {
        Animation anim = new AlphaAnimation(0.6f, 1.0f);
        anim.setDuration(300);
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        view.setAnimation(anim);
        view.startAnimation(anim);
        anim.start();
    }

    public void setMandatoryError(){
        if (setMandatories)
            this.setBackground(context.getResources().getDrawable(R.drawable.is_requiered));
    }
    public void removeMandatoryError(){
        this.setBackground(null);
    }


    public boolean isMandatoryPictureTaken() {
        if (isRequired){

            String picsStr = context.getSharedPreferences(Config.sharedPreferencName,Context.MODE_PRIVATE)
                    .getString(Config.pictures,"");
            try {
                JSONArray pics = new JSONArray(picsStr);

                for (int i = 0  ; i < pics.length() ; i++){

                    JSONObject pic = pics.getJSONObject(i);

                    if (pic.getString(conf_id)
                            .equals(elementId)){
                        return true;
                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
                log(e.getMessage());
                return false;
            }

        }else {
            return true;
        }
        return false;
    }

    private void getVariablesFromElement(JSONObject element) {
        try {
            boolean isRE,isR;
            isRE = element.has("isRequiredEach") ? element.getBoolean("isRequiredEach") : false;
            isR = element.has("isRequired") ? element.getBoolean("isRequired") : false;

            isRequired = isR || isRE;

            elementId = element.has(PicturePickerItemModel.conf_id) ? element.getString(conf_id) : "";
        } catch (JSONException e) {
            e.printStackTrace();
            log(e.getMessage());
        }
    }


    public static int spTopx(float sp , Context context){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,sp,context.getResources().getDisplayMetrics());
    }

    public void setHasPicStatus(Context context){
        button.clearAnimation();
        button.setBackground(context.getResources().getDrawable(R.drawable.picture_btn_has_pic));
    }

    public ButtonPressedCallBack getCallBack() {
        return callBack;
    }

    public void setCallBack(ButtonPressedCallBack callBack) {
        this.callBack = callBack;
    }


    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public Button getButton() {
        return button;
    }

    public void setButton(Button button) {
        this.button = button;
    }


    public interface ButtonPressedCallBack{
        void onButtonClicked(JSONObject element);
    }

}
