package simulation;

import org.apache.commons.lang3.ArrayUtils;
import simulation.entities.*;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.activations.Activation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Line;
import org.newdawn.slick.geom.Rectangle;
import simulation.field.Field;
import simulation.field.Zone;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


public class Simulation {
    private final int gazokBlueInitNum = 10;
    private final int gazokRedInitNum = 10;
    private final int gazokGreenInitNum = 10;
    private final ArrayList<GazokBlue> gazokBlueList = new ArrayList<>();
    private final ArrayList<GazokRed> gazokRedList = new ArrayList<>();
    private final ArrayList<GazokGreen> gazokGreenList = new ArrayList<>();
    private GazokBlue bestGazokBlue;
    private GazokRed bestGazokRed;
    private GazokGreen bestGazokGreen;
    private final Field field;
    private int time;

    public Simulation(Field field) {
        this.field = field;
        initGazoks();
        time = 0;
        bestGazokBlue = gazokBlueList.get(0);
        bestGazokRed = gazokRedList.get(0);
        bestGazokGreen = gazokGreenList.get(0);
    }

    public void updateGazokBlues() {
        for(GazokBlue gazok : gazokBlueList) {
            if(gazok.compareTo(bestGazokBlue) > 0) bestGazokBlue = gazok;
        }
        updateGeneralGazokParameters(gazokBlueList);
    }

    public void updateGazokReds() {
        gazokRedList.removeIf(gazok -> gazok.getEnergy() <= 0);
        ArrayList<GazokBlue> tempBlueGazokList = new ArrayList<>(gazokBlueList);
        for(GazokRed gazok : gazokRedList) {
            for(GazokBlue gazokBlue : tempBlueGazokList) {
                if(gazok.getShape().intersects(gazokBlue.getShape())) {
                    gazok.setEnergy(gazok.getEnergyMax());
                    gazok.incrementEatenBlues();
                    gazokBlueList.remove(gazokBlue);
                }
            }

            if(gazok.compareTo(bestGazokRed) > 0) bestGazokRed = gazok;
        }
        updateGeneralGazokParameters(gazokRedList);
    }

    public void updateGazokGreens() {
        gazokGreenList.removeIf(gazok -> gazok.getEnergy() <= 0);
        for (GazokGreen gazok : gazokGreenList) {
            for (GazokRed gazokRed : gazokRedList) {
                if (gazok.getHook().getLine().intersects(gazokRed.getShape())) {
                    if (!gazok.getHook().getConnected()) gazok.getHook().connect(gazokRed);
                    else {
                        gazok.getHook().getConnectedGazok()
                                .setEnergy(gazok.getHook().getConnectedGazok().getEnergy() - 1);
                        gazok.incrementEnergy();
                    }
                }
            }

            if(gazok.compareTo(bestGazokGreen) > 0) bestGazokGreen = gazok;
        }
        updateGeneralGazokParameters(gazokGreenList);
    }

    public boolean isEnded() {
        return gazokBlueList.isEmpty() || gazokRedList.isEmpty() || time>=2500;
    }

    public void save() throws IOException {
        bestGazokRed.getBrain().getNet().save(new File("bestGazokRed.zip"), true);
        bestGazokBlue.getBrain().getNet().save(new File("bestGazokBlue.zip"), true);
        bestGazokGreen.getBrain().getNet().save(new File("bestGazokGreen.zip"), true);
    }

    public void drawBackground(Graphics graphics) {
        graphics.setColor(Color.white);
        Rectangle menuBg = new Rectangle(1000,0, 500, 1000);
        graphics.draw(menuBg);
        graphics.fill(menuBg);
    }

    public void drawStats(TrueTypeFont trueTypeFont, Graphics graphics) {
        trueTypeFont.drawString(1030, 190, "Best blue gazok:", Color.black);
        trueTypeFont.drawString(1030, 220, "Activation Function 1: " +
                bestGazokBlue.getBrain().getFirstActivation().toString(), Color.black);
        trueTypeFont.drawString(1030, 250, "Activation Function 2: " +
                bestGazokBlue.getBrain().getSecondActivation().toString(), Color.black);
        trueTypeFont.drawString(1030, 280, "Time of life: " + bestGazokBlue.getTimeAlive(), Color.black);
        graphics.setColor(Color.black);
        graphics.draw(new Circle(bestGazokBlue.getX(), bestGazokBlue.getY(), bestGazokBlue.getRadius()+5));

        trueTypeFont.drawString(1030, 340, "Best red gazok:", Color.black);
        trueTypeFont.drawString(1030, 370, "Activation Function 1: " +
                bestGazokRed.getBrain().getFirstActivation().toString(), Color.black);
        trueTypeFont.drawString(1030, 400, "Activation Function 2: " +
                bestGazokRed.getBrain().getSecondActivation().toString(), Color.black);
        trueTypeFont.drawString(1030, 430, "Time of life: " + bestGazokRed.getTimeAlive(), Color.black);
        trueTypeFont.drawString(1030, 460, "Blue gazoks eaten: " +
                bestGazokRed.getEatenBlues(), Color.black);
        graphics.setColor(Color.black);
        graphics.draw(new Circle(bestGazokRed.getX(), bestGazokRed.getY(), bestGazokRed.getRadius()+5));

        trueTypeFont.drawString(1030, 520, "Best green gazok:", Color.black);
        trueTypeFont.drawString(1030, 550, "Activation Function 1: " +
                bestGazokGreen.getBrain().getFirstActivation().toString(), Color.black);
        trueTypeFont.drawString(1030, 580, "Activation Function 2: " +
                bestGazokGreen.getBrain().getSecondActivation().toString(), Color.black);
        trueTypeFont.drawString(1030, 610, "Time of life: " + bestGazokGreen.getTimeAlive(), Color.black);
        graphics.setColor(Color.black);
        graphics.draw(new Circle(bestGazokGreen.getX(), bestGazokGreen.getY(), bestGazokGreen.getRadius()+5));

        trueTypeFont.drawString(1030, 130, "Green gazoks amount: " + gazokGreenList.size(), Color.black);
        trueTypeFont.drawString(1030, 80, "Red gazoks amount: " + gazokRedList.size(), Color.black);
        trueTypeFont.drawString(1030, 30, "Blue gazoks amount: " + gazokBlueList.size(), Color.black);
    }

    public void drawGazoks(Graphics graphics) {
        for(GazokBlue gazok : gazokBlueList) {
            gazok.draw(graphics);
        }
        for(GazokRed gazok : gazokRedList) {
            gazok.draw(graphics);
        }
        for(GazokGreen gazokGreen : gazokGreenList) {
            gazokGreen.draw(graphics);
        }
    }

    public void incrementTime() {
        time++;
    }

    public void printBestGazokActivations() {
        System.out.println("Blue gazok's 1st activation function: " + bestGazokBlue.getBrain().getFirstActivation());
        System.out.println("Blue gazok's 2st activation function: " + bestGazokBlue.getBrain().getSecondActivation());
        System.out.println("Red gazok's 1st activation function: " + bestGazokRed.getBrain().getFirstActivation());
        System.out.println("Red gazok's 2st activation function: " + bestGazokRed.getBrain().getSecondActivation());
        System.out.println("Green gazok's 1st activation function: " + bestGazokGreen.getBrain().getFirstActivation());
        System.out.println("Green gazok's 2st activation function: " + bestGazokGreen.getBrain().getSecondActivation());
    }

    private <AnyGazok extends Gazok> void updateGeneralGazokParameters(ArrayList<AnyGazok> gazokList) {
        ArrayList<AnyGazok> tempGazokList = new ArrayList<>(gazokList);
        for(AnyGazok gazok : tempGazokList) {
            if(gazok.isBreedable() & gazoksCount()<50) gazokList.add((AnyGazok) gazok.duplicate());
        }
        for(AnyGazok gazok : gazokList) {
            gazok.act(getVision(gazok));
            setZone(gazok);
        }
    }

    private void initGazoks() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Load gazoks' brains? (y/n)");
        boolean load;
        String answer = scanner.nextLine();

        switch (answer) {
            case "y":
                load = true;
                break;
            case "n":
                load = false;
                break;
            default:
                load = true;
                System.out.println("U weirdo just type y or n idiot");
        }

        if(load) loadGazoks();
        else createNewGazoks();
    }

    private void loadGazoks() {
        System.out.println("Loading gazoks' brains...");
        MultiLayerNetwork blueBrain;
        try {
            blueBrain = MultiLayerNetwork.load(new File("bestGazokBlue.zip"), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        MultiLayerNetwork redBrain;
        try {
            redBrain = MultiLayerNetwork.load(new File("bestGazokRed.zip"), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        MultiLayerNetwork greenBrain;
        try {
            greenBrain = MultiLayerNetwork.load(new File("bestGazokGreen.zip"), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for(int i = 0; i<gazokBlueInitNum; i++) {
            GazokBlue gazokBlue = new GazokBlue(blueBrain, Activation.SIGMOID, Activation.RATIONALTANH);
            gazokBlueList.add(gazokBlue);
        }
        for(int i = 0; i<gazokRedInitNum; i++) {
            GazokRed gazokRed = new GazokRed(redBrain, Activation.IDENTITY, Activation.ELU);
            gazokRedList.add(gazokRed);
        }
        for(int i = 0; i<gazokGreenInitNum; i++) {
            GazokGreen gazokGreen = new GazokGreen(greenBrain, Activation.HARDTANH, Activation.GELU);
            gazokGreenList.add(gazokGreen);
        }
    }

    private void createNewGazoks() {
        System.out.println("Creating brand new gazoks...");
        for (int i = 0; i < gazokBlueInitNum; i++) {
            GazokBlue gazokBlue = new GazokBlue();
            gazokBlueList.add(gazokBlue);
        }
        for (int i = 0; i < gazokRedInitNum; i++) {
            GazokRed gazokRed = new GazokRed();
            gazokRedList.add(gazokRed);
        }
        for (int i = 0; i < gazokGreenInitNum; i++) {
            GazokGreen gazokGreen = new GazokGreen();
            gazokGreenList.add(gazokGreen);
        }
    }

    private double[] getVision(Gazok gazok) {
        return ArrayUtils.addAll(ArrayUtils.addAll(
                getVisionForList(gazok, gazokBlueList),
                getVisionForList(gazok, gazokRedList)
        ), getVisionForList(gazok, gazokGreenList));
    }

    private double[] getVisionForList(Gazok gazok, ArrayList<? extends Gazok> gazokList) {
        Line[] lines = gazok.getLines();
        double[] vision = new double[lines.length];
        float visionLength = 400;

        for(int lineNum = 0; lineNum<lines.length; lineNum++) {
            double shortestDistance = visionLength;
            for(Gazok anotherGazok : gazokList) {
                if(!gazok.equals(anotherGazok)) {
                    if (lines[lineNum].intersects(anotherGazok.getShape())) {
                        float x1 = gazok.getX();
                        float y1 = gazok.getY();
                        float x2 = anotherGazok.getX();
                        float y2 = anotherGazok.getY();

                        double distance = new Line(x1, y1, x2, y2).length();

                        if (distance < shortestDistance) {
                            shortestDistance = distance;
                        }
                    }
                }
            }
            vision[lineNum] = (visionLength-shortestDistance) / visionLength;
        }

        return vision;
    }

    private void setZone(Gazok gazok) {
        Zone zone = field.getZoneMap().get(new Coordinates(gazok.getX()/4, gazok.getY()/4));
        if(zone != null) gazok.setZone(zone);
    }

    private int gazoksCount() {
        return gazokBlueList.size() + gazokRedList.size() + gazokGreenList.size();
    }
}
