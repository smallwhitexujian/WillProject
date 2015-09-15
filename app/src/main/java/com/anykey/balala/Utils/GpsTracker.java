package com.anykey.balala.Utils;

import android.app.LoaderManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

/**
 * Created by xujian on 15/9/8.
 * 获取定位的经纬度
 */
public class GpsTracker extends Service implements LocationListener{
    private Context mcontext;
    //标记GSP状态
    private boolean isGPSEnabled = false;
    //标记网络状态
    private boolean isNetworkEnabled = false;
    //标记能否获取位置信息，
    private boolean isGetLocation = false;
    private Location location;//位置
    private double latitude;//纬度
    private double longitude;//纬度
    // 更新最多距离 米为单位
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 米
    // 更新最多时间 毫秒为单位
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 分钟
    // 声明一个 Location Manager
    protected LocationManager locationManager;

    public GpsTracker(Context context){
        this.mcontext = context;
        getLocation();
    }

    public Location getLocation(){
        try {
            locationManager = (LocationManager)mcontext.getSystemService(LOCATION_SERVICE);
            //获取GPS状态
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            //获取网络状态
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!isGPSEnabled && !isNetworkEnabled){
                //GPS和网络都不好用
            }else{
                this.isGetLocation = true;
                //首先通过网络获取地理位置
                if (isNetworkEnabled){
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES,this);
                    if (locationManager != null){
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null){
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                //如果GPS打开，则通过GPS获取定位信息
                if (location == null) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    /**
     * 获取纬度
     **/
    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }
        return latitude;
    }

    /**
     * 获取经度
     **/
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }
        return longitude;
    }

    /**
     * 检查GPS 网络是否可用
     * @return boolean
     * */
    public boolean isGetLocation() {
        return this.isGetLocation;
    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
