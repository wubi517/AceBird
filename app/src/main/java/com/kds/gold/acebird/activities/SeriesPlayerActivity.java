package com.kds.gold.acebird.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;


import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;

import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kds.gold.acebird.R;
import com.kds.gold.acebird.apps.Constants;

import com.kds.gold.acebird.apps.MyApp;
import com.kds.gold.acebird.async.GetDataService;

import com.kds.gold.acebird.dialog.PackageDlg;
import com.kds.gold.acebird.utils.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;
import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


public class SeriesPlayerActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, IVLCVout.Callback,SurfaceHolder.Callback,
        GetDataService.OnGetResultsListener,View.OnClickListener{


    SurfaceView surfaceView;
    SurfaceView remote_subtitles_surface;
    MediaPlayer mMediaPlayer = null;
    public int mHeight;
    public int mWidth;
    LibVLC libvlc;

    SurfaceHolder holder;
    LinearLayout bottom_lay, def_lay,ly_play,ly_resolution,ly_audio,ly_subtitle;

    ImageView img_play;


    MediaPlayer.TrackDescription[] traks;
    MediaPlayer.TrackDescription[] subtraks;
    String ratio;
    String[] resolutions ;
    int current_resolution = 0;
    boolean first = true;

    TextView title_txt, start_txt, end_txt;
    ImageView imageView;
    SeekBar seekBar;
    Handler mHandler = new Handler();
    Handler handler = new Handler();
    Runnable mTicker;
    String cont_url;
    int dration_time = 0,episode_num,selected_item = 0;
    boolean is_create=true,item_sel = false,is_all;
    String title,season,total_title,cmd;
    List<String> pkg_datas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vod_player);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        pkg_datas = new ArrayList<>();
        for (int i = 0; i < getResources().getStringArray(R.array.package_list1).length; i++) {
            pkg_datas.add(getResources().getStringArray(R.array.package_list1)[i]);
        }
        RelativeLayout main_lay = (RelativeLayout) findViewById(R.id.main_lay);

        surfaceView = (SurfaceView) findViewById(R.id.surface_view);
        surfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bottom_lay.getVisibility()==View.GONE){
                    bottom_lay.setVisibility(View.VISIBLE);
                }else {
                    bottom_lay.setVisibility(View.GONE);
                }
            }
        });
        holder = surfaceView.getHolder();
        holder.addCallback(this);
        holder.setFormat(PixelFormat.RGBX_8888);
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        int SCREEN_HEIGHT = displayMetrics.heightPixels;
        int SCREEN_WIDTH = displayMetrics.widthPixels;
        holder.setFixedSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        mHeight = displayMetrics.heightPixels;
        mWidth = displayMetrics.widthPixels;

        ratio = mWidth + ":"+ mHeight;
        resolutions =  new String[]{"16:9", "4:3", ratio};

        def_lay = (LinearLayout) findViewById(R.id.def_lay);
        bottom_lay = (LinearLayout) findViewById(R.id.vod_bottom_lay);
        title_txt = (TextView) findViewById(R.id.vod_channel_title);
        imageView = (ImageView) findViewById(R.id.vod_channel_img);
        start_txt = (TextView) findViewById(R.id.vod_start_time);
        end_txt = (TextView) findViewById(R.id.vod_end_time);
        seekBar = (SeekBar) findViewById(R.id.vod_seekbar);
        seekBar.setOnSeekBarChangeListener(this);

        remote_subtitles_surface = findViewById(R.id.remote_subtitles_surface);
        remote_subtitles_surface.setZOrderMediaOverlay(true);
        remote_subtitles_surface.getHolder().setFormat(PixelFormat.TRANSLUCENT);

        ly_audio = findViewById(R.id.ly_audio);

        ly_play = findViewById(R.id.ly_play);
        ly_resolution = findViewById(R.id.ly_resolution);
        ly_subtitle = findViewById(R.id.ly_subtitle);

        ly_subtitle.setOnClickListener(this);
        ly_play.setOnClickListener(this);
        ly_resolution.setOnClickListener(this);
        ly_subtitle.setOnClickListener(this);
        ly_audio.setOnClickListener(this);

        img_play = findViewById(R.id.img_play);

        cmd = getIntent().getStringExtra("cmd");
        is_all = getIntent().getBooleanExtra("all",false);
        episode_num = getIntent().getIntExtra("episode_num",0);
        getUrl();

        if(season==null){
            total_title = title;
        }else {
            total_title = season+title;
        }
        try {
            Picasso.with(this).load(getIntent().getStringExtra("img")).into(imageView);
        }catch (Exception e){
            Picasso.with(this).load(R.drawable.icon_default).into(imageView);
        }

    }

    private void getUrl(){
        GetDataService dataService = new GetDataService(SeriesPlayerActivity.this);
        dataService.getData(SeriesPlayerActivity.this,Constants.GetUrl(this)+"portal.php?type=vod&action=create_link&cmd="+cmd+"&series="+episode_num+"&forced_storage=&disable_ad=0&download=0&force_ch_link_check=0&JsHttpRequest=1-xml",100);
        dataService.onGetResultsData(SeriesPlayerActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!is_create) {
            getUrl();
        } else {
            is_create = false;
        }
    }
    @Override
    protected void onDestroy() {
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
    @Override
    protected void onUserLeaveHint()
    {
        releaseMediaPlayer();
        finish();
        super.onUserLeaveHint();
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

        mWidth = 0;
        mHeight = 0;
    }

    private void playVideo(){
        if(def_lay.getVisibility()==View.VISIBLE)def_lay.setVisibility(View.GONE);
        releaseMediaPlayer();
//        Log.e("url",path);
        toggleFullscreen(true);
        try {

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

            // Creating media player
            mMediaPlayer = new MediaPlayer(libvlc);
            mMediaPlayer.setEventListener(mPlayerListener);
            mMediaPlayer.setAspectRatio(MyApp.SCREEN_WIDTH+":"+MyApp.SCREEN_HEIGHT);


            // Seting up video output
            final IVLCVout vout = mMediaPlayer.getVLCVout();
            vout.setVideoView(surfaceView);
            if (remote_subtitles_surface != null)
                vout.setSubtitlesView(remote_subtitles_surface);

            //vout.setSubtitlesView(mSurfaceSubtitles);
            vout.setWindowSize(mWidth, mHeight);
            vout.addCallback(this);
            vout.attachViews();
//            vout.setSubtitlesView(tv_subtitle);


            Media m = new Media(libvlc, Uri.parse(cont_url));
            mMediaPlayer.setMedia(m);
            mMediaPlayer.play();

        } catch (Exception e) {
            Toast.makeText(this, "Error in creating player!", Toast
                    .LENGTH_LONG).show();
        }

    }
    private MediaPlayer.EventListener mPlayerListener = new MediaPlayerListener(this);

    private static class MediaPlayerListener implements MediaPlayer.EventListener {
        private WeakReference<SeriesPlayerActivity> mOwner;

        public MediaPlayerListener(SeriesPlayerActivity owner) {
            mOwner = new WeakReference<SeriesPlayerActivity>(owner);
        }

        @Override
        public void onEvent(MediaPlayer.Event event) {
            SeriesPlayerActivity player = mOwner.get();
            switch (event.type) {
                case MediaPlayer.Event.EndReached:
                    //동영상 끝까지 재생되었다면..
                    player.releaseMediaPlayer();
                    player.is_create = false;
                    player.onResume();
                    break;
                case MediaPlayer.Event.Playing:
//                    Toast.makeText(player, "Playing", Toast.LENGTH_SHORT).show();
                    break;
                case MediaPlayer.Event.Paused:
                case MediaPlayer.Event.Stopped:
//                    Toast.makeText(player, "Stop", Toast.LENGTH_SHORT).show();
                    break;
                case MediaPlayer.Event.Buffering:
//                    Toast.makeText(player, "Buffering", Toast.LENGTH_SHORT).show();
                    break;
                case MediaPlayer.Event.EncounteredError:
//                    Toast.makeText(player, "Error", Toast.LENGTH_SHORT).show();
                    player.def_lay.setVisibility(View.VISIBLE);
                    break;

                //아래 두 이벤트는 계속 발생됨
                case MediaPlayer.Event.TimeChanged: //재생 시간 변화
                    break;
                case MediaPlayer.Event.PositionChanged: //동영상 재생 구간 변화시
                    //Log.d(TAG, "PositionChanged");
                    break;
                default:
                    break;
            }
        }
    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_DPAD_CENTER:
                    if (mMediaPlayer.isPlaying()) {
                        mMediaPlayer.pause();
                        img_play.setImageResource(R.drawable.exo_play);
                    } else {
                        mMediaPlayer.play();
                        img_play.setImageResource(R.drawable.exo_pause);
                    }
                    if (bottom_lay.getVisibility() == View.GONE) bottom_lay.setVisibility(View.VISIBLE);
                    updateTimer();
                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    long curr_pos = mMediaPlayer.getTime();
                    long max_pos = mMediaPlayer.getLength();
                    dration_time += 30;
                    if (curr_pos < dration_time * 1000)
                        mMediaPlayer.setTime(1);
                    else {
                        int st = (int) (curr_pos - (long) dration_time * 1000);
                        mMediaPlayer.setTime(st);
                    }
                    dration_time = 0;
                    updateProgressBar();
                    updateTimer();
                    if (bottom_lay.getVisibility() == View.GONE) bottom_lay.setVisibility(View.VISIBLE);
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    dration_time += 30;
                    long curr_pos1 = mMediaPlayer.getTime();
                    long max_pos1 = mMediaPlayer.getLength();
                    if (max_pos1 < dration_time * 1000)
                        mMediaPlayer.setTime((int) (max_pos1 - 10));
                    else mMediaPlayer.setTime((int) (curr_pos1 + (long) dration_time * 1000));
                    dration_time = 0;
                    updateProgressBar();
                    updateTimer();
                    if (bottom_lay.getVisibility() == View.GONE) bottom_lay.setVisibility(View.VISIBLE);
                    break;
                case KeyEvent.KEYCODE_BACK:
                    finish();
                    break;
                case KeyEvent.KEYCODE_MENU:
                    PackageDlg packageDlg = new PackageDlg(SeriesPlayerActivity.this, pkg_datas, new PackageDlg.DialogPackageListener() {
                        @Override
                        public void OnItemClick(Dialog dialog, int position) {
                            dialog.dismiss();
                            switch (position) {
                                case 0:
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
                                case 1:
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
                                case 2:
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

            }
        }
        return super.dispatchKeyEvent(event);
    }

    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            if (mMediaPlayer != null) {
                long totalDuration = mMediaPlayer.getLength();
                long currentDuration = mMediaPlayer.getTime();
                end_txt.setText("" + Utils.milliSecondsToTimer(totalDuration));
                start_txt.setText("" + Utils.milliSecondsToTimer(currentDuration));
                int progress = (int) (Utils.getProgressPercentage(currentDuration, totalDuration));
                seekBar.setProgress(progress);
                mHandler.postDelayed(this, 500);
            }
        }
    };



    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        long totalDuration = mMediaPlayer.getLength();
        int currentPosition = Utils.progressToTimer(seekBar.getProgress(), totalDuration);
        mMediaPlayer.setTime(currentPosition);
        updateProgressBar();
    }

    private void updateTimer() {
        handler.removeCallbacks(mTicker);
        startTimer();
    }

    int maxTime;
    private void startTimer() {
        maxTime = 10;
        mTicker = new Runnable() {
            public void run() {
                if (maxTime < 1) {
                    if (bottom_lay.getVisibility() == View.VISIBLE)
                        bottom_lay.setVisibility(View.GONE);
                    return;
                }
                runNextTicker();
            }
        };
        mTicker.run();
    }

    private void runNextTicker() {
        maxTime --;
        long next = SystemClock.uptimeMillis() + 1000;
        handler.postAtTime(mTicker, next);
    }

    @Override
    public void onGetResultsData(JSONObject object, int request_code) {
        if(object!=null){
            try {
                JSONObject vod_obj = object.getJSONObject("js");
                cont_url = vod_obj.getString("cmd").replace("ffmpeg ","");
                if (mMediaPlayer != null) {
                    releaseMediaPlayer();
                    surfaceView = null;
                    holder.removeCallback(this);
                }
                surfaceView = (SurfaceView) findViewById(R.id.surface_view);
                holder = surfaceView.getHolder();
                holder.setFormat(PixelFormat.RGBX_8888);
                holder.addCallback(this);
                item_sel = true;
                playVideo();
            }catch (Exception e){
            }
        }
    }

    @Override
    public void onSurfacesCreated(IVLCVout vlcVout) {

    }

    @Override
    public void onSurfacesDestroyed(IVLCVout vlcVout) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
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

            case R.id.ly_play:
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                    img_play.setImageResource(R.drawable.exo_play);
                } else {
                    mMediaPlayer.play();
                    img_play.setImageResource(R.drawable.exo_pause);
                }
                break;
        }
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

    private void showAudioTracksList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SeriesPlayerActivity.this);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(SeriesPlayerActivity.this);
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
