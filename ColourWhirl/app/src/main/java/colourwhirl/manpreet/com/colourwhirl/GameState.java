package colourwhirl.manpreet.com.colourwhirl;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.util.DisplayMetrics;
public class GameState{

    private Ball ball = null;
    private Wheel wheel = null;
    private MediaPlayer mpScored;
    private float xCenter, yCenter;         // Center of screen
    private float speedConstant;                // Speed formula constant
    private float strokeWidth;              // Paint stroke width
    private float screenWidth;
    private float screenHeight;
    private float cornerVariation = 0 ; // degrees from centerpoint of ball to edge. Used to not having the ball to collide from center for sucess. Collision of the edges is to be accepted
    private int scorePallier = 0;
    private int score = 0;
    private Context context;

    public GameState(Context context)
    {
        this.context = context;
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        // Center of screen
        xCenter = screenWidth/2;
        yCenter = screenHeight/2;

        // Bounding rectangle for the wheel
        float  leftBound = screenWidth/10;
        float rightBound = screenWidth - leftBound;
        float radius = xCenter -leftBound;
        float upperBound = yCenter - radius;
        float lowerBound = yCenter + radius;

        speedConstant = (screenWidth / 100) * 1.5f;

        ball = new Ball(xCenter, yCenter, (speedConstant/20)*11, (screenWidth / 100) * 3);
        wheel = new Wheel(radius, leftBound, rightBound, upperBound, lowerBound);

        mpScored = MediaPlayer.create(this.context, R.raw.success);

        strokeWidth = (screenWidth / 100) * 5;
        cornerVariation = (float) Math.toDegrees(Math.asin(((ball.getSize()/10)*11)/ wheel.getRadius()));
    }

    public int getScore(){
        return score;
    }

    public void setScore(int score){
        this.score = score;
    }

    public boolean update(float angleVariation) {
        wheel.updateArcAngles(angleVariation);
        boolean arcIsTouched = false;
        String arcColorTouched = "";
        float xPosiiton = ball.getxPosition();
        float yPosition = ball.getyPosition();
        float size = ball.getSize();

            if(arcTouched(xPosiiton, yPosition, size))
            {
                arcIsTouched = true;
                float touchedAngle = ((float) Math.toDegrees(Math.atan2(xPosiiton - xCenter, yCenter - yPosition)) -90); // Math class return angles with system having 0 at postion. To convert
                                                                                                                                // canvas angles system remove 90 degress for cosistency of having the same system

                if(touchedAngle < 0){       // Changes negative angles to have positive values for conversion to a 360 degrees system
                    touchedAngle += 360;
                }

                touchedAngle -= wheel.getStartAngleBlue();  // Rotates the plane to its original position.

                if(touchedAngle < 0){       // Changes negative angles to have positive values for conversion to a 360 degrees system
                    touchedAngle += 360;
                }

                arcColorTouched = findArcTouched(touchedAngle);
        }

        if(arcIsTouched){
            ball.setxPosition(xCenter);
            ball.setyPosition(yCenter);
            ball.setDirectionAngle();
            ball.setDistanceTravelled(0);

            if(ball.getColor().equals(arcColorTouched)){
                mpScored.start();
                ++score;

                if(score == (scorePallier + 7)){
                    scorePallier = score;
                    if(score <= 14) {

                        ball.increaseSpeed(speedConstant/16);
                    }
                    else if(score == 28){
                        ball.increaseSpeed(speedConstant/20);
                    }
                    else{
                        ball.increaseSpeed(speedConstant/24);
                    }

                }
                ball.setColor();
                return true;
            }
            else{
                ball.setSpeed((speedConstant/20)*11);
                scorePallier = 0;
                ball.setColor();
                return false;
            }
        }
        else {
            ball.setDistanceTravelled(ball.getDistanceTravelled() + ball.getSpeed());
            ball.moveAlongXAxis(xCenter);
            ball.moveAlongYAxis(yCenter);
            return true;
        }
    }

    public boolean isBetween(float x, double lowerBound, double upperBound){
        return lowerBound <= x && x <= upperBound;
    }

    public boolean arcTouched(float xPosition, float yPosition, float size){
        if(Math.sqrt(Math.pow((xPosition - xCenter), 2) + Math.pow((yPosition - yCenter), 2)) >= (wheel.getRadius()-(5+(size/2)))){
            return true;
        }
        else {
            return false;
        }
    }

    public String findArcTouched(float touchedAngle){

        if(isBetween(touchedAngle,  0,45)){
            return "Blue";
        }
        else if(isBetween(touchedAngle, 60, 105 )){
            return "Red";
        }
        else if(isBetween(touchedAngle, 120, 165)){
            return "Green";
        }
        else if(isBetween(touchedAngle, 180, 225)){
            return "Pink";
        }
        else if(isBetween(touchedAngle, 240, 285)){
            return "Purple";
        }
        else if(isBetween(touchedAngle, 300, 345)){
            return "Yellow";
        }
        else{
            return "None";
        }
    }

    public void draw(Canvas canvas, Paint paint){
        //Clear the screen
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, screenWidth, screenHeight, paint);
        paint.setAntiAlias(true);

        // Set ball color
        if(ball.getColor().equals("Red")){
            paint.setARGB(255, 255, 69, 5);
        }
        else if( ball.getColor().equals("Green")) {
            paint.setARGB(255, 207, 223, 11);
        }
        else if(ball.getColor().equals("Blue")){
            paint.setARGB(255, 15, 187, 166);
        }
        else if(ball.getColor().equals("Yellow")){
            paint.setARGB(255, 255, 236, 4);
        }
        else if(ball.getColor().equals("Purple")){
            paint.setARGB(255, 161, 33, 186);
        }
        else{
            paint.setARGB(255, 255, 0, 139);
        }

        // Draw ball
        canvas.drawCircle(ball.getxPosition(), ball.getyPosition(), ball.getSize(), paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        RectF rectF = new RectF(wheel.getLeftBound(), wheel.getUpperBound(), wheel.getRightBound(), wheel.getLowerBound());

        // Set arc properties
        paint.setARGB(255, 207, 223, 11);
        canvas.drawArc(rectF, wheel.getStartAngleGreen(), 45, false, paint);
        paint.setARGB(255, 15, 187, 166);
        canvas.drawArc(rectF, wheel.getStartAngleBlue(), 45, false, paint);
        paint.setARGB(255, 255, 69, 5);
        canvas.drawArc(rectF, wheel.getStartAngleRed(), 45, false, paint);
        paint.setARGB(255, 255, 0, 139);
        canvas.drawArc(rectF, wheel.getStartAnglePink(), 45, false, paint);
        paint.setARGB(255, 161, 33, 186);
        canvas.drawArc(rectF, wheel.getStartAnglePurple(), 45, false, paint);
        paint.setARGB(255, 255, 236, 4);

        // Draw wheel
        canvas.drawArc(rectF, wheel.getStartAngleYellow(), 45, false, paint);

        paint.setColor(Color.GRAY);
        paint.setTextSize(120);
        paint.setStrokeWidth(5);

        Rect bounds = new Rect();
        paint.getTextBounds(String.valueOf(score), 0, (String.valueOf(score)).length(), bounds);

        // Draw score
        canvas.drawText(Integer.toString(score), (xCenter) - (bounds.width()/2), 100, paint);
    }
}