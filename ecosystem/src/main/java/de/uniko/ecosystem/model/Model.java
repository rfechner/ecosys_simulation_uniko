package de.uniko.ecosystem.model;

import de.uniko.ecosystem.model.trees.Spruce;
import de.uniko.ecosystem.model.trees.Tree;
import de.uniko.ecosystem.util.PerlinNoise;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Model implements Serializable {

    private static double NUM_MAX_TREES = 1e2;
    private static Model INSTANCE;
    private static final Random random = new Random();
    private ObservableList<Tree> trees = FXCollections.observableArrayList(new ArrayList<>());
    private transient List<Tree> treeBuffer = new ArrayList<>();
    private transient SpriteHandler spriteHandler = new SpriteHandler();

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
        this.trees.addAll(this.treeBuffer);
        this.treeBuffer.clear();


        // write information about trees into csv file.
    }


    public void addTree(Tree newTree){
        this.treeBuffer.add(newTree);

    }

    public ObservableList<Tree> getTrees(){
        return this.trees;
    }

    public void init(){

        for (int i = 0; i < 200; i++) {
            for (int j = 0; j < 200; j++) {
                double currentX = (double) j / 10;
                double currentY = (double) i / 10;
                double prob = PerlinNoise.noise(currentX,currentY,0);
                if(prob > 0.3){
                    // tree at that position
                    this.trees.add(Tree.createTree(i,j,Spruce.class));
                }

            }
        }

    }

    public void reset(){
        this.treeBuffer.clear();
        this.trees.clear();
    }


}
