package de.uniko.ecosystem.model;

import de.uniko.ecosystem.model.data.DataSet;
import de.uniko.ecosystem.model.data.listener.Listener;
import de.uniko.ecosystem.model.data.listener.TreeCountListener;
import de.uniko.ecosystem.model.data.listener.TreeVolumeListener;
import de.uniko.ecosystem.model.trees.*;
import de.uniko.ecosystem.util.DistPair;
import de.uniko.ecosystem.util.Pair;
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
    private final transient ArrayList<Class<? extends Tree>> treeClasses = new ArrayList<>();



    private int currentEpisode = 0;
    private final double ERROR_TOLERANCE = 1e-2;

    private DataSet dataSet;

    private double annualPercipitation;
    private double avgTemp;
    private double tempMean;
    private double tempStd;


    private final transient SpriteHandler spriteHandler = new SpriteHandler();

    private Model(){
        this.treeClasses.add(Spruce.class);
        this.treeClasses.add(Fir.class);
        this.treeClasses.add(Beech.class);

        List<Listener> tmplist = new ArrayList<>();

        for(Class<? extends Tree> cls : this.treeClasses){
            tmplist.add(new TreeCountListener(cls));
            tmplist.add(new TreeVolumeListener(cls));
        }

        this.dataSet = new DataSet(tmplist);
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
        this.currentEpisode ++;

        // calculate average Temperature for this episode.
        this.avgTemp = random.nextGaussian()*this.tempStd + this.tempMean;

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
                    "was negative :" + this.treeCountMap.get(deadTree.getClass()));
        }

        // remove volume of tree class from map.
        this.updateVolumeForClass(deadTree.getClass(), deadTree.getVolume()*-1d);

        // minor errors in volume can occur, when removing the volume of the last tree of
        // a species.
        if(this.treeVolumeMap.get(deadTree.getClass()) < 0 && -this.treeVolumeMap.get(deadTree.getClass()) > ERROR_TOLERANCE){
            throw new IllegalStateException("removing a tree resulted in faulty behaviour. The total volume of class "+deadTree.getClass()+
                    "was negative :" +this.treeVolumeMap.get(deadTree.getClass()));
        }

    }

    public ObservableList<Tree> getTrees(){
        return this.trees;
    }

    public void init(double ap, double tempMean, double tempStd){

        this.annualPercipitation = ap;
        this.tempMean = tempMean;
        this.tempStd = tempStd;

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
                    Pair<Class<? extends Tree>, Integer>tmp = getTreeFromDistribution();
                    this.addTree(Tree.createTree(x,y,tmp.first, tmp.second));
                }
            }
        }
    }

    public void reset(){
        this.currentEpisode = 0;
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

    public void updateVolumeForClass(Class<? extends Tree> cls, double difference){
        this.treeVolumeMap.put(cls, this.treeVolumeMap.getOrDefault(cls, 0d) + difference);
    }

    /**
     *
     * @return 12 months * 30 days * difference of average temperature and base temperature
     */
    public double getDD(){
        return 12 * 30 * (this.avgTemp - 4.4d);
    }

    public double getAP(){
        return this.annualPercipitation;
    }

    public Pair<Class<? extends Tree>, Integer> getTreeFromDistribution(){
        /* distributing the probability for lesser known tree-species
            to their nearest relative.
        Beech: 0.652
        Spruce: 0.206 + 0.068 = 0.274
        Fir: 0.006 + 0.068 = 0.074

         */
        double tmp = random.nextDouble();
        int age;
        Class<? extends Tree> cls;

        if(tmp <= 0.652){
            cls = Beech.class;
        } else if(tmp <= 0.926){
            cls = Spruce.class;
        } else{
            cls = Fir.class;
        }

        tmp = random.nextDouble();

        if(tmp <= 0.073){
            age = random.nextInt(15) + 5;
        } else if(tmp <= 0.219){
            age = random.nextInt(20) + 20;
        } else if(tmp <= 0.418) {
            age = random.nextInt(20) + 40;
        } else if(tmp <= 0.59){
            age = random.nextInt(20) + 60;
        } else if(tmp <= 0.708) {
            age = random.nextInt(20) + 80;
        } else if(tmp <= 0.807) {
            age = random.nextInt( 20) + 100;
        } else {
            // distribute the prob for older trees onto
            // all younger trees.
            age = random.nextInt(50) + 5;
        }

        return new Pair<>(cls ,age);
    }

    public int getEpisode(){
        return this.currentEpisode;
    }

}
