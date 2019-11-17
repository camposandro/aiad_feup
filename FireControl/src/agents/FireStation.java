package agents;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import launchers.SimulationLauncher;
import sajas.core.behaviours.CyclicBehaviour;
import sajas.core.behaviours.OneShotBehaviour;
import sajas.core.behaviours.SequentialBehaviour;
import uchicago.src.sim.gui.SimGraphics;
import utils.MapState;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class FireStation extends MyAgent {

    private SequentialBehaviour behaviour;

    public FireStation(SimulationLauncher launcher) {
        super(launcher, 0, 0);

        behaviour = new SequentialBehaviour();
        behaviour.addSubBehaviour(new InformInitialFire());
        behaviour.addSubBehaviour(new HandleWaterRequests());
        this.addBehaviour(behaviour);
    }

    @Override
    public void draw(SimGraphics simGraphics) {
        simGraphics.drawStringInRect(Color.DARK_GRAY, Color.WHITE, "Firestation");
    }

    public class InformInitialFire extends OneShotBehaviour {
        @Override
        public void action() {
            ACLMessage informMsg = new ACLMessage(ACLMessage.INFORM);

            List<MapCell> fireCells = new ArrayList<>();
            Iterator<MapCell> iter = MapState.getFireCells().iterator();
            while (iter.hasNext()) {
                fireCells.add(iter.next());
            }

            int firesIdx = 0;
            MapCell currentCell;
            for (Map.Entry<AID, MyAgent> entry : getEnvironment().agents.entrySet()) {
                if (entry.getValue() instanceof Firefighter) {
                    currentCell = fireCells.get(firesIdx);

                    AID firefighterAid = entry.getKey();
                    informMsg.clearAllReceiver();
                    informMsg.addReceiver(firefighterAid);
                    informMsg.setConversationId("inform-fires");

                    String dest = String.format("%d:%d", currentCell.getX(), currentCell.getY());
                    informMsg.setContent(dest);
                    send(informMsg);

                    if (fireCells.size() - 1 == firesIdx) {
                        firesIdx = -1;
                    }
                    firesIdx++;
                }
            }
        }
    }

    public class HandleWaterRequests extends CyclicBehaviour {
        private void handleWaterRequests() {
            MessageTemplate mtWaterRequest = MessageTemplate.MatchConversationId("request-water");
            ACLMessage reqMsg = receive(mtWaterRequest);
            // Water request message received
            if (reqMsg != null && reqMsg.getPerformative() == ACLMessage.REQUEST) {
                ACLMessage reply = reqMsg.createReply();
                reply.setPerformative(ACLMessage.INFORM);

                String waterStr = "";
                Iterator<MapCell> i = MapState.getWaterCells().iterator();
                while (i.hasNext()) {
                    MapCell cell = i.next();
                    waterStr += String.format("%d:%d,", cell.getX(), cell.getY());
                }
                reply.setContent(waterStr);
                send(reply);
            } else {
                block();
            }
        }

        @Override
        public void action() {
            handleWaterRequests();
        }
    }
}
