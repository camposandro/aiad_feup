package agents;


import launchers.SimulationLauncher;
import uchicago.src.sim.gui.SimGraphics;

import java.awt.*;

public class FireStation extends MyAgent {

    public FireStation(SimulationLauncher launcher) {
        super(launcher);
    }

    @Override
    public void draw(SimGraphics simGraphics) {
        simGraphics.drawStringInRect(Color.DARK_GRAY, Color.WHITE, "Firestation");
    }
}
