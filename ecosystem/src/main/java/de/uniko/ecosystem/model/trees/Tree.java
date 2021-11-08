package de.uniko.ecosystem.model.trees;

import de.uniko.ecosystem.model.Model;
import de.uniko.ecosystem.util.DistPair;
import javafx.scene.shape.Rectangle;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

public abstract class Tree extends Rectangle implements Serializable {
    private static final int START_SIZE = 1;

    // the idea being that constanty performing euclidean distance check to
    // find the neighbors is too expensive. Once a tree is created, it should inform all
    // neighbors about its existance. The list keeps track of trees and their corresponding distances.
    List<DistPair> neighbors = new ArrayList<>();

    // threshhold
    private static final int THRESHOLD = 10;

    int health;
    int age;

    // diameter at breast height
    double diameter;

    protected Tree(int xpos, int ypos){
        super(xpos, ypos, START_SIZE, START_SIZE);
        this.health = 100;
        this.age = 0;

        // color tree accordingly
        this.setImagePattern();

        // update neighbors for this and all other trees.
        this.introduceToNeighbors();
    }


    private void introduceToNeighbors(){

        for(Tree other : Model.getInstance().getTrees()){

            double distance = this.distance(other);
            if(distance == 0){
                Model.getInstance().removeTree(this);
                break;
            }
            if(distance <= THRESHOLD){
                other.neighbors.add(new DistPair(this, distance));
                this.neighbors.add(new DistPair(other, distance));
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
        return Math.sqrt(Math.pow(this.getX() - other.getX(), 2) + Math.pow(this.getY() - other.getY(), 2));
    }

    public abstract void setImagePattern();

    public final void update(){
        treeSpecificUpdate();
        Model.getInstance().updateVolumeForClass(this.getClass(), this.getVolume());
    }

    public abstract void treeSpecificUpdate();

    public abstract void createOffspring();

    public double getVolume(){
        return 0d;
    }

    public void removeNeighbor(Tree other){
        DistPair dummy = new DistPair(other, 0d);
        this.neighbors.remove(dummy);
    }

    public List<DistPair> getNeighbors(){
        return this.neighbors;
    }

}
