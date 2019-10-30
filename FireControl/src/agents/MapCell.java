package agents;

import launchers.SimulationLauncher;
import sajas.core.Agent;
import sajas.core.behaviours.TickerBehaviour;
import uchicago.src.sim.gui.SimGraphics;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;


public class MapCell extends MyAgent {
    private int x;
    private int y;
    private boolean isPrioritary;
    private int vegetationDensity;
    private int humidityPercentage;
    private boolean onFire;
    private int burnedPercentage;
    private Color color;
    SimulationLauncher launcher;

    public MapCell(SimulationLauncher launcher, int x, int y, int vegetationDensity, int humidityPercentage) {
        super(launcher);
        setX(x);
        setY(y);
        setIsPrioritary(false);
        setVegetationDensity(vegetationDensity);
        setHumidityPercentage(humidityPercentage);
        setOnFire(onFire);
        setBurnedPercentage(0);
        setColor(this.calcColor());
        this.addBehaviour(new MapCellBehaviour(this,10));

    }

    private Color calcColor() {

        float h = (humidityPercentage * 100 / 360  * 0.003f + 0.17f);
        if(onFire) {
            h = 0f;
        }
        float s = (vegetationDensity * 0.5f + 50) * 0.01f;
        float v = 1 - (burnedPercentage * 0.75f);

        Color a = Color.getHSBColor(
                h,
                s,
                v
        );
        return a;
    }

    protected void OnTick() {
        System.out.println("js");
        if(onFire)
            if(burnedPercentage == 100) {
                onFire = false;
                return;
            }

            MapCell[] neighbours = getNeighbours();
            for(int i = 0; i < 4; i++) {
                neighbours[i].catchFireProbability(ThreadLocalRandom.current().nextInt(0, 100));
            }
            burnedPercentage += 10;

    }



    private MapCell[] getNeighbours() {
        System.out.println("mksk");
        MapCell[] neighbours = new MapCell[4];

        neighbours[0] = launcher.getState()[x][y+1];
        neighbours[1] = launcher.getState()[x][y-1];
        neighbours[2] = launcher.getState()[x-1][y];
        neighbours[3] = launcher.getState()[x+1][y];

        return neighbours;

    }
    void catchFireProbability(int random){
        if(burnedPercentage == 100){
            return;
        }
        if(random <  vegetationDensity * (100 - humidityPercentage))
            this.onFire = true;
    }



    public boolean isPrioritary() {
        return isPrioritary;
    }

    public void setIsPrioritary(boolean isPrioritary) {
        this.isPrioritary = isPrioritary;
    }

    public int getVegetationDensity() {
        return vegetationDensity;
    }

    public void setVegetationDensity(int vegetationDensity) {
        assert vegetationDensity >= 0 && vegetationDensity <= 100;
        this.vegetationDensity = vegetationDensity;
    }

    public int getHumidityPercentage() {
        return humidityPercentage;
    }

    public void setHumidityPercentage(int humidityPercentage) {
        assert humidityPercentage >= 0 && humidityPercentage <= 100;
        this.humidityPercentage = humidityPercentage;
    }

    public boolean onFire() {
        return onFire;
    }

    public void setOnFire(boolean onFire) {
        this.onFire = onFire;
        setColor(this.calcColor());
    }

    public int getBurnedPercentage() {
        return burnedPercentage;
    }

    public void setBurnedPercentage(int burnedPercentage) {
        assert burnedPercentage >= 0 && burnedPercentage <= 100;
        this.burnedPercentage = burnedPercentage;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
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
    public void draw(SimGraphics simGraphics) {
        simGraphics.drawRect(getColor());
    }


    public class MapCellBehaviour extends TickerBehaviour {
        public MapCellBehaviour(Agent a, long period) {
            super(a, period);
        }

        protected void onTick() {

            System.out.println("js");
            if(onFire)
                if(burnedPercentage == 100) {
                    onFire = false;
                    return;
                }

            MapCell[] neighbours = getNeighbours();
            for(int i = 0; i < 4; i++) {
                neighbours[i].catchFireProbability(ThreadLocalRandom.current().nextInt(0, 100));
            }
            burnedPercentage += 10;
        }
    }
}