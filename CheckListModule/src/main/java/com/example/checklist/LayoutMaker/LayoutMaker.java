package com.example.checklist.LayoutMaker;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.checklist.BaseViewModel.BaseView;
import com.example.checklist.R;
import com.example.checklist.imageview.GestureImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.checklist.GlobalFuncs.conf_name;

public class LayoutMaker extends BaseView {

    private String correctAnswer = "correctAnswer";

//    private String name;
//    private String image_path;
//    private JSONArray texts;
//    private JSONArray replaces;

    private ArrayList<LayoutModel> layoutModels;
    private JSONObject element;
    private String currentShop;
    private Context context;

    public LayoutMaker(Context context, ArrayList<LayoutModel> layoutModels, JSONObject element,String currentShop) {
        super(context);
        this.context = context;
        this.layoutModels = layoutModels;
        this.element = element;
        this.currentShop = currentShop;
        init(context);
    }

    public LayoutMaker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LayoutMaker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(Context context){
        try {
            visibleSi = element.getString("visibleIf");
            isVisibleSi = true;
            name = element.getString(conf_name);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        getVariablesFromElement(element);

        for (int  i = 0 ; i < layoutModels.size() ; i ++){

            LayoutModel model = layoutModels.get(i);
            String shopStr = model.getShop();
            if (shopStr != null) {
                if (shopStr.equals("")) {
                    try {
                        addLayout(model.getOrder_name()
                                , model.getImage_path()
                                , new JSONArray(model.getPositions())
                                , new JSONArray(model.getReplacements()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                String shopsArray[] = shopStr.split(",");
                for (int j = 0; j < shopsArray.length; j++) {
                    String layoutShop = shopsArray[j];
                    if (layoutShop.equals(currentShop)) {
                        try {
                            addLayout(model.getOrder_name()
                                    , model.getImage_path()
                                    , new JSONArray(model.getPositions())
                                    , new JSONArray(model.getReplacements()));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    }

                }
            }else{
                try {
                    addLayout(model.getOrder_name()
                            , model.getImage_path()
                            , new JSONArray(model.getPositions())
                            , new JSONArray(model.getReplacements()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }



    }

    private void getVariablesFromElement(JSONObject element) {

    }

    private void addLayout(final String name, final String image_path, JSONArray texts , JSONArray replaces){
        View parent = LayoutInflater.from(context)
                .inflate(R.layout.layout_item_imagelayout,this,false);

        ImageView image = parent.findViewById(R.id.image);
        image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                show_image(image_path,name);
            }
        });
        TextView titletxt = parent.findViewById(R.id.title);
        titletxt.setText(name);
        LinearLayout text_linear = parent.findViewById(R.id.text_linear);
        image.setImageBitmap(BitmapFactory.decodeFile(image_path));
        try {
            for (int i = 0 ; i < texts.length() ; i++) {
                JSONObject text = texts.getJSONObject(i);


                LinearLayout linearLayout = new LinearLayout(context);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                TextView textView1 = new TextView(context);
                textView1.setText((i + 1)+". "+text.getString(LayoutModel.conf_positions_name));
                linearLayout.addView(textView1);

                if (replaces.length() > i){
                    if (!replaces.getString(i).equals("")){
                        TextView textView2 = new TextView(context);
                        textView2.setText(" ("+replaces.getJSONObject(i).getString(LayoutModel.conf_positions_name)+")");
                        textView2.setTextColor(context.getResources().getColor(R.color.bgRowBackground));
                        linearLayout.addView(textView2);
                    }
                }
                text_linear.addView(linearLayout);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.addView(parent);
    }

    private void show_image(String image_path , String name) {
        try {
            Activity activity = (Activity) context;
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setCancelable(false);
            LayoutInflater inflater = activity.getLayoutInflater();
            View view = inflater.inflate(R.layout.layout_gesture_inage_view, this, false);
            builder.setView(view);
            final AlertDialog alertDialog = builder.create();
            alertDialog.show();
            TextView nameTxt = view.findViewById(R.id.name);
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
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
