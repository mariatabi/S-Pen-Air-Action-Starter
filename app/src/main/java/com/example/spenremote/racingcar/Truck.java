package com.example.spenremote.racingcar;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.NonNull;

public class Truck {
    public int x = 0;
    public int y = 0;
    public int width = 0;
    public int height = 0;
    private Rect boundary = null;
    private boolean isLast;
    private Bitmap mBitmap;

    public Truck(@NonNull Bitmap bitmap) {
        mBitmap = bitmap;

        width = bitmap.getWidth();
        height = bitmap.getHeight();
        boundary = new Rect();
        isLast = false;
    }

    public Truck(@NonNull Bitmap bitmap, int x, int y) {
        mBitmap = bitmap;

        this.x = x;
        this.y = y;

        width = bitmap.getWidth();
        height = bitmap.getHeight();

        boundary = new Rect();
        isLast = false;
    }


    public void draw(Canvas canvas) {
        canvas.drawBitmap(mBitmap, x, y, null);
    }

    public void moveDown(int speed) {
        y += speed;
    }

    public void draw(Canvas canvas, boolean b) {
        canvas.drawBitmap(mBitmap, x, y, null);
    }

    public boolean isLast() {
        return isLast;
    }

    public void setLast(boolean last) {
        isLast = last;
    }

    public void setPosition(int x) {
        this.x = x;
    }

    public Rect getBoundary() {
        int handicap = 20;
        boundary.left = x + handicap;
        boundary.top = y + handicap;
        boundary.right = x + width - handicap;
        boundary.bottom = y + height - handicap;
        return boundary;
    }
}
