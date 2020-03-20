package com.kds.gold.acebird.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Selection;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kds.gold.acebird.R;
import com.kds.gold.acebird.apps.Constants;
import com.kds.gold.acebird.apps.MyApp;
import com.kds.gold.acebird.async.GetDataService;
import com.kds.gold.acebird.async.GetProfileService;
import com.kds.gold.acebird.async.GetTokenService;
import com.kds.gold.acebird.models.ChannelModel;
import com.kds.gold.acebird.models.LoginModel;
import com.kds.gold.acebird.models.ProfileModel;
import com.kds.gold.acebird.models.TvGenreModel;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivoty extends AppCompatActivity implements View.OnClickListener,GetTokenService.OnGetTokenResultListner,
        GetProfileService.OnGetProfilesListner,GetDataService.OnGetResultsListener{
    String token,mac,Url;
    List<TvGenreModel> tvGenreModelList;
    List<TvGenreModel>vodGenreModelList;
    List<TvGenreModel>seriesGenreModelList;
    List<ChannelModel>channelModelList;
    ChannelModel channelModel;
    ProgressBar progressBar;
    TextView txt_version,txt_mac_adress;
    EditText txt_portal,txt_mac;
    Button btn_login;
    CheckBox checkBox;
    private ImageView icon;
    int msgs,additional_services_on;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        RelativeLayout main_lay = findViewById(R.id.main_lay);
        Bitmap myImage = getBitmapFromURL(Constants.GetLoginImage(LoginActivoty.this));
        Drawable dr = new BitmapDrawable(myImage);
        main_lay.setBackgroundDrawable(dr);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        txt_version = (TextView)findViewById(R.id.txt_version);
        txt_version.setText("v"+MyApp.version_name);
        if(txt_version.getVisibility()== View.GONE){
            MyApp.instance.getPreference().put(Constants.IS_PHONE,"is_phone");
        }
        icon = findViewById(R.id.icon);
        try {
            Picasso.with(this).load(Constants.GetIcon(this)).into(icon);
        }catch (Exception e){
            Picasso.with(this).load(R.drawable.icon).into(icon);
        }
        txt_mac_adress = (TextView)findViewById(R.id.login_mac_address);
        txt_mac_adress.setText(MyApp.mac_address.toUpperCase());
        txt_portal = (EditText)findViewById(R.id.txt_portal);
        txt_portal.setText("http://");
        Selection.setSelection(txt_portal.getText(), txt_portal.getText().length());
        txt_mac = (EditText)findViewById(R.id.txt_mac);
        txt_mac.setText(MyApp.mac_address.toUpperCase());
        Selection.setSelection(txt_mac.getText(), txt_mac.getText().length());
        btn_login = (Button)findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);
        checkBox = (CheckBox)findViewById(R.id.checkbox);
        checkBox.setOnClickListener(this);
    }
    public Bitmap getBitmapFromURL(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_login:
                if(txt_mac.getText().toString().isEmpty()){
                    Toast.makeText(this,"Mac address can not be blank",Toast.LENGTH_SHORT).show();
                    return;
                }
//                if(!getApplicationContext().getPackageName().equalsIgnoreCase(new String(Base64.decode(new String (Base64.decode("cy5nb2xkLY3k1bmIyeGtMWTI5dExtdGtjeTVuYjJ4a0xuUnlaWGc9".substring(9),Base64.DEFAULT)).substring(9),Base64.DEFAULT)))){
//                    return;
//                }
                mac = txt_mac.getText().toString().replaceAll("\\s+$", "");
                mac = mac.replaceAll("\\s+$", "");
                LoginModel loginModel = new LoginModel();
                loginModel.setMac_address(mac);
                loginModel.setPortal("aaa");
                MyApp.instance.getPreference().put(Constants.APP_DOMAIN,Constants.GetUrl(this));
                MyApp.instance.getPreference().put(Constants.APP_PORTAL,Constants.GetUrl(this)+"c/");
                MyApp.instance.getPreference().put(Constants.LOGIN_INFO,loginModel);

                progressBar.setVisibility(View.VISIBLE);
                GetTokenService getTokenService = new GetTokenService(this);
                getTokenService.getData(this,Constants.GetUrl(this)+"portal.php?action=handshake&type=stb&token=&JsHttpRequest=1-xml",100);
                getTokenService.onGetTokenData(LoginActivoty.this);
                break;
        }
    }
    @Override
    public void onGetTokenData(JSONObject object, int request_code) {
        if(object!=null){
            if(request_code==100){
                try {
                    JSONObject obj = object.getJSONObject("js");
                    token = obj.getString("token");
                    if(!token.equalsIgnoreCase("null")){
                        LoginModel loginModel = new LoginModel();
                        loginModel.setMac_address(mac);
                        loginModel.setPortal(Constants.GetUrl(this));
//                        Log.e("Token",token);
                        MyApp.instance.getPreference().remove(Constants.LOGIN_INFO);
                        MyApp.instance.getPreference().put(Constants.APP_DOMAIN,Constants.GetUrl(this));
                        MyApp.instance.getPreference().put(Constants.APP_PORTAL,Constants.GetUrl(this)+"c/");
                        MyApp.instance.getPreference().put(Constants.LOGIN_INFO,loginModel);
                        MyApp.instance.getPreference().put(Constants.TOKEN,token);
//                        Url = Constants.GetUrl(this)+"portal.php?type=stb&action=get_profile&JsHttpRequest=1-xml";
                        GetProfileService profileService = new GetProfileService(LoginActivoty.this);
                        profileService.getProfile(LoginActivoty.this,Constants.GetUrl(this)+"portal.php?type=stb&action=get_profile&JsHttpRequest=1-xml",100);
                        profileService.OnGetProfilesData(LoginActivoty.this);
                    }else {
                        Toast.makeText(LoginActivoty.this,"Mac address is incorrect.",Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e) {
                }
            }
        }else {
            Toast.makeText(LoginActivoty.this,"Mac address is wrong",Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
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
                if(profileModel.getId()==null || profileModel.getId().isEmpty() || profileModel.getId().equalsIgnoreCase("null")){
                    MyApp.instance.getPreference().remove(Constants.LOGIN_INFO);
                    MyApp.instance.getPreference().remove(Constants.TOKEN);
                    MyApp.instance.getPreference().remove(Constants.APP_DOMAIN);
                    MyApp.instance.getPreference().remove(Constants.APP_PORTAL);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(LoginActivoty.this,"Mac address is wrong",Toast.LENGTH_SHORT).show();
                    return;
                }
                MyApp.profileModel = profileModel;
                MyApp.user = profileModel.getUsername();
                MyApp.pass = profileModel.getPassword();
//                Url = Constants.GetUrl(this)+"portal.php?type=itv&action=get_epg_info&period=5&JsHttpRequest=1-xml";
                GetDataService dataService = new GetDataService(LoginActivoty.this);
                dataService.getData(LoginActivoty.this,Constants.GetUrl(this)+"portal.php?type=itv&action=get_epg_info&period=5&JsHttpRequest=1-xml",100);
                dataService.onGetResultsData(LoginActivoty.this);
            }
        }else {
            Toast.makeText(LoginActivoty.this,"Portal or mac address is wrong",Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onGetResultsData(JSONObject object, int request_code) {
        if(object!=null){
            if(request_code==100){
//                Url = Constants.GetUrl(this)+"portal.php?type=itv&action=get_all_channels&force_ch_link_check=&JsHttpRequest=1-xml";
                GetDataService dataService = new GetDataService(LoginActivoty.this);
                dataService.getData(LoginActivoty.this,Constants.GetUrl(this)+"portal.php?type=itv&action=get_all_channels&force_ch_link_check=&JsHttpRequest=1-xml",200);
                dataService.onGetResultsData(LoginActivoty.this);
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
                    MyApp.instance.channel_size = channelModelList.size();
                    Map back_map = new HashMap();
                    back_map.put("channels", channelModelList);
                    MyApp.instance.back_map = back_map;
                    MyApp.instance.channelModelList  = channelModelList;
                }catch (Exception e){
                }
//                Url = Constants.GetUrl(this)+"portal.php?type=itv&action=get_all_fav_channels&fav=1&force_ch_link_check=&JsHttpRequest=1-xml";
                GetDataService dataService = new GetDataService(LoginActivoty.this);
                dataService.getData(LoginActivoty.this,Constants.GetUrl(this)+"portal.php?type=itv&action=get_all_fav_channels&fav=1&force_ch_link_check=&JsHttpRequest=1-xml",300);
                dataService.onGetResultsData(LoginActivoty.this);
            }else if(request_code==300){
                // this is fav channels
//                Url = Constants.GetUrl(this)+"portal.php?type=itv&action=get_fav_ids&force_ch_link_check=&JsHttpRequest=1-xml";
                GetDataService dataService = new GetDataService(LoginActivoty.this);
                dataService.getData(LoginActivoty.this,Constants.GetUrl(this)+"portal.php?type=itv&action=get_fav_ids&force_ch_link_check=&JsHttpRequest=1-xml",400);
                dataService.onGetResultsData(LoginActivoty.this);
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
                GetDataService dataService = new GetDataService(LoginActivoty.this);
                dataService.getData(LoginActivoty.this,Constants.GetUrl(this)+"portal.php?type=itv&action=get_genres&JsHttpRequest=1-xml",600);
                dataService.onGetResultsData(LoginActivoty.this);
            }else if(request_code==500){
                try {
                    JSONObject data_obj = object.getJSONObject("js");
                    msgs = data_obj.getInt("msgs");
                    additional_services_on = data_obj.getInt("additional_services_on");
                    Log.e("msgs",String .valueOf(msgs));
                }catch (Exception e){

                }
//                Url = Constants.GetUrl(this)+"portal.php?type=itv&action=get_genres&JsHttpRequest=1-xml";
                GetDataService dataService = new GetDataService(LoginActivoty.this);
                dataService.getData(LoginActivoty.this,Constants.GetUrl(this)+"portal.php?type=itv&action=get_genres&JsHttpRequest=1-xml",600);
                dataService.onGetResultsData(LoginActivoty.this);
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
                MyApp.instance.tvGenreModelList = tvGenreModelList;
//                Url = Constants.GetUrl(this)+"portal.php?type=vod&action=get_categories&JsHttpRequest=1-xml";
                GetDataService dataService = new GetDataService(LoginActivoty.this);
                dataService.getData(LoginActivoty.this,Constants.GetUrl(this)+"portal.php?type=vod&action=get_categories&JsHttpRequest=1-xml",700);
                dataService.onGetResultsData(LoginActivoty.this);
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
                            }catch (Exception e1){
                            }
                            vodGenreModelList.add(tvGenreModel);
                        }
                    }
                }catch (Exception e){
                }
                MyApp.instance.vodGenreModelList = vodGenreModelList;
//                Url = Constants.GetUrl(this)+"portal.php?type=series&action=get_categories&JsHttpRequest=1-xml";
                GetDataService dataService = new GetDataService(LoginActivoty.this);
                dataService.getData(LoginActivoty.this,Constants.GetUrl(this)+"portal.php?type=series&action=get_categories&JsHttpRequest=1-xml",800);
                dataService.onGetResultsData(LoginActivoty.this);
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
                MyApp.instance.seriesGenreModelList = seriesGenreModelList;
//                Url = Constants.GetUrl(this)+"portal.php?type=account_info&action=get_main_info&JsHttpRequest=1-xml";
                GetDataService dataService = new GetDataService(LoginActivoty.this);
                dataService.getData(LoginActivoty.this,Constants.GetUrl(this)+"portal.php?type=account_info&action=get_main_info&JsHttpRequest=1-xml",900);
                dataService.onGetResultsData(LoginActivoty.this);
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
                GetDataService dataService = new GetDataService(LoginActivoty.this);
                dataService.getData(LoginActivoty.this,Constants.GetUrl(this)+"portal.php?type=radio&action=get_fav_ids&JsHttpRequest=1-xml",1000);
                dataService.onGetResultsData(LoginActivoty.this);
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
        StringRequest request = new StringRequest(Constants.GetAutho2(this), new Response.Listener<String>() {
            @Override
            public void onResponse(String string) {
                try {
                    JSONObject object = new JSONObject(string);
                    if (((String) object.get("status")).equalsIgnoreCase("success")) {
                        startActivity(new Intent(LoginActivoty.this,WelcomeActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivoty.this, "Server Error!", Toast.LENGTH_SHORT).show();
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

        RequestQueue rQueue = Volley.newRequestQueue(LoginActivoty.this);
        rQueue.add(request);
    }
}
