package com.kds.gold.acebird.apps;

import android.content.Context;
import android.content.SharedPreferences;

public class Constants {
    public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    public static final String APP_INFO = "app_info";
    public static final String LOGIN_INFO= "login_info";
    public static final String SORT = "sort";
    public static final String RADIO_SORT = "radio_sort";
    public static final String VOD_SORT = "vod_sort";
    public static final String SERIES_SORT = "series_sort";
    public static final String SEASON_SORT = "season_sort";
    public static final String VOD_SEARCH = "vod_search";
    public static final String SERIES_SEARCH = "series_search";
    public static final String SEASON_SEARCH = "season_search";
    public static final String TOKEN = "token";
    public static final String MAC_ADDRESS = "mac_address";
    public static final String PROFILE = "profile";
    public static final String CHANNEL_POS = "channel_pos";
    public static final String VOD_POS = "vod_pos";
    public static final String SERIES_POS = "series_pos";
    public static final String POSITION_MODEL = "vod_position";
    public static final String APP_DOMAIN = "app_domain";
    public static final String APP_PORTAL = "app_portal";
    public static final String IS_PHONE = "is_phone";
    public static final String PIN_CODE = "pin_code";
    public static final String OSD_TIME = "osd_time";
    public static String GetExpire(Context context){
        String app_icon="";
        SharedPreferences serveripdetails = context.getSharedPreferences("serveripdetails", Context.MODE_PRIVATE);
        app_icon=serveripdetails.getString("expire","");
        return  app_icon;
    }

    public static String GetVersoin(Context context){
        String app_icon="";
        SharedPreferences serveripdetails = context.getSharedPreferences("serveripdetails", Context.MODE_PRIVATE);
        app_icon=serveripdetails.getString("version","");
        return  app_icon;
    }

    public static String GetUrl(Context context){
        String app_icon="";
        SharedPreferences serveripdetails = context.getSharedPreferences("serveripdetails", Context.MODE_PRIVATE);
        app_icon=serveripdetails.getString("ip","");
        return  app_icon;
    }

    public static String GetKey(Context context){
        String app_icon="";
        SharedPreferences serveripdetails = context.getSharedPreferences("serveripdetails", Context.MODE_PRIVATE);
        app_icon=serveripdetails.getString("key","");
        return  app_icon;
    }

    public static String GetPortal(Context mcontext)
    {
        String base_url="";
        SharedPreferences serveripdetails = mcontext.getSharedPreferences("serveripdetails", Context.MODE_PRIVATE);
        base_url=serveripdetails.getString("url","");
        return  base_url;
    }

    public static String GetAd1(Context mcontext)
    {
        String base_url="";
        SharedPreferences serveripdetails = mcontext.getSharedPreferences("serveripdetails", Context.MODE_PRIVATE);
        base_url=serveripdetails.getString("ad1","");
        return  base_url;
    }

    public static String GetAd2(Context mcontext)
    {
        String base_url="";
        SharedPreferences serveripdetails = mcontext.getSharedPreferences("serveripdetails", Context.MODE_PRIVATE);
        base_url=serveripdetails.getString("ad2","");
        return  base_url;
    }

    public static String GetIcon(Context mcontext)
    {
        String base_url="";
        SharedPreferences serveripdetails = mcontext.getSharedPreferences("serveripdetails", Context.MODE_PRIVATE);
        base_url=serveripdetails.getString("icon_image","");
        return  base_url;
    }
    public static String GetLoginImage(Context context){
        String app_icon="";
        SharedPreferences serveripdetails = context.getSharedPreferences("serveripdetails", Context.MODE_PRIVATE);
        app_icon=serveripdetails.getString("login_image","");
        return  app_icon;
    }

    public static String GetMainImage(Context context){
        String app_icon="";
        SharedPreferences serveripdetails = context.getSharedPreferences("serveripdetails", Context.MODE_PRIVATE);
        app_icon=serveripdetails.getString("main_bg","");
        return  app_icon;
    }
    public static String GetAutho1(Context context){
        String app_icon="";
        SharedPreferences serveripdetails = context.getSharedPreferences("serveripdetails", Context.MODE_PRIVATE);
        app_icon=serveripdetails.getString("autho1","");
        return  app_icon;
    }
    public static String GetAutho2(Context context){
        String app_icon="";
        SharedPreferences serveripdetails = context.getSharedPreferences("serveripdetails", Context.MODE_PRIVATE);
        app_icon=serveripdetails.getString("autho2","");
        return  app_icon;
    }

    public static String GetList(Context context){
        String app_icon="";
        SharedPreferences serveripdetails = context.getSharedPreferences("serveripdetails", Context.MODE_PRIVATE);
        app_icon=serveripdetails.getString("list","");
        return  app_icon;
    }
    public static String GetGrid(Context context){
        String app_icon="";
        SharedPreferences serveripdetails = context.getSharedPreferences("serveripdetails", Context.MODE_PRIVATE);
        app_icon=serveripdetails.getString("grid","");
        return  app_icon;
    }
    public static String Get1(Context context){
        String app_icon="";
        SharedPreferences serveripdetails = context.getSharedPreferences("serveripdetails", Context.MODE_PRIVATE);
        app_icon=serveripdetails.getString("g","");
        return  app_icon;
    }
    public static String Get2(Context context){
        String app_icon="";
        SharedPreferences serveripdetails = context.getSharedPreferences("serveripdetails", Context.MODE_PRIVATE);
        app_icon=serveripdetails.getString("b","");
        return  app_icon;
    }
    public static String Get3(Context context){
        String app_icon="";
        SharedPreferences serveripdetails = context.getSharedPreferences("serveripdetails", Context.MODE_PRIVATE);
        app_icon=serveripdetails.getString("c","");
        return  app_icon;
    }
    public static String Get4(Context context){
        String app_icon="";
        SharedPreferences serveripdetails = context.getSharedPreferences("serveripdetails", Context.MODE_PRIVATE);
        app_icon=serveripdetails.getString("d","");
        return  app_icon;
    }

}
