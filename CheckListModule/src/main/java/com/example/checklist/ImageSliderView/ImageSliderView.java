package com.example.checklist.ImageSliderView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.Printer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.example.checklist.BaseViewModel.BaseViewModel;
import com.example.checklist.BaseViewModel.ElemetActionListener;
import com.example.checklist.R;
import com.example.checklist.imageview.GestureImageView;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class ImageSliderView extends BaseViewModel {

    private SliderLayout sliderLayout;
    private TextView errorTxt;
    private ImageView image;
    private ImagesViewer.ImageSliderListener listener;

    private boolean isErrorSent = false;

    public ImageSliderView(Context context, JSONObject element, ElemetActionListener callBack, JSONObject viewAnswer, boolean isEnabled
            , ArrayList<File> imageFiles, ArrayList<String> priorities
            , ArrayList<String> names, ImagesViewer.ImageSliderListener listener,int elementPosition,int viewPosition) {
        super(context, element, callBack, viewAnswer, isEnabled, imageFiles, priorities, names,elementPosition,viewPosition);
        this.listener = listener;
        isJustView = true;
        viewAnswered();
    }

    @Override
    public JSONObject getValue() {
        return null;
    }

    @Override
    public LinearLayout InitilizeView(Context context) {

        sliderLayout = baseView.findViewById(R.id.sliderView);
        image = baseView.findViewById(R.id.imageView);
        errorTxt = baseView.findViewById(R.id.errorView);

        CardView imageSliderViewHolder = baseView.findViewById(R.id.imageSliderViewHolder);

        if (checkStoragePermission()) {
            if (imageFiles.size() > 1) {//show all images
                sliderLayout.setVisibility(VISIBLE);
                image.setVisibility(GONE);
                errorTxt.setVisibility(GONE);
                setupSlider(element, sliderLayout);
            } else if (imageFiles.size() == 0) {//show zero image error
                imageSliderViewHolder.setVisibility(GONE);
            } else {//show one image
                sliderLayout.setVisibility(GONE);
                image.setVisibility(VISIBLE);
                errorTxt.setVisibility(GONE);
                createOneImageView();

            }
        } else {//show not permission error
            sliderLayout.setVisibility(GONE);
            image.setVisibility(GONE);
            errorTxt.setVisibility(VISIBLE);
            errorTxt.setText(context.getResources().getString(R.string.errPermissionStorage));
        }

        return this;
    }

    private void createOneImageView() {
        if (imageFiles.size() != 0) {
            Bitmap bitmap = BitmapFactory.decodeFile(imageFiles.get(0).getAbsolutePath());
            image.setImageBitmap(bitmap);
            image.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    show_image(imageFiles.get(0).getAbsolutePath()
                            , names.size() != 0 ? names.get(0) : ""
                            , priorities.size() != 0 ? priorities.get(0) : "0");
                }
            });

        }
    }

    public boolean checkStoragePermission() {
        return ActivityCompat.checkSelfPermission
                (context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void setupSlider(JSONObject element, SliderLayout sliderLayout) {

        for (int i = 0; i < imageFiles.size(); i++) {
            File imageFile = imageFiles.get(i);
            if (!imageFile.exists()) {
                callBack.onAction("Slider file not exist", getElementId(), element.toString(), -1);
                if (!isErrorSent) {
                    isErrorSent = true;
                    listener.onError(context.getString(R.string.ImageNoExist), ImagesViewer.ImageStatus.IMAGE_NOT_EXIST);
                }
            }

            if (imageFile.length() == 0) {
                callBack.onAction("Slider file size is zero", getElementId(), element.toString(), -1);
                if (!isErrorSent) {
                    isErrorSent = true;
                    listener.onError(context.getString(R.string.ImageHasProblem), ImagesViewer.ImageStatus.IMAGE_HAS_PROBLEM);
                }
            }

            TextSliderView textSliderView = new TextSliderView(context);
            textSliderView.image(imageFiles.get(i)).bundle(new Bundle())
                    .setScaleType(BaseSliderView.ScaleType.FitCenterCrop)
                    .getBundle().putString("image_path", String.valueOf(imageFiles.get(i)));
            textSliderView.getBundle().putString("name", names.get(i));
            textSliderView.getBundle().putString("priority", priorities.get(i));
            textSliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                @Override
                public void onSliderClick(BaseSliderView slider) {
                    //show image
                    show_image(slider.getBundle().getString("image_path")
                            , slider.getBundle().getString("name")
                            , slider.getBundle().getString("priority"));

                }
            });
            sliderLayout.addSlider(textSliderView);
            sliderLayout.setPresetTransformer(SliderLayout.Transformer.Accordion);
            sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
            sliderLayout.setCustomAnimation(new DescriptionAnimation());
            sliderLayout.setDuration(1000 * 60 * 100);
            sliderLayout.setCurrentPosition(0);
        }

    }

    private void show_image(String image_path, String name, String priority) {
        Activity activity = (Activity) context;
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_zoom_image, this, false);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        TextView nameTxt = view.findViewById(R.id.name);
        TextView priorityTxt = view.findViewById(R.id.priority);
        if (!priority.equals("")) {
            priorityTxt.setText("Prioridad : " + priority);
            // priorityTxt.setVisibility(GONE);
        } else {
            priorityTxt.setVisibility(GONE);
        }
        nameTxt.setText(name);
        ImageView close = view.findViewById(R.id.close);
        GestureImageView content = view.findViewById(R.id.gesture_image);
        Bitmap bitmap = BitmapFactory.decodeFile(image_path);
        content.setImageBitmap(bitmap);
        close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

    }


    @Override
    public void clearData() {

    }

    @Override
    public void getElementProps() {

    }

    @Override
    public int getView() {
        return R.layout.layout_image_slider_view;
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
