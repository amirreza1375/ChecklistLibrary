package com.example.checklist.Database;

/**
 * Created by shahr on 2/6/2019.
 */

public interface IDBResultView {
    void onSuccess();
    void onItemInserted();
    void onFail(String error);
}
