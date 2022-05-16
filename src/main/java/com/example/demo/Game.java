package com.example.demo;
import javafx.scene.control.Label;

import java.util.*;

public class Game {
    public final int sizeX;
    public final int sizeY;
    public final int bombCount;
    public ArrayList<Field> FieldList;
    public static ArrayList<Label> debugLabels;

    Game(int x, int y){
        sizeX = 0;
        sizeY = 0;
        bombCount = 0;
    }

    Game(){
        sizeX = 20;
        sizeY = 14;
        FieldList = new ArrayList<>();
        Random rand = new Random();
        bombCount = 36 + rand.nextInt(8);
        createField(sizeX, sizeY);

        debugLabels = new ArrayList<>();
        debugLabels.add(new Label("Total Bombs: " + bombCount +"\t"));
        debugLabels.add(new Label("Currently Marked Bombs:\t"));
        debugLabels.add(new Label("Total Flags:\t"));
        debugLabels.add(new Label("Currently Opened: 0\t"));
        debugLabels.add(new Label("Ratio Opened:\t"));

    }

    private void createField(int sizeX, int sizeY) {
        for (int i = 0; i < sizeY; i++) {       //Iterates over Y axis (0-13 Standard)
            for (int j = 0; j < sizeX; j++) {   //Iterates over X axis (0-20 Standard)
                Field myField = new Field(i, j);
                FieldList.add(myField);
            }
        }
        Field.setFieldList(FieldList);
        Field.setMaxToOpen();

        for (int i = 0; i < Field.getFieldList().size(); i++) {
            Field.getFieldList().get(i).findNeigbours();
        }

        assignFieldValues2(scatterBombs());
    }

    private void assignFieldValues2(ArrayList<Integer> bombPositions){
        for (int i = 0; i < bombPositions.size(); i++) {    //TODO Replace with enhanced for loop
            for (int j = 0; j < Field.getFieldList().get(bombPositions.get(i)).getNeigbours().size(); j++) {
                Field.getFieldList().get(bombPositions.get(i)).getNeigbours().get(j).incrementValue();
            }
        }
    }

    private ArrayList<Integer> scatterBombs() {
        Random rand = new Random();
        ArrayList<Integer> bombPositions = new ArrayList<>();
        for (int i = 0; i < bombCount; i++) {
            int current = rand.nextInt(sizeX*sizeY-1);
            bombPositions.add(current);
            Set<Integer> set = new HashSet<>(bombPositions);     //I stole this part
            if(set.size() < bombPositions.size()){               //Removes Duplicates from list
                bombPositions.remove(bombPositions.size() - 1);
                i--;
            }
        }

        for (int i = 0; i < bombPositions.size(); i++) {
            FieldList.get(bombPositions.get(i)).placeBomb();
        }


        return bombPositions;
    }
}
