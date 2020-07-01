package com.example.aabir.metravv2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

public class SplashScreen extends AppCompatActivity {

    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        imageView = (ImageView) findViewById(R.id.splashScreenImage);

// Load the animation like this
        Animation animSlide = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide);

        Display display = getWindowManager().getDefaultDisplay();
        float width = display.getWidth();
        TranslateAnimation animation = new TranslateAnimation(-10, width-100,
                0, 0);
        animation.setDuration(3000);
        animation.setRepeatCount(0);
        animation.setRepeatMode(0);
        animation.setFillAfter(true);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                SplashScreen.this.finish();
                Intent intent=new Intent(SplashScreen.this,NavDrawerMainMenuActivity.class);
                startActivity(intent);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
// Start the animation like this
        imageView.startAnimation(animation);
    }
}
