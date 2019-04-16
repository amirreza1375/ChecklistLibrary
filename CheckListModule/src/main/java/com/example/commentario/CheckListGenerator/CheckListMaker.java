package com.example.commentario.CheckListGenerator;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.commentario.Camera.ActivityPicture;
import com.example.commentario.CheckBox.CheckBoxGroup;
import com.example.commentario.Commentario.Commentario;
import com.example.commentario.Config;
import com.example.commentario.DropDownGenerator.DropDown;
import com.example.commentario.GlobalFuncs;
import com.example.commentario.HtmlViewer.WebViewer;
import com.example.commentario.ImageFile.ImageFileConcept;
import com.example.commentario.ImageSliderModel;
import com.example.commentario.ImageSliderView.ImageSliderViewer;
import com.example.commentario.MultiTextGenerator.MultiText;
import com.example.commentario.PictureElement.PicturePickerItemModel;
import com.example.commentario.R;
import com.example.commentario.RadioGroup.RadioGroupGenerator;
import com.example.commentario.RatingGenerator.RatingGenerator;
import com.example.commentario.ResultId;
import com.example.commentario.SeekBar.Nouislider;
import com.example.commentario.SignatureView.SignatureElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import static com.example.commentario.GlobalFuncs.conf_DataBase;
import static com.example.commentario.GlobalFuncs.conf_Elemento;
import static com.example.commentario.GlobalFuncs.conf_Posicion;
import static com.example.commentario.GlobalFuncs.conf_checkBox;
import static com.example.commentario.GlobalFuncs.conf_comment;
import static com.example.commentario.GlobalFuncs.conf_dropDown;
import static com.example.commentario.GlobalFuncs.conf_elements;
import static com.example.commentario.GlobalFuncs.conf_file;
import static com.example.commentario.GlobalFuncs.conf_html;
import static com.example.commentario.GlobalFuncs.conf_id;
import static com.example.commentario.GlobalFuncs.conf_imagePicker;
import static com.example.commentario.GlobalFuncs.conf_multiText;
import static com.example.commentario.GlobalFuncs.conf_optico;
import static com.example.commentario.GlobalFuncs.conf_position;
import static com.example.commentario.GlobalFuncs.conf_radioButton;
import static com.example.commentario.GlobalFuncs.conf_rating;
import static com.example.commentario.GlobalFuncs.conf_seekBar;
import static com.example.commentario.GlobalFuncs.conf_signature;
import static com.example.commentario.GlobalFuncs.conf_type;
import static com.example.commentario.GlobalFuncs.convert_JSONArray_to_PictureModel;
import static com.example.commentario.GlobalFuncs.convert_PictureModel_to_JSONArrary;
import static com.example.commentario.GlobalFuncs.dpToPx;
import static com.example.commentario.GlobalFuncs.getTitleFromElement;

public class CheckListMaker extends ScrollView implements View.OnClickListener
        , MultiText.MandatoryListener, ImageFileConcept.ButtonPressedCallBack {

    public boolean isFirstTime = true;

    private static final String TAG = "CheckListMaker";

    public static String key_TYPE = "type";
    public static String key_POSITION = "position";
    public static String key_VALUE = "value";

    public static String SavedPicturesFlag = "SAVEDPICTURES";

    private boolean isAllAnswered = false;

    public int getPosition() {
        return position;
    }

    public static enum pageStatus {
        CHECKLIST, DRAFT, PREVIEW
    }

    private boolean enable;
    private boolean isDraft;
    private Context context;
    private CheckListDataListener.CheckListConditionListener conditionListener;
    private CheckListDataListener listener;

    //all views
    private SignatureElement signatureElement;
    private Nouislider nouislider;
    private RadioGroupGenerator radioGroupGenerator;
    private CheckBoxGroup checkBoxGroup;
    private ImageFileConcept imageFileConcept;
    private Commentario commentario;
    private DropDown dropDown;
    private ImageSliderViewer imageSliderViewer;
    private MultiText multiText;
    private RatingGenerator ratingGenerator;
    private ArrayList<View> views;

    private View btnNext;
    private View btnPre;

    private boolean IsMandatoryAnswered = true;

    private JSONObject page;
    private pageStatus currentPageStatus;
    private int position;
    private ArrayList<ImageSliderModel> imageSliderModels;
    private int shopId;
    private JSONArray picAnswers;
    private JSONArray pageAnswers;
    private LinearLayout checkList;
    private String title;
    private JSONArray datas;
    private JSONArray conditions;
    private String signatureFolderPath;

    //region constructors
    public CheckListMaker(Context context, JSONObject page, pageStatus pageStatus
            , int position, ArrayList<ImageSliderModel> imageSliderModels, int shopId
            , JSONArray picAnswers, JSONArray pageAnswers, String signatureFolderPath
            , CheckListDataListener listener) {
        super(context);
        this.context = context;
        this.page = page;
        this.currentPageStatus = pageStatus;
        this.position = position;
        this.imageSliderModels = imageSliderModels;
        this.shopId = shopId;
        this.picAnswers = picAnswers;
        this.pageAnswers = pageAnswers;
        this.signatureFolderPath = signatureFolderPath;
        this.listener = listener;
        views = new ArrayList<>();
        conditions = new JSONArray();
        setPageStatus(pageStatus);
        init(context);
    }

    private void setPageStatus(pageStatus pageStatus) {
        switch (pageStatus) {
            case DRAFT:
                isDraft = true;
                enable = true;
                break;
            case PREVIEW:
                isDraft = false;
                enable = false;
                break;
            case CHECKLIST:
                isDraft = false;
                enable = true;
                break;
        }
    }

    public CheckListMaker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckListMaker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CheckListMaker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    //endregion

    private void init(Context context) {

//        btnNext.setEnabled(false);

        //region inner
        LinearLayout innerLayout = new LinearLayout(context);
        innerLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams innerParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(42, context));
        innerParams.setMargins(dpToPx(8, context), dpToPx(8, context), dpToPx(8, context), dpToPx(8, context));

        //endregion

        //region title
//        LinearLayout titleHolder = new LinearLayout(context);
//        LinearLayout.LayoutParams holderParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
//        holderParams.setMargins(dpToPx(8,context),dpToPx(8,context),dpToPx(8,context),dpToPx(8,context));
//        titleHolder.setLayoutParams(holderParams);
//        titleHolder.setGravity(Gravity.CENTER);
//        CardView cardView = new CardView(context);
//        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
//        cardView.setCardElevation(4);
//        cardView.setRadius(4);
//        cardView.setPadding(dpToPx(8,context),dpToPx(8,context),dpToPx(8,context),dpToPx(8,context));
//        TextView titleTxt = createTitle(context,false,page);
//
//        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
//        titleParams.setMargins(dpToPx(8,context),dpToPx(8,context),dpToPx(8,context),dpToPx(8,context));
//        titleTxt.setLayoutParams(titleParams);
//
//        cardView.addView(titleTxt);
//        titleHolder.addView(cardView);
//        innerLayout.addView(titleHolder);

        View titleHolder = LayoutInflater.from(context).inflate(R.layout.layout_page_title_library, this, false);
        TextView titleTxt = titleHolder.findViewById(R.id.titleTxt);
        titleTxt.setText(getTitleFromElement(page));
        innerLayout.addView(titleHolder);

        //endregion

        //region create cmp
        try {
            checkList = createComponnents(page.getJSONArray(conf_elements), innerLayout, context);
        } catch (JSONException e) {
            e.printStackTrace();
            listener.onCheckListError(e.getMessage());
        }
        //endregion


        try {
            assert checkList != null;
            addView(innerLayout);
        } catch (Exception e) {
            e.printStackTrace();
            listener.onCheckListError(e.getMessage());
        }

    }

    private LinearLayout createComponnents(JSONArray elements, LinearLayout linearLayout, Context context) {
        for (int i = 0; i < elements.length(); i++) {
            try {
                JSONObject element = elements.getJSONObject(i);

                //signature
                if (element.getString(conf_type)
                        .equals(conf_signature)) {
                    linearLayout.addView(createSignature(element));
                    continue;
                }

                //radio
                if (element.getString(conf_type)
                        .equals(conf_radioButton)) {
                    linearLayout.addView(createRadio(element, context));
                    continue;
                }
                //checkbox
                if (element.getString(conf_type)
                        .equals(conf_checkBox)) {
                    linearLayout.addView(createCheckBox(element, context));
                    continue;
                }
                //image taker
                if (element.getString(conf_type)
                        .equals(conf_file)) {
                    linearLayout.addView(createImageTaker(element, context));
                    continue;
                }
                //seek bar
                if (element.getString(conf_type)
                        .equals(conf_seekBar)) {
                    linearLayout.addView(createNouislider(element, context));
                    continue;
                }
                //multi text
                if (element.getString(conf_type)
                        .equals(conf_multiText)) {
                    linearLayout.addView(createMultiText(element, context));
                    continue;
                }
                //dropdown
                if (element.getString(conf_type)
                        .equals(conf_dropDown)) {
                    linearLayout.addView(createDropDown(element, context));
                    continue;
                }
                //rating
                if (element.getString(conf_type)
                        .equals(conf_rating)) {
                    linearLayout.addView(createRating(element, context));
                }
                //comment
                if (element.getString(conf_type)
                        .equals(conf_comment)) {
                    linearLayout.addView(createComment(element, context));
                }
                //Html
                if (element.getString(conf_type)
                        .equals(conf_html)){
                    linearLayout.addView(createHtml(element,context));
                }
                //dataBase
                if (element.getString(conf_type)
                        .equals(conf_imagePicker)) {
                    if (element.getString(conf_DataBase)
                            .equals(conf_optico)) {
                        linearLayout.addView(createOptico(element, context));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return linearLayout;
    }

    private View createHtml(JSONObject element, Context context) {
        WebViewer webViewer = new WebViewer(context,element);
        return webViewer;
    }


    private View createOptico(JSONObject element, Context context) {
        try {
            ArrayList<File> imageFiles = new ArrayList<>();
            ArrayList<String> names = new ArrayList<>();
            ArrayList<String> priorities = new ArrayList<>();

            for (int i = 0; i < imageSliderModels.size(); i++) {

                boolean isOk = true;

                ImageSliderModel model = imageSliderModels.get(i);

                ArrayList<ResultId> resultIds = model.getResultIDS();

                boolean isAnyResultOk = false;

                for (int j = 0; j < resultIds.size(); j++) {

                    ResultId resultId = resultIds.get(j);

                    try {
                        int Posicion = element.has(conf_Posicion) ? element.getInt(conf_Posicion) : -1;
                        int Elemento = element.has(conf_Elemento) ? element.getInt(conf_Elemento) : -1;
                        int subCanal = element.has("Subcanal") ? element.getInt("Subcanal") : -1;

                        if (Posicion > -1) {
                            if (Posicion != resultId.getPosicion()) {
                                isOk = false;
                                continue;
                            }
                        }
                        if (Elemento > -1) {
                            if (Elemento != resultId.getElemento()) {
                                isOk = false;
                                continue;
                            }
                        }
                        if (subCanal > -1) {
                            if (subCanal != resultId.getSubCanal()) {
                                isOk = false;
                                continue;
                            }
                        }
                        boolean FLAG_EXIST = false;
                        //check shops
                        for (int k = 0; k < model.getShops().size(); k++) {

                            if (model.getShops().get(k) == shopId) {
                                FLAG_EXIST = true;
                                break;
                            }

                        }
                        if (FLAG_EXIST) {
                            isAnyResultOk = true;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        listener.onCheckListError(e.getMessage());
                    }
                }//end of result for

                if (isAnyResultOk) {
                    names.add(model.getName());
                    priorities.add(model.getPrioritie());
                    imageFiles.add(model.getImageFile());
                }

            }//end of org for

            ImageSliderViewer imageSliderViewer = new ImageSliderViewer(context, element
                    , imageFiles, priorities, names);
            return imageSliderViewer;

        } catch (Exception e) {
            e.printStackTrace();
            listener.onCheckListError(e.getMessage());
            return new ImageSliderViewer(context, element
                    , new ArrayList<File>(), new ArrayList<String>(), new ArrayList<String>());
        }

    }


    public void setButtons(View btnPre, View btnNext, CheckListDataListener listener) {

        this.btnPre = btnPre;
        this.btnNext = btnNext;

        btnPre.setOnClickListener(this);
        btnNext.setOnClickListener(this);

        this.listener = listener;

    }


    @Override
    public void onClick(View v) {

        if (v == btnPre) {
            if (conditionListener != null) {
                conditionListener.onClearCondtionRecieved(conditions, position);
            }

            if (listener != null) {
                listener.onPreClicked(position, datas);
            }


        }
        if (v == btnNext) {
            checkMandatoriesAndChangeButtonStatus();
            IsMandatoryAnswered = true;
            conditions = new JSONArray(new ArrayList<String>());
            JSONArray datas = getData();
            if (conditionListener != null) {
                conditionListener.onConditionRecieved(conditions, position);
            }
            if (listener != null) {
                if (isMandatoryPicturesTaken()) {
                    if (IsMandatoryAnswered) {
                        listener.onNextClicked(position, datas);
                    } else {
                        Toast.makeText(context, "answer questions", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        }
    }

    private boolean isMandatoryPicturesTaken() {
        boolean FLAG_ALL_TAKEN = true;
        for (int i = 0; i < views.size(); i++) {

            if (views.get(i) instanceof ImageFileConcept) {

                ImageFileConcept temp = (ImageFileConcept) views.get(i);

                if (!temp.isMandatoryPictureTaken()) {
                    FLAG_ALL_TAKEN = false;
                    temp.setMandatoryError();
                }

            }

        }

        return FLAG_ALL_TAKEN;

    }

    public JSONArray getData() {
        JSONArray array = new JSONArray();
        //int this func we should get values from different views and combine them
        //1.get values
        //2.combine
        //3.parse views arrary to get instances type and get data
        for (int i = 0; i < views.size(); i++) {//commentario
            if (views.get(i) instanceof Commentario) {
                JSONObject commentValue = getCommentarioValue(views.get(i));
                array.put(commentValue);
                continue;
            }
            if (views.get(i) instanceof RadioGroupGenerator) {
                JSONObject commentValue = getRadioValue(views.get(i));
                array.put(commentValue);
                continue;
            }
            if (views.get(i) instanceof CheckBoxGroup) {

                JSONObject commentValue = getCheckBoxValue(views.get(i));
                array.put(commentValue);
                continue;
            }
            if (views.get(i) instanceof Nouislider) {
                JSONObject commentValue = getNouisliderValue(views.get(i));
                array.put(commentValue);
                continue;
            }
            if (views.get(i) instanceof MultiText) {
                JSONObject commentValue = getMultiTextValue(views.get(i));
                array.put(commentValue);
                continue;
            }
            if (views.get(i) instanceof RatingGenerator) {
                JSONObject commentValue = getRatingValue(views.get(i));
                array.put(commentValue);
                continue;
            }
            if (views.get(i) instanceof DropDown) {
                JSONObject commentValue = getDropDownValue(views.get(i));
                array.put(commentValue);
            }
            if (views.get(i) instanceof SignatureElement) {
                JSONObject signatureValue = getSignatureValue(views.get(i));
                array.put(signatureValue);
            }

        }

        return array;
    }

    //region data getters

    private JSONObject getSignatureValue(View view) {
        JSONObject object = new JSONObject();
        SignatureElement signatureElement = (SignatureElement) view;
        String path = signatureElement.getValue();
        try {
            object.put(key_POSITION, position);
            object.put(key_TYPE, conf_signature);
            object.put(GlobalFuncs.conf_id, signatureElement.getElementId());
            object.put(key_VALUE, path);
        } catch (JSONException e) {
            e.printStackTrace();

        }
        return object;
    }

    private JSONObject getDropDownValue(View view) {
        JSONObject object = new JSONObject();
        DropDown dropDown = (DropDown) view;
        String item = dropDown.getValue();
        try {
            object.put(key_POSITION, position);
            object.put(key_TYPE, conf_dropDown);
            object.put(GlobalFuncs.conf_id, dropDown.getElementId());
            object.put(key_VALUE, item);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    private JSONObject getRatingValue(View view) {
        JSONObject object = new JSONObject();
        RatingGenerator ratingGenerator = (RatingGenerator) view;
        int rate = ratingGenerator.getValue();
        try {
            object.put(key_POSITION, position);
            object.put(key_TYPE, conf_rating);
            object.put(GlobalFuncs.conf_id, ratingGenerator.getElementId());
            object.put(key_VALUE, rate);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    private JSONObject getMultiTextValue(View view) {
        JSONObject object = new JSONObject();
        MultiText multiText = (MultiText) view;
        try {
            object.put(key_POSITION, position);
            object.put(key_TYPE, conf_multiText);
            object.put(key_VALUE, multiText.getValue());
            object.put(GlobalFuncs.conf_id, multiText.getElementId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    private JSONObject getNouisliderValue(View view) {
        JSONObject object = new JSONObject();
        Nouislider nouislider = (Nouislider) view;
        int value = nouislider.getValue();
        try {
            object.put(GlobalFuncs.conf_name, nouislider.getName());
            object.put(key_POSITION, position);
            object.put(key_TYPE, conf_seekBar);
            object.put(GlobalFuncs.conf_id, nouislider.getElementId());
            object.put(key_VALUE, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object;
    }

    private JSONObject getCheckBoxValue(View view) {
        JSONObject object = new JSONObject();
        CheckBoxGroup checkBoxGroup = (CheckBoxGroup) view;
        JSONArray items = checkBoxGroup.getValues();
        try {
            object.put(key_POSITION, position);
            object.put(key_TYPE, conf_checkBox);
            object.put(GlobalFuncs.conf_id, checkBoxGroup.getElementId());
            object.put(key_VALUE, items);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        addCheckBoxConditions(items);
        return object;
    }


    private JSONObject getRadioValue(View view) {
        JSONObject object = new JSONObject();
        RadioGroupGenerator radioGroupGenerator = (RadioGroupGenerator) view;
        JSONObject radioItem = radioGroupGenerator.getValue();
        try {
            object.put(key_POSITION, position);
            object.put(key_TYPE, conf_radioButton);
            object.put(GlobalFuncs.conf_id, radioGroupGenerator.getElementId());
            object.put(key_VALUE, radioItem);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        addRadioCondition(radioItem);
        return object;
    }


    private JSONObject getCommentarioValue(View view) {
        Commentario commentario = (Commentario) view;
        String comment = commentario.getCommentValue();
        JSONObject object = new JSONObject();
        try {
            object.put(GlobalFuncs.conf_name, commentario.getName());
            object.put(key_POSITION, position);
            object.put(key_TYPE, conf_comment);
            object.put(GlobalFuncs.conf_id, commentario.getElementId());
            object.put(key_VALUE, comment);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    //endregion

    private void addCheckBoxConditions(JSONArray items) {
        for (int i = 0; i < items.length(); i++) {

            try {
                JSONObject item = items.getJSONObject(i);
                item.put(conf_type, conf_checkBox);
                item.put(conf_position,position);
                conditions.put(item);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    public void checkMandatoriesAndChangeButtonStatus() {

        Log.i(TAG, "checkMandatoriesAndChangeButtonStatus: "+position);

        isAllAnswered = true;

        getData();//trigger not answered element event

        if (!isMandatoryPicturesTaken()) {
            isAllAnswered = false;

        }

        Log.i(TAG, "checkMandatoriesAndChangeButtonStatus: is all answered" + isAllAnswered);

        if (isAllAnswered) {
            if (btnNext != null) {
                btnNext.setEnabled(true);
                Log.i(TAG, "checkMandatoriesAndChangeButtonStatus: true");
            }
        } else {
            if (btnNext != null) {
                btnNext.setEnabled(false);
                Log.i(TAG, "checkMandatoriesAndChangeButtonStatus: false");
            }
        }


    }


    private void addRadioCondition(JSONObject radioItem) {
        try {
            radioItem.put(conf_type, conf_radioButton);
            radioItem.put(conf_position,position);
            conditions.put(radioItem);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public CheckListDataListener getListener() {
        return listener;
    }

    public void setListener(CheckListDataListener listener) {
        this.listener = listener;
    }

    @Override
    public void onMandatoryStatusError() {
        IsMandatoryAnswered = false;
        isAllAnswered = false;
    }

    @Override
    public void onElementStatusChanged() {
        checkMandatoriesAndChangeButtonStatus();
    }

    public CheckListDataListener.CheckListConditionListener getConditionListener() {
        return conditionListener;
    }

    public void setConditionListener(CheckListDataListener.CheckListConditionListener conditionListener) {
        this.conditionListener = conditionListener;
    }

    @Override
    public void onButtonClicked(JSONObject element) {

//        Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
        try {
            listener.onCameraLoad();
            ActivityPicture.answerPictures = getPicsByPosition(position, picAnswers);
            Intent intent = new Intent(context
                    , Class.forName("com.example.commentario.Camera.ActivityPicture"));
            intent.putExtra(SavedPicturesFlag, picAnswers.toString());
            intent.putExtra("position", position);
            intent.putExtra("element", element.toString());

            context.startActivity(intent);


        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            listener.onCheckListError(e.getMessage());
        }

    }

    private JSONArray getPicsByPosition(int position, JSONArray answers) {
        ArrayList<PicturePickerItemModel> pickerItemModels = convert_JSONArray_to_PictureModel(answers);
        for (int i = 0; i < pickerItemModels.size(); i++) {
            if (pickerItemModels.get(i).getPosition() != position) {
                pickerItemModels.remove(i);
                i--;
            }
        }
        return convert_PictureModel_to_JSONArrary(pickerItemModels);
    }

    public void updateViewsStatus() {
        findImageFileConcept(views);
    }

    private void findImageFileConcept(ArrayList<View> views) {
        for (int i = 0; i < views.size(); i++) {
            if (views.get(i) instanceof ImageFileConcept) {
                ImageFileConcept imageFileConcept = (ImageFileConcept) views.get(i);
                if (isImageFileConceptHasImage(context, imageFileConcept)) {
                    imageFileConcept.setHasPicStatus(context);
                }
            }
        }
    }


    private boolean isImageFileConceptHasImage(Context context, ImageFileConcept imageFileConcept) {
        String picturesStr = context.getSharedPreferences(Config.sharedPreferencName, Context.MODE_PRIVATE)
                .getString(Config.pictures, "");
        try {
            JSONArray pictures = new JSONArray(picturesStr);
            for (int i = 0; i < pictures.length(); i++) {
                JSONObject picture = pictures.getJSONObject(i);
                if (picture.getString(PicturePickerItemModel.conf_id)
                        .equals(imageFileConcept.getElementId())) {
                    return true;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            listener.onCheckListError(e.getMessage());
        }
        return false;
    }


    //region generate views


    private View createComment(JSONObject element, Context context) {
        commentario = new Commentario(context, enable, getCommentAnswer(element), true
                , element, this);
        commentario.setListener(this);
        views.add(commentario);
        return commentario;
    }

    private String getCommentAnswer(JSONObject element) {
        String answer = "";
        for (int i = 0; i < pageAnswers.length(); i++) {
            try {
                if (element.getString("id")
                        .equals(pageAnswers.getJSONObject(i).getString("id"))) {
                    answer = pageAnswers.getJSONObject(i).getString("value");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                listener.onCheckListError(e.getMessage());
            }
        }
        return answer;
    }

    public ArrayList<PicturePickerItemModel> getSignatures(){
        ArrayList<PicturePickerItemModel> pickerItemModels = new ArrayList<>();

        for (int i = 0 ; i < views.size() ; i++){

            if (views.get(i) instanceof SignatureElement){

                SignatureElement signatureElement = (SignatureElement) views.get(i);

                PicturePickerItemModel model = new PicturePickerItemModel();
                model.setPosition(position);
                model.setStatus(true);
                model.setId(signatureElement.getElementId());
                model.setPath(signatureElement.getValue());
                model.setCat_id(12);
                model.setCategory("12");

                pickerItemModels.add(model);

            }

        }

        return pickerItemModels;

    }

    private LinearLayout createSignature(JSONObject element) {

        signatureElement = new SignatureElement(context, element, false
                , signatureFolderPath,getSignatureAnswer(element),enable);
        views.add(signatureElement);

        return signatureElement;

    }

    private JSONObject getSignatureAnswer(JSONObject element) {
        JSONObject answer = new JSONObject();
        for (int i = 0; i < pageAnswers.length(); i++) {
            try {
                if (element.getString("id")
                        .equals(pageAnswers.getJSONObject(i).getString("id"))) {
                    answer = pageAnswers.getJSONObject(i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return answer;
    }

    private RatingGenerator createRating(JSONObject element, Context context) {
        ratingGenerator = new RatingGenerator(context, element, enable, 2);
        views.add(ratingGenerator);
        return ratingGenerator;
    }

    private DropDown createDropDown(JSONObject element, Context context) {
        dropDown = new DropDown(context, element, enable, "");
        views.add(dropDown);
        return dropDown;
    }

    private MultiText createMultiText(JSONObject element, Context context) {
        multiText = new MultiText(context, element, enable
                , getMultiTextAnswer(element), position,this);

        views.add(multiText);
        return multiText;
    }

    private JSONArray getMultiTextAnswer(JSONObject element) {
        JSONArray answer = new JSONArray();
        for (int i = 0; i < pageAnswers.length(); i++) {
            try {
                if (element.getString("id")
                        .equals(pageAnswers.getJSONObject(i).getString("id"))) {
                    answer = pageAnswers.getJSONObject(i).getJSONArray("value");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return answer;
    }

    private Nouislider createNouislider(JSONObject element, Context context) {
        nouislider = new Nouislider(context, element, getNouislderAnswer(element), enable);
        nouislider.setListener(this);
        views.add(nouislider);
        return nouislider;
    }

    private int getNouislderAnswer(JSONObject element) {
        int answer = -1;
        for (int i = 0; i < pageAnswers.length(); i++) {
            try {
                if (element.getString("id")
                        .equals(pageAnswers.getJSONObject(i).getString("id"))) {
                    answer = pageAnswers.getJSONObject(i).getInt("value");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return answer;
    }

    private ImageFileConcept createImageTaker(JSONObject element, Context context) {

        String label;

        if (currentPageStatus == CheckListMaker.pageStatus.PREVIEW) {
            label = context.getString(R.string.show_picture);
        } else {
            label = context.getString(R.string.take_picture);
        }

        imageFileConcept = new ImageFileConcept(context, element
                , label, enable, hasPic(element, imageFileConcept));
        imageFileConcept.setCallBack(this);
        views.add(imageFileConcept);
        return imageFileConcept;
    }

    private CheckBoxGroup createCheckBox(JSONObject element, Context context) {
        checkBoxGroup = new CheckBoxGroup(context, element
                , enable, getCheckBoxAnswer(element), position);
        checkBoxGroup.setListener(this);
        views.add(checkBoxGroup);
        return checkBoxGroup;
    }

    private ArrayList<String> getCheckBoxAnswer(JSONObject element) {
        ArrayList<String> answers = new ArrayList<>();
        for (int i = 0; i < pageAnswers.length(); i++) {
            try {
                if (element.getString("id")
                        .equals(pageAnswers.getJSONObject(i).getString("id"))) {
                    JSONArray values = pageAnswers.getJSONObject(i).getJSONArray("value");
                    for (int j = 0; j < values.length(); j++) {
                        if (values.getJSONObject(j).getBoolean("status")) {
                            answers.add(values.getJSONObject(j).getString("value"));
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return answers;
    }

    private RadioGroupGenerator createRadio(JSONObject element, Context context) {
        radioGroupGenerator = new RadioGroupGenerator(context
                , element, false, enable, getRadioButtonAnswer(element), position);
        radioGroupGenerator.setListener(this);
        views.add(radioGroupGenerator);
        return radioGroupGenerator;
    }

    private JSONObject getRadioButtonAnswer(JSONObject element) {
        JSONObject answer = new JSONObject();
        for (int i = 0; i < pageAnswers.length(); i++) {
            try {
                if (element.getString("id")
                        .equals(pageAnswers.getJSONObject(i).getString("id"))) {
                    JSONObject values = pageAnswers.getJSONObject(i).getJSONObject("value");
                    return values;

                }
            } catch (JSONException e) {
                e.printStackTrace();

            }
        }
        return new JSONObject();
    }


    //endregion


    private boolean hasPic(JSONObject element, ImageFileConcept imageFileConcept) {
        boolean FLAG_HAS_PIC = false;
        for (int i = 0; i < picAnswers.length(); i++) {
            try {
                JSONObject pic = picAnswers.getJSONObject(i);
                if (pic.getString(conf_id)
                        .equals(element.getString(conf_id))) {
                    FLAG_HAS_PIC = true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return FLAG_HAS_PIC;
    }

    public void clear() {
        Animation removeAnim = AnimationUtils.loadAnimation(context, R.anim.slide_out_view);
        removeAnim.setFillAfter(false);
        removeAnim.setRepeatCount(0);
        removeAnim.setRepeatMode(Animation.RESTART);
        checkList.startAnimation(removeAnim);
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkList.removeAllViews();
            }
        }, 800);
    }


}
