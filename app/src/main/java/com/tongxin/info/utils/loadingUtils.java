package com.tongxin.info.utils;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.tongxin.info.R;
import com.victor.loading.rotate.RotateLoading;

/**
 * Created by Administrator on 2015/10/22.
 */
public class loadingUtils {
    Activity mActivity;
    RelativeLayout loading;
    RotateLoading rotateloading;
    public loadingUtils(Activity mActivity) {
        this.mActivity = mActivity;
        initViews();
    }

    private void initViews()
    {
        loading = (RelativeLayout) mActivity.findViewById(R.id.loading);
        rotateloading = (RotateLoading) mActivity.findViewById(R.id.rotateloading);
        loading.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    public void show()
    {
        loading.setVisibility(View.VISIBLE);
        rotateloading.start();
    }

    public void close()
    {
        loading.setVisibility(View.GONE);
        rotateloading.stop();
    }
}

