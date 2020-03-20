package com.kds.gold.acebird.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kds.gold.acebird.R;
import com.kds.gold.acebird.adapter.MainListAdapter;
import com.kds.gold.acebird.apps.Constants;
import com.kds.gold.acebird.apps.MyApp;
import com.kds.gold.acebird.async.GetDataService;
import com.kds.gold.acebird.dialog.PackageDlg;
import com.kds.gold.acebird.models.ChannelModel;
import com.kds.gold.acebird.models.EpgModel;
import com.kds.gold.acebird.models.PlayModel;
import com.kds.gold.acebird.models.TvGenreModel;
import com.kds.gold.acebird.listner.SimpleGestureFilter;
import com.kds.gold.acebird.listner.SimpleGestureFilter.SimpleGestureListener;
import com.kds.gold.acebird.utils.Utils;
import com.squareup.picasso.Picasso;
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
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static java.lang.Integer.parseInt;

public class PreviewChannelAcitivoty extends AppCompatActivity implements  AdapterView.OnItemClickListener,View.OnClickListener,
        GetDataService.OnGetResultsListener,View.OnFocusChangeListener,SurfaceHolder.Callback,SimpleGestureListener,IVLCVout.Callback,
        AdapterView.OnItemLongClickListener{
    private MediaPlayer mMediaPlayer = null;
    private int mVideoWidth;
    private int mVideoHeight;
    int selected_item = 0;
    LibVLC libvlc;
    private SimpleGestureFilter detector;
    Context context = null;
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat time = new SimpleDateFormat("HH:mm a");
    public static SurfaceView surfaceView;
    SurfaceView remote_subtitles_surface;
    private SurfaceHolder holder;
    LinearLayout def_lay,ly_view,ly_sort,ly_fav,ly_move,ly_bottom,ly_buttons,ly_viewdlg,ly_sortdlg,ly_left,ly_right,ly_resolution,ly_audio,ly_subtitle;
    MediaPlayer.TrackDescription[] traks;
    MediaPlayer.TrackDescription[] subtraks;
    String ratio;
    String[] resolutions ;
    int current_resolution = 0;
    boolean first = true;
    RelativeLayout ly_surface,main_lay;
    ImageView btn_back,btn_guide,image_clock,image_star,channel_logo;
    Button btn_list_info,btn_list,btn_num,btn_title,btn_fav;
    ProgressBar progressBar;
    List<TvGenreModel> tvGenreModelList;
    List<ChannelModel> channels;
    List<EpgModel>epgModelList;
    List<String>  favModelIds;
    List<String >pkg_datas;
    PlayModel playModel;
    ChannelModel sel_model = null;
    int all_pos = 0;int preview_pos,sort;
    List<ChannelModel>allChannel;
    int channel_pos,sub_pos,epg_pos,page=0,total_page,max_items,total_items,move_pos = 0,fav,osd_time;
    MainListAdapter mainListAdapter;
    ListView channel_list;
    TextView txt_time,txt_category,txt_title,txt_dec,txt_channel,txt_date,txt_time_passed,txt_remain_time,txt_last_time,txt_current_dec,txt_next_dec,
            firstTime,firstTitle,secondTime,secondTitle,thirdTime,thirdTitle,fourthTime,fourthTitle,txt_progress,num_txt;
    SeekBar seekbar;
    String contentUri,mStream_id,token,genre_id,url,key = "",sortby;
    Handler mHandler = new Handler();
    Handler moveHandler = new Handler();
    Handler mEpgHandler = new Handler();
    Runnable  mTicker,moveTicker,mEpgTicker;
    boolean is_create = true,is_start = false,is_first = true, is_full = false,is_up = false,item_sel = false,is_dlg = false,is_fav = false,is_center = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_channel);
        MyApp.is_welcome = false;
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        osd_time = (int) MyApp.instance.getPreference().get(Constants.OSD_TIME);
        detector = new SimpleGestureFilter(PreviewChannelAcitivoty.this, PreviewChannelAcitivoty.this);
        context = this;
        pkg_datas = new ArrayList<>();
        pkg_datas.addAll(Arrays.asList(getResources().getStringArray(R.array.package_list)));
        main_lay = (RelativeLayout)findViewById(R.id.main_lay);
        main_lay.setOnClickListener(this);
        MyApp.is_first = true;
        allChannel = MyApp.channelModelList;
        favModelIds = MyApp.favModelIds;

        if(MyApp.instance.getPreference().get(Constants.SORT)==null){
            sort = 0;
            fav = 0;
            sortby = "number";
        }else {
            sort = (int)MyApp.instance.getPreference().get(Constants.SORT);
            if(sort==0){
                sortby = "number";
                fav = 0;
            }else if(sort==1){
                sortby = "name";
                fav = 0;
            }else {
                sortby = "fav";
                fav=1;
            }
        }
        channel_pos = (int) MyApp.instance.getPreference().get(Constants.CHANNEL_POS);
        channel_list = findViewById(R.id.channel_list);
        channel_list.setOnItemClickListener(this);
        channel_list.setOnItemLongClickListener(this);
        channel_list.requestFocus();
        btn_back = findViewById(R.id.btn_back);
        btn_guide = findViewById(R.id.btn_guide);
        image_clock = findViewById(R.id.image_clock);
        image_star = findViewById(R.id.image_star);
        channel_logo = findViewById(R.id.channel_logo);
        btn_back.setOnClickListener(this);
        btn_guide.setOnClickListener(this);
        txt_time = findViewById(R.id.txt_time);
        txt_category = findViewById(R.id.txt_category);
        txt_title = findViewById(R.id.txt_title);
        txt_dec = findViewById(R.id.txt_dec);
        txt_channel = (TextView)findViewById(R.id.txt_channel);
        txt_date = (TextView)findViewById(R.id.txt_date);
        txt_time_passed = (TextView)findViewById(R.id.txt_time_passed);
        txt_remain_time = (TextView)findViewById(R.id.txt_remain_time);
        txt_last_time = (TextView)findViewById(R.id.txt_last_time);
        txt_current_dec = (TextView)findViewById(R.id.txt_current_dec);
        txt_next_dec = (TextView)findViewById(R.id.txt_next_dec);
        firstTime = (TextView)findViewById(R.id.txt_firstTime);
        firstTitle = (TextView)findViewById(R.id.txt_firstTitle);
        secondTime  = (TextView)findViewById(R.id.secondTime);
        secondTitle = (TextView)findViewById(R.id.secondTitle);
        thirdTime = (TextView)findViewById(R.id.thirdTime);
        thirdTitle = (TextView)findViewById(R.id.thirdTitle);
        fourthTime = (TextView)findViewById(R.id.fourthTime);
        fourthTitle = (TextView)findViewById(R.id.fourthTitle);
        txt_progress = (TextView)findViewById(R.id.txt_progress);
        num_txt = (TextView)findViewById(R.id.txt_num);
        txt_progress.setVisibility(View.GONE);
        seekbar = (SeekBar)findViewById(R.id.seekbar);
        seekbar.setMax(100);
        Thread myThread = null;
        Runnable runnable = new CountDownRunner();
        myThread = new Thread(runnable);
        myThread.start();
        def_lay = (LinearLayout)findViewById(R.id.def_lay);
        ly_view = (LinearLayout)findViewById(R.id.ly_view);
        ly_sort = (LinearLayout)findViewById(R.id.ly_sort);
        ly_fav = (LinearLayout)findViewById(R.id.ly_fav);
        ly_move = (LinearLayout)findViewById(R.id.ly_move);
        ly_bottom = (LinearLayout)findViewById(R.id.ly_bottom) ;
        ly_buttons = (LinearLayout)findViewById(R.id.ly_buttons);
        ly_viewdlg = (LinearLayout)findViewById(R.id.ly_viewdlg);
        ly_sortdlg = (LinearLayout)findViewById(R.id.ly_sortdlg);
        ly_left = (LinearLayout)findViewById(R.id.ly_left);
        ly_right = (LinearLayout)findViewById(R.id.ly_right);
        ly_view.setOnClickListener(this);
        ly_sort.setOnClickListener(this);
        ly_fav.setOnClickListener(this);
        ly_move.setOnClickListener(this);
        btn_list = (Button)findViewById(R.id.btn_list);
        btn_list_info = (Button)findViewById(R.id.btn_list_info);
        btn_num = (Button)findViewById(R.id.btn_num);
        btn_title = (Button)findViewById(R.id.btn_title);
        btn_fav = (Button)findViewById(R.id.btn_fav);
        btn_list.setOnClickListener(this);
        btn_list_info.setOnClickListener(this);
        btn_num.setOnClickListener(this);
        btn_title.setOnClickListener(this);
        btn_fav.setOnClickListener(this);
        btn_list.setOnFocusChangeListener(this);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        surfaceView = (SurfaceView) findViewById(R.id.surface_view);
        ly_surface = (RelativeLayout)findViewById(R.id.ly_surface);
        ly_surface.setOnClickListener(this);
        surfaceView = (SurfaceView) findViewById(R.id.surface_view);
        holder = surfaceView.getHolder();
        holder.addCallback(this);
        holder.setFormat(PixelFormat.RGBX_8888);
        remote_subtitles_surface = findViewById(R.id.remote_subtitles_surface);
        remote_subtitles_surface.setZOrderMediaOverlay(true);
        remote_subtitles_surface.getHolder().setFormat(PixelFormat.TRANSLUCENT);

        ly_audio = findViewById(R.id.ly_audio);
        ly_resolution = findViewById(R.id.ly_resolution);
        ly_subtitle = findViewById(R.id.ly_subtitle);

        ly_subtitle.setOnClickListener(this);
        ly_resolution.setOnClickListener(this);
        ly_audio.setOnClickListener(this);

        ViewGroup.LayoutParams params = ly_surface.getLayoutParams();
        params.height = MyApp.SURFACE_HEIGHT;
        params.width = MyApp.SURFACE_WIDTH;

        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        mVideoHeight = displayMetrics.heightPixels;
        mVideoWidth = displayMetrics.widthPixels;
        int SCREEN_HEIGHT = displayMetrics.heightPixels;
        int SCREEN_WIDTH = displayMetrics.widthPixels;
        holder.setFixedSize(SCREEN_WIDTH, SCREEN_HEIGHT);

        ratio = mVideoWidth + ":"+ mVideoHeight;
        resolutions =  new String[]{"16:9", "4:3", ratio};


        setMargins(ly_surface,0,MyApp.top_margin,MyApp.right_margin,0);
        if(MyApp.instance.getPreference().get(Constants.IS_PHONE)==null){
            ly_surface.setPadding(Utils.dp2px(this,10),Utils.dp2px(this,10),Utils.dp2px(this,10),Utils.dp2px(this,10));
        }else {
            ly_surface.setPadding(Utils.dp2px(this,5),Utils.dp2px(this,5),Utils.dp2px(this,5),Utils.dp2px(this,5));
        }
        ly_surface.setLayoutParams(params);
        getChannels();
        FullScreencall();
        if(MyApp.is_list){
            ly_surface.setVisibility(View.GONE);
            ly_right.setVisibility(View.GONE);
        }
    }
    private void playVideo() {
        toggleFullscreen(true);
        releaseMediaPlayer();
        if(def_lay.getVisibility()==View.VISIBLE)def_lay.setVisibility(View.GONE);
        try {
            // TODO: make this more robust, and sync with audio demo
            ArrayList<String> options = new ArrayList<String>();
            options.add("--aout=opensles");
            options.add("--audio-time-stretch"); // time stretching
            options.add("-vvv"); // verbosity
            options.add("0");//this option is used to show the first subtitle track
            options.add("--subsdec-encoding");
            libvlc = new LibVLC(this, options);
            mMediaPlayer = new MediaPlayer(libvlc);
            mMediaPlayer.setEventListener(mPlayerListener);
            mMediaPlayer.setAspectRatio(mVideoWidth+":"+mVideoHeight);
            final IVLCVout vout = mMediaPlayer.getVLCVout();
            vout.setVideoView(surfaceView);
            if (remote_subtitles_surface != null)
                vout.setSubtitlesView(remote_subtitles_surface);
            vout.setWindowSize(mVideoWidth, mVideoHeight);
            vout.addCallback(this);
            vout.attachViews();

            Media m = new Media(libvlc, Uri.parse(contentUri));
            mMediaPlayer.setMedia(m);
            mMediaPlayer.play();

        } catch (Exception e) {
            Toast.makeText(this, "Error in creating player!", Toast
                    .LENGTH_LONG).show();
        }

        playModel = new PlayModel();
        playModel.setPage(page);
        playModel.setSub_pos(sub_pos);
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
            holder.addCallback(this);
            WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
            final DisplayMetrics displayMetrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(displayMetrics);
            int SCREEN_HEIGHT = displayMetrics.heightPixels;
            int SCREEN_WIDTH = displayMetrics.widthPixels;
            holder.setFixedSize(SCREEN_WIDTH, SCREEN_HEIGHT);
            mVideoHeight = displayMetrics.heightPixels;
            mVideoWidth = displayMetrics.widthPixels;
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
        SharedPreferences pref = getSharedPreferences("PREF_AUDIO_TRACK", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("AUDIO_TRACK", 0);
        editor.commit();

        SharedPreferences pref2 = getSharedPreferences("PREF_SUB_TRACK", MODE_PRIVATE);
        SharedPreferences.Editor editor1 = pref2.edit();
        editor1.putInt("SUB_TRACK", 0);
        releaseMediaPlayer();
    }


    private void getChannels(){
        txt_progress.setVisibility(View.VISIBLE);
        tvGenreModelList = MyApp.instance.tvGenreModelList;
        token = (String)MyApp.instance.getPreference().get(Constants.TOKEN);
        channels = new ArrayList<>();
        genre_id = tvGenreModelList.get(channel_pos).getId();
        txt_category.setText(tvGenreModelList.get(channel_pos).getTitle().toUpperCase() + "/BY " + sortby.toUpperCase());
        GetDataService getDataService = new GetDataService(PreviewChannelAcitivoty.this);
        getDataService.getData(PreviewChannelAcitivoty.this,url = Constants.GetUrl(this)+"portal.php?type=itv&action=get_ordered_list&genre="+genre_id+"&fav="+fav+"&sortby="+sortby+"&hd=0"+"&p="+page+"&JsHttpRequest=1-xml",100);
        getDataService.onGetResultsData(PreviewChannelAcitivoty.this);
    }
    private void getEpg(){
        GetDataService getDataService = new GetDataService(PreviewChannelAcitivoty.this);
        getDataService.getData(PreviewChannelAcitivoty.this,Constants.GetUrl(this)+"portal.php?type=itv&action=get_short_epg&ch_id="+mStream_id+"&size=10&JsHttpRequest=1-xml",200);
        getDataService.onGetResultsData(PreviewChannelAcitivoty.this);
    }

    int epg_time;
    int i = 0;
    private void EpgTimer(){
        epg_time = 1;
        mEpgTicker = new Runnable() {
            public void run() {
                if (epg_time < 1) {
                    i++;
                    Log.e("count",String .valueOf(i));
                    getEpg();
                    return;
                }
                runNextEpgTicker();
            }
        };
        mEpgTicker.run();
    }
    private void runNextEpgTicker() {
        epg_time--;
        long next = SystemClock.uptimeMillis() + 1000;
        mEpgHandler.postAtTime(mEpgTicker, next);
    }
    private void printEpgData(){
        if(txt_progress.getVisibility()==View.GONE){
            if(epgModelList.size()>0) {
                firstTime.setText(epgModelList.get(0).getT_time());
                firstTitle.setText(epgModelList.get(0).getName());
                if(epgModelList.size()>1){
                    secondTime.setText(epgModelList.get(1).getT_time());
                    secondTitle.setText(epgModelList.get(1).getName());
                }
                if(epgModelList.size()>2){
                    thirdTime.setText(epgModelList.get(2).getT_time());
                    thirdTitle.setText(epgModelList.get(2).getName());
                }
                if(epgModelList.size()>3){
                    fourthTime.setText(epgModelList.get(3).getT_time());
                    fourthTitle.setText(epgModelList.get(3).getName());
                }
            }else {
                firstTime.setText("");
                firstTitle.setText("");
                secondTime.setText("");
                secondTitle.setText("");
                thirdTime.setText("");
                thirdTitle.setText("");
                fourthTime.setText("");
                fourthTitle.setText("");
            }
        }
    }
    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            if (mMediaPlayer != null) {
                if (traks == null && subtraks == null) {
                    first = false;
                    traks = mMediaPlayer.getAudioTracks();
                    subtraks = mMediaPlayer.getSpuTracks();
                }
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH.mm a EEE MM/dd");
                long totalDuration = System.currentTimeMillis();
                if(epgModelList!=null && epgModelList.size()>0){
                    String time = epgModelList.get(0).getTime();
                    String time_to = epgModelList.get(0).getTime_to();
                    try {
                        Date date = sdf.parse(time);
                        long millis = date.getTime();
                        date = sdf.parse(time_to);
                        long mills_to = date.getTime();
                        if(totalDuration>millis){
                            txt_title.setText(epgModelList.get(0).getName());
                            txt_dec.setText(epgModelList.get(0).getDescr());
                            try {
                                txt_channel.setText(channels.get(sub_pos).getNumber() + " " + channels.get(sub_pos).getName());
                            }catch (Exception e1){
                                txt_channel.setText("    ");
                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        txt_channel.setText(channels.get(sub_pos).getNumber() + " " + channels.get(sub_pos).getName());
                                    }
                                }, 5000);
                            }
                            txt_date.setText(dateFormat.format(new Date()));
                            int pass_min = (int) ((totalDuration - millis)/(1000*60));
                            int remain_min = (int)(mills_to-totalDuration)/(1000*60);
                            int progress = (int) pass_min*100/(epgModelList.get(0).getDuration()/60);
                            seekbar.setProgress(progress);
                            txt_time_passed.setText("Started " + pass_min +" mins ago");
                            txt_remain_time.setText("+"+remain_min+" min");
                            txt_last_time.setText(time_to);
                            txt_current_dec.setText(epgModelList.get(0).getName());
                            txt_next_dec.setText(epgModelList.get(1).getName());
                            if(channels.get(sub_pos).getFav()==1){
                                image_star.setVisibility(View.VISIBLE);
                            }else {
                                image_star.setVisibility(View.GONE);
                            }
                            if(channels.get(sub_pos).getArchive()==1){
                                image_clock.setVisibility(View.VISIBLE);
                            }else {
                                image_clock.setVisibility(View.GONE);
                            }

                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }else {
                    txt_title.setText("No Information");
                    txt_dec.setText("No Information");
                    try {
                        txt_channel.setText(channels.get(sub_pos).getNumber() + " " + channels.get(sub_pos).getName());
                    }catch (Exception e2){
                        txt_channel.setText("    ");
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                txt_channel.setText(channels.get(sub_pos).getNumber() + " " + channels.get(sub_pos).getName());
                            }
                        }, 5000);
                    }
                    txt_date.setText(dateFormat.format(new Date()));
                    txt_time_passed.setText("      mins ago");
                    txt_remain_time.setText("      min");
                    txt_last_time.setText("         ");
                    seekbar.setProgress(0);
                    txt_current_dec.setText("No Information");
                    txt_next_dec.setText("No Information");
                }
            }
            mHandler.postDelayed(this, 500);
        }
    };



    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    public void onSurfacesCreated(IVLCVout vlcVout) {

    }

    @Override
    public void onSurfacesDestroyed(IVLCVout vlcVout) {

    }

    private MediaPlayer.EventListener mPlayerListener = new MediaPlayerListener(this);

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        return false;
    }

    private static class MediaPlayerListener implements MediaPlayer.EventListener {
        private WeakReference<PreviewChannelAcitivoty> mOwner;

        public MediaPlayerListener(PreviewChannelAcitivoty owner) {
            mOwner = new WeakReference<PreviewChannelAcitivoty>(owner);
        }

        @Override
        public void onEvent(MediaPlayer.Event event) {
            PreviewChannelAcitivoty player = mOwner.get();

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
    private void setMargins (View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
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

    @Override
    public void onSwipe(int direction) {
        switch (direction) {
            case SimpleGestureFilter.SWIPE_RIGHT:
                break;
            case SimpleGestureFilter.SWIPE_LEFT:
                break;
            case SimpleGestureFilter.SWIPE_DOWN:
                if(txt_progress.getVisibility()==View.GONE){
                    if(is_full){
                        is_up = true;
                        is_start = false;
                        MyApp.is_first = false;
                        if(txt_progress.getVisibility()==View.GONE){
                            if(page==1 && sub_pos==0){
                                page = total_page;
                                mHandler.removeCallbacks(mUpdateTimeTask);
                                is_first = true;
                                getChannels();
                            }else if(page>1 && sub_pos==0){
                                page--;
                                mHandler.removeCallbacks(mUpdateTimeTask);
                                is_first = true;
                                getChannels();
                            }else  if(sub_pos>0){
                                sub_pos--;
                                mStream_id = channels.get(sub_pos).getId();
                                mEpgHandler.removeCallbacks(mEpgTicker);
                                EpgTimer();
                                mHandler.removeCallbacks(mUpdateTimeTask);
                                MyApp.is_first = true;
                                channel_list.setSelection(sub_pos);
                                mainListAdapter.selectItem(sub_pos);
                                playChannel();
                            }
                        }
                    }else {
                        if(channel_list.getFirstVisiblePosition()==0){
                            if(page>1){
                                page--;
                                MyApp.is_first = false;
                                is_up = true;
                                getChannels();
                                if(page==playModel.getPage()){
                                    MyApp.is_first=true;
                                    mainListAdapter.selectItem(playModel.getSub_pos());
                                }
                            }else {
                                page=total_page;
                                MyApp.is_first = false;
                                is_up = true;
                                getChannels();
                                if(page==playModel.getPage()){
                                    MyApp.is_first=true;
                                    mainListAdapter.selectItem(playModel.getSub_pos());
                                }
                            }
                        }
                    }
                }
                break;
            case SimpleGestureFilter.SWIPE_UP:
                if(is_full){
                    is_up = false;
                    MyApp.is_first = false;
                    if(txt_progress.getVisibility()==View.GONE){
                        if(page<total_page && sub_pos<max_items-1){
                            sub_pos++;
                            mStream_id = channels.get(sub_pos).getId();
                            mEpgHandler.removeCallbacks(mEpgTicker);
                            EpgTimer();
                            mHandler.removeCallbacks(mUpdateTimeTask);
                            MyApp.is_first = true;
                            channel_list.setSelection(sub_pos);
                            mainListAdapter.selectItem(sub_pos);
                            playChannel();
                        }else if(page==total_page && sub_pos< (total_items%max_items - 1)){
                            sub_pos++;
                            mStream_id = channels.get(sub_pos).getId();
                            mEpgHandler.removeCallbacks(mEpgTicker);
                            EpgTimer();
                            mHandler.removeCallbacks(mUpdateTimeTask);
                            MyApp.is_first = true;
                            channel_list.setSelection(sub_pos);
                            mainListAdapter.selectItem(sub_pos);
                            playChannel();
                        }else if(page<total_page && sub_pos==max_items-1){
                            page++;
                            mHandler.removeCallbacks(mUpdateTimeTask);
                            is_first = true;
                            getChannels();
                        }else if(page==total_page && sub_pos== (total_items%max_items - 1)){
                            page=1;
                            mHandler.removeCallbacks(mUpdateTimeTask);
                            is_first = true;
                            getChannels();
                        }
                    }
                }else {
                    if(channel_list.getLastVisiblePosition()==max_items-1){
                        if(txt_progress.getVisibility()==View.GONE){
                            MyApp.is_first= false;
                            is_up = false;
                            page++;
                            getChannels();
                            if(page == playModel.getPage()){
                                MyApp.is_first = true;
                                mainListAdapter.selectItem(playModel.getSub_pos());
                            }
                        }
                    }else if(channel_list.getLastVisiblePosition()==(total_items%max_items)-1){
                        if(txt_progress.getVisibility()==View.GONE){
                            MyApp.is_first= false;
                            is_up = false;
                            page=1;
                            getChannels();
                            if(page == playModel.getPage()){
                                MyApp.is_first = true;
                                mainListAdapter.selectItem(playModel.getSub_pos());
                            }
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ly_view:
                if(ly_viewdlg.getVisibility()==View.INVISIBLE){
                    is_dlg = true;
                    ly_sortdlg.setVisibility(View.INVISIBLE);
                    ly_viewdlg.setVisibility(View.VISIBLE);
                    channel_list.setFocusable(false);
                    ly_viewdlg.requestFocus();
                    ly_viewdlg.setFocusable(true);
                    if(ly_right.getVisibility()==View.VISIBLE){
                        btn_list_info.requestFocus();
                    }else {
                        btn_list.requestFocus();
                    }
                }else {
                    channel_list.requestFocus();
                    channel_list.setFocusable(true);
                    ly_viewdlg.setFocusable(false);
                    is_dlg = false;
                    ly_viewdlg.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.ly_sort:
                if(ly_sortdlg.getVisibility()==View.INVISIBLE){
                    is_dlg = true;
                    if(is_up){
                        is_up = false;
                    }
                    ly_viewdlg.setVisibility(View.INVISIBLE);
                    ly_sortdlg.setVisibility(View.VISIBLE);
                    ly_sortdlg.requestFocus();
                    channel_list.setFocusable(false);
                    if(sort==0){
                        btn_num.requestFocus();
                    }else if(sort==1){
                        btn_title.requestFocus();
                    }else {
                        btn_fav.requestFocus();
                    }
                }else {
                    channel_list.setFocusable(true);
                    channel_list.requestFocus();
                    ly_sortdlg.setFocusable(false);
                    is_dlg = false;
                    ly_sortdlg.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.btn_list:
                MyApp.is_list = true;
                ly_right.setVisibility(View.GONE);
                ly_surface.setVisibility(View.GONE);
                mainListAdapter = new MainListAdapter(this,channels);
                channel_list.setAdapter(mainListAdapter);
                channel_list.setSelection(sub_pos);
                MyApp.is_first = true;
                mainListAdapter.selectItem(preview_pos);
                break;
            case R.id.btn_list_info:
                MyApp.is_list = false;
                MyApp.is_first = true;
                ly_right.setVisibility(View.VISIBLE);
                ly_surface.setVisibility(View.VISIBLE);
                mainListAdapter = new MainListAdapter(this,channels);
                channel_list.setAdapter(mainListAdapter);
                channel_list.setSelection(sub_pos);
                mainListAdapter.selectItem(preview_pos);
                break;
            case R.id.btn_num:
                sort = 0;
                MyApp.instance.getPreference().put(Constants.SORT,sort);
                sortby = "number";
                page = 0;
                is_first = true;
                MyApp.is_first = true;
                fav = 0;
                getChannels();
                break;
            case R.id.btn_title:
                if(txt_progress.getVisibility()==View.GONE){
                    sort = 1;
                    MyApp.instance.getPreference().put(Constants.SORT,sort);
                    sortby = "name";
                    page = 0;
                    is_first = true;
                    MyApp.is_first = true;
                    fav = 0;
                    getChannels();
                }
                break;
            case R.id.btn_fav:
                if(txt_progress.getVisibility()==View.GONE){
                    sort = 2;
                    MyApp.instance.getPreference().put(Constants.SORT,sort);
                    sortby = "fav";
                    page = 0;
                    is_first = true;
                    MyApp.is_first = true;
                    fav = 1;
                    getChannels();
                }
                break;
            case R.id.ly_fav:
                if(txt_progress.getVisibility()==View.GONE){
                    is_fav = true;
                    mStream_id = channels.get(sub_pos).getId();
                    StringBuilder buffer = new StringBuilder();
                    String res = null;
                    boolean is_exit = false;
                    for(int i = 0;i < favModelIds.size();i++){
                        if(mStream_id.equalsIgnoreCase(favModelIds.get(i))){
                            is_exit = true;
                            favModelIds.remove(i);
                            continue;
                        }
                        buffer.append(favModelIds.get(i)).append(",");
                    }
                    if(!is_exit){
                        favModelIds.add(mStream_id);
                        res = buffer.append(mStream_id).toString();
                    }else {
                        if(favModelIds.size()>0 && buffer.toString().endsWith(",")){
                            res = buffer.toString().substring(0,buffer.toString().length()-1);
                        }
                    }
                    if(favModelIds.size()==0){
                        res = mStream_id;
                        favModelIds = new ArrayList<>();
                        favModelIds.add(res);
                    }
                    MyApp.favModelIds = favModelIds;
                    if(channels.get(sub_pos).getFav()==0){
                        channels.get(sub_pos).setFav(1);
                    }else {
                        channels.get(sub_pos).setFav(0);
                    }
                    channel_list.requestFocus();
                    mainListAdapter = new MainListAdapter(this,channels);
                    channel_list.setAdapter(mainListAdapter);
                    channel_list.post(new Runnable() {
                        @Override
                        public void run() {
                            channel_list.setSelection(sub_pos);
                            channel_list.smoothScrollToPosition(sub_pos);
                        }
                    });
                    if(page == playModel.getPage()){
                        MyApp.is_first = true;
                        mainListAdapter.selectItem(playModel.getSub_pos());
                    }
                    GetDataService getDataService = new GetDataService(PreviewChannelAcitivoty.this);
                    getDataService.getData(PreviewChannelAcitivoty.this,Constants.GetUrl(this)+"portal.php?type=itv&action=set_fav&fav_ch="+res+"&JsHttpRequest=1-xml",600);
                    getDataService.onGetResultsData(PreviewChannelAcitivoty.this);
                }
                break;
            case R.id.ly_surface:
                if(!is_full && txt_progress.getVisibility()==View.GONE){
                    is_full = true;
                    if(ly_sortdlg.getVisibility()==View.VISIBLE) ly_sortdlg.setVisibility(View.GONE);
                    if(ly_viewdlg.getVisibility()==View.VISIBLE) ly_viewdlg.setVisibility(View.GONE);
                    ly_buttons.setVisibility(View.GONE);
                    ViewGroup.LayoutParams params = ly_surface.getLayoutParams();
                    params.height = MyApp.SCREEN_HEIGHT+Utils.dp2px(getApplicationContext(),50);
                    params.width = MyApp.SCREEN_WIDTH+Utils.dp2px(getApplicationContext(),50);
                    ly_surface.setPadding(Utils.dp2px(this,0),Utils.dp2px(this,0),Utils.dp2px(this,0),Utils.dp2px(this,0));
                    setMargins(ly_surface,Utils.dp2px(this,0),Utils.dp2px(this,0),Utils.dp2px(this,0),Utils.dp2px(this,0));
                    ly_surface.setLayoutParams(params);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mHandler.removeCallbacks(mUpdateTimeTask);
                            updateProgressBar();
                            ly_bottom.setVisibility(View.VISIBLE);
                            listTimer();
                        }
                    }, 2000);
                }else if(is_full){
                    if(ly_bottom.getVisibility()==View.GONE){
                        mEpgHandler.removeCallbacks(mEpgTicker);
                        EpgTimer();
                        ly_bottom.setVisibility(View.VISIBLE);
                    }else {
                        ly_bottom.setVisibility(View.GONE);
                    }
                }
                break;
            case R.id.btn_back:
                if(!is_full){
                    MyApp.key = false;
                    mHandler.removeCallbacks(mUpdateTimeTask);
                    releaseMediaPlayer();
                    finish();
                }
                break;
            case R.id.btn_guide:
                if(!is_full && txt_progress.getVisibility()==View.GONE){
                    channel_list.setFocusable(true);
                    MyApp.key = false;
                    mHandler.removeCallbacks(mUpdateTimeTask);
                    releaseMediaPlayer();
                    String channel_name = channels.get(sub_pos).getName();
                    mStream_id = channels.get(sub_pos).getId();
                    Intent intent = new Intent(this,GuideActivity.class);
                    intent.putExtra("stream_id",mStream_id);
                    intent.putExtra("channel_name",channel_name);
                    startActivity(intent);
                }
                break;
            case R.id.main_lay:
                if(ly_sortdlg.getVisibility()==View.VISIBLE){
                    ly_sortdlg.setVisibility(View.GONE);
                    is_dlg = false;
                }
                if(ly_viewdlg.getVisibility()==View.VISIBLE){
                    ly_viewdlg.setVisibility(View.GONE);
                    is_dlg=false;
                }
                break;
            case R.id.ly_audio:
                if (traks != null) {
                    if (traks.length > 0) {
                        showAudioTracksList();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "No audio tracks or not loading yet", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            "No audio tracks or not loading yet", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.ly_subtitle:
                if (subtraks != null) {
                    if (subtraks.length > 0) {
                        showSubTracksList();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "No subtitle or not loading yet", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            "No subtitle or not loading yet", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.ly_resolution:
                current_resolution++;
                if (current_resolution == resolutions.length)
                    current_resolution = 0;

                mMediaPlayer.setAspectRatio(resolutions[current_resolution]);
                break;
        }
    }
    @Override
    public void onFocusChange(View v, boolean hasFocused) {
        switch (v.getId()){
            case R.id.btn_list:
                if(hasFocused){
                }else {

                }
                break;
        }
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if(!is_start && adapterView==channel_list && txt_progress.getVisibility()==View.GONE && !is_dlg && ly_right.getVisibility()==View.VISIBLE){
            if(channels.get(preview_pos).getId().equalsIgnoreCase(channels.get(position).getId())){
                ly_surface.setVisibility(View.VISIBLE);
                is_full = true;
                ly_buttons.setVisibility(View.GONE);
                ViewGroup.LayoutParams params = ly_surface.getLayoutParams();
                params.height = MyApp.SCREEN_HEIGHT+Utils.dp2px(getApplicationContext(),50);
                params.width = MyApp.SCREEN_WIDTH+Utils.dp2px(getApplicationContext(),50);
                ly_surface.setPadding(Utils.dp2px(this,0),Utils.dp2px(this,0),Utils.dp2px(this,0),Utils.dp2px(this,0));
                setMargins(ly_surface,Utils.dp2px(this,0),Utils.dp2px(this,0),Utils.dp2px(this,0),Utils.dp2px(this,0));
                ly_surface.setLayoutParams(params);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mHandler.removeCallbacks(mUpdateTimeTask);
                        updateProgressBar();
                        ly_bottom.setVisibility(View.VISIBLE);
                        listTimer();
                    }
                }, 2000);
            }else {
                MyApp.is_first = true;
                sub_pos = position;
                epg_pos = sub_pos;
                preview_pos = sub_pos;
                mainListAdapter.selectItem(sub_pos);
                playChannel();
                mEpgHandler.removeCallbacks(mEpgTicker);
                EpgTimer();
            }
        }else if(ly_right.getVisibility()==View.GONE && !is_dlg){
            ly_surface.setVisibility(View.VISIBLE);
            MyApp.is_first = true;
            sub_pos = position;
            epg_pos = sub_pos;
            preview_pos = sub_pos;
            mainListAdapter.selectItem(sub_pos);
            playChannel();
            is_full = true;
            ly_buttons.setVisibility(View.GONE);
            ViewGroup.LayoutParams params = ly_surface.getLayoutParams();
            params.height = MyApp.SCREEN_HEIGHT+Utils.dp2px(getApplicationContext(),50);
            params.width = MyApp.SCREEN_WIDTH+Utils.dp2px(getApplicationContext(),50);
            ly_surface.setPadding(Utils.dp2px(this,0),Utils.dp2px(this,0),Utils.dp2px(this,0),Utils.dp2px(this,0));
            setMargins(ly_surface,Utils.dp2px(this,0),Utils.dp2px(this,0),Utils.dp2px(this,0),Utils.dp2px(this,0));
            ly_surface.setLayoutParams(params);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mHandler.removeCallbacks(mUpdateTimeTask);
                    updateProgressBar();
                    ly_bottom.setVisibility(View.VISIBLE);
                    listTimer();
                }
            }, 2000);
        }
    }

    @Override
    public void onGetResultsData(JSONObject object, int request_code) {
        if(object!=null){
            if(request_code==100){
                txt_progress.setVisibility(View.GONE);
                channels = new ArrayList<>();
                try {
                    JSONObject channels_obj = object.getJSONObject("js");
                    max_items = channels_obj.getInt("max_page_items");
                    total_items = channels_obj.getInt("total_items");
                    if(total_items % max_items==0){
                        total_page = total_items/max_items;
                    }else {
                        total_page = total_items/max_items + 1;
                    }
                    if(!is_up){
                        sub_pos = channels_obj.getInt("selected_item")-1;
                        if(sub_pos<0){
                            sub_pos=0;
                        }
                    }else {
                        if(page<total_page){
                            sub_pos=max_items-1;
                        }else {
                            sub_pos = total_items % max_items - 1;
                        }
                    }
                    if(page==0){
                        page = channels_obj.getInt("cur_page");
                    }

                    JSONArray channels_array = channels_obj.getJSONArray("data");
                    if(channels_array.length()>0){
                        for(int i = 0;i<channels_array.length();i++){
                            JSONObject channel_obj = channels_array.getJSONObject(i);
                            try {
                                ChannelModel channelModel = new ChannelModel();
                                channelModel.setId(channel_obj.getString("id"));
                                channelModel.setNumber(channel_obj.getString("number"));
                                channelModel.setTv_genre_id(channel_obj.getString("tv_genre_id"));
                                channelModel.setName(channel_obj.getString("name"));
                                channelModel.setCmd(channel_obj.getString("cmd"));
                                channelModel.setLock(channel_obj.getInt("lock"));
                                channelModel.setFav(channel_obj.getInt("fav"));
                                channelModel.setArchive(channel_obj.getInt("archive"));
                                try {
                                    channelModel.setLogo(channel_obj.getString("logo"));
                                }catch (Exception e2){
                                    channelModel.setLogo("");
                                }
                                channels.add(channelModel);
                            }catch (Exception e1){
                                Log.e("error","parse_error");
                            }
                        }
                        mainListAdapter = new MainListAdapter(this,channels);
                        channel_list.setAdapter(mainListAdapter);
                        channel_list.setSelection(sub_pos);
                        mStream_id = channels.get(sub_pos).getId();
                        contentUri = channels.get(sub_pos).getCmd();
                        if(contentUri.contains("ffmpeg")){
                            contentUri = channels.get(sub_pos).getCmd().replace("ffmpeg ","");
                        }else if(contentUri.contains("auto")){
                            contentUri = channels.get(sub_pos).getCmd().replace("auto ","");
                        }
                        if(MyApp.user == null || MyApp.user.isEmpty()){
                            try {
                                MyApp.user = contentUri.split("live/")[1].split("/")[0];
                                MyApp.pass = contentUri.split("live/")[1].split("/")[1];
                            }catch (Exception e){
                                MyApp.user = contentUri.split("live/")[3];
                                MyApp.pass = contentUri.split("live/")[4];
                            }

                        }
                        if(is_first){
                            mainListAdapter.selectItem(sub_pos);
                            is_first = false;
                            if (libvlc != null) {
                                releaseMediaPlayer();
                                surfaceView = null;
                            }
                            surfaceView = (SurfaceView) findViewById(R.id.surface_view);
                            holder = surfaceView.getHolder();
                            holder.setFormat(PixelFormat.RGBX_8888);
                            holder.addCallback(this);
                            WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
                            final DisplayMetrics displayMetrics = new DisplayMetrics();
                            wm.getDefaultDisplay().getMetrics(displayMetrics);
                            int SCREEN_HEIGHT = displayMetrics.heightPixels;
                            int SCREEN_WIDTH = displayMetrics.widthPixels;
                            holder.setFixedSize(SCREEN_WIDTH, SCREEN_HEIGHT);
                            mVideoHeight = displayMetrics.heightPixels;
                            mVideoWidth = displayMetrics.widthPixels;
                            item_sel = true;
                            playVideo();
                        }else if(page==playModel.getPage()){
                            MyApp.is_first = true;
                            mainListAdapter.selectItem(playModel.getSub_pos());
                        }
                        if(MyApp.key){
                            MyApp.key = false;
                            MyApp.is_first = true;
                            mainListAdapter.selectItem(sub_pos);
                        }
                        mEpgHandler.removeCallbacks(mEpgTicker);
                        EpgTimer();
                    }
                }catch (Exception e){
                    Log.e("error","play_error");
                }
            }else if(request_code==200){
                epgModelList = new ArrayList<>();
                try {
                    JSONArray epg_array = object.getJSONArray("js");
                    if(epg_array.length()>0){
                        for(int i = 0;i<epg_array.length();i++){
                            JSONObject epg_obj = epg_array.getJSONObject(i);
                            try {
                                EpgModel epgModel = new EpgModel();
                                epgModel.setId(epg_obj.getString("id"));
                                epgModel.setCh_id(epg_obj.getString("ch_id"));
                                epgModel.setCorrect(epg_obj.getString("correct"));
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
                                epgModelList.add(epgModel);
                            }catch (Exception e1){
                                Log.e("error","parse_error");
                            }
                        }

                    }
                    if(is_full){
                        MyApp.is_first = true;
                        mHandler.removeCallbacks(mUpdateTimeTask);
                        updateProgressBar();
                        ly_bottom.setVisibility(View.VISIBLE);
                        if(channels.get(sub_pos).getLogo()!=null && !channels.get(sub_pos).getLogo().isEmpty()){
                            Picasso.with(PreviewChannelAcitivoty.this).load(channels.get(sub_pos).getLogo())
                                    .into(channel_logo);
                            channel_logo.setVisibility(View.VISIBLE);
                        }else {
                            channel_logo.setVisibility(View.GONE);
                        }

                        listTimer();
                    }
                }catch (Exception e){
                    Log.e("error","error");
                }
                printEpgData();
            }else if(request_code==300){
                if(MyApp.key && !is_full){
                    txt_progress.setVisibility(View.VISIBLE);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            page=0;
                            getChannels();
                        }
                    }, 8000);
                }
            }else if(request_code==400) {
                GetDataService dataService = new GetDataService(PreviewChannelAcitivoty.this);
                dataService.getData(PreviewChannelAcitivoty.this, Constants.GetUrl(this)+"portal.php?type=watchdog&action=get_events&cur_play_type=0&event_active_id=0&init=1&JsHttpRequest=1-xml", 500);
                dataService.onGetResultsData(PreviewChannelAcitivoty.this);
            }else if(request_code==500){
                if(MyApp.key && !is_full){
                    txt_progress.setVisibility(View.VISIBLE);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            page=0;
                            getChannels();
                        }
                    }, 8000);
                }
            }else if(request_code==600){

            }else if(request_code==700){
            }
        }
    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        View view = getCurrentFocus();
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()){
                case KeyEvent.KEYCODE_MENU:
                    PackageDlg packageDlg = new PackageDlg(PreviewChannelAcitivoty.this, pkg_datas, new PackageDlg.DialogPackageListener() {
                        @Override
                        public void OnItemClick(Dialog dialog, int position) {
                            dialog.dismiss();
                            switch (position) {
                                case 0:
                                    if(!is_full &&ly_viewdlg.getVisibility()==View.INVISIBLE){
                                        is_dlg = true;
                                        ly_sortdlg.setVisibility(View.INVISIBLE);
                                        ly_viewdlg.setVisibility(View.VISIBLE);
                                        channel_list.setFocusable(false);
                                        ly_viewdlg.requestFocus();
                                        ly_viewdlg.setFocusable(true);
                                        if(ly_right.getVisibility()==View.VISIBLE){
                                            btn_list_info.requestFocus();
                                        }else {
                                            btn_list.requestFocus();
                                        }
                                    }else if(!is_full){
                                        channel_list.requestFocus();
                                        channel_list.setFocusable(true);
                                        ly_viewdlg.setFocusable(false);
                                        is_dlg = false;
                                        ly_viewdlg.setVisibility(View.INVISIBLE);
                                    }
                                    break;
                                case 1:
                                    if(!is_full && ly_sortdlg.getVisibility()==View.INVISIBLE){
                                        is_dlg = true;
                                        if(is_up){
                                            is_up = false;
                                        }
                                        ly_viewdlg.setVisibility(View.INVISIBLE);
                                        ly_sortdlg.setVisibility(View.VISIBLE);
                                        ly_sortdlg.requestFocus();
                                        channel_list.setFocusable(false);
                                        if(sort==0){
                                            btn_num.requestFocus();
                                        }else if(sort==1){
                                            btn_title.requestFocus();
                                        }else {
                                            btn_fav.requestFocus();
                                        }
                                    }else if(!is_full){
                                        channel_list.setFocusable(true);
                                        channel_list.requestFocus();
                                        ly_sortdlg.setFocusable(false);
                                        is_dlg = false;
                                        ly_sortdlg.setVisibility(View.INVISIBLE);
                                    }
                                    break;
                                case 2:
                                    if(txt_progress.getVisibility()==View.GONE){
                                        is_fav = true;
                                        mStream_id = channels.get(sub_pos).getId();
                                        StringBuilder buffer = new StringBuilder();
                                        String res = null;
                                        boolean is_exit = false;
                                        for(int i = 0;i < favModelIds.size();i++){
                                            if(mStream_id.equalsIgnoreCase(favModelIds.get(i))){
                                                is_exit = true;
                                                favModelIds.remove(i);
                                                continue;
                                            }
                                            buffer.append(favModelIds.get(i)).append(",");
                                        }
                                        if(!is_exit){
                                            favModelIds.add(mStream_id);
                                            res = buffer.append(mStream_id).toString();
                                        }else {
                                            if(favModelIds.size()>0 && buffer.toString().endsWith(",")) {
                                                Log.e("res", buffer.toString());
                                                res = buffer.toString().substring(0, buffer.toString().length() - 1);
                                            }
                                        }
                                        if(favModelIds.size()==0){
                                            res = mStream_id;
                                            favModelIds = new ArrayList<>();
                                            favModelIds.add(res);
                                        }
                                        MyApp.favModelIds = favModelIds;
                                        if(channels.get(sub_pos).getFav()==0){
                                            channels.get(sub_pos).setFav(1);
                                        }else {
                                            channels.get(sub_pos).setFav(0);
                                        }
                                        channel_list.requestFocus();
                                        mainListAdapter = new MainListAdapter(PreviewChannelAcitivoty.this,channels);
                                        channel_list.setAdapter(mainListAdapter);
                                        channel_list.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                channel_list.setSelection(sub_pos);
                                                channel_list.smoothScrollToPosition(sub_pos);
                                            }
                                        });
                                        if(page == playModel.getPage()){
                                            MyApp.is_first = true;
                                            mainListAdapter.selectItem(playModel.getSub_pos());
                                        }
//                    url = Constants.GetUrl(this)+"portal.php?type=itv&action=set_fav&fav_ch="+res+"&JsHttpRequest=1-xml";
                                        GetDataService getDataService = new GetDataService(PreviewChannelAcitivoty.this);
                                        getDataService.getData(PreviewChannelAcitivoty.this,Constants.GetUrl(PreviewChannelAcitivoty.this)+"portal.php?type=itv&action=set_fav&fav_ch="+res+"&JsHttpRequest=1-xml",600);
                                        getDataService.onGetResultsData(PreviewChannelAcitivoty.this);
                                    }
                                case 3:
                                    if (subtraks != null) {
                                        if (subtraks.length > 0) {
                                            showSubTracksList();
                                        } else {
                                            Toast.makeText(getApplicationContext(),
                                                    "No subtitle or not loading yet", Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(),
                                                "No subtitle or not loading yet", Toast.LENGTH_LONG).show();
                                    }
                                    break;
                                case 4:
                                    if (traks != null) {
                                        if (traks.length > 0) {
                                            showAudioTracksList();
                                        } else {
                                            Toast.makeText(getApplicationContext(),
                                                    "No audio tracks or not loading yet", Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(),
                                                "No audio tracks or not loading yet", Toast.LENGTH_LONG).show();
                                    }
                                    break;
                                case 5:
                                    current_resolution++;
                                    if (current_resolution == resolutions.length)
                                        current_resolution = 0;

                                    mMediaPlayer.setAspectRatio(resolutions[current_resolution]);
                                    break;
                            }
                        }
                    });
                    packageDlg.show();
                    break;
                case KeyEvent.KEYCODE_BACK:
                    if(ly_viewdlg.getVisibility()==View.VISIBLE){
                        is_dlg = false;
                        channel_list.requestFocus();
                        channel_list.setFocusable(true);
                        ly_viewdlg.setVisibility(View.GONE);
                        return true;
                    }
                    if(ly_sortdlg.getVisibility()==View.VISIBLE){
                        is_dlg = false;
                        channel_list.requestFocus();
                        channel_list.setFocusable(true);
                        ly_sortdlg.setVisibility(View.GONE);
                        return true;
                    }
                    if(ly_bottom.getVisibility()==View.VISIBLE){
                        ly_bottom.setVisibility(View.GONE);
                        return true;
                    }
                    if(is_full){
                        is_full = false;
                        is_start = false;
                        if(MyApp.is_list){
                            ly_right.setVisibility(View.GONE);
                            ly_surface.setVisibility(View.GONE);
                        }else {
                            ly_right.setVisibility(View.VISIBLE);
                            ly_surface.setVisibility(View.VISIBLE);
                        }
                        ly_buttons.setVisibility(View.VISIBLE);
                        mHandler.removeCallbacks(mUpdateTimeTask);
                        ViewGroup.LayoutParams params = ly_surface.getLayoutParams();
                        params.height = MyApp.SURFACE_HEIGHT;
                        params.width = MyApp.SURFACE_WIDTH;
                        setMargins(ly_surface,0,MyApp.top_margin,MyApp.right_margin,0);
                        ly_surface.setPadding(15,15,15,15);
                        ly_surface.setLayoutParams(params);
                        if(MyApp.key){
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    page=0;
                                    getChannels();
                                }
                            }, 5000);
                        }
                        return true;
                    }
                    finish();
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if(!is_full){
                        channel_list.setFocusable(true);
                        MyApp.key = false;
                        mHandler.removeCallbacks(mUpdateTimeTask);
                        releaseMediaPlayer();
//                        Log.e("sub",String .valueOf(sub_pos));
                        String channel_name = channels.get(sub_pos).getName();
                        mStream_id = channels.get(sub_pos).getId();
                        Intent intent = new Intent(this,GuideActivity.class);
                        intent.putExtra("stream_id",mStream_id);
                        intent.putExtra("channel_name",channel_name);
                        startActivity(intent);
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if(!is_full){
                        MyApp.key = false;
                        mHandler.removeCallbacks(mUpdateTimeTask);
                        releaseMediaPlayer();
                        finish();
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
//                    Log.e("sub",String .valueOf(sub_pos));
                    mHandler.removeCallbacks(mTicker);
                    if(is_full &&  MyApp.key){
                        if(all_pos>0){
                            all_pos--;
                        }else {
                            all_pos = MyApp.channel_size-1;
                        }
                        sel_model = allChannel.get(all_pos);
                        mHandler.removeCallbacks(mUpdateTimeTask);
                        if (libvlc != null) {
                            releaseMediaPlayer();
                            surfaceView = null;
                        }
                        surfaceView = (SurfaceView) findViewById(R.id.surface_view);
                        holder = surfaceView.getHolder();
                        holder.setFormat(PixelFormat.RGBX_8888);
                        holder.addCallback(this);
                        mStream_id = sel_model.getId();
                        if(sel_model.getLogo()!=null && !sel_model.getLogo().isEmpty()){
                            Picasso.with(PreviewChannelAcitivoty.this).load(sel_model.getLogo())
                                    .into(channel_logo);
                            channel_logo.setVisibility(View.VISIBLE);
                        }else {
                            channel_logo.setVisibility(View.GONE);
                        }
                        contentUri = sel_model.getCmd();
                        if(contentUri.contains("ffmpeg")){
                            contentUri = sel_model.getCmd().replace("ffmpeg ","");
                        }else if(contentUri.contains("auto")){
                            contentUri = sel_model.getCmd().replace("auto ","");
                        }
                        item_sel = true;
                        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
                        final DisplayMetrics displayMetrics = new DisplayMetrics();
                        wm.getDefaultDisplay().getMetrics(displayMetrics);
                        int SCREEN_HEIGHT = displayMetrics.heightPixels;
                        int SCREEN_WIDTH = displayMetrics.widthPixels;
                        holder.setFixedSize(SCREEN_WIDTH, SCREEN_HEIGHT);
                        mVideoHeight = displayMetrics.heightPixels;
                        mVideoWidth = displayMetrics.widthPixels;
                        playVideo();
                        mEpgHandler.removeCallbacks(mEpgTicker);
                        EpgTimer();
                        listTimer();
//                        url = Constants.GetUrl(this)+"portal.php?type=itv&action=set_last_id&id="+mStream_id+"&JsHttpRequest=1-xml";
                        GetDataService dataService = new GetDataService(PreviewChannelAcitivoty.this);
                        dataService.getData(PreviewChannelAcitivoty.this,Constants.GetUrl(this)+"portal.php?type=itv&action=set_last_id&id="+mStream_id+"&JsHttpRequest=1-xml",300);
                        dataService.onGetResultsData(PreviewChannelAcitivoty.this);
                        return true;
                    }
                    if(is_full){
                        is_up = true;
                        is_start = false;
                        is_center = false;
                        MyApp.is_first = false;
                        if(txt_progress.getVisibility()==View.GONE){
                            if(page==1 && sub_pos==0){
                                page = total_page;
                                mHandler.removeCallbacks(mUpdateTimeTask);
                                is_first = true;
                                getChannels();
                            }else if(page>1 && sub_pos==0){
                                page--;
                                mHandler.removeCallbacks(mUpdateTimeTask);
                                is_first = true;
                                getChannels();
                            }else  if(sub_pos>0){
                                sub_pos--;
                                mStream_id = channels.get(sub_pos).getId();
                                mEpgHandler.removeCallbacks(mEpgTicker);
                                EpgTimer();
                                mHandler.removeCallbacks(mUpdateTimeTask);
                                MyApp.is_first = true;
                                channel_list.setSelection(sub_pos);
                                mainListAdapter.selectItem(sub_pos);
                                playChannel();
                            }
                        }
                        return true;
                    }
                    is_start = false;
                    MyApp.key = false;
                    if(view ==channel_list && !is_dlg){
                        is_up = false;
                        is_center = false;
                        MyApp.is_first = false;
                        if(txt_progress.getVisibility()==View.GONE){
                            if(page<total_page && sub_pos<max_items-1){
                                sub_pos++;
                                mStream_id = channels.get(sub_pos).getId();
                                mEpgHandler.removeCallbacks(mEpgTicker);
                                EpgTimer();
                                if(is_full){
                                    mHandler.removeCallbacks(mUpdateTimeTask);
                                    MyApp.is_first = true;
                                    mainListAdapter.selectItem(sub_pos);
                                    playChannel();
                                    if(channels.get(sub_pos).getLogo()!=null && !channels.get(sub_pos).getLogo().isEmpty()){
                                        Picasso.with(PreviewChannelAcitivoty.this).load(channels.get(sub_pos).getLogo())
                                                .into(channel_logo);
                                        channel_logo.setVisibility(View.VISIBLE);
                                    }else {
                                        channel_logo.setVisibility(View.GONE);
                                    }
                                }
                            }else if(page==total_page && sub_pos< (total_items%max_items - 1)){
                                sub_pos++;
                                mStream_id = channels.get(sub_pos).getId();
                                mEpgHandler.removeCallbacks(mEpgTicker);
                                EpgTimer();
                                if(is_full){
                                    mHandler.removeCallbacks(mUpdateTimeTask);
                                    MyApp.is_first = true;
                                    mainListAdapter.selectItem(sub_pos);
                                    playChannel();
                                    if(channels.get(sub_pos).getLogo()!=null && !channels.get(sub_pos).getLogo().isEmpty()){
                                        Picasso.with(PreviewChannelAcitivoty.this).load(channels.get(sub_pos).getLogo())
                                                .into(channel_logo);
                                        channel_logo.setVisibility(View.VISIBLE);
                                    }else {
                                        channel_logo.setVisibility(View.GONE);
                                    }
                                }
                            }else if(page<total_page && sub_pos==max_items-1){
                                page++;
                                if(is_full){
                                    mHandler.removeCallbacks(mUpdateTimeTask);
                                    is_first = true;
                                }
                                getChannels();

                            }else if(page==total_page && sub_pos== (total_items%max_items - 1)){
                                page=1;
                                if(is_full){
                                    mHandler.removeCallbacks(mUpdateTimeTask);
                                    is_first = true;
                                }
                                getChannels();
                            }
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_UP:
                    mHandler.removeCallbacks(mTicker);
                    if(is_full &&  MyApp.key){
                        if(all_pos<MyApp.channel_size-1){
                            all_pos++;
                        }else {
                            all_pos = 0;
                        }
                        sel_model = allChannel.get(all_pos);
                        mHandler.removeCallbacks(mUpdateTimeTask);
                        if (libvlc != null) {
                            releaseMediaPlayer();
                            surfaceView = null;
                        }
                        surfaceView = (SurfaceView) findViewById(R.id.surface_view);
                        holder = surfaceView.getHolder();
                        holder.setFormat(PixelFormat.RGBX_8888);
                        holder.addCallback(this);
                        mStream_id = sel_model.getId();
                        if(sel_model.getLogo()!=null && !sel_model.getLogo().isEmpty()){
                            Picasso.with(PreviewChannelAcitivoty.this).load(sel_model.getLogo())
                                    .into(channel_logo);
                            channel_logo.setVisibility(View.VISIBLE);
                        }else {
                            channel_logo.setVisibility(View.GONE);
                        }
                        contentUri = sel_model.getCmd();
                        if(contentUri.contains("ffmpeg")){
                            contentUri = sel_model.getCmd().replace("ffmpeg ","");
                        }else if(contentUri.contains("auto")){
                            contentUri = sel_model.getCmd().replace("auto ","");
                        }
                        item_sel = true;
                        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
                        final DisplayMetrics displayMetrics = new DisplayMetrics();
                        wm.getDefaultDisplay().getMetrics(displayMetrics);
                        int SCREEN_HEIGHT = displayMetrics.heightPixels;
                        int SCREEN_WIDTH = displayMetrics.widthPixels;
                        holder.setFixedSize(SCREEN_WIDTH, SCREEN_HEIGHT);
                        mVideoHeight = displayMetrics.heightPixels;
                        mVideoWidth = displayMetrics.widthPixels;
                        playVideo();
                        mEpgHandler.removeCallbacks(mEpgTicker);
                        EpgTimer();
                        listTimer();
//                        url = Constants.GetUrl(this)+"portal.php?type=itv&action=set_last_id&id="+mStream_id+"&JsHttpRequest=1-xml";
                        GetDataService dataService = new GetDataService(PreviewChannelAcitivoty.this);
                        dataService.getData(PreviewChannelAcitivoty.this,Constants.GetUrl(this)+"portal.php?type=itv&action=set_last_id&id="+mStream_id+"&JsHttpRequest=1-xml",300);
                        dataService.onGetResultsData(PreviewChannelAcitivoty.this);
                        return true;
                    }
                    if(is_full){
                        is_center = false;
                        is_up = false;
                        MyApp.is_first = false;
                        if(txt_progress.getVisibility()==View.GONE){
                            if(page<total_page && sub_pos<max_items-1){
                                sub_pos++;
                                mStream_id = channels.get(sub_pos).getId();
                                mEpgHandler.removeCallbacks(mEpgTicker);
                                EpgTimer();
                                mHandler.removeCallbacks(mUpdateTimeTask);
                                MyApp.is_first = true;
                                channel_list.setSelection(sub_pos);
                                mainListAdapter.selectItem(sub_pos);
                                playChannel();
                                if(channels.get(sub_pos).getLogo()!=null && !channels.get(sub_pos).getLogo().isEmpty()){
                                    Picasso.with(PreviewChannelAcitivoty.this).load(channels.get(sub_pos).getLogo())
                                            .into(channel_logo);
                                    channel_logo.setVisibility(View.VISIBLE);
                                }else {
                                    channel_logo.setVisibility(View.GONE);
                                }
                            }else if(page==total_page && sub_pos< (total_items%max_items - 1)){
                                sub_pos++;
                                mStream_id = channels.get(sub_pos).getId();
                                mEpgHandler.removeCallbacks(mEpgTicker);
                                EpgTimer();
                                mHandler.removeCallbacks(mUpdateTimeTask);
                                MyApp.is_first = true;
                                channel_list.setSelection(sub_pos);
                                mainListAdapter.selectItem(sub_pos);
                                playChannel();
                                if(channels.get(sub_pos).getLogo()!=null && !channels.get(sub_pos).getLogo().isEmpty()){
                                    Picasso.with(PreviewChannelAcitivoty.this).load(channels.get(sub_pos).getLogo())
                                            .into(channel_logo);
                                    channel_logo.setVisibility(View.VISIBLE);
                                }else {
                                    channel_logo.setVisibility(View.GONE);
                                }
                            }else if(page<total_page && sub_pos==max_items-1){
                                page++;
                                mHandler.removeCallbacks(mUpdateTimeTask);
                                is_first = true;
                                getChannels();
                            }else if(page==total_page && sub_pos== (total_items%max_items - 1)){
                                page=1;
                                mHandler.removeCallbacks(mUpdateTimeTask);
                                is_first = true;
                                getChannels();
                            }
                        }
                        return true;
                    }
                    MyApp.key = false;
                    if(view == channel_list && !is_dlg){
                        is_up = true;
                        is_start = false;
                        is_center = false;
                        MyApp.is_first = false;
                        if(txt_progress.getVisibility()==View.GONE){
                            if(page==1 && sub_pos==0){
                                page = total_page;
                                if(is_full){
                                    mHandler.removeCallbacks(mUpdateTimeTask);
                                    is_first = true;
                                }
                                getChannels();
                            }else if(page>1 && sub_pos==0){
                                page--;
                                if(is_full){
                                    mHandler.removeCallbacks(mUpdateTimeTask);
                                    is_first = true;
                                }
                                getChannels();
                            }else  if(sub_pos>0){
                                sub_pos--;
                                mStream_id = channels.get(sub_pos).getId();
                                mEpgHandler.removeCallbacks(mEpgTicker);
                                EpgTimer();
                                if(is_full){
                                    mHandler.removeCallbacks(mUpdateTimeTask);
                                    MyApp.is_first = true;
                                    mainListAdapter.selectItem(sub_pos);
                                    playChannel();
                                }
                            }
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_CENTER:
                    if(is_full){
                        if(ly_bottom.getVisibility()==View.VISIBLE){
                            ly_bottom.setVisibility(View.GONE);
                        }else {
                            ly_bottom.setVisibility(View.VISIBLE);
                        }
                        return true;
                    }
                    break;
                case KeyEvent.KEYCODE_0:
                    MyApp.key = true;
                    if (num_txt.getVisibility() == View.GONE) num_txt.setVisibility(View.VISIBLE);
                    if (!key.isEmpty()) {
                        key += "0";
                        move_pos = parseInt(key);
                        if (move_pos > MyApp.channel_size) {
                            num_txt.setText("");
                            key = "";
                            move_pos = 0;
                            moveHandler.removeCallbacks(moveTicker);
                        } else {
                            moveHandler.removeCallbacks(moveTicker);
                            num_txt.setText(key);
                            moveTimer();
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_1:
                    MyApp.key = true;
                    if (num_txt.getVisibility() == View.GONE)
                        num_txt.setVisibility(View.VISIBLE);
                    key += "1";
                    move_pos = parseInt(key);
                    if (move_pos > MyApp.channel_size - 1) {
                        key = "";
                        num_txt.setText("");
                        move_pos = 0;
                        moveHandler.removeCallbacks(moveTicker);
                    } else {
                        moveHandler.removeCallbacks(moveTicker);
                        num_txt.setText(key);
                        moveTimer();

                    }
                    break;
                case KeyEvent.KEYCODE_2:
                    MyApp.key = true;
                    if (num_txt.getVisibility() == View.GONE)
                        num_txt.setVisibility(View.VISIBLE);
                    key += "2";
                    move_pos = parseInt(key);
                    if (move_pos > MyApp.channel_size - 1) {
                        key = "";
                        num_txt.setText("");
                        move_pos = 0;
                        moveHandler.removeCallbacks(moveTicker);
                    } else {
                        moveHandler.removeCallbacks(moveTicker);
                        num_txt.setText(key);
                        moveTimer();
                    }
                    break;
                case KeyEvent.KEYCODE_3:
                    MyApp.key = true;
                    if (num_txt.getVisibility() == View.GONE)
                        num_txt.setVisibility(View.VISIBLE);
                    key += "3";
                    move_pos = parseInt(key);
                    if (move_pos > MyApp.channel_size - 1) {
                        key = "";
                        num_txt.setText("");
                        move_pos = 0;
                        moveHandler.removeCallbacks(moveTicker);
                    } else {
                        moveHandler.removeCallbacks(moveTicker);
                        num_txt.setText(key);
                        moveTimer();
                    }
                    break;
                case KeyEvent.KEYCODE_4:
                    MyApp.key = true;
                    if (num_txt.getVisibility() == View.GONE)
                        num_txt.setVisibility(View.VISIBLE);
                    key += "4";
                    move_pos = parseInt(key);
                    if (move_pos > MyApp.channel_size - 1) {
                        key = "";
                        num_txt.setText("");
                        move_pos = 0;
                        moveHandler.removeCallbacks(moveTicker);
                    } else {
                        moveHandler.removeCallbacks(moveTicker);
                        num_txt.setText(key);
                        moveTimer();
                    }
                    break;
                case KeyEvent.KEYCODE_5:
                    MyApp.key = true;
                    if (num_txt.getVisibility() == View.GONE) num_txt.setVisibility(View.VISIBLE);
                    key += "5";
                    move_pos = parseInt(key);
                    if (move_pos > MyApp.channel_size - 1) {
                        key = "";
                        num_txt.setText("");
                        move_pos = 0;
                        moveHandler.removeCallbacks(moveTicker);
                    } else {
                        moveHandler.removeCallbacks(moveTicker);
                        num_txt.setText(key);
                        moveTimer();
                    }
                    break;
                case KeyEvent.KEYCODE_6:
                    MyApp.key = true;
                    if (num_txt.getVisibility() == View.GONE) num_txt.setVisibility(View.VISIBLE);
                    key += "6";
                    move_pos = parseInt(key);
                    if (move_pos > MyApp.channel_size - 1) {
                        key = "";
                        num_txt.setText("");
                        move_pos = 0;
                        moveHandler.removeCallbacks(moveTicker);
                    } else {
                        moveHandler.removeCallbacks(moveTicker);
                        num_txt.setText(key);
                        moveTimer();
                    }
                    break;
                case KeyEvent.KEYCODE_7:
                    MyApp.key = true;
                    if (num_txt.getVisibility() == View.GONE) num_txt.setVisibility(View.VISIBLE);
                    key += "7";
                    move_pos = parseInt(key);
                    if (move_pos > MyApp.channel_size - 1) {
                        key = "";
                        num_txt.setText("");
                        move_pos = 0;
                        moveHandler.removeCallbacks(moveTicker);
                    } else {
                        moveHandler.removeCallbacks(moveTicker);
                        num_txt.setText(key);
                        moveTimer();
                    }
                    break;
                case KeyEvent.KEYCODE_8:
                    MyApp.key = true;
                    if (num_txt.getVisibility() == View.GONE) num_txt.setVisibility(View.VISIBLE);
                    key += "8";
                    move_pos = parseInt(key);
                    if (move_pos > MyApp.channel_size - 1) {
                        key = "";
                        num_txt.setText("");
                        move_pos = 0;
                        moveHandler.removeCallbacks(moveTicker);
                    } else {
                        moveHandler.removeCallbacks(moveTicker);
                        num_txt.setText(key);
                        moveTimer();
                    }
                    break;
                case KeyEvent.KEYCODE_9:
                    MyApp.key = true;
                    if (num_txt.getVisibility() == View.GONE) num_txt.setVisibility(View.VISIBLE);
                    key += "9";
                    move_pos = parseInt(key);
                    if (move_pos > MyApp.channel_size - 1) {
                        key = "";
                        num_txt.setText("");
                        move_pos = 0;
                        moveHandler.removeCallbacks(moveTicker);
                    } else {
                        moveHandler.removeCallbacks(moveTicker);
                        num_txt.setText(key);
                        moveTimer();
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
    int moveTime;
    private void moveTimer() {
        moveTime = 2;
        moveTicker = new Runnable() {
            public void run() {
                if(moveTime==1){
                    if(is_full){
                        for(int i = 0;i<allChannel.size();i++){
                            if (parseInt(allChannel.get(i).getNumber()) == move_pos) {
                                sel_model = allChannel.get(i);
                                all_pos = i;
                                break;
                            }
                        }
                        if (sel_model == null) {
                            MyApp.key = false;
                            key = "";
                            num_txt.setText("");
                            num_txt.setVisibility(View.GONE);
                            return;
                        }else {
                            key = "";
                            num_txt.setText("");
                            num_txt.setVisibility(View.GONE);
                            mHandler.removeCallbacks(mUpdateTimeTask);
                            if (libvlc != null) {
                                releaseMediaPlayer();
                                surfaceView = null;
                            }
                            surfaceView = (SurfaceView) findViewById(R.id.surface_view);
                            holder = surfaceView.getHolder();
                            holder.setFormat(PixelFormat.RGBX_8888);
                            holder.addCallback(PreviewChannelAcitivoty.this);
                            mStream_id = sel_model.getId();
                            if(sel_model.getLogo()!=null && !sel_model.getLogo().isEmpty()){
                                Picasso.with(PreviewChannelAcitivoty.this).load(sel_model.getLogo()).into(channel_logo);
                                channel_logo.setVisibility(View.VISIBLE);
                            }else {
                                channel_logo.setVisibility(View.GONE);
                            }
                            contentUri = sel_model.getCmd();
                            if(contentUri.contains("ffmpeg")){
                                contentUri = sel_model.getCmd().replace("ffmpeg ","");
                            }else if(contentUri.contains("auto")){
                                contentUri = sel_model.getCmd().replace("auto ","");
                            }
                            item_sel = true;
                            WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
                            final DisplayMetrics displayMetrics = new DisplayMetrics();
                            wm.getDefaultDisplay().getMetrics(displayMetrics);
                            int SCREEN_HEIGHT = displayMetrics.heightPixels;
                            int SCREEN_WIDTH = displayMetrics.widthPixels;
                            holder.setFixedSize(SCREEN_WIDTH, SCREEN_HEIGHT);
                            mVideoHeight = displayMetrics.heightPixels;
                            mVideoWidth = displayMetrics.widthPixels;
                            playVideo();
                            getEpg();
                            listTimer();
//                            url = Constants.GetUrl(getApplicationContext())+"portal.php?type=itv&action=set_last_id&id="+mStream_id+"&JsHttpRequest=1-xml";
                            GetDataService dataService = new GetDataService(PreviewChannelAcitivoty.this);
                            dataService.getData(PreviewChannelAcitivoty.this,Constants.GetUrl(getApplicationContext())+"portal.php?type=itv&action=set_last_id&id="+mStream_id+"&JsHttpRequest=1-xml",300);
                            dataService.onGetResultsData(PreviewChannelAcitivoty.this);
                        }
                    }else {
                        for(int i = 0;i<allChannel.size();i++){
                            if (parseInt(allChannel.get(i).getNumber()) == move_pos) {
                                sel_model = allChannel.get(i);
                                if(sel_model.getTv_genre_id().equalsIgnoreCase(tvGenreModelList.get(channel_pos).getId())){
                                    sel_model = allChannel.get(i);
                                    all_pos = i;
                                }else {
                                    sel_model = null;
                                }
                                break;
                            }
                        }
                        if (sel_model == null) {
                            MyApp.key = false;
                            key = "";
                            num_txt.setText("");
                            num_txt.setVisibility(View.GONE);
                            Toast.makeText(PreviewChannelAcitivoty.this,"This category do not have this channel",Toast.LENGTH_SHORT).show();
                            return;
                        }else {
                            key = "";
                            num_txt.setText("");
                            num_txt.setVisibility(View.GONE);
                            mHandler.removeCallbacks(mUpdateTimeTask);
                            if (libvlc != null) {
                                releaseMediaPlayer();
                                surfaceView = null;
                            }
                            surfaceView = (SurfaceView) findViewById(R.id.surface_view);
                            holder = surfaceView.getHolder();
                            holder.setFormat(PixelFormat.RGBX_8888);
                            holder.addCallback(PreviewChannelAcitivoty.this);
                            mStream_id = sel_model.getId();
                            contentUri = sel_model.getCmd();
                            if(sel_model.getLogo()!=null && !sel_model.getLogo().isEmpty()){
                                Picasso.with(PreviewChannelAcitivoty.this).load(sel_model.getLogo()).into(channel_logo);
                                channel_logo.setVisibility(View.VISIBLE);
                            }else {
                                channel_logo.setVisibility(View.GONE);
                            }
                            if(contentUri.contains("ffmpeg")){
                                contentUri = sel_model.getCmd().replace("ffmpeg ","");
                            }else if(contentUri.contains("auto")){
                                contentUri = sel_model.getCmd().replace("auto ","");
                            }
                            item_sel = true;
                            WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
                            final DisplayMetrics displayMetrics = new DisplayMetrics();
                            wm.getDefaultDisplay().getMetrics(displayMetrics);
                            int SCREEN_HEIGHT = displayMetrics.heightPixels;
                            int SCREEN_WIDTH = displayMetrics.widthPixels;
                            holder.setFixedSize(SCREEN_WIDTH, SCREEN_HEIGHT);
                            mVideoHeight = displayMetrics.heightPixels;
                            mVideoWidth = displayMetrics.widthPixels;
                            playVideo();
                            getEpg();
                            listTimer();
//                            url = Constants.GetUrl(getApplicationContext())+"portal.php?type=itv&action=set_last_id&id="+mStream_id+"&JsHttpRequest=1-xml";
                            GetDataService dataService = new GetDataService(PreviewChannelAcitivoty.this);
                            dataService.getData(PreviewChannelAcitivoty.this,Constants.GetUrl(getApplicationContext())+"portal.php?type=itv&action=set_last_id&id="+mStream_id+"&JsHttpRequest=1-xml",300);
                            dataService.onGetResultsData(PreviewChannelAcitivoty.this);
                        }
                    }
                    return;
                }
                moveNextTicker();
            }
        };
        moveTicker.run();
    }
    private void moveNextTicker() {
        moveTime--;
        long next = SystemClock.uptimeMillis() + 1000;
        moveHandler.postAtTime(moveTicker, next);
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
    private void playChannel(){
        if (libvlc != null) {
            releaseMediaPlayer();
            surfaceView = null;
        }
        surfaceView = (SurfaceView) findViewById(R.id.surface_view);
        holder = surfaceView.getHolder();
        holder.setFormat(PixelFormat.RGBX_8888);
        holder.addCallback(this);
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        int SCREEN_HEIGHT = displayMetrics.heightPixels;
        int SCREEN_WIDTH = displayMetrics.widthPixels;
        holder.setFixedSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        mVideoHeight = displayMetrics.heightPixels;
        mVideoWidth = displayMetrics.widthPixels;

        mStream_id = channels.get(sub_pos).getId();
        contentUri = channels.get(sub_pos).getCmd();
        if(channels.get(sub_pos).getLogo()!=null && !channels.get(sub_pos).getLogo().isEmpty()){
            Picasso.with(PreviewChannelAcitivoty.this).load(channels.get(sub_pos).getLogo())
                    .into(channel_logo);
            channel_logo.setVisibility(View.VISIBLE);
        }else {
            channel_logo.setVisibility(View.GONE);
        }
        if(contentUri.contains("ffmpeg")){
            contentUri = channels.get(sub_pos).getCmd().replace("ffmpeg ","");
        }else if(contentUri.contains("auto")){
            contentUri = channels.get(sub_pos).getCmd().replace("auto ","");
        }
        item_sel = true;

        playVideo();
        channel_list.requestFocus();
//        url = Constants.GetUrl(this)+"portal.php?type=itv&action=set_last_id&id="+mStream_id+"&JsHttpRequest=1-xml";
        GetDataService dataService = new GetDataService(PreviewChannelAcitivoty.this);
        dataService.getData(PreviewChannelAcitivoty.this,Constants.GetUrl(this)+"portal.php?type=itv&action=set_last_id&id="+mStream_id+"&JsHttpRequest=1-xml",300);
        dataService.onGetResultsData(PreviewChannelAcitivoty.this);
    }
    private void showAudioTracksList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PreviewChannelAcitivoty.this);
        builder.setTitle("Audio track");

        ArrayList<String> names = new ArrayList<>();
        for (int i = 0; i < traks.length; i++) {
            names.add(traks[i].name);
        }
        String[] audioTracks = names.toArray(new String[0]);

        SharedPreferences pref = getSharedPreferences("PREF_AUDIO_TRACK", MODE_PRIVATE);
        int checkedItem = pref.getInt("AUDIO_TRACK", 0);
        builder.setSingleChoiceItems(audioTracks, checkedItem,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selected_item = which;
                    }
                });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences pref = getSharedPreferences("PREF_AUDIO_TRACK", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt("AUDIO_TRACK", selected_item);
                editor.commit();

                mMediaPlayer.setAudioTrack(traks[selected_item].id);
            }
        });
        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showSubTracksList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PreviewChannelAcitivoty.this);
        builder.setTitle("Subtitle");

        ArrayList<String> names = new ArrayList<>();
        for (int i = 0; i < subtraks.length; i++) {
            names.add(subtraks[i].name);
        }
        String[] audioTracks = names.toArray(new String[0]);

        SharedPreferences pref = getSharedPreferences("PREF_SUB_TRACK", MODE_PRIVATE);
        int checkedItem = pref.getInt("SUB_TRACK", 0);
        builder.setSingleChoiceItems(audioTracks, checkedItem,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selected_item = which;
                    }
                });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences pref = getSharedPreferences("PREF_SUB_TRACK", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt("SUB_TRACK", selected_item);
                editor.commit();
                mMediaPlayer.setSpuTrack(subtraks[selected_item].id);
            }
        });
        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
