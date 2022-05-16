package com.example.demo;

import javafx.scene.control.Button;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Field {
    private static ArrayList<Field> FieldList;
    private static int maxMultiOpen = 1;
    private static Set<Field> openedFields; //I chose a Set here to prevent Duplicates
    private static int openedThisTurn = 0;
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
        button.setOnAction(e-> fieldClicked());
        button.setMinSize(35, 35);
        button.setMaxSize(35, 35);
        value = 0;
        neigbours = new ArrayList<>();
        cardinalNeigbours = new ArrayList<>();
        openedFields = new HashSet<>();
    }

    private void fieldClicked() {       //FIXME Hack to make sure openedThisTurn can be reset, replace with better idea
        fieldActivated();
        openedThisTurn = 0;
    }

    public static void setMaxToOpen() {     //Sets how many Fields can be opened at once
        float temp = FieldList.size() / 10;
        for (int i = 0;; i++) {
            if (temp > (float)maxMultiOpen){
                maxMultiOpen = maxMultiOpen + i*4;
            }
            else break;
        }
    }

    public void fieldActivated() {        //FIXME Change this back to private once open all is no longer needed // Or don't?
        openedThisTurn++;
        if (value == -1){
            button.setText("X");
            button.setStyle("-fx-text-fill: red;-fx-font-weight: bold");
            openedFields.add(this);
        }
        else if (value == 0){
            button.setText("");
            openedFields.add(this);
            openMass();
        }
        else if (value == 1){
            button.setText("1");
            button.setStyle("-fx-text-fill: green");
            openedFields.add(this);
            openMass();
        }
        else if (value == 2){
            button.setText("2");
            button.setStyle("-fx-text-fill: yellow");
            openedFields.add(this);
        }
        else{
            button.setText(Integer.toString(value));
            button.setStyle("-fx-text-fill: orange");
            openedFields.add(this);
        }
        button.setStyle("-fx-background-color: white");
    }

    private void openMass() {
        float share = (float)openedFields.size() / (float)FieldList.size();
        if (share > 0.5){   //If more than 60% of tiles have been opened we quit opening multiple for the rest of the game
            maxMultiOpen = 0;
            return;
        }
        else if (openedThisTurn >= maxMultiOpen){      //TODO This needs to compare to a proper temp variable "(int) openedThisTurn"
            return;
        }
        else {
            for (int i = 0; i < cardinalNeigbours.size(); i++) {
                if(cardinalNeigbours.get(i).value != -1){
                    if(!openedFields.contains(cardinalNeigbours.get(i))) {
                        cardinalNeigbours.get(i).fieldActivated();
                        openedFields.add(this);
                    }
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
            FieldList.get(i).fieldActivated();
        }
    }
}
