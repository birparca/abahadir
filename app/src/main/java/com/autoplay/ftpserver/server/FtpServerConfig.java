package com.autoplay.ftpserver.server;

public final class FtpServerConfig {
    public static final int PORT = 2121;
    public static final String USERNAME = "ftpuser";
    public static final String PASSWORD = "ftpuser";
    public static final int MAX_LOGINS = 10;
    public static final int MAX_LOGINS_PER_IP = 3;

    private FtpServerConfig() {
    }
}
