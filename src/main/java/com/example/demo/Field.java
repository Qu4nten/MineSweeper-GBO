package com.example.demo;

import javafx.scene.control.Button;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

public class Field {
    private static ArrayList<Field> FieldList;
    public final int coordX;
    public final int coordY;
    public Button button;
    private int value;
    private ArrayList<Field> neigbours;

    Field(int x, int y){
        coordX = x;
        coordY = y;
        button = new Button();
        button.setOnAction(e-> FieldClicked());
        button.setMinSize(35, 35);
        button.setMaxSize(35, 35);
        value = 0;
        neigbours = new ArrayList<>();

    }

    private void FieldClicked() {
        button.setText(Integer.toString(value));
    }
    private Field getFieldOffsetFromThis(int XOffset, int YOffset){
        for (int i = 0; i < FieldList.size(); i++) {
            if((FieldList.get(i).coordX == (coordX + XOffset))&&(FieldList.get(i).coordY == (coordY + YOffset))) {
                return FieldList.get(i);
            }
        }
        return null;        //TODO: Is this legal?
    }

    public void findNeigbours(){

        neigbours.add(getFieldOffsetFromThis(-1, -1));
        neigbours.add(getFieldOffsetFromThis(-1, 0));
        neigbours.add(getFieldOffsetFromThis(-1, 1));

        neigbours.add(getFieldOffsetFromThis(0, -1));
        neigbours.add(getFieldOffsetFromThis(0, 1));

        neigbours.add(getFieldOffsetFromThis(1, -1));
        neigbours.add(getFieldOffsetFromThis(1, 0));
        neigbours.add(getFieldOffsetFromThis(1, 1));   //Fixme: Can this be looped?

        neigbours.removeAll(Collections.singleton(null));
    }

    public ArrayList<Field> getNeigbours() {
        return neigbours;
    }

    public static void setFieldList(ArrayList<Field> fieldList) {
        FieldList = fieldList;
    }
    public static ArrayList<Field> getFieldList(){
        return FieldList;
    }

    public void incrementValue() {
        if(value != -1){
            value ++;
        }
    }

    public void placeBomb() {
        value = -1;
    }
    public static void openAll(){
        for (int i = 0; i < FieldList.size(); i++) {
            FieldList.get(i).button.setText(Integer.toString(FieldList.get(i).value));
        }
    }
}
