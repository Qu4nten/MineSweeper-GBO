package com.example.demo;

import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collections;

public class Field {
    private static ArrayList<Field> FieldList;
    private static int maxMultiOpen = 1;
    private static int openedThisTurn = 0;
    public final int coordX;
    public final int coordY;
    public Button button;
    private int value;
    final private ArrayList<Field> neigbours;
    final private ArrayList<Field> cardinalNeigbours;
    private boolean flagged = false;
    final private Game myGame;


    Field(int x, int y, Game myGame){
        coordX = x;
        coordY = y;
        button = new Button();
        button.setStyle(MyStyles.closedColorBG);
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
        button.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));  //StackOverflow
        value = 0;
        neigbours = new ArrayList<>();
        cardinalNeigbours = new ArrayList<>();
        this.myGame = myGame;
    }

    public int getValue() {return value;}

    private void fieldRightClicked() {
        if (myGame.getOpenedFields().contains(this)) return;
        if (!flagged) {
            button.setText("\uD83D\uDEA9");
            button.setStyle(MyStyles.closedColorBG + MyStyles.threePlusColorFont + MyStyles.boldFont);
            flagged = true;
            myGame.getFlaggedFields().add(this);
            myGame.updateFlagLabel();
            myGame.checkWin();
        }
        else{
            button.setText("");
            button.setStyle(MyStyles.closedColorBG);
            flagged = false;
            myGame.getFlaggedFields().remove(this);
        }
    }
    private void fieldClicked() {       //FIXME Hack to make sure openedThisTurn can be reset, replace with better idea
        if (myGame.getOpenedFields().contains(this)) return;
        fieldActivated();               //TODO show7
        openedThisTurn = 0;             //All this happens *after* the multiOpen
        if(myGame.checkLose()) {
            myGame.lose();
        }
    }

    public static void setMaxToOpen() {     //Sets how many Fields can be opened at once    //TODO show3
        float temp = FieldList.size() / 10f;
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
            button.setStyle(MyStyles.blackColorFont + MyStyles.boldFont + MyStyles.openedColorBG);
            myGame.getOpenedFields().add(this);
        }
        else if (value == 0){
            button.setText("");
            button.setStyle(MyStyles.openedColorBG);
            myGame.getOpenedFields().add(this);
            openMass();
        }
        else if (value == 1){
            button.setText(Integer.toString(value));
            button.setStyle(MyStyles.openedColorBG + MyStyles.boldFont + MyStyles.oneColorFont);
            myGame.getOpenedFields().add(this);
            openMass();
        }
        else if (value == 2){
            button.setText(Integer.toString(value));
            button.setStyle(MyStyles.openedColorBG + MyStyles.boldFont + MyStyles.twoColorFont);
            myGame.getOpenedFields().add(this);
        }
        else{
            button.setText(Integer.toString(value));
            button.setStyle(MyStyles.openedColorBG + MyStyles.boldFont + MyStyles.threePlusColorFont);
            myGame.getOpenedFields().add(this);
        }
    }

    private void openMass() {
        float share = (float)myGame.getOpenedFields().size() / (float)FieldList.size();
        if (share > 0.5){   //If more than 60% of tiles have been opened we quit opening multiple for the rest of the game
            maxMultiOpen = 0;
        }
        else if (openedThisTurn >= maxMultiOpen){
            return; //TODO Consider rewriting this to avoid warning
        }
        else {
            for (Field cardinalNeigbour : cardinalNeigbours) {
                if (cardinalNeigbour.value != -1) {       //if Cardinal neighbour is *not* a bomb
                    if ((!myGame.getOpenedFields().contains(cardinalNeigbour)) && (!myGame.getFlaggedFields().contains(cardinalNeigbour))) { //if cardinalNeighbour is *not* already open or flagged
                        cardinalNeigbour.fieldActivated();  //TODO check if this check is redundant //TODO Consider rewriting this with temp var currentNeighbour
                        myGame.getOpenedFields().add(this);
                    }
                }
            }
        }

    }

    private Field getFieldOffsetFromThis(int XOffset, int YOffset){
        for (Field field : FieldList) {
            if ((field.coordX == (coordX + XOffset)) && (field.coordY == (coordY + YOffset))) {
                return field;
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
}
