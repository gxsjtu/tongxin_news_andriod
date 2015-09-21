package com.tongxin.info.com.tongxin.info.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;

import com.tongxin.info.R;
import com.tongxin.info.com.tongxin.info.utils.SharedPreUtils;

//闪屏页,可以用来检测app的合法性和新版本的验证，以及预加载一些数据
public class SplashActivity extends AppCompatActivity {

    private RelativeLayout splash_rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        splash_rl = (RelativeLayout) findViewById(R.id.splash_rl);
        startAnim();//开始动画
    }

    private void startAnim() {
        AnimationSet set = new AnimationSet(false);
        //缩放动画
        ScaleAnimation scale = new ScaleAnimation(2, 1, 2, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(1000);
        scale.setFillAfter(true);

        //渐变动画
        AlphaAnimation alpha = new AlphaAnimation(0, 1);
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
                nextPage();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        splash_rl.startAnimation(set);
    }

    private void nextPage() {
        //判断是否进入过新手指引页面
        boolean userGuide = SharedPreUtils.getBoolean(this, "is_user_guide_showed", false);
        if (!userGuide) {
            //跳的新手指引页
            startActivity(new Intent(SplashActivity.this, GuideActivity.class));
        } else {
            //跳到主页
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
        }
        finish();
    }
}
