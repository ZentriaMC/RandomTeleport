package me.darkeyedragon.randomtp.api.world.location;

public class Offset {
    private int x;
    private int z;
    private int radius;

    public Offset(int x, int z, int radius) {
        this.x = x;
        this.z = z;
        this.radius = radius;
    }

    public Offset(int radius) {
        this(0,0,radius);
    }

    public Offset() {
        this(0);
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public int getRadius() {
        return radius;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }
}
