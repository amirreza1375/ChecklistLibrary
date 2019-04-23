package com.example.checklist.SignatureView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;

import static com.example.checklist.GlobalFuncs.checkStoragePermission;
import static com.example.checklist.GlobalFuncs.conf_id;
import static com.example.checklist.GlobalFuncs.conf_value;
import static com.example.checklist.GlobalFuncs.createTitle;
import static com.example.checklist.GlobalFuncs.dpToPx;
import static com.example.checklist.GlobalFuncs.setOrgProps;
import static com.example.checklist.GlobalFuncs.showToast;

public class SignatureElement extends LinearLayout implements View.OnClickListener
            ,SignatureGenerator.SignatureListener {

    private static final String TAG = "SignetureElement";

    public static String SignatureFolder = "Signatures";

    private ImageView sigenatureViewver;
    private AlertDialog alertDialog;

    private String elementId;
    private boolean isRequired;
    private JSONObject element;
    private Context context;
    private String folderPath;
    private JSONObject answer;
    private boolean enable;
    private String signaturePath;


    public SignatureElement(Context context, JSONObject element, boolean isRequired
    , String folderPath,JSONObject answer,boolean enable) {
        super(context);
        this.element = element;
        this.isRequired = isRequired;
        this.context = context;
        this.folderPath = folderPath;
        this.answer = answer;
        this.enable = enable;

        init(context);
    }

    public SignatureElement(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SignatureElement(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SignatureElement(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void init(Context context){

        getVariablesFromElement(element);

        setOrgProps(context,this);

        CardView cardView = new CardView(context);
        LayoutParams cardParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        cardParams.setMargins(dpToPx(2,context),dpToPx(4,context),dpToPx(2,context),dpToPx(4,context));
        cardView.setLayoutParams(cardParams);
        cardView.setRadius(dpToPx(4,context));
        cardView.setElevation(dpToPx(4,context));

        addView(cardView);

        LinearLayout parent = new LinearLayout(context);
        parent.setOrientation(VERTICAL);
        LayoutParams parentParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        parent.setLayoutParams(parentParams);
        cardView.addView(parent);

        TextView titleTxt = createTitle(context,isRequired,element);

        parent.addView(titleTxt);

        LayoutParams guidParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        guidParams.setMargins(dpToPx(8,context),dpToPx(2,context),dpToPx(8,context),dpToPx(2,context));


        TextView guideTxt = new TextView(context);
        guideTxt.setLayoutParams(guidParams);
        guideTxt.setTextColor(Color.RED);
        guideTxt.setText("Presione para firmar");
        parent.addView(guideTxt);

        sigenatureViewver = new ImageView(context);

        if (answer != null){
            if (answer.has(conf_value)){
                try {
                    Bitmap bitmap = BitmapFactory.decodeFile(answer.getString(conf_value));
                    sigenatureViewver.setImageBitmap(bitmap);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
        ,dpToPx(200,context));

        sigenatureViewver.setLayoutParams(params);

        parent.addView(sigenatureViewver);

        this.setEnabled(enable);

        this.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        if (v == this){
            showSignatureAlert();
        }

    }

    public String getValue(){
        return signaturePath;
    }

    private void showSignatureAlert() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(new SignatureGenerator(context,this));

        alertDialog = builder.create();

        alertDialog.show();

    }

    @Override
    public void onSignatureGenerated(Bitmap signatureBitMap) {
        if (alertDialog != null){
            alertDialog.dismiss();
        }
        sigenatureViewver.setImageBitmap(signatureBitMap);
        if (checkStoragePermission(context)) {
            this.signaturePath = saveImage(signatureBitMap);
        }else{
            showToast((Activity) context,"You must allow storage permission in settings");
        }
    }

    public String saveImage(Bitmap bitmap){
        String targetFolder = folderPath;
        String nameWithType = String.valueOf(System.currentTimeMillis()) + ".jpg";
        File file = new File(targetFolder,nameWithType);
        Log.i(TAG, "saveImage: "+file.getAbsolutePath());
        if (!file.exists()) {
            try {
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();

                return file.getAbsolutePath();

            } catch (Exception e) {
                Log.i(TAG, "saveImage: "+e);
                e.printStackTrace();
                return "image save error";
            }
        }
        Log.i(TAG, "saveImage: "+file.getAbsolutePath());
        return file.getAbsolutePath();
    }
    private  File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(folderPath);

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        // Create a media file name
        String timeStamp = String.valueOf(System.currentTimeMillis());
        File mediaFile;
        String mImageName="SIGN"+ timeStamp +".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

    private void getVariablesFromElement(JSONObject element) {
        try {
            elementId = element.has(conf_id) ? element.getString(conf_id) : "";

//            isRequired = element.has(conf_isRequired) ? element.getBoolean(conf_isRequired) : false;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getElementId() {
        return elementId;
    }
}
