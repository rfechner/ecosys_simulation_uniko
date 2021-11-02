package de.uniko.ecosystem.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Model implements Serializable {

    private static Model INSTANCE;

    private List<Tree> trees = new ArrayList<>();
    private transient List<Tree> treeBuffer = new ArrayList<>();
    private transient SpriteHandler spriteHandler = new SpriteHandler();

    public IntegerProperty currentEpisode = new SimpleIntegerProperty(0);

    private Model(){

    }

    public static Model getInstance(){
        if(INSTANCE == null){
            INSTANCE = new Model();
        }

        return INSTANCE;
    }

    public void update(){
        // update every updatable object
        for (Tree tree : this.trees){
            tree.update();
        }

        // add new trees to tree list
        trees.addAll(this.treeBuffer);
        this.treeBuffer.clear();


        // increment episode counter --> this triggers the re-rendering inside the Controller
        this.currentEpisode.set(this.currentEpisode.get() + 1);

        // write information about trees into csv file.
    }


    public void addTree(Tree newTree){
        this.treeBuffer.add(newTree);

    }

    public List<Tree> getTrees(){
        return this.trees;
    }



}
