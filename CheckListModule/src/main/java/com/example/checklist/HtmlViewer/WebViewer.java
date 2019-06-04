package com.example.checklist.HtmlViewer;

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.checklist.R;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.checklist.GlobalFuncs.conf_htmlValue;
import static com.example.checklist.GlobalFuncs.log;
import static com.example.checklist.GlobalFuncs.setOrgProps;

public class WebViewer extends LinearLayout {

    private JSONObject element;
    private TextView webView;

    public WebViewer(Context context, JSONObject element) {
        super(context);
        this.element = element;
        init(context);
    }

    public WebViewer(Context context,AttributeSet attrs) {
        super(context, attrs);
    }

    public WebViewer(Context context,AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private void init(Context context){

        setOrgProps(context,this);

        View view = LayoutInflater.from(context).inflate(R.layout.layout_html_item,this,false);

        String htmlStr = getHtmlValue(element);

        if (!htmlStr.equals("")) {

        this.addView(view);

        webView = view.findViewById(R.id.webText);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                webView.setText(Html.fromHtml(htmlStr, Html.FROM_HTML_MODE_COMPACT));
            } else {
                webView.setText(Html.fromHtml(htmlStr));
            }
        }

    }

    private String getHtmlValue(JSONObject element) {
        try {
            String htmlStr = element.getString(conf_htmlValue);
            return htmlStr;
        } catch (JSONException e) {
            e.printStackTrace();
            log(e.getMessage());
            return "";
        }

    }


}
