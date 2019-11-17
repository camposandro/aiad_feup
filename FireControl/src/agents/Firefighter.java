package agents;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import launchers.SimulationLauncher;
import sajas.core.behaviours.*;
import uchicago.src.sim.gui.SimGraphics;
import utils.MapState;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

public class Firefighter extends MyAgent {

    private enum State {Waiting, Driving, Searching, Extinguishing, Refilling, Return}

    private static int VIEWING_DIST = 3;
    private static int EXTINGUISHING_DIST = 2;
    private static int HEALTH_DAMAGE = 5;
    private static int MAX_WATER_CAPACITY = 100;
    private static int PUMPING_VELOCITY = 5;

    int water;
    int[] destination = new int[2];

    private MapCell[][] perception;
    private HashSet<MapCell> fires;
    private HashSet<MapCell> firesToExtinguish;

    private List<MapCell> cells = new ArrayList<>();

    private State currentState;

    private ParallelBehaviour behaviour;

    private MessageTemplate mtRequestFires;

    public Firefighter(SimulationLauncher launcher, int x, int y) {
        super(launcher, x, y);

        perception = new MapCell[VIEWING_DIST * 2 + 1][VIEWING_DIST * 2 + 1];
        fires = new HashSet<>();
        firesToExtinguish = new HashSet<>();

        destination[0] = -1;
        water = MAX_WATER_CAPACITY;
        currentState = State.Waiting;

        behaviour = new ParallelBehaviour(ParallelBehaviour.WHEN_ALL) {
            public int onEnd() {
                System.out.println("Firefighter behaviour terminated with success!");
                return 0;
            }
        };
        behaviour.addSubBehaviour(new ReceiveInitialFireBehaviour());
        behaviour.addSubBehaviour(new SendPerceptionBehaviour());
        behaviour.addSubBehaviour(new PerceptionBehaviour(this, SimulationLauncher.FF_UPDATE_RATE));
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
        Iterator<MapCell> i = firesToExtinguish.iterator();
        while (i.hasNext()) {
            if (water >= 0) {
                i.next().beExtinguished();
                water -= 10;
            } else {
                behaviour.addSubBehaviour(new RequestWaterBehaviour());
                currentState = State.Refilling;
                break;
            }
        }
    }

    public class ReceiveInitialFireBehaviour extends OneShotBehaviour {
        public void receiveInitialFire() {
            // Prepare the template to get proposals
            MessageTemplate mt = MessageTemplate.MatchConversationId("inform-fires");
            ACLMessage firesMsg = receive(mt);
            // Fires inform message received
            if (firesMsg != null && firesMsg.getPerformative() == ACLMessage.INFORM) {
                String posRegex = "(\\d+):(\\d+)";
                Pattern pattern = Pattern.compile(posRegex);
                Matcher matcher = pattern.matcher(firesMsg.getContent());
                if (matcher.matches()) {
                    int destX = Integer.valueOf(matcher.group(1));
                    int destY = Integer.valueOf(matcher.group(2));
                    setDestination(destX, destY);
                    System.out.println(firesMsg);
                    currentState = State.Driving;
                }
            } else {
                block();
            }
        }
        @Override
        public void action() {
            receiveInitialFire();
        }
    }

    public class PerceptionBehaviour extends TickerBehaviour {

        public PerceptionBehaviour(Firefighter ff, long period) {
            super(ff, period);
        }

        public void updatePerception() {
            clearFires();
            for (int i = 0; i < VIEWING_DIST * 2 + 1; i++) {
                for (int j = 0; j < VIEWING_DIST * 2 + 1; j++) {
                    int cellX = state.getX() - VIEWING_DIST + i;
                    int cellY = state.getY() - VIEWING_DIST + j;
                    MapCell cell = MapState.getGridPos(cellX, cellY);
                    processCell(i, j, cell);
                }
            }
            // Firefighter surrounded by fire
            if (fires.size() >= Math.pow(VIEWING_DIST * 2 + 1, 2) / 2) {
                state.decreaseHealth(HEALTH_DAMAGE);
            } else {
                state.resetHealth();
            }
        }

        public void clearFires() {
            fires.clear();
            firesToExtinguish.clear();
        }

        public void processCell(int i, int j, MapCell cell) {
            perception[i][j] = cell;
            if (cell != null && cell.isOnFire()) {
                fires.add(cell);
                if (MapState.calculateDist(cell, (Firefighter) myAgent) <= EXTINGUISHING_DIST) {
                    firesToExtinguish.add(cell);
                    currentState = State.Extinguishing;
                }
            }
        }

        @Override
        protected void onTick() {
            updatePerception();
        }
    }

    public class SendPerceptionBehaviour extends CyclicBehaviour {
        public void sendPerceptionFires() {
            ACLMessage reqMsg = receive(mtRequestFires);
            // Fires request message received
            if (reqMsg != null && reqMsg.getPerformative() == ACLMessage.REQUEST) {
                // Refuse own messages
                if (reqMsg.getSender() == getAID()) {
                    return;
                }
                ACLMessage reply = reqMsg.createReply();
                reply.setPerformative(ACLMessage.INFORM);

                String firesStr = "";
                Iterator<MapCell> i = fires.iterator();
                while (i.hasNext()) {
                    MapCell cell = i.next();
                    firesStr += String.format("%d:%d,", cell.getX(), cell.getY());
                }
                reply.setContent(firesStr);

                send(reply);
                System.out.println(reply);
            } else {
                block();
            }
        }
        @Override
        public void action() {
            sendPerceptionFires();
        }
    }

    public class MovingBehaviour extends TickerBehaviour {
        int numSearchingTicks = 0;

        public MovingBehaviour(Firefighter ff, long period) {
            super(ff, period);
        }

        protected void onTick() {
            switch(currentState) {
                case Driving: {
                    if (getX() == destination[0] && getY() == destination[1]) {
                        behaviour.addSubBehaviour(new RequestNearestFireBehaviour());
                    } else {
                        move();
                    }
                    break;
                }
                case Searching: {
                    if (numSearchingTicks == SimulationLauncher.NUM_ROAMING_TICKS) {
                        setDestination(0,0);
                        currentState = State.Return;
                    } else {
                        if (!fires.isEmpty()) {
                            Iterator<MapCell> iter = fires.iterator();
                            MapCell cell = iter.next();
                            setDestination(cell.getX(), cell.getY());
                            currentState = State.Driving;
                            numSearchingTicks = 0;
                        } else {
                            Random rand = new Random();
                            int x = rand.nextInt(MapState.envWidth);
                            int y = rand.nextInt(MapState.envHeight);
                            setDestination(x,y);
                            numSearchingTicks++;
                        }
                        move();
                    }
                    break;
                }
                case Extinguishing: {
                    if (firesToExtinguish.isEmpty()) {
                        if (!fires.isEmpty()) {
                            Iterator<MapCell> i = fires.iterator();
                            MapCell cell = i.next();
                            setDestination(cell.getX(),cell.getY());
                            currentState = State.Driving;
                        } else {
                            behaviour.addSubBehaviour(new RequestNearestFireBehaviour());
                        }
                    } else {
                        extinguishFire();
                    }
                    break;
                }
                case Refilling: {
                    System.out.println("Refilling...");
                    if (water == MAX_WATER_CAPACITY) {
                        setDestination(0, 0);
                        currentState = State.Driving;
                    } else if (getX() == destination[0] && getY() == destination[1]) {
                        if (water >= MAX_WATER_CAPACITY - PUMPING_VELOCITY)
                            water = MAX_WATER_CAPACITY;
                        else
                            water += PUMPING_VELOCITY;
                    } else {
                        move();
                        System.out.println("MOVING TOWARDS WATER!");
                        System.out.println(getX() + "," + getY());
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

    public class RequestNearestFireBehaviour extends OneShotBehaviour {
        public void requestNearestFire() {
            ACLMessage reqMsg = new ACLMessage(ACLMessage.REQUEST);
            for (Map.Entry<AID, MyAgent> entry : getEnvironment().agents.entrySet()) {
                if (entry.getKey() != getAID() && entry.getValue() instanceof Firefighter) {
                    AID firefighterAid = entry.getKey();
                    reqMsg.addReceiver(firefighterAid);
                }
            }
            reqMsg.setConversationId("request-fires");
            reqMsg.setReplyWith("fire-req" + System.currentTimeMillis());
            send(reqMsg);
            System.out.println(reqMsg);

            mtRequestFires =  MessageTemplate.and(MessageTemplate.MatchConversationId("request-fires"),
                    MessageTemplate.MatchInReplyTo(reqMsg.getReplyWith()));

            behaviour.addSubBehaviour(new ReceiveNearestFireBehaviour());
        }

        @Override
        public void action() {
            requestNearestFire();
        }
    }

    public class ReceiveNearestFireBehaviour extends CyclicBehaviour {
        int numReceivedMsg = 0;

        public void handleFiresAnswer() {
            ACLMessage replyMsg = receive(mtRequestFires);
            if (replyMsg != null && replyMsg.getPerformative() == ACLMessage.INFORM) {
                String fireRegex = "(\\d+):(\\d+),?";
                Pattern pattern = Pattern.compile(fireRegex);
                Matcher matcher = pattern.matcher(replyMsg.getContent());
                System.out.println(replyMsg);
                while (matcher.find()) {
                    int fireX = Integer.valueOf(matcher.group(1));
                    int fireY = Integer.valueOf(matcher.group(2));
                    cells.add(new MapCell(fireX,fireY));
                }
            } else {
                block();
            }
            numReceivedMsg++;
            if (numReceivedMsg == getEnvironment().getFirefighters().size() - 1) {
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
                move();
            } else {
                currentState = State.Searching;
            }
        }

        @Override
        public void action() {
            handleFiresAnswer();
        }
    }

    public class RequestWaterBehaviour extends OneShotBehaviour {
        private void sendWaterRequest() {
            ACLMessage reqMsg = new ACLMessage(ACLMessage.REQUEST);
            reqMsg.addReceiver(getEnvironment().getFirestationAID());
            reqMsg.setConversationId("request-water");
            send(reqMsg);
            behaviour.addSubBehaviour(new WaitWaterRequestBehaviour());
        }

        @Override
        public void action() {
            sendWaterRequest();
        }
    }

    public class WaitWaterRequestBehaviour extends OneShotBehaviour {
        private List<MapCell> waterCells;

        public WaitWaterRequestBehaviour() {
            waterCells = new ArrayList<>();
        }

        private void handleWaterAnswer() {
            MessageTemplate mt = MessageTemplate.MatchConversationId("request-water");
            ACLMessage waterMsg = receive(mt);
            // Water inform message received
            if (waterMsg != null && waterMsg.getPerformative() == ACLMessage.INFORM) {
                String posRegex = "(\\d+):(\\d+),?";
                Pattern pattern = Pattern.compile(posRegex);
                Matcher matcher = pattern.matcher(waterMsg.getContent());
                while (matcher.find()) {
                    int waterX = Integer.valueOf(matcher.group(1));
                    int waterY = Integer.valueOf(matcher.group(2));
                    waterCells.add(new MapCell(waterX,waterY));
                }
                setWaterDestination();
            } else {
                block();
            }
        }

        private void setWaterDestination() {
            List<Integer> cellDists = waterCells.stream()
                    .map(c -> MapState.calculateDist(c, (Firefighter) myAgent))
                    .collect(Collectors.toList());
            int minDistCellIndex = cellDists.indexOf(Collections.min(cellDists));
            setDestination(waterCells.get(minDistCellIndex).getX(), waterCells.get(minDistCellIndex).getY());
        }

        @Override
        public void action() {
            handleWaterAnswer();
        }
    }
}
