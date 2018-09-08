package com.example.davis.namegame;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.CountDownTimer;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GameActivity extends AppCompatActivity {
    String[] memberNames;
    Button button1;
    Button button2;
    Button button3;
    Button button4;
    Button endGame;
    TextView scoreTextView;
    TextView timeTextView;
    ImageView currentPicture;
    ArrayList<Integer> currentOptions;
    int randomInt;
    int ID;
    int currentRandomInt;
    int score;
    int answerIndex;
    String sText;
    TypedArray pics;
    Random rand;
    CountDownTimer timer;
    String currentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        memberNames = getResources().getStringArray(R.array.member_names);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        endGame = findViewById(R.id.endGame);
        currentPicture = findViewById(R.id.currentPicture);
        currentOptions = new ArrayList<>();
        score = 0;
        scoreTextView = findViewById(R.id.score);
        timeTextView = findViewById(R.id.timeText);
        sText = getResources().getString(R.string.score_text);
        pics = getResources().obtainTypedArray(R.array.pictures);
        rand = new Random();
        updateScore();
        playGame();
    }

    public void playGame() {
        initializeBoard();
        timer = new CountDownTimer(5000, 1000) {
            public void onTick(long millisUntilFinished) {
                timeTextView.setText(Long.toString(millisUntilFinished / 1000 + 1));
            }

            public void onFinish() {
                playGame();
            }
        }.start();
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCorrect(0);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCorrect(1);
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCorrect(2);
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCorrect(3);
            }
        });
        endGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlert();
            }
        });
        currentPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertContact();
            }
        });
    }

    public void insertContact() {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);

        intent.putExtra(ContactsContract.Intents.Insert.NAME, currentName);

        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
        boolean isIntentSafe = activities.size() > 0;
        if (isIntentSafe) {
            startActivity(intent);
        }
    }

    public void checkCorrect(int index) {
        if (answerIndex != index) {
            Toast.makeText(getApplicationContext(), "Incorrect name", Toast.LENGTH_SHORT).show();
        } else {
            score += 1;
            updateScore();
        }
        timer.cancel();
        playGame();
    }

    public void showAlert() {
        //AlertDialog from StackOverflow: https://stackoverflow.com/questions/2115758/how-do-i-display-an-alert-dialog-on-android

        new AlertDialog.Builder(this)
                .setTitle("Ending game")
                .setMessage("Are you sure you want to end the game?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intentRestartGame = new Intent(GameActivity.this, MainActivity.class);
                        GameActivity.this.startActivity(intentRestartGame);
                    }
                }).setNegativeButton("No", null).show();
    }

    public void updateScore() {
        scoreTextView.setText(sText + Integer.toString(score));
    }
    public void initializeBoard() {
        //Got code for randomly getting a picture from StackOverflow: https://stackoverflow.com/questions/15545753/random-genaration-of-image-from-drawable-folder-in-android
        currentOptions = new ArrayList<>();
        randomInt = rand.nextInt(pics.length());
        ID = pics.getResourceId(randomInt, 0);

        currentPicture.setImageResource(ID);
        currentOptions.add(randomInt);
        currentName = memberNames[randomInt];
        int i = 0;
        while (i < 3) {
            currentRandomInt = rand.nextInt(pics.length());
            if ((currentRandomInt != randomInt) && (!currentOptions.contains(currentRandomInt))) {
                currentOptions.add(currentRandomInt);
                i += 1;
            }
        }
        Collections.shuffle(currentOptions);
        button1.setText(memberNames[currentOptions.get(0)]);
        button2.setText(memberNames[currentOptions.get(1)]);
        button3.setText(memberNames[currentOptions.get(2)]);
        button4.setText(memberNames[currentOptions.get(3)]);
        for (int k = 0; k < 4; k += 1) {
            if (currentOptions.get(k) == randomInt) {
                answerIndex = k;
            }
        }
    }
}
