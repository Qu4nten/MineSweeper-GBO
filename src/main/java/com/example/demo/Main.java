package com.example.demo;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;

import static javafx.geometry.Pos.BASELINE_CENTER;

public class Main extends Application {

    private Stage stage;

    private Scene mainScene;
    private Scene leaderScene;

    private Scene gamePrepScene;

    private Scene gameScene;

    @Override
    public void start(Stage primaryStage) throws IOException {

        stage = primaryStage;

        mainScene = createMainScene();
        leaderScene = createLeaderBoardScene();
        gamePrepScene = createGamePrepScene();
        //Field.openAll(); //TODO (fully) remove this once it's no longer needed

        stage.setTitle("MINESWEEPER");
        stage.setScene(mainScene);
        stage.setResizable(false);
        stage.show();
    }

    private void createGameSceneAndSwitch(int sizeX, int sizeY, int difficulty){
        TilePane tilePane = new TilePane();
        Game myGame = new Game(sizeX, sizeY, difficulty, this);

        for (int i = 0; i < myGame.FieldList.size(); i++) {
            tilePane.getChildren().add(myGame.FieldList.get(i).button);
        }
        tilePane.setPrefColumns(sizeX);
        tilePane.setAlignment(Pos.CENTER);
        tilePane.setHgap(0);
        tilePane.setVgap(0);
        tilePane.setStyle("-fx-border-color: black");
        tilePane.setPadding(new Insets(10, 10, 10 ,10));
        VBox fieldButtonVBox = new VBox();
        fieldButtonVBox.getChildren().add(tilePane);
        fieldButtonVBox.setFillWidth(false);
        fieldButtonVBox.setAlignment(Pos.CENTER);

        HBox labelHBox = new HBox();
        for (int i = 0; i < myGame.debugLabels.size(); i++) {
            labelHBox.getChildren().add(myGame.debugLabels.get(i));
        }
        labelHBox.setAlignment(Pos.TOP_CENTER);
        labelHBox.setMinHeight(100);

        VBox vbox = new VBox(labelHBox, fieldButtonVBox);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(10,10,10,10));
        Scene GameScene = new Scene(vbox, 960, 720);
        switchScenes(GameScene);
    }

    private Scene createGamePrepScene() {
        Button button1 = new Button("Back");
        button1.setMaxWidth(Double.MAX_VALUE);
        button1.setOnAction(e-> switchScenes(mainScene));

        Label labelPL = new Label("PLAY!");
        labelPL.setFont(new Font("Comic Sans MS", 50));
        labelPL.setTextFill(Color.web("Red"));

        Label labelSZ = new Label("Size:");

        ObservableList<String> optionsSize =
                FXCollections.observableArrayList(
                        "20 : 14",
                        "14 :  9",
                        "24 : 17"
                );
        final ComboBox comboBoxSize = new ComboBox(optionsSize);
        comboBoxSize.getSelectionModel().selectFirst();

        HBox hbox1 = new HBox();
        HBox.setMargin(labelSZ, new Insets(0, 10, 0, 10));

        hbox1.getChildren().add(labelSZ);
        hbox1.getChildren().add(comboBoxSize);

        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "Easy",
                        "Medium",
                        "Hard"
                );
        final ComboBox comboBoxDifficulty = new ComboBox(options);
        comboBoxDifficulty.getSelectionModel().selectFirst();

        Button button2 = new Button("Start");

        button2.setOnAction(e->{
            int x       = -1;
            int y       = -1;
            int diffc   = -1;
            int choice = comboBoxSize.getSelectionModel().getSelectedIndex();
            if (choice == 0){x = 20;y = 14;}
            else if (choice == 1){x = 14; y = 9;}
            else if (choice == 2){x = 24; y = 17;}
            diffc = comboBoxDifficulty.getSelectionModel().getSelectedIndex();
            createGameSceneAndSwitch(x, y,diffc);
        });


        VBox vbox = new VBox(labelPL, hbox1, comboBoxDifficulty, button2, button1);
        vbox.setFillWidth(true);
        vbox.setAlignment(Pos.BASELINE_LEFT);

        Scene gamePrepScene = new Scene(vbox,960,720);
        return gamePrepScene;
    }

    public void createPostGameSceneAndSwitch(boolean state){

    }
    private Scene createMainScene() {
        double r=50;

        Label labelMS = new Label("MINESWEEPER");
        labelMS.setFont(new Font("Comic Sans MS", 50));
        labelMS.setTextFill(Color.web("Red"));

        Button button1 = new Button();
        button1.setText("PLAY");
        button1.setMaxWidth(Double.MAX_VALUE);
        button1.setFont(new Font("Comic Sans MS", 30));
        //button1.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(button1, Priority.ALWAYS);
        button1.setShape(new Circle(r));
        button1.setMinSize(18*r, 2*r);
        button1.setMaxSize(18*r, 2*r);
        button1.setOnAction(e -> stage.setScene(gamePrepScene));

        Button button2 = new Button();
        button2.setText("LEADERBOARD");
        button2.setFont(new Font("Comic Sans MS", 30));
        button2.setMaxWidth(Double.MAX_VALUE);
        button2.setShape(new Circle(r));
        button2.setMinSize(18*r, 2*r);
        button2.setMaxSize(18*r, 2*r);
        button2.setOnAction(e-> switchScenes(leaderScene));

        Button button3 = new Button();
        button3.setText("TUTORIAL");
        button3.setFont(new Font("Comic Sans MS", 30));
        button3.setMaxWidth(Double.MAX_VALUE);
        button3.setShape(new Circle(r));
        button3.setMinSize(18*r, 2*r);
        button3.setMaxSize(18*r, 2*r);
        //button3.setOnAction(e-> switchScenes(gameScene));

        VBox layout = new VBox(20, labelMS, button1, button2, button3);
        layout.setAlignment(BASELINE_CENTER);
        layout.setFillWidth(true);


        VBox.setMargin(labelMS, new Insets(0, 10, 0, 10));
        //                                  (top, right, bottom, left)

        layout.setStyle("-fx-padding: 16;");
        layout.setSpacing(50);


        Scene scene1 = new Scene(layout, 960, 720);
        return scene1;
    }
    private Scene createLeaderBoardScene() {
        BorderPane borderPane = new BorderPane();
        VBox vBox = new VBox();
        Button button = new Button("Back");

        Label labelLB = new Label("LEADERBOARD");
        labelLB.setFont(new Font("Comic Sans MS", 50));
        labelLB.setTextFill(Color.web("Red"));

        borderPane.setTop(labelLB);
        BorderPane.setAlignment(labelLB, Pos.CENTER);

        button.setMaxWidth(Double.MAX_VALUE);
        button.setOnAction(e-> switchScenes(mainScene));
        borderPane.setBottom(button);

        borderPane.setCenter(vBox);
        BorderPane.setAlignment(vBox, Pos.TOP_RIGHT);
        vBox.getChildren().add(createListing());
        vBox.getChildren().add(createListing());
        vBox.getChildren().add(createListing());

        Scene scene2 = new Scene(borderPane, 960,720);
        return scene2;
    }



    public HBox createListing() {

        Label label1 = new Label("Test Placement");
        label1.setFont(new Font("Arial", 20));
        label1.setAlignment(BASELINE_CENTER);

        Label label2 = new Label("Test Name");
        label2.setFont(new Font("Arial", 20));
        Label label3 = new Label("Test Score 123");
        label3.setFont(new Font("Arial", 20));
        HBox hBox = new HBox(label1, label2, label3);
        return hBox;
    }
    public void switchScenes(Scene scene) {
        stage.setScene(scene);
    }

    public static void main(String[] args) {
        launch();
    }
}