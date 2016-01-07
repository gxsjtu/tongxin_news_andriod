package com.tongxin.info.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.tongxin.info.domain.MyApp;

import java.io.ByteArrayOutputStream;

/**
 * Created by Administrator on 2015/10/30.
 */
public class UserUtils {
    public static String Tel;
    private Activity mActivity;
    public UserUtils(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public void setTel(String tel)
    {
        MyApp myApp = (MyApp) mActivity.getApplication();
        myApp.setTel(tel);
    }

    public String getTel()
    {
        MyApp myApp = (MyApp) mActivity.getApplication();
        return myApp.getTel();
    }

    public void setPwd(String pwd)
    {
        MyApp myApp = (MyApp) mActivity.getApplication();
        myApp.setPwd(pwd);
    }

    public String getPwd()
    {
        MyApp myApp = (MyApp) mActivity.getApplication();
        return myApp.getPwd();
    }

    public void setClientId(String clientId)
    {
        MyApp myApp = (MyApp) mActivity.getApplication();
        myApp.setClientId(clientId);
    }

    public String getClientId()
    {
        MyApp myApp = (MyApp) mActivity.getApplication();
        return myApp.getClientId();
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static byte[] bmpToByteArray1(final Bitmap bmp, final boolean needRecycle) {

        int i;
        int j;
        if (bmp.getHeight() > bmp.getWidth()) {
            i = bmp.getWidth();
            j = bmp.getWidth();
        } else {
            i = bmp.getHeight();
            j = bmp.getHeight();
        }

        Bitmap localBitmap = Bitmap.createBitmap(i, j, Bitmap.Config.RGB_565);
        Canvas localCanvas = new Canvas(localBitmap);

        while (true) {
            localCanvas.drawBitmap(bmp, new Rect(0, 0, i, j), new Rect(0, 0,i, j), null);
            if (needRecycle)
                bmp.recycle();
            ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
            localBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                    localByteArrayOutputStream);
            localBitmap.recycle();
            byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
            try {
                localByteArrayOutputStream.close();
                return arrayOfByte;
            } catch (Exception e) {
                //F.out(e);
            }
            i = bmp.getHeight();
            j = bmp.getHeight();
        }
    }

//    public boolean isUserValid()
//    {
//        String
//    }
}
