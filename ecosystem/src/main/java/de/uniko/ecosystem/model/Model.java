package de.uniko.ecosystem.model;

import de.uniko.ecosystem.model.data.DataSet;
import de.uniko.ecosystem.model.data.listener.Listener;
import de.uniko.ecosystem.model.data.listener.TreeCountListener;
import de.uniko.ecosystem.model.data.listener.TreeVolumeListener;
import de.uniko.ecosystem.model.trees.Spruce;
import de.uniko.ecosystem.model.trees.Tree;
import de.uniko.ecosystem.util.DistPair;
import de.uniko.ecosystem.util.PerlinNoise;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Model implements Serializable {

    private static Model instance;
    private static final Random random = new Random();

    private final ObservableList<Tree> trees = FXCollections.observableArrayList(new ArrayList<>());
    private final transient List<Tree> addBuffer = new ArrayList<>();
    private final transient List<Tree> removeBuffer = new ArrayList<>();
    private final transient HashMap<Class<? extends Tree>, Integer> treeCountMap = new HashMap<>();
    private final transient HashMap<Class<? extends Tree>, Double> treeVolumeMap = new HashMap<>();

    private DataSet dataSet;


    private final transient SpriteHandler spriteHandler = new SpriteHandler();

    private Model(){

    }

    public static Model getInstance(){
        if(instance == null){
            instance = new Model();
        }

        return instance;
    }

    /** 1) add new trees (those spawned in last episode)
     *  2) delete dead trees (those that died in the last episode)
     *  3) update all trees
     *  4) store info for current episode
     */
    public void update(){

        // add new trees to tree list
        this.trees.addAll(this.addBuffer);
        this.addBuffer.clear();

        // remove any dead trees from list
        this.trees.removeAll(this.removeBuffer);

        // for each tree that should be removed, loop over all neighbors of
        // this specific tree and remove it from the neighbor list.
        for(Tree tree : removeBuffer){
            for(DistPair dp : tree.getNeighbors()){
                dp.other.removeNeighbor(tree);
            }
        }

        // finally, delete objects from list
        this.trees.removeAll(removeBuffer);

        this.removeBuffer.clear();

        // update every updatable object
        for (Tree tree : this.trees){
            tree.update();
        }

        // only now allow the Listeners to readout the values.
        this.dataSet.update();
    }


    public void addTree(Tree newTree){
        this.addBuffer.add(newTree);

        //incerements the tree counter
        this.treeCountMap.put(newTree.getClass(), this.treeCountMap.getOrDefault(newTree.getClass(), 0) + 1);
        this.treeVolumeMap.put(newTree.getClass(), this.treeVolumeMap.getOrDefault(newTree.getClass(), 0d) + newTree.getVolume());
    }

    public void removeTree(Tree deadTree){
        this.removeBuffer.add(deadTree);

        //decrements the tree counter
        this.treeCountMap.put(deadTree.getClass(), this.treeCountMap.get(deadTree.getClass()) - 1);
        if(this.treeCountMap.get(deadTree.getClass()) < 0){
            throw new IllegalStateException("removing a tree resulted in faulty behaviour. The total count of class "+deadTree.getClass()+
                    "was negative.");
        }

        // remove volume of tree class from map.
        this.treeVolumeMap.put(deadTree.getClass(), this.treeVolumeMap.get(deadTree.getClass()) - deadTree.getVolume());
        if(this.treeVolumeMap.get(deadTree.getClass()) < 0){
            throw new IllegalStateException("removing a tree resulted in faulty behaviour. The total volume of class "+deadTree.getClass()+
                    "was negative.");
        }

    }

    public ObservableList<Tree> getTrees(){
        return this.trees;
    }

    public void init(){
        //TODO: remove with version 1.0 -> extract to function call
        List<Listener> tmplist = new ArrayList<>();
        tmplist.add(new TreeCountListener(Spruce.class));
        tmplist.add(new TreeVolumeListener(Spruce.class));
        this.dataSet = new DataSet(tmplist);


        int xOffset = 20; // draw 20 pixels more because of split screen border
        int width = 400;
        int height = 400;

        // idea: create perlin noise once, then let user decide parameters for terrain formation.
        for (int x = 0; x < width + xOffset; x+=4) {
            for (int y = 0; y < height; y+=4) {
                double currentX = (double) y / 20;
                double currentY = (double) x / 20;
                double noiseValue = PerlinNoise.noise(currentX,currentY,0);

                // 0.2 seems to be an okay-ish threshold for generating tree-groups
                if(noiseValue > 0.1 && random.nextDouble() > 0.8){
                    this.addTree(Tree.createTree(x,y,Spruce.class));
                }
            }
        }
    }

    public void reset(){
        this.removeBuffer.clear();
        this.addBuffer.clear();
        this.trees.clear();
    }

    public Integer getTreeCountForClass(Class<?extends Tree> cls){
        return this.treeCountMap.getOrDefault(cls, 0);
    }

    public Double getVolumeForClass(Class<? extends Tree> cls){
        return this.treeVolumeMap.getOrDefault(cls, 0d);
    }

    public void export(String path){
        this.dataSet.writeToCSV(path);

    }

    public ObservableList<Tree> getTreeList(){
        return this.trees;
    }

    public void updateVolumeForClass(Class<? extends Tree> cls, double newValue){
        this.treeVolumeMap.put(cls, newValue - this.treeVolumeMap.get(cls));
    }

}
