package simulation.entities;

import org.newdawn.slick.geom.Line;

public abstract class Entity {
    protected int x;
    protected int y;
    protected float angle;
    protected double speed;

    public float[] moveByDistance(float distance) {
        float angleRad = (float) (angle / 180.0 * Math.PI);
        Line line = getRotatedLine(angleRad, distance);
        float newX  = line.getEnd().x;
        float newY = line.getEnd().y;

        return new float[]{newX, newY};
    }

    public Line getRotatedLine(float angleRad, float length) {
        float xEnd = (float) (length * Math.cos(angleRad) + x);
        float yEnd = (float) (length * Math.cos(Math.PI / 2.0 - angleRad) + y);

        return new Line(x, y, xEnd, yEnd);
    }
}
