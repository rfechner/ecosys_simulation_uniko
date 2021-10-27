package de.uniko.ecosystem.control;

import de.uniko.ecosystem.model.Model;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;

public class Controller {

    public Model model;


    @FXML
    public Button startButton;

    @FXML
    public Slider rainSlider;

    @FXML
    public Pane treePane;

    @FXML
    public void startSimulationButtonPressed(){
        // readout deltaTime, totalEpisodes... additional information
        int deltaTime = 1;
        int numEpisodes = 1000;
        // create new Model from random noise
        this.model = new Model();


        while(this.model.currentEpisode.get() % deltaTime == 0){
            this.model.update();
        }

        // extract information?
    }

    @FXML
    public void initialize(){

    }
}