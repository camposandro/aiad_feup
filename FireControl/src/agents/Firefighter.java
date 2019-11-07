package agents;

import launchers.SimulationLauncher;
import sajas.core.Agent;
import sajas.core.behaviours.TickerBehaviour;
import uchicago.src.sim.engine.BasicAction;
import uchicago.src.sim.gui.SimGraphics;

import java.awt.*;

public class Firefighter extends MyAgent {

    char[][] perception;
    int viewingDistance = 3;
    int extinguishDistance = 1;
    private int x;
    private int y;

    enum state {driving, searching, extinguishing, refilling};
    String currentState = "driving";
    int[] destination = new int[2];
    int[] pos;

    int speed;
    int health;
    int water;
    int maxCapacity;
    int pumpingVelocity;

    public Firefighter(SimulationLauncher launcher) {
        super(launcher);
        this.addBehaviour(new FirefighterBehaviour(this,1000));
        destination[0] = -1;
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

    public class FirefighterBehaviour extends TickerBehaviour {
        Agent agent;
        public FirefighterBehaviour(Agent a, long period) {
            super(a, period);
            agent = a;
        }
        protected void onTick() {
            System.out.println("Dest x: " + destination[0] + " y: " + destination[1] + " position x: " + x + " y: " + y );
            switch(currentState) {
                case "driving": {

                    if(destination[0] == -1) {
                        setDestination(50,50);
                    }
                        /*
                        if(water < 0.5 * maxCapacity){
                            currentState = "refill";
                            return;
                        }
                        destination = askCentralForDirections();
                        return;
                    }
                    else if(visibleFire(perception)) {
                        currentState = "extinguishing";
                        return;
                    }*/
                    else
                        move();
                    break;
                }
                case "searching": {

                    break;
                }
                case "extinguishing": {
                    /*
                    if(!visibleFire(perception)){
                        currentState = "driving";
                        setDestination(-1,-1);
                    }
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
                    }

                     */
                    break;
                }
                case "refilling": {
                    if(water == maxCapacity){
                        currentState = "driving";
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
