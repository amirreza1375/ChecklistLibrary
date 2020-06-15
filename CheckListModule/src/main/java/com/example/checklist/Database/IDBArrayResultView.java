package com.example.checklist.Database;

import java.util.ArrayList;

public interface IDBArrayResultView<M>{
    void onSuccess(ArrayList<M> results);
    void onFail(String error);
}
