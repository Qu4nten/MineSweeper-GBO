package com.example.demo;

import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Field {
    private static ArrayList<Field> FieldList;
    private static int maxMultiOpen = 1;
    private static Set<Field> openedFields = new HashSet<>(); //I chose a Set here to prevent Duplicates
    private static Set<Field> flaggedFields = new HashSet<>();
    private static int openedThisTurn = 0;
    public final int coordX;
    public final int coordY;
    public Button button;
    private int value;
    private ArrayList<Field> neigbours;
    private ArrayList<Field> cardinalNeigbours;
    private boolean flagged = false;

    Field(int x, int y){
        coordX = x;
        coordY = y;
        button = new Button();
        button.setStyle("-fx-background-color: black");
        button.setOnMouseClicked(event->
        {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (!flagged){
                    fieldClicked();
                }
            }
            if (event.getButton() == MouseButton.SECONDARY) {
                fieldRightClicked();
            }
        });
        button.setMinSize(35, 35);
        button.setMaxSize(35, 35);
        value = 0;
        neigbours = new ArrayList<>();
        cardinalNeigbours = new ArrayList<>();
    }

    public static Set<Field> getFlaggedFields() {
        return flaggedFields;
    }

    private void fieldRightClicked() {
        if (openedFields.contains(this)) return;
        if (!flagged) {
            button.setText("\uD83D\uDEA9");
            button.setStyle("-fx-text-fill: #c41414;-fx-background-color: black;-fx-font-weight: bold");
            flagged = true;
            flaggedFields.add(this);
        }
        else if (flagged){
            button.setText("");
            button.setStyle("-fx-text-fill: #c41414;-fx-background-color: black;-fx-font-weight: bold");
            flagged = false;
            flaggedFields.remove(this);
        }
        Game.debugLabels.get(2).setText("Current Flags: " + flaggedFields.size() + "\t");
    }


    private void fieldClicked() {       //FIXME Hack to make sure openedThisTurn can be reset, replace with better idea
        if (openedFields.contains(this)) return;
        fieldActivated();               //TODO show7
        openedThisTurn = 0;             //All this happens *after* the multiOpen
        Game.debugLabels.get(3).setText("Currently Opened: " + openedFields.size() + "\t");
        float ratio = Math.round(100 * openedFields.size() / FieldList.size());
        ratio /= 100;
        Game.debugLabels.get(4).setText("Ratio Opened: " + ratio + "\t");
    }

    public static void setMaxToOpen() {     //Sets how many Fields can be opened at once    //TODO show3
        float temp = FieldList.size() / 10;
        for (int i = 0;; i++) {
            if (temp > (float)maxMultiOpen){
                maxMultiOpen = maxMultiOpen + i*4;
            }
            else break;
        }
    }

    public void fieldActivated() {        //FIXME Change this back to private once open all is no longer needed // Or don't?
        openedThisTurn++;                 //TODO show8
        if (value == -1){
            button.setText("⬤");
            button.setStyle("-fx-text-fill: black;-fx-font-weight: bold;-fx-background-color: white");
            openedFields.add(this);
        }
        else if (value == 0){
            button.setText("");
            button.setStyle("-fx-background-color: white");
            openedFields.add(this);
            openMass();
        }
        else if (value == 1){
            button.setText("1");
            button.setStyle("-fx-text-fill: #184d28;-fx-background-color: white;-fx-font-weight: bold");
            openedFields.add(this);
            openMass();
        }
        else if (value == 2){
            button.setText("2");
            button.setStyle("-fx-text-fill: #a85c32;-fx-background-color: white;-fx-font-weight: bold");
            openedFields.add(this);
        }
        else{
            button.setText(Integer.toString(value));
            button.setStyle("-fx-text-fill: #c41414;-fx-background-color: white;-fx-font-weight: bold");
            openedFields.add(this);
        }
    }

    private void openMass() {               //TODO show9
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
                if(cardinalNeigbours.get(i).value != -1){       //if Cardinal neighbour is *not* a bomb
                    if(!openedFields.contains(cardinalNeigbours.get(i))) { //if cardinalNeighbour is *not* already open
                        cardinalNeigbours.get(i).fieldActivated();  //TODO check if this check is redundant
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

    public void findNeigbours(){                                        //TODO show4

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
