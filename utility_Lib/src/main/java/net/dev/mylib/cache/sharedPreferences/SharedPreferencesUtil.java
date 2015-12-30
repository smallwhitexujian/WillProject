package net.dev.mylib.cache.sharedPreferences;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import net.dev.mylib.DebugLogs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

/**
 * @author xujian
 *         SharedPreferencesUtil
 */
public class SharedPreferencesUtil {
    private static final String SHAREDPREFERENCE_NAME = "sharedpreferences_xj";

    private static SharedPreferencesUtil mInstance;

    private static SharedPreferences mSharedPreferences;

    private static SharedPreferences.Editor mEditor;

    public synchronized static SharedPreferencesUtil getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPreferencesUtil(context);
        }
        return mInstance;
    }

    private SharedPreferencesUtil(Context context) {
        mSharedPreferences = context.getSharedPreferences(SHAREDPREFERENCE_NAME, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    public synchronized boolean putString(String key, String value) {
        mEditor.putString(key, value);
        return mEditor.commit();
    }

    public synchronized boolean putStringArrayList(String key, ArrayList<String> value) {
        for (int j = 0; j < value.size() - 1; j++) {
            if (value.get(value.size() - 1).equals(value.get(j))) {
                value.remove(j);
            }
        }
        mEditor.putInt("Size", value.size());

        if (value.size() == 4) {
            mEditor.putString(key + 0, value.get(3));
            mEditor.putString(key + 1, value.get(0));
            mEditor.putString(key + 2, value.get(1));
        } else if (value.size() == 3) {
            mEditor.putString(key + 0, value.get(2));
            mEditor.putString(key + 1, value.get(0));
            mEditor.putString(key + 2, value.get(1));
        } else {
            for (int i = 0; i < value.size(); i++) {
                mEditor.putString(key + i, value.get(value.size() - 1 - i));
            }

        }
        return mEditor.commit();
    }

    public synchronized boolean putInt(String key, int value) {
        mEditor.putInt(key, value);
        return mEditor.commit();
    }

    public synchronized boolean putLong(String key, long value) {
        mEditor.putLong(key, value);
        return mEditor.commit();
    }

    public synchronized boolean putFloat(String key, float value) {
        mEditor.putFloat(key, value);
        return mEditor.commit();
    }

    public synchronized boolean putBoolean(String key, boolean value) {
        mEditor.putBoolean(key, value);
        return mEditor.commit();
    }

    @SuppressLint("NewApi")
    public synchronized boolean putStringSet(String key, Set<String> value) {
        mEditor.putStringSet(key, value);
        return mEditor.commit();
    }

    public String getString(String key, String value) {
        return mSharedPreferences.getString(key, value);
    }

    public ArrayList<String> getStringArrayList(String key, int size) {
        ArrayList<String> al = new ArrayList<String>();
        int loop;
        if (size > 4)
            loop = 4;
        else
            loop = size;
        for (int i = 0; i < loop; i++) {
            String name = mSharedPreferences.getString(key + i, null);
            al.add(name);
        }
        return al;
    }

    public int getInt(String key, int value) {
        return mSharedPreferences.getInt(key, value);
    }

    public long getLong(String key, long value) {
        return mSharedPreferences.getLong(key, value);
    }

    public float getFloat(String key, float value) {
        return mSharedPreferences.getFloat(key, value);
    }

    public boolean getBoolean(String key, boolean value) {
        return mSharedPreferences.getBoolean(key, value);
    }

    @SuppressLint("NewApi")
    public Set<String> getStringSet(String key, Set<String> value) {
        return mSharedPreferences.getStringSet(key, value);
    }

    public boolean remove(String key) {
        mEditor.remove(key);
        return mEditor.commit();
    }

    private static final String PREFERENCES_AUTO_LOGIN = "UserAutoLogin";
    private static final String PREFERENCES_USER_NAME = "UserName";
    private static final String PREFERENCES_USER_TOKEN = "Token";
    private static final String PREFERENCES_USER_PASSWORD = "UserPassword";
    private static final String PREFERENCES_USER_ID = "UserId";
    private static final String PREFERENCES_USER_HEAD = "UserHead";
    private static final String PREFERENCES_USER_LEVEL = "UserLevel";
    private static final String PREFERENCES_UP_APK_INFO_isUp = "isUp";
    private static final String PREFERENCES_UP_APK_INFO_VERSION= "version";
    private static final String PREFERENCES_UP_APK_INFO_MESSAGE= "message";
    private static final String PREFERENCES_UP_APK_INFO_URL= "upLoadApkUrl";
    private static final String PREFERENCES_UP_APK_INFO_CANCEL= "cancel";
    private static final String PREFERENCES_ACCOUT_NUM = "AccoutNum";//统计

    public void isCancel(boolean isCancel){
        mEditor.putBoolean(PREFERENCES_UP_APK_INFO_CANCEL, isCancel);
        mEditor.commit();
    }

    public boolean getIsCancel() {
        return mSharedPreferences.getBoolean(PREFERENCES_UP_APK_INFO_CANCEL, false);
    }

    public boolean isAutoLogin() {
        return mSharedPreferences.getBoolean(PREFERENCES_AUTO_LOGIN, false);
    }

    public String getUserName() {
        return mSharedPreferences.getString(PREFERENCES_USER_NAME, "");
    }

    public String getUserHead() {
        return mSharedPreferences.getString(PREFERENCES_USER_HEAD, "");
    }

   
    public void saveUserName(String username) {
        mEditor.putString(PREFERENCES_USER_NAME, username);
        mEditor.commit();
    }

    public void saveUserHead(String head) {
        mEditor.putString(PREFERENCES_USER_HEAD, head);
        mEditor.commit();
    }

    public String getUserPwd() {
        return mSharedPreferences.getString(PREFERENCES_USER_PASSWORD, "");
    }

    public String getUserId() {
        return mSharedPreferences.getString(PREFERENCES_USER_ID, "");
    }

    public String getToken() {
        return mSharedPreferences.getString(PREFERENCES_USER_TOKEN, "");
    }

    public String getUserLevel() {
        return mSharedPreferences.getString(PREFERENCES_USER_LEVEL, "");
    }

    public boolean getApkInfo_isUp(){//是否强制升级
        return  mSharedPreferences.getBoolean(PREFERENCES_UP_APK_INFO_isUp, false);
    }

    public String getApkInfo_message(){//升级内容
        return  mSharedPreferences.getString(PREFERENCES_UP_APK_INFO_MESSAGE, "");
    }

    public String getApkInfo_version(){//升级version
        return  mSharedPreferences.getString(PREFERENCES_UP_APK_INFO_VERSION,"");
    }

    public String getApkInfo_Url(){//升级地址
        return  mSharedPreferences.getString(PREFERENCES_UP_APK_INFO_URL,"");
    }

    public void saveLoginInfo(Boolean autoLogin, String Token, String userId, String name, String head, String level) {
        assert (mEditor != null);
        mEditor.putBoolean(PREFERENCES_AUTO_LOGIN, autoLogin);
        mEditor.putString(PREFERENCES_USER_ID, userId);
        mEditor.putString(PREFERENCES_USER_TOKEN, Token);
        mEditor.putString(PREFERENCES_USER_NAME, name);
        mEditor.putString(PREFERENCES_USER_HEAD, head);
        mEditor.putString(PREFERENCES_USER_LEVEL, level);
        mEditor.commit();
    }

    public void clearUserInfo() {
        assert (mEditor != null);
        mEditor.putBoolean(PREFERENCES_AUTO_LOGIN, false);
        mEditor.putString(PREFERENCES_USER_NAME, "");
        mEditor.putString(PREFERENCES_USER_HEAD, "");
        mEditor.putString(PREFERENCES_USER_PASSWORD, "");
        mEditor.putString(PREFERENCES_USER_ID, "");
        mEditor.putString(PREFERENCES_USER_TOKEN, "");
        mEditor.putString(PREFERENCES_USER_LEVEL, "");
        mEditor.commit();
    }

    public void saveUpLoadApk(Boolean isUp ,String VersionCode ,String message ,String URl){
        assert (mEditor != null);
        mEditor.putBoolean(PREFERENCES_UP_APK_INFO_isUp, isUp);
        mEditor.putString(PREFERENCES_UP_APK_INFO_VERSION, VersionCode);
        mEditor.putString(PREFERENCES_UP_APK_INFO_MESSAGE, message);
        mEditor.putString(PREFERENCES_UP_APK_INFO_URL, URl);
        mEditor.commit();
    }

    public void clearUploadApkInfo(){
        assert (mEditor != null);
        mEditor.putBoolean(PREFERENCES_UP_APK_INFO_isUp, false);
        mEditor.putString(PREFERENCES_UP_APK_INFO_VERSION, "");
        mEditor.putString(PREFERENCES_UP_APK_INFO_MESSAGE, "");
        mEditor.putString(PREFERENCES_UP_APK_INFO_URL, "");
        mEditor.commit();
    }

    //每天超过多少次
    public boolean everyDayOver(int num){
        Date dt = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        long today = Long.valueOf(sdf.format(dt));
        long recordDay = mSharedPreferences.getLong("recordDay", 0);
        int size = mSharedPreferences.getInt(PREFERENCES_ACCOUT_NUM,0);
        if (today != recordDay){
            mEditor.putLong("recordDay",today);
            mEditor.putInt(PREFERENCES_ACCOUT_NUM,1);
            return false;
        }else {
            if(size++ < num){
                mEditor.putInt(PREFERENCES_ACCOUT_NUM,size);
                return false;
            }
            return true;
        }
    }

}
