package com.example.checklist.LayoutMaker;

public class LayoutModel {

    private String oreder_id ;
    private String order_name;
    private String img;
    private String shop;
    private String countpos;
    private String positions;
    private String image_path;
    private String item_id;
    private String replacements;

    public static String conf_oreder_id = "oreder_id" ;
    public static String conf_order_name = "name";
    public static String conf_img = "img";
    public static String conf_shop = "shop";
    public static String conf_countpos = "countpos";
    public static String conf_positions = "positions";//this json array
    public static String conf_positions_name = "name";
    public static String conf_positions_img = "img";
    public static String conf_positions_phone_id = "phone_id";
    public static String conf_item_id = "order_id";
    public static String conf_replacements = "replacements";


    public LayoutModel(String oreder_id, String order_name, String img, String shop, String countpos, String positions, String image_path, String item_id, String replacements) {
        this.oreder_id = oreder_id;
        this.order_name = order_name;
        this.img = img;
        this.shop = shop;
        this.countpos = countpos;
        this.positions = positions;
        this.image_path = image_path;
        this.item_id = item_id;
        this.replacements = replacements;
    }

    public String getReplacements() {
        return replacements;
    }

    public void setReplacements(String replacements) {
        this.replacements = replacements;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getPositions() {
        return positions;
    }

    public void setPositions(String positions) {
        this.positions = positions;
    }

    public String getCountpos() {
        return countpos;
    }

    public void setCountpos(String countpos) {
        this.countpos = countpos;
    }

    public String getShop() {
        return shop;
    }

    public void setShop(String shop) {
        this.shop = shop;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getOrder_name() {
        return order_name;
    }

    public void setOrder_name(String order_name) {
        this.order_name = order_name;
    }

    public String getOreder_id() {
        return oreder_id;
    }

    public void setOreder_id(String oreder_id) {
        this.oreder_id = oreder_id;
    }
}
