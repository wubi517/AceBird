package com.kds.gold.acebird.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kds.gold.acebird.R;
import com.kds.gold.acebird.adapter.CategoryListAdapter;
import com.kds.gold.acebird.apps.Constants;
import com.kds.gold.acebird.apps.MyApp;
import com.kds.gold.acebird.dialog.AccountDlg;
import com.kds.gold.acebird.dialog.OSDDlg;
import com.kds.gold.acebird.dialog.ParentContrlDlg;
import com.kds.gold.acebird.dialog.PinDlg;
import com.kds.gold.acebird.dialog.ReloadDlg;
import com.kds.gold.acebird.dialog.SettingDlg;
import com.kds.gold.acebird.ijkplayer.activity.IjkChannelPlay;
import com.kds.gold.acebird.models.TvGenreModel;
import com.kds.gold.acebird.listner.SimpleGestureFilter;
import com.kds.gold.acebird.listner.SimpleGestureFilter.SimpleGestureListener;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WelcomeActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,View.OnClickListener,SimpleGestureListener {
    private SimpleGestureFilter detector;
    Context context = null;
    ImageView image_left, image_center, image_right,image_ad1,image_ad2,icon;
    ListView categroy_list;
    TextView txt_left, txt_center, txt_right, txt_date, txt_time;
    Button btn_left,btn_right;
    CategoryListAdapter categoryListAdapter;
    List<TvGenreModel> tvGenreModels;
    List<String > settingDatas;
    LinearLayout ly_center;
    int category_pos,vod_pos,series_pos;
    String nPortal,nMac;
    SimpleDateFormat date = new SimpleDateFormat("EEE,  dd  MMM, yyyy");
    SimpleDateFormat time = new SimpleDateFormat("HH:mm a");
    boolean is_center = false,doubleBackToExitPressedOnce = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        LinearLayout main_lay = findViewById(R.id.main_lay);
        Bitmap myImage = getBitmapFromURL(Constants.GetMainImage(WelcomeActivity.this));
        Drawable dr = new BitmapDrawable(myImage);
        main_lay.setBackgroundDrawable(dr);
        detector = new SimpleGestureFilter(WelcomeActivity.this, WelcomeActivity.this);
        context = this;
        MyApp.instance.is_welcome = true;
        settingDatas = new ArrayList<>();
        for (int i = 0; i < getResources().getStringArray(R.array.setting_list).length; i++) {
            settingDatas.add(getResources().getStringArray(R.array.setting_list)[i]);
        }

        image_ad1 = findViewById(R.id.image_ad1);
        image_ad2 = findViewById(R.id.image_ad2);
        icon = findViewById(R.id.icon);

        Picasso.with(this).load(Constants.GetIcon(this))
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .error(R.drawable.icon)
                .into(icon);

        Picasso.with(this).load(Constants.GetAd1(this))
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .error(R.drawable.ad1)
                .into(image_ad1);
        Picasso.with(this).load(Constants.GetAd2(this))
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .error(R.drawable.ad2)
                .into(image_ad2);

        ly_center = (LinearLayout)findViewById(R.id.ly_center);
        ly_center.setOnClickListener(this);
        txt_date = (TextView) findViewById(R.id.txt_date);
        txt_time = (TextView) findViewById(R.id.txt_time);
        image_left = (ImageView) findViewById(R.id.image_left);
        image_center = (ImageView) findViewById(R.id.image_center);
        image_right = (ImageView) findViewById(R.id.image_right);
        txt_left = (TextView) findViewById(R.id.txt_left);
        txt_right = (TextView) findViewById(R.id.txt_right);
        txt_center = (TextView) findViewById(R.id.txt_center);
        categroy_list = (ListView) findViewById(R.id.category_list);
        btn_left = (Button)findViewById(R.id.btn_left);
        btn_right = (Button)findViewById(R.id.btn_right);
        btn_left.setOnClickListener(this);
        btn_right.setOnClickListener(this);
        categroy_list.setOnItemClickListener(this);
        Thread myThread = null;
        Runnable runnable = new CountDownRunner();
        myThread = new Thread(runnable);
        myThread.start();
        switch (txt_center.getText().toString().toLowerCase()) {
            case "tv":
                if (MyApp.instance.getPreference().get(Constants.CHANNEL_POS) == null) {
                    category_pos = 0;
                } else {
                    category_pos = (int) MyApp.instance.getPreference().get(Constants.CHANNEL_POS);
                }
                tvGenreModels = MyApp.instance.tvGenreModelList;
                categoryListAdapter = new CategoryListAdapter(WelcomeActivity.this, tvGenreModels);
                categroy_list.setAdapter(categoryListAdapter);
                categoryListAdapter.selectItem(category_pos);
                categroy_list.setSelection(category_pos);
                break;
            case "tv series":
                if(MyApp.instance.getPreference().get(Constants.SERIES_POS)==null){
                    series_pos = 0;
                }else {
                    series_pos = (int)MyApp.instance.getPreference().get(Constants.SERIES_POS);
                }
                tvGenreModels = MyApp.instance.seriesGenreModelList;
                categoryListAdapter = new CategoryListAdapter(WelcomeActivity.this, tvGenreModels);
                categroy_list.setAdapter(categoryListAdapter);
                categoryListAdapter.selectItem(series_pos);
                categroy_list.setSelection(series_pos);
                break;
            case "radio":

                break;
            case "video club":
                if(MyApp.instance.getPreference().get(Constants.VOD_POS)==null){
                    vod_pos = 0;
                }else {
                    vod_pos = (int)MyApp.instance.getPreference().get(Constants.VOD_POS);
                }
                tvGenreModels = MyApp.instance.vodGenreModelList;
                categoryListAdapter = new CategoryListAdapter(WelcomeActivity.this, tvGenreModels);
                categroy_list.setAdapter(categoryListAdapter);
                categoryListAdapter.selectItem(vod_pos);
                categroy_list.setSelection(vod_pos);
                break;
        }
//        if(!getApplicationContext().getPackageName().equalsIgnoreCase(new String(Base64.decode(new String (Base64.decode("cy5nb2xkLY3k1bmIyeGtMWTI5dExtdGtjeTVuYjJ4a0xuUnlaWGc9".substring(9),Base64.DEFAULT)).substring(9),Base64.DEFAULT)))){
//            return;
//        }
        FullScreencall();
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
    protected void onResume(){
        MyApp.instance.is_welcome = true;
        super.onResume();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        View view = getCurrentFocus();
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
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
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    btn_left.setFocusable(false);
                    btn_right.setFocusable(false);
                    categroy_list.requestFocus();
                    RowRight();
                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    btn_left.setFocusable(false);
                    btn_right.setFocusable(false);
                    categroy_list.requestFocus();
                    RowLeft();
                    break;
                case KeyEvent.KEYCODE_DPAD_CENTER:
                    if(view==categroy_list){
                        switch (txt_center.getText().toString()){
                            case "tv":
                                is_center = true;
                                MyApp.instance.getPreference().put(Constants.CHANNEL_POS,category_pos);
                                if(tvGenreModels.get(category_pos).getTitle().toLowerCase().contains("adult")){
                                    PinDlg pinDlg = new PinDlg(WelcomeActivity.this, new PinDlg.DlgPinListener() {
                                        @Override
                                        public void OnYesClick(Dialog dialog, String pin_code) {
                                            String pin = (String )MyApp.instance.getPreference().get(Constants.PIN_CODE);
                                            if(pin_code.equalsIgnoreCase(pin)){
                                                dialog.dismiss();
                                                if(MyApp.instance.getPreference().get(Constants.IS_PHONE)==null){
                                                    startActivity(new Intent(WelcomeActivity.this,IjkChannelPlay.class));
                                                }else {
                                                    startActivity(new Intent(WelcomeActivity.this,PreviewChannelPhoneAcitivoty.class));
                                                }

                                            }else {
                                                dialog.dismiss();
                                                Toast.makeText(WelcomeActivity.this, "Your Pin code was incorrect. Please try again", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                        @Override
                                        public void OnCancelClick(Dialog dialog, String pin_code) {
                                            dialog.dismiss();
                                        }
                                    });
                                    pinDlg.show();
                                }else {
                                    if(MyApp.instance.getPreference().get(Constants.IS_PHONE)==null){
                                        startActivity(new Intent(WelcomeActivity.this,IjkChannelPlay.class));
                                    }else {
                                        startActivity(new Intent(WelcomeActivity.this,PreviewChannelPhoneAcitivoty.class));
                                    }
                                }
                                break;
                            case "video club":
                                is_center = true;
                                MyApp.instance.getPreference().put(Constants.VOD_POS,category_pos);
                                startActivity(new Intent(this,PreviewVodActivity.class));
                                break;
                            case "tv series":
                                is_center = true;
                                MyApp.instance.getPreference().put(Constants.SERIES_POS,category_pos);
                                startActivity(new Intent(this,PreviewSeriesActivity.class));
                                break;
                        }

                    }else {
                        if(txt_center.getText().toString().equalsIgnoreCase("setting")){
                            SettingDlg settingDlg = new SettingDlg(WelcomeActivity.this, settingDatas, new SettingDlg.DialogSettingListner() {
                                @Override
                                public void OnItemClick(Dialog dialog, int position) {
                                    switch (position){
                                        case 0:
                                            ParentContrlDlg dlg = new ParentContrlDlg(WelcomeActivity.this, new ParentContrlDlg.DialogUpdateListener() {
                                                @Override
                                                public void OnUpdateNowClick(Dialog dialog, int code) {
                                                    if(code==1){
                                                        dialog.dismiss();
                                                    }
                                                }
                                                @Override
                                                public void OnUpdateSkipClick(Dialog dialog, int code) {
                                                    dialog.dismiss();
                                                }
                                            });
                                            dlg.show();
                                            break;
                                        case 1:
                                            ReloadDlg();
                                            break;
                                        case 2:
                                            try {
                                                Runtime.getRuntime().exec(new String[]{"/system/bin/su","-c","reboot now"});
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            break;
                                        case 3:
                                            OSDDlg osdDlg = new OSDDlg(WelcomeActivity.this, new OSDDlg.EpisodeDlgListener() {
                                                @Override
                                                public void OnYesClick(Dialog dialog, int episode_num) {
                                                    MyApp.instance.getPreference().put(Constants.OSD_TIME,episode_num);
                                                }
                                            });
                                            osdDlg.show();
                                            break;
                                        case 4:
                                            AccountDlg accountDlg = new AccountDlg(WelcomeActivity.this, new AccountDlg.DialogMacListener() {
                                                @Override
                                                public void OnYesClick(Dialog dialog) {
                                                }
                                            });
                                            accountDlg.show();
                                            break;
                                        case 5:
                                            MyApp.instance.getPreference().remove(Constants.LOGIN_INFO);
                                            MyApp.instance.getPreference().remove(Constants.CHANNEL_POS);
                                            MyApp.instance.getPreference().remove(Constants.POSITION_MODEL);
                                            MyApp.instance.getPreference().remove(Constants.SERIES_POS);
                                            MyApp.instance.getPreference().remove(Constants.VOD_POS);
                                            startActivity(new Intent(WelcomeActivity.this,LoginActivoty.class));
                                            finish();
                                            break;
                                    }
                                }
                            });
                            settingDlg.show();
                        }else if(txt_center.getText().toString().equalsIgnoreCase("radio")){
                            startActivity(new Intent(this,RadioActivity.class));
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if(view==categroy_list){
                        if(category_pos<tvGenreModels.size()-1){
                            categroy_list.setSelection(category_pos);
                            category_pos++;
                            categoryListAdapter.selectItem(category_pos);
                        }else {
                            category_pos = 0;
                            categroy_list.setSelection(category_pos);
                            categoryListAdapter.selectItem(category_pos);
                            return true;
                        }
                    }else if(view==btn_left || view== btn_right){
                        if(category_pos<tvGenreModels.size()-1){
                            categroy_list.setSelection(category_pos);
                            category_pos++;
                            categoryListAdapter.selectItem(category_pos);
                        }else {
                            category_pos = 0;
                            categroy_list.setSelection(category_pos);
                            categoryListAdapter.selectItem(category_pos);
                            return true;
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_UP:
                    if(view==categroy_list){
                        if(category_pos>0){
                            categroy_list.setSelection(category_pos);
                            category_pos--;
                            categoryListAdapter.selectItem(category_pos);
                        }else {
                            categroy_list.setSelection(tvGenreModels.size()-1);
                            category_pos = tvGenreModels.size()-1;
                            categoryListAdapter.selectItem(category_pos);
                            return true;
                        }
                    }else if(view==btn_left || view== btn_right){
                        if(category_pos>0){
                            categroy_list.setSelection(category_pos);
                            category_pos--;
                            categoryListAdapter.selectItem(category_pos);
                        }else {
                            categroy_list.setSelection(tvGenreModels.size()-1);
                            category_pos = tvGenreModels.size()-1;
                            categoryListAdapter.selectItem(category_pos);
                            return true;
                        }
                    }
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent me) {
//        Log.e("last_pos",String .valueOf(channel_list.getLastVisiblePosition()));
        this.detector.onTouchEvent(me);
        return super.dispatchTouchEvent(me);
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (txt_center.getText().toString().toLowerCase()) {
            case "tv":
                    category_pos = position;
                    MyApp.instance.getPreference().put(Constants.CHANNEL_POS,position);
                    if(tvGenreModels.get(category_pos).getTitle().toLowerCase().contains("adult")){
                        PinDlg pinDlg = new PinDlg(WelcomeActivity.this, new PinDlg.DlgPinListener() {
                            @Override
                            public void OnYesClick(Dialog dialog, String pin_code) {
                                String pin = (String )MyApp.instance.getPreference().get(Constants.PIN_CODE);
                                if(pin_code.equalsIgnoreCase(pin)){
                                    dialog.dismiss();
                                    if(MyApp.instance.getPreference().get(Constants.IS_PHONE)==null){
                                        startActivity(new Intent(WelcomeActivity.this,IjkChannelPlay.class));
                                    }else {
                                        startActivity(new Intent(WelcomeActivity.this,PreviewChannelPhoneAcitivoty.class));
                                    }
                                }else {
                                    Toast.makeText(WelcomeActivity.this, "Your Pin code was incorrect. Please try again", Toast.LENGTH_LONG).show();
                                }
                            }
                            @Override
                            public void OnCancelClick(Dialog dialog, String pin_code) {
                                dialog.dismiss();
                            }
                        });
                        pinDlg.show();
                    }else {
                        if(MyApp.instance.getPreference().get(Constants.IS_PHONE)==null){
                            startActivity(new Intent(WelcomeActivity.this,IjkChannelPlay.class));
                        }else {
                            startActivity(new Intent(WelcomeActivity.this,PreviewChannelPhoneAcitivoty.class));
                        }
                    }
                    categoryListAdapter.selectItem(position);
                break;
            case "tv series":
                is_center = true;
                category_pos = position;
                MyApp.instance.getPreference().put(Constants.SERIES_POS,category_pos);
                categoryListAdapter.selectItem(position);
                startActivity(new Intent(this,PreviewSeriesActivity.class));
                break;
            case "video club":
                is_center = true;
                category_pos = position;
                MyApp.instance.getPreference().put(Constants.VOD_POS,category_pos);
                categoryListAdapter.selectItem(position);
                startActivity(new Intent(this,PreviewVodActivity.class));
                break;
            case "radio":
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_left:
                RowLeft();
                break;
            case R.id.btn_right:
                RowRight();
                break;
            case R.id.ly_center:
                if(txt_center.getText().toString().equalsIgnoreCase("setting")){
                    SettingDlg settingDlg = new SettingDlg(WelcomeActivity.this, settingDatas, new SettingDlg.DialogSettingListner() {
                        @Override
                        public void OnItemClick(Dialog dialog, int position) {
                            switch (position){
                                case 0:
                                    ParentContrlDlg dlg = new ParentContrlDlg(WelcomeActivity.this, new ParentContrlDlg.DialogUpdateListener() {
                                        @Override
                                        public void OnUpdateNowClick(Dialog dialog, int code) {
                                            if(code==1){
                                                dialog.dismiss();
                                            }
                                        }
                                        @Override
                                        public void OnUpdateSkipClick(Dialog dialog, int code) {
                                            dialog.dismiss();
                                        }
                                    });
                                    dlg.show();
                                    break;
                                case 1:
                                    ReloadDlg();
                                    break;
                                case 2:
                                    try {
                                        Runtime.getRuntime().exec(new String[]{"/system/bin/su","-c","reboot now"});
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                case 3:
                                    OSDDlg osdDlg = new OSDDlg(WelcomeActivity.this, new OSDDlg.EpisodeDlgListener() {
                                        @Override
                                        public void OnYesClick(Dialog dialog, int episode_num) {
                                            MyApp.instance.getPreference().put(Constants.OSD_TIME,episode_num);
                                        }
                                    });
                                    osdDlg.show();
                                    break;
                                case 4:
                                    AccountDlg accountDlg = new AccountDlg(WelcomeActivity.this, new AccountDlg.DialogMacListener() {
                                        @Override
                                        public void OnYesClick(Dialog dialog) {
                                        }
                                    });
                                    accountDlg.show();
                                    break;
                                case 5:
                                    MyApp.instance.getPreference().remove(Constants.LOGIN_INFO);
                                    MyApp.instance.getPreference().remove(Constants.CHANNEL_POS);
                                    MyApp.instance.getPreference().remove(Constants.POSITION_MODEL);
                                    MyApp.instance.getPreference().remove(Constants.SERIES_POS);
                                    MyApp.instance.getPreference().remove(Constants.VOD_POS);
                                    startActivity(new Intent(WelcomeActivity.this,LoginActivoty.class));
                                    finish();
                            }
                        }
                    });
                    settingDlg.show();
                }else if(txt_center.getText().toString().equalsIgnoreCase("radio")){
                    startActivity(new Intent(this,RadioActivity.class));
                }
                break;
        }
    }

    @Override
    public void onSwipe(int direction) {
        switch (direction) {
            case SimpleGestureFilter.SWIPE_RIGHT:
                RowLeft();
                break;
            case SimpleGestureFilter.SWIPE_LEFT:
                RowRight();
                break;
        }
    }

    @Override
    public void onDoubleTap() {

    }

    class CountDownRunner implements Runnable {
        // @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    doWork();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                }
            }
        }
    }

    public void doWork() {
        runOnUiThread(new Runnable() {
            public void run() {
                try {
                    txt_time.setText(time.format(new Date()));
                    txt_date.setText(date.format(new Date()));
                } catch (Exception e) {
                }
            }
        });
    }

    private void  RowLeft(){
        switch (txt_center.getText().toString().toLowerCase()) {
            case "tv":
                if(MyApp.instance.getPreference().get(Constants.VOD_POS)==null){
                    vod_pos = 0;
                }else {
                    vod_pos = (int)MyApp.instance.getPreference().get(Constants.VOD_POS);
                }
                tvGenreModels = MyApp.instance.vodGenreModelList;
                categoryListAdapter = new CategoryListAdapter(WelcomeActivity.this, tvGenreModels);
                categroy_list.setAdapter(categoryListAdapter);
                categoryListAdapter.selectItem(vod_pos);
                categroy_list.setSelection(vod_pos);
                image_center.setImageResource(R.drawable.video_club);
                txt_center.setText("VIDEO CLUB");
                image_left.setImageResource(R.drawable.setting);
                txt_left.setText("SETTING");
                image_right.setImageResource(R.drawable.tv_icon);
                txt_right.setText("TV");
                break;
            case "tv series":
                if (MyApp.instance.getPreference().get(Constants.CHANNEL_POS) == null) {
                    category_pos = 0;
                } else {
                    category_pos = (int) MyApp.instance.getPreference().get(Constants.CHANNEL_POS);
                }
                tvGenreModels = MyApp.instance.tvGenreModelList;
                categoryListAdapter = new CategoryListAdapter(WelcomeActivity.this, tvGenreModels);
                categroy_list.setAdapter(categoryListAdapter);
                categoryListAdapter.selectItem(category_pos);
                categroy_list.setSelection(category_pos);
                image_center.setImageResource(R.drawable.tv_icon);
                txt_center.setText("TV");
                image_right.setImageResource(R.drawable.tv_serires);
                txt_right.setText("TV SERIES");
                image_left.setImageResource(R.drawable.video_club);
                txt_left.setText("VIDEO CLUB");
                break;
            case "radio":
                if (MyApp.instance.getPreference().get(Constants.SERIES_POS) == null) {
                    category_pos = 0;
                } else {
                    category_pos = (int) MyApp.instance.getPreference().get(Constants.SERIES_POS);
                }
                tvGenreModels = MyApp.instance.seriesGenreModelList;
                categoryListAdapter = new CategoryListAdapter(WelcomeActivity.this, tvGenreModels);
                categroy_list.setAdapter(categoryListAdapter);
                categoryListAdapter.selectItem(category_pos);
                categroy_list.setSelection(category_pos);
//                            image_center.setPadding(Utils.dp2px(this,85),Utils.dp2px(this,8),Utils.dp2px(this,85),Utils.dp2px(this,0));
                image_center.setImageResource(R.drawable.tv_serires);
                txt_center.setText("TV SERIES");
                image_right.setImageResource(R.drawable.radio);
                txt_right.setText("RADIO");
                image_left.setImageResource(R.drawable.tv_icon);
                txt_left.setText("TV");
                break;
            case "setting":
                image_center.setImageResource(R.drawable.radio);
                txt_center.setText("RADIO");
                image_right.setImageResource(R.drawable.setting);
                txt_right.setText("SETTING");
                image_left.setImageResource(R.drawable.tv_serires);
                txt_left.setText("TV SERIES");
                break;
            case "video club":
                tvGenreModels = new ArrayList<>();
                categoryListAdapter = new CategoryListAdapter(WelcomeActivity.this, tvGenreModels);
                categroy_list.setAdapter(categoryListAdapter);
                image_center.setImageResource(R.drawable.setting);
                image_right.setImageResource(R.drawable.video_club);
                image_left.setImageResource(R.drawable.radio);
                txt_center.setText("SETTING");
                txt_right.setText("VIDEO CLUB");
                txt_left.setText("RADIO");
                break;

        }
    }
    private void  RowRight(){
        switch (txt_center.getText().toString().toLowerCase()) {
            case "tv":
                if (MyApp.instance.getPreference().get(Constants.SERIES_POS) == null) {
                    category_pos = 0;
                } else {
                    category_pos = (int) MyApp.instance.getPreference().get(Constants.SERIES_POS);
                }
                tvGenreModels = MyApp.instance.seriesGenreModelList;
                categoryListAdapter = new CategoryListAdapter(WelcomeActivity.this, tvGenreModels);
                categroy_list.setAdapter(categoryListAdapter);
                categoryListAdapter.selectItem(category_pos);
                categroy_list.setSelection(category_pos);
//                            categoryListAdapter.selectItem(category_pos);
//                            image_center.setPadding(Utils.dp2px(this,85),Utils.dp2px(this,18),Utils.dp2px(this,85),Utils.dp2px(this,0));
                image_center.setImageResource(R.drawable.tv_serires);
                txt_center.setText("TV SERIES");
                image_right.setImageResource(R.drawable.radio);
                txt_right.setText("RADIO");
                image_left.setImageResource(R.drawable.tv_icon);
                txt_left.setText("TV");
                break;
            case "tv series":
                if (MyApp.instance.getPreference().get(Constants.SERIES_POS) == null) {
                    category_pos = 0;
                } else {
                    category_pos = (int) MyApp.instance.getPreference().get(Constants.SERIES_POS);
                }
                tvGenreModels = new ArrayList<>();
                categoryListAdapter = new CategoryListAdapter(WelcomeActivity.this, tvGenreModels);
                categroy_list.setAdapter(categoryListAdapter);
                categoryListAdapter.selectItem(category_pos);
                categroy_list.setSelection(category_pos);
                image_center.setImageResource(R.drawable.radio);
                txt_center.setText("RADIO");
                image_right.setImageResource(R.drawable.setting);
                txt_right.setText("SETTING");
                image_left.setImageResource(R.drawable.tv_serires);
                txt_left.setText("TV SERIES");
                break;
            case "radio":
                image_center.setImageResource(R.drawable.setting);
                txt_center.setText("SETTING");
                image_right.setImageResource(R.drawable.video_club);
                txt_right.setText("VIDEO CLUB");
                image_left.setImageResource(R.drawable.radio);
                txt_left.setText("RADIO");
                break;
            case "video club":
                if (MyApp.instance.getPreference().get(Constants.CHANNEL_POS) == null) {
                    category_pos = 0;
                } else {
                    category_pos = (int) MyApp.instance.getPreference().get(Constants.CHANNEL_POS);
                }
                tvGenreModels = MyApp.instance.tvGenreModelList;
                categoryListAdapter = new CategoryListAdapter(WelcomeActivity.this, tvGenreModels);
                categroy_list.setAdapter(categoryListAdapter);
                categoryListAdapter.selectItem(category_pos);
                categroy_list.setSelection(category_pos);
                image_center.setImageResource(R.drawable.tv_icon);
                txt_center.setText("TV");
                image_right.setImageResource(R.drawable.tv_serires);
                txt_right.setText("TV SERIES");
                image_left.setImageResource(R.drawable.video_club);
                txt_left.setText("VIDEO CLUB");
                break;
            case "setting":
                if(MyApp.instance.getPreference().get(Constants.VOD_POS)==null){
                    vod_pos = 0;
                }else {
                    vod_pos = (int)MyApp.instance.getPreference().get(Constants.VOD_POS);
                }
                tvGenreModels = MyApp.instance.vodGenreModelList;
                categoryListAdapter = new CategoryListAdapter(WelcomeActivity.this, tvGenreModels);
                categroy_list.setAdapter(categoryListAdapter);
                categoryListAdapter.selectItem(vod_pos);
                categroy_list.setSelection(vod_pos);
                image_center.setImageResource(R.drawable.video_club);
                image_left.setImageResource(R.drawable.setting);
                image_right.setImageResource(R.drawable.tv_icon);
                txt_center.setText("VIDEO CLUB");
                txt_left.setText("SETTING");
                txt_right.setText("TV");
                break;
        }
    }

    private void ReloadDlg(){
        ReloadDlg reloadDlg = new ReloadDlg(WelcomeActivity.this, new ReloadDlg.DialogUpdateListener() {
            @Override
            public void OnUpdateNowClick(Dialog dialog) {
                dialog.dismiss();
                startActivity(new Intent(WelcomeActivity.this,SplashActivity.class));
                finish();
            }

            @Override
            public void OnUpdateSkipClick(Dialog dialog) {
                dialog.dismiss();
            }
        });
        reloadDlg.show();
    }

    public void FullScreencall() {
        if(Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else  {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
}
