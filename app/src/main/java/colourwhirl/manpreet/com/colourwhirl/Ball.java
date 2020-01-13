package colourwhirl.manpreet.com.colourwhirl;

import java.util.Random;

public class Ball {
    private float xPosition = 0.0f;
    private float yPosition = 0.0f;
    private String color = "null";
    private float speed = 0.0f;
    private float size = 0.0f;
    private double directionAngle = 0.0f;
    private float distanceTravelled = 0.0f;

    public Ball(float xCenter, float yCenter, float speed, float size){
        xPosition = xCenter;
        yPosition = yCenter;
        this.speed = speed;
        this.size = size;
        setDirectionAngle();
        setColor();
    }


    public float getxPosition() {
        return xPosition;
    }

    public void setxPosition(float xPosition) {
        this.xPosition = xPosition;
    }

    public float getyPosition() {
        return yPosition;
    }

    public void setyPosition(float yPosition) {
        this.yPosition = yPosition;
    }

    public String getColor() {
        return color;
    }

    public void setColor() {
        Random rn = new Random();

        int random = rn.nextInt((6 - 1) + 1) + 1;

        if (random == 1) {
            color = "Red";
        }
        else if (random == 2) {
            color = "Green";
        }
        else if( random == 3){
            color = "Blue";
        }
        else if( random == 4){
            color = "Pink";
        }
        else if( random == 5){
            color = "Yellow";
        }
        else{
            color = "Purple";
        }
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void increaseSpeed(float increment){
        this.speed += increment;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public double getDirectionAngle() {
        return directionAngle;
    }

    public void setDirectionAngle() {
        directionAngle = Math.random()*360;
    }

    public void moveAlongXAxis(float xCenter){
        xPosition = ((float)((xCenter) + distanceTravelled * Math.cos(directionAngle)));
    }

    public void moveAlongYAxis(float yCenter){
        yPosition = ((float)((yCenter) + distanceTravelled * Math.sin(directionAngle)));
    }

    public void setDistanceTravelled(float distanceTravelled){
        this.distanceTravelled = distanceTravelled;
    }

    public float getDistanceTravelled(){
        return distanceTravelled;
    }
}
