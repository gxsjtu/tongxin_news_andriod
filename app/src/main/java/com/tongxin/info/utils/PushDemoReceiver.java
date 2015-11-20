package com.tongxin.info.utils;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;
import com.tongxin.info.R;
import com.tongxin.info.activity.MainActivity;
import com.tongxin.info.domain.MyLifecycleHandler;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2015/11/2.
 */
public class PushDemoReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        //int recivebadge = Integer.parseInt(SharedPreUtils.getString(context, "badgecount","0"));
        switch (bundle.getInt(PushConsts.CMD_ACTION)) {
            case PushConsts.GET_MSG_DATA:
                // 获取透传数据
                // String appid = bundle.getString("appid");
                byte[] payload = bundle.getByteArray("payload");

                String taskid = bundle.getString("taskid");
                String messageid = bundle.getString("messageid");

                // smartPush第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
                boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);

                if (payload != null) {

                    String data = new String(payload);
                    String msg="";
                    String exit="";
                    int sound = 0;
                    int badge = 0;
                    try {
                        JSONObject jsonObject = new JSONObject(data);
                        msg = jsonObject.getString("msg");
                        badge = jsonObject.getInt("badge");
                        exit = jsonObject.getString("exit");
                        sound = jsonObject.getInt("sound");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (exit.equals("退出")) {
                        SharedPreUtils.setBoolean(context, "mustLogin", true);
                    } else {
                        if(badge == 0)
                            badge++;

                        if (badge > 1) {
                            //多条
                            msg = "您有" + badge + "条未读消息";
                        }


                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);

                        Intent intent2 = new Intent(context, MainActivity.class);
                        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent2, 0);
                        mBuilder.setContentTitle("同鑫资讯")//设置通知栏标题
                                .setContentText(msg) //设置通知栏显示内容
                                .setContentIntent(pendingIntent) //设置通知栏点击意图
                                .setTicker("同鑫资讯") //通知首次出现在通知栏，带上升动画效果的
                                .setWhen(System.currentTimeMillis())
                                .setPriority(Notification.PRIORITY_DEFAULT)
                                .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                                .setOngoing(false)//ture，
                                .setSmallIcon(R.drawable.push);//设置通知小ICON

                            if(sound == 1)
                            {
                                mBuilder.setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND);
                            }

                        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        Notification notify = mBuilder.build();
                        notify.flags = Notification.FLAG_AUTO_CANCEL;

                        if(isBackGroundRunning()) {
                            BadgeUtils.setBadgeCount(context, badge,msg,mNotificationManager,notify);
                        }


                        SharedPreUtils.setString(context, "badgecount", String.valueOf(badge));
                        Intent intentCount = new Intent("com.tongxin.badge");
                        intentCount.putExtra("count", badge);
                        context.sendBroadcast(intentCount);
                    }
                }
                break;

            case PushConsts.GET_CLIENTID:
                // 获取ClientID(CID)
                // 第三方应用需要将CID上传到第三方服务器，并且将当前用户帐号和CID进行关联，以便日后通过用户帐号查找CID进行消息推送
                String cid = bundle.getString("clientid");
                break;

            case PushConsts.THIRDPART_FEEDBACK:
                /*
                 * String appid = bundle.getString("appid"); String taskid =
                 * bundle.getString("taskid"); String actionid = bundle.getString("actionid");
                 * String result = bundle.getString("result"); long timestamp =
                 * bundle.getLong("timestamp");
                 *
                 * Log.d("GetuiSdkDemo", "appid = " + appid); Log.d("GetuiSdkDemo", "taskid = " +
                 * taskid); Log.d("GetuiSdkDemo", "actionid = " + actionid); Log.d("GetuiSdkDemo",
                 * "result = " + result); Log.d("GetuiSdkDemo", "timestamp = " + timestamp);
                 */
                break;

            default:
                break;
        }
    }

    public boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    public boolean isBackGroundRunning() {
        return !MyLifecycleHandler.isApplicationInForeground();
    }
}
