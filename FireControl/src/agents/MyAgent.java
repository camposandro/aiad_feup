package agents;

import launchers.SimulationLauncher;
import sajas.core.Agent;
import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;
import utils.AgentState;

public abstract class MyAgent extends Agent implements Drawable {

    private transient SimulationLauncher environment;

    protected AgentState state;

    public MyAgent(SimulationLauncher environment) {
        this.environment = environment;
        this.state = new AgentState();
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
    public int getX() {
        return state.getX();
    }

    @Override
    public int getY() {
        return state.getY();
    }

    protected void updateState(int x, int y) {
        state.setX(x);
        state.setY(y);
    }
}
