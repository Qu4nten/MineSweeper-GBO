package com.example.demo;
import java.util.*;

public class Game {
    public final int sizeX;
    public final int sizeY;
    public final int bombCount;
    public ArrayList<Field> FieldList;

    Game(int x, int y){
        sizeX = x;
        sizeY = y;
        Random rand = new Random();
        double randMod = (rand.nextFloat()/5)-0.1;
        bombCount =  (int)Math.round( ((sizeX*sizeY)/12) *randMod);     //TODO: Fix this atrocity
        FieldList = new ArrayList<>();

    }

    Game(){
        sizeX = 20;
        sizeY = 14;
        FieldList = new ArrayList<>();

        Random rand = new Random();
        /*double randMod = (rand.nextFloat()/5)-0.1;
        bombCount =  (int)Math.round( ((sizeX*sizeY)/12) *randMod);     //TODO: Fix this atrocity*/
        bombCount = 36 + rand.nextInt(8);
        createField(sizeX, sizeY);
        System.out.println("The bomb count is " + bombCount);
    }

    private void createField(int sizeX, int sizeY) {
        for (int i = 0; i < sizeY; i++) {       //Iterates over Y axis (0-13 Standard)
            for (int j = 0; j < sizeX; j++) {   //Iterates over X axis (0-20 Standard)
                Field myField = new Field(i, j);
                FieldList.add(myField);
            }
        }
        Field.setFieldList(FieldList);

        for (int i = 0; i < Field.getFieldList().size(); i++) {
            Field.getFieldList().get(i).findNeigbours();
        }

        assignFieldValues2(scatterBombs());
    }

    /*private void assignFieldValues(ArrayList<Integer> bombPositions) {
        for (int i = 0; i < bombPositions.size(); i++) {            //Iterates over every Bomb Position
            Field current = FieldList.get(bombPositions.get(i));    //This is the bomb Field
            ArrayList<Field> surroundingFields = new ArrayList<Field>();
            surroundingFields = getSurroundingFields(current);      //The 3-8 Fields around the bomb
            for (int j = 0; j < surroundingFields.size(); j++) {
                if(surroundingFields.get(j).value >= 0) {           //If Field is not a bomb itself
                    surroundingFields.get(j).value += 1;
                }
            }
        }

    } */
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

        System.out.println(bombPositions);

        for (int i = 0; i < bombPositions.size(); i++) {
            FieldList.get(bombPositions.get(i)).placeBomb();
        }


        return bombPositions;
    }
}
