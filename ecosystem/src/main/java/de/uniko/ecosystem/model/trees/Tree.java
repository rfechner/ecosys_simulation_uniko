package de.uniko.ecosystem.model.trees;

import de.uniko.ecosystem.model.Model;
import de.uniko.ecosystem.util.DistPair;
import javafx.scene.shape.Rectangle;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

public abstract class Tree extends Rectangle implements Serializable {
    private static final int START_SIZE = 1;
    private static final int RENDER_SCALE = 10;
    List<DistPair> neighbors = new ArrayList<>();

    // threshhold
    public static int THRESHOLD = 30;
    private static final float coef1 = -1f / 5062500;
    private static final float coef2 = 11f / 10125;
    private static final float const1 = -40f / 81;
    private static final float PI = 3.1415f;


    // diameter at breast height
    double diameter;
    int badYearCounter = 0;

    // total height of tree
    double height, volume;
    double leafArea;

    protected Tree(int xpos, int ypos, int age){
        super(xpos, ypos, START_SIZE, START_SIZE);

        this.initialize(age);
        this.volume = this.height * this.diameter*this.diameter*0.25f*PI;
        this.leafArea = c()*this.diameter*this.diameter;
        this.setWidth(diameter / RENDER_SCALE);
        this.setHeight(diameter / RENDER_SCALE);

        // color tree accordingly
        this.setImagePattern();

        this.introduceToNeighbors();
    }


    private void introduceToNeighbors(){

        for(Tree other : Model.getInstance().getTrees()){

            double distance = this.distance(other.getX(), other.getY());
            if(!this.equals(other) && distance == 0){
                Model.getInstance().removeTree(this);
                break;
            }
            if(distance <= THRESHOLD){
                other.neighbors.add(new DistPair(this, distance));
                this.neighbors.add(new DistPair(other, distance));
            }
        }
    }

    public static Tree createTree(int xpos, int ypos, Class<?> cls, int age){
        Tree ret;

        switch (cls.getSimpleName()){
            case "Spruce":  ret = new Spruce(xpos, ypos, age); break;
            case "Fir":     ret = new Fir(xpos, ypos, age); break;
            case "Beech":   ret = new Beech(xpos, ypos, age); break;
            default : throw new IllegalArgumentException(cls.getSimpleName()+" is not recognized as a class extending Tree, or" +
                    "this functionality isnt implemented yet.");
        }

        return ret;
    }

    /**
     *
     * @param otherx xpos of other tree
     * @param othery ypos of other tree
     * @return euclidean distance to other tree
     */
    public double distance(double otherx, double othery){
        return Math.sqrt(Math.pow(this.getX() - otherx, 2) + Math.pow(this.getY() - othery, 2));
    }

    public abstract void setImagePattern();

    public void update(){
        if(this.diameter < 0){
            int debug = 1;
        }
        this.height = b1() + b2()*this.diameter - b3()*this.diameter*this.diameter;

        if(this.height < 0){
            int debug = 1;
        }
        double oldVolume = this.volume;

        // volume approximated by volume of cylinder with tree height and base with radius diameter/2.
        this.volume = this.height * this.diameter*this.diameter*0.25f*PI;

        // update the models database for tree volume by class
        Model.getInstance().updateVolumeForClass(this.getClass(), this.volume - oldVolume);

        this.leafArea = c()*this.diameter*this.diameter;

        double delta_diameter = 0.5f*(firstDerivative(this.diameter, false) + firstDerivative( this.diameter + firstDerivative(this.diameter, false), false));

        this.diameter += delta_diameter;

        // scaled to reasonable size, for rendering
        this.setWidth(this.diameter / RENDER_SCALE);
        this.setHeight(this.diameter / RENDER_SCALE);

        // if the increase in diameter isn't high enough,
        // we model the current year as a bad year.
        // The more consecutive bad years a tree has, the higher its
        // chances of dying.
        if(delta_diameter < 0.01){
            this.badYearCounter ++;
        } else {
            this.badYearCounter = 0;
        }

        double deathChance;

        if(badYearCounter > 0){
            deathChance = 1 - Math.exp(-0.0837*badYearCounter);
        } else {
            deathChance = this.dc();
        }



        if(Math.random() < deathChance){
            Model.getInstance().removeTree(this);
        }

    }

    public void initialize(int age){
        this.diameter = 5;

        // run update() with no penalty
        for(int i = 0; i < age; i++){
            this.height = b1() + b2()*this.diameter - b3()*this.diameter*this.diameter;
            this.diameter += 0.5f*(firstDerivative(this.diameter, true) + firstDerivative( this.diameter + firstDerivative(this.diameter, true), true));
        }
    }

    public double firstDerivative(double D, boolean init){


        double denominator = (274 + 3*b2()*D - 4*b3()*D*D);
        if(denominator <0){
            int debug = 1;
        }

        if((1 - this.height*D*d1())/denominator  < 0){
            int debug = 1;
        }

        if(!init && Model.getInstance().getEpisode() == 10){
            int debug = 1;
        }

        return growthFactor() * D *
                (1 - this.height*D*d1())/denominator * (init ? 1 : lightPenalty()*waterPenalty()*temperaturePenalty());
    }

    public double getVolume(){
        return this.volume;
    }

    public void removeNeighbor(Tree other){
        DistPair dummy = new DistPair(other, 0d);
        this.neighbors.remove(dummy);
    }
    public List<DistPair> getNeighbors(){
        return this.neighbors;
    }

    public double neighborLeafArea(){
        double result = 0;

        for(DistPair dp : this.neighbors){
            if(dp.other.height >= this.height){
                result += dp.other.leafArea;
            }
        }

        return result;
    }

    //TODO: smarter function?
    public double lightPenalty(){
        // assuming Qmax = 1
        return Math.exp(-shadeIntolerance() * this.neighborLeafArea() / 1e4);
    }

    //TODO: is this right?
    public double waterPenalty(){
        // cancelling factors in advance to reduce computation time
        // 1e7 / (2.54 * 264.172) * 52 = 774966.34559
        return Math.min(1, Math.max(0.01, this.leafArea* Model.getInstance().getAP() /((getW(this.diameter)*this.diameter * 774966.34559))));
    }

    public double temperaturePenalty(){
        double dd = Model.getInstance().getDD();
        return Math.min(1, Math.max(0.01,coef1*dd*dd + coef2*dd - const1));
    }

    public abstract float c();
    public abstract float HMAX();
    public abstract float DMAX();
    public abstract float b1();
    public abstract float b2();
    public abstract float b3();
    public abstract float d1();
    public abstract float growthFactor();
    public abstract float dc();
    public abstract float shadeIntolerance();

    /**
     *
     * @param diameter the diameter of the tree at breast height
     * @return 1, if the tree's diameter is small, elsewise the
     *          result of a linear function
     */
    public static double getW(double diameter){
        return diameter <= 5d ? 0.01 : 0.0036*diameter - 0.008;
    }





}
