package agents;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import launchers.SimulationLauncher;
import sajas.core.Agent;
import sajas.core.behaviours.CyclicBehaviour;
import sajas.core.behaviours.TickerBehaviour;
import uchicago.src.sim.gui.SimGraphics;
import utils.MapState;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Firefighter extends MyAgent {

    int viewingDistance = 3;
    MapCell[][] perception = new MapCell[viewingDistance * 2][viewingDistance * 2];
    HashSet<MapCell> fires = new HashSet();
    int extinguishDistance = 1;
    private int fireTicks = 1;
    private int ticksPeriod = 0;

    enum State {Waiting, Driving, Searching, Extinguishing, Refilling}

    State currentState;
    int[] destination = new int[2];
    int[] pos;

    int speed;
    int health;
    int water;
    int maxCapacity;
    int pumpingVelocity;

    public Firefighter(SimulationLauncher launcher, int x, int y) {
        super(launcher, x, y);
        destination[0] = -1;
        currentState = State.Waiting;
        this.addBehaviour(new FirefighterBehaviour(this, SimulationLauncher.FF_UPDATE_RATE));
        this.addBehaviour(new PerceptionBehaviour(this));
    }

    @Override
    public void draw(SimGraphics simGraphics) {
        simGraphics.drawHollowOval(Color.BLACK);
    }

    protected void updatePerception() {
        fires.clear();
        for(int i = 0; i < viewingDistance * 2; i++)
            for(int j = 0; j < viewingDistance * 2; j++) {
                MapCell cell = MapState.getGridPos(state.getX() - viewingDistance + i, state.getY() - viewingDistance + j);
                perception[i][j] = cell;
                if(cell != null && cell.isOnFire()) {
                    fires.add(cell);
                    this.currentState = State.Extinguishing;
                }
            }
    }

    protected void move() {
        if (destination[0] >= getEnvironment().getEnvWidth()) {
            destination[0] = getEnvironment().getEnvWidth() - 1;
        } else if (destination[0] < 0) {
            destination[0] = 0;
        }
        if (destination[1] >= getEnvironment().getEnvHeight()) {
            destination[1] = getEnvironment().getEnvHeight() - 1;
        } else if (destination[1] < 0) {
            destination[1] = 0;
        }
        if (state.getX() < destination[0])
            setX(state.getX() + 1);
        else if (state.getX() > destination[0])
            setX(state.getX() - 1);
        if (state.getY() < destination[1])
            setY(state.getY() + 1);
        else if (state.getY() > destination[1])
            setY(state.getY() - 1);
    }

    protected void setDestination(int x, int y) {
        destination[0] = x;
        destination[1] = y;
    }

    protected void askCentralForDirections() {

    }

    private void extinguishFire() {
        water -= 10;
        Iterator<MapCell> i = fires.iterator();

        while (i.hasNext())
            i.next().beExtinguished();
    }

    public class FirefighterBehaviour extends TickerBehaviour {
        Agent agent;

        public FirefighterBehaviour(Agent a, long period) {
            super(a, period);
            agent = a;
        }

        public void requestNearestFire() {
            ACLMessage reqMsg = new ACLMessage(ACLMessage.REQUEST);
            for (Map.Entry<AID, MyAgent> entry : getEnvironment().agents.entrySet()) {
                if (entry.getValue() instanceof Firefighter) {
                    AID firefighterAid = entry.getKey();
                    reqMsg.addReceiver(firefighterAid);
                    reqMsg.setConversationId("request-fires");
                    agent.send(reqMsg);
                    //System.out.println(reqMsg);
                }
            }
            handleFiresAnswer();
        }

        public void handleFiresAnswer() {
            MessageTemplate mt = MessageTemplate.MatchConversationId("handle-fires-answer");
            ACLMessage replyMsg = agent.receive(mt);
            System.out.println(replyMsg);
            if (replyMsg != null && replyMsg.getPerformative() == ACLMessage.INFORM) {
                String fireRegex = "(\\d+):(\\d+),?";
                Pattern pattern = Pattern.compile(fireRegex);
                Matcher matcher = pattern.matcher(replyMsg.getContent());

                List<MapCell> cells = new ArrayList<MapCell>();
                while (matcher.find()) {
                    System.out.println(matcher.group(1));
                    System.out.println(matcher.group(2));
                    int fireX = Integer.valueOf(matcher.group(1));
                    int fireY = Integer.valueOf(matcher.group(2));
                    System.out.println("fireX=" + fireX + "," + fireY);
                    cells.add(new MapCell(fireX,fireY));
                }

                if(!cells.isEmpty()) {
                    List<Integer> cellDists = cells.stream()
                            .map(c -> MapState.calculateDist(c.getX(), c.getY(), getX(), getY()))
                            .collect(Collectors.toList());
                    int minDistCellIndex = cellDists.indexOf(Collections.min(cellDists));
                    setDestination(cells.get(minDistCellIndex).getX(), cells.get(minDistCellIndex).getY());
                } else {
                    Random rand = new Random();
                    int x = rand.nextInt(getEnvironment().getEnvWidth());
                    int y = rand.nextInt(getEnvironment().getEnvHeight());
                    setDestination(x,y);
                    System.out.println("ROAMING BOIIII");
                }
            }
        }

        protected void onTick() {
            updatePerception();

            switch(currentState) {
                case Waiting: {
                    // Prepare the template to get proposals
                    MessageTemplate mt = MessageTemplate.MatchConversationId("inform-fires");
                    ACLMessage firesMsg = myAgent.receive(mt);
                    // Fires inform message received
                    if (firesMsg != null && firesMsg.getPerformative() == ACLMessage.INFORM) {
                        //System.out.println(firesMsg.toString());
                        String posRegex = "(\\d+):(\\d+)";
                        Pattern pattern = Pattern.compile(posRegex);
                        Matcher matcher = pattern.matcher(firesMsg.getContent());
                        if (matcher.matches()) {
                            int destX = Integer.valueOf(matcher.group(1));
                            int destY = Integer.valueOf(matcher.group(2));
                            setDestination(destX, destY);
                        }
                        currentState = State.Driving;
                    }
                    break;
                }
                case Driving: {
                    if (destination[0] == -1) {
                        if (water < 0.5 * maxCapacity) {
                            currentState = State.Refilling;
                            return;
                        }/*
                        askCentralForDirections();
                        return;
                    }
                    else if(visibleFire(perception)) {
                        currentState = State.Extinguishing;
                        return;
                    */
                    } else
                        move();
                    break;
                }
                case Searching: {
                    break;
                }
                case Extinguishing: {
                    if(fires.isEmpty()){
                        System.out.println("FIRES EMPTY!");
                        requestNearestFire();
                        currentState = State.Driving;
                    } else {
                        Iterator<MapCell> i = fires.iterator();
                        while (i.hasNext())
                            i.next().beExtinguished();
                    }/*
                    else if(dangerousFire(perception)){
                        moveBack(); //esta fnção também vai ser fdd
                    }
                    else if(water < 0.1 * maxCapacity) {
                        messageToCentralRefill();
                        currentState = "refill";
                        setDestination(-1,-1);
                    }
                    else if(fire.distance <= extinguishDistance) {
                        extinguishFire();
                        return;
                    }
                    else if(fire(perception) < viewingDistance) {
                        messageToCentralNewFire(fire.position, fire.size);
                        move(fire.position);            //No idea how we are going to do this  YET!!!
                        return;
                    }*/
                    break;
                }
                case Refilling: {
                    if (water == maxCapacity) {
                        currentState = State.Driving;
                        setDestination(-1, -1);
                        return;
                    } else if (pos == destination) {
                        if (water >= maxCapacity - pumpingVelocity)
                            water = maxCapacity;
                        else
                            water += pumpingVelocity;
                        return;
                    } else {
                        move();
                        return;
                    }
                }
            }
        }
    }

    public class PerceptionBehaviour extends CyclicBehaviour {
        MyAgent agent;
        public PerceptionBehaviour(MyAgent agent) {
            this.agent = agent;
        }
        public void sendPerceptionFires() {
            // Prepare the template to get proposals
            MessageTemplate mt = MessageTemplate.MatchConversationId("request-fires");
            ACLMessage reqMsg = myAgent.receive(mt);
            // Refuse own messages
            if (reqMsg == null || reqMsg.getSender() == getAID()) {
                return;
            }
            // Fires request message received
            if (reqMsg != null && reqMsg.getPerformative() == ACLMessage.REQUEST) {
                ACLMessage reply = reqMsg.createReply();
                reply.setPerformative(ACLMessage.INFORM);
                String firesStr = "";
                Iterator<MapCell> i = fires.iterator();
                while (i.hasNext()) {
                    MapCell cell = i.next();
                    firesStr += String.format("%d:%d,", cell.getX(), cell.getY());
                }

                reply.setConversationId("handle-fires-answer");
                reply.setContent(firesStr);
                agent.send(reply);
                //System.out.println(reply);
            }
        }

        public void action() {
            sendPerceptionFires();
        }
    }
}
