package com.ttsea.jlibrary.common.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.ttsea.jlibrary.common.encryptor.MD5Utils;
import com.ttsea.jlibrary.debug.JLog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * 手机工具类<br>
 * <p>
 * <b>date:</b> 2018/5/18 15:37 <br>
 * <b>author:</b> Jason <br>
 * <b>version:</b> 1.0 <br>
 */
final public class DeviceUtils {
    public static final String DEFAULT_MAC = "02:00:00:00:00:00";
    private static String DEVICE_ID = "";

    /**
     * 获取本机IP，如果获取不到，则返回“127.0.0.1”
     *
     * @return
     * @throws SocketException
     */
    public static String getLocalHostIP() throws SocketException {
        String hostIp = "127.0.0.1";

        Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();

        while (nis.hasMoreElements()) {
            NetworkInterface ni = (NetworkInterface) nis.nextElement();
            Enumeration<InetAddress> ias = ni.getInetAddresses();

            while (ias.hasMoreElements()) {
                InetAddress ia = ias.nextElement();
                if (ia != null && ia instanceof Inet6Address) {
                    continue;// skip ipv6
                }
                String ip = ia.getHostAddress();
                if (!"127.0.0.1".equals(ip)) {
                    hostIp = ia.getHostAddress();
                    break;
                }
            }
        }

        return hostIp;
    }

    /**
     * 获取本机卡的IMSI，需要以下权限：<br>
     * android.permission.READ_PHONE_STATE
     *
     * @param context 上下文
     * @return
     * @throws Exception
     */
    @Deprecated
    private static String getLocalPhoneNo(Context context) throws Exception {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String phoneId = tm.getLine1Number();

        if (phoneId == null) {
            phoneId = "";
        }

        return phoneId;
    }


    /**
     * 获取本机的IMEI<br>
     * android 10已经禁止获取了<br>
     * 需要以下权限：<br>
     * android.permission.READ_PHONE_STATE
     *
     * @return
     * @throws Exception
     */
    private static String getIMEI(Context context) throws Exception {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = tm.getDeviceId();

        if (imei == null) {
            imei = "";
        }

        return imei;
    }

    /**
     * 获取本机的SN序列号<br>
     * 获取序列号不需要权限，但是有一定的局限性，在有些手机上会出现垃圾数据，比如红米手机返回的就是连续的非随机数
     *
     * @return
     * @throws Exception
     */
    private static String getSN(Context context) throws Exception {
        return Build.SERIAL;
    }

    /**
     * 获取本机的ANDROID_ID<br>
     * 刷机后会变化<br>
     * 少数手机会返回空
     *
     * @return
     * @throws Exception
     */
    private static String getAndroidId(Context context) throws Exception {
        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return androidId;
    }

    /**
     * 获取本机的UniquePsuedoId<br>
     * 由于是与设备信息直接相关，如果是同一批次出厂的的设备有可能出现生成的内容可能是一样的。
     * （通过模拟器实验过，打开两个完全一样的模拟器，生成的内容是完全一下），
     * 所以如果单独使用该方法也是不能用于生成唯一标识符的。
     *
     * @return
     * @throws Exception
     */
    private static String getUniquePsuedoId(Context context) throws Exception {
        String m_szDevIDShort = "35"//
                + (Build.BOARD.length() % 10)//
                + (Build.BRAND.length() % 10)//
                + (Build.CPU_ABI.length() % 10)//
                + (Build.DEVICE.length() % 10)//
                + (Build.MANUFACTURER.length() % 10)//
                + (Build.MODEL.length() % 10) //
                + (Build.PRODUCT.length() % 10);//

        // Thanks to @Roman SL!
        // http://stackoverflow.com/a/4789483/950427
        // Only devices with API >= 9 have android.os.Build.SERIAL
        // http://developer.android.com/reference/android/os/Build.html#SERIAL
        // If a user upgrades software or roots their phone, there will be a
        // duplicate entry
        String serial = null;
        try {
            serial = Build.class.getField("SERIAL").get(null).toString();

            // Go ahead and return the serial for api => 9
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode())
                    .toString();
        } catch (Exception e) {
            // String needs to be initialized
            serial = "serial"; // some value
        }

        // Thanks @Joe!
        // http://stackoverflow.com/a/2853253/950427
        // Finally, combine the values we have found by using the UUID class to
        // create a unique identifier
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }

    /**
     * 获取本机的系统信息
     *
     * @return
     * @throws Exception
     */
    private static String getBuildInfo(Context context) throws Exception {
        StringBuffer buildSB = new StringBuffer();
        buildSB.append(Build.BRAND).append("_");
        buildSB.append(Build.PRODUCT).append("_");
        buildSB.append(Build.DEVICE).append("_");
        buildSB.append(Build.ID).append("_");
        buildSB.append(Build.VERSION.INCREMENTAL);

        return buildSB.toString();
    }

    /**
     * 设备唯一ID<br>
     *
     * @param context 上下文
     * @return
     * @throws Exception
     */
    @Nullable
    public static String getDeviceId(Context context) {
        if (!Utils.isEmpty(DEVICE_ID)) {
            return DEVICE_ID;
        }

        synchronized (DeviceUtils.class) {
            if (Utils.isEmpty(DEVICE_ID)) {
                StringBuffer buffer = new StringBuffer();

                // 1.IMEI
                try {
                    String imei = getIMEI(context);
                    JLog.d("imei:" + imei);
                    if (!Utils.isEmpty(imei)) {
                        buffer.append(imei);
                    }
                } catch (Exception e) {
                    JLog.w("Exception e:" + e.getMessage());
                }

                // 2.MAC
                try {
                    String mac = getMacAddress(context);
                    JLog.d("mac:" + mac);
                    if (!Utils.isEmpty(mac)) {
                        buffer.append(mac);
                    }
                } catch (Exception e) {
                    JLog.w("Exception e:" + e.getMessage());
                }

                // 3.SN
                try {
                    String sn = getSN(context);
                    JLog.d("sn:" + sn);
                    if (!Utils.isEmpty(sn)) {
                        buffer.append(sn);
                    }
                } catch (Exception e) {
                    JLog.w("Exception e:" + e.getMessage());
                }

                // 4.ANDROID_ID
                try {
                    String androidId = getAndroidId(context);
                    JLog.d("androidId:" + androidId);
                    if (!Utils.isEmpty(androidId)) {
                        buffer.append(androidId);
                    }
                } catch (Exception e) {
                    JLog.w("Exception e:" + e.getMessage());
                }

                // 5.uniquePsuedoId
                try {
                    String uniqueId = getUniquePsuedoId(context);
                    JLog.d("uniqueId:" + uniqueId);
                    if (!Utils.isEmpty(uniqueId)) {
                        buffer.append(uniqueId);
                    }
                } catch (Exception e) {
                    JLog.w("Exception e:" + e.getMessage());
                }

                // 6.buildInfo
                try {
                    String buildInfo = getBuildInfo(context);
                    JLog.d("buildInfo:" + buildInfo);
                    if (!Utils.isEmpty(buildInfo)) {
                        buffer.append(buildInfo);
                    }
                } catch (Exception e) {
                    JLog.w("Exception e:" + e.getMessage());
                }

                String value = buffer.toString();

                //以上都取不到，则默认使用用户名
                if (Utils.isEmpty(value)) {
                    //value = UserInfoUtils.getLocalUserName(context);
                    return value;
                }

                try {
                    DEVICE_ID = MD5Utils.stringMD5(value);
                    JLog.d("create DEVICE_ID:" + DEVICE_ID);

                } catch (Exception e) {
                    JLog.w("create DEVICE_ID, Exception e:" + e.getMessage());
                }
            }
        }

        return DEVICE_ID;
    }

    /**
     * 获取本机的wifi mac地址
     *
     * @param context
     * @return mac address or 02:00:00:00:00:00
     */
    public static String getMacAddress(Context context) {
        // Build.VERSION_CODES.M = 23
        // Build.VERSION_CODES.N = 24

        String mac = DEFAULT_MAC;
        if (Build.VERSION.SDK_INT < 23) {
            mac = getMacDefault(context);

        } else if (Build.VERSION.SDK_INT >= 23 && Build.VERSION.SDK_INT < 24) {
            mac = getMacFromFile();

        } else if (Build.VERSION.SDK_INT >= 24) {
            mac = getMacFromHardware();
        }

        return mac;
    }

    /**
     * Android 6.0 之前（不包括6.0） 必须的权限 <uses-permission
     * android:name="android.permission.ACCESS_WIFI_STATE" />
     *
     * @param context
     * @return
     */
    private static String getMacDefault(Context context) {
        String mac = DEFAULT_MAC;
        if (context == null) {
            return mac;
        }

        WifiManager wifi = (WifiManager) context.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        if (wifi == null) {
            return mac;
        }
        WifiInfo info = null;
        try {
            info = wifi.getConnectionInfo();
        } catch (Exception e) {
        }
        if (info == null) {
            return null;
        }
        mac = info.getMacAddress();
        if (!TextUtils.isEmpty(mac)) {
            mac = mac.toUpperCase(Locale.ENGLISH);
        }
        return mac;
    }

    /**
     * Android 6.0（包括） - Android 7.0（不包括）
     *
     * @return
     */
    private static String getMacFromFile() {
        String WifiAddress = "02:00:00:00:00:00";
        try {
            WifiAddress = new BufferedReader(new FileReader(new File(
                    "/sys/class/net/wlan0/address"))).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return WifiAddress;
    }

    /**
     * 遍历循环所有的网络接口，找到接口是 wlan0 必须的权限 <uses-permission
     * android:name="android.permission.INTERNET" />
     *
     * @return
     */
    private static String getMacFromHardware() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface
                    .getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0"))
                    continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }

                return res1.toString();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return DEFAULT_MAC;
    }
}
