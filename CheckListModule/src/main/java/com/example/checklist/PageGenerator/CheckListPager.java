package com.example.checklist.PageGenerator;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.checklist.BaseViewModel.ViewTypeKey;
import com.example.checklist.CheckListGenerator.CheckListDataListener;
import com.example.checklist.CheckListGenerator.PageView;
import com.example.checklist.Config;
import com.example.checklist.Database.ModuleLogEvent;
import com.example.checklist.FinishCheckList.CheckListFinishPage;
import com.example.checklist.ImageSliderModel;
import com.example.checklist.ImageSliderView.ImagesViewer;
import com.example.checklist.LayoutMaker.LayoutModel;
import com.example.checklist.PictureElement.PicturePickerItemModel;
import com.example.checklist.ProductCounter.ProductModel;
import com.example.checklist.R;
import com.example.checklist.TimeTracker.TimeTracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static com.example.checklist.Camera.ActivityPicture.getPicsFromSharedPreferences;
import static com.example.checklist.Config.TIME_TRACKER_KEY;
import static com.example.checklist.GlobalFuncs.addEvenLog;
import static com.example.checklist.GlobalFuncs.conf_checkBox;
import static com.example.checklist.GlobalFuncs.conf_name;
import static com.example.checklist.GlobalFuncs.conf_pages;
import static com.example.checklist.GlobalFuncs.conf_position;
import static com.example.checklist.GlobalFuncs.conf_radioButton;
import static com.example.checklist.GlobalFuncs.conf_type;
import static com.example.checklist.GlobalFuncs.conf_value;
import static com.example.checklist.GlobalFuncs.convert_ArrayList_to_JSONArray;
import static com.example.checklist.GlobalFuncs.convert_JSONArray_to_ArrayList;
import static com.example.checklist.GlobalFuncs.convert_JSONArray_to_PictureModel;
import static com.example.checklist.GlobalFuncs.convert_PictureModel_to_JSONArrary;
import static com.example.checklist.GlobalFuncs.showToast;
import static com.example.checklist.PictureElement.PicturePickerItemModel.conf_status;

public class CheckListPager extends LinearLayout implements CheckListDataListener
        , CheckListDataListener.CheckListConditionListener, CheckListFinishPage.FinishedPageActionListener
        , View.OnClickListener {

    private static final String TAG = "CheckListPager";

    /**
     * new Variables
     */

    private ArrayList<JSONObject> visibleSiData;

    private ArrayList<String> errors;

    private JSONArray TTData;

    private HashMap<Integer, PageView> pagesByPosition;//have all data and not loose any page data
    private ArrayList<JSONObject> conditions;//find next page
    private ArrayList<Integer> pagePositionQueue;//filtering data at the end
    private int currentPagePosition = 0;//for having current page position

    /*********************/

    private PageView preCheckListMaker;
    private PageView tempCheckListMaker;

    private AlertDialog saveAsDraftDialog;
    private AlertDialog lastPageDialog;

    private int maxPostion = 0;

    private int userPagePosition = -1;

    private int prePosition;
    private ArrayList<Integer> createdPagesPositions;

    private JSONObject checklistObject;
    private ViewGroup buttonHolder;
    private View pre;
    private View next;
    private ArrayList<PicturePickerItemModel> picAnswers;
    public static PageView.pageStatus pageStatus;
    private JSONArray checkListAnswer;
    private ArrayList<ImageSliderModel> imageSliderModels;
    private String signatureFolderPath;
    private ArrayList<PageView> pages;
    //    private HashMap<Integer, CheckListMaker> pageKeyValue;

    private int pageQueuePointer = -1;
    private JSONArray pagesArray;
    private Context context;

    private PageView checkListMaker;
    private CheckListFinishPage finishPage;
    private CheckListListener listListener;
    private int shopId;
    private ArrayList<LayoutModel> layoutModels;

    private int cachedPage;
    private ArrayList<ProductModel> productModels;
    private String checklistSurverId;
    public static String appFolder;
    public static String picturesFolder;

    public static boolean setMandatories = true;


    private LinearLayout TimeTracker;
    private AlertDialog timesAlert;
    private Button cancelBtn;
    private Button saveBtn;
    private LinearLayout timesContainer;
    private com.example.checklist.TimeTracker.TimeTracker timeTracker;


    //region cosntructors

    public CheckListPager(Context context, JSONObject checklistObject, ViewGroup buttonHolder, View pre, View next
            , ArrayList<PicturePickerItemModel> picAnswers, PageView.pageStatus pageStatus
            , JSONArray checkListAnswer, ArrayList<ImageSliderModel> imageSliderModels
            , String signatureFolderPath, CheckListListener listListener
            , int shopId, ArrayList<LayoutModel> layoutModels, int cachedPage, ArrayList<ProductModel> productModels
            , String checklistSurverId, String appFolder, String picturesFolder) {
        super(context);
        this.context = context;
        this.checklistObject = checklistObject;
        this.buttonHolder = buttonHolder;
        this.pre = pre;
        this.next = next;
        this.picAnswers = picAnswers;
        this.pageStatus = pageStatus;
        this.checkListAnswer = checkListAnswer;
        this.imageSliderModels = imageSliderModels;
        this.signatureFolderPath = signatureFolderPath;
        this.listListener = listListener;
        this.shopId = shopId;
        this.layoutModels = layoutModels;
        this.cachedPage = cachedPage;
        this.productModels = productModels;
        this.checklistSurverId = checklistSurverId;
        this.appFolder = appFolder;
        this.picturesFolder = picturesFolder;
        pages = new ArrayList<>();
        pagesByPosition = new HashMap<>();
        conditions = new ArrayList<>();
        pagePositionQueue = new ArrayList<>();
        TTData = new JSONArray();
        createdPagesPositions = new ArrayList<>();
        errors = new ArrayList<>();
        visibleSiData = new ArrayList<>();
//        pageKeyValue = new HashMap<>();
        getPagesArray(checklistObject);
        InitilizeTimeTrackerDialog();
        setDraftVisibleSiData(checkListAnswer);
        init(context);
        setLastAnsweredPagePosition(checkListAnswer);
        createAllPages();
    }

    private void setDraftVisibleSiData(JSONArray checkListAnswer) {

        for (int i = 0 ; i < checkListAnswer.length() ; i ++){

            try {

                JSONObject answer = checkListAnswer.getJSONObject(i);
               //-------------------------------------------------------------------------------- TYPE RADIO BUTTON

                if (answer.getString(conf_type).equals(conf_radioButton)){
                    String name = answer.getString(conf_name);
                    String value = answer.getString(conf_value);
                    insertIntoVisibleSiData(name,value,-1,true);

                }
                //-------------------------------------------------------------------------------- TYPE CHECK BOX

                else if (answer.getString(conf_type).equals(conf_checkBox)){

                    JSONArray values = answer.getJSONArray(conf_value);

                    String name = answer.getString(conf_name);

                    for (int j = 0 ; j < values.length() ; j++){

                        JSONObject value = values.getJSONObject(j);
                        if (value.getBoolean(conf_status)) {


                            String subValue = value.getString(conf_value);

                            insertIntoVisibleSiData(name, subValue, -1, true);
                        }

                    }

                }
                //-------------------------------------------------------------------------------- DONE
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    //region TimeTracker

    private void updateAllCommentariosInAllPages() {
        for (int i = 0; i <= maxPostion; i++) {

            PageView checkListMaker = pagesByPosition.get(i);

            if (checkListMaker != null) {
                checkListMaker.updateCommetarios(TTData);
            }

        }
    }


    private TimeTracker createTimeTrackerView() {
        JSONObject object = new JSONObject();
        try {
            object = checklistObject.getJSONObject(TIME_TRACKER_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
            listListener.TimeTrackerStatus(false);
        }
        listListener.TimeTrackerStatus(true);

        return new TimeTracker(context, object, new TimeTracker.TimeTrackerListener() {
            @Override
            public void onTimeTackerElementError() {
                listListener.TimeTrackerStatus(false);
            }
        }, checkListAnswer);
    }


    private void InitilizeTimeTrackerDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            Activity activity = (Activity) context;
            LayoutInflater inflater = activity.getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_time_tracker, this, false);

            //cast
            timesContainer = view.findViewById(R.id.timesContainer);
            cancelBtn = view.findViewById(R.id.cancelBtn);
            saveBtn = view.findViewById(R.id.saveBtn);

            timeTracker = createTimeTrackerView();
            timesContainer.addView(timeTracker);

            cancelBtn.setOnClickListener(this);
            saveBtn.setOnClickListener(this);
            builder.setCancelable(false);
            builder.setView(view);
            timesAlert = builder.create();
        } catch (Exception e) {
            e.printStackTrace();
            timeTracker = createTimeTrackerView();
            timesContainer.addView(timeTracker);
        }

    }


    //endregion


    public CheckListPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckListPager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    //endregion

    private void init(Context context) {

        //region org props
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT
                , LayoutParams.MATCH_PARENT);
        setLayoutParams(params);
        setOrientation(VERTICAL);
        //endregion

        addPostionToQueue(0);
        PageView firstPage = createPage(checklistObject, context, 0);
        if (firstPage != null) {
            addPageByPosition(firstPage);
        } else {
            listListener.CheckListHasError(context.getString(R.string.emptyPageCheckList));
        }


    }

    public void updateCheckListView() {
        for (int i = 0; i < pagePositionQueue.size(); i++) {
            PageView page = pagesByPosition.get(pagePositionQueue.get(i));
            if (page != null) {
                page.updateViewsStatus();
            }
        }
        PageView page = pagesByPosition.get(currentPagePosition);
        if (page != null) {
            page.updateViewsStatus();
        }
    }

    private void setMaxPostion(int position) {
        if (position > maxPostion) {
            this.maxPostion = position;
        }
    }

    private void setUserPagePosition(PageView checkListMaker) {
        this.userPagePosition = checkListMaker.getPosition();
    }

    private PageView createPage(JSONObject response, Context context, int position) {

        try {
            checkListMaker = new PageView(context
                    , response.getJSONArray("pages")
                    .getJSONObject(position)
                    , pageStatus, position
                    , imageSliderModels, shopId, getPagePicAnswers(position, picAnswers)
                    , getPageAnswersByPosition(position)
                    , signatureFolderPath
                    , this
                    , layoutModels
                    , productModels
                    , checklistSurverId
                    , convert_ArrayList_to_JSONArray(conditions));
            checkListMaker.setConditionListener(this);
            checkListMaker.setButtons(pre, next, this);

            checkListMaker.checkMandatoriesAndChangeButtonStatus(false);

//            checkListMaker.setConditionListener(this);
            hideAllPagesAddPage(checkListMaker);
            Log.i(TAG, "View added 3: ");

            setMaxPostion(position);

            setUserPagePosition(checkListMaker);

            checkListMaker.setVisibleSiFromOtherePages(visibleSiData);

            return checkListMaker;

        } catch (JSONException e) {
            e.printStackTrace();
            log(e.getMessage());
            errors.add(e.getMessage());
//            listListener.CheckListHasError(e.getMessage());
        }
        return null;
    }

    private JSONArray getPageAnswersByPosition(int position) {
        JSONArray pageAnswers = new JSONArray();

        for (int i = 0; i < checkListAnswer.length(); i++) {
            try {
                JSONObject answer = checkListAnswer.getJSONObject(i);
                if (answer.getInt("position")
                        == position) {
                    pageAnswers.put(checkListAnswer.getJSONObject(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                log(e.getMessage());
                errors.add(e.getMessage());
//                listListener.CheckListHasError(e.getMessage());
            }
        }

        return pageAnswers;

    }

    private void createAllPages() {
        if (pageStatus == PageView.pageStatus.DRAFT) {
            if (cachedPage > 0) {
                addAnswerConditions(checkListAnswer);
                createPages();
                showCachedPage(getCachedPage());
            } else {
                createDraftPages();
            }

        }
    }

    private void createDraftPages() {

        for (int i = 0; i < checkListAnswer.length(); i++) {

            try {
                JSONObject answer = checkListAnswer.getJSONObject(i);
                int position = answer.has(conf_position) ? answer.getInt(conf_position) : 0;
                if (!isCreated(position)) {
                    Log.i(TAG, "createDraftPages: " + position);
                    pagesByPosition.put(position, createPage(checklistObject, context, position));
                    createdPagesPositions.add(position);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                errors.add(e.getMessage());
            }

        }
        showPage(pagesByPosition.get(0));

    }

    private boolean isCreated(int position) {
        if (position == 0)
            return true;
        for (Integer pagePosition : createdPagesPositions) {
            if (pagePosition == position)
                return true;
        }
        return false;
    }

    private void showCachedPage(int cachedPagePosition) {
        PageView cachedPage = pagesByPosition.get(cachedPagePosition);
        if (cachedPage != null) {
            showPage(cachedPage);
        }
    }

    private int getCachedPage() {
        return userPagePosition;
    }


    private void createPages() {
        for (int i = currentPagePosition + 1; i <= cachedPage; i++) {
            try {
                if (isPageAllowedToShowByOrder(pagesArray.getJSONObject(i))) {
                    setCurrentPagePosition(i);
                    addPostionToQueue(i);
                    Log.i(TAG, "createPages: " + i);
                    pagesByPosition.put(i, createPage(checklistObject, context, i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                errors.add(e.getMessage());
            }
        }
    }

    private void addAnswerConditions(JSONArray answers) {

        for (int i = 0; i < answers.length(); i++) {

            try {
                JSONObject answer = answers.getJSONObject(i);

                if (answer.getString(conf_type)
                        .equals(conf_checkBox)) {
                    setCheckboxCondition(answer);
                }

                if (answer.getString(conf_type)
                        .equals(conf_radioButton)) {
                    setRadioButtonCondition(answer);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                errors.add(e.getMessage());
            }

        }

    }

    private void setRadioButtonCondition(JSONObject answer) {
        try {
            JSONObject value = answer.getJSONObject(conf_value);
            conditions.add(value);

        } catch (JSONException e) {
            e.printStackTrace();
            errors.add(e.getMessage());
        }
    }

    private void setCheckboxCondition(JSONObject answer) {
        try {
            JSONArray values = answer.getJSONArray(conf_value);
            for (int i = 0; i < values.length(); i++) {
                JSONObject value = values.getJSONObject(i);
                if (value.getBoolean(conf_status)) {
                    conditions.add(value);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errors.add(e.getMessage());
        }
    }

    private JSONArray getPagePicAnswers(int position, ArrayList<PicturePickerItemModel> pictures) {

        ArrayList<PicturePickerItemModel> temp = new ArrayList<>();

        for (int i = 0; i < pictures.size(); i++) {
            if (pictures.get(i).getPosition() == position) {
                temp.add(pictures.get(i));
            }
        }
        return convert_PictureModel_to_JSONArrary(temp);
    }

    /**
     * this function searchs for next page by conditions
     * and returns {@link PageView}
     * and if don't findes page shows last page
     * and returns Null
     *
     * @return
     */
    private PageView nextPage() {
        for (int i = currentPagePosition + 1; i < pagesArray.length(); i++) {
            try {
                if (isPageAllowedToShowByOrder(pagesArray.getJSONObject(i))) {
                    setCurrentPagePosition(i);
                    return createPage(checklistObject, context, i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                log(e.getMessage());
                errors.add(e.getMessage());
//                listListener.CheckListHasError(e.getMessage());
            }
        }
        showLastPage();
        return null;
    }

    /**
     *         **    New functions   **
     */

    /**
     * OnNextClicked Methods
     */

    //add pages conditions to condition by filtering element type
    private void addConditions(JSONArray conditions) {// -1-
        for (int i = 0; i < conditions.length(); i++) {
            try {
                JSONObject condition = conditions.getJSONObject(i);
                if (condition.getString(conf_type)
                        .equals(conf_checkBox)) {
                    if (condition.getBoolean("status")) {
                        addToConditions(this.conditions, condition);
                    } else {
                        removeFromConditions(this.conditions, condition);
                    }
                } else if (condition.getString(conf_type)
                        .equals(conf_radioButton)) {
                    removeOthersFromCondition(condition);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                log(e.getMessage());
                errors.add(e.getMessage());
//                listListener.CheckListHasError(e.getMessage());
            }
        }
    }

    //when user press next add page to <int,CheckListMaker>
    private void addPageByPosition(PageView checkListMaker) {// -2-
        Log.i(TAG, "addPageByPosition: " + checkListMaker.getPosition());
        this.pagesByPosition.put(checkListMaker.getPosition(), checkListMaker);
    }

    //when user press next add page position in queue
    private void addPostionToQueue(int position) {// -3-
        boolean FLAG_EXIST = false;
        for (int i = 0; i < this.pagePositionQueue.size(); i++) {
            if (pagePositionQueue.get(i) == position) {
                FLAG_EXIST = true;
            }
        }
        if (!FLAG_EXIST) {
            log("position added -> " + position);
            this.pagePositionQueue.add(position);
        }
    }

    //get current page position
    private int getCurrentPagePostion() {// -4-
        return this.currentPagePosition;
    }

    //set current page
    private void setCurrentPagePosition(int position) {
        this.currentPagePosition = position;
    }

    //if checkbox has true status add conditions
    private void addToConditions(ArrayList<JSONObject> conditions, JSONObject condition) {
        boolean FLAG_EXIST = false;
        for (int i = 0; i < conditions.size(); i++) {

            try {
                if (condition.getString("id")
                        .equals(conditions.get(i).getString("id"))
                        && condition.getString("value")
                        .equals(conditions.get(i).getString("value"))) {
                    FLAG_EXIST = true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                log(e.getMessage());
                errors.add(e.getMessage());
//                listListener.CheckListHasError(e.getMessage());
            }
        }
        if (!FLAG_EXIST) {
            this.conditions.add(condition);
        }
    }

    //if checkbox has false status remove from conditions
    private void removeFromConditions(ArrayList<JSONObject> conditions, JSONObject condition) {

        for (int i = 0; i < conditions.size(); i++) {
            try {
                if (condition.getString("id")
                        .equals(conditions.get(i).getString("id"))
                        && condition.getString("value")
                        .equals(conditions.get(i).getString("value"))) {
                    this.conditions.remove(i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                log(e.getMessage());
                errors.add(e.getMessage());
//                listListener.CheckListHasError(e.getMessage());
            }

        }
    }

    //if radiobutton index changed remove others and add new one
    private void removeOthersFromCondition(JSONObject condition) {
        for (int i = 0; i < conditions.size(); i++) {
            JSONObject cond = conditions.get(i);
            try {
                if (cond.getString("id")
                        .equals(condition.getString("id"))) {
                    conditions.remove(i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                log(e.getMessage());
                errors.add(e.getMessage());
            }
        }
        conditions.add(condition);
    }

    //show page and set buttons for this page
    private void showNextPage(PageView nextPage) {
        showPage(nextPage);
    }
    /********************************end of OnNextClicked*************************************/


    /**
     * OnBackClicked Methods
     */
    //remove page condition when user press back
    private void removeConditionsByPosition(int position) {
        for (int i = 0; i < conditions.size(); i++) {
            try {
                if (conditions.get(i).getInt("position") == position) {
                    this.conditions.remove(i);
                    i--;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                log(e.getMessage());
                errors.add(e.getMessage());
//                listListener.CheckListHasError(e.getMessage());
            }
        }
    }

    //get previous page posiotn
    private int getPreviousPagePosition() {
        if (pagePositionQueue.size() > 1) {
            log("previous page is -> " + pagePositionQueue.get(pagePositionQueue.size() - 1));
            return pagePositionQueue.get(pagePositionQueue.size() - 1);
        }
        if (pagePositionQueue.size() > 0) {
            return pagePositionQueue.get(pagePositionQueue.size() - 1);
        }
        log("page queue size is < 1 - previous page is 0");
        return 0;
    }

    //remove pageQueue last index when user press back
    private void removeFromPageQueue() {
        log("page queue size ->" + pagePositionQueue.size());
        if (pagePositionQueue.size() > 1) {
            log("remove index -> " + (pagePositionQueue.size() - 1));
            log("remove value -> " + pagePositionQueue.get(pagePositionQueue.size() - 1));
            pagePositionQueue.remove(pagePositionQueue.size() - 1);
        }
    }

    //show previous page by the last position saved
    private void showPreviousPage() {
        int previousPagePosition = getPreviousPagePosition();//get pre page position
        PageView prePage = pagesByPosition.get(previousPagePosition);
        if (prePage != null) {
            showPage(prePage);
            prePage.checkMandatoriesAndChangeButtonStatus(false);
            prePage.setVisibleSiFromOtherePages(visibleSiData);
        }

    }

    /********************************end of OnBackClicked************************************/


    /**
     * Global functions
     */

    private void showPage(PageView checkListMaker) {
        setUserPagePosition(checkListMaker);
        log("showing page -> " + checkListMaker.getPosition());
        setCurrentPagePosition(checkListMaker.getPosition());
        checkListMaker.setButtons(pre, next, this);
        hideAllPagesAddPage(checkListMaker);
        Log.i(TAG, "View added 4: ");
        setCurrentPagePosition(checkListMaker.getPosition());
//        updatePages(false);
    }

    private int getNextPagePosition() {

        for (int i = getCurrentPagePostion() + 1; i < pagesArray.length(); i++) {
            try {
                if (isPageAllowedToShowByOrder(pagesArray.getJSONObject(i))) {
                    log("next page is -> " + i);
                    return i;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                log(e.getMessage());
                errors.add(e.getMessage());
            }
        }
        log("no next page -> (-1)");
        return -1;
    }

    private void showLastPage() {
        switch (pageStatus) {
            case PREVIEW:
                showLastPageAlert();
                break;
            default:

                finishPage = new CheckListFinishPage(context, this);
                hideAllPagesAddPage(finishPage);
                Log.i(TAG, "View added 1: ");
                if (listListener != null) {
                    listListener.Finished();

                } else {
                    showToast((Activity) context, "You must Impelement CheckListListener");
                }
                break;
        }

    }

    private void showLastPageAlert() {

        AlertDialog.Builder builder = createLastPageAlertBuilder();

        lastPageDialog = builder.create();

        lastPageDialog.show();
    }

    private AlertDialog.Builder createLastPageAlertBuilder() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);


        builder.setMessage(context.getString(R.string.previewLastPageMessage));
        builder.setCancelable(true);

        builder.setPositiveButton(context.getString(R.string.closeCheckList), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //close dialog
                addEvenLog(context, -1, "Last page alert", "Positive", "N/I", "");
                lastPageDialog.dismiss();
                listListener.closeChecklist();
            }
        });

        builder.setNegativeButton(context.getString(R.string.closeAlert), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addEvenLog(context, -1, "Last page alert", "Negative", "N/I", "");
                lastPageDialog.dismiss();
            }
        });

        return builder;

    }


    private void hideAllPagesAddPage(final ViewGroup checkListMaker) {
        Log.i(TAG, "hideAllPages: ");
        Activity activity = (Activity) context;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                removeAllViews();
                addView(checkListMaker);
            }
        });

    }

    private boolean isPageAllowedToShowByOrder(JSONObject page) {

        String visiblePage = "visiblePage";
        boolean FLAG_EXIST = false;
        String currentPageId = "";
        String currentPageValue = "";
        String showElemento = "n";

        if (!page.has(visiblePage)) {
            addEvenLog(context, -1, "Has not value", "Visible page", "N/I", "");
            return true;
        }

        try {
            String[] visiblePageConditions = page.getString(visiblePage).split(":");
            if (visiblePageConditions.length >= 2) {
                currentPageId = visiblePageConditions[0];
                currentPageValue = visiblePageConditions[1];
            } else {
                addEvenLog(context, -1, "Value is -> : ", "Visible page", "N/I", "");
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errors.add(e.getMessage());
            addEvenLog(context, -1, e.getMessage(), "Visible page", "N/I", "");
            return true;
        }

        for (int i = 0; i < conditions.size(); i++) {
            try {
                String tempId = conditions.get(i).getString("id");
                String tempValue = conditions.get(i).getString("value");
                if (currentPageValue.equals(showElemento)
                        && tempId.equals(currentPageId)) {
                    Log.i(TAG, "checkVisibleByOrder: condition is ok");
                    addEvenLog(context, -1, "Value match : " + i, "Visible page", tempId + ":" + tempValue, "");
                    return true;
                }
                if (tempId.equals(currentPageId)
                        && tempValue.equals(currentPageValue)) {
                    addEvenLog(context, -1, "Value match : " + i, "Visible page", currentPageId + ":" + currentPageValue, "");
                    FLAG_EXIST = true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                log(e.getMessage());
                errors.add(e.getMessage());
                addEvenLog(context, -1, e.getMessage(), "Visible page", "N/I", "");
            }
        }
        return FLAG_EXIST;

    }

//    private boolean checkVisibleByOrder(JSONObject page) {
//
//        String visiblePage = "visiblePage";
//
//        if (page.has(visiblePage)) {
//            try {
//                if (page.getString(visiblePage).equals("")
//                        || page.getString(visiblePage).equals("0")) {
//                    Log.i(TAG, "checkVisibleByOrder: visible page value is = empty or 0");
//                    return true;
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//                log(e.getMessage());
//                listListener.CheckListHasError(e.getMessage());
//            }
//        } else {
//            Log.i(TAG, "checkVisibleByOrder: not visible page value");
//            return true;
//        }
//        boolean FLAG_EXIST = false;
//        String currentPageId = null;
//        String currentPageValue = null;
//        String showElemento = "n";
//
//        try {
//            String[] visiblePages = page.getString(visiblePage).split(":");
//            if (visiblePages.length >= 2) {
//                currentPageId = visiblePages[0];
//                currentPageValue = visiblePages[1];
//            }else{
//                return true;
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//            log(e.getMessage());
//            listListener.CheckListHasError(e.getMessage());
//        }
//        for (int i = 0; i < conditions.size(); i++) {
//            try {
//                String tempId = conditions.get(i).getString("id");
//                String tempValue = conditions.get(i).getString("value");
//                if (currentPageValue.equals(showElemento)
//                        && tempId.equals(currentPageId)) {
//                    Log.i(TAG, "checkVisibleByOrder: condition is ok");
//                    return true;
//                }
//                if (tempId.equals(currentPageId)
//                        && tempValue.equals(currentPageValue)) {
//                    FLAG_EXIST = true;
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//                log(e.getMessage());
////                listListener.CheckListHasError(e.getMessage());
//            }
//        }
////        Log.i(TAG, "checkVisibleByOrder: no statment called , condition size = " + conditions.size());
//        return FLAG_EXIST;
//    }

    private void getPagesArray(JSONObject object) {
        try {
            pagesArray = object.getJSONArray(conf_pages);
        } catch (JSONException e) {
            e.printStackTrace();
            log(e.getMessage());
            errors.add(e.getMessage());
            listListener.CheckListHasError(e.getMessage());
        }
    }

    private void updatePages(boolean isNextClicked) {  // Importatnt func
        log("pages updated");
        for (int i = 0; i < maxPostion; i++) {
            PageView temp = pagesByPosition.get(i);

            if (temp != null)
                temp.checkMandatoriesAndChangeButtonStatus(isNextClicked);
        }
        if (pagesByPosition.get(currentPagePosition) != null) {
            pagesByPosition.get(currentPagePosition).checkMandatoriesAndChangeButtonStatus(isNextClicked);
        }
    }

    public void setListListener(CheckListListener listListener) {
        this.listListener = listListener;
    }


    private void showSaveAlert() {

        if (pageStatus == PageView.pageStatus.PREVIEW) {

            listListener.closeChecklist();

        } else {

            AlertDialog.Builder builder = initializeSaveBuilder();

            saveAsDraftDialog = builder.create();

            saveAsDraftDialog.show();
        }
    }

    private AlertDialog.Builder initializeSaveBuilder() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);


        builder.setMessage(context.getString(R.string.save_to_draft_msg));
        builder.setCancelable(true);

        builder.setPositiveButton(context.getString(R.string.log_out_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //close dialog
                listListener.showSavingLoad();
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        addEvenLog(context, -1, "Yes", "Save data alert", "N/I", "");
                        saveAsDraftDialog.dismiss();
                        if (pageStatus != PageView.pageStatus.PREVIEW) {
                            JSONArray data = getAllDataDraft();
                            ArrayList<PicturePickerItemModel> picturePickerItemModels = convert_JSONArray_to_PictureModel(getPicsFromSharedPreferences(context));
                            addEvenLog(context, -1, "Yes", "Save data alert", "N/I", data.toString() + " pics count = " + picturePickerItemModels.size());
                            listListener.LogEvent(new ModuleLogEvent(context).getAllItems());
                            new ModuleLogEvent(context).dropAndCreateTable();

                            listListener.SaveAsDraft(data, picturePickerItemModels, false, getSignatures(), errors);
                        } else
                            listListener.CheckListMessage(context.getString(R.string.notSaveInPreview));

                    }
                }).start();

            }
        });

//        builder.setNegativeButton(context.getString(R.string.no), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                addEvenLog(context, -1, "No", "Save data alert", "N/I", "");
//                listListener.LogEvent(new ModuleLogEvent(context).getAllItems());
//                new ModuleLogEvent(context).dropAndCreateTable();
//                listListener.onError("User pressed no to save data");
//                listListener.StopFromSaving();
//                saveAsDraftDialog.dismiss();
//                listListener.closeChecklist();
//            }
//        });

        return builder;


    }

    private void addError(String error) {
        errors.add(error + " -" + getDate() + "-" + getTime());
    }

    private void setLastAnsweredPagePosition(JSONArray answers) {
        try {
            for (int i = 0; i < answers.length(); i++) {
                JSONObject answer = answers.getJSONObject(i);
                setMaxPostion(answer.getInt(conf_position));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            errors.add(e.getMessage());
        }
    }

    public static String getDate() {
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        String date = dateFormat.format(today).replace("/", "-");
        return date;
    }

    public static String getTime() {
        Date now = new Date();
        SimpleDateFormat sdfDatee = new SimpleDateFormat("HH:mm");//dd/MM/yyyy
        String time = sdfDatee.format(now);
        return time;
    }


    public void updateMandatory() {
//        updatePages(false);
    }

    public boolean isSetMandatories() {
        return setMandatories;
    }

    public void setSetMandatories(boolean setMandatories) {
        this.setMandatories = setMandatories;
    }

    public void showSaveAsDraftMessage() {
        showSaveAlert();
    }

    public void instantSaveCheckList(boolean isAppClosed) {
        JSONArray data = getAllDataDraft();
        ArrayList<PicturePickerItemModel> picturePickerItemModels = convert_JSONArray_to_PictureModel(getPicsFromSharedPreferences(context));
        if (pageStatus != PageView.pageStatus.PREVIEW) {
            addEvenLog(context, -1, "picture count = " + picturePickerItemModels.size()
                    + " datas = " + data, "Save as finished", "N/I", "");
            listListener.LogEvent(new ModuleLogEvent(context).getAllItems());
            new ModuleLogEvent(context).dropAndCreateTable();
            listListener.instantSaveCall(data, picturePickerItemModels, isAppClosed, getSignatures(), userPagePosition, errors);
        }
//        else
//            listListener.CheckListMessage(context.getString(R.string.notSaveInPreview));
    }


    /********************************end of Global functions************************************/


    /********************************end of New Functions************************************/


    private void removeCurrentPageImages(int position) {
        String picStr = context.getSharedPreferences(Config.sharedPreferencName, Context.MODE_PRIVATE).getString(Config.pictures, "");
        if (picStr.equals("")) {
            picStr = "[]";
        }
        try {
            JSONArray pics = new JSONArray(picStr);
            ArrayList<JSONObject> picsArray = convert_JSONArray_to_ArrayList(pics);
            for (int i = 0; i < picsArray.size(); i++) {
                if (picsArray.get(i).getInt("position") == position) {
                    picsArray.remove(i);
                    i--;
                }
            }
            SharedPreferences.Editor editor = context.getSharedPreferences(Config.sharedPreferencName, Context.MODE_PRIVATE).edit();
            editor.putString(Config.pictures, String.valueOf(convert_ArrayList_to_JSONArray(picsArray))).apply();
        } catch (JSONException e) {
            e.printStackTrace();
            log(e.getMessage());
            errors.add(e.getMessage());
        }
    }


//    public JSONArray getValue() {//TODO must be get data by passed positions
//        JSONArray array = new JSONArray();
//        for (int i = 0; i < pagePositionQueue.size(); i++) {
//            CheckListMaker page = pagesByPosition.get(pagePositionQueue.get(i));
//            if (page != null) {
//                JSONArray array1 = page.getData();
//                for (int j = 0; j < array1.length(); j++) {
//                    try {
//                        array.put(array1.getJSONObject(j));
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                        listListener.CheckListHasError(e.getMessage());
//                    }
//                }
//            }
//        }
//        return array;
//    }

    //here we are getting all data of checklist - what ever user chose
    private JSONArray getAllData() {
//        JSONArray allData = new JSONArray();
        pagePositionQueue.add(userPagePosition);
        for (int i = 0; i < pagePositionQueue.size(); i++) {
            int position = pagePositionQueue.get(i);
            removeDataWithPostion(position);

            PageView page = pagesByPosition.get(position);
            if (page != null) {
                JSONArray temp = page.getData();

                for (int j = 0; j < temp.length(); j++) {

                    try {
                        checkListAnswer.put(temp.getJSONObject(j));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        log(e.getMessage());
                        addEvenLog(context, -1, e.getMessage(), "Get data error", "N/I", "");
                        errors.add(e.getMessage());
                    }

                }
            }

        }
        return checkListAnswer;
    }

    private JSONArray getAllDataDraft() {
        JSONArray allData = new JSONArray();

        for (int i = 0; i < maxPostion + 1; i++) {

            PageView page = pagesByPosition.get(i);
            if (page != null) {
                JSONArray temp = page.getData();

                for (int j = 0; j < temp.length(); j++) {

                    try {
                        allData.put(temp.getJSONObject(j));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        log(e.getMessage());
                        errors.add(e.getMessage());
                    }

                }
            }

        }
        return allData;
    }

    private void removeDataWithPostion(int position) {

        ArrayList<JSONObject> datas = convert_JSONArray_to_ArrayList(checkListAnswer);

        for (int i = 0; i < datas.size(); i++) {

            JSONObject data = datas.get(i);

            try {
                if (data.getInt(conf_position)
                        == position) {
                    datas.remove(i);
                    i--;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                errors.add(e.getMessage());
            }

        }

        checkListAnswer = convert_ArrayList_to_JSONArray(datas);

    }

    private void log(String message) {
        Log.i(TAG, "log: -> " + message);
    }


    /**
     * ***     Events    ***
     */

    @Override
    public void onNextClicked(int position, JSONArray datas) {
        addEvenLog(context, -1, datas.toString(), "OnNextClicked", "N/I", position + "");
        log("Next button clicked");
        //first check if we have page with this position
        int nextPagePosition = getNextPagePosition();
        PageView nextPage = pagesByPosition.get(nextPagePosition);
        if (nextPage != null) {//next page is avilable
            log("found cache page");
            nextPage.setConditions(convert_ArrayList_to_JSONArray(conditions));
            nextPage.updateCommetarios(TTData);
            showNextPage(nextPage);
            setCurrentPagePosition(nextPagePosition);
            addPostionToQueue(position);
            nextPage.checkMandatoriesAndChangeButtonStatus(false);
            nextPage.setVisibleSiFromOtherePages(visibleSiData);
            listListener.onPageChanged(1);
        } else {
            log("not cache page");//if not do the rest
            PageView checkListMaker = nextPage();//find next page
//            log("Created page -> "+checkListMaker.getPosition());
            if (checkListMaker != null) {//if page exist
                checkListMaker.setConditions(convert_ArrayList_to_JSONArray(conditions));
                checkListMaker.updateCommetarios(TTData);
                log(" - " + checkListMaker.getPosition() + " created page");
                addPageByPosition(checkListMaker);//add to key,value
                setCurrentPagePosition(checkListMaker.getPosition());
                checkListMaker.setVisibleSiFromOtherePages(visibleSiData);
                checkListMaker.checkMandatoriesAndChangeButtonStatus(false);
                listListener.onPageChanged(1);
            }
            addPostionToQueue(position);//add position of page to queue to know use passed this page
        }
//        addPostionToQueue(nextPagePosition);
//        log(nextPagePosition+"");

    }


    @Override
    public void onPreClicked(int position, JSONArray datas) {
        log("Previous button clicked");
        showPreviousPage();
        removeFromPageQueue();
        listListener.onPageChanged(-1);
    }


    @Override
    public void onCheckListError(String error) {
//        listListener.CheckListHasError(error);
        listListener.ChecklistErrorMessage(error);
    }

    @Override
    public void onImageSliderError(String err, ImagesViewer.ImageStatus errCode) {
        listListener.ImageSliderError(err, errCode);

    }

    @Override
    public void onCameraLoad() {
        listListener.StopFromSaving();
    }

    @Override
    public void onChecklistLoadStarted() {
        listListener.onShowChecklistLoad();
    }

    @Override
    public void onChecklistLoadFinished() {
        listListener.onHideCehcklistLoad();
    }

    @Override
    public void onConditionaryDataChanged(String name, String value, boolean isChecked, String type) {

        int index = searchInVisibleSiArray(visibleSiData,name,value,type);

        if (index != -1) {

            if (type.equals(ViewTypeKey.RADIO_GROUP)) {
                //search in array to find it if found change if not insert

                insertIntoVisibleSiData(name,value,index,true);

            } else {
                //if is false search and find it and change

                //if is true can be first time search if found change if not insert
                insertIntoVisibleSiData(name,value,index,isChecked);
            }
        }else{

            insertIntoVisibleSiData(name,value,index,true);

        }

    }

    @Override
    public void onViewHidden(String name, String type) {
        for (int i = 0 ; i < visibleSiData.size() ; i++){

            JSONObject object = visibleSiData.get(i);

            try {
                String elementName = object.getString(conf_name);

                if (name.trim().equals(elementName.trim())){
                    visibleSiData.remove(i);
                    i--;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


    private void insertIntoVisibleSiData(String name , String value,int index,boolean isChecked){
        JSONObject object = new JSONObject();

        try {
            object.put(conf_name, name);
            object.put(conf_value, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (index < 0) {
            visibleSiData.add(object);
        }else{
            if (isChecked) {
                visibleSiData.set(index, object);
            }else{
                visibleSiData.remove(index);
            }
        }

    }

    private int searchInVisibleSiArray(ArrayList<JSONObject> visibleSiData,String currentName,String currentValue,String type){

        for (int i = 0 ; i < visibleSiData.size() ; i++){

            JSONObject object = visibleSiData.get(i);

            try {
                String name = object.getString(conf_name);
                String value = object.getString(conf_value);

                if (type.equals(ViewTypeKey.RADIO_GROUP)){
                    if (name.equals(currentName)){
                        return i;
                    }
                }else {
                    if (name.equals(currentName)
                    && value.equals(currentValue)){
                        return i;
                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }


    @Override
    public void onConditionRecieved(JSONArray conditions, int position) {
        addConditions(conditions);
    }

    @Override
    public void onClearCondtionRecieved(JSONArray removedConditions, int position) {
        removeConditionsByPosition(position);
    }


    @Override
    public void onFinishClicked() {
        //get all data and clear sharedpreferences pictures
        listListener.showSavingLoad();
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateCheckListView();
                if (pageStatus != PageView.pageStatus.PREVIEW) {
                    int NotAnsweredPagePosition = getNotAnsweredMandatoryPagePosition();
                    if (NotAnsweredPagePosition == -1) {
                        ArrayList<PicturePickerItemModel> picturePickerItemModels = convert_JSONArray_to_PictureModel(getPicsFromSharedPreferences(context));
                        ArrayList<PicturePickerItemModel> signatures = getSignatures();
                        JSONArray datas = getAllData();
                        addEvenLog(context, -1, "picture count = " + picturePickerItemModels.size()
                                + " signature count = " + signatures.size()
                                + " datas = " + datas, "Save as finished", "N/I", "");
                        listListener.LogEvent(new ModuleLogEvent(context).getAllItems());
                        new ModuleLogEvent(context).dropAndCreateTable();
                        listListener.SaveAsFinished(datas, picturePickerItemModels, signatures, errors);
                    } else {
//                errors.add("User didn't answer question in page with position = "+NAPos);
                        listListener.onError("User didn't answer question in page with position = " + NotAnsweredPagePosition);
                        getAllData();
                        openFirstPageNotAnswered(NotAnsweredPagePosition);
                    }
                } else {
//            errors.add(context.getString(R.string.notSaveInPreview));
                    listListener.onError(context.getString(R.string.notSaveInPreview));
                    listListener.CheckListMessage(context.getString(R.string.notSaveInPreview));
                }
            }
        }).start();
    }

    private void openFirstPageNotAnswered(int position) {
        for (int i = 0; i < pagePositionQueue.size(); i++) {
            if (pagePositionQueue.get(i) >= position) {
                pagePositionQueue.remove(i);
                i--;
            }
        }
        PageView page = pagesByPosition.get(position);
        if (page != null) {
            Activity activity = (Activity) context;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    buttonHolder.setVisibility(VISIBLE);
                    pre.setVisibility(VISIBLE);
                    next.setVisibility(VISIBLE);
                    listListener.onHideCehcklistLoad();
                }
            });
            showPage(page);
            page.setMandatoryErrors();
        }
    }

    private int getNotAnsweredMandatoryPagePosition() {
        int firstNotAnsweredPagePosition = -1;
        for (int i = 0; i < pagePositionQueue.size(); i++) {

            PageView page = pagesByPosition.get(pagePositionQueue.get(i));
            if (page != null) {
                if (!page.isMandariesAnsweredInPage()) {
                    if (firstNotAnsweredPagePosition == -1) {
                        firstNotAnsweredPagePosition = page.getPosition();
                    } else {
                        if (firstNotAnsweredPagePosition > page.getPosition()) {
                            firstNotAnsweredPagePosition = page.getPosition();
                        }
                    }
                }
            }

        }
        addEvenLog(context, -1, "Not answered page = " + firstNotAnsweredPagePosition, "Not answered", "N/I", "");
        return firstNotAnsweredPagePosition;
    }

    @Override
    public void onBackCheckListClicked() {
        //remove finish page and show last checklist page
        listListener.onBackClicked();

        if (pagePositionQueue.size() > 0) {
            int prePosition = pagePositionQueue.get(pagePositionQueue.size() - 1);
            hideAllPagesAddPage(pagesByPosition.get(prePosition));
            Log.i(TAG, "View added 2: ");
        }
    }

    @Override
    public void onSaveAsDraftClicked() {
        listListener.showSavingLoad();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //get all data and clear sharedpreferences picture
                if (pageStatus != PageView.pageStatus.PREVIEW) {
                    ArrayList<PicturePickerItemModel> picturePickerItemModels = convert_JSONArray_to_PictureModel(getPicsFromSharedPreferences(context));
                    ArrayList<PicturePickerItemModel> signatures = getSignatures();
                    JSONArray datas = getAllDataDraft();
                    addEvenLog(context, -1, "picture count = " + picturePickerItemModels.size()
                            + " signature count = " + signatures.size()
                            + " datas = " + datas, "Save as draft", "N/I", "");
                    listListener.LogEvent(new ModuleLogEvent(context).getAllItems());
                    new ModuleLogEvent(context).dropAndCreateTable();
                    listListener.SaveAsDraft(datas, picturePickerItemModels, false, signatures, errors);
                } else {
                    listListener.CheckListMessage(context.getString(R.string.notSaveInPreview));
                }

            }
        }).start();
    }

    private ArrayList<PicturePickerItemModel> getSignatures() {

        ArrayList<PicturePickerItemModel> models = new ArrayList<>();

        for (int i = 0; i < pagePositionQueue.size(); i++) {

            int position = pagePositionQueue.get(i);

            PageView page = pagesByPosition.get(position);

            if (page != null) {

                ArrayList<PicturePickerItemModel> temp = page.getSignatures();

                for (int j = 0; j < temp.size(); j++) {
                    if (temp.get(j).getPath() != null && temp.get(j).getName() != null)
                        models.add(temp.get(j));

                }


            }

        }

        return models;

    }

    private void dismissDialogHandler(final AlertDialog alertDialog) {
        if (alertDialog != null) {
            Activity activity = (Activity) context;
            if (!activity.isFinishing()) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        alertDialog.dismiss();
                    }
                });
            }
        }
    }

    public void showDialogHandler(final AlertDialog alertDialog) {
        if (alertDialog != null) {
            Activity activity = (Activity) context;
            if (!activity.isFinishing()) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        alertDialog.show();
                    }
                });
            }
        }
    }


    public CheckListListener getListListener() {
        return listListener;
    }

    @Override
    public void onClick(View view) {
        if (view == cancelBtn) {
            addEvenLog(context, -1, "", "Cancel button", "N/I", "");
            dismissDialogHandler(timesAlert);
        }
        if (saveBtn == view) {
            addEvenLog(context, -1, "", "Save button", "N/I", "");
            TTData = timeTracker.getElementValue();
//            checkListMaker.updateCommetarios(TTData);
            updateAllCommentariosInAllPages();
            dismissDialogHandler(timesAlert);
            Log.i(TAG, "onClick: " + TTData);
        }

    }

    public void showTimeTracker() {
        showDialogHandler(timesAlert);
        timesAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }

    /***************************   end of Events   ********************************/


    public interface CheckListListener {
        void SaveAsDraft(JSONArray array, ArrayList<PicturePickerItemModel> pics, boolean isAppClosed, ArrayList<PicturePickerItemModel> signatures, ArrayList<String> errors);

        void SaveAsFinished(JSONArray array, ArrayList<PicturePickerItemModel> pics, ArrayList<PicturePickerItemModel> signatures, ArrayList<String> errors);

        void Finished();

        void onBackClicked();

        void CheckListHasError(String error);

        void closeChecklist();

        void StopFromSaving();

        void ChecklistErrorMessage(String msg);

        void instantSaveCall(JSONArray array, ArrayList<PicturePickerItemModel> pics, boolean isAppClosed, ArrayList<PicturePickerItemModel> signatures, int userPagePosition, ArrayList<String> errors);

        void CheckListMessage(String msg);

        void ImageSliderError(String err, ImagesViewer.ImageStatus errCode);

        void onPageChanged(int mode);


        void TimeTrackerStatus(boolean status);

        void onError(String error);

        void LogEvent(ArrayList<ModuleLogEvent> moduleLogEvents);

        void onShowChecklistLoad();

        void onHideCehcklistLoad();

        void showSavingLoad();
    }

}
