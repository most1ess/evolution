package simulation.entities;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Line;

public class Hook extends Entity{
    private final float startX;
    private final float startY;
    private final float range;
    private boolean connected;
    private boolean movingForward;
    private boolean movingBack;
    private Line line;
    private GazokRed connectedGazok;

    public Hook(float startX, float startY) {
        this.startX = startX;
        this.startY = startY;
        speed = 10;
        range = 300;
        x = (int) startX;
        y = (int) startY;
        connected = false;
        movingForward = false;
        movingBack = false;
        line = new Line(startX, startY, x, y);
    }

    public void startMoving() {
        if(!movingBack && !connected && !movingForward) {
            movingForward = true;
        }
    }

    public void move() {
        if(movingForward) {
            float[] newCoordinates = moveByDistance((float) speed);
            x = (int) newCoordinates[0];
            y = (int) newCoordinates[1];
            if(line.length() >= range/2) {
                movingForward = false;
                connected = false;
                movingBack = true;
            }
        }
        if(movingBack) {
            x = (int) startX;
            y = (int) startY;
            renew();
        }
        if(connected) {
            x = connectedGazok.x;
            y = connectedGazok.y;
            if(connectedGazok.getEnergy() <= 0) {
                x = (int) startX;
                y = (int) startY;
                renew();
            }
            if(line.length() >= range) {
                connected = false;
                movingForward = false;
                movingBack = true;
            }
        }
        line = new Line(startX, startY, x, y);
    }

    public void draw(Graphics graphics) {
        graphics.setColor(Color.green);
        graphics.draw(line);
    }

    public void connect(GazokRed gazokRed) {
        if(!movingBack && !connected) {
            connectedGazok = gazokRed;
            connected = true;
            movingForward = false;
        }
    }

    public void renew() {
        connected = false;
        movingForward = false;
        movingBack = false;
    }

    public Line getLine() {
        return line;
    }

    public boolean getConnected() {
        return connected;
    }

    public GazokRed getConnectedGazok() {
        return connectedGazok;
    }

    public void setAngle(float angle) {
        if(!movingForward && !movingBack && !connected) this.angle = angle;
    }
}
