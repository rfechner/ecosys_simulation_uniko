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

    private static Model instance;
    private static final Random random = new Random();
    private ObservableList<Tree> trees = FXCollections.observableArrayList(new ArrayList<>());
    private transient List<Tree> treeBuffer = new ArrayList<>();
    private transient SpriteHandler spriteHandler = new SpriteHandler();

    private Model(){

    }

    public static Model getInstance(){
        if(instance == null){
            instance = new Model();
        }

        return instance;
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
        int x_offset = 20; // draw 20 pixels more because of split screen border
        int width = 400;
        int height = 400;

        // idea: create perlin noise once, then let user decide parameters for terrain formation.
        for (int x = 0; x < width + x_offset; x+=4) {
            for (int y = 0; y < height; y+=4) {
                double currentX = (double) y / 20;
                double currentY = (double) x / 20;
                double noiseValue = PerlinNoise.noise(currentX,currentY,0);

                // 0.2 seems to be an okay-ish threshold for generating tree-groups
                if(noiseValue > 0.1 && random.nextDouble() > 0.8){
                    this.trees.add(Tree.createTree(x,y,Spruce.class));

                }

            }
        }

    }

    public void reset(){
        this.treeBuffer.clear();
        this.trees.clear();
    }


}
