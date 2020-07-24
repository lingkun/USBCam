package com.icatchtek.baseutil.network;


import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.telephony.TelephonyManager;

import com.icatchtek.baseutil.log.AppLog;
import com.icatchtek.baseutil.perference.BasePreferences;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

public class NetWorkUtils {
    private static String TAG = NetWorkUtils.class.getSimpleName();


    public static void checkNetworkState(final Context context, final OnCallback onCallback) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!isNetworkConnected(context)) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onCallback.onError();
                        }
                    });
                    return;
                }
                String path = "https://www.baidu.com";
                URL url = null;
                try {
                    url = new URL(path);
                } catch (MalformedURLException e) {
                    AppLog.d(TAG, "MalformedURLException");
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onCallback.onError();
                        }
                    });
                    return;
                }
                HttpURLConnection conn = null;
                try {
                    conn = (HttpURLConnection) url.openConnection();
                } catch (IOException e) {
                    AppLog.d(TAG, "IOException");
                    e.printStackTrace();
                }
                conn.setConnectTimeout(1000);
                try {
                    conn.setRequestMethod("HEAD");
                } catch (ProtocolException e) {
                    AppLog.d(TAG, "ProtocolException");
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onCallback.onError();
                        }
                    });
                    return;
                }
                try {
                    int responseCode = conn.getResponseCode();
                    AppLog.d(TAG, "getResponseCode responseCode=" + responseCode);

                    if (conn.getResponseCode() == 200) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                onCallback.onSuccess();
                            }
                        });
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                onCallback.onError();
                            }
                        });
                    }
                } catch (IOException e) {
                    AppLog.d(TAG, "IOException");
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onCallback.onError();
                        }
                    });
                }
            }
        }).start();
    }

//    public static void isInternetAvailable() {
//        //创建okHttpClient对象
//        OkHttpClient mOkHttpClient = new OkHttpClient();
//        mOkHttpClient.setConnectTimeout(5, TimeUnit.SECONDS);
//        mOkHttpClient.setWriteTimeout(5, TimeUnit.SECONDS);
//        mOkHttpClient.setReadTimeout(5, TimeUnit.SECONDS);
//
//        final Request request = new Request.Builder()
//                .url("https://www.baidu.com")
//                .build();
//
//        Call call = mOkHttpClient.newCall(request);
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Request request, IOException e) {
//                AppLog.d(TAG, "onFailure");
//            }
//
//            @Override
//            public void onResponse(final Response response) throws IOException {
//                AppLog.d(TAG, "onResponse");
//            }
//        });
//    }


    public static void isNetworkOnline(final Context context, final OnCallback onCallback) {
        Runtime runtime = Runtime.getRuntime();
//        final String ip = "www.baidu.com";// ping 的地址，可以换成任何一种可靠的外网
        final String ip = "www.163.com";
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean ret = false;

                if (!isNetworkConnected(context)) {
                    ret = false;
                } else {
                    AppLog.d(TAG, "start ping");
                    try {
                        Process ipProcess = Runtime.getRuntime().exec("ping -c 1 -w 2 " + ip);
                        int exitValue = ipProcess.waitFor();
                        ret = (exitValue == 0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                final boolean finalRet = ret;
                AppLog.d(TAG, "isNetworkOnline=" + ret);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (finalRet) {
                            onCallback.onSuccess();
                        } else {
                            onCallback.onError();
                        }
                    }
                });
            }
        }).start();

    }

    /**
     * 检测网络是否连接
     *
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        // 得到网络连接信息
        boolean ret = false;
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // 去进行判断网络是否连接
        if (manager.getActiveNetworkInfo() != null) {
            ret = manager.getActiveNetworkInfo().isAvailable();
        }
        AppLog.d(TAG, "isNetworkAvailable=" + ret);
        return ret;
    }

    /**
     * 判断是否有网络连接
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        boolean isConnected = false;
        //----@sha, 20180516 mark;
//        int apntype = getAPNType(context);
//        if (apntype == 1) {
//            if (context != null) {
//                ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//                NetworkInfo mWiFiNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//                if (mWiFiNetworkInfo != null) {
//                    isConnected = mWiFiNetworkInfo.isAvailable();
//                }
//            }
//        } else if (apntype == 4) {
//            boolean allow4GNetwork = BasePreferences.readBoolDataByName(context, "allow4GNetwork");
//            isConnected = allow4GNetwork;
//        } else {
//            isConnected = false;
//        }

        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null && mNetworkInfo.isAvailable() && mNetworkInfo.isConnected()) {
                isConnected = true;
            }
        }

        AppLog.d(TAG, "isNetworkConnected=" + isConnected);
        return isConnected;
    }


    /**
     * 判断WIFI网络是否可用
     *
     * @param context
     * @param context
     * @return
     */
    public static boolean isMobileConnected(Context context) {
        if (context != null) {
            //获取手机所有连接管理对象(包括对wi-fi,net等连接的管理)
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            //获取NetworkInfo对象
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            //判断NetworkInfo对象是否为空 并且类型是否为MOBILE
            if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE)
                return networkInfo.isAvailable();
        }
        return false;
    }

    /**
     * 获取当前网络连接的类型信息
     * 原生
     *
     * @param context
     * @return
     */
    public static int getConnectedType(Context context) {
        if (context != null) {
            //获取手机所有连接管理对象
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            //获取NetworkInfo对象
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isAvailable()) {
                //返回NetworkInfo的类型
                return networkInfo.getType();
            }
        }
        return -1;
    }

    /**
     * 获取当前的网络状态 ：没有网络-0：WIFI网络1：4G网络-4：3G网络-3：2G网络-2
     * 自定义
     *
     * @param context
     * @return
     */
    public static int getAPNType(Context context) {
        //结果返回值
        int netType = 0;
        //获取手机所有连接管理对象
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //获取NetworkInfo对象
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        //NetworkInfo对象为空 则代表没有网络
        if (networkInfo == null) {
            return netType;
        }
        //否则 NetworkInfo对象不为空 则获取该networkInfo的类型
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_WIFI) {
            //WIFI
            netType = 1;
        } else if (nType == ConnectivityManager.TYPE_MOBILE) {
            int nSubType = networkInfo.getSubtype();
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            AppLog.d(TAG, "nSubType=" + nSubType);
            //3G   联通的3G为UMTS或HSDPA 电信的3G为EVDO
            if (nSubType == TelephonyManager.NETWORK_TYPE_LTE
                    && !telephonyManager.isNetworkRoaming()) {
                netType = 4;
            } else if (nSubType == TelephonyManager.NETWORK_TYPE_UMTS
                    || nSubType == TelephonyManager.NETWORK_TYPE_HSDPA
                    || nSubType == TelephonyManager.NETWORK_TYPE_EVDO_0
                    && !telephonyManager.isNetworkRoaming()) {
                netType = 3;
                //2G 移动和联通的2G为GPRS或EGDE，电信的2G为CDMA
            } else if (nSubType == TelephonyManager.NETWORK_TYPE_GPRS
                    || nSubType == TelephonyManager.NETWORK_TYPE_EDGE
                    || nSubType == TelephonyManager.NETWORK_TYPE_CDMA
                    && !telephonyManager.isNetworkRoaming()) {
                netType = 2;
            } else {
                netType = 2;
            }
        }
        AppLog.d(TAG, "netType=" + netType);
        return netType;
    }

    /**
     * 判断GPS是否打开
     * ACCESS_FINE_LOCATION权限
     *
     * @param context
     * @return
     */
    public static boolean isGPSEnabled(Context context) {
        //获取手机所有连接LOCATION_SERVICE对象
        LocationManager locationManager = ((LocationManager) context.getSystemService(Context.LOCATION_SERVICE));
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    //判断是否有外网连接（普通方法不能判断外网的网络是否连接，比如连接上局域网）
    public static final boolean ping() {

        String result = null;
        try {
            String ip = "www.baidu.com";// ping 的地址，可以换成任何一种可靠的外网
            Process p = Runtime.getRuntime().exec("ping -c 1 -w 5 " + ip);// ping网址3次
            // 读取ping的内容，可以不加
            InputStream input = p.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            StringBuffer stringBuffer = new StringBuffer();
            String content = "";
            AppLog.d("------ping---dd--", "result content ");
//            while ((content = in.readLine()) != null) {
//                stringBuffer.append(content);
//            }
//            AppLog.d("------ping---ff--", "result content : " + stringBuffer.toString());
            // ping的状态
            int status = p.waitFor();
            if (status == 0) {
                result = "success";
                return true;
            } else {
                result = "failed";
            }
        } catch (IOException e) {
            result = "IOException";
        } catch (InterruptedException e) {
            result = "InterruptedException";
        } finally {
            AppLog.d("----result---", "result = " + result);
        }
        return false;
    }

    public interface OnCallback {
        void onSuccess();

        void onError();
    }

//    public void checkNetworkState(){
//
//    }

//    public static boolean is24GWifi(Context context) {
//        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//        boolean ret = false;
//        if (wifiInfo != null) {
//            int frequency = wifiInfo.getFrequency();
//            AppLog.d(TAG, "frequency =" + frequency);
//            if (frequency > 2400 && frequency < 2500) {
//                ret = true;
//            } else {
//                ret = false;
//            }
//        }
//
//        return ret;
//    }

    public static boolean is24GWifi(Context context) {
        int freq = 0;
        boolean ret = false;
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.LOLLIPOP) {
            AppLog.d(TAG, "SDK_INT > LOLLIPOP");
            freq = wifiInfo.getFrequency();
        } else {
            String ssid = wifiInfo.getSSID();
            if (ssid != null && ssid.length() > 2) {
                String ssidTemp = ssid.substring(1, ssid.length() - 1);
                List<ScanResult> scanResults = wifiManager.getScanResults();
                for (ScanResult scanResult : scanResults) {
                    if (scanResult.SSID.equals(ssidTemp)) {
                        freq = scanResult.frequency;
                        break;
                    }
                }
            }
        }

        if (freq > 2400 && freq < 2500) {
            ret = true;
        } else {
            ret = false;
        }
        return ret;
    }
}