package com.kds.gold.acebird.apps;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.StrictMode;
import android.support.multidex.MultiDexApplication;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.kds.gold.acebird.models.ChannelModel;
import com.kds.gold.acebird.models.ProfileModel;
import com.kds.gold.acebird.models.TvGenreModel;


import java.util.List;
import java.util.Map;
import java.util.TimeZone;


/**
 * Created by RST on 7/10/2017.
 */

public class MyApp extends MultiDexApplication{
    public static MyApp instance;
    private MyPreference preference;
    public static String version_name,user,pass,time_zone;
    public static  ProfileModel profileModel;
    public static List<TvGenreModel> tvGenreModelList;
    public static List<TvGenreModel>vodGenreModelList;
    public static List<TvGenreModel>seriesGenreModelList;
    public static List<ChannelModel>channelModelList;
    public static List<String>favRadioModelIds;
    public static List<String>favModelIds;
    public static Map back_map;
    public static int SCREEN_WIDTH, SCREEN_HEIGHT, ITEM_V_WIDTH, ITEM_V_HEIGHT,channel_size,SURFACE_WIDTH,SURFACE_HEIGHT,top_margin,right_margin;
    public static String mac_address;
    public static boolean is_first,key,is_list = false,is_welcome,is_after = false,is_search,touch = false;

    public MyPreference getPreference() {
        return preference;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        preference = new MyPreference(getApplicationContext(), Constants.APP_INFO);
        getTimeZone();
        getScreenSize();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();

    }
    private void getScreenSize() {
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        SCREEN_WIDTH = displayMetrics.widthPixels;
        SCREEN_HEIGHT = displayMetrics.heightPixels;
        if(SCREEN_WIDTH<SCREEN_HEIGHT){
            int w = SCREEN_WIDTH;
            SCREEN_WIDTH = SCREEN_HEIGHT;
            SCREEN_HEIGHT = w;
        }
        ITEM_V_WIDTH = (int) (SCREEN_WIDTH /8);
        ITEM_V_HEIGHT = (int) (ITEM_V_WIDTH * 1.6);
        SURFACE_WIDTH = (int)(SCREEN_WIDTH/3);
        SURFACE_HEIGHT = (int)(SURFACE_WIDTH*0.65);
        top_margin = SCREEN_HEIGHT/7;
        right_margin = SCREEN_WIDTH/14;
    }

    private void getTimeZone(){
        TimeZone tz = TimeZone.getDefault();
        time_zone = tz.getID();
        Log.e("time",String.valueOf(time_zone));
    }

    public void loadVersion() {
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        version_name = pInfo.versionName;
    }
    public void versionCheck(){
        if (android.os.Build.VERSION.SDK_INT > 11) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

}
