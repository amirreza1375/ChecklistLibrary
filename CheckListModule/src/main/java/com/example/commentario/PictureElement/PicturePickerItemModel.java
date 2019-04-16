package com.example.commentario.PictureElement;

public class PicturePickerItemModel {

    public static String conf_index = "index";
    public static String conf_status = "status";
    public static String conf_path = "path";
    public static String conf_catId = "catId";
    public static String conf_hasPic = "hasPic";
    public static String conf_count = "count";
    public static String conf_id = "id";
    public static String conf_category = "category";
    public static String conf_checkListId = "checklistid";
    public static String conf_position = "position";

    private String category;
    private String id;
    private int count ;
    private boolean hasPic;
    private int cat_id;
    private String path = "";
    private boolean status;
    private int index;
//    private long checkListId = -1;
    private int position = -1;

    public PicturePickerItemModel(){

    }

    public PicturePickerItemModel(String category, String id, int count, boolean hasPic, int cat_id
            , String path, boolean status, int index,int position) {
        this.category = category;
        this.id = id;
        this.count = count;
        this.hasPic = hasPic;
        this.cat_id = cat_id;
        this.path = path;
        this.status = status;
        this.index = index;
//        this.checkListId = checkListId;
        this.position = position;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getCat_id() {
        return cat_id;
    }

    public void setCat_id(int cat_id) {
        this.cat_id = cat_id;
    }

    public boolean isHasPic() {
        return hasPic;
    }

    public void setHasPic(boolean hasPic) {
        this.hasPic = hasPic;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }



    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
