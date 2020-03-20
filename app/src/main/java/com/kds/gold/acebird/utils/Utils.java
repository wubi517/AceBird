package com.kds.gold.acebird.utils;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import com.kds.gold.acebird.apps.Constants;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    final static String fileName = "stalker_account.txt";
    final static String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
    final static String TAG = Utils.class.getName();
    public static boolean emailValidation(String email){
        Pattern pattern = Pattern.compile(Constants.EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }
    public static String getAppDir(Context context) {
        return context.getApplicationContext().getFilesDir().getAbsolutePath();
    }
    public static final String getMD5(String str) {
        String s2;
        while (true) {
            try {
                final MessageDigest instance = MessageDigest.getInstance("MD5");
                instance.update(str.getBytes(), 0, str.length());
                for (s2 = new BigInteger(1, instance.digest()).toString(16); s2.length() < 32; s2 = "0" + s2) {}
            }
            catch (NoSuchAlgorithmException ex) {
                System.out.println("Exception while encrypting to md5");
                ex.printStackTrace();
                final MessageDigest instance = null;
                continue;
            }
            break;
        }
        return s2;
    }
    public static String getPhoneMac(Context context) {
        try {
            String s = getEthMacfromEfuse("/sys/class/efuse/mac");
            if (s == null) {
                s = getEthMacfromEfuse("/sys/class/net/eth0/address");
            }
            if (s == null) {
                final Class<?> forName = Class.forName("android.os.SystemProperties");
                s = (String)forName.getMethod("get", String.class).invoke(forName, "ubootenv.var.ethaddr");
                if (s == null) {
                    final WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
                    if (wifiManager != null) {
                        s = wifiManager.getConnectionInfo().getMacAddress();
                    }
                }
            }
            if(s==null || s.isEmpty()){
                try {
                    List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
                    for (NetworkInterface nif: all) {
                        if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                        byte[] macBytes = nif.getHardwareAddress();
                        if (macBytes == null) {
                            return "";
                        }

                        StringBuilder res1 = new StringBuilder();
                        for (byte b: macBytes) {
                            //res1.append(Integer.toHexString(b & 0xFF) + ":");
                            res1.append(String.format("%02X:", b));
                        }

                        if (res1.length() > 0) {
                            res1.deleteCharAt(res1.length() - 1);
                        }
                        return res1.toString();
                    }
                }catch (Exception e){
                    return "020000000000";
                }
            }
            if (s == null) {
                return "c44eac0561b5";
            }
            return s;
        }
        catch (Exception ex) {
            return "000000000099";
        }
    }
    public static String getExtension (String url) {
        return url.substring(url.lastIndexOf(".") + 1);
    }

    private static String getEthMacfromEfuse(final String s) {
        String s2;
        try {
            final BufferedReader bufferedReader = new BufferedReader(new FileReader(s), 12);
            try {
                final String line = bufferedReader.readLine();
                bufferedReader.close();
                s2 = line;
            }
            finally {
                bufferedReader.close();
            }
        }
        catch (Exception ex) {
            s2 = null;
        }
        return s2;
    }


    public static String getPhoneSerialNumber() {
        String serial = null;
        try {
            serial = Build.class.getField("SERIAL").get(null).toString();
        } catch (Exception exception) {
            serial = "serialnumber";
        }
        return serial;
    }

    public static String getDeviceModel() {
        String model = Build.MODEL;
        return model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }

        return phrase.toString();
    }

    public static String getTime() {
        long time = System.currentTimeMillis() / 1000L;
        return String.valueOf(time);
    }


    public static String milliSecondsToTimer(long milliseconds){
        String finalTimerString = "";
        String secondsString = "";
        int hours = (int)( milliseconds / (1000*60*60));
        int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
        int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);
        if(hours > 0){
            finalTimerString = hours + ":";
        }
        if(seconds < 10){secondsString = "0" + seconds;
        }else{secondsString = "" + seconds;}
        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        return finalTimerString;
    }

    public static String getDateToTimer(long sec) {
        Date dt = new Date(sec);
        DateFormat format = new SimpleDateFormat("HH:mm:ss");
        String time = format.format(dt);
        return time;
    }

    public static String getRemainTime(long sec) {
        int hours = (int) (sec / 3600);
        int min = (int) (sec % 3600 / 60);
//        int second = (int) sec % 3600 % 60;
        String time = "";
        String min_str = "";
        if (min < 10)
            min_str = "0" + Math.abs(min);
        else
            min_str = String.valueOf(min);
        time = "- " + hours + ":" + min_str;
        return time;
    }


    public static int getProgressPercentage(long currentDuration, long totalDuration){
        Double percentage = (double) 0;
        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);
        percentage =(((double)currentSeconds)/totalSeconds) * 100;
        return percentage.intValue();
    }

    public static int progressToTimer(int progress, long totalDuration) {
        int currentDuration = 0;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double)progress) / 100) * totalDuration);
        return currentDuration * 1000;
    }

    public static String GetCurrentDate(String pattern) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        return df.format(c.getTime());
    }

    public static boolean checktimings(String current_time, String endtime, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            Date date1 = sdf.parse(current_time);
            Date date2 = sdf.parse(endtime);
            if (date1.before(date2)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static  String ReadFile( Context context){
        String line = null;
        try {
            File file1 = new File(path);
            File file = new File(file1.getPath() + File.separator + fileName);
            FileInputStream fileInputStream = new FileInputStream (file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();

            while ( (line = bufferedReader.readLine()) != null )
            {
                stringBuilder.append(line + System.getProperty("line.separator"));
            }
            fileInputStream.close();
            line = stringBuilder.toString();

            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            Log.e(TAG, ex.getMessage());
        }
        catch(IOException ex) {
            Log.e(TAG, ex.getMessage());
        }
        return line;
    }

    public static boolean saveToFile( String data){
        try {
            new File(path).mkdir();
            File file1 = new File(path);
            File file = new File(file1.getPath() + File.separator + fileName);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file,true);
            fileOutputStream.write((data + System.getProperty("line.separator")).getBytes());
            return true;
        }  catch(FileNotFoundException ex) {
            Log.e(TAG, ex.getMessage());
        }  catch(IOException ex) {
            Log.e(TAG, ex.getMessage());
        }
        return  false;
    }

    public static String getRedirectUrl(String url){
        String second_url= url;
        try {
            URLConnection conn = new URL( url ).openConnection();
            System.out.println( "orignal url: " + conn.getURL() );
            conn.connect();
            System.out.println( "connected url: " + conn.getURL() );
            InputStream inputS = conn.getInputStream();
            System.out.println( "redirected url: " + conn.getURL() );
            second_url = String .valueOf(conn.getURL());
            inputS.close();
            return second_url;
        }catch (Exception e){
            return second_url;
        }
    }
}
