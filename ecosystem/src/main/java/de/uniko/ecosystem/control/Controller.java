package de.uniko.ecosystem.control;

import de.uniko.ecosystem.model.Model;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class Controller {

    private Model model;
    private PauseTransition timer;
    private int numberOfEpisodes;
    private int currentEpisode;

    @FXML
    public Button startButton;

    @FXML
    public Slider rainSlider;

    @FXML
    public Pane treePane;

    @FXML
    public void startSimulationButtonPressed(){

        //query number of episodes to play
        this.numberOfEpisodes = 100;
        this.currentEpisode = 1;

        // init starting conditions
        this.model.init();
        this.treePane.getChildren().addAll(this.model.getTrees());


        this.timer = new PauseTransition(Duration.millis(1000));

        this.timer.setOnFinished( (e) -> {
            Model.getInstance().update();
            this.currentEpisode++;

            if(this.currentEpisode<=numberOfEpisodes){
                this.timer.playFromStart();
            } else {
                this.timer.stop();
            }
        });

        this.timer.play();


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
        if(this.timer != null){
            this.timer.stop();
        }

        // reset all objects in pane, reset all default values for sliders etc.
        this.treePane.getChildren().clear();
        this.rainSlider.setValue(50d);


        // reset underlying model
        this.model.reset();
    }


}