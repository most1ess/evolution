package simulation;

import org.newdawn.slick.*;
import simulation.field.Field;

import java.io.IOException;

public class Screen extends BasicGame {
    private Simulation simulation;
    private TrueTypeFont trueTypeFont;
    private Field field;

    public Screen(String title) {
        super(title);
    }

    @Override
    public void init(GameContainer gameContainer) {
        field = new Field();
        field.init();
        simulation = new Simulation(field);

        java.awt.Font font = new java.awt.Font("Verdana", java.awt.Font.BOLD, 20);
        trueTypeFont = new TrueTypeFont(font, true);
    }

    @Override
    public void update(GameContainer gameContainer, int i) {
        simulation.incrementTime();
        simulation.updateGazokBlues();
        simulation.updateGazokReds();
        simulation.updateGazokGreens();

        if(simulation.isEnded()) {
            try {
                simulation.save();
                simulation.printBestGazokActivations();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.exit(0);
        }
    }

    @Override
    public void render(GameContainer gameContainer, Graphics graphics) {
        simulation.drawBackground(graphics);
        field.draw(graphics);
        simulation.drawGazoks(graphics);
        simulation.drawStats(trueTypeFont, graphics);
    }
}

