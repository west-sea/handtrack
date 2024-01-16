package com.example.myapplication;

import static com.example.myapplication.blocks.iBlock;
import static com.example.myapplication.blocks.jBlock;
import static com.example.myapplication.blocks.lBlock;
import static com.example.myapplication.blocks.nowBlock;
import static com.example.myapplication.blocks.num;
import static com.example.myapplication.blocks.oBlock;
import static com.example.myapplication.blocks.randomNumber;
import static com.example.myapplication.blocks.sBlock;
import static com.example.myapplication.blocks.tBlock;
import static com.example.myapplication.blocks.zBlock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class Draw extends View {

    private int motion = Stational;
    public static final int Stational = 0;
    public static final int Left = 1;
    public static final int Right = 2;
    public static final int rotate = 3;
    public static final int Down = 4;

    private static final int xmax = 10;
    private static final int ymax = 15;
    static final int blocksize = 75;
    static int fieldW = xmax * blocksize;
    static int fieldH = ymax * blocksize;

    public static int offsetx = 0;
    public static int offsety = 0;

    public static final int blockLenght = 4;
    int[][] field = new int[15][10];
    public static int score = 0;
    public static boolean gameOverFlag = false;
    static boolean moveflag;
    int count = 0;
    int conmbo = 0;
    static int ghosty;


    Paint paint = new Paint();
    blocks bs = new blocks();

    public Draw(Context context) {
        super(context);
    }

    public Draw(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Draw(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void onDraw(Canvas ca) {
        super.onDraw(ca);

        randomNumber();

        //블록이 선택된 후, 처리방식
        if (!moveflag) {
            initstartpoi();
            bs.setNowBlock();
        }
        if (!gameOverFlag) {
            switch (motion) {
                //오른쪽 이동
                case Right:
                    if (canMove(1, 0, nowBlock)) {
                        offsetx = offsetx + 1;
                    }
                    break;

                //왼쪽 이동
                case Left:
                    if (canMove(-1, 0, nowBlock)) {
                        offsetx = offsetx - 1;
                    }
                    break;

                //회전 처리
                case rotate:
                    if (canRotate(0, 0, nowBlock)) {
                        bs.roteta();
                    }
                    break;

                //아래쪽 이동
                case Down:
                    if (canMove(0, 1, nowBlock)) {
                        offsety++;
                    } else {
                        if (!canMove(0, 0, nowBlock)) {
                            gameOverFlag = true;
                        }
                        blockFixt();
                        checkfield();
                    }

                default:
                    break;
            }
            blockDraw(ca);
        }
    }

    //회전 or 좌우 움직임 취득
    public void showfield(int motion) {
        invalidate();
        this.motion = motion;
    }

    //reset 처리
    public void reset() {
        gameOverFlag = false;
        score = 0;
        motion = Stational;
        moveflag = false;
        resetfield();
    }

    // field reset 처리
    public void resetfield() {
        for (int i = 0; i < ymax; i++) {
            for (int j = 0; j < xmax; j++) {
                field[i][j] = 0;
            }
        }
    }


    //블록 그리기 처리
    public void blockDraw(Canvas ca) {


        Paint p0 = new Paint();
        Paint p1 = new Paint();
        p0.setColor(Color.BLACK);
        p1.setColor(Color.WHITE);
        p1.setStyle(Paint.Style.STROKE);
        ca.drawRect(0, 0, fieldW, fieldH, p0);
        //固定されたブロックの描画処理
        for (int i = 0; i < ymax; i++) {
            for (int j = 0; j < xmax; j++) {
                int px = j * blocksize;
                int py = i * blocksize;
                switch (field[i][j]) {
                    case tBlock:
                        paint.setColor(Color.BLUE);
                        ca.drawRect(px + blocksize, py + blocksize, px, py, paint);
                        ca.drawRect(px + blocksize, py + blocksize, px, py, p1);
                        break;
                    case sBlock:
                        paint.setColor(Color.GREEN);
                        ca.drawRect(px + blocksize, py + blocksize, px, py, paint);
                        ca.drawRect(px + blocksize, py + blocksize, px, py, p1);
                        break;
                    case iBlock:
                        paint.setColor(Color.RED);
                        ca.drawRect(px + blocksize, py + blocksize, px, py, paint);
                        ca.drawRect(px + blocksize, py + blocksize, px, py, p1);
                        break;
                    case oBlock:
                        paint.setColor(Color.argb(250, 0, 255, 255));
                        ca.drawRect(px + blocksize, py + blocksize, px, py, paint);
                        ca.drawRect(px + blocksize, py + blocksize, px, py, p1);
                        break;
                    case lBlock:
                        paint.setColor(Color.argb(255, 255, 0, 255));
                        ca.drawRect(px + blocksize, py + blocksize, px, py, paint);
                        ca.drawRect(px + blocksize, py + blocksize, px, py, p1);
                        break;
                    case jBlock:
                        paint.setColor(Color.YELLOW);
                        ca.drawRect(px + blocksize, py + blocksize, px, py, paint);
                        ca.drawRect(px + blocksize, py + blocksize, px, py, p1);
                        break;
                    case zBlock:
                        paint.setColor(Color.argb(255, 255, 165, 0));
                        ca.drawRect(px + blocksize, py + blocksize, px, py, paint);
                        ca.drawRect(px + blocksize, py + blocksize, px, py, p1);
                        break;
                }
                ca.drawRect(px + blocksize, py + blocksize, px, py, p1);
            }
        }
        //움직이는 블록의 그리기 처리
        for (int i = 0; i < blockLenght; i++) {
            for (int j = 0; j < blockLenght; j++) {
                if (blocks.nowBlock[i][j] == 1) {
                    drawMoveBlock(+offsetx + j, offsety + i, ca);
                    ghostDraw(offsetx + j, getGhosty() + i, ca);
                }
            }
        }
    }


    //움직이는 블록 그리기 처리
    public void drawMoveBlock(int x, int y, Canvas ca) {

        Paint p1 = new Paint();
        p1.setColor(Color.WHITE);
        p1.setStyle(Paint.Style.STROKE);

        int px = x * blocksize;
        int py = y * blocksize;

        switch (num) {
            case tBlock:
                paint.setColor(Color.BLUE);
                ca.drawRect(px + blocksize, py + blocksize, px, py, paint);
                ca.drawRect(px + blocksize, py + blocksize, px, py, p1);
                break;

            case sBlock:
                paint.setColor(Color.GREEN);
                ca.drawRect(px + blocksize, py + blocksize, px, py, paint);
                ca.drawRect(px + blocksize, py + blocksize, px, py, p1);
                break;

            case iBlock:
                paint.setColor(Color.RED);
                ca.drawRect(px + blocksize, py + blocksize, px, py, paint);
                ca.drawRect(px + blocksize, py + blocksize, px, py, p1);
                break;

            case oBlock:
                paint.setColor(Color.argb(255, 0, 255, 255));
                ca.drawRect(px + blocksize, py + blocksize, px, py, paint);
                ca.drawRect(px + blocksize, py + blocksize, px, py, p1);
                break;

            case lBlock:
                paint.setColor(Color.argb(255, 255, 0, 255));
                ca.drawRect(px + blocksize, py + blocksize, px, py, paint);
                ca.drawRect(px + blocksize, py + blocksize, px, py, p1);
                break;

            case jBlock:
                paint.setColor(Color.YELLOW);
                ca.drawRect(px + blocksize, py + blocksize, px, py, paint);
                ca.drawRect(px + blocksize, py + blocksize, px, py, p1);
                break;

            case zBlock:
                paint.setColor(Color.argb(255, 255, 165, 0));
                ca.drawRect(px + blocksize, py + blocksize, px, py, paint);
                ca.drawRect(px + blocksize, py + blocksize, px, py, p1);
                break;

            default:
                break;
        }
    }

    //블록 출현 위치 정의 (offset 초기화)
    public static void initstartpoi() {
        offsetx = xmax / 2 - blockLenght / 2;
        offsety = 0;
    }

    //회전 가능하지 확인하는 처리

    public int[][] getRotatedBlock(int[][] currentBlock){
        int[][] rotatedBlock = new int[blockLenght][blockLenght];
        for(int i=0 ; i<blockLenght; i++){
            for(int j=0; j<blockLenght; j++){
                rotatedBlock[i][j] = currentBlock[blockLenght - j -1][i];
            }
        }
        return rotatedBlock;
    }

    public boolean canRotate(int dx, int dy, int[][] currentBlock){
        int[][] rotatedBlock = getRotatedBlock(currentBlock);

        for(int i = 0; i < blockLenght; i++){
            for(int j = 0; j < blockLenght; j++){
                if(rotatedBlock[i][j] != 0){
                    int nx = offsetx + j + dx;
                    int ny = offsety + i + dy;

                    // Check if the new x and y coordinates are within the field boundaries
                    if(nx < 0 || nx >= xmax || ny < 0 || ny >= ymax){
                        return false;
                    }

                    // Check if the new position overlaps with existing blocks in the field
                    if(field[ny][nx] != 0){
                        return false;
                    }
                }
            }
        }
        return true;
    }


    //이동 가능한지 확인하는 처리 --- then 회전 가능한지 확인하는 처리???
    public boolean canMove(int dx, int dy, int[][] nowBlock) {
        for (int i = 0; i < blockLenght; i++) {
            for (int j = 0; j < blockLenght; j++) {
                if (nowBlock[i][j] != 0) {
                    int nx = offsetx + j + dx;
                    int ny = offsety + i + dy;
                    if (ny < 0) {
                        return false;
                    }
                    if (nx < 0) {
                        return false;
                    }
                    if (nx >= xmax) {
                        return false;
                    }
                    if (ny >= ymax) {
                        return false;
                    }
                    if (field[ny][nx] != 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean ghostCanMove(int dx, int dy, int[][] nowBlock) {
        for (int i = 0; i < blockLenght; i++) {
            for (int j = 0; j < blockLenght; j++) {
                if (nowBlock[i][j] != 0) {
                    int nx = offsetx +j + dx;
                    int ny = ghosty + i + dy;
                    if (ny < 0) {
                        return false;
                    }
                    if (nx < 0) {
                        return false;
                    }
                    if (nx >= xmax) {
                        return false;
                    }
                    if (ny >= ymax) {
                        return false;
                    }
                    if (field[ny][nx] != 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }


    //블록 고정 처리 (nowblock에서 field로 대체)
    public void blockFixt() {
        for (int i = 0; i < blockLenght; i++) {
            for (int j = 0; j < blockLenght; j++) {
                switch (num) {
                    case tBlock:
                        if (nowBlock[i][j] == 1) {
                            field[offsety + i][offsetx + j] = 1;
                        }
                        break;
                    case sBlock:
                        if (nowBlock[i][j] == 1) {
                            field[offsety + i][offsetx + j] = 2;
                        }
                        break;
                    case iBlock:
                        if (nowBlock[i][j] == 1) {
                            field[offsety + i][offsetx + j] = 3;
                        }
                        break;
                    case oBlock:
                        if (nowBlock[i][j] == 1) {
                            field[offsety + i][offsetx + j] = 4;
                        }
                        break;
                    case lBlock:
                        if (nowBlock[i][j] == 1) {
                            field[offsety + i][offsetx + j] = 5;
                        }
                        break;
                    case jBlock:
                        if (nowBlock[i][j] == 1) {
                            field[offsety + i][offsetx + j] = 6;
                        }
                        break;
                    case zBlock:
                        if (nowBlock[i][j] == 1) {
                            field[offsety + i][offsetx + j] = 7;
                        }
                        break;
                }
            }
        }
        bs.randomNumbers.remove(0);
        moveflag = false;
    }

    //열의 삭제 처리
    public void clearLine(int row) {
        for (int j = 0; j < xmax; j++) {
            field[row][j] = 0;
        }
        for (int i = row; i > 0; i--) {
            for (int j = 0; j < xmax; j++) {
                field[i][j] = field[i - 1][j];
            }
        }
        for (int j = 0; j < xmax; j++) {
            field[0][j] = 0;
        }
    }

    //field에서 열을 확인, 삭제하는 처리
    public void checkfield() {
        for (int i = 0; i < ymax; i++) {
            for (int j = 0; j < xmax; j++) {
                if (field[i][j] == 0) {
                    count = 0;
                    conmbo = 0;
                    break;
                } else if (j == xmax - 1 && field[i][j] > 0) {
                    clearLine(i);
                    count++;
                    conmbo++;
                }
            }
        }
        if (count > 0) {
            score += 100 * (1 + conmbo * 0.1) * count;
        }
    }

    //ghost 낙하예측
    public int getGhosty() {
        ghosty = 0;
        while (ghostCanMove(0, 1, nowBlock)) {
            ghosty++;
        }
        return ghosty;
    }

    //고스트 묘사
    public void ghostDraw(int x, int y, Canvas ca) {
        Paint p1 = new Paint();
        p1.setColor(Color.WHITE);
        p1.setStyle(Paint.Style.STROKE);

        int px = x * blocksize;
        int py = y * blocksize;

        switch (num) {
            case tBlock:
                paint.setColor(Color.argb(127, 0, 0, 255));
                ca.drawRect(px + blocksize, py + blocksize, px, py, paint);
                ca.drawRect(px + blocksize, py + blocksize, px, py, p1);
                break;

            case sBlock:
                paint.setColor(Color.argb(127, 0, 255, 0));
                ca.drawRect(px + blocksize, py + blocksize, px, py, paint);
                ca.drawRect(px + blocksize, py + blocksize, px, py, p1);
                break;

            case iBlock:
                paint.setColor(Color.argb(127, 255, 0, 0));
                ca.drawRect(px + blocksize, py + blocksize, px, py, paint);
                ca.drawRect(px + blocksize, py + blocksize, px, py, p1);
                break;

            case oBlock:
                paint.setColor(Color.argb(127, 0, 255, 255));
                ca.drawRect(px + blocksize, py + blocksize, px, py, paint);
                ca.drawRect(px + blocksize, py + blocksize, px, py, p1);
                break;

            case lBlock:
                paint.setColor(Color.argb(127, 255, 0, 255));
                ca.drawRect(px + blocksize, py + blocksize, px, py, paint);
                ca.drawRect(px + blocksize, py + blocksize, px, py, p1);
                break;

            case jBlock:
                paint.setColor(Color.argb(127, 255, 255, 0));
                ca.drawRect(px + blocksize, py + blocksize, px, py, paint);
                ca.drawRect(px + blocksize, py + blocksize, px, py, p1);
                break;

            case zBlock:
                paint.setColor(Color.argb(127, 255, 165, 0));
                ca.drawRect(px + blocksize, py + blocksize, px, py, paint);
                ca.drawRect(px + blocksize, py + blocksize, px, py, p1);
                break;

            default:
                break;
        }
    }
}
