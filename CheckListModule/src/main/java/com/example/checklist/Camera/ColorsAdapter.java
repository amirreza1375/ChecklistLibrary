package com.example.checklist.Camera;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.checklist.R;
import com.github.pavlospt.CircleView;


public class ColorsAdapter extends LinearLayout {

    private View view;

    private ViewGroup parent;
    private ColorItemListener listener;
    private int color_code;

    public ColorsAdapter(Context context, ViewGroup parent, ColorItemListener listener, int color_code) {
        super(context);
        this.parent = parent;
        this.listener = listener;
        this.color_code = color_code;
        init(context);
    }

    public ColorsAdapter(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ColorsAdapter(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public ColorsAdapter(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public View get_view(){
        return view;
    }

    private void init(Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.layout_color_item_library,parent,false);
        final CircleView item = view.findViewById(R.id.item);
        item.setBackgroundColor(color_code);
        item.setSubtitleColor(color_code);
        item.setTitleColor(color_code);
        item.setStrokeColor(color_code);
        item.setFillColor(color_code);

        item.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onColorPicked(color_code);
            }
        });
    }

    public interface ColorItemListener{
        void onColorPicked(int color_code);
    }
}
