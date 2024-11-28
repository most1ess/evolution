package simulation.entities;

import org.apache.commons.lang3.ArrayUtils;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Line;
import simulation.field.Zone;

public abstract class Gazok extends Entity implements Comparable<Gazok> {
    protected static final int xMax = 1000;
    protected static final int yMax = 1000;

    protected double speedBonus;
    protected double energy;
    protected double healBonus;
    protected double energyMax;

    protected float radius;
    protected Color color;
    protected Circle shape;
    protected Image image;
    protected Zone zone;
    protected int timeAlive;
    protected int visionLength;

    protected Brain brain;
    protected int[] neuronNum;

    protected int duplicationRadius;
    protected double mutationChance;
    protected double mutationRadius;

    public Gazok() {
        healBonus = 0;
        speedBonus = 0;
        speed = 4;
        visionLength = 400;
        x = (int) (Math.random() * xMax);
        y = (int) (Math.random() * yMax);

        angle = -90;
        timeAlive = 0;
    }

    @Override
    public int compareTo(Gazok gazok) {
        return Integer.compare(timeAlive, gazok.timeAlive);
    }

    public abstract Gazok getInstance(Brain brain);

    public abstract Line[] getLines();

    public void draw(Graphics graphics) {
        shape.setCenterX(x);
        shape.setCenterY(y);

        image.draw(x-radius,y-radius,radius*2, radius*2);
    }

    public Gazok duplicate() {
        Gazok gazok = getInstance(brain.mutate(mutationChance, mutationRadius));

        int newX = x + (int) (duplicationRadius/2 - Math.random() * duplicationRadius);
        int newY = y + (int) (duplicationRadius/2 - Math.random() * duplicationRadius);
        gazok.setX(newX);
        gazok.setY(newY);

        return gazok;
    }

    public void act(double[] vision) {
        if(y>yMax) y = 0;
        if(y<0) y = yMax;
        if(x>xMax) x = 0;
        if(x<0) x = xMax;

        if(zone!=null) {
            switch (zone) {
                case NORMAL:
                    healBonus = 0;
                    speedBonus = 0;
                    break;
                case SPEED:
                    healBonus = 0;
                    speedBonus = 2;
                    break;
                case HEAL:
                    healBonus = 1;
                    speedBonus = 0;
            }
        }

        timeAlive++;
    }

    public double[] addZoneEffectToVision(double[] vision) {
        double[] zoneEffect = new double[3];

        if(zone!=null) {
            switch (zone) {
                case NORMAL:
                    zoneEffect[0] = 1;
                    break;
                case SPEED:
                    zoneEffect[1] = 1;
                    break;
                case HEAL:
                    zoneEffect[2] = 1;
            }
        }

        return ArrayUtils.addAll(vision, zoneEffect);
    }

    public boolean isBreedable() {
        return energy >= energyMax;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getTimeAlive() {
        return timeAlive;
    }

    public double getEnergy() {
        return energy;
    }

    public double getEnergyMax() {
        return energyMax;
    }

    public float getRadius() {
        return radius;
    }

    public Circle getShape() {
        return shape;
    }

    public Brain getBrain() {
        return brain;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setEnergy(double energy) {
        this.energy = energy;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }
}