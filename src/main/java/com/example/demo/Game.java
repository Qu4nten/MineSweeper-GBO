package com.example.demo;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.text.Font;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class Game {
    private static Set<Field> bombSet = new HashSet<>();
    public final int sizeX;     //Set to private
    public final int sizeY;
    private final int difficulty;
    private final int bombCount;
    public ArrayList<Field> FieldList;
    public static ArrayList<Label> debugLabels;
    private Label timeLabel;
    private Label flagLabel;
    private final Instant startTime = Instant.now();
    private static Main myController;

    Game(int x, int y, int diffi, Main myCtr){
        sizeX = x;
        sizeY = y;
        difficulty = diffi;
        myController = myCtr;
        FieldList = new ArrayList<>();
        timeLabel = new Label();
        Random rand = new Random();
        float difficultyMultiplier = 1;
        switch (difficulty){
            case 0:
                difficultyMultiplier = 0.8f;
                break;
            case 1:
                difficultyMultiplier = 1f;
                break;
            case 2:
                difficultyMultiplier = 1.2f;
                break;
            default:
                difficultyMultiplier = 1f;
        }
        bombCount = (int) Math.floor((Math.floor((x*y)/7) + rand.nextInt((int) Math.floor((x*y)/40)))*difficultyMultiplier) ;

        createLabels();

        //bombCount = 2; //TODO Debug Purposes, remove later
        createField(sizeX, sizeY);

        debugLabels = new ArrayList<>();
        debugLabels.add(new Label("Total Bombs: " + bombCount +"\t"));      //TODO Put this in separate method
        debugLabels.add(new Label("Currently Marked Bombs:\t"));
        debugLabels.add(new Label("Total Flags:\t"));
        debugLabels.add(new Label("Currently Opened: 0\t"));
        debugLabels.add(new Label("Ratio Opened:\t"));


    }

    public String getGameTime() {
        return Long.toString(Duration.between(startTime, Instant.now()).toSeconds());
    }
    public int getBombCount() {
        return bombCount;
    }

    public int getDifficulty() {
        return difficulty;
    }

    private void createLabels() {
        flagLabel = new Label("\uD83D\uDEA9" + Integer.toString(bombCount));
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
        String timePassed = Long.toString(Duration.between(startTime, Instant.now()).toSeconds());
        timeLabel.setText(timePassed);
    }
    public void updateFlagLabel() {
        int flagsToSet = bombCount - Field.getFlaggedFields().size();
        flagLabel.setText("\uD83D\uDEA9" + flagsToSet);
    }

    private void createField(int sizeX, int sizeY) {        //TODO show2
        for (int i = 0; i < sizeY; i++) {       //Iterates over Y axis (0-13 Standard)
            for (int j = 0; j < sizeX; j++) {   //Iterates over X axis (0-19 Standard)
                Field myField = new Field(i, j, this);
                FieldList.add(myField);
            }
        }
        Field.setFieldList(FieldList);
        Field.setMaxToOpen();

        for (int i = 0; i < Field.getFieldList().size(); i++) {
            Field.getFieldList().get(i).findNeigbours();
        }

        assignFieldValues(scatterBombs());
    }

    private void assignFieldValues(ArrayList<Integer> bombPositions){          //TODO show6
        for (int i = 0; i < bombPositions.size(); i++) {    //TODO Replace with enhanced for loop
            for (int j = 0; j < Field.getFieldList().get(bombPositions.get(i)).getNeigbours().size(); j++) {
                Field.getFieldList().get(bombPositions.get(i)).getNeigbours().get(j).incrementValue();
            }   //Get Current Field with Bomb, get all neighbours, iterate over these and increment
        }
    }

    private ArrayList<Integer> scatterBombs() {         //TODO show5
        Random rand = new Random();
        ArrayList<Integer> bombPositions = new ArrayList<>();   //TODO This should just be a set
        for (int i = 0; i < bombCount; i++) {                   //Places bomb at random point in array
            int current = rand.nextInt(sizeX*sizeY-1);
            bombPositions.add(current);
            Set<Integer> set = new HashSet<>(bombPositions);     //I stole this part
            if(set.size() < bombPositions.size()){               //Removes Duplicates from list
                bombPositions.remove(bombPositions.size() - 1);
                i--;
            }
            else {
                bombSet.add(FieldList.get(current));        //This Set contains all Bomb Fields for later win decision
            }
        }
        for (Field i: bombSet) {    //TODO Debug Purposes
            System.out.println(i.coordX + "\t" + i.coordY);
        }
        for (int i = 0; i < bombPositions.size(); i++) {
            FieldList.get(bombPositions.get(i)).placeBomb();
        }


        return bombPositions;
    }
    public void checkWin() {
        if (Field.getFlaggedFields().equals(bombSet)){
            Game.win();
        }

    }
    public int getCorrectlyFlagged() {
        int correctlyFlagged = 0;
        Iterator<Field> flaggedIterator = Field.getFlaggedFields().iterator();
        while(flaggedIterator.hasNext()) {
            if (flaggedIterator.next().getValue() == -1) {
                correctlyFlagged++;
            }
        }
        return correctlyFlagged;
    }

    private static void win() {

    System.out.println("You win");
    myController.createPostGameSceneAndSwitch(true);
    }

    public boolean checkLose() {
        Set<Field> openedFields = Field.getOpenedFields();
        Iterator<Field> fieldIterator = openedFields.iterator();
        while (fieldIterator.hasNext()){
            if (fieldIterator.next().getValue() == -1){
                return true;
            }
        }
        return false;
    }

    public void lose() {
        System.out.println("You lose");
        myController.createPostGameSceneAndSwitch(false);
    }
}
