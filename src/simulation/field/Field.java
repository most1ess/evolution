package simulation.field;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import simulation.Coordinates;

import java.util.TreeMap;

public class Field {
    private final Noise noise;
    private TreeMap<Coordinates, Zone> zoneMap;

    public Field() {
        int height = 250;
        int width = 250;
        noise = new Noise(null, 1.0F, width, height);
    }

    public void init() {
        noise.initialise();
        zoneMap = noise.getZoneMap();
    }

    public void draw(Graphics graphics) {
        for(java.util.Map.Entry<Coordinates, Zone> entry : zoneMap.entrySet()) {
            switch(entry.getValue()) {
                case NORMAL:
                    graphics.setColor(Color.gray);
                    break;
                case HEAL:
                    graphics.setColor(Color.pink);
                    break;
                default:
                    graphics.setColor(Color.cyan);
                    break;
            }

            Coordinates coordinates = entry.getKey();
            graphics.fill(new Rectangle(coordinates.x()*4, coordinates.y()*4, 4, 4));
        }
    }

    public TreeMap<Coordinates, Zone> getZoneMap() {
        return zoneMap;
    }
}
