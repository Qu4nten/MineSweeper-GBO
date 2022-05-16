package com.example.demo;

import javafx.scene.control.Button;

import java.util.ArrayList;
import java.util.Collections;

public class Field {
    private static ArrayList<Field> FieldList;
    private static int maxMultiOpen = 1;
    private static int openedFields = 0;
    public final int coordX;
    public final int coordY;
    public Button button;
    private int value;
    private ArrayList<Field> neigbours;
    private ArrayList<Field> cardinalNeigbours;

    Field(int x, int y){
        coordX = x;
        coordY = y;
        button = new Button();
        button.setStyle("-fx-background-color: black");
        button.setOnAction(e-> fieldClicked0());
        button.setMinSize(35, 35);
        button.setMaxSize(35, 35);
        value = 0;
        neigbours = new ArrayList<>();
        cardinalNeigbours = new ArrayList<>();
    }

    private void fieldClicked0() {
        openedFields = 0;       //FIXME Cringey hack I hate (that also doesn't work), also rename this to something sensible
        fieldClicked();
    }

    public static void setMaxToOpen() {
        float temp = FieldList.size() / 10;
        for (int i = 0;; i++) {
            if (temp > (float)maxMultiOpen){
                maxMultiOpen = maxMultiOpen + i*4;
            }
            else break;
        }
    }

    public void fieldClicked() {        //FIXME Change this back to private once open all is no longer needed // Or don't?
        if (value == -1){
            button.setText("X");
            button.setStyle("-fx-text-fill: red;-fx-font-weight: bold");
            openedFields += 1;
        }
        else if (value == 0){
            button.setText("");
            openedFields += 1;
            openMass();
        }
        else if (value == 1){
            button.setText("1");
            button.setStyle("-fx-text-fill: green");
            openedFields += 1;
            openMass();
        }
        else if (value == 2){
            button.setText("2");
            button.setStyle("-fx-text-fill: yellow");
            openedFields += 1;
        }
        else{
            button.setText(Integer.toString(value));
            button.setStyle("-fx-text-fill: orange");
            openedFields += 1;
        }
        button.setStyle("-fx-background-color: white");
    }

    private void openMass() {
        float share = (float)openedFields / (float)FieldList.size();
        if (share > 0.6){   //If more than 60% of tiles have been opened we quit opening multiple for the rest of the game
            maxMultiOpen = 0;
            return;
        }
        else if (openedFields >= maxMultiOpen){
            return;
        }
        else {
            for (int i = 0; i < cardinalNeigbours.size(); i++) {
                if(cardinalNeigbours.get(i).value != -1){
                    cardinalNeigbours.get(i).fieldClicked();
                    openedFields++;
                }
            }
        }

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
        cardinalNeigbours.add(getFieldOffsetFromThis(-1, 0));
        neigbours.add(getFieldOffsetFromThis(-1, 1));

        neigbours.add(getFieldOffsetFromThis(0, -1));
        cardinalNeigbours.add(getFieldOffsetFromThis(0, -1));
        neigbours.add(getFieldOffsetFromThis(0, 1));
        cardinalNeigbours.add(getFieldOffsetFromThis(0, 1));

        neigbours.add(getFieldOffsetFromThis(1, -1));
        neigbours.add(getFieldOffsetFromThis(1, 0));
        cardinalNeigbours.add(getFieldOffsetFromThis(1, 0));
        neigbours.add(getFieldOffsetFromThis(1, 1));   //Fixme: Can this be looped?

        neigbours.removeAll(Collections.singleton(null));
        cardinalNeigbours.removeAll(Collections.singleton(null));
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
            FieldList.get(i).fieldClicked();
        }
    }
}
