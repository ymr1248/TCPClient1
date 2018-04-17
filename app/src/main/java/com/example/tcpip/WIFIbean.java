package com.example.tcpip;

/**
 * Created by Administrator on 2018/4/10 0010.
 */

public class WIFIbean {
    public String ssid;
    public  String password;

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "WIFIbean{" +
                "ssid='" + ssid + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
