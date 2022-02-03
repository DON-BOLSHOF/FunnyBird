package ru.samsung.itschool.hello.funnybird;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


public class GameView extends View{

    private boolean isActive;
    private boolean gameOver;

    private Sprite playerBird;
    private Sprite enemyBird1;
    private Sprite enemyBird2;
    private Sprite enemyBird3;
    private Sprite money;

    private float x;
    private float y;

    private int viewWidth;
    private int viewHeight;

    private int points = 0;

    private final int timerInterval = 30;
    private final int pointLVL = 100;
    private final int gameOverPoints = -50;
    private int LVL = 1;
    private int timerNextLVL = 0;

    public GameView(Context context) {
        super(context);

        isActive = true;
        gameOver = false;
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.player);
        int w = b.getWidth()/5;
        int h = b.getHeight()/3;
        Rect firstFrame = new Rect(0, 0, w, h);
        playerBird = new Sprite(10, 0, 0, 100, firstFrame, b);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                if (i ==0 && j == 0) {
                    continue;
                }
                if (i ==2 && j == 3) {
                    continue;
                }
                playerBird.addFrame(new Rect(j*w, i*h, j*w+w, i*w+w));
            }
        }
        b = BitmapFactory.decodeResource(getResources(), R.drawable.money);
        w = b.getWidth()/7;
        h = b.getHeight();
        firstFrame = new Rect(0, 0, w, h);
        money = new Sprite(1000, 0, -100, 20, firstFrame, b);

        for (int i = 0; i < 6; i++) {
            money.addFrame(new Rect(i*w+25, 0, i*w+w-20, h));
        }

        b = BitmapFactory.decodeResource(getResources(), R.drawable.enemy);

        w = b.getWidth()/5;
        h = b.getHeight()/3;
        firstFrame = new Rect(4*w, 0, 5*w, h);

        enemyBird1 = new Sprite(2000, 250, -300, 0, firstFrame, b);
        enemyBird2 = new Sprite(enemyBird1);
        enemyBird3 = new Sprite(enemyBird1);

        for (int i = 0; i < 3; i++) {
            for (int j = 4; j >= 0; j--) {

                if (i ==0 && j == 4) {
                    continue;
                }

                if (i ==2 && j == 0) {
                    continue;
                }

                enemyBird1.addFrame(new Rect(j*w, i*h, j*w+w, i*w+w));
                enemyBird2.addFrame(new Rect(j*w, i*h, j*w+w, i*w+w));
                enemyBird3.addFrame(new Rect(j*w, i*h, j*w+w, i*w+w));
            }
        }

        enemyBird2.setX(2200);
        enemyBird2.setY(1600);
        enemyBird3.setX(1800);
        enemyBird3.setY(1000);

        Timer t = new Timer();
        t.start();
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        viewWidth = w;
        viewHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawARGB(250, 127, 199, 255);
        playerBird.draw(canvas);
        enemyBird1.draw(canvas);
        enemyBird2.draw(canvas);
        enemyBird3.draw(canvas);
        money.draw(canvas);
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setTextSize(55.0f);
        p.setColor(Color.argb(255,111,0,204));
        final RectF rect = new RectF(viewWidth/2-200,viewHeight-100,viewWidth/2+200,viewHeight-20);
        canvas.drawRoundRect(rect,50,100,p);
        p.setColor(Color.WHITE);
        canvas.drawText("Пауза",viewWidth/2-100,viewHeight-50,p);
        canvas.drawText(points + "", viewWidth - 200, 70, p);
        canvas.drawText("Уровень № " + LVL, viewWidth - 900, 70, p);
        if (gameOver){
            canvas.drawText("Вы проиграли", viewWidth/2-150,viewHeight/2,p);
        }
        if (timerNextLVL>0 && timerNextLVL<15){
            canvas.drawText("Новый уровень", viewWidth/2-150,viewHeight/2,p);
            timerNextLVL+=1;
        } else{
            timerNextLVL = 0;
        }
        if (!isActive && !gameOver){
            p.setColor(Color.BLACK);
            final RectF pauseRect = new RectF(viewWidth/2-200,viewHeight/2,viewWidth/2+200,viewHeight/2+100);
            canvas.drawRoundRect(pauseRect,50,100,p);
            p.setColor(Color.WHITE);
            canvas.drawText("Пауза",viewWidth/2-75,viewHeight/2-50,p);
            canvas.drawText("Снять паузу",viewWidth/2-150,viewHeight/2+50,p);
        }
    }

    protected void update () {
        if (isActive && !gameOver) {
            playerBird.update(timerInterval);
            enemyBird1.update(timerInterval);
            enemyBird2.update(timerInterval);
            enemyBird3.update(timerInterval);
            money.update(timerInterval);

            if (playerBird.getY() + playerBird.getFrameHeight() > viewHeight) {
                playerBird.setY(viewHeight - playerBird.getFrameHeight());
                playerBird.setVy(-playerBird.getVy());
                points-=3;
            } else if (playerBird.getY() < 0) {
                playerBird.setY(0);
                playerBird.setVy(-playerBird.getVy());
                points-=3;
            }

            if (enemyBird1.getX() < -enemyBird1.getFrameWidth()) {
                teleportEnemy(enemyBird1);
                points += 10;
            }

            if (enemyBird2.getX() < -enemyBird2.getFrameWidth()) {
                teleportEnemy(enemyBird2);
                points += 10;
            }

            if (enemyBird3.getX() < -enemyBird3.getFrameWidth()) {
                teleportEnemy(enemyBird3);
                points += 10;
            }

            if (money.getX() < -money.getFrameWidth()) {
                teleportMoney();
            }

            if (enemyBird1.intersect(playerBird)) {
                teleportEnemy(enemyBird1);
                points -= 40;
            }

            if (enemyBird2.intersect(playerBird)) {
                teleportEnemy(enemyBird2);
                points -= 40;
            }

            if (enemyBird3.intersect(playerBird)) {
                teleportEnemy(enemyBird3);
                points -= 40;
            }

            if (money.intersect(playerBird)){
                money.setVy(-1000);
                money.setVx(0);
            }

            if (money.getY()<-money.getFrameWidth()){
                teleportMoney();
            }

            if (points >= pointLVL*LVL){
                newLevel();
            }

            if (points <= gameOverPoints){
                gameOver = true;
            }
        }

        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

         x = event.getX();
         y = event.getY();

        if (x>=viewWidth/2-200 && x<=viewWidth/2+200 && y>viewHeight-100 &&y<=viewHeight-20 && isActive==true){
            isActive = false;
        }

        if (isActive && !gameOver) {
            int eventAction = event.getAction();
            if (eventAction == MotionEvent.ACTION_DOWN) {

                if (event.getY() < playerBird.getBoundingBoxRect().top) {
                    playerBird.setVy(-100);
                    points-=3;
                } else if (event.getY() > (playerBird.getBoundingBoxRect().bottom)) {
                    playerBird.setVy(100);
                    points-=3;
                }
            }
            if (x>=enemyBird1.getX() && x<=enemyBird1.getFrameWidth()+enemyBird1.getX() && y>=enemyBird1.getY() && y<=enemyBird1.getFrameHeight()+enemyBird1.getY()){
                teleportEnemy(enemyBird1);
            }
            if (x>=enemyBird2.getX() && x<=enemyBird2.getFrameWidth()+enemyBird2.getX() && y>=enemyBird2.getY() && y<=enemyBird2.getFrameHeight()+enemyBird2.getY()){
                teleportEnemy(enemyBird2);
            }
            if (x>=enemyBird3.getX() && x<=enemyBird3.getFrameWidth()+enemyBird3.getX() && y>=enemyBird3.getY() && y<=enemyBird3.getFrameHeight()+enemyBird3.getY()){
                teleportEnemy(enemyBird3);
            }
        }

        if (x>=viewWidth/2-200 && x<=viewWidth/2+200 && y>=viewHeight/2 && y<=viewHeight/2+100 && !isActive){
            isActive = true;
        }

        return true;
    }


    private void teleportEnemy (Sprite enemy) {
        enemy.setX(viewWidth + Math.random() * 500);
        enemy.setY(Math.random() * (viewHeight - enemy.getFrameHeight()));
    }

    private void teleportMoney () {
        money.setX(viewWidth/2 + Math.random() * 500);
        money.setY(Math.random() * (viewHeight - money.getFrameHeight()));
        money.setVx(-100);
        money.setVy(20);
        points += 20;
    }

    private void newLevel(){
        points = 0;
        LVL+=1;
        timerNextLVL += 1;
        enemyBird1.setVx(enemyBird1.getVx()-100);
        enemyBird2.setVx(enemyBird2.getVx()-100);
        enemyBird3.setVx(enemyBird3.getVx()-100);

    }

    class Timer extends CountDownTimer {

        public Timer() {
            super(Integer.MAX_VALUE, timerInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            update();
        }

        @Override
        public void onFinish() {

        }
    }
}