package socket.lib.protocol;

/**
 * Created by jjfly on 15-8-31.
 * 协议接口。 长度 数据 处理码
 */
public abstract class Protocol {
    public int getDataLen(byte[] pack){
        return 0;
    }
    //返回协议头长度
    public int getHeadLen(){
        return 0;
    }
    //数据
    public byte[] getData(byte[] pack){
        return  null;
    }
    //状态码
    public int getType(byte[] pack){
        return 0;
    }
    //心跳包
    public byte[]  heartbeatParcel(){
        return null;
    }

}
