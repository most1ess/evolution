package simulation.entities;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.activations.Activation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Line;

public class GazokRed extends Gazok {
    private int eatenBlues;

    public GazokRed() {
        super();

        generateFields();

        brain = new Brain(neuronNum);
    }

    public GazokRed(Brain brain) {
        super();

        generateFields();

        this.brain = brain;
    }

    public GazokRed(MultiLayerNetwork net, Activation firstActivation, Activation secondActivation) {
        super();

        generateFields();

        brain = new Brain(neuronNum, firstActivation, secondActivation, net);
    }

    @Override
    public Gazok getInstance(Brain brain) {
        return new GazokRed(brain);
    }

    @Override
    public int compareTo(Gazok gazok) {
        int timeAliveComparison = super.compareTo(gazok);
        if(timeAliveComparison != 0) return timeAliveComparison;
        else {
            GazokRed gazokRed = (GazokRed) gazok;
            return Integer.compare(this.eatenBlues, gazokRed.getEatenBlues());
        }
    }

    @Override
    public Gazok duplicate() {
        energy = energyMax-1;

        return super.duplicate();
    }

    @Override
    public Line[] getLines() {
        Line[] lines = new Line[21];
        float angleRad = (float) (angle / 180.0 * Math.PI);

        for(int i = -10; i<11; i++) {
            float currentAngleRad = (float) (angleRad + Math.PI / 72.0 * (float) i);

            lines[10+i] = getRotatedLine(currentAngleRad, visionLength);
        }

        return lines;
    }

    private void generateFields() {
        try {
            image = new Image("gazokRed.png");
        } catch (SlickException e) {
            System.out.println("No image for GazokRed found!");
        }

        energy = 99;
        energyMax = 100;
        eatenBlues = 0;

        radius = 10;
        color = Color.red;
        shape = new Circle(x, y, radius);
        neuronNum = new int[]{66, 4, 4};

        duplicationRadius = 20;
        mutationChance = 0.1;
        mutationRadius = 0.2;
    }

    public void act(double[] vision) {
        super.act(vision);

        int prediction = brain.predict(addZoneEffectToVision(vision));

        float[] newCoordinates;
        switch(prediction) {
            case 0:
                angle+=3;
                image.rotate(3);
                break;
            case 1:
                angle-=3;
                image.rotate(-3);
                break;
            case 2:
                if(Math.random() < 0.5) energy -= 1;
                newCoordinates = moveByDistance((float) (speed + speedBonus)*2);
                x = (int) newCoordinates[0];
                y = (int) newCoordinates[1];
                break;
            case 3:
                newCoordinates = moveByDistance((float) (speed + speedBonus));
                x = (int) newCoordinates[0];
                y = (int) newCoordinates[1];
                break;
        }

        if(timeAlive%10 == 0) energy = energy - 2 + healBonus;
    }

    public void incrementEatenBlues() {
        eatenBlues++;
    }

    public int getEatenBlues() {
        return eatenBlues;
    }
}
