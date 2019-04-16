package com.example.commentario.FinishCheckList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.commentario.R;

public class CheckListFinishPage extends LinearLayout implements View.OnClickListener {

    Button finishBtn;
    LinearLayout saveBtn;
    LinearLayout backBtn;
    private FinishedPageActionListener listener;

    public CheckListFinishPage(Context context,FinishedPageActionListener listener) {
        super(context);
        this.listener = listener;
        init(context);
    }

    public CheckListFinishPage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckListFinishPage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CheckListFinishPage(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context){
        try {

            View view = LayoutInflater.from(context).inflate(R.layout.layout_checklist_finished_library, this, false);
            finishBtn = view.findViewById(R.id.finish);
            saveBtn = view.findViewById(R.id.btn_save);
            backBtn = view.findViewById(R.id.back_checklist);

            addView(view);

            finishBtn.setOnClickListener(this);
            saveBtn.setOnClickListener(this);
            backBtn.setOnClickListener(this);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        if (v == finishBtn){
            listener.onFinishClicked();
        }
        if (v == backBtn){
            listener.onBackCheckListClicked();
        }
        if (v == saveBtn){
            listener.onSaveAsDraftClicked();
        }
    }

    public interface FinishedPageActionListener{
        void onFinishClicked();
        void onBackCheckListClicked();
        void onSaveAsDraftClicked();
    }

}
