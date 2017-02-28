package com.example.mobilesafe.engine;

/**
 * Created by sing on 14-1-16.
 * desc:使看门狗服务停止对packname保护的接口
 */
public interface IService {
    public void callTempStopProtect(String packname);
}
