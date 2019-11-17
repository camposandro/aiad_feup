package agents;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
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

public class Firefighter extends MyAgent {

    private enum State {Waiting, Driving, Searching, Extinguishing, Refilling, Return}

    private static int VIEWING_DIST = 6;
    private static int EXTINGUISHING_DIST = 2;
    private static int HEALTH_DAMAGE = 5;
    private static int MAX_WATER_CAPACITY = 100;
    private static int PUMPING_VELOCITY = 5;

    int water;
    int[] destination = new int[2];
    int[] lastFireDest = new int[2];

    private MapCell[][] perception;
    private HashSet<MapCell> fires;
    private HashSet<MapCell> firesToExtinguish;

    private List<MapCell> fireCells = new ArrayList<>();
    private List<MapCell> waterCells = new ArrayList<>();

    private State currentState;

    private ParallelBehaviour behaviour;

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
        behaviour.addSubBehaviour(new MessageListenerBehaviour());
        behaviour.addSubBehaviour(new PerceptionBehaviour(this, SimulationLauncher.FF_UPDATE_RATE));
        behaviour.addSubBehaviour(new MovingBehaviour(this, SimulationLauncher.FF_UPDATE_RATE));
        this.addBehaviour(behaviour);
    }

    @Override
    public void draw(SimGraphics simGraphics) {
        if(state.getHealth() <= 0)
            simGraphics.drawFastOval(Color.BLACK);
        else
            simGraphics.drawHollowFastOval(Color.BLACK);
    }

    private void setDestination(int x, int y) {
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

    private void setLastFireDest(int x, int y) {
        lastFireDest[0] = x;
        lastFireDest[1] = y;
    }

    private void setDestination(int[] pos) {
        if (pos.length == 2) {
            setDestination(pos[0],pos[1]);
        }
    }

    private boolean arrived() {
        return state.getX() == destination[0] && state.getY() == destination[1];
    }

    private void move() {
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
                water -= 1;
            } else {
                requestNearestWater();
                break;
            }
        }
    }

    private void requestNearestFire() {
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
    }

    private void calcFireDestination() {
        if(!fireCells.isEmpty()) {
            List<Integer> cellDists = fireCells.stream()
                    .map(c -> MapState.calculateDist(c, this))
                    .collect(Collectors.toList());
            int minDistCellIndex = cellDists.indexOf(Collections.min(cellDists));
            setDestination(fireCells.get(minDistCellIndex).getX(), fireCells.get(minDistCellIndex).getY());
            move();
        } else {
            currentState = State.Searching;
        }
    }

    private void requestNearestWater() {
        ACLMessage reqMsg = new ACLMessage(ACLMessage.REQUEST);
        reqMsg.addReceiver(getEnvironment().getFirestationAID());
        reqMsg.setConversationId("request-water");
        send(reqMsg);
    }

    private void calcWaterDestination() {
        if (!waterCells.isEmpty()) {
            List<Integer> cellDists = waterCells.stream()
                    .map(c -> MapState.calculateDist(c, this))
                    .collect(Collectors.toList());
            int minDistCellIndex = cellDists.indexOf(Collections.min(cellDists));
            setLastFireDest(getX(),getY());
            setDestination(waterCells.get(minDistCellIndex).getX(), waterCells.get(minDistCellIndex).getY());
            currentState = State.Refilling;
        }
    }

    private class MessageListenerBehaviour extends CyclicBehaviour {
        int numReceivedMsg = 0;

        public void receiveInitialFire(ACLMessage msg) {
            // Fires inform message received
            if (msg.getPerformative() == ACLMessage.INFORM) {
                String posRegex = "(\\d+):(\\d+)";
                Pattern pattern = Pattern.compile(posRegex);
                Matcher matcher = pattern.matcher(msg.getContent());
                if (matcher.matches()) {
                    int destX = Integer.valueOf(matcher.group(1));
                    int destY = Integer.valueOf(matcher.group(2));
                    setDestination(destX, destY);
                    currentState = State.Driving;
                }
            }
        }

        public void sendPerceptionFires(ACLMessage msg) {
            // Fires request message received
            if (msg.getPerformative() == ACLMessage.REQUEST) {
                // Refuse own messages
                if (msg.getSender() == getAID()) {
                    return;
                }
                ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.INFORM);

                String firesStr = "";
                Iterator<MapCell> i = fires.iterator();
                while (i.hasNext()) {
                    MapCell cell = i.next();
                    firesStr += String.format("%d:%d,", cell.getX(), cell.getY());
                }
                reply.setContent(firesStr);

                send(reply);
            }
        }

        public void handleFiresAnswer(ACLMessage msg) {
            if (msg.getPerformative() == ACLMessage.INFORM) {
                String fireRegex = "(\\d+):(\\d+),?";
                Pattern pattern = Pattern.compile(fireRegex);
                Matcher matcher = pattern.matcher(msg.getContent());
                while (matcher.find()) {
                    int fireX = Integer.valueOf(matcher.group(1));
                    int fireY = Integer.valueOf(matcher.group(2));
                    fireCells.add(new MapCell(fireX, fireY));
                }
            }
            numReceivedMsg++;
            if (numReceivedMsg == getEnvironment().getFirefighters().size() - 1) {
                calcFireDestination();
                numReceivedMsg = 0;
            }
        }

        private void handleWaterAnswer(ACLMessage msg) {
            // Water inform message received
            if (msg.getPerformative() == ACLMessage.INFORM) {
                String posRegex = "(\\d+):(\\d+),?";
                Pattern pattern = Pattern.compile(posRegex);
                Matcher matcher = pattern.matcher(msg.getContent());
                while (matcher.find()) {
                    int waterX = Integer.valueOf(matcher.group(1));
                    int waterY = Integer.valueOf(matcher.group(2));
                    waterCells.add(new MapCell(waterX,waterY));
                }
                calcWaterDestination();
            }
        }

        @Override
        public void action() {
            ACLMessage msg = receive();
            if (msg != null) {
                //System.out.println(msg);
                switch (msg.getConversationId()) {
                    case "inform-fires":
                        receiveInitialFire(msg);
                        break;
                    case "request-fires":
                        //sendPerceptionFires(msg);
                        //handleFiresAnswer(msg);
                        break;
                    case "request-water":
                        handleWaterAnswer(msg);
                        break;
                    default:
                        return;
                }
            } else {
                block();
            }
        }
    }

    private class PerceptionBehaviour extends TickerBehaviour {
        public PerceptionBehaviour(Firefighter ff, long period) {
            super(ff,period);
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
                    if (currentState != State.Refilling) {
                        currentState = State.Extinguishing;
                    }
                }
            }
        }

        @Override
        public void onTick() {
            updatePerception();
        }
    }

    private class MovingBehaviour extends TickerBehaviour {
        int numSearchingTurns = 0;

        public MovingBehaviour(Firefighter ff, long period) {
            super(ff, period);
        }

        protected void onTick() {
            System.out.println("[" + getX() + "," + getY() + "] is in state " + currentState.toString());
            switch(currentState) {
                case Driving: {
                    if (fires.isEmpty()) {
                        requestNearestFire();
                    } else {
                        move();
                    }
                    break;
                }
                case Searching: {
                    if (numSearchingTurns == SimulationLauncher.NUM_ROAMING_TICKS) {
                        setDestination(0,0);
                        currentState = State.Return;
                    } else {
                        if (!fires.isEmpty()) {
                            Iterator<MapCell> iter = fires.iterator();
                            MapCell cell = iter.next();
                            setDestination(cell.getX(), cell.getY());
                            currentState = State.Driving;
                            numSearchingTurns = 0;
                        } else if(arrived()) {
                            Random rand = new Random();
                            int x = rand.nextInt(MapState.envWidth);
                            int y = rand.nextInt(MapState.envHeight);
                            setDestination(x,y);
                            numSearchingTurns++;
                            currentState = State.Driving;
                        }
                        move();
                    }
                    break;
                }
                case Extinguishing: {
                    if (firesToExtinguish.isEmpty()) {
                        if (!fires.isEmpty()) {
                            Random rand = new Random();
                            int fireIdx = rand.nextInt(fires.size()) - 1;
                            Iterator<MapCell> i = fires.iterator();
                            MapCell cell = i.next();
                            while (fireIdx > 0) {
                                cell = i.next();
                                fireIdx--;
                            }
                            setDestination(cell.getX(),cell.getY());
                            currentState = State.Driving;
                        } else {
                            requestNearestFire();
                        }
                    } else {
                        extinguishFire();
                    }
                    break;
                }
                case Refilling: {
                    if (water == MAX_WATER_CAPACITY) {
                        setDestination(lastFireDest);
                        currentState = State.Driving;
                    } else if (arrived()) {
                        if (water >= MAX_WATER_CAPACITY - PUMPING_VELOCITY)
                            water = MAX_WATER_CAPACITY;
                        else
                            water += PUMPING_VELOCITY;
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
}
