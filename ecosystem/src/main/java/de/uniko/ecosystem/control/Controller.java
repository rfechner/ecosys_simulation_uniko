package de.uniko.ecosystem.control;

import de.uniko.ecosystem.model.Model;

import de.uniko.ecosystem.model.trees.Tree;
import javafx.animation.PauseTransition;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class Controller {



    private Model model;
    private PauseTransition timer;
    private int numberOfEpisodes;
    private int currentEpisode;

    public static int WIDTH, HEIGHT;


    @FXML
    public Button startButton;

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
    public TextField apMeanTextField;

    @FXML
    public TextField apStdTextField;

    @FXML
    public TextField offspringRateTextField;

    @FXML
    public void startSimulationButtonPressed(){
        this.startButton.setDisable(true);
        //query number of episodes to play
        this.numberOfEpisodes = (int)300;
        this.currentEpisode = 1;

        WIDTH = (int)this.treePane.getWidth();
        HEIGHT = (int)this.treePane.getHeight();

        // get user input values
        double tempMean = this.tempMeanTextField.getText().isEmpty() ? 10d : Double.parseDouble(this.tempMeanTextField.getText().replace(",", "."));
        double tempStd = this.tempStdTextField.getText().isEmpty() ? 1d : Double.parseDouble(this.tempStdTextField.getText().replace(",", "."));

        double apMean = this.apMeanTextField.getText().isEmpty() ? 700d : Double.parseDouble(this.apMeanTextField.getText().replace(",", "."));
        double apStd = this.apStdTextField.getText().isEmpty() ? 50d : Double.parseDouble(this.apStdTextField.getText().replace(",", "."));

        double offspringRate_percent = this.offspringRateTextField.getText().isEmpty() ? 1 : Double.parseDouble(this.offspringRateTextField.getText().replace(",", "."));

        // init starting conditions
        this.model.init(apMean, apStd, tempMean, tempStd, offspringRate_percent / 100d);

        this.timer = new PauseTransition(Duration.millis(2000));

        this.timer.setOnFinished( (e) -> {
            this.model.update();
            this.currentEpisode++;

            if(this.currentEpisode<=numberOfEpisodes){
                if(currentEpisode == 2){
                    timer.setDuration(Duration.millis(10));
                }
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
        this.exportPathTextField.setDisable(true);
        this.exportButton.setDisable(true);
        this.tempMeanTextField.clear();
        this.tempStdTextField.clear();
        this.apMeanTextField.clear();
        this.apStdTextField.clear();
        this.offspringRateTextField.clear();

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