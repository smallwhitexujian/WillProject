package com.anykey.balala;

/**
 * Created by xujian on 15/9/10.
 * 常量状态码
 */
public class GlobalDef {
    public static final int SERVICE_STATUS_SUCCESS = 0;


    public static final int SO_DOHEART = 100;                 //心跳
    public static final int WM_ROOM_LOGIN_SUCCESS =    10000;         //房间登陆成功
    public static final int WM_ROOM_SEND_MESSAGE =     10001;         //房间发送消息
    public static final int WM_ROOM_RECEIVE_MESSAGE =  10003;         //房间接受消息
}
