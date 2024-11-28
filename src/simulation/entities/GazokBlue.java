package simulation.entities;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.activations.Activation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Line;

public class GazokBlue extends Gazok {
    public GazokBlue() {
        super();

        generateFields();

        brain = new Brain(neuronNum);
    }

    public GazokBlue(Brain brain) {
        super();

        generateFields();

        this.brain = brain;
    }

    public GazokBlue(MultiLayerNetwork net, Activation firstActivation, Activation secondActivation) {
        super();

        generateFields();

        brain = new Brain(neuronNum, firstActivation, secondActivation, net);
    }

    @Override
    public Gazok getInstance(Brain brain) {
        return new GazokBlue(brain);
    }

    @Override
    public Gazok duplicate() {
        energy = 0;

        return super.duplicate();
    }

    @Override
    public Line[] getLines() {
        Line[] lines = new Line[20];
        float angleRad = (float) (angle / 180.0 * Math.PI);

        for(int i = 0; i<20; i++) {
            lines[i] = getRotatedLine(angleRad, visionLength);

            angleRad += (float) (Math.PI / 10.0);
        }

        return lines;
    }

    private void generateFields() {
        energy = 0;
        energyMax = 25;
        try {
            image = new Image("gazokBlue.png");
        } catch (SlickException e) {
            System.out.println("No image for GazokBlue was found!");
        }

        radius = 5;
        color = Color.cyan;
        shape = new Circle(x, y, radius);
        neuronNum = new int[]{63, 3, 3};

        duplicationRadius = 50;
        mutationChance = 0.1;
        mutationRadius = 0.2;
    }

    public void act(double[] vision) {
        super.act(vision);

        int prediction = brain.predict(addZoneEffectToVision(vision));

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
                float[] newCoordinates = moveByDistance((float) (speed + speedBonus));
                x = (int) newCoordinates[0];
                y = (int) newCoordinates[1];
                break;
        }

        if(timeAlive%10 == 0) energy = energy + 1 + healBonus;
    }
}
