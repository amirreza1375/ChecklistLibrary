package com.example.checklist.ServerJsonGenerator;

import com.example.checklist.GlobalFuncs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.checklist.GlobalFuncs.conf_id;
import static com.example.checklist.GlobalFuncs.conf_productCount;
import static com.example.checklist.GlobalFuncs.conf_type;
import static com.example.checklist.GlobalFuncs.conf_value;
import static com.example.checklist.GlobalFuncs.convert_ArrayList_to_JSONArray;
import static com.example.checklist.GlobalFuncs.convert_JSONArray_to_ArrayList;
import static com.example.checklist.GlobalFuncs.log;

public class ServerJSONCleanUp {

    private String json;

    private String MultiTextServerName = "multitext";

    public ServerJSONCleanUp(String json){
        this.json = json;
    }

    //we should create json array with string that comes

    private JSONArray convertStringToJSONArray(String str){
        try {
            return new JSONArray(str);
        } catch (JSONException e) {
            e.printStackTrace();
            log(e.getMessage());
        }
        return new JSONArray();
    }

    //now we should parse json array and detect types
    //then send them to their own method to get
    //final json object

    public JSONArray cleanUpJSON(){
        JSONArray finalJSON = new JSONArray();
        JSONArray answers = convertStringToJSONArray(json);

        for (int i = 0 ; i < answers.length() ; i++){
            try {
                JSONObject answer = answers.getJSONObject(i);

                if (answer.getString(GlobalFuncs.conf_type)
                        .equals(GlobalFuncs.conf_checkBox)){//check box
                    //json array
                    JSONArray checks = cleanUpCheckBox(answer);
                    for (int j = 0 ; j < checks.length() ; j++){

                        finalJSON.put(checks.getJSONObject(j));
                    }
                    continue;
                }

                if (answer.getString(GlobalFuncs.conf_type)
                        .equals(GlobalFuncs.conf_radioButton)){//radio button
                    //json object
                    finalJSON.put(cleanUpRadioButton(answer));
                    continue;
                }

                if (answer.getString(GlobalFuncs.conf_type)
                        .equals(GlobalFuncs.conf_comment)){

                    finalJSON.put(cleanUpComment(answer));
                    continue;
                }
                if (answer.getString(GlobalFuncs.conf_type)
                        .equals(GlobalFuncs.conf_seekBar)){
                    finalJSON.put(cleanUpNouidlider(answer));
                    continue;
                }
                if (answer.getString(GlobalFuncs.conf_type)
                        .equals(GlobalFuncs.conf_file)){
                    finalJSON.put(cleanUpFile(answer));
                    continue;
                }
                if (answer.getString(GlobalFuncs.conf_type)
                        .equals(GlobalFuncs.conf_multiText)){
                    finalJSON.put(cleanUpMultiText(answer));
                }
                if (answer.getString(conf_type)
                        .equals(conf_productCount)){
                    JSONArray productAnswers = cleanUpProductCounter(answer);
                    for (int j = 0 ; j < productAnswers.length() ; j++){
                        finalJSON.put(productAnswers.getJSONObject(j));
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
                log(e.getMessage());
            }


        }
        return removeEmpryValues(finalJSON);

    }

    private JSONArray removeEmpryValues(JSONArray array){
        ArrayList<JSONObject> values = convert_JSONArray_to_ArrayList(array);

        for (int i = 0 ; i < values.size() ; i++){

            JSONObject value = values.get(i);

            try {
                if (value.getString(conf_id).equals("")) {

                    values.remove(i);

                    i--;

                }
            } catch (JSONException e) {
                e.printStackTrace();

                values.remove(i);

                i--;
            }

        }

        return convert_ArrayList_to_JSONArray(values);

    }

    private JSONArray cleanUpProductCounter(JSONObject answer) {
        try {
            return answer.getJSONArray(conf_value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }

    private JSONObject cleanUpMultiText(JSONObject answer) {

        JSONObject cleanedMultiText = new JSONObject();

        //position
        //id
        //type
        //values array

        try {
            JSONArray values = answer.getJSONArray(GlobalFuncs.conf_value);
            cleanedMultiText.put(GlobalFuncs.conf_position,answer.getInt(GlobalFuncs.conf_position));
            cleanedMultiText.put(GlobalFuncs.conf_type,MultiTextServerName);
            cleanedMultiText.put(GlobalFuncs.conf_id,answer.getString(GlobalFuncs.conf_id));
            //add child value array
            cleanedMultiText.put(GlobalFuncs.conf_value,values);
        } catch (JSONException e) {
            e.printStackTrace();
            log(e.getMessage());
        }

        return cleanedMultiText;
    }

    private JSONObject cleanUpFile(JSONObject answer) {
        return answer;
    }

    private JSONObject cleanUpNouidlider(JSONObject answer) {
        return answer;
    }

    private JSONObject cleanUpComment(JSONObject answer) {
        JSONObject finalCommentJSON = new JSONObject();
        int position;
        String type = GlobalFuncs.conf_comment;
        String id;

        try {
            position = answer.getInt(GlobalFuncs.conf_position);
            if (!answer.has(GlobalFuncs.conf_value)){
                return new JSONObject();
            }
            if (answer.getString(GlobalFuncs.conf_value).equals("")){
                return new JSONObject();
            }
            id = answer.getString(GlobalFuncs.conf_id);

            finalCommentJSON.put(GlobalFuncs.conf_position,position);
            finalCommentJSON.put(GlobalFuncs.conf_type,type);
            finalCommentJSON.put(GlobalFuncs.conf_id,id);

            finalCommentJSON.put(GlobalFuncs.conf_value,answer.getString(GlobalFuncs.conf_value));
            finalCommentJSON.put(GlobalFuncs.conf_name,answer.getString(GlobalFuncs.conf_name));


        } catch (JSONException e) {
            e.printStackTrace();
            log(e.getMessage());
        }

        return finalCommentJSON;
    }

    private JSONObject cleanUpRadioButton(JSONObject answer) {
        return answer;
    }

    private JSONArray cleanUpCheckBox(JSONObject object){
        JSONArray finalCheckJSON = new JSONArray();
        int position;
        String type = GlobalFuncs.conf_checkBox;
        String id,name;


        //get variables
        try {
            position = object.getInt(GlobalFuncs.conf_position);
            id = object.getString(GlobalFuncs.conf_id);
            name = object.getString(GlobalFuncs.conf_name);

            JSONArray checkValues = object.getJSONArray(GlobalFuncs.conf_value);
            for (int i = 0 ; i < checkValues.length() ; i++){

                JSONObject checkValue = checkValues.getJSONObject(i);

                if (checkValue.getBoolean("status")) {

                    JSONObject temp = new JSONObject();
                    temp.put(GlobalFuncs.conf_position, position);
                    temp.put(GlobalFuncs.conf_type, type);
                    temp.put(GlobalFuncs.conf_id, id);
                    temp.put(GlobalFuncs.conf_name, name);
                    temp.put(GlobalFuncs.conf_value, checkValue.getString(GlobalFuncs.conf_value));
                    temp.put(GlobalFuncs.conf_index, checkValue.getInt(GlobalFuncs.conf_index));

                    finalCheckJSON.put(temp);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            log(e.getMessage());
        }
        return finalCheckJSON;

    }

}
