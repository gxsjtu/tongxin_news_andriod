package com.tongxin.info.activity;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.tongxin.info.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by cc on 2015/11/13.
 */
public class DetailForShowImg extends Activity {
    private ImageView img_ForShow;
    private String url;
    private Bitmap bitmap = null;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    img_ForShow.setImageBitmap(bitmap);
                    break;
                case 1:
                   finish();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sq_showimg);

        Intent intent = getIntent();
        url = intent.getStringExtra("IMGURLFORSHOW");
        img_ForShow = (ImageView)findViewById(R.id.img_ForShow);
        returnBitMap();
//        if(bitmap != null)
//        {
//            img_ForShow.setImageBitmap(bitmap);
//        }
//        else
//        {
//            finish();
//        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {

            case MotionEvent.ACTION_DOWN:
                finish();
//                System.out.println("aaaaaaaaaaaaaaa");
                break;
        }

        return true;
    }

    private void returnBitMap() {

            new Thread() {
                @Override
                public void run() {
                    final Message msg = Message.obtain();
                    try {
                        URL myFileUrl = null;
                        try {
                            myFileUrl = new URL(url);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);
                        msg.what = 0;
                    is.close();
                    } catch (IOException e) {
                        msg.what = 1;
                        e.printStackTrace();
                }
                    mHandler.sendMessage(msg);
            }
        }.start();
    }
}
