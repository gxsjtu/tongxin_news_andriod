package com.tongxin.info.activity;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.tongxin.info.R;
import com.tongxin.info.utils.loadingUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by cc on 2015/11/13.
 */
public class DetailForShowImg extends BaseActivity {
    private ImageView img_ForShow;
    private String url;
    private Bitmap bitmap = null;
    private String isCanFinish;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
//                    img_ForShow.setImageBitmap(bitmap);
                    hideLoading();
                    break;
                case 1:
                    hideLoading();
                    finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sq_showimg);
        isCanFinish = "";
        Intent intent = getIntent();
        url = intent.getStringExtra("IMGURLFORSHOW");
        img_ForShow = (ImageView) findViewById(R.id.img_ForShow);

        showLoading();
//        ImageLoader.getInstance().displayImage(url, img_ForShow);
        ImageLoader.getInstance().displayImage(url, img_ForShow, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
                Toast.makeText(DetailForShowImg.this, "图片加载失败，请稍后重试！", Toast.LENGTH_SHORT).show();
                hideLoading();
                setResult(20);
                finish();
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                hideLoading();
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });
//        loadingUtils.show();
//        ImageLoader.getInstance().loadImage(url,new SimpleImageLoadingListener()
//        {
//            public void onLoadingComplete(String imageUri, android.view.View view, android.graphics.Bitmap loadedImage)
//            {
//                img_ForShow.setImageBitmap(loadedImage);
//                loadingUtils.close();
//            }
//
//            public void onLoadingFailed(String imageUri, android.view.View view, com.nostra13.universalimageloader.core.assist.FailReason failReason) {
//                Toast.makeText(DetailForShowImg.this, "图片加载失败，请稍后重试！", Toast.LENGTH_SHORT).show();
//                loadingUtils.close();
//                setResult(20);
//                finish();
//            };
//        });


//        showImg();
//        closeRef();
//        ImageLoader.getInstance().displayImage(url, img_ForShow);
//        loadingUtils.close();
//        returnBitMap();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            hideLoading();
            setResult(20);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {

            case MotionEvent.ACTION_DOWN:
//
//                finish();
                setResult(20);
                finish();
//                System.out.println("aaaaaaaaaaaaaaa");
                break;
        }

        return true;
    }

    private void closeRef()
    {
        new Thread() {
            @Override
            public void run() {
                final Message msg = Message.obtain();

                try {
                    ImageLoader.getInstance().displayImage(url, img_ForShow);
                    msg.what = 0;
//                    isCanFinish = "YES";
                } catch (Exception e) {
                    msg.what = 1;
//                    isCanFinish = "YES";
                    e.printStackTrace();
                }
                mHandler.sendMessage(msg);
            }
        }.start();
    }

    private void showImg()
    {
        new Thread() {
            @Override
            public void run() {
                final Message msg = Message.obtain();
                try {
                    ImageLoader.getInstance().displayImage(url, img_ForShow);
                    msg.what = 0;
                }
                catch (Exception ex)
                {
                    msg.what = 1;
                    ex.printStackTrace();
                }
                mHandler.sendMessage(msg);
            }
        }.start();
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
//                        BitmapFactory.Options opt = new BitmapFactory.Options();
                        HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                        BitmapFactory.Options options = new  BitmapFactory.Options();

                        options.inJustDecodeBounds =  false;
                        options.inPreferredConfig =  Bitmap.Config.RGB_565;
                        options.inPurgeable = true;
                        options.inInputShareable = true;
                        options.inSampleSize = 2;
                    InputStream is = conn.getInputStream();
//                        bmp = BitmapFactory.decodeFileDescriptor(is.getFD(), null, opts);
//                    bitmap = comp(BitmapFactory.decodeStream(is,null,options));
                        bitmap = BitmapFactory.decodeStream(is,null,options);
                       // comp(bitmap);
                        msg.what = 0;

                        isCanFinish = "YES";
                    is.close();
                    } catch (IOException e) {
                        msg.what = 1;
                        isCanFinish = "YES";
                        e.printStackTrace();
                }
//                    loadingUtils.close();
                    mHandler.sendMessage(msg);
            }
        }.start();
    }

//    private Bitmap comp(Bitmap image) {
//
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//        if( baos.toByteArray().length / 1024>1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
//            baos.reset();//重置baos即清空baos
//            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//这里压缩50%，把压缩后的数据存放到baos中
//        }
//        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
//        BitmapFactory.Options newOpts = new BitmapFactory.Options();
//        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
//        newOpts.inJustDecodeBounds = true;
//        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
//        newOpts.inJustDecodeBounds = false;
//        int w = newOpts.outWidth;
//        int h = newOpts.outHeight;
//        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
//        float hh = 800f;//这里设置高度为800f
//        float ww = 480f;//这里设置宽度为480f
//        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
//        int be = 1;//be=1表示不缩放
//        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
//            be = (int) (newOpts.outWidth / ww);
//        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
//            be = (int) (newOpts.outHeight / hh);
//        }
//        if (be <= 0)
//            be = 1;
//        newOpts.inSampleSize = be;//设置缩放比例
//        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
//        isBm = new ByteArrayInputStream(baos.toByteArray());
//        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
//        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
//    }
//
//    private Bitmap compressImage(Bitmap image) {
//
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
//        int options = 100;
//        while ( baos.toByteArray().length / 1024>100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
//            baos.reset();//重置baos即清空baos
//            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
//            options -= 10;//每次都减少10
//        }
//        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
//        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
//        return bitmap;
//    }


    @Override
    protected void onDestroy() {
        if(bitmap!=null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }

        super.onDestroy();
    }
}
