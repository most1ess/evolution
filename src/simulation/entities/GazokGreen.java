package simulation.entities;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.activations.Activation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Line;

public class GazokGreen extends Gazok {
    private Hook hook;

    public GazokGreen() {
        super();

        generateFields();

        brain = new Brain(neuronNum);
    }

    public GazokGreen(Brain brain) {
        super();

        generateFields();

        this.brain = brain;
    }

    public GazokGreen(MultiLayerNetwork net, Activation firstActivation, Activation secondActivation) {
        super();

        generateFields();

        brain = new Brain(neuronNum, firstActivation, secondActivation, net);
    }

    @Override
    public Gazok getInstance(Brain brain) {
        return new GazokGreen(brain);
    }

    @Override
    public GazokGreen duplicate() {
        GazokGreen gazokGreen = (GazokGreen) super.duplicate();
        gazokGreen.generateHook();
        energy = 30;
        return gazokGreen;
    }

    @Override
    public void draw(Graphics graphics) {
        super.draw(graphics);

        hook.draw(graphics);
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
        energy = 89;
        energyMax = 90;
        try {
            image = new Image("gazokGreen.png");
        } catch (SlickException e) {
            System.out.println("No image for GazokBlue was found!");
        }

        radius = 7;
        color = Color.cyan;
        shape = new Circle(x, y, radius);
        neuronNum = new int[]{66, 3, 3};

        duplicationRadius = 45;
        mutationChance = 0.1;
        mutationRadius = 0.2;
        generateHook();
    }

    public void act(double[] vision) {
        super.act(vision);

        int prediction = brain.predict(addZoneEffectToVision(vision));

        hook.move();

        switch(prediction) {
            case 0:
                angle+=3;
                hook.setAngle(angle);
                image.rotate(3);
                break;
            case 1:
                angle-=3;
                hook.setAngle(angle);
                image.rotate(-3);
                break;
            case 2:
                hook.startMoving();
                break;
        }

        if(timeAlive%10 == 0) energy = energy - 2 + healBonus;
    }

    public void incrementEnergy() {
        energy++;
    }

    public Hook getHook() {
        return hook;
    }

    public void generateHook() {
        hook = new Hook(x, y);
    }
}
