package com.example.mobilesafe.engine;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Created by sing on 14-1-3.
 * desc:
 */
public class GPSInfoProvider {

    private static GPSInfoProvider mGPSInfoProvider;

    private static LocationManager lm;

    private static MyListener listener;

    private static SharedPreferences sp;

    private GPSInfoProvider() {
    }

    public synchronized static GPSInfoProvider getInstance(Context context) {
        if (mGPSInfoProvider == null) {
            mGPSInfoProvider = new GPSInfoProvider();

            //获取位置管理器
            lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            //获取查询地理位置的查询条件对象（内部是map集合）
            Criteria criteria = new Criteria();

            //设置精准度：最精准
            criteria.setAccuracy(Criteria.ACCURACY_FINE);

            //gps定位是否允许产生开销
            criteria.setCostAllowed(true);

            //手机的功耗消耗情况（实时定位时，设置为最高）
            criteria.setPowerRequirement(Criteria.POWER_HIGH);

            //获取海拔高度
            criteria.setAltitudeRequired(true);

            //对手机的移动速度是否敏感
            criteria.setSpeedRequired(true);

            //获取当前手机最好用的位置提供者，参数一：查询选择条件，参数二：传递true表示只有可用的位置提供者时才会返回
            String provider = lm.getBestProvider(criteria, true);

            listener = new GPSInfoProvider().new MyListener();

            //调用位置更新方法。参数一：位置提供者，参数二：最短的更新位置信息时间（最好大于60000即一分钟）
            //参数三：最短通知距离，参数四：位置改变时的监听对象
            lm.requestLocationUpdates(provider, 60000, 100, listener);
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }

        return mGPSInfoProvider;
    }


    /**
     * 取消位置监听
     */
    public void stopListen() {
        lm.removeUpdates(listener);
        listener = null;
    }

    /**
     * 位置监听对象
     */
    protected class MyListener implements LocationListener {

        /**
         * 当手机位置发生改变时调用
         *
         * @param location
         */
        @Override
        public void onLocationChanged(Location location) {
            String latitude = "latitude:" + location.getLatitude();
            String longitude = "longitude:" + location.getLongitude();
            String meter = "accuracy:" + location.getAccuracy();

            System.out.println(latitude + "-" + longitude + "-" + meter);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("last_location", latitude + "-" + longitude + "-" + meter);
            editor.commit();
        }

        /**
         * 当位置提供者状态发生变化时调用
         *
         * @param s
         * @param i
         * @param bundle
         */
        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        /**
         * 当位置提供者不可用时
         *
         * @param s
         */
        @Override
        public void onProviderDisabled(String s) {

        }

        /**
         * 当位置提供者可用时
         *
         * @param s
         */
        @Override
        public void onProviderEnabled(String s) {

        }
    }

    /**
     * 读取手机位置
     *
     * @return
     */
    public String getLocation() {
        return sp.getString("last_location", "");
    }

}
