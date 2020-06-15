package com.example.checklist.Database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;


public abstract class DBModelBaseTemp<T>  {

    public static final String MODEL_TAG = "Models";

    private final Class<T> a ;

    public DBModelBaseTemp(Class<T> a){
        this.a = a;
    }

    public DBModelBaseTemp(){
        ParameterizedType p = (ParameterizedType) this.getClass().getGenericSuperclass();
        Class<T> tClass = (Class<T>) p.getActualTypeArguments()[0];
        this.a = tClass;
    }

    protected String errorMessage;

    public String getErrorMessage() {
        return errorMessage;
    }


    abstract public SQLiteDatabase dropAndCreateTable();
    abstract public long insert(T item,SQLiteDatabase db,IDBResultView callBack);
    abstract public T getByJson(JSONObject json);
    abstract public void refreshTable(JSONArray json, final IDBResultView callback);

    public abstract T fetchById(long id);
    public abstract T getItemByCursor(Cursor cursor);
    public abstract T getItemForCursor(Cursor cursor);
    public abstract boolean updateImgPath(SQLiteDatabase db,String URL,long id,IDBResultView callBack);
    public abstract ArrayList<T> getAllItems();
    public abstract ArrayList<T> getItemsByCursor(Cursor cursor,SQLiteDatabase db);
    public abstract String getImgURLById(long id);
    public abstract ContentValues getValuse(T item);
}

