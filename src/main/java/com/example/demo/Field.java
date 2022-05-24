package com.example.demo;

import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collections;

public class Field {
    private static int openedThisTurn = 0;
    private final int coordX;
    private final int coordY;
    private final Button button;
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
        button.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        //Bei der obigen Zeile handelt es sich um FremdCode
        value = 0;
        neigbours = new ArrayList<>();
        cardinalNeigbours = new ArrayList<>();
        this.myGame = myGame;
    }

    public int getValue() {return value;}

    public Button getButton() {
        return button;
    }

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
    private void fieldClicked() {
        if (myGame.getOpenedFields().contains(this)) return;
        fieldActivated();               //MultiOpen and thus the recursive function happens here
        openedThisTurn = 0;             //All this happens *after* the multiOpen
        if(myGame.checkLose()) {
            myGame.lose();
        }
    }
    private void fieldActivated() { //Field was clicked, and we might want to open multiple fields
        openedThisTurn++;
        if (value == -1){
            button.setText("⬤");
            button.setStyle(MyStyles.blackColorFont + MyStyles.boldFont + MyStyles.openedColorBG);
            myGame.getOpenedFields().add(this);
        }
        else if (value == 0){
            button.setText("");
            button.setStyle(MyStyles.openedColorBG);
            myGame.getOpenedFields().add(this);
            openMulti();
        }
        else if (value == 1){
            button.setText(Integer.toString(value));
            button.setStyle(MyStyles.openedColorBG + MyStyles.boldFont + MyStyles.oneColorFont);
            myGame.getOpenedFields().add(this);
            openMulti();
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

    private void openMulti() {
        float share = (float)myGame.getOpenedFields().size() / (float)myGame.getFieldList().size();
        if (share > 0.5){   //If more than 60% of tiles have been opened we quit opening multiple for the rest of the game
            myGame.setMaxToOpen(0);
        }
        else if (openedThisTurn >= myGame.getMaxToOpen()){
            return; //TODO Consider rewriting this to avoid warning
        }
        else {
            for (Field cardinalNeigbour : cardinalNeigbours) {
                if (cardinalNeigbour.value != -1) {       //if Cardinal neighbour is *not* a bomb
                    if ((!myGame.getOpenedFields().contains(cardinalNeigbour)) && (!myGame.getFlaggedFields().contains(cardinalNeigbour))) { //if cardinalNeighbour is *not* already open or flagged
                        cardinalNeigbour.fieldActivated();
                        myGame.getOpenedFields().add(this);
                    }
                }
            }
        }

    }

    private Field getFieldOffsetFromThis(int XOffset, int YOffset){
        for (Field field : myGame.getFieldList()) {
            if ((field.coordX == (this.coordX + XOffset)) && (field.coordY == (this.coordY + YOffset))) {
                return field;
            }
        }
        return null;
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

    public void incrementValue() {
        if(value != -1){
            value ++;
        }
    }

    public void placeBomb() {
        value = -1;
    }
}
