package utils;

public class AgentState {

    protected int x;
    private int y;
    private int health;

    public AgentState() {
        this(0,0);
    }

    public AgentState(int x, int y) {
        setX(x);
        setY(y);
        setHealth(100);
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
}
