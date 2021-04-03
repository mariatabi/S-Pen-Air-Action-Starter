package com.example.spenremote.racingcar;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final int INIT_SPEED = 8;

    private View contLabel;
    private TextView tvNotify;
    private TextView tvScore, tvBest;
    private ImageView ivCenter;
    private RacingCar racingCar;
    private int score, level, bestScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main);

    contLabel = findViewById(R.id.contNotify);
    tvNotify = findViewById(R.id.notify);
    tvScore = findViewById(R.id.score);
    tvBest = findViewById(R.id.best);
    ivCenter = findViewById(R.id.imgCenter);
    ivCenter.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            play();
        }
    });
    racingCar = findViewById(R.id.racingView);


    initialize();
}

    @Override
    protected void onResume() {
        super.onResume();
        if (racingCar != null && racingCar.getPlayState() == RacingCar.PlayState.Pause) {
            racingCar.resume();
        }
    }

    @Override
    protected void onPause() {
        if (racingCar != null && racingCar.getPlayState() == RacingCar.PlayState.Playing) {
            pause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {

        racingCar.reset();
        super.onDestroy();
    }

    private Handler racingHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RacingCar.MSG_SCORE:
                    score = score + (level);
                    tvScore.setText(String.valueOf(score));
                    break;
                case RacingCar.MSG_COLLISION:
                    boolean achieveBest = false;
                    if (bestScore < score) {
                        tvBest.setText(String.valueOf(score));
                        bestScore = score;
                        saveBestScore(bestScore);
                        achieveBest = true;
                    }
                    collision(achieveBest);
                    break;
                case RacingCar.MSG_COMPLETE:
                    prepare();
                    break;
                case RacingCar.MSG_PAUSE:
                    setTvNotify(R.string.pause);
                    prepare();
                    break;
                default:
                    break;
            }
        }
    };

    private void initialize() {
        reset();
        setTvNotify(R.string.ready);
        prepare();
    }

    private int loadBestScore() {
        SharedPreferences preferences = getSharedPreferences("MyFirstGame", Context.MODE_PRIVATE);
        if (preferences.contains("BestScore")) {
            return preferences.getInt("BestScore", 0);
        } else {
            return 0;
        }
    }

    private void saveBestScore(int bestScore) {
        SharedPreferences preferences = getSharedPreferences("MyFirstGame", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("BestScore", bestScore);
        editor.commit();
    }

    private void reset() {
        score = 0;
        level = 1;
        bestScore = loadBestScore();

        racingCar.setSpeed(INIT_SPEED);
        racingCar.setPlayState(RacingCar.PlayState.Ready);

        tvScore.setText(String.valueOf(score));
        tvBest.setText(String.valueOf(bestScore));
    }

    private void restart() {
        reset();
        racingCar.setPlayState(RacingCar.PlayState.Restart);
        setTvNotify(R.string.restart);
        prepare();
    }

    private void prepare() {
        RacingCar.PlayState state = racingCar.getPlayState();

        int resource = R.drawable.ic_play;
        if (state == RacingCar.PlayState.Pause) {
            resource = R.drawable.ic_pause;
        } else if (state == RacingCar.PlayState.Restart) {
            resource = R.drawable.ic_retry;
        }

        ivCenter.setImageResource(resource);
        showLabelContainer();
    }

    private void play() {
        if (racingCar.getPlayState() == RacingCar.PlayState.Collision
                || racingCar.getPlayState() == RacingCar.PlayState.Restart) {
            initialize();

            racingCar.reset();
            return;
        }

        // Click on playing
        if (racingCar.getPlayState() == RacingCar.PlayState.Playing) {
            pause();
        } else {
            ivCenter.setImageResource(R.drawable.ic_pause);

            // Click on pause
            if (racingCar.getPlayState() == RacingCar.PlayState.Pause) {
                ivCenter.setImageResource(R.drawable.ic_play);
                racingCar.resume();
                hideLabelContainer();
            } else if (racingCar.getPlayState() == RacingCar.PlayState.LevelUp) {
                racingCar.resume();
                hideLabelContainer();
            } else {
                hideLabelContainer();
                racingCar.play(racingHandler);
            }
        }
    }

    private void pause() {
        Log.d("RacingCar", "pause(): ");
        ivCenter.setImageResource(R.drawable.ic_pause);
        racingCar.pause();
    }

    private boolean canResume() {
        RacingCar.PlayState state = racingCar.getPlayState();
        if (state == RacingCar.PlayState.Pause
                || state == RacingCar.PlayState.Ready
                || state == RacingCar.PlayState.Restart) {
            return true;
        }
        return false;
    }

    private void resume() {
        hideLabelContainer();
        play();
    }

    private void setTvNotify(int stringId) {
        tvNotify.setText(stringId);
    }

    private void collision(boolean achieveBest) {
        if (achieveBest) {
            setTvNotify(R.string.best_ranking);
        } else {
            setTvNotify(R.string.try_again);
        }

        contLabel.setVisibility(View.VISIBLE);
        contLabel.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left));

        ivCenter.setImageResource(R.drawable.ic_retry);
    }

    private void showLabelContainer() {
        Log.d("RacingCar", "showLabelContainer: ");
        contLabel.setVisibility(View.VISIBLE);
        if (contLabel.getAnimation() != null) {
            contLabel.getAnimation().cancel();
        }
        contLabel.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left));
    }

    private void hideLabelContainer() {
        Log.d("RacingCar", "hideLabelContainer: ");
        Animation anim = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                contLabel.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        contLabel.startAnimation(anim);
    }

    private void moveCarTo(int direction) {
        racingCar.moveCarTo(direction);
    }

    private void moveCar() {
        racingCar.move();
    }
}