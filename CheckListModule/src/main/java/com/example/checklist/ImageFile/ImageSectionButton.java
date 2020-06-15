package com.example.checklist.ImageFile;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.checklist.BaseViewModel.BaseViewModel;
import com.example.checklist.BaseViewModel.ElemetActionListener;
import com.example.checklist.Config;
import com.example.checklist.PictureElement.PicturePickerItemModel;
import com.example.checklist.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.checklist.GlobalFuncs.conf_id;
import static com.example.checklist.GlobalFuncs.log;

public class ImageSectionButton extends BaseViewModel {

    private Button button;
    private ImageFileConcept.ButtonPressedCallBack buttonCallBack;
    private String btnText;
    private boolean hasPic;

    public ImageSectionButton(Context context, JSONObject element, String btnText, boolean hasPic, ElemetActionListener callBack, JSONObject viewAnswer
            , boolean isEnabled,int elementPosition,int viewPosition) {
        super(context, element, callBack, viewAnswer, isEnabled,elementPosition,viewPosition);
        this.btnText = btnText;
        this.hasPic = hasPic;
    }

    public ImageSectionButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageSectionButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public JSONObject getValue() {
        if (isMandatoryPictureTaken())
            viewAnswered();
        else
            viewAnswerRemoved();
        return null;
    }

    @Override
    public LinearLayout InitilizeView(Context context) {

        titleText = baseView.findViewById(R.id.titleText);
        button = baseView.findViewById(R.id.button);

        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageSectionButton.super.callBack.onAction("ImageButton", getElementId(), element.toString(), -1);
                removeMandatoryError();
                if (callBack != null)
                    buttonCallBack.onButtonClicked(element);
            }
        });

        if (!hasPic) {
            button.setBackground(context.getResources().getDrawable(R.drawable.new_login_btn));
            makeMeBlink(button);
        } else {
            button.setBackground(context.getResources().getDrawable(R.drawable.new_btn_back));
            button.clearAnimation();
        }

        if (!elementEnabled && !isImageFileConceptHasImage()) {
            this.setVisibility(GONE);
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
        return R.layout.layout_image_section_button_view;
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

    public boolean isMandatoryPictureTaken() {

        String picsStr = context.getSharedPreferences(Config.sharedPreferencName, Context.MODE_PRIVATE)
                .getString(Config.pictures, "");
        try {
            JSONArray pics = new JSONArray(picsStr);

            for (int i = 0; i < pics.length(); i++) {

                JSONObject pic = pics.getJSONObject(i);

                if (pic.getString(conf_id)
                        .equals(elementId)) {
                    return true;
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
            log(e.getMessage());
            return false;
        }


        return false;
    }

    public void checkMadatory(Context context) {
        if (isImageFileConceptHasImage()) {
            setHasPicStatus(context);
            isViewAnswered = true;
        }
    }

    public void setHasPicStatus(final Context context) {
        ImageSectionButton.super.callBack.onAction("Image has value", getElementId(), element.toString(), -1);
        Activity activity = (Activity) context;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                button.clearAnimation();
                button.setBackground(context.getResources().getDrawable(R.drawable.new_btn_back));
            }
        });
    }

    private boolean isImageFileConceptHasImage() {
        String picturesStr = context.getSharedPreferences(Config.sharedPreferencName, Context.MODE_PRIVATE)
                .getString(Config.pictures, "");
        try {
            JSONArray pictures = new JSONArray(picturesStr);
            for (int i = 0; i < pictures.length(); i++) {
                JSONObject picture = pictures.getJSONObject(i);
                if (picture.getString(PicturePickerItemModel.conf_id)
                        .equals(this.getElementId())) {
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

    public void setCallBack(ImageFileConcept.ButtonPressedCallBack callBack) {
        this.buttonCallBack = callBack;
    }

}
