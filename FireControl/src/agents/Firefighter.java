package agents;

import launchers.SimulationLauncher;
import uchicago.src.sim.gui.SimGraphics;

import java.awt.*;

public class Firefighter extends MyAgent {
    public Firefighter(SimulationLauncher launcher) {
        super(launcher);
    }

    @Override
    public void draw(SimGraphics simGraphics) {
        simGraphics.drawStringInHollowOval(Color.BLACK, Color.BLACK, "Firefighter");
    }
}
