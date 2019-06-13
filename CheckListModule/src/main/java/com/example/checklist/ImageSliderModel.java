package com.example.checklist;

import java.io.File;
import java.util.ArrayList;

public class ImageSliderModel {
    private File imageFile;
    private String name;
    private String prioritie;
    private ArrayList<Integer> shops;
    private ArrayList<ResultId> resultIDS;
    private String surveyIdes;

    public File getImageFile() {
        return imageFile;
    }

    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrioritie() {
        return prioritie;
    }

    public void setPrioritie(String prioritie) {
        this.prioritie = prioritie;
    }


    public ArrayList<Integer> getShops() {
        return shops;
    }

    public void setShops(ArrayList<Integer> shops) {
        this.shops = shops;
    }

    public ArrayList<ResultId> getResultIDS() {
        return resultIDS;
    }

    public void setResultIDS(ArrayList<ResultId> resultIDS) {
        this.resultIDS = resultIDS;
    }


    public String getSurveyIdes() {
        return surveyIdes;
    }

    public void setSurveyIdes(String surveyIdes) {
        this.surveyIdes = surveyIdes;
    }
}
