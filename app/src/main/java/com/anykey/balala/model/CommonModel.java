package com.anykey.balala.model;

import java.io.Serializable;

/**
 * Created by xujian on 15/8/26.
 */
public class CommonModel implements Serializable  {
    public String code="-1";     //状态码
    public String message;  //错误消息
    public String msg;      //消息
    public long time;       //时间
    public int index;//页码
    public long mycoin;      //我的金币
    public String bartype;  //升级bar的类型
    public String pagecount;//分页的总数

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CommonModel that = (CommonModel) o;

        return !(code != null ? !code.equals(that.code) : that.code != null);

    }

    @Override
    public int hashCode() {
        return code != null ? code.hashCode() : -1;
    }

    @Override
    public String toString() {
        return "CommonModel{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
