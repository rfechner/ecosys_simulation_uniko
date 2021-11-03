package de.uniko.ecosystem.model.trees;


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
    public void update() {
        this.setX(this.getX()+random.nextDouble()*10);
        this.setY(this.getY()+random.nextDouble()*10);
    }

    @Override
    public void createOffspring() {
        // create a new tree in the near vicinity.
    }
}
