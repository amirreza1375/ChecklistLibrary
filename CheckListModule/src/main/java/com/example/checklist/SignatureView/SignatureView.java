package com.example.checklist.SignatureView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.checklist.BaseViewModel.BaseViewModel;
import com.example.checklist.BaseViewModel.ElemetActionListener;
import com.example.checklist.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;

import static com.example.checklist.GlobalFuncs.checkStoragePermission;
import static com.example.checklist.GlobalFuncs.conf_value;
import static com.example.checklist.GlobalFuncs.log;
import static com.example.checklist.GlobalFuncs.showToast;

public class SignatureView extends BaseViewModel implements SignatureGenerator.SignatureListener {

    private static final String TAG = "SignatureView";

    private String folderPath;

    private String signaturePath;
    private ImageView sigenatureViewver;
    private AlertDialog alertDialog;

    public SignatureView(Context context, JSONObject element, ElemetActionListener callBack, String folderPath, JSONObject viewAnswer, boolean isEnabled,int elementPosition,int viewPosition) {
        super(context, element, callBack, viewAnswer, isEnabled,elementPosition,viewPosition);
        this.folderPath = folderPath;
    }

    public SignatureView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SignatureView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public JSONObject getValue() {
        JSONObject answer = getGeneralValues();
        try {
            answer.put(conf_value, signaturePath);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return answer;
    }

    @Override
    public LinearLayout InitilizeView(Context context) {

        sigenatureViewver = baseView.findViewById(R.id.signatureImg);

        sigenatureViewver.setEnabled(elementEnabled);

        if (signaturePath != null){
            if (!signaturePath.equals("")){
                Bitmap bitmap = BitmapFactory.decodeFile(signaturePath);
                sigenatureViewver.setImageBitmap(bitmap);
                viewAnswered();
            }
        }
        sigenatureViewver.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showSignatureAlert();
            }
        });

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
        return R.layout.layout_signature_view;
    }

    @Override
    public void getAnswer(JSONObject answer) {
        try {
            signaturePath = answer.getString(conf_value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showSignatureAlert() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(new SignatureGenerator(context, this, callBack));

        alertDialog = builder.create();

        alertDialog.show();

    }

    @Override
    public void onSignatureGenerated(Bitmap signatureBitMap) {
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        sigenatureViewver.setImageBitmap(signatureBitMap);
        if (checkStoragePermission(context)) {
            this.signaturePath = saveImage(signatureBitMap);
            viewAnswered();
        } else {
            showToast((Activity) context, "You must allow storage permission in settings");
        }
    }

    public String saveImage(Bitmap bitmap) {
        String targetFolder = folderPath;
        String nameWithType = String.valueOf(System.currentTimeMillis()) + ".jpg";
        File file = new File(targetFolder, nameWithType);
        Log.i(TAG, "saveImage: " + file.getAbsolutePath());
        if (!file.exists()) {
            try {
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();

                return file.getAbsolutePath();

            } catch (Exception e) {
                callBack.onAction("Signature save image", getElementId(), e.getMessage(), -1);
                Log.i(TAG, "saveImage: " + e);
                e.printStackTrace();
                log(e.getMessage());
                return "image save error";
            }
        } else {
            callBack.onAction("Signature file not exist", getElementId(), element.toString(), -1);
        }
        Log.i(TAG, "saveImage: " + file.getAbsolutePath());
        return file.getAbsolutePath();
    }
}
