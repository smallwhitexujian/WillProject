package socket.lib.protocol;


import socket.lib.util.ByteUtil;

import net.dev.mylib.DebugLogs;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/8/21 0021.
 * 协议 拼接包
 */
public class WillProtocol extends Protocol {
    public static final String TAG = "WillProtocol";
    public static final String KEY_PROTOCOL_LEN = TAG + "_len";
    public static final String KEY_PROTOCOL_TYPE = TAG + "_type";
    public static final String KEY_PROTOCOL_DATA = TAG + "_data";

    public static final int PACK_LEN = 4;
    public static final int PACK_TYPE_LEN = 4;


    /**
     * 解包 返回包解析
     * @param pack
     * @return
     */
    public Map<String, byte[]> parseWillPackage(byte[] pack) {
        int pack_data_len;
        //四字节长度+四字节操作码
        if (pack == null || pack.length < PACK_LEN + PACK_TYPE_LEN) {
            return null;
        }
        Map<String, byte[]> protocolMap = new HashMap<String, byte[]>();
        byte[] lenBuffer = new byte[PACK_LEN];
        System.arraycopy(pack, 0, lenBuffer, 0, PACK_LEN);
        int dataLen = ByteUtil.bytes2Int(lenBuffer, 0, ByteUtil.BIG_ENDIAN);
        protocolMap.put(KEY_PROTOCOL_LEN, lenBuffer);

        byte[] typeBuffer = new byte[PACK_TYPE_LEN];
        System.arraycopy(pack, PACK_LEN, typeBuffer, 0, PACK_TYPE_LEN);
        protocolMap.put(KEY_PROTOCOL_TYPE, typeBuffer);

        byte[] dataBuffer = new byte[dataLen];
        System.arraycopy(pack, PACK_LEN + PACK_TYPE_LEN, dataBuffer, 0, dataLen - PACK_LEN -PACK_TYPE_LEN);
        protocolMap.put(KEY_PROTOCOL_DATA, dataBuffer);
        return protocolMap;
    }

    /**
     * 获取数据包长
     * @param pack
     * @return
     */
    @Override
    public int getDataLen(byte[] pack) {
        //四字节长度+四字节操作码
        int packLen = PACK_LEN;
        int packTypeLen = PACK_TYPE_LEN;
        if (pack == null || pack.length < packLen + packTypeLen) {
            return -1;
        }
        byte[] lenBuffer = new byte[packLen];
        System.arraycopy(pack, 0, lenBuffer, 0, packLen);
        int dataLen = ByteUtil.bytes2Int(lenBuffer, 0, ByteUtil.BIG_ENDIAN);
        return dataLen;
    }

    /**
     * 获得数据源
     * @param pack
     * @return
     */
    @Override
    public byte[] getData(byte[] pack) {
        Map<String, byte[]> packMap = parseWillPackage(pack);
        return packMap.get(KEY_PROTOCOL_DATA);
    }

    /**
     * 操作码 状态码
     * @param pack
     * @return
     */
    @Override
    public int getType(byte[] pack) {
        Map<String, byte[]> packMap = parseWillPackage(pack);
        byte[] typeAry = packMap.get(KEY_PROTOCOL_TYPE);
        return ByteUtil.bytes2Int(typeAry, 0, ByteUtil.BIG_ENDIAN);
    }

    /**
     * 包头长度
     * @return
     */
    @Override
    public int getHeadLen() {
        return PACK_LEN+PACK_TYPE_LEN;
    }

    //login parcel
    public static byte[] loginParcel(String jsonStr) {
        int typeValue = 2001;
        int dataLen = PACK_LEN+PACK_TYPE_LEN+jsonStr.getBytes().length;
        byte[] pack = parcel(dataLen, typeValue, jsonStr);
        DebugLogs.i(ByteUtil.bytes2Hex(pack));
        return pack;
    }


    //心跳包
    public static byte[] beatheart() {
        int typeValue = 100;
        byte[] pack = parcel(PACK_LEN+PACK_TYPE_LEN, typeValue, null);
        DebugLogs.i(ByteUtil.bytes2Hex(pack));
        return pack;
    }

    //發送廣播
    public static byte[] broadcast(String msg) {
        int typeValue = 2102;
        JSONObject jsonObj = new JSONObject();
        byte[] pack = null;
        try {
            jsonObj.put("msg", msg);
            String str = jsonObj.toString();
            pack = parcel(PACK_LEN + PACK_TYPE_LEN + str.getBytes().length, typeValue, str);
            DebugLogs.i(ByteUtil.bytes2Hex(pack));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return pack;
    }


    /**
     * 拼包方法
     * @param packLen   包长
     * @param typeValue 操作码
     * @param jsonStr   json数据
     * @return
     */
    private static byte[] parcel(int packLen, int typeValue, String jsonStr) {
        byte[] parcelAry = new byte[packLen];
        byte[] packLenAry = ByteUtil.INT32_2_INT8(packLen, ByteUtil.BIG_ENDIAN);
        byte[] typeAry = ByteUtil.INT32_2_INT8(typeValue, ByteUtil.BIG_ENDIAN);
        System.arraycopy(packLenAry, 0, parcelAry, 0, PACK_LEN);
        System.arraycopy(typeAry, 0, parcelAry, PACK_LEN, PACK_TYPE_LEN);
        if (jsonStr != null) {
            System.arraycopy(jsonStr.getBytes(), 0, parcelAry, PACK_LEN+PACK_TYPE_LEN, jsonStr.getBytes().length);
        }
        return parcelAry;
    }

    /***-------------------------房间-----------------------------**/
    //login parcel
    public static byte[] RoomloginParcel(String jsonStr) {
        int typeValue = 110;
        int dataLen = PACK_LEN+PACK_TYPE_LEN+jsonStr.getBytes().length;
        byte[] pack = parcel(dataLen, typeValue, jsonStr);
        return pack;
    }

    public static byte[] sendText(String jsonstr){
        int typeValue = 10003;
        byte[] pack;
        DebugLogs.e("------->发送聊天消息");
        pack = parcel(PACK_LEN + PACK_TYPE_LEN + jsonstr.getBytes().length, typeValue, jsonstr);
        return pack;
    }
}