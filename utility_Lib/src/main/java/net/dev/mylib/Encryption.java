package net.dev.mylib;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;

import net.dev.mylib.DebugLogs;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by xujian on 15/8/28.
 * 加密解密
 */
public class Encryption {
    private static final String KEY_Secret ="自己特殊的前面key";
    /**
     *  32位 加密方式，包括中文，
     * @param str
     * @return
     */
    public static String MD5(String str) {
        byte[] hash = null;
        try {
            hash = MessageDigest.getInstance("MD5").digest(str.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

    /**
     * 获取hash值
     * @param context 上下文
     * @param pkgName 报名
     */
    public static void getKeyHash(Context context,String pkgName) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(pkgName, PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                DebugLogs.e("KeyHash:" + Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            //something
        } catch (NoSuchAlgorithmException e) {
            //something
        }
    }
    /**
     * 获得Hash码
     *
     * @param fileName
     * @param hashType
     * @return
     * @throws Exception
     */
    public static String getHash(String fileName, String hashType) throws Exception {
        InputStream fis;
        fis = new FileInputStream(fileName);
        byte[] buffer = new byte[1024];
        MessageDigest md5 = MessageDigest.getInstance(hashType);
        int numRead = 0;
        while ((numRead = fis.read(buffer)) > 0) {
            md5.update(buffer, 0, numRead);
        }
        fis.close();
        return toHexString(md5.digest());
    }

    private static String toHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            sb.append(hexChar[(b[i] & 0xf0) >>> 4]);
            sb.append(hexChar[b[i] & 0x0f]);
        }
        return sb.toString();
    }

    private static char[] hexChar = { '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    /**
     * hashMap(key)安字母顺序排序 a,b,c...
     */
    public static List<Map.Entry<String, String>> getSortHashMap(Map<String, String> map) {
        List<Map.Entry<String, String>> infoIds =new ArrayList<Map.Entry<String, String>>(map.entrySet());//排序前
        Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {
            public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                return (o1.getKey()).toString().compareTo(o2.getKey());
            }
        });
        return infoIds;
    }

    /**
     * MD5加密方法(signid = MD5（（参数+MD5+appid）排序）+secret)
     */
    public static String getMD5Result(Map<String, String> map){
        List<Map.Entry<String, String>> infoIds =new ArrayList<Map.Entry<String, String>>(map.entrySet());//排序前

        Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {
            public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                return (o1.getKey()).toString().compareTo(o2.getKey());
            }
        });
        String getEntry = "";
        for (int i = 0; i < infoIds.size(); i++) {
            getEntry = getEntry+infoIds.get(i).getKey()+infoIds.get(i).getValue();
        }
        DebugLogs.e(getEntry);
        String sign =MD5(getEntry + KEY_Secret) ;

        return sign;
    }

}
