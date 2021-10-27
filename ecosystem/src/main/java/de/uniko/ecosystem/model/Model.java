package de.uniko.ecosystem.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;

import java.util.ArrayList;
import java.util.List;

public class Model {

    public List<Tree> trees = new ArrayList<>();
    public List<Tree> treeBuffer = new ArrayList<>();
    public IntegerProperty currentEpisode = new SimpleIntegerProperty(0);

    public void update(){
        // update every updatable object


        // increment episode counter --> this triggers the re-rendering inside the Controller
        this.currentEpisode.set(this.currentEpisode.get() + 1);

        // write information about trees into csv file.
    }



}
