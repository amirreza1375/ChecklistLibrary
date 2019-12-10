package com.example.checklist.Camera;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.checklist.R;
import com.github.pavlospt.CircleView;

import java.io.File;
import java.io.FileOutputStream;


import ja.burhanrashid52.photoeditor.OnPhotoEditorListener;
import ja.burhanrashid52.photoeditor.OnSaveBitmap;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.ViewType;

public class ActivityEdit extends AppCompatActivity implements View.OnClickListener
,ColorsAdapter.ColorItemListener
{
    private static final String TAG = "ActivityEdit";
View rootView;
    private Button capture;
    private ImageView img;
    private PhotoEditorView mPhotoEditorView;
    private PhotoEditor mPhotoEditor;
    private ImageView brush;
    private ImageView text;
    private ImageView undo;
    private ImageView cancel_text;
    private ImageView redo;
    private ImageView align;
    private ImageView eraser;
    private EditText text_input;
    private ImageView set;
    private LinearLayout options;
    private LinearLayout colors;
    private RelativeLayout parent;

    private int color_codes[] ;
    private int text_align = 2;

    private String image_path;

    private Bitmap saved_bitmap = null;
    private ImageView save;

    private boolean brush_mode = false ;
    private int Color_int = Color.BLACK;


    private CircleView picked ;


    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_library);

        brush = findViewById(R.id.brush);
        text_input = findViewById(R.id.text_input);
        set = findViewById(R.id.set);
        options = findViewById(R.id.options);
        text = findViewById(R.id.text);
        undo = findViewById(R.id.undo);
        redo = findViewById(R.id.redo);
        parent = findViewById(R.id.parent);
        align = findViewById(R.id.align);
        cancel_text = findViewById(R.id.cancel_text);
        colors = findViewById(R.id.colors);
        eraser = findViewById(R.id.eraser);
        save = findViewById(R.id.save);
        mPhotoEditorView = findViewById(R.id.photoEditorView);

         picked = findViewById(R.id.picked);

        color_codes = getResources().getIntArray(R.array.colors);

        text.setOnClickListener(this);
        brush.setOnClickListener(this);
        set.setOnClickListener(this);
        undo.setOnClickListener(this);
        save.setOnClickListener(this);
        redo.setOnClickListener(this);
        eraser.setOnClickListener(this);
        align.setOnClickListener(this);
        cancel_text.setOnClickListener(this);

        text_input.setTextAlignment(text_align);

        picked.setBackgroundColor(Color_int);

        Bundle bundle = getIntent().getExtras();
        Bitmap bitmap = null;
        if (bundle != null){
            String path = bundle.getString("path");
            Log.i(TAG, "onCreate: "+path);
            image_path = path;
             bitmap = BitmapFactory.decodeFile(path);
        }

        mPhotoEditorView.getSource().setImageBitmap(bitmap);
        mPhotoEditorView.bringToFront();
        mPhotoEditor = new PhotoEditor.Builder(this, mPhotoEditorView)
                .setPinchTextScalable(true)
                .build();

        add_colors();

    }

    private void add_colors() {
        for (int color_code : color_codes) {
            ColorsAdapter adapter = new ColorsAdapter(this, parent, this, color_code);
            colors.addView(adapter.get_view());

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @SuppressLint("WrongConstant")
    @Override
    public void onClick(final View v) {
        if (v == brush){
            mPhotoEditor.setBrushDrawingMode(false);
            if (brush_mode){
                brush.setBackgroundDrawable(getResources().getDrawable(R.drawable.oprions_back));
                mPhotoEditor.setBrushDrawingMode(false);
                brush_mode = false;
            }else {
                brush.setBackgroundDrawable(getResources().getDrawable(R.drawable.on_background));
                brush_mode = true;
                mPhotoEditor.setBrushDrawingMode(true);
                mPhotoEditor.setBrushColor(Color_int);
            }
        }
        if (v == text){
            brush.setBackgroundDrawable(getResources().getDrawable(R.drawable.oprions_back));
            mPhotoEditor.setBrushDrawingMode(false);
            options.setVisibility(View.VISIBLE);
            cancel_text.setVisibility(View.VISIBLE);
            options.bringToFront();
            cancel_text.bringToFront();
            text_input.setFocusableInTouchMode(true);
            text_input.requestFocus();

        }
        if (set == v){
            if (!TextUtils.isEmpty(text_input.getText().toString().trim())){
                mPhotoEditor.addText(text_input.getText().toString(),Color_int);
                text_input.setText("");
                options.setVisibility(View.INVISIBLE);
                cancel_text.setVisibility(View.INVISIBLE);

                /**
                 *
                 */
                mPhotoEditor.setOnPhotoEditorListener(new OnPhotoEditorListener() {
                    @Override
                    public void onEditTextChangeListener(View rootView, String text, int colorCode) {
                        Log.i(TAG, "onEditTextChangeListener: "+rootView);
                        rootView.setVisibility(View.GONE);
                        //brush.setBackground(null);
                        options.setVisibility(View.VISIBLE);
                        options.bringToFront();
                        text_input.setFocusableInTouchMode(true);
                        text_input.setText(text);
                        text_input.setTextColor(colorCode);
                        text_input.requestFocus();
                    }

                    @Override
                    public void onAddViewListener(ViewType viewType, int numberOfAddedViews) {
                        Log.i(TAG, "onAddViewListener: "+viewType);

                    }

                    @Override
                    public void onRemoveViewListener(int numberOfAddedViews) {
                        Log.i(TAG, "onRemoveViewListener: "+numberOfAddedViews);
                    }

                    @Override
                    public void onRemoveViewListener(ViewType viewType, int numberOfAddedViews) {
                        Log.i(TAG, "onRemoveViewListener: ");
                    }

                    @Override
                    public void onStartViewChangeListener(ViewType viewType) {
                        Log.i(TAG, "onStartViewChangeListener: ");
                    }

                    @Override
                    public void onStopViewChangeListener(ViewType viewType) {
                        Log.i(TAG, "onStopViewChangeListener: ");
                    }
                });

            }


        }
        if (v == undo){
            mPhotoEditor.undo();

        }
        if (v == redo){
            mPhotoEditor.redo();

        }
        if (v == eraser){
            mPhotoEditor.brushEraser();
        }
        if (cancel_text == v){
            options.setVisibility(View.INVISIBLE);
            cancel_text.setVisibility(View.INVISIBLE);
        }
        if (v == align){
            if (text_align == 4){
                text_align = 2;
            }else {
                text_align ++;
            }
            text_input.setTextAlignment(text_align);
            switch (text_align){
                case 2:
                    align.setImageResource(R.drawable.right);
                    break;
                case 3:
                    align.setImageResource(R.drawable.left);
                    break;
                case 4:
                    align.setImageResource(R.drawable.center);
                    break;
            }
        }


        if (save == v){
            mPhotoEditor.saveAsBitmap(new OnSaveBitmap() {
                @Override
                public void onBitmapReady(Bitmap saveBitmap) {
                    saved_bitmap = saveBitmap;
                    String path = ovverride_edited_image(saved_bitmap);
                    Log.i(TAG, "onBitmapReady: "+path);
                    Intent data = new Intent();
                    data.putExtra("path", path);
                    setResult(RESULT_OK, data);
                    finish();

                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(ActivityEdit.this, "Couldn't save imag try again", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }


    private String ovverride_edited_image(Bitmap saved_bitmap) {
        File file = createImageFile(saved_bitmap);

        return String.valueOf(file);
    }
    private File createImageFile(Bitmap bm) {

        File file = new File(image_path);

        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return file;
    }

    @Override
    public void onColorPicked(int color_code) {
        Color_int = color_code;
        picked.setBackgroundColor(color_code);
        picked.setSubtitleColor(color_code);
        picked.setTitleColor(color_code);
        picked.setStrokeColor(color_code);
        picked.setFillColor(color_code);
        text_input.setTextColor(color_code);
        mPhotoEditor.setBrushColor(color_code);
    }


}
