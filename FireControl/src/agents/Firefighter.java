package agents;

import launchers.SimulationLauncher;
import sajas.core.Agent;
import sajas.core.behaviours.TickerBehaviour;
import uchicago.src.sim.gui.SimGraphics;
import utils.MapState;

import java.awt.*;
import java.util.HashSet;
import java.util.Iterator;

public class Firefighter extends MyAgent {

    int viewingDistance =3;
    MapCell[][] perception = new MapCell[viewingDistance*2][viewingDistance*2];
    public static HashSet<MapCell> fires = new HashSet<>();
    int extinguishDistance = 1;
    private int x;
    private int y;
    private int fireTicks = 2;
    private int ticksPeriod = 0;

    enum State {Driving, Searching, Extinguishing, Refilling}

    State currentState;
    int[] destination = new int[2];
    int[] pos;

    int speed;
    int health;
    int water;
    int maxCapacity;
    int pumpingVelocity;
    SimulationLauncher launcher;

    public Firefighter(SimulationLauncher launcher) {
        super(launcher);
        this.launcher = launcher;
        this.addBehaviour(new FirefighterBehaviour(this,500));
        destination[0] = -1;
        currentState = State.Driving;
    }

    @Override
    public void draw(SimGraphics simGraphics) {
        simGraphics.drawStringInHollowOval(Color.BLACK, Color.BLACK, "Firefighter");
    }

    @Override
    public int getX() {
        return x;
    }
    @Override
    public int getY() {
        return y;
    }
    @Override
    public void setX(int x) {
        this.x = x;
    }
    @Override
    public void setY(int y) {
        this.y = y;
    }


    protected void updatePerception() {
        fires.clear();
        for(int i = 0; i < viewingDistance*2; i++)
            for(int j = 0; j < viewingDistance * 2; j++) {
                MapCell cell = launcher.getStatePos(x - 3 + i, y - 3 + j);
                perception[i][j] = cell;
                if(cell!= null && cell.isOnFire()) {
                    fires.add(cell);
                    this.currentState = State.Extinguishing;
                }
            }
    }

    protected void move() {

        if(x < destination[0] )
            setX(x + 1);
        else if(x > destination[0])
            setX(x - 1);

        if(y < destination[1])
            setY(y + 1);
        else if(y > destination[1])
            setY(y - 1);
    }
    protected void setDestination(int x,int y) {
        destination[0] = x;
        destination[1] = y;
    }

    protected void askCentralForDirections() {
        /*
        destination[0] = ;
        destination[1] = ;*/
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
        protected void onTick() {
            if(ticksPeriod >= fireTicks) {
                HashSet<MapCell> a = (HashSet) MapState.fireCell.clone();
                Iterator<MapCell> i = a.iterator();

                while (i.hasNext())
                    i.next().update();
                ticksPeriod = 0;
            }
            else
                ticksPeriod++;
            updatePerception();

            switch(currentState) {
                case Driving: {

                    if(destination[0] == -1) {
                        setDestination(50,50);


                        if(water < 0.5 * maxCapacity){
                            currentState = State.Refilling;
                            return;
                        }/*
                        askCentralForDirections();
                        return;
                    }
                    else if(visibleFire(perception)) {
                        currentState = State.Extinguishing;
                        return;

                    */}
                    else
                        move();
                    break;
                }
                case Searching: {

                    break;
                }
                case Extinguishing: {

                    if(fires.isEmpty()){
                        currentState = State.Driving;
                        setDestination(-1,-1);
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
                    if(water == maxCapacity){
                        currentState = State.Driving;
                        setDestination(-1,-1);
                        return;
                    }
                    else if(pos == destination) {
                        if(water >= maxCapacity - pumpingVelocity)
                            water = maxCapacity;
                        else
                            water += pumpingVelocity;
                        return;
                    }
                    else {
                        move();
                        return;
                    }
                }

            }

        }
    }
}
