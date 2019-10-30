package agents;

import launchers.SimulationLauncher;
import sajas.core.Agent;
import sajas.core.behaviours.TickerBehaviour;
import uchicago.src.sim.gui.SimGraphics;

import java.awt.*;

public class Firefighter extends MyAgent {
    public Firefighter(SimulationLauncher launcher) {
        super(launcher);
        this.addBehaviour(new FirefighterBehaviour(this,1000));
    }

    @Override
    public void draw(SimGraphics simGraphics) {
        simGraphics.drawStringInHollowOval(Color.BLACK, Color.BLACK, "Firefighter");
    }

    public class FirefighterBehaviour extends TickerBehaviour {
        Agent agent;
        public FirefighterBehaviour(Agent a, long period) {
            super(a, period);
            agent = a;
        }
        @Override
        protected void onTick() {
            ((Firefighter) agent).updateState(1,1);
        }
    }
}
