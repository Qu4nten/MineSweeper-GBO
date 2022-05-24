package com.example.demo;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.text.Font;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class Game {
    private final Set<Field> bombSet = new HashSet<>();
    private final int sizeX;
    private final int sizeY;
    private final int difficulty;
    private final int bombCount;
    private int maxToOpen;
    private final ArrayList<Field> fieldList;
    private final Set<Field> flaggedFields = new HashSet<>();
    private final Set<Field> openedFields = new HashSet<>();
    private Label timeLabel;
    private Label flagLabel;
    private final Instant startTime = Instant.now();
    private static Main myController;

    Game(int x, int y, int diffi, Main myCtr){
        sizeX = x;
        sizeY = y;
        difficulty = diffi;
        myController = myCtr;
        fieldList = new ArrayList<>();
        timeLabel = new Label();
        Random rand = new Random();
        float difficultyMultiplier = switch (difficulty) {
            case 0  -> 0.3f;        //This might be too low
            case 1  -> 1f;
            case 2  -> 1.2f;
            default -> 1f;
        };
        if(true){bombCount = (int) Math.floor((Math.floor((x*y)/7f) + rand.nextInt((int) Math.floor((x*y)/40f)))*difficultyMultiplier);}
        else {bombCount = 2;}//TODO Presentation purposes
        createLabels();
        createField(sizeX, sizeY);
    }

    public Set<Field> getOpenedFields() {
        return openedFields;
    }

    public Set<Field> getFlaggedFields() {
        return flaggedFields;
    }

    public String getGameTime() {
        return Long.toString(Duration.between(startTime, Instant.now()).toSeconds());
    }
    public int getBombCount() {
        return bombCount;
    }

    public ArrayList<Field> getFieldList() {
        return fieldList;
    }

    public int getDifficulty() {
        return difficulty;
    }

    private void createLabels() {
        flagLabel = new Label("\uD83D\uDEA9" + bombCount);
        flagLabel.setPadding(new Insets(0, 50, 0, 50 ));
        flagLabel.setFont(new Font("Impact", 25));


        timeLabel = new Label();
        timeLabel.setPadding(new Insets(0, 50, 0, 50 ));
        timeLabel.setFont(new Font("Impact", 25));
    }
    public Label getTimeLabel() {
        return timeLabel;
    }

    public Label getFlagLabel() {
        return flagLabel;
    }
    public void updateTimeLabel() {
        String timePassed = "\uD83D\uDD51"+ Duration.between(startTime, Instant.now()).toSeconds();
        timeLabel.setText(timePassed);
    }
    public void updateFlagLabel() {
        int flagsToSet = bombCount - flaggedFields.size();
        flagLabel.setText("\uD83D\uDEA9" + flagsToSet);
    }

    private void createField(int sizeX, int sizeY) {
        for (int i = 0; i < sizeY; i++) {       //Iterates over Y axis (0-13 Standard)
            for (int j = 0; j < sizeX; j++) {   //Iterates over X axis (0-19 Standard)
                Field myField = new Field(i, j, this);
                fieldList.add(myField);
            }
        }
        setMaxToOpen();

        for (Field field : fieldList) {
            field.findNeigbours();
        }

        assignFieldValues(scatterBombs());
    }

    public int getMaxToOpen() {
        return maxToOpen;
    }

    public void setMaxToOpen(int maxToOpen) {
        this.maxToOpen = maxToOpen;
    }

    private void setMaxToOpen() {
        float temp = fieldList.size() / 10f;        //1 ; 5 ; 13 ; 25 ; 41 ; 61; 81 etc.
        for (int i = 0;; i++) {
            if (temp > (float)maxToOpen){
                maxToOpen = maxToOpen + i*4;
            }
            else break;
        }
    }

    private void assignFieldValues(Set<Integer> bombPositions){
        for (Integer bombPosition : bombPositions) {
            for (int j = 0; j < fieldList.get(bombPosition).getNeigbours().size(); j++) {
                fieldList.get(bombPosition).getNeigbours().get(j).incrementValue();
            }   //Get Current Field with Bomb, get all neighbours, iterate over these and increment
        }
    }

    private Set<Integer> scatterBombs() {
        Random rand = new Random();
        int sizePre;
        int sizePost;
        Set<Integer> bombPositions = new HashSet<>();
        for (int i = 0; i < bombCount; i++) {                   //Places bomb at random point in array
            int current = rand.nextInt(sizeX*sizeY-1);
            sizePre = bombPositions.size();
            bombPositions.add(current);
            sizePost = bombPositions.size();
            if(sizePre == sizePost){
                i--;
            }
            else {
                bombSet.add(fieldList.get(current));
            }

        }
        for (Integer bombPosition : bombPositions) {
            fieldList.get(bombPosition).placeBomb();
        }


        return bombPositions;
    }
    public void checkWin() {
        if (flaggedFields.equals(bombSet)){
            Game.win();
        }

    }
    public int getCorrectlyFlagged() {
        int correctlyFlagged = 0;
        for (Field flaggedField : flaggedFields) {
            if (flaggedField.getValue() == -1) {
                correctlyFlagged++;
            }
        }
        return correctlyFlagged;
    }

    private static void win() {
    myController.createPostGameSceneAndSwitch(true);
    }

    public boolean checkLose() {
        for (Field openedField : openedFields) {
            if (openedField.getValue() == -1) {
                return true;
            }
        }
        return false;
    }

    public void lose() {
        myController.createPostGameSceneAndSwitch(false);
    }
}
