package com.example.paint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DrawView<Protected> extends View {


    private static final float TOUCH_TOLERANCE = 4;

    private float mX, mY, shapeX, shapeY;

    private Path mPath;

    // shape, pencil
    private String mAction;

    // the Paint class encapsulates the color
    // and style information about
    // how to draw the geometries,text and bitmaps
    private Paint mPaint, paint;

    // list of rects
    private ArrayList<Rectangle> rects = new ArrayList<>();

    // shape
    private Rect mRect;

    // ArrayList to store all the strokes
    // drawn by the user on the Canvas
    private ArrayList<Stroke> paths = new ArrayList<>();
    private int currentColor;
    private int strokeWidth;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mBitmapPaint = new Paint(Paint.DITHER_FLAG);


    // Constructors to initialise all the attributes
    public DrawView(Context context) {
        this(context, null);
    }

    public DrawView(Context context, AttributeSet attrs) {

        super(context, attrs);

        mPaint = new Paint();
        paint = new Paint();

        // set default action
        mAction = "pencil";

        // the below methods smoothens
        // the drawings of the user
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.GREEN);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        // 0xff=255 in decimal
        mPaint.setAlpha(0xff);
    }


    // this method instantiate the bitmap and object
    public void init(int height, int width) {

        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

        // set an initial color of the brush
        currentColor = Color.RED;

        // set an initial brush size
        strokeWidth = 20;

        mX = 0;
        mY = 0;
        // shape attrs
        shapeX = 0;
        shapeY = 0;
    }


    // sets the current color of stroke
    public void setColor(int color) {
        currentColor = color;
    }

    // sets the stroke width
    public void setStrokeWidth(int width) {
        strokeWidth = width;
    }

    // sets the current color of stroke
    public void setAction(String action) {
        mAction = action;
    }



    public void undo() {
        // check whether the List is empty or not
        // if empty, the remove method will return an error
        if (paths.size() != 0) {
            paths.remove(paths.size() - 1);
            invalidate();
        }

    }
    public void move() {
        // check whether the List is empty or not
        // if empty, the remove method will return an error
        if (paths.size() != 0) {
            paths.remove(paths.size() - 1);
            invalidate();
        }

    }


    // this methods returns the current bitmap

    public Bitmap save() {
        return mBitmap;
    }


    // this is the main method where

    // the actual drawing takes place

    @Override
    protected void onDraw(Canvas canvas) {

        // save the current state of the canvas before,
        // to draw the background of the canvas
        canvas.save();

        // DEFAULT color of the canvas
        int backgroundColor = Color.WHITE;
        mCanvas.drawColor(backgroundColor);

        // now, we iterate over the list of paths
        // and draw each path on the canvas
        for (Stroke fp : paths) {
            mPaint.setColor(fp.color);
            mPaint.setStyle(fp.fillStyle == 1 ? Paint.Style.FILL : Paint.Style.STROKE);
            mPaint.setStrokeWidth(fp.strokeWidth);
            mCanvas.drawPath(fp.path, mPaint);
        }

        // draw rect
        if (mAction.equals("shape")) {
            paint.setColor(mPaint.getColor());
            mCanvas.drawRect(mRect, paint);
        }
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);

        canvas.restore();

    }

    // the below methods manages the touch
    // response of the user on the screen
    // firstly, we create a new Stroke
    // and add it to the paths list
    private void touchStart(float x, float y) {
        mPath = new Path();

        // finally remove any curve
        // or line from the path
        mPath.reset();

        if (mAction.equals("pencil")) {
            Stroke fp = new Stroke(currentColor, strokeWidth, mPath, 0);
            paths.add(fp);

            // this methods sets the starting
            // point of the line being drawn
            mPath.moveTo(x, y);
            // we save the current
            // coordinates of the finger
            mX = x;
            mY = y;

        } else if (mAction.equals("rect")) {
            Stroke fp = new Stroke(currentColor, strokeWidth, mPath, 1);
            paths.add(fp);
            mPath.addRect(x, y, x, y, Path.Direction.CW);
            shapeX = x;
            shapeY = y;
        } else {
            shapeX = x;
            shapeY = y;
            mY = 0; mX = 0;
            Stroke fp = new Stroke(currentColor, strokeWidth, mPath, 1);
            paths.add(fp);
            mPath.addCircle(x, y, (float) 0, Path.Direction.CW);
        }

    }


    // in this method we check
    // if the move of finger on the
    // screen is greater than the
    // Tolerance we have previously defined,
    // then we call the quadTo() method which
    // actually smooths the turns we create,
    // by calculating the mean position between
    // the previous position and current position
    private void touchMove(float x, float y) {

        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);

        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {

            if (mAction.equals("pencil")) {
                mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            } else if (mAction.equals("rect")) {
                //mRect = new Rect((int) shapeX,  (int) shapeY, (int)( x), (int)(y));
                mPath.reset();
                mPath.addRect(shapeX, shapeY, x, y, Path.Direction.CW);
            } else {
                mPath.reset();
                mPath.addCircle(shapeX, shapeY, Math.min((x + mX) / 4, (y + mY) / 4), Path.Direction.CW);
            }
            mX = x;
            mY = y;
        }
    }


    // at the end, we call the lineTo method
    // which simply draws the line until
    // the end position

    private void touchUp() {
        if (mAction.equals("pencil")) {
            mPath.lineTo(mX, mY);
        } else if (mAction.equals("rect")){
            mPath.addRect(shapeX, shapeY, mX, mY, Path.Direction.CW);
        } else if (mAction.equals("circle")){
            //mPath.addCircle(shapeX, shapeY, Math.min(mX, mY), Path.Direction.CW);
        }
    }

    // the onTouchEvent() method provides us with
    // the information about the type of motion
    // which has been taken place, and according
    // to that we call our desired methods

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        if (mAction.equals("move")) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    paths.get(0).path.moveTo(x, y);
                    invalidate();
                    break;

                case MotionEvent.ACTION_MOVE:
                    paths.get(0).path.lineTo(x, y);
                    invalidate();
                    break;

                case MotionEvent.ACTION_UP:
                    invalidate();
                    break;
            }

            invalidate();

        } else {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touchStart(x, y);
                    invalidate();
                    break;

                case MotionEvent.ACTION_MOVE:
                    touchMove(x, y);
                    invalidate();
                    break;

                case MotionEvent.ACTION_UP:
                    touchUp();
                    invalidate();
                    break;
            }
        }

        return true;

    }
}
