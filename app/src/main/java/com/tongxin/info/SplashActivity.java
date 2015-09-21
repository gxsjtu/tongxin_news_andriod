package com.tongxin.info;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;

//闪屏页,可以用来检测app的合法性和新版本的验证，以及预加载一些数据
public class SplashActivity extends AppCompatActivity {

    private RelativeLayout splash_rl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        splash_rl = (RelativeLayout) findViewById(R.id.splash_rl);
        startAnim();
    }

    private void startAnim() {
        AnimationSet set = new AnimationSet(false);
        //缩放动画
        ScaleAnimation scale = new ScaleAnimation(2, 1, 2, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(1000);
        scale.setFillAfter(true);

        //渐变动画
        AlphaAnimation alpha = new AlphaAnimation(0,1);
        alpha.setDuration(2000);
        alpha.setFillAfter(true);

        set.addAnimation(scale);
        set.addAnimation(alpha);

        //监听动画
        set.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            //动画结束
            @Override
            public void onAnimationEnd(Animation animation) {
                //跳转到下一页

            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        splash_rl.startAnimation(set);
    }
}
