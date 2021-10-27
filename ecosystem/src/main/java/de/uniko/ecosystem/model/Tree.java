package de.uniko.ecosystem.model;

import javafx.scene.shape.Rectangle;

public class Tree extends Rectangle {
    private static final int START_SIZE = 3;
    private TreeType type;
    private int health;
    private int age;

    public Tree(int xpos, int ypos, TreeType type){
        super(xpos, ypos, START_SIZE, START_SIZE);
        this.type = type;
        this.health = 100;
        this.age = 0;
    }


    public void update(){
        // this.health = ..

    }

    public void createOffspring(){
        // add new tree of same type to models buffer.
    }
}
