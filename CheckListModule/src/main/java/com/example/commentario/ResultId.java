package com.example.commentario;

public class ResultId {

    private int ID;
    private int subCanal;
    private int elemento;
    private int posicion;

    public ResultId(int id, int subCanal, int elemento, int posicion) {
        this.ID = id;
        this.subCanal = subCanal;
        this.elemento = elemento;
        this.posicion = posicion;
    }

    public ResultId(){

    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getSubCanal() {
        return subCanal;
    }

    public void setSubCanal(int subCanal) {
        this.subCanal = subCanal;
    }

    public int getElemento() {
        return elemento;
    }

    public void setElemento(int elemento) {
        this.elemento = elemento;
    }

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

}
