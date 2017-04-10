package com.wenld.servicebinder;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.widget.Toast;

/**
 * <p/>
 * Author: wenld on 2017/4/10 11:26.
 * blog: http://www.jianshu.com/u/99f514ea81b3
 * github: https://github.com/LidongWen
 */

public class MessageService extends Service {

    private static final String TAG = "MessageService";

    private MessageServiceConnection mServiceConnection;
    private MessageBind mMessageBind;

    private int MessageId = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        if (mServiceConnection == null) {
            mServiceConnection = new MessageServiceConnection();
        }
//
        if (mMessageBind == null) {
            mMessageBind = new MessageBind();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //提高优先级
//        startForeground(MessageId, new Notification());

        bindService(new Intent(MessageService.this, GuardService.class),
                mServiceConnection, Context.BIND_IMPORTANT);
        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMessageBind;
    }

    private class MessageBind extends ProcessConnection.Stub {

        @Override
        public String getUserName() throws RemoteException {
            return "MessageService";
        }

        @Override
        public String getUserPassword() throws RemoteException {
            return null;
        }
    }

    private class MessageServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // 建立连接
            Toast.makeText(MessageService.this, "建立连接", Toast.LENGTH_LONG).show();
            try {
                // 与远程服务通信
                mMessageBind.asInterface(service);
                Toast.makeText(MessageService.this, "连接" + mMessageBind.getUserName() + "服务成功", Toast.LENGTH_LONG).show();

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // 断开连接
            Toast.makeText(MessageService.this, "断开连接", Toast.LENGTH_LONG).show();

            Intent guardIntent = new Intent(MessageService.this, GuardService.class);
            // 发现断开我就从新启动和绑定
            startService(guardIntent);
            MessageService.this.bindService(guardIntent,
                    mServiceConnection, Context.BIND_IMPORTANT);
        }
    }
}
