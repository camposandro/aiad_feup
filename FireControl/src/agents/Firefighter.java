package agents;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import launchers.SimulationLauncher;
import sajas.core.behaviours.Behaviour;
import sajas.core.behaviours.CyclicBehaviour;
import sajas.core.behaviours.ParallelBehaviour;
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

    private enum State {Waiting, Driving, Searching, Extinguishing, Refilling, Return}

    private static int VIEWING_DIST = 3;
    private static int EXTINGUISHING_DIST = 2;

    int speed;
    int health;
    int water;
    int maxCapacity;
    int pumpingVelocity;
    int[] destination = new int[2];
    int[] pos;

    private MapCell[][] perception;
    private HashSet<MapCell> fires;
    private HashSet<MapCell> firesToExtinguish;

    private List<MapCell> cells = new ArrayList<>();

    private State currentState;

    private ParallelBehaviour behaviour;

    public Firefighter(SimulationLauncher launcher, int x, int y) {
        super(launcher, x, y);

        destination[0] = -1;
        currentState = State.Waiting;

        perception = new MapCell[VIEWING_DIST * 2 + 1][VIEWING_DIST * 2 + 1];
        fires = new HashSet<>();
        firesToExtinguish = new HashSet<>();

        behaviour = new ParallelBehaviour(ParallelBehaviour.WHEN_ALL) {
            public int onEnd() {
                System.out.println("Firefighter behaviour terminated with success!");
                return 0;
            }
        };
        behaviour.addSubBehaviour(new MessageProcessingBehaviour(this));
        behaviour.addSubBehaviour(new PerceptionBehaviour(this));
        behaviour.addSubBehaviour(new MovingBehaviour(this, SimulationLauncher.FF_UPDATE_RATE));
        this.addBehaviour(behaviour);
    }

    @Override
    public void draw(SimGraphics simGraphics) {
        simGraphics.drawHollowFastOval(Color.BLACK);
    }

    protected void setDestination(int x, int y) {
        int rightBound = MapState.envWidth;
        int lowerBound = MapState.envHeight;
        if (x >= rightBound) {
            x = rightBound - 1;
        } else if (x < 0) {
            x = 0;
        }
        if (y >= lowerBound) {
            y = lowerBound - 1;
        } else if (y < 0) {
            y = 0;
        }
        destination[0] = x;
        destination[1] = y;
    }

    protected void move() {
        if (state.getX() < destination[0])
            setX(state.getX() + 1);
        else if (state.getX() > destination[0])
            setX(state.getX() - 1);
        if (state.getY() < destination[1])
            setY(state.getY() + 1);
        else if (state.getY() > destination[1])
            setY(state.getY() - 1);
    }

    private void extinguishFire() {
        water -= 10;
        Iterator<MapCell> i = firesToExtinguish.iterator();

        while (i.hasNext())
            i.next().beExtinguished();
    }

    public class MessageProcessingBehaviour extends Behaviour {
        Firefighter ff;
        int numReceivedMsg;

        public MessageProcessingBehaviour(Firefighter ff) {
            this.ff = ff;
            numReceivedMsg = 0;
        }

        public void receiveInitialFire() {
            // Prepare the template to get proposals
            MessageTemplate mt = MessageTemplate.MatchConversationId("inform-fires");
            ACLMessage firesMsg = myAgent.receive(mt);
            // Fires inform message received
            if (firesMsg != null && firesMsg.getPerformative() == ACLMessage.INFORM) {
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
        }

        public void requestNearestFire() {
            ACLMessage reqMsg = new ACLMessage(ACLMessage.REQUEST);
            for (Map.Entry<AID, MyAgent> entry : getEnvironment().agents.entrySet()) {
                if (entry.getValue() instanceof Firefighter) {
                    AID firefighterAid = entry.getKey();
                    reqMsg.addReceiver(firefighterAid);
                    reqMsg.setConversationId("request-fires");
                    ff.send(reqMsg);
                    System.out.println(reqMsg);
                }
            }
            handleFiresAnswer();
        }

        public void handleFiresAnswer() {
            MessageTemplate mt = MessageTemplate.MatchConversationId("handle-fires-answer");
            ACLMessage replyMsg = ff.receive(mt);
            if (replyMsg != null && replyMsg.getPerformative() == ACLMessage.INFORM) {
                System.out.println(replyMsg);
                String fireRegex = "(\\d+):(\\d+),?";
                Pattern pattern = Pattern.compile(fireRegex);
                Matcher matcher = pattern.matcher(replyMsg.getContent());
                while (matcher.find()) {
                    int fireX = Integer.valueOf(matcher.group(1));
                    int fireY = Integer.valueOf(matcher.group(2));
                    cells.add(new MapCell(fireX,fireY));
                }
            }
            numReceivedMsg++;
            if (numReceivedMsg == getEnvironment().getFirefighters().size() - 1) {
                System.out.println("RECEIVED ALL MESSAGES!");
                calcDestination();
                numReceivedMsg = 0;
            }
        }

        public void calcDestination() {
            if(!cells.isEmpty()) {
                List<Integer> cellDists = cells.stream()
                        .map(c -> MapState.calculateDist(c.getX(), c.getY(), getX(), getY()))
                        .collect(Collectors.toList());
                int minDistCellIndex = cellDists.indexOf(Collections.min(cellDists));
                setDestination(cells.get(minDistCellIndex).getX(), cells.get(minDistCellIndex).getY());
            } else {
                currentState = State.Searching;
            }
        }

        @Override
        public void action() {
            switch (currentState) {
                case Waiting: {
                    receiveInitialFire();
                    break;
                }
                case Driving: {
                    if(destination[0] == state.getX() && destination[1] == state.getY()) {
                        requestNearestFire();
                    }
                    break;
                }
                case Extinguishing: {
                    if(fires.isEmpty() && destination[0] == state.getX() && destination[1] == state.getY()){
                        System.out.println("FIRES EMPTY!");
                        requestNearestFire();
                        currentState = State.Driving;
                    }
                    break;
                }
            }
        }

        @Override
        public boolean done() {
            return currentState == State.Return;
        }
    }

    public class MovingBehaviour extends TickerBehaviour {
        Firefighter ff;
        int numSearchingTicks = 0;

        public MovingBehaviour(Firefighter ff, long period) {
            super(ff, period);
            this.ff = ff;
        }

        public void calcDestination() {
            if(!cells.isEmpty()) {
                List<Integer> cellDists = cells.stream()
                        .map(c -> MapState.calculateDist(c.getX(), c.getY(), getX(), getY()))
                        .collect(Collectors.toList());
                int minDistCellIndex = cellDists.indexOf(Collections.min(cellDists));
                setDestination(cells.get(minDistCellIndex).getX(), cells.get(minDistCellIndex).getY());
            } else {
                currentState = State.Searching;
            }
        }

        protected void onTick() {
            switch(currentState) {
                case Driving: {
                    if(!fires.isEmpty() && firesToExtinguish.isEmpty()) {
                        Iterator<MapCell> i = fires.iterator();
                        MapCell cell = i.next();
                        destination[0] = cell.getX();
                        destination[1] = cell.getY();
                    }
                    move();
                    break;
                }
                case Searching: {
                    if (numSearchingTicks == SimulationLauncher.NUM_ROAMING_TICKS) {
                        setDestination(0,0);
                        currentState = State.Return;
                    } else if (!fires.isEmpty()) {
                        numSearchingTicks = 0;
                        currentState = State.Driving;
                    } else {
                        Random rand = new Random();
                        int x = rand.nextInt(MapState.envWidth);
                        int y = rand.nextInt(MapState.envHeight);
                        setDestination(x,y);
                        numSearchingTicks++;
                        System.out.println(getName() + " is ROAMING BOIIII");
                    }
                    break;
                }
                case Extinguishing: {
                    if(!fires.isEmpty() && firesToExtinguish.isEmpty()) {
                        Iterator<MapCell> i = fires.iterator();
                        MapCell cell = i.next();
                        destination[0] = cell.getX();
                        destination[1] = cell.getY();
                        currentState = State.Driving;
                    } else {
                        Iterator<MapCell> i = firesToExtinguish.iterator();
                        while (i.hasNext())
                            i.next().beExtinguished();
                        currentState = State.Driving;
                    }
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
                    }
                    break;
                }
                case Return: {
                    move();
                    break;
                }
                default:
                    return;
            }
        }
    }

    public class PerceptionBehaviour extends CyclicBehaviour {
        Firefighter ff;

        public PerceptionBehaviour(Firefighter ff) {
            this.ff = ff;
        }

        public void updatePerception() {
            fires.clear();
            firesToExtinguish.clear();
            for(int i = 0; i < VIEWING_DIST * 2 + 1; i++)
                for(int j = 0; j < VIEWING_DIST * 2 + 1; j++) {
                    MapCell cell = MapState.getGridPos(state.getX() - VIEWING_DIST + i, state.getY() - VIEWING_DIST + j);
                    perception[i][j] = cell;
                    if(cell != null && cell.isOnFire()) {
                        fires.add(cell);
                        if (MapState.calculateDist(getX(),getY(),cell.getX(),cell.getY()) <= EXTINGUISHING_DIST) {
                            ff.currentState = State.Extinguishing;
                            firesToExtinguish.add(cell);
                        }
                    }
                }
        }

        public void sendPerceptionFires() {
            // Prepare the template to get proposals
            MessageTemplate mt = MessageTemplate.MatchConversationId("request-fires");
            ACLMessage reqMsg = ff.receive(mt);
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
                ff.send(reply);
            }
        }

        public void action() {
            updatePerception();
            sendPerceptionFires();
        }
    }
}
