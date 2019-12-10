package com.example.checklist.TimeTracker;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.checklist.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static com.example.checklist.GlobalFuncs.conf_text;
import static com.example.checklist.GlobalFuncs.createTitle;
import static com.example.checklist.GlobalFuncs.dpToPx;
import static com.example.checklist.GlobalFuncs.getTitleFromElement;
import static com.example.checklist.GlobalFuncs.setOrgProps;

public class TimeTracker extends LinearLayout implements View.OnClickListener {

    //region original variables
    private boolean isRequired = true;
    private JSONObject element;
    private TimeTrackerListener listener;
    private JSONArray answers;
    private JSONArray elements;
    private Context context;
    private HashMap<TextView, String> elemensViewId;
    private HashMap<String, String> elemensIdValue;
    private ArrayList<TextView> textViews;

    //endregion

    //region variables
    public final static String ELEMENTS = "elements";
    public final static String ID = "id";
    public final static String TITLE = "title";
    //endregion


    public TimeTracker(Context context, JSONObject element, TimeTrackerListener listener, JSONArray answers) {
        super(context);
        this.context = context;
        this.element = element;
        this.listener = listener;
        this.answers = answers;
        textViews = new ArrayList<>();
        elemensViewId = new HashMap<>();
        elemensIdValue = new HashMap<>();
        init(context);
    }

    public TimeTracker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TimeTracker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {

        //get element variables
        getElementVariables(element);

        //region org porps
        setOrgProps(context, this);

        //region title
        TextView titleTxt = new TextView(context);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , LinearLayout.LayoutParams.WRAP_CONTENT);
        titleParams.setMargins(dpToPx(8, context), dpToPx(8, context)
                , dpToPx(8, context), dpToPx(0, context));
        titleTxt.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        titleTxt.setTextSize(26);
        titleTxt.setTextColor(Color.BLACK);
        titleTxt.setLayoutParams(titleParams);
        titleTxt.setText(getTitleFromElement(element, isRequired));
        addView(titleTxt);
        //endregion

        //create items
        generateItemsWithArray(elements);


    }

    private void generateItemsWithArray(JSONArray elements) {
        for (int i = 0; i < elements.length(); i++) {
            try {
                JSONObject object = elements.getJSONObject(i);
                View view = LayoutInflater.from(context).inflate(R.layout.layout_time_tracker_item, this, false);
                TextView titleTxt = view.findViewById(R.id.titleTxt);
                TextView timeTxt = view.findViewById(R.id.timeTxt);
                titleTxt.setText(object.getString(TITLE));
                textViews.add(timeTxt);
                elemensViewId.put(timeTxt, object.getString(ID));
                this.addView(view);
                timeTxt.setOnClickListener(this);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void getElementVariables(JSONObject element) {
        try {
            elements = element.has(ELEMENTS) ? element.getJSONArray(ELEMENTS) : new JSONArray();
            if (elements.length() == 0) {
                listener.onTimeTackerElementError();
                return;
            }

        } catch (JSONException e) {
            listener.onTimeTackerElementError();
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        try {
            TextView textView = (TextView) view;
            setCurrentTimeOnTextView(textView);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeItemValue(TextView textView) {
        String id = elemensViewId.get(textView);
        elemensIdValue.put(id, textView.getText().toString());
    }

    public JSONArray getElementValue() {
        try {
            JSONArray data = new JSONArray();

            for (int i = 0; i < textViews.size(); i++) {

                JSONObject object = new JSONObject();

                TextView temp = textViews.get(i);

                String id = elemensViewId.get(temp);

                String value = elemensIdValue.get(id);

                object.put("id", id);
                object.put("value", value);

                data.put(object);

            }
            return data;


        } catch (
                JSONException e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    private boolean textViewHasTime(TextView textView) {
        String time = textView.getText().toString();
        if (time.equals(""))
            return false;
        String times[] = time.split(":");
        if (times.length < 2)
            return false;
        try {
            int hour = Integer.parseInt(times[0]);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void setCurrentTimeOnTextView(TextView textView) {
        SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm:ss");//dd/MM/yyyy
        Date now = new Date();
        String currentTime = sdfDate.format(now);
        if (!textViewHasTime(textView)) {
            textView.setText(currentTime);
            changeItemValue(textView);
        } else {
            InititlizeTimeAlert(textView, currentTime, textView.getText().toString());
        }
    }

    private void InititlizeTimeAlert(final TextView textView, final String currentTime, String oldTime) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setTitle(context.getString(R.string.newTimequestion));
        builder.setMessage(" " + context.getString(R.string.lastTime) + oldTime + "\n " + context.getString(R.string.newTime) + currentTime);

        builder.setPositiveButton("Set new time", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                textView.setText(currentTime);
                changeItemValue(textView);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog newTimeAlert = builder.create();
        newTimeAlert.show();


    }


    public interface TimeTrackerListener {
        void onTimeTackerElementError();

    }

}
