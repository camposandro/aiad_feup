package agents;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import launchers.SimulationLauncher;
import sajas.core.Agent;
import sajas.core.behaviours.OneShotBehaviour;
import uchicago.src.sim.gui.SimGraphics;
import utils.MapState;

import java.awt.*;
import java.util.Map;

public class FireStation extends MyAgent {

    public FireStation(SimulationLauncher launcher) {
        super(launcher, 0, 0);
        this.addBehaviour(new FirestationBehaviour(this));
    }

    @Override
    public void draw(SimGraphics simGraphics) {
        simGraphics.drawStringInRect(Color.DARK_GRAY, Color.WHITE, "Firestation");
    }

    public class FirestationBehaviour extends OneShotBehaviour {
        Agent agent;

        public FirestationBehaviour(Agent a) {
            agent = a;
        }

        @Override
        public void action() {
            ACLMessage informMsg = new ACLMessage(ACLMessage.INFORM);
            int i = 0;
            for (Map.Entry<AID, MyAgent> entry : getEnvironment().agents.entrySet()) {
                if (entry.getValue() instanceof Firefighter) {
                    AID firefighterAid = entry.getKey();
                    informMsg.addReceiver(firefighterAid);
                    informMsg.setConversationId("inform-fires");
                    MapCell a = MapState.fireCell1;
                    String dest = String.format("%d:%d", a.getX() - getEnvironment().getFirefighters().size() / 2 + 2 * i, a.getY() - getEnvironment().getFirefighters().size() / 2  + 2 * i);
                    informMsg.setContent(dest);
                    agent.send(informMsg);
                    System.out.println(informMsg);
                }
                i++;
            }
        }
    }
}
