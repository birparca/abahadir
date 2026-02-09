package com.autoplay.ftpserver.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.autoplay.ftpserver.server.FtpServerConfig;
import com.autoplay.ftpserver.server.FtpServerController;

public class FtpServerService extends Service {
    public static final String ACTION_START = "com.autoplay.ftpserver.action.START";
    public static final String ACTION_STOP = "com.autoplay.ftpserver.action.STOP";
    private static final String CHANNEL_ID = "ftp_server_channel";
    private static final int NOTIFICATION_ID = 2121;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent != null ? intent.getAction() : null;
        if (ACTION_STOP.equals(action)) {
            stopServer();
            stopSelf();
            return START_NOT_STICKY;
        }
        startForeground(NOTIFICATION_ID, buildNotification());
        startServer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        stopServer();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startServer() {
        FtpServerController.getInstance().start(getApplicationContext());
    }

    private void stopServer() {
        FtpServerController.getInstance().stop();
    }

    private Notification buildNotification() {
        String contentText = "FTP sunucu " + FtpServerConfig.PORT + " portunda çalışıyor.";
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Autoplay FTP Server")
                .setContentText(contentText)
                .setSmallIcon(android.R.drawable.stat_sys_upload)
                .setOngoing(true)
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "FTP Server",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}
