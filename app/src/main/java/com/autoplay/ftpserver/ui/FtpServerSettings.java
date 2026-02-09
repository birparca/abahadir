package com.autoplay.ftpserver.ui;

import android.content.Context;
import android.content.SharedPreferences;

import com.autoplay.ftpserver.server.FtpServerConfig;

public final class FtpServerSettings {
    private static final String PREFS_NAME = "ftp_server_prefs";
    private static final String KEY_PORT = "port";

    private FtpServerSettings() {
    }

    public static int getPort(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_PORT, FtpServerConfig.DEFAULT_PORT);
    }

    public static void setPort(Context context, int port) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putInt(KEY_PORT, port).apply();
    }
}
