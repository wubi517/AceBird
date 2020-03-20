package com.kds.gold.acebird.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kds.gold.acebird.R;
import com.kds.gold.acebird.adapter.DateListAdapter;
import com.kds.gold.acebird.adapter.EpgListAdapter;
import com.kds.gold.acebird.apps.Constants;
import com.kds.gold.acebird.apps.MyApp;
import com.kds.gold.acebird.async.GetDataService;
import com.kds.gold.acebird.models.EpgModel;
import com.kds.gold.acebird.listner.SimpleGestureFilter;
import com.kds.gold.acebird.listner.SimpleGestureFilter.SimpleGestureListener;

import org.json.JSONArray;
import org.json.JSONObject;
import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class GuideActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,View.OnClickListener,
        GetDataService.OnGetResultsListener,SimpleGestureListener,SurfaceHolder.Callback,IVLCVout.Callback{
    private SimpleGestureFilter detector;
    Context context = null;
    private static final String TAG = "PlayerActivity";
    private static final int REQUEST_READ_EXTERNAL_STORAGE_PERMISSION = 1;
    private MediaPlayer mMediaPlayer = null;
    private int mVideoWidth;
    private int mVideoHeight;
    Button btn_rewind,btn_play,btn_forward;
    LibVLC libvlc;
    private SurfaceView surfaceView;
    private SurfaceHolder holder;
    SeekBar seekbar;
    ProgressBar progressBar;
    LinearLayout def_lay,ly_bottom;
    List<EpgModel>epgModels;
    ListView date_list, epg_list;
    DateListAdapter dateListAdapter;
    EpgListAdapter epgListAdapter;
    List<String > date_datas;
    String mStream_id,token,epg_date,contentUri,channel_name,start_time,stream;
    TextView txt_epg_data,txt_channel,txt_time,txt_progress,txt_title,txt_dec,channel_title,txt_date,txt_time_passed,txt_remain_time,txt_last_time,
             txt_current_dec,txt_next_dec;
    ImageView btn_back;
    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat sdf1 = new SimpleDateFormat("MM-dd HH:mm");
    SimpleDateFormat time = new SimpleDateFormat("HH:mm a");
    SimpleDateFormat catch_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat catchupFormat = new SimpleDateFormat("yyyy-MM-dd:HH-mm");
    int page,total_items,page_nums,selected_num,duration,osd_time;
    boolean is_up = false, is_create = true,item_sel = false,is_start = false;
    Handler mHandler = new Handler();
    Runnable mTicker;
    long current_time,startMill;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .penaltyLog()
                .detectAll()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .penaltyLog()
                .detectAll()
                .build());
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        osd_time = (int) MyApp.instance.getPreference().get(Constants.OSD_TIME);
        detector = new SimpleGestureFilter(GuideActivity.this, GuideActivity.this);
        context = this;
        date_datas = new ArrayList<>();
        for(int i = -6;i<1;i++){
            long milisecond = System.currentTimeMillis() + i*24*3600*1000;
            date_datas.add(getFromDate(milisecond));
        }
//        if(!getApplicationContext().getPackageName().equalsIgnoreCase(new String(Base64.decode(new String (Base64.decode("cy5nb2xkLY3k1bmIyeGtMWTI5dExtdGtjeTVuYjJ4a0xuUnlaWGc9".substring(9),Base64.DEFAULT)).substring(9),Base64.DEFAULT)))){
//            return;
//        }
        seekbar = (SeekBar)findViewById(R.id.seekbar);
        seekbar.setMax(100);
        txt_title = (TextView)findViewById(R.id.txt_title);
        txt_dec = (TextView)findViewById(R.id.txt_dec);
        channel_title = (TextView)findViewById(R.id.channel_title);
        txt_date = (TextView)findViewById(R.id.txt_date);
        txt_time_passed = (TextView)findViewById(R.id.txt_time_passed);
        txt_remain_time = (TextView)findViewById(R.id.txt_remain_time);
        txt_last_time = (TextView)findViewById(R.id.txt_last_time);
        txt_current_dec = (TextView)findViewById(R.id.txt_current_dec);
        txt_next_dec = (TextView)findViewById(R.id.txt_next_dec);
        surfaceView = (SurfaceView)findViewById(R.id.surface_view);
        btn_rewind = findViewById(R.id.btn_rewind);
        btn_forward = findViewById(R.id.btn_forward);
        btn_play = findViewById(R.id.btn_play);
        btn_rewind.setOnClickListener(this);
        btn_play.setOnClickListener(this);
        btn_forward.setOnClickListener(this);
        surfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ly_bottom.getVisibility()==View.GONE){
                    ly_bottom.setVisibility(View.VISIBLE);
                }else {
                    ly_bottom.setVisibility(View.GONE);
                }
            }
        });
        holder = surfaceView.getHolder();
        holder.setFormat(PixelFormat.RGBX_8888);
        holder.addCallback(this);

        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        def_lay = (LinearLayout)findViewById(R.id.def_lay);
        ly_bottom = (LinearLayout)findViewById(R.id.ly_bottom);
        Intent intent = getIntent();
        txt_channel = (TextView)findViewById(R.id.txt_channel);
        mStream_id = intent.getStringExtra("stream_id");
        txt_channel.setText(intent.getStringExtra("channel_name"));
        channel_name = intent.getStringExtra("channel_name");
        token = (String) MyApp.instance.getPreference().get(Constants.TOKEN);
        txt_epg_data = (TextView)findViewById(R.id.txt_epg_data);
        txt_time = (TextView)findViewById(R.id.txt_time);
        txt_progress = (TextView)findViewById(R.id.txt_progress);
        btn_back = (ImageView)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);
//        findViewById(R.id.ly_back).setOnClickListener(this);
        date_list = (ListView)findViewById(R.id.date_list);
        dateListAdapter = new DateListAdapter(this,date_datas);
        date_list.setAdapter(dateListAdapter);
        date_list.setOnItemClickListener(this);
        dateListAdapter.selectItem(6);
        epg_list = (ListView)findViewById(R.id.epg_list);
        epg_list.setOnItemClickListener(this);
        epg_date = sdf.format(new Date());
        page = 0;
        getEpgData();
        FullScreencall();
        Thread myThread = null;
        Runnable runnable = new CountDownRunner();
        myThread = new Thread(runnable);
        myThread.start();
    }
    private void getEpgData(){
        txt_progress.setVisibility(View.VISIBLE);
//        String URL = Constants.GetUrl(this)+"portal.php?type=epg&action=get_simple_data_table&ch_id="+mStream_id+"&date="+epg_date+"&p="+page+"&JsHttpRequest=1-xml";
        GetDataService getDataService = new GetDataService(GuideActivity.this);
        getDataService.getData(GuideActivity.this,Constants.GetUrl(this)+"portal.php?type=epg&action=get_simple_data_table&ch_id="+mStream_id+"&date="+epg_date+"&p="+page+"&JsHttpRequest=1-xml",100);
        getDataService.onGetResultsData(GuideActivity.this);
    }
    private void printEpgData(){
        txt_progress.setVisibility(View.GONE);
        epgListAdapter = new EpgListAdapter(this,epgModels);
        epg_list.setAdapter(epgListAdapter);
        if(selected_num>0){
            selected_num--;
        }
        int p = epg_list.getLastVisiblePosition();
        if(selected_num>p){
            epg_list.setSelection(selected_num);
        }
        epgListAdapter.selectItem(selected_num);
        printEpgDataInDetail();
    }
    private void printEpgDataInDetail(){
        String epgtime = epgModels.get(selected_num).getT_time() + "-" + epgModels.get(selected_num).getT_time_to();
        String epgData = epgModels.get(selected_num).getDescr();
        String epgText = epgtime + " - " + epgData;

        Spannable spannable = new SpannableString(epgText);
        spannable.setSpan(new ForegroundColorSpan(Color.YELLOW), 0, epgtime.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        txt_epg_data.setText(spannable, TextView.BufferType.SPANNABLE);
    }
    private String getFromDate(long millisecond){
        Date date = new Date();
        date.setTime(millisecond);
        String formattedDate=new SimpleDateFormat("EEE, dd  MMM").format(date);
        return formattedDate;
    }
    private String getFromDate1(long millisecond){
        Date date = new Date();
        date.setTime(millisecond);
        String formattedDate=sdf1.format(date);
        return formattedDate;
    }
    private String getFromCatchDate(long millisecond){
        Date date = new Date();
        date.setTime(millisecond);
        String formattedDate=catchupFormat.format(date);
        return formattedDate;
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if(adapterView==date_list){
            is_up = false;
            dateListAdapter.selectItem(position);
            long milisecond = System.currentTimeMillis() + (position-6)*24*3600*1000;
            Date date = new Date();
            date.setTime(milisecond);
            epg_date = sdf.format(date);
            page = 0;
            getEpgData();
        }else if(adapterView == epg_list ){
            if(!is_start){
                selected_num = position;
//                Log.e("selected", String .valueOf(selected_num));
                epgListAdapter.selectItem(selected_num);
                printEpgDataInDetail();
                if(epgModels.get(selected_num).getMark_archive()==1 && surfaceView.getVisibility()==View.GONE){
                    stream = epgModels.get(selected_num).getCh_id();
                    Date date = null;
                    long wrongMedialaanTime = TimeZone.getDefault().getRawOffset() + TimeZone.getDefault().getDSTSavings();
                    Calendar c = Calendar.getInstance();
                    int numOfWeek = c.get(Calendar.DAY_OF_WEEK);
                    Log.e("numofWeek",String .valueOf(numOfWeek));
                    try {
                        date = catch_format.parse(epgModels.get(selected_num).getTime());
                        startMill = date.getTime() - wrongMedialaanTime+3600*1000;
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(startMill);
                        int num = calendar.get(Calendar.DAY_OF_WEEK);
//                        if(num>numOfWeek){
//                            startMill = startMill;
                            Log.e("num",String .valueOf(num));
//                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (libvlc != null) {
                        releaseMediaPlayer();
                        surfaceView = null;
                    }
                    surfaceView = (SurfaceView) findViewById(R.id.surface_view);
                    holder = surfaceView.getHolder();
                    holder.setFormat(PixelFormat.RGBX_8888);
                    holder.addCallback(this);
                    start_time = getFromCatchDate(startMill);
                    duration = epgModels.get(selected_num).getDuration()/60;
                    contentUri = Constants.GetUrl(this)+"streaming/timeshift.php?username="+MyApp.instance.user+"&password="+MyApp.instance.pass+"&stream="+stream+"&start="+start_time+"&duration="+duration;
                    playVideo();
                    surfaceView.setVisibility(View.VISIBLE);
                    current_time = new Date().getTime();
                    ly_bottom.setVisibility(View.VISIBLE);
                    updateProgressBar();
                    listTimer();
                }
            }else {
                is_start = false;
            }

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_rewind:
                if(surfaceView.getVisibility()==View.VISIBLE){
                    mHandler.removeCallbacks(mTicker);
                    Date date1 = new Date();
                    if (date1.getTime() > current_time + 60 * 1000) {
                        current_time += 60 * 1000;
                        if (libvlc != null) {
                            releaseMediaPlayer();
                            surfaceView = null;
                        }
                        surfaceView = (SurfaceView) findViewById(R.id.surface_view);
                        holder = surfaceView.getHolder();
                        holder.setFormat(PixelFormat.RGBX_8888);
                        holder.addCallback(this);
                        startMill = startMill-60*1000;
                        start_time = getFromCatchDate(startMill);
                        duration = duration+1;
                        contentUri = Constants.GetUrl(this) + "streaming/timeshift.php?username=" + MyApp.instance.user + "&password=" + MyApp.instance.pass + "&stream=" + stream + "&start=" + start_time + "&duration=" + duration;
                        item_sel = true;
                        playVideo();
                        ly_bottom.setVisibility(View.VISIBLE);
                        updateProgressBar();
                        listTimer();
                    }
                }
                break;
            case R.id.btn_play:
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                } else {
                    mMediaPlayer.play();
                }
                break;
            case R.id.btn_forward:
                is_start = true;
                if(surfaceView.getVisibility()==View.VISIBLE){
                    mHandler.removeCallbacks(mTicker);
                    current_time -= 60*1000;
                    if (libvlc != null) {
                        releaseMediaPlayer();
                        surfaceView = null;
                    }
                    surfaceView = (SurfaceView) findViewById(R.id.surface_view);
                    holder = surfaceView.getHolder();
                    holder.setFormat(PixelFormat.RGBX_8888);
                    holder.addCallback(this);
                    startMill = startMill+60*1000;
                    start_time = getFromCatchDate(startMill);
                    duration = duration-1;
                    contentUri = Constants.GetUrl(this) + "streaming/timeshift.php?username=" + MyApp.instance.user + "&password=" + MyApp.instance.pass + "&stream=" + stream + "&start=" + start_time + "&duration=" + duration;
                    item_sel = true;
                    playVideo();
                    surfaceView.setVisibility(View.VISIBLE);
                    ly_bottom.setVisibility(View.VISIBLE);
                    updateProgressBar();
                    listTimer();
                }
                break;
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        View view = getCurrentFocus();
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()){
                case KeyEvent.KEYCODE_BACK:
                    if(ly_bottom.getVisibility()==View.VISIBLE){
                        ly_bottom.setVisibility(View.GONE);
                        return true;
                    }
                    if(surfaceView.getVisibility()==View.VISIBLE){
                        releaseMediaPlayer();
                        surfaceView.setVisibility(View.GONE);
                        def_lay.setVisibility(View.GONE);
                        epg_list.requestFocus();
                        return true;
                    }
                    finish();
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    is_start = true;
                    if(surfaceView.getVisibility()==View.VISIBLE){
                        mHandler.removeCallbacks(mTicker);
                        current_time -= 60*1000;
                        if (libvlc != null) {
                            releaseMediaPlayer();
                            surfaceView = null;
                        }
                        surfaceView = (SurfaceView) findViewById(R.id.surface_view);
                        holder = surfaceView.getHolder();
                        holder.setFormat(PixelFormat.RGBX_8888);
                        holder.addCallback(this);
                        startMill = startMill+60*1000;
                        start_time = getFromCatchDate(startMill);
                        duration = duration-1;
                        contentUri = Constants.GetUrl(this) + "streaming/timeshift.php?username=" + MyApp.instance.user + "&password=" + MyApp.instance.pass + "&stream=" + stream + "&start=" + start_time + "&duration=" + duration;
                        item_sel = true;
                        playVideo();
                        surfaceView.setVisibility(View.VISIBLE);
                        ly_bottom.setVisibility(View.VISIBLE);
                        updateProgressBar();
                        listTimer();
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if(view==date_list && surfaceView.getVisibility()==View.GONE){
                        finish();
                    }else if(surfaceView.getVisibility()==View.VISIBLE){
                        mHandler.removeCallbacks(mTicker);
                        Date date1 = new Date();
                        if (date1.getTime() > current_time + 60 * 1000) {
                            current_time += 60 * 1000;
                            if (libvlc != null) {
                                releaseMediaPlayer();
                                surfaceView = null;
                            }
                            surfaceView = (SurfaceView) findViewById(R.id.surface_view);
                            holder = surfaceView.getHolder();
                            holder.setFormat(PixelFormat.RGBX_8888);
                            holder.addCallback(this);
                            startMill = startMill-60*1000;
                            start_time = getFromCatchDate(startMill);
                            duration = duration+1;
                            contentUri = Constants.GetUrl(this) + "streaming/timeshift.php?username=" + MyApp.instance.user + "&password=" + MyApp.instance.pass + "&stream=" + stream + "&start=" + start_time + "&duration=" + duration;
                            item_sel = true;
                            playVideo();
                            ly_bottom.setVisibility(View.VISIBLE);
                            updateProgressBar();
                            listTimer();
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if(view==epg_list && txt_progress.getVisibility() == View.GONE && surfaceView.getVisibility()==View.GONE){
                        is_up = false;
                        is_start = true;
                        if(selected_num<9){
                            selected_num++;
                            epgListAdapter.selectItem(selected_num);
                            printEpgDataInDetail();
                            return true;
                        }else {
                            if(page<page_nums){
                                page++;
                                getEpgData();
                            }
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_UP:
                    if(view==epg_list && txt_progress.getVisibility()==View.GONE && surfaceView.getVisibility()==View.GONE){
                        is_up = true;
                        is_start = true;
                        if(selected_num>0){
                            selected_num--;
                            epgListAdapter.selectItem(selected_num);
                            printEpgDataInDetail();
                            return true;
                        }else {
                            if(page>1){
                                page--;
                                getEpgData();
                            }
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_CENTER:
                    if(surfaceView.getVisibility()==View.VISIBLE){
                        if(ly_bottom.getVisibility()==View.VISIBLE){
                            ly_bottom.setVisibility(View.GONE);
                        }else {
                            ly_bottom.setVisibility(View.VISIBLE);
                        }
                    }else {
                        if(view==epg_list && is_start){
                            epgListAdapter.selectItem(selected_num);
                            printEpgDataInDetail();
                            if(epgModels.get(selected_num).getMark_archive()==1 && surfaceView.getVisibility()==View.GONE){
                                stream = epgModels.get(selected_num).getCh_id();
                                long wrongMedialaanTime = TimeZone.getDefault().getRawOffset() + TimeZone.getDefault().getDSTSavings();
                                Date date = null;
                                try {
                                    date = catch_format.parse(epgModels.get(selected_num).getTime());
                                    startMill = date.getTime() - wrongMedialaanTime;
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                start_time = getFromCatchDate(startMill);
                                duration = epgModels.get(selected_num).getDuration()/60;
                                contentUri = Constants.GetUrl(this)+"streaming/timeshift.php?username="+MyApp.instance.user+"&password="+MyApp.instance.pass+"&stream="+stream+"&start="+start_time+"&duration="+duration;
                                playVideo();
                                surfaceView.setVisibility(View.VISIBLE);
                                current_time = new Date().getTime();
                                ly_bottom.setVisibility(View.VISIBLE);
                                updateProgressBar();
                                listTimer();
                            }
                        }
                    }
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent me) {
//        Log.e("last_pos",String .valueOf(epg_list.getLastVisiblePosition()));
        this.detector.onTouchEvent(me);
        return super.dispatchTouchEvent(me);
    }

    @Override
    public void onSwipe(int direction) {
        String str = "";
        switch (direction) {
            case SimpleGestureFilter.SWIPE_RIGHT:
                break;
            case SimpleGestureFilter.SWIPE_LEFT:
                break;
            case SimpleGestureFilter.SWIPE_DOWN:
                if(surfaceView.getVisibility()==View.GONE){
                    if(txt_progress.getVisibility()==View.GONE && epg_list.getFirstVisiblePosition()==0){
                        if(page>1){
                            page--;
                            getEpgData();
                        }
                    }
                }
                break;
            case SimpleGestureFilter.SWIPE_UP:
                if(surfaceView.getVisibility()==View.GONE){
                    if(txt_progress.getVisibility()==View.GONE && epg_list.getLastVisiblePosition()==9){
                        if(page<page_nums){
                            page++;
                            getEpgData();
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onDoubleTap() {

    }
    @Override
    public void onGetResultsData(JSONObject object, int request_code) {
        if(object!=null){
            if(request_code==100){
//                Log.e("result",String .valueOf(object));
                epgModels = new ArrayList<>();
                try {
                    JSONObject data_object = object.getJSONObject("js");
                    int cur_page = data_object.getInt("cur_page");
                    if(page==0){
                        if(cur_page==0){
                            page=1;
                        }else {
                            page = cur_page;
                        }
                    }
                    total_items = data_object.getInt("total_items");
                    if(total_items%10==0){
                        page_nums = total_items/10;
                    }else {
                        page_nums = total_items/10+1;
                    }
                    if(is_up){
                        selected_num = 10;
                    }else {
                        selected_num = data_object.getInt("selected_item");
                    }
                    try {
                        JSONArray epg_array = data_object.getJSONArray("data");
                        if(epg_array.length()>0){
                            for(int i = 0;i<epg_array.length();i++){
                                JSONObject epg_obj = epg_array.getJSONObject(i);
                                try {
                                    EpgModel epgModel = new EpgModel();
                                    epgModel.setId(epg_obj.getString("id"));
                                    epgModel.setCh_id(epg_obj.getString("ch_id"));
                                    epgModel.setTime(epg_obj.getString("time"));
                                    epgModel.setTime_to(epg_obj.getString("time_to"));
                                    epgModel.setName(epg_obj.getString("name"));
                                    epgModel.setDescr(epg_obj.getString("descr"));
                                    epgModel.setReal_id(epg_obj.getString("real_id"));
                                    epgModel.setCategory(epg_obj.getString("category"));
                                    epgModel.setDirector(epg_obj.getString("director"));
                                    epgModel.setActor(epg_obj.getString("actor"));
                                    epgModel.setT_time(epg_obj.getString("t_time"));
                                    epgModel.setT_time_to(epg_obj.getString("t_time_to"));
                                    epgModel.setDuration(epg_obj.getInt("duration"));
                                    epgModel.setStart_timestamp(epg_obj.getInt("start_timestamp"));
                                    epgModel.setStop_timestamp(epg_obj.getInt("stop_timestamp"));
                                    epgModel.setMark_memo(epg_obj.getInt("mark_memo"));
                                    epgModel.setMark_archive(epg_obj.getInt("mark_archive"));
                                    epgModels.add(epgModel);
                                }catch (Exception e1){
                                    Log.e("error","error");
                                }
                            }
                        }
                    }catch (Exception e2){
                        Log.e("error","error");
                    }
                }catch (Exception e){

                }
                if(epgModels.size()>0){
                    printEpgData();
                }
            }
        }
    }
    private void playVideo() {
        toggleFullscreen(true);
        releaseMediaPlayer();
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        mVideoHeight = displayMetrics.heightPixels;
        mVideoWidth = displayMetrics.widthPixels;
        if(def_lay.getVisibility()==View.VISIBLE)def_lay.setVisibility(View.GONE);
        try {
//            Log.e("url",contentUri);
            // Create LibVLC
            // TODO: make this more robust, and sync with audio demo
            ArrayList<String> options = new ArrayList<String>();
            //options.add("--subsdec-encoding <encoding>");
            options.add("--aout=opensles");
            options.add("--audio-time-stretch"); // time stretching
            options.add("-vvv"); // verbosity
            options.add("0");//this option is used to show the first subtitle track
            options.add("--subsdec-encoding");

            libvlc = new LibVLC(this, options);

            mMediaPlayer = new MediaPlayer(libvlc);
            mMediaPlayer.setEventListener(mPlayerListener);
            mMediaPlayer.setAspectRatio(mVideoWidth+":"+mVideoHeight);


            // Seting up video output
            final IVLCVout vout = mMediaPlayer.getVLCVout();
            vout.setVideoView(surfaceView);


            //vout.setSubtitlesView(mSurfaceSubtitles);
            vout.setWindowSize(mVideoWidth, mVideoHeight);
            vout.addCallback(this);
            vout.attachViews();
//            vout.setSubtitlesView(tv_subtitle);


            Media m = new Media(libvlc, Uri.parse(contentUri));
            mMediaPlayer.setMedia(m);
            mMediaPlayer.play();

        } catch (Exception e) {
            Toast.makeText(this, "Error creating player!", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onUserLeaveHint() {
        releaseMediaPlayer();
        super.onUserLeaveHint();
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (!is_create) {
            if (libvlc != null) {
                releaseMediaPlayer();
                surfaceView = null;
            }
            surfaceView = (SurfaceView) findViewById(R.id.surface_view);
            holder = surfaceView.getHolder();
            holder.setFormat(PixelFormat.RGBX_8888);
            playVideo();
        } else {
            is_create = false;
        }

    }
    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaPlayer();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void toggleFullscreen(boolean fullscreen) {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        if (fullscreen) {
            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        } else {
            attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        }
        getWindow().setAttributes(attrs);
    }
    private void releaseMediaPlayer() {
        if (libvlc == null)
            return;
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            final IVLCVout vout = mMediaPlayer.getVLCVout();
            vout.removeCallback(this);
            vout.detachViews();
        }

        holder = null;
        libvlc.release();
        libvlc = null;

        mVideoWidth = 0;
        mVideoHeight = 0;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
    }




    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private MediaPlayer.EventListener mPlayerListener = new MediaPlayerListener(this);

    private static class MediaPlayerListener implements MediaPlayer.EventListener {
        private WeakReference<GuideActivity> mOwner;

        public MediaPlayerListener(GuideActivity owner) {
            mOwner = new WeakReference<GuideActivity>(owner);
        }

        @Override
        public void onEvent(MediaPlayer.Event event) {
            GuideActivity player = mOwner.get();

            switch (event.type) {
                case MediaPlayer.Event.EndReached:
                    player.releaseMediaPlayer();
                    player.is_create = false;
                    player.onResume();
                    break;
                case MediaPlayer.Event.Playing:
                    break;
                case MediaPlayer.Event.Paused:
                case MediaPlayer.Event.Stopped:
                    break;
                case MediaPlayer.Event.Buffering:
                    break;
                case MediaPlayer.Event.EncounteredError:
                    player.def_lay.setVisibility(View.VISIBLE);
                    break;
                case MediaPlayer.Event.TimeChanged:
                    break;
                case MediaPlayer.Event.PositionChanged:
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onSurfacesCreated(IVLCVout vlcVout) {

    }

    @Override
    public void onSurfacesDestroyed(IVLCVout vlcVout) {

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
                } catch (Exception e) {
                }
            }
        });
    }

    public void FullScreencall() {
        if( Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
    int maxTime;

    private void listTimer() {
        maxTime = osd_time;
        mTicker = new Runnable() {
            public void run() {
                if (maxTime < 1) {
                    ly_bottom.setVisibility(View.GONE);
                    return;
                }
                runNextTicker();
            }
        };
        mTicker.run();
    }
    private void runNextTicker() {
        maxTime--;
        long next = SystemClock.uptimeMillis() + 1000;
        mHandler.postAtTime(mTicker, next);
    }

    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            if (mMediaPlayer != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH.mm a EEE MM/dd");
                long totalDuration = System.currentTimeMillis();
                if(epgModels!=null && epgModels.size()>0){
                    txt_date.setText(dateFormat.format(new Date()));
                    int pass_min = (int) ((totalDuration - current_time)/(1000*60));
                    int remain_min = (int)(epgModels.get(selected_num).getStop_timestamp()-epgModels.get(selected_num).getStart_timestamp())/(60) - pass_min;
                    int progress = pass_min * 100/(pass_min+remain_min);
                    seekbar.setProgress(progress);
                    txt_time_passed.setText("Started " + pass_min +" mins ago");
                    txt_remain_time.setText("+"+remain_min+"min");
                    txt_last_time.setText(getFromDate1(current_time + epgModels.get(selected_num).getStop_timestamp()*1000-epgModels.get(selected_num).getStart_timestamp()*1000));
                    txt_dec.setText(epgModels.get(selected_num).getDescr());
                    txt_title.setText(epgModels.get(selected_num).getName());
                    channel_title.setText(channel_name);
                    txt_current_dec.setText(epgModels.get(selected_num).getName());
                    try {
                        txt_next_dec.setText(epgModels.get(selected_num+1).getName());
                    }catch (Exception e){
                        txt_next_dec.setText("No Information");
                    }
                }else {
                    txt_title.setText("No Information");
                    txt_dec.setText("No Information");
                    channel_title.setText(channel_name);
                    txt_date.setText(dateFormat.format(new Date()));
                    txt_time_passed.setText("");
                    txt_remain_time.setText("");
                    txt_last_time.setText("");
                    seekbar.setProgress(0);
                    txt_current_dec.setText("No Information");
                    txt_next_dec.setText("No Information");
                }
            }
            mHandler.postDelayed(this, 500);
        }
    };
}
