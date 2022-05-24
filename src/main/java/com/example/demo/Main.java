package com.example.demo;

import javafx.application.Application;
import javafx.application.Platform;
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
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import static javafx.geometry.Pos.BASELINE_CENTER;
import static javafx.geometry.Pos.CENTER_LEFT;

public class Main extends Application {

    private Stage stage;

    private Scene mainScene;
    private Scene leaderScene;

    private Scene gamePrepScene;

    private Game myGame;

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
        this.myGame = myGame;

        for (int i = 0; i < myGame.FieldList.size(); i++) {
            tilePane.getChildren().add(myGame.FieldList.get(i).button);
        }
        tilePane.setPrefColumns(sizeX);
        tilePane.setAlignment(Pos.CENTER);
        tilePane.setHgap(0);
        tilePane.setVgap(0);
        tilePane.setStyle(MyStyles.borderColorBlack);
        tilePane.setPadding(new Insets(10, 10, 10 ,10));
        VBox fieldButtonVBox = new VBox();
        fieldButtonVBox.getChildren().add(tilePane);
        fieldButtonVBox.setFillWidth(false);
        fieldButtonVBox.setAlignment(Pos.CENTER);

        HBox labelBox = new HBox();
        labelBox.getChildren().add(myGame.getFlagLabel());
        labelBox.getChildren().add(myGame.getTimeLabel());
        labelBox.setAlignment(Pos.TOP_CENTER);
        labelBox.setMinHeight(100);

        TimerTask updateTimeLabel = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() ->{
                    myGame.updateTimeLabel();
                });
            }
        };
        Timer labelTimer = new Timer();
        labelTimer.scheduleAtFixedRate(updateTimeLabel, 0, 1000);

        VBox vbox = new VBox(labelBox, fieldButtonVBox);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(10,10,10,10));
        Scene GameScene = new Scene(vbox, 960, 720);
        switchScenes(GameScene);
    }

    private Scene createGamePrepScene() {
        Button button1 = new Button("Back");
        button1.setMaxWidth(Double.MAX_VALUE);
        button1.setOnAction(e-> switchScenes(mainScene));
        button1.setStyle("-fx-font: 20px \"Impact\";");
        button1.setPadding(new Insets(10, 30, 10, 30));

        Label labelPL = new Label("PLAY!");
        labelPL.setFont(new Font(MyStyles.fontGeneral, 50));
        labelPL.setTextFill(Color.web("Red"));

        ObservableList<String> optionsSize =
                FXCollections.observableArrayList(
                        "20 : 14",
                        "14 :  9",
                        "24 : 17"
                );
        final ComboBox comboBoxSize = new ComboBox(optionsSize);
        comboBoxSize.getSelectionModel().selectFirst();
        comboBoxSize.setStyle("-fx-font: 30px \"Impact\";");

        HBox hbox1 = new HBox();

        hbox1.getChildren().add(comboBoxSize);

        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "Easy",
                        "Medium",
                        "Hard"
                );
        final ComboBox comboBoxDifficulty = new ComboBox(options);
        comboBoxDifficulty.getSelectionModel().selectFirst();
        comboBoxDifficulty.setStyle("-fx-font: 30px \"Impact\";");
        hbox1.getChildren().add(comboBoxDifficulty);
        HBox.setMargin(comboBoxDifficulty, new Insets(10, 0, 50, 50));
        HBox.setMargin(comboBoxSize, new Insets(10, 0,50,0));
        hbox1.setAlignment(Pos.CENTER);

        Button button2 = new Button("Start");
        button2.setPadding(new Insets(20, 50, 20, 50));
        button2.setStyle("-fx-font: 30px \"Impact\";");

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


        VBox vbox = new VBox(50, labelPL, hbox1, button2, button1);
        vbox.setFillWidth(true);
        vbox.setAlignment(BASELINE_CENTER);

        Scene gamePrepScene = new Scene(vbox,960,720);
        return gamePrepScene;
    }

    public void createPostGameSceneAndSwitch(boolean state){
        VBox vbox = new VBox();

        Label labelOutcome = new Label("YOU LOSE");
        if(state){labelOutcome.setText("YOU WIN");}
        labelOutcome.setPadding(new Insets(0,0,50,0));
        labelOutcome.setFont(new Font(MyStyles.fontGeneral, 100));



        HBox statsHBox = new HBox(125);
        VBox statsLeftVBox = new VBox();
        VBox statsRightVBox = new VBox();

        statsHBox.setAlignment(Pos.CENTER);


        Label labelTotalBombs = new Label("TOTAL BOMBS:");
        labelTotalBombs.setFont(new Font(MyStyles.fontGeneral, 30));

        Label labelTotalBombsVar = new Label(Integer.toString(myGame.getBombCount()));
        labelTotalBombsVar.setFont(new Font(MyStyles.fontGeneral, 30));


        Label labelBombsFound = new Label("BOMBS FOUND:");
        labelBombsFound.setFont(new Font(MyStyles.fontGeneral, 30));

        Label labelBombsFoundVar = new Label(Integer.toString(myGame.getCorrectlyFlagged()));
        labelBombsFoundVar.setFont(new Font(MyStyles.fontGeneral, 30));


        Label labelTime = new Label("TIME:");
        labelTime.setFont(new Font(MyStyles.fontGeneral, 30));

        Label labelTimeVar = new Label(myGame.getGameTime());
        labelTimeVar.setFont(new Font(MyStyles.fontGeneral, 30));


        Label labelDifficulty = new Label("DIFFICULTY:");
        labelDifficulty.setFont(new Font(MyStyles.fontGeneral, 30));

        Label labelDifficultyVar = new Label(myUtility.getDifficulty(myGame.getDifficulty()));
        labelDifficultyVar.setFont(new Font(MyStyles.fontGeneral, 30));

        statsLeftVBox.getChildren().addAll(labelTotalBombs, labelBombsFound, labelTime, labelDifficulty);
        statsRightVBox.getChildren().addAll(labelTotalBombsVar, labelBombsFoundVar, labelTimeVar, labelDifficultyVar);
        statsHBox.getChildren().addAll(statsLeftVBox, statsRightVBox);


        Button buttonReturnMain = new Button("BACK TO MAIN");
        buttonReturnMain.setFont(new Font(MyStyles.fontGeneral, 60));
        buttonReturnMain.setPadding(new Insets(20, 40, 20, 40));
        buttonReturnMain.setTextAlignment(TextAlignment.CENTER);
        buttonReturnMain.setOnAction(e-> switchScenes(mainScene));
        VBox.setMargin(buttonReturnMain, new Insets(50, 0, 20, 0));


        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(labelOutcome, statsHBox, buttonReturnMain);
        Scene postGameScene = new Scene(vbox, 960, 720);
        switchScenes(postGameScene);
    }
    private Scene createMainScene() {
        double r=50;

        Label labelMS = new Label("MINESWEEPER");
        labelMS.setFont(new Font(MyStyles.fontGeneral, 50));
        labelMS.setTextFill(Color.web("Red"));

        Button button1 = new Button();
        button1.setText("PLAY");
        button1.setMaxWidth(Double.MAX_VALUE);
        button1.setFont(new Font(MyStyles.fontGeneral, 30));
        //button1.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(button1, Priority.ALWAYS);
        button1.setShape(new Circle(r));
        button1.setMinSize(18*r, 2*r);
        button1.setMaxSize(18*r, 2*r);
        button1.setOnAction(e -> stage.setScene(gamePrepScene));

        Button button2 = new Button();
        button2.setText("LEADERBOARD");
        button2.setFont(new Font(MyStyles.fontGeneral, 30));
        button2.setMaxWidth(Double.MAX_VALUE);
        button2.setShape(new Circle(r));
        button2.setMinSize(18*r, 2*r);
        button2.setMaxSize(18*r, 2*r);
        button2.setOnAction(e-> switchScenes(leaderScene));

        Button button3 = new Button();
        button3.setText("TUTORIAL");
        button3.setFont(new Font(MyStyles.fontGeneral, 30));
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

        layout.setStyle(MyStyles.padding16);
        layout.setSpacing(50);


        Scene scene1 = new Scene(layout, 960, 720);
        return scene1;
    }
    private Scene createLeaderBoardScene() {
        BorderPane borderPane = new BorderPane();
        VBox vBox = new VBox();
        Button button = new Button("Back");

        Label labelLB = new Label("LEADERBOARD");
        labelLB.setFont(new Font(MyStyles.fontGeneral, 50));
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