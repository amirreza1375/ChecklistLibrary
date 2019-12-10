package com.example.checklist.PageGenerator;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.example.checklist.CheckListGenerator.CheckListDataListener;
import com.example.checklist.CheckListGenerator.CheckListMaker;
import com.example.checklist.Config;
import com.example.checklist.FinishCheckList.CheckListFinishPage;
import com.example.checklist.ImageSliderModel;
import com.example.checklist.ImageSliderView.ImagesViewer;
import com.example.checklist.LayoutMaker.LayoutModel;
import com.example.checklist.PictureElement.PicturePickerItemModel;
import com.example.checklist.ProductCounter.ProductModel;
import com.example.checklist.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.checklist.Camera.ActivityPicture.getPicsFromSharedPreferences;
import static com.example.checklist.GlobalFuncs.conf_checkBox;
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
        , CheckListDataListener.CheckListConditionListener, CheckListFinishPage.FinishedPageActionListener {

    private static final String TAG = "CheckListPager";

    /**
     * new Variables
     */

    private HashMap<Integer, CheckListMaker> pagesByPosition;//have all data and not loose any page data
    private ArrayList<JSONObject> conditions;//find next page
    private ArrayList<Integer> pagePositionQueue;//filtering data at the end
    private int currentPagePosition = 0;//for having current page position

    /*********************/

    private CheckListMaker preCheckListMaker;
    private CheckListMaker tempCheckListMaker;

    private AlertDialog saveAsDraftDialog;
    private AlertDialog lastPageDialog;

    private int maxPostion = 0;

    private int userPagePosition = -1;

    private int prePosition;

    private JSONObject object;
    private View pre;
    private View next;
    private ArrayList<PicturePickerItemModel> picAnswers;
    public static CheckListMaker.pageStatus pageStatus;
    private JSONArray checkListAnswer;
    private ArrayList<ImageSliderModel> imageSliderModels;
    private String signatureFolderPath;
    private ArrayList<CheckListMaker> pages;
    //    private HashMap<Integer, CheckListMaker> pageKeyValue;

    private int pageQueuePointer = -1;
    private JSONArray pagesArray;
    private Context context;

    private CheckListMaker checkListMaker;
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

    //region cosntructors

    public CheckListPager(Context context, JSONObject Object, View pre, View next
            , ArrayList<PicturePickerItemModel> picAnswers, CheckListMaker.pageStatus pageStatus
            , JSONArray checkListAnswer, ArrayList<ImageSliderModel> imageSliderModels
            , String signatureFolderPath, CheckListListener listListener
            , int shopId, ArrayList<LayoutModel> layoutModels, int cachedPage, ArrayList<ProductModel> productModels
            ,String checklistSurverId,String appFolder,String picturesFolder) {
        super(context);
        this.context = context;
        this.object = Object;
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
//        pageKeyValue = new HashMap<>();
        getPagesArray(Object);
        init(context);

        createAllPages();
    }


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
        CheckListMaker firtPage = createPage(object, context, 0);
        if (firtPage != null) {
            addPageByPosition(firtPage);
        } else {
            listListener.CheckListHasError(context.getString(R.string.emptyPageCheckList));
        }


    }

    public void updateCheckListView() {
        if (checkListMaker != null)
            checkListMaker.updateViewsStatus();
    }

    private void setMaxPostion(int position) {
        if (position > maxPostion) {
            this.maxPostion = position;
        }
    }

    private void setUserPagePosition(CheckListMaker checkListMaker){
        this.userPagePosition = checkListMaker.getPosition();
    }

    private CheckListMaker createPage(JSONObject response, Context context, int position) {

        log("creating page -> " + position);
        try {
            checkListMaker = new CheckListMaker(context
                    , response.getJSONArray("pages")
                    .getJSONObject(currentPagePosition)
                    , pageStatus, currentPagePosition
                    , imageSliderModels, shopId, getPagePicAnswers(position, picAnswers)
                    , getPageAnswersByPosition(position)
                    , signatureFolderPath
                    , this
                    , layoutModels
                    ,productModels
                    ,checklistSurverId);
            checkListMaker.setConditionListener(this);
            checkListMaker.setButtons(pre, next, this);

            checkListMaker.checkMandatoriesAndChangeButtonStatus(false);
//            checkListMaker.setConditionListener(this);

            addView(checkListMaker);

            setMaxPostion(currentPagePosition);

            setUserPagePosition(checkListMaker);

            return checkListMaker;

        } catch (JSONException e) {
            e.printStackTrace();
            log(e.getMessage());
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
//                listListener.CheckListHasError(e.getMessage());
            }
        }

        return pageAnswers;

    }

    private void createAllPages(){
        if (pageStatus == CheckListMaker.pageStatus.DRAFT){
            if (cachedPage > 0) {
                addAnswerConditions(checkListAnswer);
                createPages();
                showCachedPage(getCachedPage());
            }

        }
    }

    private void showCachedPage(int cachedPagePosition) {
        CheckListMaker cachedPage = pagesByPosition.get(cachedPagePosition);
        if (cachedPage != null){
            showPage(cachedPage);
        }
    }

    private int getCachedPage() {
        return userPagePosition;
    }


    private void createPages() {
        for (int i = currentPagePosition + 1 ; i <= cachedPage ; i++){
            try {
                if (checkVisibleByOrder(pagesArray.getJSONObject(i))){
                    setCurrentPagePosition(i);
                    addPostionToQueue(i);
                    pagesByPosition.put(i,createPage(object,context,i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void addAnswerConditions(JSONArray answers) {

        for (int i = 0 ; i < answers.length() ; i++){

            try {
                JSONObject answer = answers.getJSONObject(i);

                if (answer.getString(conf_type)
                        .equals(conf_checkBox)){
                    setCheckboxCondition(answer);
                }

                if (answer.getString(conf_type)
                        .equals(conf_radioButton)){
                    setRadioButtonCondition(answer);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    private void setRadioButtonCondition(JSONObject answer) {
        try {
            JSONObject value = answer.getJSONObject(conf_value);
            conditions.add(value);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setCheckboxCondition(JSONObject answer) {
        try {
            JSONArray values = answer.getJSONArray(conf_value);
            for (int i = 0 ; i < values.length() ; i++){
                JSONObject value = values.getJSONObject(i);
                if (value.getBoolean(conf_status)){
                    conditions.add(value);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
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
     * and returns {@link CheckListMaker}
     * and if don't findes page shows last page
     * and returns Null
     *
     * @return
     */
    private CheckListMaker nextPage() {
        for (int i = currentPagePosition + 1; i < pagesArray.length(); i++) {
            try {
                if (checkVisibleByOrder(pagesArray.getJSONObject(i))) {
                    hideAllPages();
                    setCurrentPagePosition(i);
                    return createPage(object, context, i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                log(e.getMessage());
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
//                listListener.CheckListHasError(e.getMessage());
            }
        }
    }

    //when user press next add page to <int,CheckListMaker>
    private void addPageByPosition(CheckListMaker checkListMaker) {// -2-
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
            }
        }
        conditions.add(condition);
    }

    //show page and set buttons for this page
    private void showNextPage(CheckListMaker nextPage) {
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
        CheckListMaker prePage = pagesByPosition.get(previousPagePosition);
        if (prePage != null)
            showPage(prePage);

    }

    /********************************end of OnBackClicked************************************/


    /**
     * Global functions
     */

    private void showPage(CheckListMaker checkListMaker) {
        setUserPagePosition(checkListMaker);
        log("showing page -> " + checkListMaker.getPosition());
        setCurrentPagePosition(checkListMaker.getPosition());
        hideAllPages();
        checkListMaker.setButtons(pre, next, this);
        addView(checkListMaker);
        setCurrentPagePosition(checkListMaker.getPosition());
        updatePages(false);
    }

    private int getNextPagePosition() {

        for (int i = getCurrentPagePostion() + 1; i < pagesArray.length(); i++) {
            try {
                if (checkVisibleByOrder(pagesArray.getJSONObject(i))) {
                    log("next page is -> " + i);
                    return i;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                log(e.getMessage());
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
                hideAllPages();
                finishPage = new CheckListFinishPage(context, this);
                addView(finishPage);
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
                lastPageDialog.dismiss();

                listListener.closeChecklist();
            }
        });

        builder.setNegativeButton(context.getString(R.string.closeAlert), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                lastPageDialog.dismiss();

            }
        });

        return builder;

    }


    private void hideAllPages() {
        removeAllViews();
    }

    private boolean checkVisibleByOrder(JSONObject page) {

        String visiblePage = "visiblePage";

        if (page.has(visiblePage)) {
            try {
                if (page.getString(visiblePage).equals("")
                        || page.getString(visiblePage).equals("0")) {
                    Log.i(TAG, "checkVisibleByOrder: visible page value is = empty or 0");
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                log(e.getMessage());
                listListener.CheckListHasError(e.getMessage());
            }
        } else {
            Log.i(TAG, "checkVisibleByOrder: not visible page value");
            return true;
        }
        boolean FLAG_EXIST = false;
        String currentPageId = null;
        String currentPageValue = null;
        String showElemento = "n";

        try {
            String visiblePages[] = page.getString(visiblePage).split(":");
            currentPageId = visiblePages[0];
            currentPageValue = visiblePages[1];
        } catch (JSONException e) {
            e.printStackTrace();
            log(e.getMessage());
            listListener.CheckListHasError(e.getMessage());
        }
        for (int i = 0; i < conditions.size(); i++) {
            try {
                String tempId = conditions.get(i).getString("id");
                String tempValue = conditions.get(i).getString("value");
                if (currentPageValue.equals(showElemento)
                        && tempId.equals(currentPageId)) {
                    Log.i(TAG, "checkVisibleByOrder: condition is ok");
                    return true;
                }
                if (tempId.equals(currentPageId)
                        && tempValue.equals(currentPageValue)) {
                    FLAG_EXIST = true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                log(e.getMessage());
//                listListener.CheckListHasError(e.getMessage());
            }
        }
//        Log.i(TAG, "checkVisibleByOrder: no statment called , condition size = " + conditions.size());
        return FLAG_EXIST;
    }

    private void getPagesArray(JSONObject object) {
        try {
            pagesArray = object.getJSONArray(conf_pages);
        } catch (JSONException e) {
            e.printStackTrace();
            log(e.getMessage());
            listListener.CheckListHasError(e.getMessage());
        }
    }

    private void updatePages(boolean isNextClicked) {  // Importatnt func
        log("pages updated");
        for (int i = 0; i < maxPostion; i++) {
            CheckListMaker temp = pagesByPosition.get(i);

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

        if (pageStatus == CheckListMaker.pageStatus.PREVIEW) {

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
                saveAsDraftDialog.dismiss();
                if (pageStatus != CheckListMaker.pageStatus.PREVIEW)
                    listListener.SaveAsDraft(getAllData(), convert_JSONArray_to_PictureModel(getPicsFromSharedPreferences(context)), false, getSignatures());
                else
                    listListener.CheckListMessage(context.getString(R.string.notSaveInPreview));

            }
        });

        builder.setNegativeButton(context.getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listListener.StopFromSaving();
                saveAsDraftDialog.dismiss();
                listListener.closeChecklist();
            }
        });

        return builder;


    }


    public void updateMandatory() {
        updatePages(false);
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
        if (pageStatus != CheckListMaker.pageStatus.PREVIEW)
            listListener.instantSaveCall(getAllData(), convert_JSONArray_to_PictureModel(getPicsFromSharedPreferences(context)), isAppClosed, getSignatures(),userPagePosition);
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

            CheckListMaker page = pagesByPosition.get(position);
            if (page != null) {
                JSONArray temp = page.getData(true);

                for (int j = 0; j < temp.length(); j++) {

                    try {
                        checkListAnswer.put(temp.getJSONObject(j));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        log(e.getMessage());
                    }

                }
            }

        }
        return checkListAnswer;
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
        log("Next button clicked");
        //first check if we have page with this position
        int nextPagePosition = getNextPagePosition();
        CheckListMaker nextPage = pagesByPosition.get(nextPagePosition);
        if (nextPage != null) {//next page is avilable
            log("found cache page");
            showNextPage(nextPage);
            setCurrentPagePosition(nextPagePosition);
            addPostionToQueue(position);
        } else {
            log("not cache page");//if not do the rest
            CheckListMaker checkListMaker = nextPage();//find next page
//            log("Created page -> "+checkListMaker.getPosition());
            if (checkListMaker != null) {//if page exist
                log(" - " + checkListMaker.getPosition() + " created page");
                addPageByPosition(checkListMaker);//add to key,value
                setCurrentPagePosition(checkListMaker.getPosition());
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
    }


    @Override
    public void onCheckListError(String error) {
//        listListener.CheckListHasError(error);
        listListener.ChecklistErrorMessage(error);
    }

    @Override
    public void onImageSliderError(String err, ImagesViewer.ImageStatus errCode) {
        listListener.ImageSliderError(err,errCode);

    }

    @Override
    public void onCameraLoad() {
        listListener.StopFromSaving();
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
        if (pageStatus != CheckListMaker.pageStatus.PREVIEW)
            listListener.SaveAsFinished(getAllData(), convert_JSONArray_to_PictureModel(getPicsFromSharedPreferences(context)), getSignatures());
        else
            listListener.CheckListMessage(context.getString(R.string.notSaveInPreview));
    }

    @Override
    public void onBackCheckListClicked() {
        //remove finish page and show last checklist page
        listListener.onBackClicked();
        removeAllViews();
        if (pagePositionQueue.size() > 0) {
            int prePosition = pagePositionQueue.get(pagePositionQueue.size() - 1);
            addView(pagesByPosition.get(prePosition));
        }
    }

    @Override
    public void onSaveAsDraftClicked() {
        //get all data and clear sharedpreferences picture
        if (pageStatus != CheckListMaker.pageStatus.PREVIEW)
            listListener.SaveAsDraft(getAllData(), convert_JSONArray_to_PictureModel(getPicsFromSharedPreferences(context)), false, getSignatures());
        else
            listListener.CheckListMessage(context.getString(R.string.notSaveInPreview));
    }

    private ArrayList<PicturePickerItemModel> getSignatures() {

        ArrayList<PicturePickerItemModel> models = new ArrayList<>();

        for (int i = 0; i < pagePositionQueue.size(); i++) {

            int position = pagePositionQueue.get(i);

            CheckListMaker page = pagesByPosition.get(position);

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

    public CheckListListener getListListener() {
        return listListener;
    }

    /***************************   end of Events   ********************************/


    public interface CheckListListener {
        void SaveAsDraft(JSONArray array, ArrayList<PicturePickerItemModel> pics, boolean isAppClosed, ArrayList<PicturePickerItemModel> signatures);

        void SaveAsFinished(JSONArray array, ArrayList<PicturePickerItemModel> pics, ArrayList<PicturePickerItemModel> signatures);

        void Finished();

        void onBackClicked();

        void CheckListHasError(String error);

        void closeChecklist();

        void StopFromSaving();

        void ChecklistErrorMessage(String msg);

        void instantSaveCall(JSONArray array, ArrayList<PicturePickerItemModel> pics, boolean isAppClosed, ArrayList<PicturePickerItemModel> signatures,int userPagePosition);

        void CheckListMessage(String msg);

        void ImageSliderError(String err, ImagesViewer.ImageStatus errCode);


    }

}
