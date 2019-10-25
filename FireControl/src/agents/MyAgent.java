package agents;

import launchers.SimulationLauncher;
import sajas.core.Agent;
import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;
import utils.AgentState;

import java.awt.*;

public abstract class MyAgent extends Agent implements Drawable {

    private transient SimulationLauncher environment;

    private AgentState state;

    public MyAgent(SimulationLauncher environment, Color color) {
        this.environment = environment;
        this.state = new AgentState(color);
    }

    @Override
    protected void setup() {
        super.setup();
    }

    @Override
    protected void takeDown() {
        super.takeDown();
    }

    public SimulationLauncher getEnvironment() {
        return environment;
    }

    public void setEnvironment(SimulationLauncher environment) {
        this.environment = environment;
    }

    public AgentState getState() {
        return state;
    }

    public void setState(AgentState state) {
        this.state = state;
    }

    @Override
    public void draw(SimGraphics simGraphics) {
        simGraphics.drawCircle(state.getColor());
    }

    @Override
    public int getX() {
        return state.getX();
    }

    @Override
    public int getY() {
        return state.getY();
    }

    public Color getColor() {
        return state.getColor();
    }
}
