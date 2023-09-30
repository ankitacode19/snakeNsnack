package com.example.snakensnack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private final List<Snakepoints> snakepointsList = new ArrayList<>();
    private SurfaceView surfaceView;
    private TextView scoreTV;
    private String movingPosition = "right";
    private SurfaceHolder surfaceHolder;
    private int score = 0;
    private static final int pointSize = 28;
    private static final int defaultTalePoints = 2;
    private static final int snakeColor = Color.rgb(0, 254, 150);
    private static final int snakeMovingSpeed = 400;
    private int positionX, positionY;
    private Timer timer;
    private Canvas canvas = null;
    // Generate random RGB values
    Random random = new Random();
    int red = random.nextInt(256);
    int green = random.nextInt(256);
    int blue = random.nextInt(256);
    int randomColor = Color.rgb(red, green, blue);
    private Paint pointColor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //getting surface view and score textview from xml file
        surfaceView = findViewById(R.id.surfaceView);
        scoreTV = findViewById(R.id.scoreTV);

        //getting image buttons from xml file
        final AppCompatImageButton topBtn = findViewById(R.id.topBtn);
        final AppCompatImageButton leftBtn = findViewById(R.id.leftBtn);
        final AppCompatImageButton rightBtn = findViewById(R.id.rightBtn);
        final AppCompatImageButton bottomBtn = findViewById(R.id.bottomBtn);

        //adding callback to surface view
        surfaceView.getHolder().addCallback(this);
        topBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!movingPosition.equals("bottom"));{
                    movingPosition = "top";
                }
            }
        });
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!movingPosition.equals("right"));{
                    movingPosition = "left";
                }
            }
        });
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!movingPosition.equals("left"));{
                    movingPosition = "right";
                }
            }
        });
        bottomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!movingPosition.equals("top"));{
                    movingPosition = "bottom";
                }
            }
        });
    }
    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        this.surfaceHolder = surfaceHolder;
        init();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }
    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

    }
    private void init(){
        snakepointsList.clear();
        scoreTV.setText("0");
        score = 0;
        movingPosition = "right";
        int startPositionX = (pointSize) * defaultTalePoints;
        for (int i = 0; i < defaultTalePoints; i++){
            Snakepoints snakePoints = new Snakepoints(startPositionX, pointSize);
            snakepointsList.add(snakePoints);
            startPositionX -= (pointSize * 2);
            addPoints();
            moveSnake();
        }
    }
    private void addPoints(){
        int surfaceWidth = surfaceView.getWidth() - (pointSize * 2);
        int surfaceHeight = surfaceView.getHeight() - (pointSize * 2);
        int randomXposition = new Random().nextInt(surfaceWidth/pointSize);
        int randomYposition = new Random().nextInt(surfaceHeight/pointSize);

        if((randomXposition % 2) != 0){
            randomXposition = randomXposition++;
        }
        if((randomYposition % 2) != 0){
            randomYposition = randomYposition++;
        }
        positionX = (pointSize * randomXposition) + pointSize;
        positionY = (pointSize * randomYposition) + pointSize;
    }
    private void moveSnake(){
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                int headPositionX = snakepointsList.get(0).getPositionX();
                int headPositionY = snakepointsList.get(0).getPositionY();

                if(headPositionX == positionX && headPositionY == positionY){
                    growSnake();
                    addPoints();
                }
                switch (movingPosition){
                    case "right":
                        snakepointsList.get(0).setPositionX(headPositionX + (pointSize * 2));
                        snakepointsList.get(0).setPositionY(headPositionY);
                        break;

                    case "left":
                        snakepointsList.get(0).setPositionX(headPositionX - (pointSize * 2));
                        snakepointsList.get(0).setPositionY(headPositionY);
                        break;

                    case "top":
                        snakepointsList.get(0).setPositionX(headPositionX);
                        snakepointsList.get(0).setPositionY(headPositionY - (pointSize * 2));
                        break;

                    case "bottom":
                        snakepointsList.get(0).setPositionX(headPositionX);
                        snakepointsList.get(0).setPositionY(headPositionY + (pointSize * 2));
                        break;
                }
                if(checkGameOver(headPositionX, headPositionY)){
                    timer.purge();
                    timer.cancel();
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Your Score = " + score);
                    builder.setTitle("Noob fr");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Start Again", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            init();
                        }
                    });
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            builder.show();
                        }
                    });
                }
                else {
                    canvas = surfaceHolder.lockCanvas();
                    canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
                    canvas.drawCircle(snakepointsList.get(0).getPositionX(), snakepointsList.get(0).getPositionY(), pointSize, createPointColor());
                    canvas.drawCircle(positionX, positionY, pointSize, createPointColor());
                    for(int i = 1; i < snakepointsList.size(); i++){
                        int getTempPositionX = snakepointsList.get(i).getPositionX();
                        int getTempPositionY = snakepointsList.get(i).getPositionY();

                        snakepointsList.get(i).setPositionX(headPositionX);
                        snakepointsList.get(i).setPositionY(headPositionY);
                        canvas.drawCircle(snakepointsList.get(i).getPositionX(), snakepointsList.get(i).getPositionY(), pointSize, createPointColor());

                        headPositionX = getTempPositionX;
                        headPositionY = getTempPositionY;
                    }

                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        },  1000 - snakeMovingSpeed, 1000 - snakeMovingSpeed);
    }
    public void growSnake(){
        Snakepoints snakepoints = new Snakepoints(0, 0);
        snakepointsList.add(snakepoints);
        score++;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                scoreTV.setText(String.valueOf(score));
            }
        });
    }
    private boolean checkGameOver(int headPositionY, int headPositionX){
        boolean gameOver = false;
        if(snakepointsList.get(0).getPositionX() < 0 ||
                snakepointsList.get(0).getPositionY() < 0 ||
                snakepointsList.get(0).getPositionX() >= surfaceView.getWidth() ||
                snakepointsList.get(0).getPositionY() >= surfaceView.getHeight()){
            gameOver = true;
        }
        else {
            for (int i = 1; i < snakepointsList.size(); i++) {
                if(headPositionX == snakepointsList.get(i).getPositionX()||
                        headPositionY == snakepointsList.get(i).getPositionY()){
                    gameOver = true;
                    break;
                }
            }
        }
        return gameOver;
    }
    private Paint createPointColor(){
        if(pointColor == null){
            pointColor = new Paint();
            pointColor.setColor(randomColor);
            pointColor.setStyle(Paint.Style.FILL);
            pointColor.setAntiAlias(true);

            return pointColor;
        }
            return pointColor;
    }
}