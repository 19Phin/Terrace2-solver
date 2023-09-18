package net.dialingspoon.terrace;

public class Tile {
    private boolean isOn;
    private int color; // 0 represents no color, other integers represent different colors
    private int number; // 0 represents no number, 1-6 represent different numbers
    private int mustBeOn; // 1 if the tile must be on, 2 if it must be off, -1 if no restriction
    private int touching; // 0-4 represent different numbers, -1 if no restriction
    public Tile(boolean isOn, int color, int number) {
        this.isOn = isOn;
        this.color = color;
        this.number = number;
        this.mustBeOn = -1; // By default, no restriction
        this.touching = -1;
    }
    public Tile(boolean isOn, int color, int number, int mustBeOn, int touching) {
        this.isOn = isOn;
        this.color = color;
        this.number = number;
        this.mustBeOn = mustBeOn;
        this.touching = touching;
    }

    public boolean isOn() {
        return isOn;
    }

    public int getColor() {
        return color;
    }

    public int getNumber() {
        return number;
    }

    public int mustBeOn() {
        return mustBeOn;
    }
    public int getTouching() {
        return touching;
    }

    public void setOn(boolean isOn) {
        this.isOn = isOn;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setMustBeOn(int mustBeOn) {
        this.mustBeOn = mustBeOn;
    }
    public void setTouching(int touching) {
        this.touching = touching;
    }
}
