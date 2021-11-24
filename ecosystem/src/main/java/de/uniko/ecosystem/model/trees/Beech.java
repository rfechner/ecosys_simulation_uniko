package de.uniko.ecosystem.model.trees;

import javafx.scene.paint.Color;

public class Beech extends Tree{
    private static final int HMAX = 2660;
    private static final int DMAX = 122;
    private static final float c = 2.2f/ 10000;
    private static final float b1 = 137f;
    private static final float b2 = 2f*(HMAX - b1) / DMAX;
    private static final float b3 = (HMAX-b1) / (DMAX*DMAX);
    private static final float d1 = 1f / (DMAX*HMAX);
    private static final float growthFactor = 150f;
    private static final float deathChance = 0.0129554f;
    private static final float shadeIntolerance = 0.35f;





    protected Beech(int xpos, int ypos, int age) {
        super(xpos, ypos, age);
    }

    @Override
    public void setImagePattern() {
        this.setFill(Color.RED);
    }


    public float c(){
        return c;
    }

    @Override
    public float HMAX() {
        return HMAX;
    }

    @Override
    public float DMAX() {
        return DMAX;
    }

    @Override
    public float b1() {
        return b1;
    }

    @Override
    public float b2() {
        return b2;
    }

    @Override
    public float b3() {
        return b3;
    }

    @Override
    public float d1() {
        return d1;
    }

    @Override
    public float growthFactor() {
        return growthFactor;
    }

    @Override
    public float dc() {
        return deathChance;
    }

    @Override
    public float shadeIntolerance() {
        return shadeIntolerance;
    }
}
