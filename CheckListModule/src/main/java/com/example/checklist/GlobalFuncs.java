package com.example.checklist;

import android.Manifest;
import android.app.Activity;
import android.content.Context;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.checklist.CheckListGenerator.CheckListMaker;
import com.example.checklist.CheckListGenerator.PageView;
import com.example.checklist.Database.IDBResultView;
import com.example.checklist.Database.ModuleLogEvent;
import com.example.checklist.PageGenerator.CheckListPager;
import com.example.checklist.PictureElement.PicturePickerItemModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class GlobalFuncs {



    //region json keys

    public static String LOG = "CheckListMaker";

    public static String conf_index = "index";
    public static String conf_value = "value";
    public static String conf_title = "title";
    public static String conf_id = "id";
    public static String conf_name = "name";
    public static String conf_items = "items";
    public static String conf_isRequired = "isRequired";
    public static String conf_inputType = "inputType";
    public static String conf_maxLength = "maxLength";
    public static String conf_dropDown_choices = "choices";
    public static String conf_disableOthers = "disableOther";
    public static String conf_choices = "choices";
    public static String conf_text = "text";
    public static String conf_answer = "answer";
    public static String conf_type = "type";
    public static String conf_radioButton = "radiogroup";
    public static String conf_elements = "elements";
    public static String conf_checkBox = "checkbox";
    public static String conf_file = "file";
    public static String conf_seekBar = "nouislider";
    public static String conf_multiText = "multipletext";
    public static String conf_dropDown = "dropdown";
    public static String conf_rating = "rating";
    public static String conf_comment = "comment";
    public static String conf_optico = "Optico";
    public static String conf_accessory = "Accessories";
    public static String conf_layout = "Layout";
    public static String conf_DataBase = "Database";
    public static String conf_imagePicker = "imagepicker";
    public static String conf_resultIdes = "result_id";
    public static String conf_Posicion = "Posicion";
    public static String conf_Subcanal = "subcanal";
    public static String conf_Elemento = "Elemento";
    public static String conf_ID = "ID";
    public static String conf_pages = "pages";
    public static String conf_position = "position";
    public static String conf_visibileSi = "visibleIf";
    public static String conf_required = "isRequired";
    public static String conf_signature = "signaturepad";
    public static String conf_html = "html";
    public static String conf_htmlValue = "html";
    public static String conf_productId = "product_id";
    public static String conf_stock = "stock";
    public static String conf_tipo = "Tipo";
    public static String conf_shopId = "shop_id";
    public static String conf_productName = "product_name";
    public static String conf_productCount = "productCount";
    public static String conf_isAnswered = "isAnswered";
    public static String conf_val = "val";
    public static String conf_class = "class";
    public static String conf_valType = "valType";
    public static String conf_tipoNA = "Tipo NA";
    public static String conf_simpleText = "simpletext";
    public static String conf_rangeMax = "rangeMax";
    public static String conf_rangeMin = "rangeMin";
    public static String conf_systemId = "SystemId";


    //endregion

    public static int dpToPx(float dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static String getTitleFromElement(JSONObject element, boolean isRequired) {

        try {
            if (element.has("titleLocation")) {
                if (element.getString("titleLocation").equals("hidden")) {
                    return "";
                }
            }
            String title = element.has(conf_title) ? element.getString(conf_title) : "";
            if (title.equals("")) {
                title = element.getString(conf_name);
            }
            if (isRequired)
                return title + "*";
            return title;
        } catch (JSONException e) {
            e.printStackTrace();
            log(e.getMessage());
            return "no title";
        }
    }



    public static ArrayList<JSONObject> convert_JSONArray_to_ArrayList(JSONArray array) {
        ArrayList<JSONObject> objects = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            try {
                objects.add(array.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
                log(e.getMessage());
            }
        }
        return objects;
    }

    public static JSONArray convert_ArrayList_to_JSONArray(ArrayList<JSONObject> objects) {
        JSONArray array = new JSONArray();
        for (int i = 0; i < objects.size(); i++) {
            array.put(objects.get(i));
        }
        return array;
    }

    public static TextView createTitle(Context context, boolean isRequired, JSONObject element) {

        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                , LinearLayout.LayoutParams.WRAP_CONTENT);
        titleParams.setMargins(dpToPx(8, context), dpToPx(8, context)
                , dpToPx(8, context), dpToPx(0, context));


        TextView titleTxt = new TextView(context);
        titleTxt.setTextSize(16);
        titleTxt.setTextColor(Color.BLACK);
        titleTxt.setLayoutParams(titleParams);
        titleTxt.setText(getTitleFromElement(element, isRequired));

        return titleTxt;

    }

    public static void setOrgProps(Context context, LinearLayout org) {
        LinearLayout.LayoutParams orgParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                , LinearLayout.LayoutParams.WRAP_CONTENT);
        orgParams.setMargins(dpToPx(8, context), dpToPx(8, context)
                , dpToPx(8, context), dpToPx(8, context));
        org.setOrientation(LinearLayout.VERTICAL);
        org.setLayoutParams(orgParams);
    }

    public static JSONArray convert_PictureModel_to_JSONArrary(ArrayList<PicturePickerItemModel> models) {

        JSONArray array = new JSONArray();

        for (int i = 0; i < models.size(); i++) {
            if (models.get(i) != null) {
                JSONObject object = new JSONObject();
                try {
                    object.put(PicturePickerItemModel.conf_category, models.get(i).getCategory());
                    object.put(PicturePickerItemModel.conf_id, models.get(i).getId());
                    object.put(PicturePickerItemModel.conf_count, models.get(i).getCount());
                    object.put(PicturePickerItemModel.conf_hasPic, models.get(i).isHasPic());
                    object.put(PicturePickerItemModel.conf_catId, models.get(i).getCat_id());
                    object.put(PicturePickerItemModel.conf_path, models.get(i).getPath());
                    object.put(PicturePickerItemModel.conf_status, models.get(i).isStatus());
                    object.put(PicturePickerItemModel.conf_index, models.get(i).getIndex());
                    object.put(PicturePickerItemModel.conf_position, models.get(i).getPosition());
                    object.put(PicturePickerItemModel.conf_name, models.get(i).getName());

                    array.put(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                    log(e.getMessage());
                }
            }
        }
        return array;
    }

    public static ArrayList<PicturePickerItemModel> convert_JSONArray_to_PictureModel(JSONArray array) {

        ArrayList<PicturePickerItemModel> models = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {


            PicturePickerItemModel model = new PicturePickerItemModel();
            try {
                JSONObject object = array.getJSONObject(i);
                model.setCat_id(object.getInt(PicturePickerItemModel.conf_catId));
                model.setCategory(object.getString(PicturePickerItemModel.conf_category));
                model.setPath(object.getString(PicturePickerItemModel.conf_path));
                model.setCount(object.getInt(PicturePickerItemModel.conf_count));
                model.setIndex(object.getInt(PicturePickerItemModel.conf_index));
                model.setStatus(object.getBoolean(PicturePickerItemModel.conf_status));
                model.setHasPic(object.getBoolean(PicturePickerItemModel.conf_hasPic));
                model.setId(object.getString(PicturePickerItemModel.conf_id));
                model.setPosition(object.getInt(PicturePickerItemModel.conf_position));
                model.setName(object.getString(PicturePickerItemModel.conf_name));

                models.add(model);

            } catch (JSONException e) {
                e.printStackTrace();
                log(e.getMessage());
            }

        }

        return models;
    }

    public static void showToast(final Activity activity, final String message) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static ArrayList<Integer> convert_String_to_ArrayListInteger(String shops_str) {
        try {
            if (shops_str == null) {
                return new ArrayList();
            }
            if (shops_str.length() == 0) {
                return new ArrayList();
            }
            String[] shop_str_array = shops_str.split(",");
            ArrayList<Integer> shops = new ArrayList();
            for (String shop_id : shop_str_array) {
                shops.add(Integer.parseInt(shop_id));
            }
            return shops;
        } catch (Exception e) {
            e.printStackTrace();
            log(e.getMessage());
            return new ArrayList<Integer>();
        }
    }

    public static boolean checkStoragePermission(Context context) {
        return ActivityCompat.checkSelfPermission
                (context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }


    public static void log(String msg) {
        Log.i(LOG, "log -> " + msg);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private ArrayList<Character> convertArrayToArrayList(char[] chars) {
        ArrayList<Character> characters = new ArrayList<>();
        for (char c : chars) {
            characters.add(c);
        }
        return characters;
    }

    private String convertCharArrayToString(ArrayList<Character> characters) {
        String str = "";
        for (char c : characters) {
            str = str + c;
        }
        return str;
    }

    private String removeNumbersFromAdress(String adress) {

        char charArray[] = adress.toCharArray();

        ArrayList<Character> characters = convertArrayToArrayList(charArray);

        for (int i = characters.size() - 1; i >= 0; i--) {

            char c = characters.get(i);

            try {
                if (Character.isDigit(c)) {
                    characters.remove(i);
                    i++;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return convertCharArrayToString(characters);

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

    public static void addEvenLog(Context context,int pagePosition,String data,String name,String id,String pageAnswers){
        if (CheckListPager.pageStatus != PageView.pageStatus.PREVIEW) {
            ModuleLogEvent moduleLogEvent = new ModuleLogEvent(context, "", "", "", "", getDate(), getTime()
                    , "", "", "", pagePosition + " -> " + "Question id = " + id + data, ""
                    , name, pageAnswers, ""
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

}
