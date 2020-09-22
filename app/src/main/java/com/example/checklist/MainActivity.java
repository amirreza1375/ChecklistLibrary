package com.example.checklist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.example.checklist.BarCode.BarCodeView;
import com.example.checklist.BaseViewModel.ElemetActionListener;
import com.example.checklist.R;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements ElemetActionListener {
    private LinearLayout parent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        parent = findViewById(R.id.parent);
        parent.addView(new BarCodeView(this,new JSONObject(),this,new JSONObject(),true,0,0));
    }

    @Override
    public void onAction(String name, String id, String data, int pagePosition) {

    }

    @Override
    public void onConditionaryDataChanged(String id, String value, boolean isChecked, String type) {

    }

    @Override
    public void isHiddenView() {

    }
}
