package agents;

import launchers.SimulationLauncher;
import uchicago.src.sim.gui.SimGraphics;

import java.awt.*;

public class Fire extends MyAgent {
    public Fire(SimulationLauncher launcher, int x, int y) {
        super(launcher);
        this.updateState(x,y);
    }

    @Override
    public void draw(SimGraphics simGraphics) {
        simGraphics.drawStringInRect(Color.RED, Color.WHITE, "Fire");
    }
}
