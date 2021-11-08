package de.uniko.ecosystem.model.trees;


import de.uniko.ecosystem.model.Model;
import de.uniko.ecosystem.util.DistPair;
import de.uniko.ecosystem.util.Pair;
import javafx.scene.paint.Color;

import java.util.Random;

public class Spruce extends Tree {
    private static Random random = new Random();
    protected Spruce(int xpos, int ypos) {
        super(xpos, ypos);

    }

    @Override
    public void setImagePattern() {
        this.setFill(Color.GREEN);
    }

    @Override
    public void treeSpecificUpdate() {
        // simple growth function for testing purposes
        if(random.nextDouble() > 0.9){
            this.setWidth(this.getWidth() + 1);
            this.setHeight(this.getHeight() + 1);
        }

        for(DistPair pair : this.neighbors){
            // put update functions here
        }

        // add offspring to treeBuffer

        // if this tree is dead, add tree to killbuffer and
        if(random.nextDouble() > 0.8){
            Model.getInstance().removeTree(this);
        }
    }

    @Override
    public void createOffspring() {
        // create a new tree in the near vicinity.
    }
}
