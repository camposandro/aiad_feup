package utils;

import sajas.core.Agent;

import java.awt.*;

public class AgentState {

    private int x;
    private int y;
    private int health;
    private Color color;

    public AgentState(Color color) {
        this(0,0, color);
    }

    public AgentState(int x, int y, Color color) {
        setX(x);
        setY(y);
        setHealth(100);
        setColor(color);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
