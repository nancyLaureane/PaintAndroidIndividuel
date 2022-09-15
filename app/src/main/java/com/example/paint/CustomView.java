package com.example.paint;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

public class CustomView extends View {

    public Rect rectangle;
    public Paint paint;

    public CustomView(Context context) {
        super(context);
        int x = 50;
        int y =  50;
        int sideLength = 200;

        rectangle = new Rect(x, y, sideLength, sideLength);

        paint = new Paint();
        paint.setColor(Color.GRAY);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.BLUE);
        canvas.drawRect(rectangle, paint);
        canvas.drawCircle(500, 100, 50, paint);
    }
}
