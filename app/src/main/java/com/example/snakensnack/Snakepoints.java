package com.example.snakensnack;

public class Snakepoints {
    private int PositionX, PositionY;


    public Snakepoints(int PositionX, int PositionY) {
        this.PositionX = PositionX;
        this.PositionY = PositionY;
    }
    public int getPositionX() {
        return PositionX;
    }
    public void setPositionX(int positionX) {
        PositionX = positionX;
    }
    public int getPositionY() {
        return PositionY;
    }
    public void setPositionY(int positionY) {
        PositionY = positionY;
    }


}
