package com.example.paint;

import android.graphics.Paint;
import android.graphics.Rect;

public class Rectangle {
    public Paint paint;
    public Rect rect;

    public Rectangle(Rect rect, Paint paint) {
        this.rect = rect;
        this.paint = paint;
    }
}
