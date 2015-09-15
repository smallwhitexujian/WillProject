package com.anykey.balala;

/**
 * Created by xujian on 15/8/26.
 * 接口地址配置文件
 */
public class CommonUrlConfig  {
    private static boolean isDebug = AppBalala.isDebug;
    /**
     * 接口返回状态码
     */
    public interface RequestState {
        String OK = "1000";
        String ERR = "1008";
    }




    public static String TEST_HOST_URL = "http://192.168.199.8:801/";
    public static String HOST_URL =  "http://192.168.199.8:801/";

    public static final String PHOTO_TEMP_NAME = "/balala/img/balabalaphotocache.jpg";

    public static String phoneRegister =  (isDebug ? TEST_HOST_URL : HOST_URL ) + "webServers/User/UserSmRegister";
    public static String getPhoneCode = (isDebug ? TEST_HOST_URL : HOST_URL ) +  "webServers/User/GetPhoneCode"; //获取手机验证码

    public static String DynamicLssue = (isDebug ? TEST_HOST_URL : HOST_URL ) +  "Webservers/Find/DynamicLssue"; //发布动态

    public static String DiscoverySquare = (isDebug ? TEST_HOST_URL : HOST_URL ) + "Webservers/Find/DiscoverySquare"; //动态列表

    /***登陆注册接口***/
    public static String facebookRegister = (isDebug ? TEST_HOST_URL : HOST_URL ) + "webServers/User/FbRegister";
    public static String emailRegister = (isDebug ? TEST_HOST_URL : HOST_URL ) + "webServers/User/UserEmRegister";

    //用户注册信息修改接口
    public static String userRegInfoFill = (isDebug ? TEST_HOST_URL : HOST_URL ) + "WebServers/User/UserRegInfoFill";


    public static String userUploadPhotos = (isDebug ? TEST_HOST_URL : HOST_URL ) + "Webservers/User/UserUploadPhotos";









}
