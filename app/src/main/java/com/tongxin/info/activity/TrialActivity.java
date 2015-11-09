package com.tongxin.info.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Administrator on 2015/11/9.
 */
public class TrialActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String view_type = intent.getStringExtra("Type");
        if(view_type == "trial")
        {
            //申请试用

        }
        else
        {
            //忘记密码

        }
    }
}
