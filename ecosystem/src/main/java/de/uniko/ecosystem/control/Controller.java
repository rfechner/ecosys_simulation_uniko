package de.uniko.ecosystem.control;

import de.uniko.ecosystem.model.Model;

import de.uniko.ecosystem.model.trees.Tree;
import javafx.animation.PauseTransition;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
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
    public Button exportButton;

    @FXML
    public TextField exportPathTextField;


    @FXML
    public void startSimulationButtonPressed(){

        //query number of episodes to play
        this.numberOfEpisodes = 10;
        this.currentEpisode = 1;

        // init starting conditions
        this.model.init();

        this.timer = new PauseTransition(Duration.millis(1000));

        this.timer.setOnFinished( (e) -> {
            this.model.update();
            this.currentEpisode++;

            if(this.currentEpisode<=numberOfEpisodes){
                this.timer.playFromStart();
            } else {
                this.timer.stop();
                this.stopSimulation();
            }
        });

        this.timer.play();


    }

    public void onTreeListChange(ListChangeListener.Change<? extends Tree> change){
        while(change.next()){
            this.treePane.getChildren().addAll(change.getAddedSubList());
            this.treePane.getChildren().removeAll(change.getRemoved());
        }
    }

    @FXML
    public void onResetButtonPressed(ActionEvent actionEvent) {
        this.reset();
    }

    @FXML
    public void initialize(){
        this.model = Model.getInstance();
        this.model.getTreeList().addListener(this::onTreeListChange);
        // on list element added
    }

    private void reset(){
        if(this.timer != null){
            this.timer.stop();
        }

        // reset all objects in pane, reset all default values for sliders etc.
        this.treePane.getChildren().clear();
        this.rainSlider.setValue(50d);
        this.exportPathTextField.setDisable(true);
        this.exportButton.setDisable(true);

        // reset underlying model
        this.model.reset();
    }

    public void stopSimulation(){
        this.exportButton.setDisable(false);
        this.exportPathTextField.setDisable(false);
    }

    @FXML
    public void export(ActionEvent actionEvent) {
        String path = this.exportPathTextField == null ? "result.csv" : this.exportPathTextField.getText() + ".csv";
        this.model.export(path);
    }
}