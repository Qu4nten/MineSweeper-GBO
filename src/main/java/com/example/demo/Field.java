package com.example.demo;

import javafx.scene.control.Button;

public class Field {
    public final int coordX;
    public final int coordY;
    public Button button;
    public boolean isBomb;

    Field(int x, int y){
        coordX = x;
        coordY = y;
        button = new Button();
        button.setOnAction(e-> FieldClicked());
        button.setMinSize(35, 35);
        button.setMaxSize(35, 35);
    }

    private void FieldClicked() {
        if(button.getText() == "x") {
            button.setText("");
        }
        else{
                button.setText("x");
        }
        if(isBomb){
            button.setText("O");
        }
    }
}
