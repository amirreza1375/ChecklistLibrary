package com.example.checklist.SeekBar;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.checklist.BaseViewModel.BaseViewModel;
import com.example.checklist.BaseViewModel.ElemetActionListener;
import com.example.checklist.GlobalFuncs;
import com.example.checklist.R;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.checklist.GlobalFuncs.conf_rangeMax;
import static com.example.checklist.GlobalFuncs.conf_rangeMin;
import static com.example.checklist.GlobalFuncs.conf_value;

public class SeekBarView extends BaseViewModel implements SeekBar.OnSeekBarChangeListener {

    private SeekBar seekBar;
    private TextView counterTxt, minTxt, maxTxt;
    private int rangeMax, rangeMin;
    private int choosenProgress = -1;

    public SeekBarView(Context context, JSONObject element, ElemetActionListener callBack, JSONObject viewAnswer, boolean isEnabled,int elementPosition,int viewPosition) {
        super(context, element, callBack, viewAnswer, isEnabled,elementPosition,viewPosition);
    }

    public SeekBarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SeekBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setProgress(int progress) {
        seekBar.setProgress(progress);
        counterTxt.setText(progress + "");
    }

    @Override
    public JSONObject getValue() {
        JSONObject answer = getGeneralValues();

        try {
            answer.put(GlobalFuncs.conf_value, seekBar.getProgress());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return answer;
    }

    @Override
    public LinearLayout InitilizeView(Context context) {
        seekBar = baseView.findViewById(R.id.seekBar);
        counterTxt = baseView.findViewById(R.id.counterTxt);
        minTxt = baseView.findViewById(R.id.minTxt);
        maxTxt = baseView.findViewById(R.id.maxTxt);

        seekBar.setOnSeekBarChangeListener(this);

        setProgress(rangeMin);
        minTxt.setText(rangeMin + "");
        maxTxt.setText(rangeMax + "");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            seekBar.setMin(rangeMin);
        }
        seekBar.setMax(rangeMax);

        if (choosenProgress != -1)
            seekBar.setProgress(choosenProgress);


        return this;
    }

    @Override
    public void clearData() {
        setProgress(rangeMin);
    }

    @Override
    public void getElementProps() {
        try {
            rangeMax = element.has(conf_rangeMax) ? element.getInt(conf_rangeMax) : 100;
            rangeMin = element.has(conf_rangeMin) ? element.getInt(conf_rangeMin) : 0;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getView() {
        return R.layout.layout_seekbar_view;
    }

    @Override
    public void getAnswer(JSONObject answer) {
        try {
            choosenProgress = answer.has(conf_value) ? answer.getInt(conf_value) : 0;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        viewAnswered();
        if(progress >= rangeMin && progress <= rangeMax)
            setProgress(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
