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

import java.util.Date;

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
    public TextField tempMeanTextField;

    @FXML
    public TextField tempStdTextField;

    @FXML
    public void startSimulationButtonPressed(){
        this.startButton.setDisable(true);
        //query number of episodes to play
        this.numberOfEpisodes = (int)1e3;
        this.currentEpisode = 1;

        double ap = this.rainSlider.getValue();
        double tempMean = this.tempMeanTextField.getText() == null ? 10d : Double.parseDouble(this.tempMeanTextField.getText());
        double tempStd = this.tempStdTextField.getText() == null ? 1d : Double.parseDouble(this.tempStdTextField.getText());

        // init starting conditions
        this.model.init(ap, tempMean, tempStd);

        this.timer = new PauseTransition(Duration.millis(0));

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
        this.startButton.setDisable(false);
    }

    @FXML
    public void initialize(){
        this.model = Model.getInstance();
        this.model.getTreeList().addListener(this::onTreeListChange);
    }

    private void reset(){
        if(this.timer != null){
            this.timer.stop();
        }

        // reset all objects in pane, reset all default values for sliders etc.
        this.treePane.getChildren().clear();
        this.rainSlider.setValue(0d);
        this.exportPathTextField.setDisable(true);
        this.exportButton.setDisable(true);
        this.tempMeanTextField.clear();
        this.tempStdTextField.clear();

        // reset underlying model
        this.model.reset();
    }

    public void stopSimulation(){
        this.exportButton.setDisable(false);
        this.exportPathTextField.setDisable(false);
    }

    @FXML
    public void export(ActionEvent actionEvent) {
        String directory = "";
        String filename = this.exportPathTextField.getText() == null ? "result.csv" : this.exportPathTextField.getText() + ".csv";

        this.model.export(directory + filename);
    }
}