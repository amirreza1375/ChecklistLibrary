package com.example.checklist.Camera;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.checklist.CheckListGenerator.CheckListMaker;
import com.example.checklist.Config;
import com.example.checklist.PageGenerator.CheckListPager;
import com.example.checklist.PictureElement.PictureElementMaker;
import com.example.checklist.PictureElement.PicturePickerItemModel;
import com.example.checklist.PictureElement.PicturesRecyclerView;
import com.example.checklist.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.checklist.Camera.ActivityCamera.IMAGE_RESULT;
import static com.example.checklist.GlobalFuncs.convert_JSONArray_to_PictureModel;
import static com.example.checklist.GlobalFuncs.convert_PictureModel_to_JSONArrary;
import static com.example.checklist.GlobalFuncs.showToast;

public class ActivityPicture extends AppCompatActivity  implements View.OnClickListener
,PictureElementMaker.TakePictureItemClickListener {

    public static String data_str = "";
    private static final String TAG = "ActivityPicture";
    private RecyclerView recycler;
    private ArrayList<String> img_pathes;
    private ImageButton back;
    private TextView done;
    private int current_index = 0;
    private int position;
    private long checkListId;

    public static JSONArray answerPictures;
    //
    private ArrayList<PicturePickerItemModel> models;
    private PicturePickerItemModel picturePickerItemModel;

    private ArrayList<Integer> choosen_indexes;
    private boolean picAdded = false;

    private String catId;
    private HashMap<Integer, String> images;
    private ArrayList<Integer> image_types;
    private ArrayList<Integer> choosen_pics;
    private JSONObject data;
    private JSONArray pictures;
    private JSONArray pictures_data;
    private boolean status;
    private String element_id;
    private boolean isRequiredEach = false;
    private boolean isRequired = false;
    private Uri imageUri;

    private boolean hasAllPermissions = false;

    private GestureDetector gestureDetector;

    private boolean IsTimeOut = true;
    private LinearLayout parent;

    private PictureElementMaker elementMaker;

    private ArrayList<PicturePickerItemModel> pictureModels;

    private JSONArray picAnswers;
    private boolean hasPic = false;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null) {
            outState.clear();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activitypicture_library);

        ActivityCamera.sub_folder_path = String.valueOf(System.currentTimeMillis());


        back = findViewById(R.id.back);
        done = findViewById(R.id.done);
        parent = findViewById(R.id.parent);


        back.setOnClickListener(this);
        done.setOnClickListener(this);

        pictureModels = new ArrayList<>();
        img_pathes = new ArrayList<>();
        images = new HashMap<>();
        choosen_pics = new ArrayList<>();
        image_types = new ArrayList<>();
        pictures = new JSONArray();
        models = new ArrayList<>();
        pictures_data = new JSONArray();
        data = new JSONObject();
        choosen_indexes = new ArrayList<>();

        String element = "{}";
        Bundle bundlee = getIntent().getExtras();
        if (bundlee != null){
            element = bundlee.getString("element");
            position = bundlee.getInt("position");
            String answerPicsStr = bundlee.getString(CheckListMaker.SavedPicturesFlag);
            try {
                answerPictures = new JSONArray(answerPicsStr);
                if (answerPictures.length() > 0){
                    hasPic = true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                hasPic = false;
            }
        }

        setMandatoryStatus(element);

        if (hasPic){
            addPicAnswers(convert_JSONArray_to_PictureModel(answerPictures));
        }

        try {

            boolean FLAG_ENABLED = false;

            if (CheckListPager.pageStatus != CheckListMaker.pageStatus.PREVIEW){
                FLAG_ENABLED = true;
            }
            if (!FLAG_ENABLED){
                done.setVisibility(View.GONE);
            }

            elementMaker = new PictureElementMaker(this,new JSONObject(element)
            ,true,FLAG_ENABLED,getPicAnswers(),position);
            elementMaker.setMlistener(this);
            parent.addView(elementMaker);
            elementMaker.getModels();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        addPrePictures();
        elementMaker.updateRecycler();

        String model = Build.MANUFACTURER;
//        Toast.makeText(this, model, Toast.LENGTH_SHORT).show();


    }

    private void setMandatoryStatus(String elementStr) {

        try {
            JSONObject element = new JSONObject(elementStr);

            if (element.has("isRequiredEach")){
                if (element.getBoolean("isRequiredEach")){
                    isRequiredEach = true;
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void addPicAnswers(ArrayList<PicturePickerItemModel> models) {



    }

    private JSONArray getPicAnswers(){

        JSONArray finalJSON = new JSONArray();

        String answersStr = getSharedPreferences(Config.sharedPreferencName,MODE_PRIVATE)
        .getString(Config.pictures,"");

        try {
            JSONArray SPPics = new JSONArray(answersStr);
            for (int i = 0 ; i < SPPics.length() ; i++){
                finalJSON.put(SPPics.getJSONObject(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0 ; i < answerPictures.length() ; i++){
            try {
                JSONObject answer = answerPictures.getJSONObject(i);
                if (isPicExist(answer,finalJSON)){
                    finalJSON.put(answer);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return finalJSON;

    }

    @Override
    public void onPictureItemClicked(PicturesRecyclerView.ViewHolder holder, PicturePickerItemModel model) {
        Intent intent = null;
        try {
            this.picturePickerItemModel = model;
            intent = new Intent(ActivityPicture.this
            ,Class.forName(ActivityCamera.FLAG_CUSTOM_CAMERA));
            startActivityForResult(intent,101);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onModelsAdded(ArrayList<PicturePickerItemModel> modelss) {
        this.pictureModels.addAll(modelss);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101){
            if (resultCode == RESULT_OK){
                String path = data.getExtras().getString(IMAGE_RESULT);
                addImagePath(path);
            }
        }
    }

    private void addImagePath(String path) {
        boolean FLAG_ADDED = false;
        int index = -1;
        for (int i = 0 ; i < pictureModels.size() ; i++){
            if (pictureModels.get(i).getIndex() == picturePickerItemModel.getIndex()){
                pictureModels.get(i).setPath(path);
                pictureModels.get(i).setPosition(position);
                FLAG_ADDED = true;
                index = i;
                break;
            }
        }
        if (!FLAG_ADDED){
            picturePickerItemModel.setPath(path);
            pictureModels.add(picturePickerItemModel);
        }
        setImage(index == -1 ? picturePickerItemModel : pictureModels.get(index));
    }

    private void setImage(PicturePickerItemModel picturePickerItemModel) {
        elementMaker.updateRecycler();
    }

    @Override
    public void onClick(View v) {
        if (v == back){
            setResult(RESULT_CANCELED);
            finish();
        }
        if (v == done){
            if (takenPicturesCount() > 0) {
                putDataInSharedPrefrences(getPictures());
                finish();
            }else{
                showToast(ActivityPicture.this,getString(R.string.take_picture));
            }
        }
    }

    private void putDataInSharedPrefrences(JSONArray pictures) {
        JSONArray allPics = new JSONArray();
        //first we should get other pages pictures

        String prePicsStr = getSharedPreferences(Config.sharedPreferencName,MODE_PRIVATE)
                .getString(Config.pictures,"");

        if (prePicsStr.equals("")){
            prePicsStr = "[]";
        }

        try {
            JSONArray prePictures = new JSONArray(prePicsStr);
            for (int i = 0 ; i < prePictures.length() ; i++){
                allPics.put(prePictures.getJSONObject(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        try {
            for (int i = 0 ; i < pictures.length() ; i++){
               JSONObject object = pictures.getJSONObject(i);
               if(!isPicExist(object,allPics)) {
                   allPics.put(object);
               }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SharedPreferences.Editor editor = getSharedPreferences(Config.sharedPreferencName,MODE_PRIVATE)
                .edit();
        editor.putString(Config.pictures,allPics.toString()).apply();
    }

    public static JSONArray getPicsFromSharedPreferences(Context context){
        String picsStr = context.getSharedPreferences(Config.sharedPreferencName,MODE_PRIVATE)
                .getString(Config.pictures,"");
        try {
            return new JSONArray(picsStr);
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    private void addPrePictures(){
        JSONArray allAnswers = new JSONArray();
        String picsStr = getSharedPreferences(Config.sharedPreferencName,MODE_PRIVATE)
                .getString(Config.pictures,"[]");
        if (picsStr.equals("")){
            picsStr = "[]";
        }
        try {
            JSONArray pics = new JSONArray(picsStr);
            for (int i = 0 ; i < pics.length() ; i++){
                allAnswers.put(pics.getJSONObject(i));
            }
            for (int i = 0 ; i < answerPictures.length() ; i++){
                JSONObject pic = answerPictures.getJSONObject(i);
                if (!isPicExist(pic,allAnswers)){
                   allAnswers.put(answerPictures.getJSONObject(i));
                }
            }

            //now add all pics to models

            for (int i = 0 ; i < allAnswers.length() ; i++) {
                JSONObject pic = allAnswers.getJSONObject(i);
                for (int j = 0; j < pictureModels.size(); j++) {

                    if (pictureModels.get(j).getId()
                            .equals(pic.getString(PicturePickerItemModel.conf_id))
                            && pictureModels.get(j).getIndex()
                             == (pic.getInt(PicturePickerItemModel.conf_index))
                            ){
                        putPicInModel(pictureModels.get(j),pic);
                    }

                }
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void putPicInModel(PicturePickerItemModel picturePickerItemModel, JSONObject pic) {
        try {
            picturePickerItemModel.setPath(pic.getString(PicturePickerItemModel.conf_path));
            picturePickerItemModel.setHasPic(true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean isPicExist(JSONObject pic, JSONArray allAnswers) {
        for (int i = 0 ; i < allAnswers.length() ; i++){
            try {
                JSONObject object = allAnswers.getJSONObject(i);
                if (pic.getString(PicturePickerItemModel.conf_id)
                        .equals(object.getString(PicturePickerItemModel.conf_id))
                        && pic.getInt(PicturePickerItemModel.conf_index)
                         == (object.getInt(PicturePickerItemModel.conf_index))){
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return false;

    }

    private int takenPicturesCount(){
        if (isRequiredEach) {
            ArrayList<PicturePickerItemModel> tempModels = pictureModels;
            for (int i = 0; i < tempModels.size(); i++) {
                if (tempModels.get(i).getPath().equals("")) {
                    tempModels.remove(i);
                    i--;
                }
            }
            return tempModels.size();
        }
        return 1;
    }

    private JSONArray getPictures() {
        ArrayList<PicturePickerItemModel> tempModels = pictureModels;
        for (int i = 0 ; i < tempModels.size() ; i++){
            if (tempModels.get(i).getPath().equals("")){
                tempModels.remove(i);
                i--;
            }
        }
        JSONArray array = convert_PictureModel_to_JSONArrary(tempModels);
        return array;
    }
}
