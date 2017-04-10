package com.wenld.servicebinder;

import android.app.Notification;
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
 * Author: wenld on 2017/4/10 11:50.
 * blog: http://www.jianshu.com/u/99f514ea81b3
 * github: https://github.com/LidongWen
 * 守护进程
 */

public class GuardService extends Service {
    private int GuardId = 1;
    GuardSerViceAIDL guardSerViceAIDL;

    @Override
    public void onCreate() {
        super.onCreate();
        if (guardSerViceAIDL == null) {
            guardSerViceAIDL = new GuardSerViceAIDL();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new ProcessConnection.Stub() {
            @Override
            public String getUserName() throws RemoteException {
                return "GuardService";
            }

            @Override
            public String getUserPassword() throws RemoteException {
                return null;
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //提高优先级
        startForeground(GuardId, new Notification());

        GuardService.this.bindService(new Intent(GuardService.this, MessageService.class),
                mServiceConn, Context.BIND_IMPORTANT);
        return Service.START_STICKY;
    }


    private class GuardSerViceAIDL extends ProcessConnection.Stub {

        @Override
        public String getUserName() throws RemoteException {
            return "GuardService";
        }

        @Override
        public String getUserPassword() throws RemoteException {
            return null;
        }
    }

    private ServiceConnection mServiceConn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                // 与远程服务通信
                guardSerViceAIDL.asInterface(service);
                Toast.makeText(GuardService.this, "连接" + guardSerViceAIDL.getUserName() + "服务成功", Toast.LENGTH_LONG).show();

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //断开连接

            Intent guardIntent = new Intent(GuardService.this, MessageService.class);
            // 发现断开我就从新启动和绑定
            startService(guardIntent);
            GuardService.this.bindService(guardIntent,
                    mServiceConn, Context.BIND_IMPORTANT);
        }
    };
}
