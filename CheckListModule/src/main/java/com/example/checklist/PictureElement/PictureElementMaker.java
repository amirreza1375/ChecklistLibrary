package com.example.checklist.PictureElement;

import android.content.Context;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.checklist.Config;
import com.example.checklist.GlobalFuncs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.checklist.GlobalFuncs.conf_id;

public class PictureElementMaker extends LinearLayout implements PicturesRecyclerView.ItemClickCallBack {

    //region element keys
    private String imagetypeName = "imagetypeName";
    private String imageTypeCount = "count";
    private String imagetype = "imagetype";
    //endregion

    private boolean FLAG_ENABLE = true;
    private JSONArray answers;
    private int position;
    ArrayList<PicturePickerItemModel> models ;
    private RecyclerView recyclerView;
    private JSONObject element;
    private boolean status;
    private String id;
    private TakePictureItemClickListener mlistener;

    public PictureElementMaker(Context context, JSONObject element, boolean status
    , boolean FLAG_ENABLE, JSONArray answers,int position) {
        super(context);
        this.element = element;
        this.status = status;
        this.FLAG_ENABLE = FLAG_ENABLE;
        this.answers = answers;
        this.position = position;
        models = new ArrayList<>();
        init(context);
    }

    public PictureElementMaker(Context context,  AttributeSet attrs) {
        super(context, attrs);
    }

    public PictureElementMaker(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private void init(Context context){

        getVariablesFromElement(element);

        //region title
        TextView titleTxt = new TextView(context);
        titleTxt.setText(getTitleFromElement(element));
        //endregion

        //region recycler props
        recyclerView = new RecyclerView(context);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        params.setMargins(dpToPx(8,context),dpToPx(8,context),dpToPx(8,context),dpToPx(8,context));
        recyclerView.setLayoutParams(params);
        //endregion
        addView(recyclerView);


        setupRecyclerData(element);
        setAnswerImages(answers);
        initRecycler(context,this);
//        updateRecycler();

    }

    private void setAnswerImages(JSONArray answers) {
        ArrayList<PicturePickerItemModel> answerModels = GlobalFuncs.convert_JSONArray_to_PictureModel(answers);
        for (int i = 0 ; i < answerModels.size() ; i++){
            PicturePickerItemModel answerModel = answerModels.get(i);
            //we should check id first
            if (id.equals(answerModel.getId())) {//then check index
                for (int j = 0; j < models.size(); j++) {
                    PicturePickerItemModel model = models.get(j);
                    if (answerModel.getIndex() == model.getIndex()) {
                        models.get(j).setPath(answerModel.getPath());
                    }
                }
            }
        }
        updateRecycler();
    }

    private String getTitleFromElement(JSONObject element) {
        String conf_title = "title";
        try {
            return element.getString(conf_title);
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    private void setupRecyclerData(JSONObject element) {
        try {
            String name = element.has(Config.name) ? element.getString(Config.name) : "no name";
            String imageTypeNames[] = element.getString(imagetypeName).split(",");
            String imageTypes[] = element.getString(imagetype).split(",");
            int countIndex = 0;
            for (int i = 0 ; i < imageTypeNames.length ; i++){//names
                //get count of that type
                String nameCountStr = element.has(imageTypeCount+"-"+imageTypeNames[i]) ? element.getString(imageTypeCount+"-"+imageTypeNames[i]) : "1";
                int nameCount = Integer.parseInt(nameCountStr);
                int countLastIndex = 0;
                for (int j = 0 ; j < nameCount ; j++){//count of type
                    PicturePickerItemModel model = new PicturePickerItemModel();
                    model.setIndex(countIndex + j);
                    model.setCategory(imageTypeNames[i]);
                    model.setCat_id(Integer.parseInt(imageTypes[i]));
                    model.setStatus(status);
                    model.setId(id);
                    model.setPosition(position);
                    model.setName(name);


                    models.add(model);

                    countLastIndex = j + 1;//set j as last time every time

                }//end of types count

                countIndex = countLastIndex;//add last j to index

            }//end of names

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getModels(){
        mlistener.onModelsAdded(models);
    }

    private void initRecycler(Context context , LinearLayout parent) {
        PicturesRecyclerView recyclerViewAdapter = new PicturesRecyclerView(context
                ,parent,models);
        recyclerView.setLayoutManager(new GridLayoutManager(context,2));
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.setCallBack(this);
        recyclerViewAdapter.setFLAG_ENABLED(FLAG_ENABLE);
    }
    public void updateRecycler(){
        if (recyclerView.getAdapter() != null)
            recyclerView.getAdapter().notifyDataSetChanged();
    }
    public static int dpToPx(float dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    private void getVariablesFromElement(JSONObject element){
        try {
            id = element.getString(conf_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPictureItemClicked(PicturesRecyclerView.ViewHolder holder,PicturePickerItemModel model) {
        mlistener.onPictureItemClicked(holder,model);

    }

    public TakePictureItemClickListener getMlistener() {
        return mlistener;
    }

    public void setMlistener(TakePictureItemClickListener mlistener) {
        this.mlistener = mlistener;
    }

    public boolean isFLAG_ENABLE() {
        return FLAG_ENABLE;
    }

    public void setFLAG_ENABLE(boolean FLAG_ENABLE) {
        this.FLAG_ENABLE = FLAG_ENABLE;
    }

    public interface TakePictureItemClickListener{
        void onPictureItemClicked(PicturesRecyclerView.ViewHolder holder, PicturePickerItemModel model);
        void onModelsAdded(ArrayList<PicturePickerItemModel> models);
    }

}
