package colourwhirl.manpreet.com.colourwhirl;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

public class HomeView extends View {
    private GameState gameState;

    public HomeView(Context ctx, GameState gameState){
        super(ctx);
        this.gameState = gameState;
    }

    @Override
    public void onDraw(Canvas canvas){
        this.gameState.draw(canvas, new Paint());
    }
}
