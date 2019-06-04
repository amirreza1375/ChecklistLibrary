package com.example.checklist;

import java.util.ArrayList;

public class ReverseArray<T> {

    private T t;

    public  ArrayList<T> reverseArray(ArrayList<T> surveys) {
        ArrayList<T> temp = new ArrayList<>();
        for (int i = surveys.size() - 1; i >= 0; i--) {
            temp.add(surveys.get(i));
        }
        return temp;
    }

}
