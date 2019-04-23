package com.example.checklist.SignatureView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.checklist.R;
import com.simplify.ink.InkView;

import static com.example.checklist.GlobalFuncs.dpToPx;
import static com.example.checklist.GlobalFuncs.setOrgProps;

public class SignatureGenerator extends LinearLayout implements View.OnClickListener {

    private InkView inkView;
    private Button btnGetSignature;

    private Bitmap signatureBitMap;
    private SignatureListener signatureListener;

    public SignatureGenerator(Context context,SignatureListener signatureListener) {
        super(context);
        this.signatureListener = signatureListener;
        init(context);
    }

    public SignatureGenerator(Context context,  AttributeSet attrs) {
        super(context, attrs);
    }

    public SignatureGenerator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SignatureGenerator(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    private void init(Context context){

        setOrgProps(context,this);

        this.setPadding(dpToPx(8,context),dpToPx(8,context),dpToPx(8,context),dpToPx(8,context));

        inkView = new InkView(context);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                ,dpToPx(220,context));

        inkView.setLayoutParams(layoutParams);

        inkView.setColor(Color.BLACK);

        inkView.setMinStrokeWidth(0.5f);

        inkView.setMaxStrokeWidth(1.0f);

        addView(inkView);

        btnGetSignature = new Button(context);

        btnGetSignature.setOnClickListener(this);

        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                ,dpToPx(48,context));

        btnParams.setMargins(dpToPx(32,context),8,dpToPx(32,context),8);

        btnGetSignature.setLayoutParams(btnParams);

        btnGetSignature.setBackground(context.getDrawable(R.drawable.picture_btn_has_pic));

        btnGetSignature.setText("Done");

        addView(btnGetSignature);

    }



    @Override
    public void onClick(View v) {
        if (btnGetSignature == v){
            if (inkView != null){
                signatureBitMap = inkView.getBitmap(Color.WHITE);
                signatureListener.onSignatureGenerated(signatureBitMap);
            }
        }
    }

    public interface SignatureListener{
        void onSignatureGenerated(Bitmap signatureBitMap);
    }

}
