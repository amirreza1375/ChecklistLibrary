package com.example.checklist.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.checklist.DBHelper.DBContract;
import com.example.checklist.DBHelper.LogEventDBHelperModule;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;



public class ModuleLogEvent extends DBModelBaseTemp<ModuleLogEvent> {

    private Context context;

    private long id;
    private String appVersion, token, user, internet, date, time, phone, oldVersion
            , newVersion, serverData, checklistJson, eventName, answerJson, syncData;
    private int isSynced;

    public ModuleLogEvent(Context context){
        this.context = context;
    }

    public ModuleLogEvent(Context context,String appVersion, String token, String user, String internet, String date, String time, String phone, String oldVersion, String newVersion, String serverData, String checklistJson, String eventName, String answerJson, String syncData, int isSynced) {
        this.context = context;
        this.appVersion = appVersion;
        this.token = token;
        this.user = user;
        this.internet = internet;
        this.date = date;
        this.time = time;
        this.phone = phone;
        this.oldVersion = oldVersion;
        this.newVersion = newVersion;
        this.serverData = serverData;
        this.checklistJson = checklistJson;
        this.eventName = eventName;
        this.answerJson = answerJson;
        this.syncData = syncData;
        this.isSynced = isSynced;
    }

    @Override
    public SQLiteDatabase dropAndCreateTable() {
        SQLiteDatabase db = LogEventDBHelperModule.getInstance(context).getWritableDatabase();
        db.execSQL(DBContract.LogEventModuleTalbe.SQL_DELETE_ENTRIES);
        db.execSQL(DBContract.LogEventModuleTalbe.SQL_CREATE_ENTRIES);
        return db;
    }

    private SQLiteDatabase getDataBaseConnection(){
        return LogEventDBHelperModule.getInstance(context).getWritableDatabase();
    }

    public void getNotSyncedJsonArray(final LogMonitorDataListener callBack){
        getAllNotSyncedItems(new IDBArrayResultView<ModuleLogEvent>() {
            @Override
            public void onSuccess(ArrayList<ModuleLogEvent> results) {
                callBack.onDataRecieved(getJSONArrayFromArrayListLogMonitor(results));
            }

            @Override
            public void onFail(String error) {

            }
        });
    }

    public JSONArray getJSONArrayFromArrayListLogMonitor(ArrayList<ModuleLogEvent> moduleLogEvents) {
        JSONArray datas = new JSONArray();
        for (int i = 0; i < moduleLogEvents.size(); i++) {

            JSONObject data = new JSONObject();

            ModuleLogEvent moduleLogEvent = moduleLogEvents.get(i);

            try {
                data.put(DBContract.LogEventModuleTalbe._ID, moduleLogEvent.getId());
                data.put(DBContract.LogEventModuleTalbe.APP_VERIOSN, moduleLogEvent.getAppVersion());
                data.put(DBContract.LogEventModuleTalbe.TOKEN, moduleLogEvent.getToken());
                data.put(DBContract.LogEventModuleTalbe.USER, moduleLogEvent.getUser());
                data.put(DBContract.LogEventModuleTalbe.INTERNET, moduleLogEvent.getInternet());
                data.put(DBContract.LogEventModuleTalbe.DATE, moduleLogEvent.getDate());
                data.put(DBContract.LogEventModuleTalbe.TIME, moduleLogEvent.getTime());
                data.put(DBContract.LogEventModuleTalbe.PHONE, moduleLogEvent.getPhone());
                data.put(DBContract.LogEventModuleTalbe.OLD_VERSION, moduleLogEvent.getOldVersion());
                data.put(DBContract.LogEventModuleTalbe.NEW_VERSION, moduleLogEvent.getNewVersion());
                data.put(DBContract.LogEventModuleTalbe.SERVER_DATA, moduleLogEvent.getAppVersion());
                data.put(DBContract.LogEventModuleTalbe.CHECKLIST_JSON, moduleLogEvent.getServerData());
                data.put(DBContract.LogEventModuleTalbe.EVENT_NAME, moduleLogEvent.getEventName());
                data.put(DBContract.LogEventModuleTalbe.ANSWER_JSON, moduleLogEvent.getAnswerJson());
                data.put(DBContract.LogEventModuleTalbe.SYNC_DATA, moduleLogEvent.getSyncData());
                data.put(DBContract.LogEventModuleTalbe.IS_SYNCED, moduleLogEvent.getIsSynced());


                datas.put(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        return datas;
    }

    public void getAllNotSyncedItems(final IDBArrayResultView<ModuleLogEvent> callBack){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (LogEventDBHelperModule.getInstance(context).isTableExists(DBContract.LogEventModuleTalbe.TABLE_NAME, true)) {

                    SQLiteDatabase db = getDataBaseConnection();
                    if (db.isOpen()) {
                        ArrayList<ModuleLogEvent> moduleLogEvents = getItemsByCursor(getDataBaseConnection().rawQuery("SELECT * FROM " + DBContract.LogEventModuleTalbe.TABLE_NAME
                                + " where " + DBContract.LogEventModuleTalbe.IS_SYNCED + " ='" + 0 + "'", null), db);
                        callBack.onSuccess(moduleLogEvents);
                    }
                }
            }
        }).start();
    }

    @Override
    public long insert(ModuleLogEvent item, SQLiteDatabase db, IDBResultView callBack) {
        SQLiteDatabase DB = getDataBaseConnection();

        if (LogEventDBHelperModule.getInstance(context).isTableExists(DBContract.LogEventModuleTalbe.TABLE_NAME,true)) {


            if (DB.isOpen()) {

                long id = DB.insert(DBContract.LogEventModuleTalbe.TABLE_NAME, null, getValuse(item));

                if (id > 0)
                    callBack.onSuccess();
                else
                    callBack.onFail("No inserted");
                return id;
            } else {
                DB = getDataBaseConnection();
                long id = DB.insert(DBContract.LogEventModuleTalbe.TABLE_NAME, null, getValuse(item));

                if (id > 0)
                    callBack.onSuccess();
                else
                    callBack.onFail("No inserted");
                return id;
            }
        }
        callBack.onFail("No table");
        return 0;
    }

    @Override
    public ModuleLogEvent getByJson(JSONObject json) {
        return null;
    }

    @Override
    public void refreshTable(JSONArray json, IDBResultView callback) {

    }

    @Override
    public ModuleLogEvent fetchById(long id) {
        return null;
    }

    @Override
    public ModuleLogEvent getItemByCursor(Cursor cursor) {
        return null;
    }

    public void setAllSentSynced(JSONArray data) {

        String ides = "";
        for (int i = 0; i < data.length(); i++) {
            try {
                String id = data.getJSONObject(i).getString(DBContract.LogEventModuleTalbe._ID);
                if (data.length() == 1) {
                    ides = id;
                }else {
                    if (i == data.length() - 1){
                        ides += id;
                    }else {
                        ides += id + ",";
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }//here we have all ides in IDES variable like this 1,3,5,6,7,23

        if (LogEventDBHelperModule.getInstance(context).isTableExists(DBContract.LogEventModuleTalbe.TABLE_NAME,true)) {

            String SQL = " UPDATE " + DBContract.LogEventModuleTalbe.TABLE_NAME
                    + " SET " + DBContract.LogEventModuleTalbe.IS_SYNCED + " = '1'" +
                    " WHERE " + DBContract.LogEventModuleTalbe._ID + " IN ("
                    + ides + " ) ";

            getDataBaseConnection().execSQL(SQL);
        }

    }

    @Override
    public ModuleLogEvent getItemForCursor(Cursor cursor) {


        long ID = cursor.getLong(cursor.getColumnIndex(DBContract.LogEventModuleTalbe._ID));
        String AV = cursor.getString(cursor.getColumnIndex(DBContract.LogEventModuleTalbe.APP_VERIOSN));
        String tokenTemp = cursor.getString(cursor.getColumnIndex(DBContract.LogEventModuleTalbe.TOKEN));
        String userTemp = cursor.getString(cursor.getColumnIndex(DBContract.LogEventModuleTalbe.USER));
        String internetTemp = cursor.getString(cursor.getColumnIndex(DBContract.LogEventModuleTalbe.INTERNET));
        String phoneTemp = cursor.getString(cursor.getColumnIndex(DBContract.LogEventModuleTalbe.PHONE));
        String oldversionTemp = cursor.getString(cursor.getColumnIndex(DBContract.LogEventModuleTalbe.OLD_VERSION));
        String newversionTemp = cursor.getString(cursor.getColumnIndex(DBContract.LogEventModuleTalbe.NEW_VERSION));
        String dateTemp = cursor.getString(cursor.getColumnIndex(DBContract.LogEventModuleTalbe.DATE));
        String timeTemp = cursor.getString(cursor.getColumnIndex(DBContract.LogEventModuleTalbe.TIME));
        String serverdataTemp = cursor.getString(cursor.getColumnIndex(DBContract.LogEventModuleTalbe.SERVER_DATA));
        String checklistjsonTemp = cursor.getString(cursor.getColumnIndex(DBContract.LogEventModuleTalbe.CHECKLIST_JSON));
        String eventnameTemp = cursor.getString(cursor.getColumnIndex(DBContract.LogEventModuleTalbe.EVENT_NAME));
        String answerjsonTemp = cursor.getString(cursor.getColumnIndex(DBContract.LogEventModuleTalbe.ANSWER_JSON));
        String syncdataTemp = cursor.getString(cursor.getColumnIndex(DBContract.LogEventModuleTalbe.SYNC_DATA));
        int issyncedTemp = cursor.getInt(cursor.getColumnIndex(DBContract.LogEventModuleTalbe.IS_SYNCED));

        ModuleLogEvent moduleLogEvent = new ModuleLogEvent(context,AV,tokenTemp,userTemp,internetTemp,dateTemp,timeTemp,phoneTemp
                ,oldversionTemp,newversionTemp
        ,serverdataTemp,checklistjsonTemp,eventnameTemp,answerjsonTemp,syncdataTemp,issyncedTemp);
        moduleLogEvent.setId(ID);

        return moduleLogEvent;
    }

    @Override
    public boolean updateImgPath(SQLiteDatabase db, String URL, long id, IDBResultView callBack) {
        return false;
    }

    @Override
    public ArrayList<ModuleLogEvent> getAllItems() {
        if (LogEventDBHelperModule.getInstance(context).isTableExists(DBContract.LogEventModuleTalbe.TABLE_NAME,true)) {

            String SQL = "SELECT * FROM " + DBContract.LogEventModuleTalbe.TABLE_NAME;
            Cursor cursor = getDataBaseConnection().rawQuery(SQL, null);
            ArrayList<ModuleLogEvent> moduleLogEvents = getItemsByCursor(cursor, getDataBaseConnection());
            return moduleLogEvents;
        }
        return new ArrayList<>();
    }

    @Override
    public ArrayList<ModuleLogEvent> getItemsByCursor(Cursor cursor, SQLiteDatabase db) {
        try {
            ArrayList<ModuleLogEvent> moduleLogEvents = new ArrayList<>();
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {

                    moduleLogEvents.add(getItemForCursor(cursor));

                    cursor.moveToNext();
                }
            }

            return moduleLogEvents;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            cursor.close();
        }
        return new ArrayList<>();
    }

    @Override
    public String getImgURLById(long id) {
        return null;
    }

    @Override
    public ContentValues getValuse(ModuleLogEvent item) {
        ContentValues values = new ContentValues();

        values.put(DBContract.LogEventModuleTalbe.APP_VERIOSN,item.getAppVersion());
        values.put(DBContract.LogEventModuleTalbe.TOKEN,item.getToken());
        values.put(DBContract.LogEventModuleTalbe.USER,item.getUser());
        values.put(DBContract.LogEventModuleTalbe.INTERNET,item.getInternet());
        values.put(DBContract.LogEventModuleTalbe.DATE,item.getDate());
        values.put(DBContract.LogEventModuleTalbe.TIME,item.getTime());
        values.put(DBContract.LogEventModuleTalbe.PHONE,item.getPhone());
        values.put(DBContract.LogEventModuleTalbe.OLD_VERSION,item.getOldVersion());
        values.put(DBContract.LogEventModuleTalbe.NEW_VERSION,item.getNewVersion());
        values.put(DBContract.LogEventModuleTalbe.SERVER_DATA,item.getServerData());
        values.put(DBContract.LogEventModuleTalbe.CHECKLIST_JSON,item.getChecklistJson());
        values.put(DBContract.LogEventModuleTalbe.EVENT_NAME,item.getEventName());
        values.put(DBContract.LogEventModuleTalbe.ANSWER_JSON,item.getAnswerJson());
        values.put(DBContract.LogEventModuleTalbe.SYNC_DATA,item.getSyncData());
        values.put(DBContract.LogEventModuleTalbe.IS_SYNCED,item.getIsSynced());

        return values;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getInternet() {
        return internet;
    }

    public void setInternet(String internet) {
        this.internet = internet;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOldVersion() {
        return oldVersion;
    }

    public void setOldVersion(String oldVersion) {
        this.oldVersion = oldVersion;
    }

    public String getNewVersion() {
        return newVersion;
    }

    public void setNewVersion(String newVersion) {
        this.newVersion = newVersion;
    }

    public String getServerData() {
        return serverData;
    }

    public void setServerData(String serverData) {
        this.serverData = serverData;
    }

    public String getChecklistJson() {
        return checklistJson;
    }

    public void setChecklistJson(String checklistJson) {
        this.checklistJson = checklistJson;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getAnswerJson() {
        return answerJson;
    }

    public void setAnswerJson(String answerJson) {
        this.answerJson = answerJson;
    }

    public String getSyncData() {
        return syncData;
    }

    public void setSyncData(String syncData) {
        this.syncData = syncData;
    }

    public int getIsSynced() {
        return isSynced;
    }

    public void setIsSynced(int isSynced) {
        this.isSynced = isSynced;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
