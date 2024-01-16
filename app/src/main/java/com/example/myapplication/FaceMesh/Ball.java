package com.example.myapplication.FaceMesh;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Ball {
    private int x;
    private int y;
    private int radius;
    private int dx;
    private int dy;
    private int color;
    public int top;
    public int bottom;
    public int left;
    public int right;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Ball(int x, int y, int radius, int dx, int dy, int color) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.dx = dx;
        this.dy = dy;
        this.color = color;

        //상하좌우 끝점 좌표
        this.top = y - radius;
        this.bottom = y + radius;
        this.left = x - radius;
        this.right = x + radius;
    }

    public void update(int screenWidth, int screenHeight) {
        x += dx;
        y += dy;

        // ********* UPDATE가 되지 않아서 문제였을지도 *********
        this.top = y - radius;
        this.bottom = y + radius;
        this.left = x - radius;
        this.right = x + radius;

        // Collision with edges
        //if (x < radius+400 || x > screenWidth - radius-400) {
        if (x < radius + 200 || x > screenWidth - radius - 400) {
            dx = -dx;
        }
        //if (y < radius+300 || y > screenHeight - radius-300) {
        if (y < radius + 400 || y > screenHeight - radius - 800) {
            dy = -dy;
        }

        this.top = y - radius;
        this.bottom = y + radius;
        this.left = x - radius;
        this.right = x + radius;
    }

    public void draw(Canvas canvas, Paint paint) {
        paint.setColor(color);
        canvas.drawCircle(x, y, radius, paint);
    }
}