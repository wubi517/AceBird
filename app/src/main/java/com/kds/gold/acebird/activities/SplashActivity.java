package com.kds.gold.acebird.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kds.gold.acebird.BuildConfig;
import com.kds.gold.acebird.R;
import com.kds.gold.acebird.apps.Constants;
import com.kds.gold.acebird.apps.MyApp;
import com.kds.gold.acebird.async.GetAsyncTask;
import com.kds.gold.acebird.async.GetDataService;
import com.kds.gold.acebird.async.GetProfileService;
import com.kds.gold.acebird.async.GetTokenService;
import com.kds.gold.acebird.dialog.ConnectionDlg;
import com.kds.gold.acebird.dialog.MacDlg;
import com.kds.gold.acebird.dialog.UpdateDlg;
import com.kds.gold.acebird.models.ChannelModel;
import com.kds.gold.acebird.models.LoginModel;
import com.kds.gold.acebird.models.ProfileModel;
import com.kds.gold.acebird.models.TvGenreModel;
import com.kds.gold.acebird.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SplashActivity extends AppCompatActivity implements GetDataService.OnGetResultsListener,
        GetTokenService.OnGetTokenResultListner,GetProfileService.OnGetProfilesListner,GetAsyncTask.OnGetResultsListener{
    String token,mac,app_Url;
    List<TvGenreModel> tvGenreModelList;
    List<TvGenreModel>vodGenreModelList;
    List<TvGenreModel>seriesGenreModelList;
    List<ChannelModel>channelModelList;
    ChannelModel channelModel;
    int msgs,additional_services_on;
    boolean doubleBackToExitPressedOnce = false;
    SharedPreferences serveripdetails;
    String version;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash2);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .penaltyLog()
                .detectAll()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .penaltyLog()
                .detectAll()
                .build());
        if (MyApp.instance.getPreference().get(Constants.MAC_ADDRESS) == null) {
            MyApp.mac_address = Utils.getPhoneMac(SplashActivity.this);
//                MyApp.mac_address = "08:C5:E1:AE:15:E1";
            MyApp.instance.getPreference().put(Constants.MAC_ADDRESS, MyApp.mac_address);
        } else
//                MyApp.mac_address = "08:C5:E1:AE:15:E1";
            MyApp.mac_address = (String) MyApp.instance.getPreference().get(Constants.MAC_ADDRESS);
        serveripdetails = this.getSharedPreferences("serveripdetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor server_editor0 = serveripdetails.edit();
        server_editor0.putString("list","list");
        server_editor0.putString("grid","grid");
        server_editor0.putString("a","Zyqc9u");
        server_editor0.putString("c","Zyg5qrc29u");
        server_editor0.putString("d","Zyceg29u");
        server_editor0.putString("e","Zy5qd29u");
        server_editor0.putString("f","Zy5rhqc29u");
        server_editor0.putString("g","Zy5qc29u");
        server_editor0.commit();
//        if(!getApplicationContext().getPackageName().equalsIgnoreCase(new String(Base64.decode(new String (Base64.decode("cy5nb2xkLY3k1bmIyeGtMWTI5dExtdGtjeTVuYjJ4a0xuUnlaWGc9".substring(9),Base64.DEFAULT)).substring(9),Base64.DEFAULT)))){
////            return;
////        }
        try {
            JSONObject jsonObject = new JSONObject(loadJSONFromAsset());
            SharedPreferences.Editor server_editor = serveripdetails.edit();
            byte[] decodeValue00 = Base64.decode(jsonObject.getString("f").substring(10), Base64.DEFAULT);
            byte[] decodeValue01 = Base64.decode(new String (decodeValue00), Base64.DEFAULT);
            server_editor.putString("url", new String (decodeValue01));
            byte[] decodeValue10 = Base64.decode(jsonObject.getString("g").substring(4), Base64.DEFAULT);
            server_editor.putString("key",new String (decodeValue10));
            byte[] decodeValue30 = Base64.decode(jsonObject.getString("j").substring(25),Base64.DEFAULT);
            byte[] decodeVaue31 = Base64.decode(new String(decodeValue30).substring(20),Base64.DEFAULT);
            server_editor.putString("b",new String (decodeVaue31));
            server_editor.apply();
        }catch (Exception e){
            Toast.makeText(SplashActivity.this, "Server Error!", Toast.LENGTH_SHORT).show();
        }

        try {
            JSONObject jsonObject = new JSONObject(loadJSONFromAsset1());
            SharedPreferences.Editor server_editor = serveripdetails.edit();
            byte[] decodeValue20 = Base64.decode(jsonObject.getString("google_kye").substring(5),Base64.DEFAULT);
            byte[] decodeVaue21 = Base64.decode(new String(decodeValue20).substring(11),Base64.DEFAULT);
            server_editor.putString("autho1",new String (decodeVaue21));
            byte[] decodeValue30 = Base64.decode(jsonObject.getString("firebase").substring(5),Base64.DEFAULT);
            byte[] decodeVaue31 = Base64.decode(new String(decodeValue30).substring(17),Base64.DEFAULT);
            server_editor.putString("autho2",new String (decodeVaue31));
            server_editor.apply();
        }catch (Exception e){
            Toast.makeText(SplashActivity.this, "Server Error!", Toast.LENGTH_SHORT).show();
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            CheckSDK23Permission();
        } else {
            getRespond();
        }
        RelativeLayout main_lay =  findViewById(R.id.main_lay);
//        PlayGifView playGifView = findViewById(R.id.splash_gif);
//        playGifView.setImageResource(R.raw.start);
//        DisplayMetrics metrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(metrics);
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) playGifView.getLayoutParams();
//        params.width =  metrics.widthPixels;
//        params.height = metrics.heightPixels;
//        params.leftMargin = 0;
//        playGifView.setLayoutParams(params);
        if(MyApp.instance.getPreference().get(Constants.PIN_CODE)==null){
            MyApp.instance.getPreference().put(Constants.PIN_CODE,"0000");
        }

//        Log.e("path",Utils.getAppDir(this));
    }
    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getApplicationContext().getAssets().open(new String (Base64.decode(Constants.Get1(this),Base64.DEFAULT)));
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
    public String loadJSONFromAsset1() {
        String json = null;
        try {
            InputStream is = getApplicationContext().getAssets().open(Constants.GetGrid(this)+".json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
    @Override
    public void onGetResultsData1(JSONObject object, int request_code) {
        if(object==null){
            ConnectionDlg connectionDlg = new ConnectionDlg(SplashActivity.this, new ConnectionDlg.DialogConnectionListener() {
                @Override
                public void OnRetryClick(Dialog dialog) {
                    dialog.dismiss();
                    GetAsyncTask asyncTask1 = new GetAsyncTask(SplashActivity.this, 1100);
                    asyncTask1.execute(Constants.GetExpire(SplashActivity.this) + MyApp.mac_address);
                    asyncTask1.onGetResultsData1(SplashActivity.this);
                }

                @Override
                public void OnHelpClick(Dialog dialog) {
                    startActivity(new Intent(SplashActivity.this, ConnectionErrorActivity.class));
                }
            });
            connectionDlg.show();
        }else {
            if(request_code==1000){
                try {
                    if(object.get("stat").toString().equalsIgnoreCase("1")){
                        GetAsyncTask asyncTask = new GetAsyncTask(SplashActivity.this,1100);
                        asyncTask.execute(Constants.GetVersoin(SplashActivity.this));
                        asyncTask.onGetResultsData1(SplashActivity.this);
                    }else {
                        MacDlg macDlg = new MacDlg(SplashActivity.this, new MacDlg.DialogMacListener() {
                            @Override
                            public void OnYesClick(Dialog dialog) {
                                dialog.dismiss();
                                finish();
                                System.exit(0);
                                android.os.Process.killProcess(android.os.Process.myPid());
                            }
                        });
                        macDlg.show();
                    }
                }catch (Exception e){
                    MacDlg macDlg = new MacDlg(SplashActivity.this, new MacDlg.DialogMacListener() {
                        @Override
                        public void OnYesClick(Dialog dialog) {
                            dialog.dismiss();
                            finish();
                            System.exit(0);
                            android.os.Process.killProcess(android.os.Process.myPid());
                        }
                    });
                    macDlg.show();
                }
            }else if(request_code==1100){
            }
        }
    }

    private void getUpdate(){
        MyApp.instance.versionCheck();
        double code = 0.0;
        try {
            code = Double.parseDouble(version);
        }catch (Exception e){

        }
        MyApp.instance.loadVersion();
        double app_vs = Double.parseDouble(MyApp.version_name);
        if (code > app_vs) {
            UpdateDlg updateDlg = new UpdateDlg(SplashActivity.this, new UpdateDlg.DialogUpdateListener() {
                @Override
                public void OnUpdateNowClick(Dialog dialog) {
                    dialog.dismiss();
                    new versionUpdate().execute(app_Url);
                }

                @Override
                public void OnUpdateSkipClick(Dialog dialog) {
                    dialog.dismiss();
                    getAutho();
                }
            });
            updateDlg.show();
        }else {
            getAutho();
        }
    }
    private void getToken(){
        LoginModel loginModel = (LoginModel) MyApp.instance.getPreference().get(Constants.LOGIN_INFO);
        mac = loginModel.getMac_address();
        mac = mac.replaceAll("\\s+$", "");
        MyApp.instance.getPreference().put(Constants.APP_PORTAL,Constants.GetUrl(this)+"c/");
//        Url = Constants.GetUrl(this)+"portal.php?action=handshake&type=stb&token=&JsHttpRequest=1-xml";
        GetTokenService getTokenService = new GetTokenService(this);
        getTokenService.getData(this,Constants.GetUrl(this)+"portal.php?action=handshake&type=stb&token=&JsHttpRequest=1-xml",100);
        getTokenService.onGetTokenData(SplashActivity.this);
    }
    @Override
    public void onGetResultsData(JSONObject object, int request_code) {
        if(object!=null){
            if(request_code==100){
//                Url = Constants.GetUrl(this)+"portal.php?type=itv&action=get_all_channels&force_ch_link_check=&JsHttpRequest=1-xml";
                GetDataService dataService = new GetDataService(SplashActivity.this);
                dataService.getData(SplashActivity.this, Constants.GetUrl(this)+"portal.php?type=itv&action=get_all_channels&force_ch_link_check=&JsHttpRequest=1-xml",200);
                dataService.onGetResultsData(SplashActivity.this);
            }else if(request_code==200){
                channelModelList = new ArrayList<>();
                try {
                    JSONObject channels_obj = object.getJSONObject("js");
                    JSONArray channels_array = channels_obj.getJSONArray("data");
                    if(channels_array.length()>0){
                        for(int i = 0;i<channels_array.length();i++){
                            JSONObject channel_obj = channels_array.getJSONObject(i);
                            try {
                                channelModel = new ChannelModel();
                                channelModel.setId(channel_obj.getString("id"));
                                channelModel.setNumber(channel_obj.getString("number"));
                                channelModel.setTv_genre_id(channel_obj.getString("tv_genre_id"));
                                channelModel.setName(channel_obj.getString("name"));
                                channelModel.setCmd(channel_obj.getString("cmd"));
                                channelModel.setTv_archiev_duration(channel_obj.getInt("tv_archive_duration"));
                                channelModel.setLocked(channel_obj.getInt("locked"));
                                channelModel.setLock(channel_obj.getInt("lock"));
                                channelModel.setFav(channel_obj.getInt("fav"));
                                channelModel.setArchive(channel_obj.getInt("archive"));
                                try {
                                    channelModel.setLogo(channel_obj.getString("logo"));
                                }catch (Exception e2){
                                    channelModel.setLogo("");
                                }
                                channelModelList.add(channelModel);
                            }catch (Exception e1){

                            }
                        }
                    }
                    MyApp.channel_size = channelModelList.size();
                    Map back_map = new HashMap();
                    back_map.put("channels", channelModelList);
                    MyApp.back_map = back_map;
                    MyApp.channelModelList  = channelModelList;
                }catch (Exception e){
                }
//                Url = Constants.GetUrl(this)+"portal.php?type=itv&action=get_all_fav_channels&fav=1&force_ch_link_check=&JsHttpRequest=1-xml";
                GetDataService dataService = new GetDataService(SplashActivity.this);
                dataService.getData(SplashActivity.this,Constants.GetUrl(this)+"portal.php?type=itv&action=get_all_fav_channels&fav=1&force_ch_link_check=&JsHttpRequest=1-xml",300);
                dataService.onGetResultsData(SplashActivity.this);
            }else if(request_code==300){

//                Url = Constants.GetUrl(this)+"portal.php?type=itv&action=get_fav_ids&force_ch_link_check=&JsHttpRequest=1-xml";
                GetDataService dataService = new GetDataService(SplashActivity.this);
                dataService.getData(SplashActivity.this,Constants.GetUrl(this)+"portal.php?type=itv&action=get_fav_ids&force_ch_link_check=&JsHttpRequest=1-xml",400);
                dataService.onGetResultsData(SplashActivity.this);
            }else if(request_code==400){
                //this is fav channels ids
                List<String > favModelIds = new ArrayList<>();
                try {
                    JSONArray idsArray = object.getJSONArray("js");
                    for(int i = 0;i<idsArray.length();i++){
                        String id = String.valueOf(idsArray.getInt(i));
                        favModelIds.add(id);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                MyApp.favModelIds = favModelIds;
//                Url = Constants.GetUrl(this)+"portal.php?type=itv&action=get_genres&JsHttpRequest=1-xml";
                GetDataService dataService = new GetDataService(SplashActivity.this);
                dataService.getData(SplashActivity.this,Constants.GetUrl(this)+"portal.php?type=itv&action=get_genres&JsHttpRequest=1-xml",600);
                dataService.onGetResultsData(SplashActivity.this);
            }else if(request_code==500){
                try {
                    JSONObject data_obj = object.getJSONObject("js");
                    msgs = data_obj.getInt("msgs");
                    additional_services_on = data_obj.getInt("additional_services_on");
//                    Log.e("msgs",String .valueOf(msgs));
                }catch (Exception e){

                }
//                Url = Constants.GetUrl(this)+"portal.php?type=itv&action=get_genres&JsHttpRequest=1-xml";
                GetDataService dataService = new GetDataService(SplashActivity.this);
                dataService.getData(SplashActivity.this,Constants.GetUrl(this)+"portal.php?type=itv&action=get_genres&JsHttpRequest=1-xml",600);
                dataService.onGetResultsData(SplashActivity.this);
            }else if(request_code==600){
                tvGenreModelList = new ArrayList<>();
                try {
                    JSONArray genre_array = object.getJSONArray("js");
                    if(genre_array.length()>0){
                        for(int i = 0;i<genre_array.length();i++){
                            JSONObject genre_obj = genre_array.getJSONObject(i);
                            TvGenreModel tvGenreModel = new TvGenreModel();
                            try {
                                tvGenreModel.setId(genre_obj.getString("id"));
                                tvGenreModel.setTitle(genre_obj.getString("title"));
                                tvGenreModel.setAlias(genre_obj.getString("alias"));
                            }catch (Exception e1){
                            }
                            tvGenreModelList.add(tvGenreModel);
                        }
                    }
                }catch (Exception e){
                }
                MyApp.tvGenreModelList = tvGenreModelList;
//                Url = Constants.GetUrl(this)+"portal.php?type=vod&action=get_categories&JsHttpRequest=1-xml";
                GetDataService dataService = new GetDataService(SplashActivity.this);
                dataService.getData(SplashActivity.this,Constants.GetUrl(this)+"portal.php?type=vod&action=get_categories&JsHttpRequest=1-xml",700);
                dataService.onGetResultsData(SplashActivity.this);
            }else if(request_code==700){
                vodGenreModelList = new ArrayList<>();
                try {
                    JSONArray genre_array = object.getJSONArray("js");
                    if(genre_array.length()>0){
                        for(int i = 0;i<genre_array.length();i++){
                            JSONObject genre_obj = genre_array.getJSONObject(i);
                            TvGenreModel tvGenreModel = new TvGenreModel();
                            try {
                                tvGenreModel.setId(genre_obj.getString("id"));
                                tvGenreModel.setTitle(genre_obj.getString("title"));
                                tvGenreModel.setAlias(genre_obj.getString("alias"));
                            }catch(Exception e1){
                            }
                            vodGenreModelList.add(tvGenreModel);
                        }
                    }
                }catch(Exception e){
                }
                MyApp.vodGenreModelList = vodGenreModelList;

//                Url = Constants.GetUrl(this)+"portal.php?type=series&action=get_categories&JsHttpRequest=1-xml";
                GetDataService dataService = new GetDataService(SplashActivity.this);
                dataService.getData(SplashActivity.this,Constants.GetUrl(this)+"portal.php?type=series&action=get_categories&JsHttpRequest=1-xml",800);
                dataService.onGetResultsData(SplashActivity.this);
            }else if(request_code==800){
                seriesGenreModelList = new ArrayList<>();
                try {
                    JSONArray genre_array = object.getJSONArray("js");
                    if(genre_array.length()>0){
                        for(int i = 0;i<genre_array.length();i++){
                            JSONObject genre_obj = genre_array.getJSONObject(i);
                            TvGenreModel tvGenreModel = new TvGenreModel();
                            try {
                                tvGenreModel.setId(genre_obj.getString("id"));
                                tvGenreModel.setTitle(genre_obj.getString("title"));
                                tvGenreModel.setAlias(genre_obj.getString("alias"));
                            }catch(Exception e1){
                            }
                            seriesGenreModelList.add(tvGenreModel);
                        }
                    }
                }catch(Exception e){
                }
                MyApp.seriesGenreModelList = seriesGenreModelList;
//                Url = Constants.GetUrl(this)+"portal.php?type=account_info&action=get_main_info&JsHttpRequest=1-xml";
                GetDataService dataService = new GetDataService(SplashActivity.this);
                dataService.getData(SplashActivity.this,Constants.GetUrl(this)+"portal.php?type=account_info&action=get_main_info&JsHttpRequest=1-xml",900);
                dataService.onGetResultsData(SplashActivity.this);


            }else if(request_code==900){
                try {
                    JSONObject account_obj = object.getJSONObject("js");
                    String expired = "";
                    if(account_obj.getString("phone")!=null){
                        expired = account_obj.getString("phone");
                    }
                    if(!expired.isEmpty()){
                        MyApp.instance.getPreference().put(Constants.PROFILE,expired);
                    }
                }catch (Exception e){

                }
                if(MyApp.instance.getPreference().get(Constants.OSD_TIME)==null){
                    MyApp.instance.getPreference().put(Constants.OSD_TIME,3);
                }
//                Url = Constants.GetUrl(this)+"portal.php?type=radio&action=get_fav_ids&JsHttpRequest=1-xml";
                GetDataService dataService = new GetDataService(SplashActivity.this);
                dataService.getData(SplashActivity.this,Constants.GetUrl(this)+"portal.php?type=radio&action=get_fav_ids&JsHttpRequest=1-xml",1000);
                dataService.onGetResultsData(SplashActivity.this);
            }else if(request_code==1000){
                List<String > favModelIds = new ArrayList<>();
                try {
                    JSONArray idsArray = object.getJSONArray("js");
                    for(int i = 0;i<idsArray.length();i++){
                        String id = String.valueOf(idsArray.getInt(i));
                        favModelIds.add(id);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                MyApp.favRadioModelIds = favModelIds;
                getAuthorization();
            }
        }
    }

    private void getAuthorization(){
        StringRequest request = new StringRequest(Constants.GetAutho1(this), new Response.Listener<String>() {
            @Override
            public void onResponse(String string) {
                try {
                    JSONObject object = new JSONObject(string);
                    if (((String) object.get("status")).equalsIgnoreCase("success")) {
                        startActivity(new Intent(SplashActivity.this,WelcomeActivity.class));
                        finish();
                    } else {
                        Toast.makeText(SplashActivity.this, "Server Error!", Toast.LENGTH_SHORT).show();
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(), "Some error occurred!!", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(SplashActivity.this);
        rQueue.add(request);
    }


    private void getAutho(){
        if(MyApp.instance.getPreference().get(Constants.LOGIN_INFO)==null){
            startActivity(new Intent(SplashActivity.this,LoginActivoty.class));
            finish();
        }else {
            getToken();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void CheckSDK23Permission() {
        List<String> permissionsNeeded = new ArrayList<String>();
        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("READ / WRITE SD CARD");
        if (!addPermission(permissionsList, Manifest.permission.READ_PHONE_STATE))
            permissionsNeeded.add("READPHONE");
        if (permissionsList.size() > 0) {
            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                    124);
        }else {
           getRespond();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionsList, String permission) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            if (!shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        getRespond();
    }

    @Override
    public void onGetTokenData(JSONObject object, int request_code) {
        if(object!=null){
            if(request_code==100){
                try {
                    JSONObject obj = object.getJSONObject("js");
                    token = obj.getString("token");
//                    Log.e("Token",token);
                    if(!token.equalsIgnoreCase("null")){
                        LoginModel loginModel = new LoginModel();
                        loginModel.setMac_address(mac);
                        loginModel.setPortal("aaa");
                        MyApp.instance.getPreference().put(Constants.APP_DOMAIN,Constants.GetUrl(this));
                        MyApp.instance.getPreference().put(Constants.LOGIN_INFO,loginModel);
                        MyApp.instance.getPreference().put(Constants.TOKEN,token);
                    }
//                    Url = Constants.GetUrl(this)+"portal.php?type=stb&action=get_profile&JsHttpRequest=1-xml";
                    GetProfileService profileService = new GetProfileService(SplashActivity.this);
                    profileService.getProfile(SplashActivity.this,Constants.GetUrl(this)+"portal.php?type=stb&action=get_profile&JsHttpRequest=1-xml",100);
                    profileService.OnGetProfilesData(SplashActivity.this);
                }catch (Exception e) {
                }
            }
        }else {
            Toast.makeText(SplashActivity.this,"Portal or mac address is wrong",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SplashActivity.this,LoginActivoty.class));
            finish();
        }
    }

    @Override
    public void OnGetProfilesData(JSONObject object, int request_code) {
        if(object!=null){
            if(request_code==100){
                ProfileModel profileModel = new ProfileModel();
                try {
                    JSONObject profile_obj = object.getJSONObject("js");
                    try {
                        profileModel.setId(profile_obj.getString("id"));
                    }catch (Exception e3){
                        profileModel.setId(profile_obj.getString("user_id"));
                    }
                    try {
                        profileModel.setPassword(profile_obj.getString("password"));
                    }catch (Exception e){
                        profileModel.setPassword("");
                    }
                    try {
                        profileModel.setUsername(profile_obj.getString("username"));
                    }catch (Exception e1){
                        profileModel.setUsername("");
                    }
                }catch (Exception e){
                    Log.e("error","parseError");
                }
                if(profileModel.getId()==null || profileModel.getId().isEmpty()|| profileModel.getId().equalsIgnoreCase("null")){
                    Toast.makeText(SplashActivity.this,"Portal or Mac address is wrong",Toast.LENGTH_SHORT).show();
                    MyApp.instance.getPreference().remove(Constants.LOGIN_INFO);
                    MyApp.instance.getPreference().remove(Constants.TOKEN);
                    MyApp.instance.getPreference().remove(Constants.APP_DOMAIN);
                    MyApp.instance.getPreference().remove(Constants.APP_PORTAL);
                    startActivity(new Intent(SplashActivity.this,LoginActivoty.class));
                    finish();
                    return;
                }
                MyApp.profileModel = profileModel;
                MyApp.user = profileModel.getUsername();
                MyApp.pass = profileModel.getPassword();
//                Utils.saveToFile(mac+"==="+portal);
//                Url = Constants.GetUrl(this)+"portal.php?type=itv&action=get_epg_info&period=5&JsHttpRequest=1-xml";
                GetDataService dataService = new GetDataService(SplashActivity.this);
                dataService.getData(SplashActivity.this,Constants.GetUrl(this)+"portal.php?type=itv&action=get_epg_info&period=5&JsHttpRequest=1-xml",100);
                dataService.onGetResultsData(SplashActivity.this);
            }
        }else {
            Toast.makeText(SplashActivity.this,"Portal or mac address is wrong",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SplashActivity.this,LoginActivoty.class));
            finish();
        }
    }

    @SuppressLint("StaticFieldLeak")
    class versionUpdate extends AsyncTask<String, Integer, String> {
        ProgressDialog mProgressDialog;
        File file;

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(SplashActivity.this);
            mProgressDialog.setMessage(getResources().getString(R.string.request_download));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(values[0]);
        }

        @Override
        protected String doInBackground(String... params) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                int fileLength = connection.getContentLength();
                input = connection.getInputStream();
                String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
                String fileName = "staler.apk";
                destination += fileName;
                final Uri uri = Uri.parse("file://" + destination);

                file = new File(destination);
                if(file.exists()){
                    file.delete();
                }
                output = new FileOutputStream(file, false);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    if (fileLength > 0)
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }
                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            mProgressDialog.dismiss();
            if (result != null) {
                Toast.makeText(getApplicationContext(),"Update Failed",Toast.LENGTH_LONG).show();
                getAutho();
            } else
                startInstall(file);
        }
    }

    private void startInstall(File fileName) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(FileProvider.getUriForFile(SplashActivity.this, BuildConfig.APPLICATION_ID + ".provider",fileName), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN){
            switch (event.getKeyCode()){
                case KeyEvent.KEYCODE_BACK:
                    if (!doubleBackToExitPressedOnce) {
                        this.doubleBackToExitPressedOnce = true;
                        Toast.makeText(this, "Please click BACK again to exit.", Toast.LENGTH_SHORT).show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                doubleBackToExitPressedOnce = false;
                            }
                        }, 2000);
                        return true;
                    }
                    finish();
                    System.exit(0);
                    android.os.Process.killProcess(android.os.Process.myPid());
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private void getRespond(){
        StringRequest request = new StringRequest(Constants.Get2(this), new Response.Listener<String>() {
            @Override
            public void onResponse(String string) {
                try {
                    JSONObject object = new JSONObject(string);
                    if (((String) object.get("status")).equalsIgnoreCase("success")) {
                        String url = (String) object.get("data");
                        if (url.startsWith("http://") || url.startsWith("https://")) {
                            if (url.endsWith("/")){
                                url = url;
                            }else {
                                url = url+"/";
                            }
                            JSONArray array = object.getJSONArray("images");
                            SharedPreferences.Editor server_editor = serveripdetails.edit();
                            server_editor.putString("ip", url);
                            server_editor.putString("icon_image",(String) array.get(0));
                            server_editor.putString("main_bg",(String )array.get(1));
                            server_editor.putString("login_image",(String) array.get(2));
                            server_editor.putString("ad1",(String)array.get(3));
                            server_editor.putString("ad2",(String)array.get(4));
                            version = object.getString("version");
                            app_Url = object.getString("appUrl");
                            server_editor.apply();
                            getUpdate();
                        } else {
                            Toast.makeText(SplashActivity.this, "Invalid Server URL!", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(SplashActivity.this, "Server Error!", Toast.LENGTH_SHORT).show();
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(), "Some error occurred!!", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(SplashActivity.this);
        rQueue.add(request);
    }
}
