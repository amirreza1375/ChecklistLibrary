package com.example.commentario.ImageSliderView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.example.commentario.R;
import com.example.commentario.imageview.GestureImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.example.commentario.GlobalFuncs.createTitle;
import static com.example.commentario.GlobalFuncs.dpToPx;
import static com.example.commentario.GlobalFuncs.setOrgProps;

public class ImageSliderViewer extends LinearLayout {


    //region used variables
    private ArrayList<File> imageFiles;
    private ArrayList<String> names;
    private ArrayList<String> priorities;
    //endregion

    //region variables
    private Context context;
    private JSONObject element;
    private boolean isRequired;
    //endregion

    //region json keys
    private String conf_title = "title";
    //endregion


    public ImageSliderViewer(Context context, JSONObject element
    , ArrayList<File> imageFiles,ArrayList<String> priorities
    ,ArrayList<String> names) {
        super(context);
        this.context = context;
        this.element = element;
        this.imageFiles = imageFiles;
        this.names = names;
        this.priorities = priorities;
        init(context);


    }

    public ImageSliderViewer(Context context,  AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageSliderViewer(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ImageSliderViewer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context){

        if (imageFiles.size() == 0){
            removeView();
        }

        //region org props
        setOrgProps(context,this);
        //endregion

        //region title props
        TextView titleTxt = createTitle(context,false,element);
        //endregion

        LayoutParams sliderParams = new LayoutParams(LayoutParams.MATCH_PARENT
        ,dpToPx(200,context));
        SliderLayout sliderLayout = setupSlider(element,context);
        sliderLayout.setLayoutParams(sliderParams);
        //region add view
        addView(titleTxt);
        if (checkStoragePermission()){
            addView(sliderLayout);
        }else {
            TextView err = new TextView(context);
            err.setText("Storage Permision needed");
            err.setTextColor(Color.RED);
            addView(err);
            err.setTextSize(18);
        }

        setOrientation(VERTICAL);
        //endregion

    }

    public void removeView(){
        this.setVisibility(GONE);
    }

    public boolean checkStoragePermission() {
        return ActivityCompat.checkSelfPermission
                (context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private SliderLayout setupSlider(JSONObject element, Context context) {

        SliderLayout sliderLayout = new SliderLayout(context);
        for (int i = 0 ; i < imageFiles.size() ; i++){

            TextSliderView textSliderView = new TextSliderView(context);
            textSliderView.image(imageFiles.get(i)).bundle(new Bundle())
                    .setScaleType(BaseSliderView.ScaleType.FitCenterCrop)
                    .getBundle().putString("image_path", String.valueOf(imageFiles.get(i)));
            textSliderView.getBundle().putString("name",names.get(i));
            textSliderView.getBundle().putString("priority",priorities.get(i));
            textSliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                @Override
                public void onSliderClick(BaseSliderView slider) {
                    //show image
                    show_image(slider.getBundle().getString("image_path")
                            ,slider.getBundle().getString("name")
                            ,slider.getBundle().getString("priority"));

                }
            });
            sliderLayout.addSlider(textSliderView);
            sliderLayout.setPresetTransformer(SliderLayout.Transformer.Accordion);
            sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
            sliderLayout.setCustomAnimation(new DescriptionAnimation());
            sliderLayout.setDuration(1000 * 60 * 100);
            sliderLayout.setCurrentPosition(0);
        }

        return sliderLayout;
    }

    private String  getTitleFromElement(JSONObject element) {

        try {
            return element.getString(conf_title);
        } catch (JSONException e) {
            e.printStackTrace();
            return "no title";
        }
    }

    private void show_image(String image_path , String name , String priority) {
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
        }else {
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

}
