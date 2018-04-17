package com.example.tcpip;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

public class MainActivity extends Activity implements AdapterView.OnItemSelectedListener {

    /*
     * 1.加权限(网络)
     * 2.给服务器发请求写在子线程
     * 3.必须用Handler机制
     * 4.Ip地址
     * 5.乱码:改成UTF-8
     */
    private String TAG = "man";
    private TextView tv;
    private EditText countent;
    private Spinner wifiName;
    private EditText wifiPassWord;
    private WIFIbean wifIbean;
    private ArrayList<String> mData = null;
    private String wifiname1 = "";
    private boolean one_selected = false;
    private BaseAdapter myAdadpter = null;
    Gson gson = new Gson();
    List<ScanResult> list;
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 0x123) {
                String obj = (String) msg.obj;0000000000000000000000
                tv.setText(obj);
            }
        }

        ;
    };


    public void click(View view) {
        new Thread() {
            public void run() {
                try {
                    wifIbean = new WIFIbean();
                    wifIbean.setPassword(wifiPassWord.getText().toString());
                    wifIbean.setSsid(wifiname1);
                    String s = gson.toJson(wifIbean, WIFIbean.class);
                    Socket client;
                    Log.d(TAG, "run: 333333333333333333333" + s);
                    //socket=new Socket("192.168.1.102", 12345);//注意这里
                    client = new Socket();
//                    SocketAddress socAddress = new InetSocketAddress("192.168.4.1", 8266);
                    SocketAddress socAddress = new InetSocketAddress("192.168.0.102", 8266);
                    client.connect(socAddress, 3000);//超时3秒
                    try {
                        //客户端从服务端获取数据

                        //获取要发送的字符串
                        DataOutputStream dos = new DataOutputStream(client.getOutputStream());
                        //将字符串按：UTF-8字节流方式传输。先传输长度，再传输字节内容。
                        dos.writeUTF(s);
                    /**/

                        Log.d(TAG, "run: 333333333333333333333");
//                        BufferedReader bufferedReader =
//                                new BufferedReader
//                                        (new InputStreamReader
//                                                (client.getInputStream()));
                        //tv.setText(text);
                        //线程复用

                        InputStream inputStream = client.getInputStream();
                        DataInputStream input = new DataInputStream(inputStream);
                        byte[] b = new byte[1000000];
                        int length = input.read(b);
                        String Msg = new String(b, 0, length, "UTF-8");
                        Log.v(TAG, Msg);

                        Message msg = Message.obtain();
                        msg.what = 0x123;
                        msg.obj = Msg;
                        handler.sendMessage(msg);
                        dos.flush();
                        dos.close();

//                        bufferedReader.close();
                        client.close();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        Log.d(TAG, "1run: " + e);
                        e.printStackTrace();
                    } finally {
                        client.close();
                    }

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    Log.d(TAG, "2run: " + e);
                    e.printStackTrace();
                }

            }

            ;
        }.start();
    }

    private void bindViews() {
        myAdadpter = new MyAdapter<String>(mData, R.layout.item) {
            @Override
            public void bindView(ViewHolder holder, String obj) {
                holder.setText(R.id.txt_name, obj);
            }
        };
        wifiName.setAdapter(myAdadpter);
        wifiName.setOnItemSelectedListener(this);

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.wifi_name:
                if (one_selected) {
                    wifiname1 = parent.getItemAtPosition(position).toString();
                } else one_selected = true;
                break;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.tv);
        countent = (EditText) findViewById(R.id.numIP);
        wifiName = (Spinner) findViewById(R.id.wifi_name);
        wifiPassWord = (EditText) findViewById(R.id.wifi_password);
        countent.setText("at");
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        list = wifiManager.getScanResults();
        mData = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            ScanResult scanResult = list.get(i);
            if (scanResult.SSID.toString().equals("")) {

            } else {
                mData.add(scanResult.SSID);
            }

        }
        wifiname1 = list.get(0).SSID.toString();
        bindViews();
    }
}
