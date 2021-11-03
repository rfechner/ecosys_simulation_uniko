package de.uniko.ecosystem.control;

import de.uniko.ecosystem.model.Model;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;

public class Controller {

    private Model model;
    private int renderEveryNFrames;
    private int numEpisodes;

    @FXML
    public Button startButton;

    @FXML
    public Slider rainSlider;

    @FXML
    public Pane treePane;

    @FXML
    public void startSimulationButtonPressed(){
        this.reset();

        // init starting conditions
        this.model.init();
        this.treePane.getChildren().addAll(this.model.getTrees());


        Thread timer = new Thread(() -> {
            int numE = 10;
            int cur = 1;

            while(numE > cur++){
                Platform.runLater(() -> Model.getInstance().update());
                try{
                    Thread.sleep(1000);
                }catch (InterruptedException ie){
                    ie.printStackTrace();
                }
            }

        });
        timer.setDaemon(true);
        timer.start();
    }

    @FXML
    public void onResetButtonPressed(ActionEvent actionEvent) {
        this.reset();
    }

    @FXML
    public void initialize(){
        this.model = Model.getInstance();

        // on list element added
    }

    private void reset(){
        // reset all objects in pane, reset all default values for sliders etc.
        this.treePane.getChildren().clear();
        this.rainSlider.setValue(50d);


        // reset underlying model
        this.model.reset();
    }


}