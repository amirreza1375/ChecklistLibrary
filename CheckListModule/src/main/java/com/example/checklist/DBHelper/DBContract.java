package com.example.checklist.DBHelper;

import android.provider.BaseColumns;

public class DBContract {

    public static class LogEventModuleTalbe implements BaseColumns {
        public static final String TABLE_NAME = "ModuleLogEventTable";

        public static final String APP_VERIOSN = "appVersion";
        public static final String TOKEN = "token";
        public static final String USER = "user";
        public static final String INTERNET = "internet";
        public static final String DATE = "date";
        public static final String TIME = "time";
        public static final String PHONE = "phone";
        public static final String OLD_VERSION = "oldVersion";
        public static final String NEW_VERSION = "newVersion";
        public static final String SERVER_DATA = "serverData";
        public static final String CHECKLIST_JSON = "checklistJson";
        public static final String EVENT_NAME = "eventName";
        public static final String ANSWER_JSON = "answerJson";
        public static final String SYNC_DATA = "syncData";
        public static final String IS_SYNCED = "isSynced";

        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        APP_VERIOSN + " TEXT," +
                        TOKEN + " TEXT," +
                        USER + " TEXT," +
                        INTERNET + " TEXT," +
                        DATE + " TEXT," +
                        TIME + " TEXT," +
                        PHONE + " TEXT," +
                        OLD_VERSION + " TEXT," +
                        NEW_VERSION + " TEXT," +
                        SERVER_DATA + " TEXT," +
                        CHECKLIST_JSON + " TEXT," +
                        EVENT_NAME + " TEXT," +
                        ANSWER_JSON + " TEXT," +
                        SYNC_DATA + " TEXT," +
                        IS_SYNCED+" INTEGER)";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " +TABLE_NAME;
    }

}
