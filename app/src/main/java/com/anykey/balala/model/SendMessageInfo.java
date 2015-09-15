package com.anykey.balala.model;

import java.io.Serializable;

/**
 * Created by xujian on 15/9/10.
 * 发送聊天格式
 */
public class SendMessageInfo implements Serializable{
    public int code ;
    public FromUser From ;
    public String Message ;
    public static class FromUser{
        public String Uid ;
        public String Name ;
    }
}
