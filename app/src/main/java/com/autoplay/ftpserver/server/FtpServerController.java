package com.autoplay.ftpserver.server;

import android.content.Context;
import android.util.Log;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.ClearTextPasswordEncryptor;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;

import java.io.File;

public final class FtpServerController {
    private static final String TAG = "FtpServerController";
    private static FtpServerController instance;
    private FtpServer server;

    private FtpServerController() {
    }

    public static synchronized FtpServerController getInstance() {
        if (instance == null) {
            instance = new FtpServerController();
        }
        return instance;
    }

    public synchronized void start(Context context) {
        if (server != null && !server.isStopped()) {
            return;
        }
        try {
            FtpServerFactory serverFactory = new FtpServerFactory();
            ListenerFactory listenerFactory = new ListenerFactory();
            listenerFactory.setPort(FtpServerConfig.PORT);
            serverFactory.addListener("default", listenerFactory.createListener());

            PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
            userManagerFactory.setPasswordEncryptor(new ClearTextPasswordEncryptor());

            File homeDir = new File(context.getExternalFilesDir(null), "ftp");
            if (!homeDir.exists() && !homeDir.mkdirs()) {
                Log.w(TAG, "Failed to create FTP home directory: " + homeDir.getAbsolutePath());
            }

            BaseUser user = new BaseUser();
            user.setName(FtpServerConfig.USERNAME);
            user.setPassword(FtpServerConfig.PASSWORD);
            user.setHomeDirectory(homeDir.getAbsolutePath());
            user.setMaxIdleTime(0);
            userManagerFactory.setFile(new File(context.getFilesDir(), "users.properties"));
            serverFactory.setUserManager(userManagerFactory.createUserManager());
            serverFactory.getUserManager().save(user);

            serverFactory.getConnectionConfig().setMaxLogins(FtpServerConfig.MAX_LOGINS);
            serverFactory.getConnectionConfig().setMaxLoginsPerIp(FtpServerConfig.MAX_LOGINS_PER_IP);

            server = serverFactory.createServer();
            server.start();
            Log.i(TAG, "FTP server started on port " + FtpServerConfig.PORT);
        } catch (Exception e) {
            Log.e(TAG, "Failed to start FTP server", e);
        }
    }

    public synchronized void stop() {
        if (server == null) {
            return;
        }
        try {
            server.stop();
            Log.i(TAG, "FTP server stopped");
        } catch (Exception e) {
            Log.e(TAG, "Failed to stop FTP server", e);
        } finally {
            server = null;
        }
    }

    public synchronized boolean isRunning() {
        return server != null && !server.isStopped();
    }
}
