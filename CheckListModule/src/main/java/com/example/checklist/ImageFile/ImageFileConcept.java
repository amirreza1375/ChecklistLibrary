package com.example.checklist.ImageFile;

import android.app.Activity;
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

import com.example.checklist.BaseViewModel.BaseView;
import com.example.checklist.BaseViewModel.ElemetActionListener;
import com.example.checklist.BaseViewModel.MandatoryListener;
import com.example.checklist.Config;
import com.example.checklist.MultiTextGenerator.MultiText;
import com.example.checklist.PictureElement.PicturePickerItemModel;
import com.example.checklist.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.checklist.GlobalFuncs.conf_id;
import static com.example.checklist.GlobalFuncs.conf_name;
import static com.example.checklist.GlobalFuncs.createTitle;
import static com.example.checklist.GlobalFuncs.dpToPx;
import static com.example.checklist.GlobalFuncs.log;
import static com.example.checklist.PageGenerator.CheckListPager.setMandatories;

public class ImageFileConcept extends BaseView {

    //region used variables
    private Button button;
    private ButtonPressedCallBack callBack;
    private boolean isRequiredEach;
    //endregion

    //region variables
    private String title;
    private JSONObject element;
    private String btnText;
    private boolean enable;
    private boolean hasPic;
    private MandatoryListener listener;
    private Context context;
    //endregion

    //region constructors
    public ImageFileConcept(Context context
            , JSONObject element, String btnText, boolean enable, boolean hasPic, ElemetActionListener callBack) {
        super(context,callBack);
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

    private void init(Context context) {
        try {
            visibleSi = element.getString("visibleIf");
            isVisibleSi = true;
            name = element.getString(conf_name);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        getVariablesFromElement(element);

        //region parent props
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(dpToPx(8, context), dpToPx(8, context), dpToPx(8, context), dpToPx(8, context));
        setLayoutParams(layoutParams);
        //endregion


        CardView cardView = new CardView(context);
        //region cardView props
        LayoutParams cardParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(dpToPx(8, context), dpToPx(8, context), dpToPx(8, context), dpToPx(8, context));
        cardView.setLayoutParams(cardParams);
        cardView.setCardElevation(4.0f);
        cardView.setRadius(16.0f);
        //endregion

        LinearLayout cardInside = new LinearLayout(context);
        //region inner layout props
        LayoutParams innerLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        cardInside.setLayoutParams(innerLayoutParams);
        cardInside.setOrientation(LinearLayout.VERTICAL);
        //endregion

        TextView txt;//= new TextView(context);
        //region txt props
        txt = createTitle(context, isMandatory, element);

        //endregion

        button = new Button(context);
        //region button props
        LayoutParams btnParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        btnParams.setMargins(dpToPx(32, context), dpToPx(8, context), dpToPx(32, context), dpToPx(8, context));
        button.setLayoutParams(btnParams);

        if (!hasPic) {
            button.setBackground(context.getResources().getDrawable(R.drawable.new_login_btn));
            makeMeBlink(button);
        } else {
            button.setBackground(context.getResources().getDrawable(R.drawable.new_btn_back));
            button.clearAnimation();
        }

        button.setTextColor(Color.WHITE);
        button.setText(btnText);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageFileConcept.super.callBack.onAction("ImageButton",getElementId(),element.toString(),-1);
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

        } else {
            if (hasPic) {//preview mode with picture must show
                cardView.addView(cardInside);
                cardInside.addView(txt);
                cardInside.addView(button);
                addView(cardView);
            }else{
                ImageFileConcept.super.callBack.onAction("Image not have value",getElementId(),element.toString(),-1);
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

    public void setMandatoryError() {
        if (setMandatories)
            this.setBackground(context.getResources().getDrawable(R.drawable.is_requiered));
    }

    public void removeMandatoryError() {
        this.setBackground(null);
    }


    public boolean isMandatoryPictureTaken() {
        if (isMandatory) {

            String picsStr = context.getSharedPreferences(Config.sharedPreferencName, Context.MODE_PRIVATE)
                    .getString(Config.pictures, "");
            try {
                JSONArray pics = new JSONArray(picsStr);

                for (int i = 0; i < pics.length(); i++) {

                    JSONObject pic = pics.getJSONObject(i);

                    if (pic.getString(conf_id)
                            .equals(viewID)) {
                        return true;
                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
                log(e.getMessage());
                return false;
            }

        } else {
            return true;
        }
        return false;
    }

    private boolean  isImageFileConceptHasImage(Context context, ImageFileConcept imageFileConcept) {
        String picturesStr = context.getSharedPreferences(Config.sharedPreferencName, Context.MODE_PRIVATE)
                .getString(Config.pictures, "");
        try {
            JSONArray pictures = new JSONArray(picturesStr);
            for (int i = 0; i < pictures.length(); i++) {
                JSONObject picture = pictures.getJSONObject(i);
                if (picture.getString(PicturePickerItemModel.conf_id)
                        .equals(imageFileConcept.getElementId())) {
                    return true;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            log(e.getMessage());
//            listener.onCheckListError(e.getMessage());
        }
        return false;
    }

    private void getVariablesFromElement(JSONObject element) {
        try {
            boolean isRE, isR;
            isRE = element.has("isRequiredEach") ? element.getBoolean("isRequiredEach") : false;
            isR = element.has("isRequired") ? element.getBoolean("isRequired") : false;

            isMandatory = isR || isRE;

            viewID = element.has(PicturePickerItemModel.conf_id) ? element.getString(conf_id) : "";
        } catch (JSONException e) {
            e.printStackTrace();
            log(e.getMessage());
        }
    }


    public static int spTopx(float sp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

    public void setHasPicStatus(final Context context) {
        ImageFileConcept.super.callBack.onAction("Image has value",getElementId(),element.toString(),-1);
        Activity activity = (Activity) context;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                button.clearAnimation();
                button.setBackground(context.getResources().getDrawable(R.drawable.new_btn_back));
            }
        });
    }

    public ButtonPressedCallBack getCallBack() {
        return callBack;
    }

    public void setCallBack(ButtonPressedCallBack callBack) {
        this.callBack = callBack;
    }


    public Button getButton() {
        return button;
    }

    public void setButton(Button button) {
        this.button = button;
    }

    public void checkMadatory(Context context) {
        if (isImageFileConceptHasImage(context, this)) {
            setHasPicStatus(context);
            isViewAnswered = true;
        }
    }


    public interface ButtonPressedCallBack {
        void onButtonClicked(JSONObject element);
    }

}
