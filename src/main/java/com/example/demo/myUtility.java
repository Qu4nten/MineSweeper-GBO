package com.example.demo;

public class myUtility {
    static String getDifficulty(int diff) {
        switch (diff){
            case 0:
                return "Easy";
            case 1:
                return "Medium";
            case 2:
                return "Hard";
            default:
                return "";
        }
    }
}
