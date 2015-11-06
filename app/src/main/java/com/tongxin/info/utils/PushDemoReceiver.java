package com.tongxin.info.utils;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;
import com.tongxin.info.activity.LoginActivity;
import com.tongxin.info.domain.MyApp;

import java.util.List;

/**
 * Created by Administrator on 2015/11/2.
 */
public class PushDemoReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();

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
                    if(data.equals("退出")) {
                        SharedPreUtils.setBoolean(context,"mustLogin",true);
//                        Toast.makeText(context,"用户登录信息已失效",Toast.LENGTH_SHORT).show();
//                        Intent intent1 = new Intent(context, LoginActivity.class);
//                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        context.startActivity(intent1);
//                        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//                        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
//                        if (!tasks.isEmpty()) {
//                            ComponentName topActivity = tasks.get(0).topActivity;
//                            if (topActivity.getPackageName().equals(context.getPackageName())) {
//                                SharedPreUtils.setBoolean(context,"mustLogin",true);
//                                //在前台运行着
////                                Toast.makeText(context,"用户登录信息已失效",Toast.LENGTH_SHORT).show();
////                                Intent intent1 = new Intent(context, LoginActivity.class);
////                                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                                context.startActivity(intent1);
//                            }
//                            else
//                            {
//                                //在后台或未启动
//                                SharedPreUtils.setBoolean(context,"mustLogin",true);
//                            }
//                        }

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
}
