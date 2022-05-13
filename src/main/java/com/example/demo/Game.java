package com.example.demo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

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
        FieldList = new ArrayList<Field>();

    }

    Game(){
        sizeX = 20;
        sizeY = 14;
        FieldList = new ArrayList<Field>();

        Random rand = new Random();
        /*double randMod = (rand.nextFloat()/5)-0.1;
        bombCount =  (int)Math.round( ((sizeX*sizeY)/12) *randMod);     //TODO: Fix this atrocity*/
        bombCount = 36 + rand.nextInt(8);
        createField(sizeX, sizeY);
        System.out.println("The bomb count is " + bombCount);
    }

    private void createField(int sizeX, int sizeY) {
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                Field myField = new Field(i, j);
                FieldList.add(myField);
            }
        }
        scatterBombs();
    }

    private void scatterBombs() {
        Random rand = new Random();
        ArrayList<Integer> bombPositions = new ArrayList<Integer>();
        for (int i = 0; i < bombCount; i++) {
            int current = rand.nextInt(sizeX*sizeY-1);
            bombPositions.add(current);
            Set<Integer> set = new HashSet<Integer>(bombPositions);     //I stole this part
            if(set.size() < bombPositions.size()){
                bombPositions.remove(bombPositions.size() - 1);
                i--;
            }
        }
        System.out.println(bombPositions);

        for (int i = 0; i < bombPositions.size(); i++) {
            FieldList.get(bombPositions.get(i)).isBomb = true;
        }
    }
}
