package com.example.checklist.BarCode;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.checklist.BaseViewModel.BaseViewModel;
import com.example.checklist.BaseViewModel.ElemetActionListener;
import com.example.checklist.R;
import com.google.zxing.Result;

import org.json.JSONObject;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class BarCodeView extends BaseViewModel implements ZXingScannerView.ResultHandler, View.OnClickListener {

    private TextView resultTxt;
    private LinearLayout scannerViewHolder;
    private Button scanBtn;

    private ZXingScannerView scannerView;

    public BarCodeView(Context context, JSONObject element, ElemetActionListener callBack, JSONObject viewAnswer, boolean isEnabled, int pagePosition, int viewPosition) {
        super(context, element, callBack, viewAnswer, isEnabled, pagePosition, viewPosition);
    }

    @Override
    public JSONObject getValue() {
        return null;
    }

    @Override
    public LinearLayout InitilizeView(Context context) {

        scannerView = new ZXingScannerView(context);
        scannerView.setResultHandler(this);

        resultTxt = baseView.findViewById(R.id.resultTxt);
        scannerViewHolder = baseView.findViewById(R.id.scannerView);
        scanBtn = baseView.findViewById(R.id.scanBtn);

        scanBtn.setOnClickListener(this);

        scannerViewHolder.addView(scannerView);

        resultTxt.setVisibility(GONE);
        scannerViewHolder.setVisibility(GONE);

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
        return R.layout.layout_barcode_scanner_item;
    }

    @Override
    public void getAnswer(JSONObject answer) {

    }

    @Override
    public void handleResult(Result rawResult) {
        resultTxt.setText("Resultado : "+rawResult.getText().toLowerCase()+"\n Formato : "+rawResult.getBarcodeFormat());
        resultTxt.setVisibility(VISIBLE);
        scanBtn.setVisibility(VISIBLE);
        scannerViewHolder.setVisibility(GONE);
        scannerView.stopCamera();
    }

    @Override
    public void onClick(View view) {
        if (view == scanBtn){
            resultTxt.setVisibility(GONE);
            scanBtn.setVisibility(GONE);
            scannerViewHolder.setVisibility(VISIBLE);
            scannerView.startCamera();
        }
    }
}

