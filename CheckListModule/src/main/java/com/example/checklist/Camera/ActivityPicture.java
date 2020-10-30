package com.example.checklist.Camera;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.checklist.BaseViewModel.ElemetActionListener;
import com.example.checklist.CheckListGenerator.PageView;
import com.example.checklist.Config;
import com.example.checklist.Database.IDBResultView;
import com.example.checklist.Database.ModuleLogEvent;
import com.example.checklist.PageGenerator.CheckListPager;
import com.example.checklist.PictureElement.PictureElementMaker;
import com.example.checklist.PictureElement.PicturePickerItemModel;
import com.example.checklist.PictureElement.PicturesRecyclerView;
import com.example.checklist.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import static com.example.checklist.Camera.ActivityCamera.IMAGE_RESULT;
import static com.example.checklist.GlobalFuncs.conf_id;
import static com.example.checklist.GlobalFuncs.conf_position;
import static com.example.checklist.GlobalFuncs.convert_ArrayList_to_JSONArray;
import static com.example.checklist.GlobalFuncs.convert_JSONArray_to_ArrayList;
import static com.example.checklist.GlobalFuncs.convert_JSONArray_to_PictureModel;
import static com.example.checklist.GlobalFuncs.convert_PictureModel_to_JSONArrary;
import static com.example.checklist.GlobalFuncs.getDate;
import static com.example.checklist.GlobalFuncs.getTime;
import static com.example.checklist.GlobalFuncs.log;
import static com.example.checklist.GlobalFuncs.showToast;

public class ActivityPicture extends AppCompatActivity implements View.OnClickListener
        , PictureElementMaker.TakePictureItemClickListener , ElemetActionListener {

    public static final int CAMERA_REQ_CODE = 110;


    public static String data_str = "";
    private static final String TAG = "ActivityPicture";
    private RecyclerView recycler;
    private ArrayList<String> img_pathes;
    private ImageView back;
    private Button done;
    private ProgressBar savingLoad;
    private int current_index = 0;
    private int position;
    private long checkListId;

    private String elementId;

    private boolean activityClosed;

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
    protected void onResume() {
        super.onResume();
        activityClosed = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (activityClosed) {
            removeCurrentPageImages(position, elementId);
            putDataInSharedPrefrences(getPictures());
            this.activityClosed = false;
//            setResult(-1);
            setResult(0);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onBackPressed() {
        this.activityClosed = false;
        setResult(-1);
        super.onBackPressed();
    }

    public void takePhoto() {
        Intent cameraIntent = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory()
                + "/Operator Track"));
        startActivityForResult(cameraIntent, CAMERA_REQ_CODE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_library_new);

        ActivityCamera.sub_folder_path = String.valueOf(System.currentTimeMillis());


        back = findViewById(R.id.back);
        done = findViewById(R.id.done);
        parent = findViewById(R.id.parent);
        savingLoad = findViewById(R.id.savingLoad);


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
        if (bundlee != null) {
            element = bundlee.getString("element");
            position = bundlee.getInt("position");

            String answerPicsStr = bundlee.getString(PageView.SavedPicturesFlag);
            try {
                answerPictures = new JSONArray(answerPicsStr);
                if (answerPictures.length() > 0) {
                    hasPic = true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                log(e.getMessage());
                hasPic = false;
            }
        }

        setMandatoryStatus(element);

        if (hasPic) {
            addPicAnswers(convert_JSONArray_to_PictureModel(answerPictures));
        }

        try {

            boolean FLAG_ENABLED = false;

            if (CheckListPager.pageStatus != PageView.pageStatus.PREVIEW) {
                FLAG_ENABLED = true;
            }
            if (!FLAG_ENABLED) {
                done.setVisibility(View.GONE);
            }

            elementMaker = new PictureElementMaker(this, new JSONObject(element)
                    , true, FLAG_ENABLED, getPicAnswers(), position, this,this);
            elementMaker.setMlistener(this);
            parent.addView(elementMaker);
            elementMaker.getModels();
            this.elementId = elementMaker.getElementId();

        } catch (JSONException e) {
            e.printStackTrace();
            log(e.getMessage());
        }

        addPrePictures();
        elementMaker.updateRecycler();

        String model = Build.MANUFACTURER;
//        Toast.makeText(this, model, Toast.LENGTH_SHORT).show();


    }

    private void removeCurrentPageImages(int position, String id) {
        String picStr = getSharedPreferences(Config.sharedPreferencName, Context.MODE_PRIVATE).getString(Config.pictures, "");
        if (picStr.equals("")) {
            picStr = "[]";
        }
        try {
            JSONArray pics = new JSONArray(picStr);
            ArrayList<JSONObject> picsArray = convert_JSONArray_to_ArrayList(pics);
            for (int i = 0; i < picsArray.size(); i++) {
                if (picsArray.get(i).getInt("position") == position
                        && picsArray.get(i).getString("id").equals(id)) {
                    picsArray.remove(i);
                    i--;
                }
            }
            SharedPreferences.Editor editor = getSharedPreferences(Config.sharedPreferencName, Context.MODE_PRIVATE).edit();
            editor.putString(Config.pictures, String.valueOf(convert_ArrayList_to_JSONArray(picsArray))).apply();
        } catch (JSONException e) {
            e.printStackTrace();
            log(e.getMessage());
        }
    }

    private void setMandatoryStatus(String elementStr) {

        try {
            JSONObject element = new JSONObject(elementStr);

            if (element.has("isRequiredEach")) {
                if (element.getBoolean("isRequiredEach")) {
                    isRequiredEach = true;
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
            log(e.getMessage());
        }

    }

    private void addPicAnswers(ArrayList<PicturePickerItemModel> models) {


    }

    private JSONArray getPicAnswers() {

        JSONArray finalJSON = new JSONArray();

        String answersStr = getSharedPreferences(Config.sharedPreferencName, MODE_PRIVATE)
                .getString(Config.pictures, "");

        try {
            JSONArray SPPics = new JSONArray(answersStr);
            for (int i = 0; i < SPPics.length(); i++) {
                if (isPictureForThisPage(position, SPPics.getJSONObject(i))) {
                    finalJSON.put(SPPics.getJSONObject(i));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            log(e.getMessage());
        }

        for (int i = 0; i < answerPictures.length(); i++) {
            try {
                JSONObject answer = answerPictures.getJSONObject(i);
                if (isPicExist(answer, finalJSON)) {
                    finalJSON.put(answer);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                log(e.getMessage());
            }

        }

        return finalJSON;

    }

    private boolean isPictureForThisPage(int position, JSONObject jsonObject) {
        try {
            int imagePosition = jsonObject.getInt(conf_position);
            if (imagePosition == position)
                return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onPictureItemClicked(PicturesRecyclerView.ViewHolder holder, PicturePickerItemModel model) {
        this.activityClosed = false;
        Intent intent = null;
//        try {
        this.picturePickerItemModel = model;
//            takePhoto();
        try {
            intent = new Intent(ActivityPicture.this
                    , Class.forName(ActivityCamera.FLAG_CUSTOM_CAMERA));
            startActivityForResult(intent, 101);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//            log(e.getMessage());
//        }

    }

    @Override
    public void onModelsAdded(ArrayList<PicturePickerItemModel> modelss) {
        this.pictureModels.addAll(modelss);
    }

    @Override
    public void onNoImageAppeared(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ActivityPicture.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                String path = data.getExtras().getString(IMAGE_RESULT);
                addImagePath(path);
            } else if (resultCode == 1) {
                setResult(0);
                finish();
            }
        }

        if (requestCode == CAMERA_REQ_CODE && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
//            imageView.setImageBitmap(photo);
//            knop.setVisibility(Button.VISIBLE);

            // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
            Uri tempUri = getImageUri(getApplicationContext(), photo);

            // CALL THIS METHOD TO GET THE ACTUAL PATH
            File finalFile = new File(getRealPathFromURI(tempUri));

            addImagePath(finalFile.toString());

        }
    }

    private void addImagePath(String path) {
        boolean FLAG_ADDED = false;
        int index = -1;
        for (int i = 0; i < pictureModels.size(); i++) {
            if (pictureModels.get(i).getIndex() == picturePickerItemModel.getIndex()) {
                pictureModels.get(i).setPath(path);
                pictureModels.get(i).setPosition(position);
                FLAG_ADDED = true;
                index = i;
                break;
            }
        }
        if (!FLAG_ADDED) {
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
        if (v == back) {
            this.activityClosed = false;
            setResult(-1);
            finish();
        }
        if (v == done) {
            if (takenPicturesCount() > 0) {
                savingLoad.setVisibility(View.VISIBLE);
                removeCurrentPageImages(position, elementId);
                putDataInSharedPrefrences(getPictures());
                this.activityClosed = false;
                setResult(-1);
                finish();
            } else {
                showToast(ActivityPicture.this, getString(R.string.take_picture));
            }
        }
    }

    private void putDataInSharedPrefrences(JSONArray pictures) {
        JSONArray allPics = new JSONArray();
        //first we should get other pages pictures

        String prePicsStr = getSharedPreferences(Config.sharedPreferencName, MODE_PRIVATE)
                .getString(Config.pictures, "");

        if (prePicsStr.equals("")) {
            prePicsStr = "[]";
        }

        try {


            ArrayList<JSONObject> prePicturesArray = convert_JSONArray_to_ArrayList(new JSONArray(prePicsStr));
            for (int i = 0; i < prePicturesArray.size(); i++) {
                try {
                    if (prePicturesArray.get(i).getString(conf_id)
                            .equals(elementId)) {
                        prePicturesArray.remove(i);
                        i--;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            JSONArray prePictures = convert_ArrayList_to_JSONArray(prePicturesArray);

            for (int i = 0; i < prePictures.length(); i++) {
                allPics.put(prePictures.getJSONObject(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            log(e.getMessage());
        }

        try {
            for (int i = 0; i < pictures.length(); i++) {
                JSONObject object = pictures.getJSONObject(i);
                if (!isPicExist(object, allPics)) {
                    allPics.put(object);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            log(e.getMessage());
        }

        SharedPreferences.Editor editor = getSharedPreferences(Config.sharedPreferencName, MODE_PRIVATE)
                .edit();
        editor.putString(Config.pictures, allPics.toString()).apply();
    }

    public static JSONArray getPicsFromSharedPreferences(Context context) {
        String picsStr = context.getSharedPreferences(Config.sharedPreferencName, MODE_PRIVATE)
                .getString(Config.pictures, "");
        try {
            return new JSONArray(picsStr);
        } catch (JSONException e) {
            e.printStackTrace();
            log(e.getMessage());
            return new JSONArray();
        }
    }

    private void addPrePictures() {
        JSONArray allAnswers = new JSONArray();
        String picsStr = getSharedPreferences(Config.sharedPreferencName, MODE_PRIVATE)
                .getString(Config.pictures, "[]");
        if (picsStr.equals("")) {
            picsStr = "[]";
        }
        try {
            JSONArray pics = new JSONArray(picsStr);
            for (int i = 0; i < pics.length(); i++) {
                if (isPictureForThisPage(position, pics.getJSONObject(i))) {
                    allAnswers.put(pics.getJSONObject(i));
                }
            }
            for (int i = 0; i < answerPictures.length(); i++) {
                JSONObject pic = answerPictures.getJSONObject(i);
                if (!isPicExist(pic, allAnswers)) {
                    allAnswers.put(answerPictures.getJSONObject(i));
                }
            }

            //now add all pics to models

            for (int i = 0; i < allAnswers.length(); i++) {
                JSONObject pic = allAnswers.getJSONObject(i);
                for (int j = 0; j < pictureModels.size(); j++) {

                    if (pictureModels.get(j).getId()
                            .equals(pic.getString(PicturePickerItemModel.conf_id))
                            && pictureModels.get(j).getIndex()
                            == (pic.getInt(PicturePickerItemModel.conf_index))) {
                        putPicInModel(pictureModels.get(j), pic);
                    }

                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
            log(e.getMessage());
        }
    }

    //put path and make it has picture
    private void putPicInModel(PicturePickerItemModel picturePickerItemModel, JSONObject pic) {
        try {
            picturePickerItemModel.setPath(pic.getString(PicturePickerItemModel.conf_path));
            picturePickerItemModel.setHasPic(true);
        } catch (JSONException e) {
            e.printStackTrace();
            log(e.getMessage());
        }
    }

    private boolean isPicExist(JSONObject pic, JSONArray allAnswers) {
        for (int i = 0; i < allAnswers.length(); i++) {
            try {
                JSONObject object = allAnswers.getJSONObject(i);
                if (pic.getString(PicturePickerItemModel.conf_id)
                        .equals(object.getString(PicturePickerItemModel.conf_id))
                        && pic.getInt(PicturePickerItemModel.conf_index)
                        == (object.getInt(PicturePickerItemModel.conf_index))) {
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                log(e.getMessage());
            }

        }
        return false;

    }

    private int takenPicturesCount() {

        if (isRequiredEach) {
            int countImages = 0;
            for (int i = 0; i < pictureModels.size(); i++) {
                if (!pictureModels.get(i).getPath().equals("")) {
                    countImages++;
                }
            }

            if (countImages == pictureModels.size()) {
                return 1;
            }

        } else {
            int countImages = 0;
            for (int i = 0; i < pictureModels.size(); i++) {
                if (!pictureModels.get(i).getPath().equals("")) {
                    countImages++;
                }
            }
            if (countImages > 0) {
                return 1;
            }
        }


//        if (isRequiredEach) {
//            ArrayList<PicturePickerItemModel> tempModels = pictureModels;
//            for (int i = 0; i < tempModels.size(); i++) {
//                if (tempModels.get(i).getPath().equals("")) {
//                    tempModels.remove(i);
//                    i--;
//                }
//            }
//            return tempModels.size();
//        }
//        return 1;
        return 0;
    }

    private JSONArray getPictures() {
        ArrayList<PicturePickerItemModel> tempModels = pictureModels;
        for (int i = 0; i < tempModels.size(); i++) {
            if (tempModels.get(i).getPath().equals("")) {
                tempModels.remove(i);
                i--;
            }
        }
        JSONArray array = convert_PictureModel_to_JSONArrary(tempModels);
        return array;
    }

    @Override
    public void onAction(String name,String id, String data, int pagePosition) {
        if (pagePosition == -1)
            pagePosition = position;

        ModuleLogEvent moduleLogEvent = new ModuleLogEvent(ActivityPicture.this,"","","","",getDate(),getTime()
                ,"","","",pagePosition+" -> "+data+" Question id = "+elementId,"",name,answerPictures.toString(),""
                ,0);
        moduleLogEvent.insert(moduleLogEvent, null, new IDBResultView() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onItemInserted() {

            }

            @Override
            public void onFail(String error) {

            }
        });
    }

    @Override
    public void onConditionaryDataChanged(String id, String value, boolean isChecked,String type) {

    }

    @Override
    public void isHiddenView() {

    }
}
