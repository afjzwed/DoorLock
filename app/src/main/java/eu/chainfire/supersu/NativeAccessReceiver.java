package eu.chainfire.supersu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.cxwl.hurry.doorlock.ui.activity.MainActivity;
import com.cxwl.hurry.doorlock.utils.ToastUtil;


/**
 * 开机(更新)启动广播监听器
 * Created by William on 2018/4/24.
 */

public class NativeAccessReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_PACKAGE_REPLACED) || action.equals(Intent
                .ACTION_PACKAGE_ADDED)) {
            String packageName = intent.getData().getSchemeSpecificPart();
            if (packageName.equals("com.cxwl.hurry.doorlock")) {
                startActivity(context, MainActivity.class, null);
            }
        } else if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            startActivity(context, MainActivity.class,null);
            ToastUtil.showShort("开机启动成功");
        }

//        if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
//            String packageName = intent.getData().getSchemeSpecificPart();
//            Toast.makeText(context, "安装成功" + packageName, Toast.LENGTH_LONG).show();
//        }
//        if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
//            String packageName = intent.getData().getSchemeSpecificPart();
//            Toast.makeText(context, "卸载成功" + packageName, Toast.LENGTH_LONG).show();
//        }
//        if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
//            String packageName = intent.getData().getSchemeSpecificPart();
//            Toast.makeText(context, "替换成功" + packageName, Toast.LENGTH_LONG).show();
//        }
    }


    public void startActivity(Context context,Class<?> clz, Bundle bundle) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(context, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        context.startActivity(intent);
    }
}
