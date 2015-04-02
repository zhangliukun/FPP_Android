package com.zale.data;

public class SettingsData {
    // public static final String SERVER_IP = "58.210.161.122";
    public static volatile String SERVER_IP      = "";
    // public static final String SERVER_IP = "192.168.1.113";
    // public static final String SERVER_IP = "58.210.161.123";
    public static volatile int    SERVER_PORT    = 0;

    public static final String    AUTHENTICATION = "#0";
    public static final String    INITDATA       = "#10";
    public static final String    NEWDATA        = "#11";
    public static final String    OLDDATA        = "#12";
    public static final String    PROBLEM_SOLVED = "#2";
    public static final String    COMMUNITY      = "#3";

    // 故障处理状态
    public static final int       UNSOLVED       = 1;    // 未解决
    public static final int       SOLVED         = 3;    // 已解决
    public static final int       UNSENDTOSERVER = 4;    // 未发送至服务器的
}
