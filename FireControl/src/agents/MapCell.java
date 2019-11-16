package agents;

import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;
import utils.MapState;

import java.awt.*;
import java.io.Serializable;
import java.util.concurrent.ThreadLocalRandom;

public class MapCell implements Drawable, Serializable  {

    private int x;
    private int y;
    private boolean isPrioritary;
    private int vegetationDensity;
    private int humidityPercentage;
    private int burnedPercentage;
    private int probOfFire;
    private boolean onFire = false;
    private int burningRate = 0;
    private int soilType; //0 - vegetation, 1 - asphalt, 2 - dirt, 3 - water, 4 - house
    private Color color;
    private int propsCoefficient = 6;

    public MapCell() {
    }

    public MapCell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public MapCell(int x, int y, int vegetationDensity, int humidityPercentage, int soilType) {
        setX(x);
        setY(y);
        setIsPrioritary(false);
        setOnFire(false);
        setVegetationDensity(vegetationDensity);
        setHumidityPercentage(humidityPercentage);
        setSoilType(soilType);
        setBurnedPercentage(0);
        setColor(this.calcColor());
        setProbOfFire( humidityPercentage + (100 - vegetationDensity));
    }

    protected void setProbOfFire( int densitySum){
        this.probOfFire = densitySum / propsCoefficient;
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
            v = 1 - (burnedPercentage * 0.75f)/100;

            a = Color.getHSBColor(h, s, v);
        }
        return a;
    }

    private MapCell[] getNeighbours() {
        MapCell[] neighbours = new MapCell[4];

        neighbours[0] = MapState.getGridPos(x,y+1);
        neighbours[1] = MapState.getGridPos(x,y-1);
        neighbours[2] = MapState.getGridPos(x-1,y);
        neighbours[3] = MapState.getGridPos(x+1,y);

        return neighbours;
    }

    void catchFireProbability(int random) {
        if (burnedPercentage == 100) {
            return;
        }
        if (soilType != 3 && soilType != 1 && random <= probOfFire) {
            this.onFire = true;
            setColor(this.calcColor());
            MapState.getFirecells().add(this);
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

    public boolean isOnFire() {
        return onFire;
    }

    public void setOnFire(boolean onFire) {
        this.onFire = onFire;
            this.burningRate = 100;
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
        if (burnedPercentage < 0 || burnedPercentage > 100) {
            return;
        }
        else if(burnedPercentage > 100){
            this.burnedPercentage = 100;
            return;
        }
        this.burnedPercentage = burnedPercentage;
    }

    public Color getColor() {
        return color;
    }

    protected void beExtinguished() {
        propsCoefficient += 2;
        this.burningRate -= 40;
        if (this.burningRate <= 0) {
            this.setOnFire(false);
            this.probOfFire = 0;
        }
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getY() {
        return y;
    }

    public void draw(SimGraphics simGraphics) {
        simGraphics.drawRect(getColor());
    }

    public void update() {
        if (onFire) {
            if (burnedPercentage == 100) {
                onFire = false;
                return;
            }
            MapCell[] neighbours = getNeighbours();
            for(int i = 0; i < 4; i++) {
                if(neighbours[i] != null) {
                    int fireProbability = ThreadLocalRandom.current().nextInt(0, 100);
                    neighbours[i].catchFireProbability(fireProbability);
                }
            }
            setBurnedPercentage(burnedPercentage + 5 * (100- humidityPercentage)/100);
            setColor(calcColor());
        }
    }
}