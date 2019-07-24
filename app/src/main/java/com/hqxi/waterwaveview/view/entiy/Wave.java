package com.hqxi.waterwaveview.view.entiy;

public class Wave {
    private float radius;
    private float width;
    private int color;

    public Wave(float width, int color) {
        reset(width, color);
    }

    public void reset(float width, int color) {
        radius = 0;
        this.width = width;
        this.color = color;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
