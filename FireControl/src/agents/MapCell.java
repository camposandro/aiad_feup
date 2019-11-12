package agents;

import launchers.SimulationLauncher;
import sajas.core.Agent;
import sajas.core.behaviours.TickerBehaviour;
import uchicago.src.sim.gui.SimGraphics;
import utils.MapState;

import java.awt.*;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;


public class MapCell extends MyAgent {
    private int x;
    private int y;
    private boolean isPrioritary;
    private int vegetationDensity;
    private int humidityPercentage;
    private int burnedPercentage;

    public boolean isOnFire() {
        return onFire;
    }

    private boolean onFire = false;
    private int soilType; //0 - vegetation, 1 - asphalt, 2 - dirt, 3 - water, 4 - house
    private Color color;
    SimulationLauncher launcher;

    public MapCell(SimulationLauncher launcher, int x, int y, int vegetationDensity, int humidityPercentage, int soilType, boolean onFire) {
        super(launcher);
        this.launcher = launcher;
        setX(x);
        //System.out.println(this.getX());
        setY(y);
        setIsPrioritary(false);
        setOnFire(onFire);
        setVegetationDensity(vegetationDensity);
        setHumidityPercentage(humidityPercentage);
        setSoilType(soilType);
        setBurnedPercentage(0);
        setColor(this.calcColor());

    }

    private Color calcColor() {

        float h = 0, s = 0, v = 0;
        Color a;

        if (onFire) {
            h = 0;
            s = (vegetationDensity * 0.5f + 50) * 0.01f;
            v = 1 - (burnedPercentage * 0.75f * 0.01f);
            a = Color.getHSBColor(h, s, v);
        }
        else if (soilType == 1) { //asphalt
            a = new Color(60, 60, 60);
        } else if (soilType == 2) { //dirt
            a = new Color(230, 182, 115);
        } else if (soilType == 3) { //watter
            a = new Color(147, 176, 204);
        } else if (soilType == 4) { //houses
            a = new Color(140, 45, 25);
        } else if (soilType == 5) { //centralSquare
            a = new Color(200, 200, 200);
        } else {
            h = humidityPercentage * 100 / 360 * 0.003f + 0.15f;
            s = (vegetationDensity * 0.5f + 50) * 0.01f;
            v = 1 - (burnedPercentage * 0.75f);

            a = Color.getHSBColor(h, s, v);
        }


        return a;
    }

    private MapCell[] getNeighbours() {
        MapCell[] neighbours = new MapCell[4];

        neighbours[0] = launcher.getStatePos(x,y+1);
        neighbours[1] = launcher.getStatePos(x,y-1);
        neighbours[2] = launcher.getStatePos(x-1,y);
        neighbours[3] = launcher.getStatePos(x+1,y);
        return neighbours;

    }

    void catchFireProbability(int random) {
        if (burnedPercentage == 100) {
            return;
        }
        if (random <= vegetationDensity * (100 - humidityPercentage)) {
            this.onFire = true;
            setColor(this.calcColor());
            MapState.fireCell.add(this);
        }
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

    public void setOnFire(boolean onFire) {
        this.onFire = onFire;
        setColor(this.calcColor());
    }

    public void setSoilType(int soilType) {
        this.soilType = soilType;
        setColor(this.calcColor());
    }

    public int getSoilType(){
        return this.soilType;
    }

    public int getBurnedPercentage() {
        return burnedPercentage;
    }

    public void setBurnedPercentage(int burnedPercentage) {
        assert burnedPercentage >= 0 && burnedPercentage <= 100;
        this.burnedPercentage = burnedPercentage;
        //System.out.println(this.burnedPercentage);
    }

    public Color getColor() {
        return color;
    }

    protected void beExtinguished() {
        this.setOnFire(false);
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public int getX() {
        return x;
    }
    @Override
    public void setX(int x) {
        this.x = x;
    }
    @Override
    public void setY(int y) {
        this.y = y;
    }



    @Override
    public int getY() {
        return y;
    }

    @Override
    public void draw(SimGraphics simGraphics) {
        simGraphics.drawRect(getColor());
    }

    protected void update() {
        if (onFire) {
            //MapState.fireCell.add(this);
            if (burnedPercentage == 100) {
                onFire = false;
                return;
            }

            MapCell[] neighbours = getNeighbours();
            for(int i = 0; i < 4; i++) {
                if(neighbours[i] != null)
                    neighbours[i].catchFireProbability(ThreadLocalRandom.current().nextInt(0, 100));
            }
            setBurnedPercentage(burnedPercentage + 5);
            setColor(calcColor());
        }
    }
}