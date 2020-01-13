package colourwhirl.manpreet.com.colourwhirl;

public class Wheel {
    private float radius, leftBound, rightBound, upperBound, lowerBound;

    // Arcs' start and end angles
    private float startAngleBlue = 15;
    private float startAngleRed = startAngleBlue + 60;
    private float startAngleGreen = startAngleBlue + 120;
    private float startAnglePink = startAngleBlue + 180;
    private float startAnglePurple = startAngleBlue + 240;
    private float startAngleYellow = startAngleBlue + 300;

    public Wheel(float radius, float leftBound, float rightBound, float upperBound, float lowerBound){
        this.radius = radius;
        this.leftBound = leftBound;
        this.rightBound = rightBound;
        this.upperBound = upperBound;
        this.lowerBound = lowerBound;
    }

    public void updateArcAngles(float angleVariation){
        if((startAngleBlue + angleVariation) >= 0 && (startAngleBlue + angleVariation) < 360){
            startAngleBlue += angleVariation;
        }
        else if((startAngleBlue + angleVariation) < 0 ){
            startAngleBlue = 360 + (angleVariation + startAngleBlue);
        }
        else{
            startAngleBlue = (startAngleBlue + angleVariation) -360;
        }

        if((startAngleBlue + 60) >= 360 ){
            startAngleRed = (startAngleBlue + 60) - 360;
        }
        else{
            startAngleRed = startAngleBlue + 60;
        }

        if((startAngleBlue + 120) >= 360 ){
            startAngleGreen = (startAngleBlue + 120) - 360;
        }
        else{
            startAngleGreen= startAngleBlue + 120;
        }

        if((startAngleBlue + 180) >= 360 ){
            startAnglePink = (startAngleBlue + 180) - 360;
        } else{
            startAnglePink = startAngleBlue + 180;
        }

        if((startAngleBlue + 240) >= 360 ){
            startAnglePurple = (startAngleBlue + 240) - 360;
        }
        else{
            startAnglePurple = startAngleBlue + 240;
        }

        if((startAngleBlue + 300) >= 360 ){
            startAngleYellow = (startAngleBlue + 300) - 360;
        }
        else{
            startAngleYellow = startAngleBlue + 300;
        }
    }

    public float getLeftBound(){
        return leftBound;
    }

    public float getRightBound(){
        return rightBound;
    }

    public float getUpperBound(){
        return upperBound;
    }

    public float getLowerBound(){
        return lowerBound;
    }

    public float getRadius(){
        return radius;
    }

    public float getStartAngleBlue() {
        return startAngleBlue;
    }

    public float getStartAngleRed() {
        return startAngleRed;
    }

    public float getStartAngleGreen() {
        return startAngleGreen;
    }

    public float getStartAnglePink() {
        return startAnglePink;
    }

    public float getStartAnglePurple() {
        return startAnglePurple;
    }

    public float getStartAngleYellow() {
        return startAngleYellow;
    }
}
