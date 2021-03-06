package com.tongxin.info.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tongxin.info.R;

/**
 * Created by Administrator on 2015/11/18.
 */
public class ToastUtils {
    static int xoffset = 0;
    static int yoffset = 200;
    static int color = Color.rgb(255,0,0);
    public static void Show(Context context,String msg)
    {
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, xoffset, yoffset);
        View view = toast.getView();
        view.setBackgroundColor(color);
        toast.show();
    }
    public static void ShowLong(Context context,String msg)
    {
        Toast toast = Toast.makeText(context,msg,Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP,xoffset,yoffset);
        View view = toast.getView();
        view.setBackgroundColor(color);
        toast.show();
    }

    public static void ShowForCopy(Context context,String msg){
        Toast toast = new Toast(context);
//        Toast toast = Toast.makeText(context, null, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, xoffset, yoffset);
//        View view = toast.getView();
        View view = View.inflate(context, R.layout.showmsgcopy, null);
        ((TextView)view.findViewById(R.id.tv_ForCopyMsg)).setText(msg);
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_SHORT);
        view.setBackgroundColor(Color.parseColor("#006400"));
        toast.show();
    }
}
