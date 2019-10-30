package utils;

import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;

import java.awt.*;

public class MapCell implements Drawable {
    private int x;
    private int y;
    private boolean isPrioritary;
    private int vegetationDensity;
    private int humidityPercentage;
    private boolean isOnFire;
    private int burnedPercentage;
    private Color color;

    public MapCell(int x, int y, int vegetationDensity, int humidityPercentage) {
        setX(x);
        setY(y);
        setIsPrioritary(false);
        setVegetationDensity(vegetationDensity);
        setHumidityPercentage(humidityPercentage);
        setIsOnFire(isOnFire);
        setBurnedPercentage(0);
        setColor(this.calcColor());
    }

    private void setX(int x) {
        this.x = x;
    }

    private void setY(int y) {
        this.y = y;
    }

    private Color calcColor() {
        float h = (humidityPercentage * 360 / 100f);
        float s = vegetationDensity * 0.75f + 25;
        float b = burnedPercentage * 0.75f + 25;
        Color a = Color.getHSBColor(
                (humidityPercentage * 360 / 100f),
                (vegetationDensity * 0.75f + 25) * 0.01f,
                (burnedPercentage * 0.75f + 25) * 0.01f
        );
        System.out.println(h);
        return a;
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
        return isOnFire;
    }

    public void setIsOnFire(boolean isOnFire) {
        this.isOnFire = isOnFire;
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
}