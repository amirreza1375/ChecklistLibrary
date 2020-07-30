package com.example.checklist.CheckListGenerator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.checklist.BaseViewModel.BaseView;
import com.example.checklist.BaseViewModel.BaseViewModel;
import com.example.checklist.BaseViewModel.ElemetActionListener;
import com.example.checklist.BaseViewModel.IConditionChangeListener;
import com.example.checklist.BaseViewModel.ViewTypeKey;
import com.example.checklist.Camera.ActivityPicture;
import com.example.checklist.CheckBox.CheckBoxView;
import com.example.checklist.Commentario.CommentView;
import com.example.checklist.Config;
import com.example.checklist.Database.IDBResultView;
import com.example.checklist.Database.ModuleLogEvent;
import com.example.checklist.DropDownGenerator.DropDownView;
import com.example.checklist.HtmlViewer.HTMLView;
import com.example.checklist.ImageFile.ImageFileConcept;
import com.example.checklist.ImageFile.ImageSectionButton;
import com.example.checklist.ImageSliderModel;
import com.example.checklist.ImageSliderView.ImageSliderView;
import com.example.checklist.ImageSliderView.ImagesViewer;
import com.example.checklist.LayoutMaker.LayoutMaker;
import com.example.checklist.LayoutMaker.LayoutModel;
import com.example.checklist.MultiTextGenerator.MultiTextView;
import com.example.checklist.NACheckBox.NA_CheckBoxView;
import com.example.checklist.PageGenerator.CheckListPager;
import com.example.checklist.PictureElement.PicturePickerItemModel;
import com.example.checklist.ProductCounter.ProductModel;
import com.example.checklist.ProductCounter.ProductView;
import com.example.checklist.R;
import com.example.checklist.RadioGroupMaker.RadioGoupView;
import com.example.checklist.RatingGenerator.RatingView;
import com.example.checklist.ResultId;
import com.example.checklist.SeekBar.SeekBarView;
import com.example.checklist.SignatureView.SignatureView;
import com.example.checklist.SimpleText.SimpleTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import static com.example.checklist.GlobalFuncs.addEvenLog;
import static com.example.checklist.GlobalFuncs.conf_DataBase;
import static com.example.checklist.GlobalFuncs.conf_Elemento;
import static com.example.checklist.GlobalFuncs.conf_Posicion;
import static com.example.checklist.GlobalFuncs.conf_checkBox;
import static com.example.checklist.GlobalFuncs.conf_comment;
import static com.example.checklist.GlobalFuncs.conf_dropDown;
import static com.example.checklist.GlobalFuncs.conf_elements;
import static com.example.checklist.GlobalFuncs.conf_file;
import static com.example.checklist.GlobalFuncs.conf_html;
import static com.example.checklist.GlobalFuncs.conf_id;
import static com.example.checklist.GlobalFuncs.conf_imagePicker;
import static com.example.checklist.GlobalFuncs.conf_multiText;
import static com.example.checklist.GlobalFuncs.conf_name;
import static com.example.checklist.GlobalFuncs.conf_optico;
import static com.example.checklist.GlobalFuncs.conf_position;
import static com.example.checklist.GlobalFuncs.conf_productCount;
import static com.example.checklist.GlobalFuncs.conf_radioButton;
import static com.example.checklist.GlobalFuncs.conf_rating;
import static com.example.checklist.GlobalFuncs.conf_seekBar;
import static com.example.checklist.GlobalFuncs.conf_signature;
import static com.example.checklist.GlobalFuncs.conf_simpleText;
import static com.example.checklist.GlobalFuncs.conf_tipo;
import static com.example.checklist.GlobalFuncs.conf_tipoNA;
import static com.example.checklist.GlobalFuncs.conf_title;
import static com.example.checklist.GlobalFuncs.conf_type;
import static com.example.checklist.GlobalFuncs.conf_value;
import static com.example.checklist.GlobalFuncs.convert_ArrayList_to_JSONArray;
import static com.example.checklist.GlobalFuncs.convert_JSONArray_to_ArrayList;
import static com.example.checklist.GlobalFuncs.convert_JSONArray_to_PictureModel;
import static com.example.checklist.GlobalFuncs.convert_PictureModel_to_JSONArrary;
import static com.example.checklist.GlobalFuncs.getDate;
import static com.example.checklist.GlobalFuncs.getTime;
import static com.example.checklist.GlobalFuncs.getTitleFromElement;
import static com.example.checklist.GlobalFuncs.hideKeyboard;
import static com.example.checklist.GlobalFuncs.log;

public class PageView extends ScrollView implements ElemetActionListener, ImagesViewer.ImageSliderListener
        , ImageFileConcept.ButtonPressedCallBack, View.OnClickListener, IConditionChangeListener {

    private ScrollView scrollView;

    private View pre;
    private View next;


    public static enum pageStatus {
        CHECKLIST, DRAFT, PREVIEW
    }

    private static final String TAG = "PageView";
    public static String SavedPicturesFlag = "SAVEDPICTURES";

    private Context context;
    private JSONObject page;
    private int pagePosition;
    private ArrayList<ImageSliderModel> imageSliderModels;
    private String shopId;
    private JSONArray picAnswers;
    private JSONArray pageAnswers;
    private String signatureFolderPath;
    private CheckListDataListener listener;
    private ArrayList<LayoutModel> layoutModels;
    private ArrayList<ProductModel> productModels;
    private String checklistServerId;
    private JSONArray allConditions;
    private PageView.pageStatus currentPageStatus;

    private JSONArray conditions;
    private boolean isDraft;
    private boolean enable;
    private boolean isHiddenViewExist = false;

    private ArrayList<BaseViewModel> views;

    private LinearLayout viewHolder;
    private CheckListDataListener.CheckListConditionListener conditionListener;

    public PageView(Context context, JSONObject page, PageView.pageStatus pageStatus
            , int position, ArrayList<ImageSliderModel> imageSliderModels, String shopId
            , JSONArray picAnswers, JSONArray pageAnswers, String signatureFolderPath
            , CheckListDataListener listener, ArrayList<LayoutModel> layoutModels
            , ArrayList<ProductModel> productModels, String checklistServerId
            , JSONArray allConditions) {
        super(context);
        this.context = context;
        this.page = page;
        this.currentPageStatus = pageStatus;
        this.pagePosition = position;
        this.imageSliderModels = imageSliderModels;
        this.shopId = shopId;
        this.picAnswers = picAnswers;
        this.pageAnswers = pageAnswers;
        this.signatureFolderPath = signatureFolderPath;
        this.listener = listener;
        listener.onChecklistLoadStarted();
        this.layoutModels = layoutModels;
        this.productModels = productModels;
        this.checklistServerId = checklistServerId;
        this.allConditions = allConditions;
        views = new ArrayList<>();
        conditions = new JSONArray();

        if (position == 0)
            addFirstPageConditions(pageAnswers);

        setPageStatus(pageStatus);
        init();
        updateItemsByConditionChanged();
        updateViewsStatus();
    }

    private void init() {
        View baseView = LayoutInflater.from(context).inflate(R.layout.layout_page_view, this, false);
        scrollView = baseView.findViewById(R.id.scrollView);
        TextView titleText = baseView.findViewById(R.id.titleText);
        String name = "";
        String title = "";
        try {
            name = page.has(conf_name) ? page.getString(conf_name) : "";
            title = page.has(conf_title) ? page.getString(conf_title) : "";
        } catch (JSONException e) {
            e.printStackTrace();
        }
        titleText.setText(name + "\n" + title);

        LinearLayout viewHolder = baseView.findViewById(R.id.viewHolder);

        try {
            createComponnents(page.getJSONArray(conf_elements), viewHolder);
            addView(baseView);

            scrollView.fullScroll(ScrollView.FOCUS_UP);

            listener.onChecklistLoadFinished();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void setPageStatus(PageView.pageStatus pageStatus) {
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

    public void setVisibleSiFromOtherePages(ArrayList<JSONObject> visibleSiData) {

        for (BaseViewModel view : views) {

            if (view.isElementIsVisibleSi()) {
                boolean isExist = false;

                for (JSONObject object : visibleSiData) {

                    try {
                        String name = object.getString(conf_name);
                        String value = object.getString(conf_value);


                        if (name.trim().equals(view.getElementVisibleSiName().trim())
                                && value.trim().equals(view.getElementVisibleSiValue().trim())) {
                            showView(view);
                            isExist = true;
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                if (!isExist) {
                    hideView(view);
                }
            }
        }

    }

    public void setMandatoryErrors() {
        for (BaseViewModel view : views) {
            if (view.isShowen()) {
                if (!view.isMandatoryAnswered()) {
                    view.setMandatoryError();
                }
            }
        }
    }

    //region create components

    private LinearLayout createComponnents(JSONArray elements, LinearLayout linearLayout) {
        for (int i = 0; i < elements.length(); i++) {
            try {
                JSONObject element = elements.getJSONObject(i);

                //simple text
                if (element.getString(conf_type)
                        .equals(conf_simpleText)) {
                    linearLayout.addView(createSimpleText(element, i));
                    continue;
                }
                //signature
                if (element.getString(conf_type)
                        .equals(conf_signature)) {
                    linearLayout.addView(createSignature(element, i));
                    continue;
                }

                //radio
                if (element.getString(conf_type)
                        .equals(conf_radioButton)) {
                    linearLayout.addView(createRadio(element, context, i));
                    continue;
                }
                //checkbox
                if (element.getString(conf_type)
                        .equals(conf_checkBox)) {
                    if (element.has(conf_tipoNA)) {
                        if (element.getBoolean(conf_tipoNA)) {
                            linearLayout.addView(createNACheckBox(element, i));
                        } else {
                            linearLayout.addView(createSignature(element, i));
                        }
                    } else {
                        linearLayout.addView(createCheckBox(element, context, i));
                    }
                    continue;
                }
                //image taker
                if (element.getString(conf_type)
                        .equals(conf_file)) {
                    linearLayout.addView(createImageTaker(element, context, i));
                    continue;
                }
                //seek bar
                if (element.getString(conf_type)
                        .equals(conf_seekBar)) {
                    linearLayout.addView(createSeekBar(element, context, i));
                    continue;
                }
                //multi text
                if (element.getString(conf_type)
                        .equals(conf_multiText)) {
                    linearLayout.addView(createMultiText(element, context, i));
                    continue;
                }
                //dropdown
                if (element.getString(conf_type)
                        .equals(conf_dropDown)) {
                    linearLayout.addView(createDropDown(element, context, i));
                    continue;
                }
                //rating
                if (element.getString(conf_type)
                        .equals(conf_rating)) {
                    linearLayout.addView(createRating(element, context, i));
                }
                //comment
                if (element.getString(conf_type)
                        .equals(conf_comment)) {
                    if (element.has(conf_tipo)) {
                        if (element.getString(conf_tipo)
                                .equals(conf_productCount)) {
                            linearLayout.addView(createProductCounter(element, context, i));
                        } else {

                            if (isComment(element)) {
                                linearLayout.addView(createComment(element, context, i));
                            } else {
                                linearLayout.addView(createLayout(element, context, i));
                            }
                        }

                    } else {
                        if (isComment(element)) {
                            linearLayout.addView(createComment(element, context, i));
                        } else {
                            linearLayout.addView(createLayout(element, context, i));
                        }
                    }

                }
                //Html
                if (element.getString(conf_type)
                        .equals(conf_html)) {
                    linearLayout.addView(createHtml(element, context, i));
                }
                //dataBase
                if (element.getString(conf_type)
                        .equals(conf_imagePicker)) {
                    if (element.getString(conf_DataBase)
                            .equals(conf_optico)) {
                        Log.i(TAG, "createComponnents: optico added");
                        linearLayout.addView(createOptico(element, context, i));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                log(e.getMessage());
            }

        }
        return linearLayout;
    }


    private ImageSectionButton createImageTaker(JSONObject element, Context context, int position) {

        String label;

        if (currentPageStatus == PageView.pageStatus.PREVIEW) {
            label = context.getString(R.string.show_picture);
        } else {
            label = context.getString(R.string.take_picture);
        }

        ImageSectionButton imageFileConcept = new ImageSectionButton(context, element
                , label, hasPic(element), this, new JSONObject(), enable, pagePosition, position);
        imageFileConcept.setCallBack(this);
        views.add(imageFileConcept);
        return imageFileConcept;
    }


    private CheckBoxView createCheckBox(JSONObject element, Context context, int position) {
        CheckBoxView checkBoxGroup = new CheckBoxView(context, element
                , this, getViewAnswer(element), enable, this, pagePosition, position);
        views.add(checkBoxGroup);
        return checkBoxGroup;
    }

    private RadioGoupView createRadio(JSONObject element, Context context, int position) {
        RadioGoupView radioGroupMaker = new RadioGoupView(context
                , element, this, getViewAnswer(element), enable, this, pagePosition, position);
        views.add(radioGroupMaker);
        return radioGroupMaker;
    }

    private SeekBarView createSeekBar(JSONObject element, Context context, int position) {
        SeekBarView seekBarView = new SeekBarView(context, element, this, getViewAnswer(element), enable, pagePosition, position);
        views.add(seekBarView);
        return seekBarView;
    }

    private int getNouislderAnswer(JSONObject element, int position) {
        int answer = -1;
        for (int i = 0; i < pageAnswers.length(); i++) {
            try {
                if (element.getString("id")
                        .equals(pageAnswers.getJSONObject(i).getString("id"))) {
                    answer = pageAnswers.getJSONObject(i).getInt("value");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                log(e.getMessage());
            }
        }
        return answer;
    }

    private View createComment(JSONObject element, Context context, int position) {
        CommentView commentario = new CommentView(context, element, this
                , getViewAnswer(element), enable, pagePosition, position);
        views.add(commentario);
        return commentario;
    }

    private RatingView createRating(JSONObject element, Context context, int position) {
        RatingView ratingGenerator = new RatingView(context, element, this, new JSONObject(), enable, pagePosition, position);
        views.add(ratingGenerator);
        return ratingGenerator;
    }

    private DropDownView createDropDown(JSONObject element, Context context, int position) {
        DropDownView dropDown = new DropDownView(context, element, this, new JSONObject(), enable, pagePosition, position);
        views.add(dropDown);
        return dropDown;
    }

    private MultiTextView createMultiText(JSONObject element, Context context, int position) {
        MultiTextView multiText = new MultiTextView(context, element, this
                , getViewAnswer(element), enable, pagePosition, position);
        views.add(multiText);
        return multiText;
    }


    private LinearLayout createSignature(JSONObject element, int position) {

        SignatureView signatureElement = new SignatureView(context, element, this
                , signatureFolderPath, getViewAnswer(element), enable, pagePosition, position);
        views.add(signatureElement);

        return signatureElement;

    }


    private View createSimpleText(JSONObject element, int position) {
        SimpleTextView simpleTextView = new SimpleTextView(context, element, this, getViewAnswer(element), enable, pagePosition, position);
        views.add(simpleTextView);
        return simpleTextView;
    }


    private View createNACheckBox(JSONObject element, int position) {
        NA_CheckBoxView naCheckBoxCreator = new NA_CheckBoxView(context, element, this, getViewAnswer(element), enable, pagePosition, position);
        views.add(naCheckBoxCreator);
        return naCheckBoxCreator;
    }


    private View createProductCounter(JSONObject element, Context context, int position) {
        ProductView productCounter = new ProductView(context, element, this, getViewAnswer(element)
                , productModels, shopId + "", enable, pagePosition, position);
        views.add(productCounter);
        return productCounter;
    }

    private View createLayout(JSONObject element, Context context, int position) {
        LayoutMaker layoutMaker = new LayoutMaker(context, layoutModels, element, shopId + "", this);
        return layoutMaker;
    }

    private View createHtml(JSONObject element, Context context, int position) {
        HTMLView webViewer = new HTMLView(context, element, this, new JSONObject(), enable, pagePosition, position);
        return webViewer;
    }

    private View createOptico(JSONObject element, Context context, int position) {
        try {
            ArrayList<File> imageFiles = new ArrayList<>();
            ArrayList<String> names = new ArrayList<>();
            ArrayList<String> priorities = new ArrayList<>();

            for (int i = 0; i < imageSliderModels.size(); i++) {

                ImageSliderModel model = imageSliderModels.get(i);

                if (isConditionsAreOk(model, element)) {
                    if (isSurveyOk(model)) {
                        names.add(model.getName());
                        priorities.add(model.getPrioritie());
                        imageFiles.add(model.getImageFile());
                    }
                }

            }//end of org for

            ImageSliderView imagesViewer = new ImageSliderView(context, element
                    , this, new JSONObject(), enable, imageFiles, priorities, names, this, pagePosition, position);
            return imagesViewer;

        } catch (Exception e) {
            e.printStackTrace();
            log(e.getMessage());
//            listener.onCheckListError(e.getMessage());
            return new ImageSliderView(context, element
                    , this, new JSONObject(), enable
                    , new ArrayList<File>(), new ArrayList<String>()
                    , new ArrayList<String>(), this, pagePosition, position);
        }

    }


    //endregion

    private void updateItemsByConditionChanged() {
        if (isHiddenViewExist) {
            for (BaseViewModel view : views) {
                if (view.isElementIsVisibleSi()) {
                    if (isIdExistInConditions(view.getElementVisibleSiName(), view.getElementVisibleSiValue()))
                        showView(view);
                    else
                        hideView(view);
                }
            }
        }
    }

    private void addFirstPageConditions(JSONArray answers) {
        for (int i = 0; i < answers.length(); i++) {
            try {
                JSONObject answer = answers.getJSONObject(i);
                if (answer.getString(conf_type).equals(conf_radioButton)) {
                    conditions.put(answer.getJSONObject(conf_value));
                } else if (answer.getString(conf_type).equals(conf_checkBox)) {
                    JSONArray values = answer.getJSONArray(conf_value);
                    for (int j = 0; j < values.length(); j++) {
                        conditions.put(values.getJSONObject(j));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isComment(JSONObject element) {
        boolean FLAG_IS_COMMENT = true;

        if (element.has("Layout")) {
            try {
                if (element.getString("Layout").equals("")
                        || element.getString("Layout").equals("0")) {
                    FLAG_IS_COMMENT = true;
                } else {
                    FLAG_IS_COMMENT = false;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            FLAG_IS_COMMENT = true;
        }

        return FLAG_IS_COMMENT;
    }

    private boolean isConditionsAreOk(ImageSliderModel model, JSONObject element) {

        ArrayList<ResultId> resultIds = model.getResultIDS();

        boolean isOk = true;
        boolean isAnyResultOk = false;

        for (int j = 0; j < resultIds.size(); j++) {

            ResultId resultId = resultIds.get(j);

            try {
                int Posicion = element.has(conf_Posicion) ? element.getInt(conf_Posicion) : -1;
                int Elemento = element.has(conf_Elemento) ? element.getInt(conf_Elemento) : -1;
                int subCanal = element.has("Subcanal") ? element.getInt("Subcanal") : -1;

                Log.i(TAG, "isConditionsAreOk: Posicion " + Posicion + " = " + resultId.getPosicion());
                Log.i(TAG, "isConditionsAreOk: Elemnto " + Elemento + " = " + resultId.getElemento());
                Log.i(TAG, "isConditionsAreOk: SubCanal " + subCanal + " = " + resultId.getSubCanal());

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

                    if (model.getShops().get(k).toString().trim().equals( shopId)) {
                        Log.i(TAG, "isConditionsAreOk: Shop is Ok" + shopId + " = " + model.getShops().get(k));
                        FLAG_EXIST = true;
                        break;
                    }

                }
                if (FLAG_EXIST) {

                    isAnyResultOk = true;
                }

            } catch (JSONException e) {
                e.printStackTrace();
                log(e.getMessage());
                listener.onCheckListError(e.getMessage());
            }
        }//end of result for
        Log.i(TAG, "isConditionsAreOk: result is ok" + isAnyResultOk);
        return isAnyResultOk;
    }

    private boolean isSurveyOk(ImageSliderModel model) {
        if (model.getSurveyIdes() == null)
            return true;
        if (model.getSurveyIdes().equals(""))
            return true;

        String surveys[] = model.getSurveyIdes().split(",");
        for (String survey : surveys) {
            if (survey.equals(checklistServerId)) {
                return true;
            }
        }
        Log.i(TAG, "isSurveyOk: Not Ok");
        return false;
    }

    private JSONObject getViewAnswer(JSONObject element) {
        for (int i = 0; i < pageAnswers.length(); i++) {
            try {
                JSONObject viewAnswer = pageAnswers.getJSONObject(i);
                if (viewAnswer.getString(conf_id).equals(element.getString(conf_id))) {
                    return viewAnswer;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return new JSONObject();
    }


    private boolean hasPic(JSONObject element) {
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
                log(e.getMessage());
            }
        }
        return FLAG_HAS_PIC;
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

    private void hideView(BaseViewModel view) {
//        Log.i(TAG, "hideView: "+view.getName());
        Log.i(TAG, "hideView: " + view.getElementId());
        view.setVisibility(GONE);
        view.setShown(false);
        removeElementData(view);
        addEvenLog(context, pagePosition, view.getElementId(), "Hide view", view.getElementId(), pageAnswers.toString());
    }

    private void showView(BaseViewModel view) {
//        Log.i(TAG, "showView: "+view.getName());
        addEvenLog(context, pagePosition, view.getElementId(), "Show view", view.getElementId(), pageAnswers.toString());
        view.setVisibility(VISIBLE);
        view.setShown(true);
    }

    private void removeElementData(BaseViewModel baseView) {
        removeElementConditions(baseView.getElementId());//remove element data from conditions
        if (baseView instanceof CheckBoxView) {
            listener.onViewHidden(baseView.getElementName(), ViewTypeKey.CHECK_BOX);
            CheckBoxView checkBoxGroup = (CheckBoxView) baseView;
            checkBoxGroup.clearData();
            removeElementChild(baseView.getElementName());
//            Log.i(TAG, "removeElementData: "+baseView.getName());
        } else if (baseView instanceof RadioGoupView) {
            RadioGoupView radioGroupMaker = (RadioGoupView) baseView;
            radioGroupMaker.clearData();
            listener.onViewHidden(baseView.getElementName(), ViewTypeKey.RADIO_GROUP);
            removeElementChild(baseView.getElementName());
//            Log.i(TAG, "removeElementData: "+baseView.getName());
        }


    }

    private void removeElementChild(String elementName) {
        int index = hasChild(elementName);

        if (index < views.size() && index >= 0)
            hideView(views.get(index));

    }

    private int hasChild(String elementName) {

        for (int i = 0; i < views.size(); i++) {

            if (views.get(i).isElementIsVisibleSi()) {

                String VS_Name = views.get(i).getElementVisibleSiName();

                if (VS_Name.trim().equals(elementName.trim())) {

                    return i;

                }

            }

        }
        return -1;
    }

    private void removeElementConditions(String elementId) {

        ArrayList<JSONObject> conditionsArr = convert_JSONArray_to_ArrayList(conditions);
        for (int i = 0; i < conditionsArr.size(); i++) {

            try {
                if (conditionsArr.get(i).getString(conf_id).equals(elementId)) {
//                    Log.i(TAG, "removeElementConditions: id = "+elementId + " name = "+conditionsArr.get(i).getString(conf_name));
                    conditionsArr.remove(i);
                    i--;
                }
            } catch (JSONException e) {
                e.printStackTrace();
//                Log.i(TAG, "removeElementConditions: error = "+e.getMessage());
            }

        }
        conditions = convert_ArrayList_to_JSONArray(conditionsArr);
    }

    private boolean isIdExistInConditions(String visibleSiName, String visibleSiValueWrong) {
        String visibleSiValue = visibleSiValueWrong.replace("\"", "");
        for (int i = 0; i < conditions.length(); i++) {
            try {
                JSONObject condition = conditions.getJSONObject(i);
                String conditionName = condition.has(conf_name) ? condition.getString(conf_name).trim() : "";
                String conditionValue = condition.has(conf_value) ? condition.getString(conf_value).trim() : "";
                boolean conditionStatus = condition.has("status") && condition.getBoolean("status");
                if (conditionStatus) {
                    if (conditionName.equals(visibleSiName.trim()) && conditionValue.equals(visibleSiValue.trim())) {
                        return true;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < allConditions.length(); i++) {
            try {
                JSONObject condition = allConditions.getJSONObject(i);
                String conditionName = condition.getString(conf_name).trim();
                String conditionValue = condition.getString(conf_value).trim();
                boolean conditionStatus = condition.getBoolean("status");
                if (conditionStatus) {
                    if (conditionName.equals(visibleSiName.trim()) && conditionValue.equals(visibleSiValue.trim())) {
                        return true;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public void clear() {
        Animation removeAnim = AnimationUtils.loadAnimation(context, R.anim.slide_out_view);
        removeAnim.setFillAfter(false);
        removeAnim.setRepeatCount(0);
        removeAnim.setRepeatMode(Animation.RESTART);
        viewHolder.startAnimation(removeAnim);
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                viewHolder.removeAllViews();
            }
        }, 800);
    }

    //region update
    public void updateViewsStatus() {
        findImageFileConcept(views);
    }

    private void findImageFileConcept(ArrayList<BaseViewModel> views) {
        for (int i = 0; i < views.size(); i++) {
            if (views.get(i) instanceof ImageSectionButton) {
                ImageSectionButton imageFileConcept = (ImageSectionButton) views.get(i);
                imageFileConcept.checkMadatory(context);
                if (isImageFileConceptHasImage(context, imageFileConcept)) {
                    imageFileConcept.setHasPicStatus(context);
                    addEvenLog(context, pagePosition, context.getSharedPreferences(Config.sharedPreferencName, Context.MODE_PRIVATE)
                            .getString(Config.pictures, ""), "Set has picture", imageFileConcept.getElementId(), "");
                }
            }
        }
    }


    private boolean isImageFileConceptHasImage(Context context, ImageSectionButton imageFileConcept) {
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
            log(e.getMessage());
//            listener.onCheckListError(e.getMessage());
        }
        return false;
    }
    //endregion

    //region call backs

    @Override
    public void onAction(String name, String id, String data, int pagePosition) {
        if (PageView.pageStatus.PREVIEW != currentPageStatus) {
            ModuleLogEvent moduleLogEvent = new ModuleLogEvent(context, "", "", "", "", getDate(), getTime()
                    , "", "", "", pagePosition + " -> " + data + " Question id = " + id, "", name, "", ""
                    , 0);
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
    }

    @Override
    public void onConditionaryDataChanged(String name, String value, boolean isChecked, String type) {
        listener.onConditionaryDataChanged(name, value, isChecked, type);
        if (isHiddenViewExist) {
            for (BaseViewModel view : views) {
                if (view.isElementIsVisibleSi()) {
                    String viewVisibleSiName = view.getElementVisibleSiName();
                    String viewVisibleSiValue = view.getElementVisibleSiValue();

                    if (viewVisibleSiName.trim().equals(name.trim())) {//Check name
                        if (viewVisibleSiValue.trim().equals(value.trim())) {//Check value
                            if (isChecked)//if Check box is selected
                                showView(view);
                            else//if check box is unselected
                                hideView(view);
                        } else {
                            if (type.equals(ViewTypeKey.RADIO_GROUP))
                                hideView(view);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void isHiddenView() {
        isHiddenViewExist = true;
    }


    @Override
    public void onError(String err, ImagesViewer.ImageStatus errCode) {
        listener.onImageSliderError(err, errCode);
    }

    @Override
    public void onButtonClicked(JSONObject element) {
        try {
            listener.onCameraLoad();
            ActivityPicture.answerPictures = getPicsByPosition(pagePosition, picAnswers);
            Intent intent = new Intent(context
                    , Class.forName("com.example.checklist.Camera.ActivityPicture"));
            intent.putExtra(SavedPicturesFlag, picAnswers.toString());
            intent.putExtra("position", pagePosition);
            intent.putExtra("element", element.toString());

            Activity activity = (Activity) context;
            activity.startActivityForResult(intent, 111);


        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            log(e.getMessage());
            listener.onCheckListError(e.getMessage());
            addEvenLog(context, pagePosition, e.getMessage(), "Intenet error", "catch", "");
        }

    }

    @Override
    public void onClick(View v) {

        hideKeyboard((Activity) context);
//
        if (v == pre) {
            JSONArray data = getData();
            addEvenLog(context, pagePosition, "", "Back button clicked", "N/I", pageAnswers.toString());
            if (conditionListener != null) {
                conditionListener.onClearCondtionRecieved(conditions, pagePosition);
            }
            if (listener != null) {
                listener.onPreClicked(pagePosition, data);
            }
        }
        if (v == next) {
            JSONArray data = getData();
            if (conditionListener != null) {
                conditionListener.onConditionRecieved(conditions, pagePosition);
            }
            listener.onNextClicked(pagePosition, data);
        }
    }

    //endregion


    //region condition handler

    @Override
    public void onCheckBoxConditionChanged(JSONArray data, int position) {

    }

    @Override
    public void onRadioGroupConditionChanged(JSONObject data, int position) {
        addRadioCondition(data);
    }

    private void removeRadioConditionsWithId(String id) {
        ArrayList<JSONObject> conditions = convert_JSONArray_to_ArrayList(this.conditions);
        for (int i = 0; i < conditions.size(); i++) {
            JSONObject condition = conditions.get(i);
            try {
                if (condition.getString(conf_id).equals(id)) {
                    conditions.remove(i);
                    i--;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        this.conditions = convert_ArrayList_to_JSONArray(conditions);
    }


    private void addRadioCondition(JSONObject radioItem) {

        try {
            removeRadioConditionsWithId(radioItem.getString(conf_id));
            radioItem.put(conf_type, conf_radioButton);
            radioItem.put(conf_position, pagePosition);
            conditions.put(radioItem);
        } catch (JSONException e) {
            e.printStackTrace();
            log(e.getMessage());
        }

    }

    private void removeCheckBoxConditionsWithIdAnd(String id) {
        ArrayList<JSONObject> conditions = convert_JSONArray_to_ArrayList(this.conditions);
        for (int i = 0; i < conditions.size(); i++) {
            JSONObject condition = conditions.get(i);
            try {
                if (condition.getString(conf_id).equals(id)) {
                    conditions.remove(i);
                    i--;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        this.conditions = convert_ArrayList_to_JSONArray(conditions);
    }

    private void addCheckBoxConditions(JSONArray items, String id) {
        removeCheckBoxConditionsWithIdAnd(id);
        for (int i = 0; i < items.length(); i++) {

            try {
                JSONObject item = items.getJSONObject(i);
                item.put(conf_type, conf_checkBox);
                item.put(conf_position, pagePosition);
                item.put(conf_id, item.getString(conf_id));
                conditions.put(item);

            } catch (JSONException e) {
                e.printStackTrace();
                log(e.getMessage());
            }

        }
    }

    //endregion

    //region setter getter

    //setter
    public void setConditionListener(CheckListDataListener.CheckListConditionListener conditionListener) {
        this.conditionListener = conditionListener;
    }


    //getter
    public ArrayList<PicturePickerItemModel> getSignatures() {
        ArrayList<PicturePickerItemModel> pickerItemModels = new ArrayList<>();

        for (int i = 0; i < views.size(); i++) {

            if (views.get(i) instanceof SignatureView) {

                SignatureView signatureElement = (SignatureView) views.get(i);

                PicturePickerItemModel model = new PicturePickerItemModel();
                model.setPosition(pagePosition);
                model.setStatus(true);
                model.setId(signatureElement.getElementId());
                model.setPath("");//TODO path should be added from json answer
                model.setCat_id(12);
                model.setCategory("12");

                pickerItemModels.add(model);

            }

        }

        return pickerItemModels;

    }

    public void setConditions(JSONArray allConditions) {
        this.allConditions = allConditions;
    }

    //endregion

    //region public funcs
    public boolean isMandariesAnsweredInPage() {
        for (BaseViewModel view : views) {
            if (view.isShowen()) {
                if (!view.isMandatoryAnswered())
                    return false;
            }
        }
        return true;
    }

    public int getPosition() {
        return pagePosition;
    }

    public void checkMandatoriesAndChangeButtonStatus(boolean b) {

    }

    public void updateCommetarios(JSONArray ttData) {
        //TODO
    }

    public JSONArray getData() {
        JSONArray answers = new JSONArray();
        for (BaseViewModel view : views) {
            if (view != null) {
                if (view.isShowen()) {
                    if (view.getValue() != null)
                        answers.put(view.getValue());
                }
            }
        }
        return answers;
    }


    public void setButtons(View pre, View next, CheckListDataListener listDataListener) {

        this.pre = pre;
        this.next = next;
        this.listener = listDataListener;
        pre.setOnClickListener(this);
        next.setOnClickListener(this);
    }
    //endregion
}
