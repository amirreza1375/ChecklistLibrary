package com.example.checklist.DBHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import com.example.checklist.Config;

import static com.example.checklist.Config.DATABASE_NAME_MODULE;

public class LogEventDBHelperModule extends SQLiteOpenHelper {


    public static LogEventDBHelperModule logEventTableDBHelper;

    private LogEventDBHelperModule(@Nullable Context context) {
        super(context, DATABASE_NAME_MODULE, null, Config.VERSION);
    }

    public static LogEventDBHelperModule getInstance(Context context){
        if (logEventTableDBHelper == null)
            logEventTableDBHelper = new LogEventDBHelperModule(context);
        return logEventTableDBHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DBContract.LogEventModuleTalbe.SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DBContract.LogEventModuleTalbe.SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }

    public boolean isTableExists(String tableName, boolean openDb) {
        SQLiteDatabase mDatabase = this.getWritableDatabase();
        if(openDb) {
            if(mDatabase == null || !mDatabase.isOpen()) {
                mDatabase = getReadableDatabase();
            }

            if(!mDatabase.isReadOnly()) {
                mDatabase.close();
                mDatabase = getReadableDatabase();
            }
        }

        Cursor cursor = mDatabase.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"+tableName+"'", null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }

}
