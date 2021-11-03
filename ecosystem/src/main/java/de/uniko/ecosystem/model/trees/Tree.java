package de.uniko.ecosystem.model.trees;

import de.uniko.ecosystem.model.Model;
import de.uniko.ecosystem.util.Pair;
import javafx.scene.shape.Rectangle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Tree extends Rectangle implements Serializable {
    private static final int START_SIZE = 3;

    // the idea being that constanty performing euclidean distance check to
    // find the neighbors is too expensive. Once a tree is created, it should inform all
    // neighbors about its existance. The list keeps track of trees and their corresponding distances.
    private List<Pair<Tree, Double>> neighbors = new ArrayList<>();

    // threshhold
    private static final int THRESHOLD = 10;

    private int health;
    private int age;
    private int xpos, ypos;

    protected Tree(int xpos, int ypos){
        super(xpos, ypos, START_SIZE, START_SIZE);
        this.health = 100;
        this.age = 0;
        this.xpos = xpos;
        this.ypos = ypos;

        // color tree accordingly
        this.setImagePattern();

        // update neighbors for this and all other trees.
        this.informNeighbors();
    }


    private void informNeighbors(){
        for(Tree other : Model.getInstance().getTrees()){
            double distance = this.distance(other);

            if(distance <= THRESHOLD){
                other.neighbors.add(new Pair<Tree,Double>(this, distance));
                this.neighbors.add(new Pair<Tree,Double>(other, distance));
            }
        }
    }

    public static Tree createTree(int xpos, int ypos, Class<?> cls){
        Tree ret;

        switch (cls.getSimpleName()){
            case "Spruce": ret =  new Spruce(xpos, ypos); break;
            default : throw new IllegalArgumentException(cls.getSimpleName()+" is not recognized as a class extending Tree, or" +
                    "this functionality isnt implemented yet.");
        }

        return ret;
    }

    /**
     *
     * @param other Tree to be compared to
     * @return euclidean distance to other tree
     */
    private double distance(Tree other){
        return Math.sqrt(Math.pow(this.xpos - (double)other.xpos, 2) + Math.pow(this.ypos - (double)other.ypos, 2));
    }

    public abstract void setImagePattern();

    public abstract void update();

    public abstract void createOffspring();
}
