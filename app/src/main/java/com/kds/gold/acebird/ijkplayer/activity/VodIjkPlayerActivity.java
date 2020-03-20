package com.kds.gold.acebird.ijkplayer.activity;


import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TextView;

import com.kds.gold.acebird.R;
import com.kds.gold.acebird.dialog.PackageDlg;
import com.kds.gold.acebird.ijkplayer.fragment.TracksFragment;
import com.kds.gold.acebird.ijkplayer.widget.media.AndroidMediaController;
import com.kds.gold.acebird.ijkplayer.widget.media.IjkVideoView;
import com.kds.gold.acebird.ijkplayer.widget.media.MeasureHelper;
import com.kds.gold.acebird.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.misc.ITrackInfo;


public class VodIjkPlayerActivity extends AppCompatActivity implements  TracksFragment.ITrackHolder,SeekBar.OnSeekBarChangeListener,View.OnClickListener{
    private DrawerLayout mDrawerLayout;
    private ViewGroup mRightDrawer;
    private AndroidMediaController mMediaController;
    private IjkVideoView mVideoView;
    private TextView mToastTextView;
    private TableLayout mHudView;

    LinearLayout bottom_lay, def_lay,ly_play,ly_resolution,ly_audio;
    ImageView img_play;

    TextView title_txt, start_txt, end_txt;
    ImageView imageView;
    SeekBar seekBar;
    Handler mHandler = new Handler();
    Handler handler = new Handler();

    Runnable mTicker;
    String cont_url;
    List<String>  pkg_datas;

    int dration_time = 0;

    boolean is_create=true;
    String title,season,total_title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vod_ijkplayer);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        pkg_datas = new ArrayList<>();
        for (int i = 0; i < getResources().getStringArray(R.array.package_list3).length; i++) {
            pkg_datas.add(getResources().getStringArray(R.array.package_list3)[i]);
        }
        RelativeLayout main_lay =  findViewById(R.id.main_lay);
        cont_url = getIntent().getStringExtra("content_Uri");

        mMediaController = new AndroidMediaController(this, false);

        mToastTextView = (TextView) findViewById(R.id.toast_text_view);
        mHudView = (TableLayout) findViewById(R.id.hud_view);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mRightDrawer = (ViewGroup) findViewById(R.id.right_drawer);

        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");

        mVideoView = (IjkVideoView) findViewById(R.id.video_view);
        mVideoView.setMediaController(mMediaController);
        mMediaController.hide();
        mVideoView.setHudView(mHudView);

        def_lay = (LinearLayout) findViewById(R.id.def_lay);
        bottom_lay = (LinearLayout) findViewById(R.id.vod_bottom_lay);
        title_txt = (TextView) findViewById(R.id.vod_channel_title);
        imageView = (ImageView) findViewById(R.id.vod_channel_img);
        start_txt = (TextView) findViewById(R.id.vod_start_time);
        end_txt = (TextView) findViewById(R.id.vod_end_time);
        seekBar = (SeekBar) findViewById(R.id.vod_seekbar);
        seekBar.setOnSeekBarChangeListener(this);


        ly_audio = findViewById(R.id.ly_audio);

        ly_play = findViewById(R.id.ly_play);
        ly_resolution = findViewById(R.id.ly_resolution);

        ly_play.setOnClickListener(this);
        ly_resolution.setOnClickListener(this);
        ly_audio.setOnClickListener(this);

        img_play = findViewById(R.id.img_play);
        title_txt.setText(getIntent().getStringExtra("title"));
        title = getIntent().getStringExtra("title");
        season = getIntent().getStringExtra("season");
        if(season==null){
            total_title = title;
        }else {
            total_title = season+title;
        }
        try {
            Picasso.with(this).load(getIntent().getStringExtra("image_Url")).into(imageView);
        }catch (Exception e){
            Picasso.with(this).load(R.drawable.icon_default).into(imageView);
        }

        playVideo();
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (!is_create) {
            releaseMediaPlayer();
            playVideo();
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
        editor.commit();
        releaseMediaPlayer();
    }

    private void releaseMediaPlayer() {
        if(mVideoView==null)
            return;
        mVideoView.release(true);
    }

    private void playVideo(){
        toggleFullscreen(true);
//        Log.e("url",cont_url);
        mVideoView = (IjkVideoView) findViewById(R.id.video_view);
        mVideoView.setMediaController(mMediaController);
        mVideoView.setHudView(mHudView);
        mMediaController.hide();
        mVideoView.setVideoPath(cont_url);
        mVideoView.start();

        updateProgressBar();
        updateTimer();

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


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        try {
            long curr_pos = mVideoView.getCurrentPosition();
            long max_pos = mVideoView.getDuration();
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                switch (event.getKeyCode()) {
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                        if (mVideoView.isPlaying()) {
                            mVideoView.pause();
                            img_play.setImageResource(R.drawable.exo_play);
                        } else {
                            mVideoView.start();
                            img_play.setImageResource(R.drawable.exo_pause);
                        }
                        if (bottom_lay.getVisibility() == View.GONE) bottom_lay.setVisibility(View.VISIBLE);
                        updateTimer();
                        break;
                    case KeyEvent.KEYCODE_DPAD_LEFT:
                        dration_time += 30;
                        if (curr_pos < dration_time * 1000)
                            mVideoView.seekTo(1);
                        else {
                            int st = (int) (curr_pos - (long) dration_time * 1000);
                            mVideoView.seekTo(st);
                        }
                        dration_time = 0;
                        updateProgressBar();
                        updateTimer();
                        if (bottom_lay.getVisibility() == View.GONE) bottom_lay.setVisibility(View.VISIBLE);
                        break;
                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                        dration_time += 30;
                        if (max_pos < dration_time * 1000)
                            mVideoView.seekTo((int) (max_pos - 10));
                        else mVideoView.seekTo((int) (curr_pos + (long) dration_time * 1000));
                        dration_time = 0;
                        updateProgressBar();
                        updateTimer();
                        if (bottom_lay.getVisibility() == View.GONE) bottom_lay.setVisibility(View.VISIBLE);
                        break;
                    case KeyEvent.KEYCODE_BACK:
                        if(bottom_lay.getVisibility()==View.VISIBLE){
                            bottom_lay.setVisibility(View.GONE);
                            return true;
                        }
                        if (mDrawerLayout.isDrawerOpen(mRightDrawer)) {
                            Fragment f = getSupportFragmentManager().findFragmentById(R.id.right_drawer);
                            if (f != null) {
                                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                transaction.remove(f);
                                transaction.commit();
                            }
                            mDrawerLayout.closeDrawer(mRightDrawer);
                            return true;
                        }
                        releaseMediaPlayer();
                        finish();
                        break;
                    case KeyEvent.KEYCODE_MENU:
                        PackageDlg packageDlg = new PackageDlg(VodIjkPlayerActivity.this, pkg_datas, new PackageDlg.DialogPackageListener() {
                            @Override
                            public void OnItemClick(Dialog dialog, int position) {
                                dialog.dismiss();
                                switch (position) {
                                    case 0:
                                        showAudioTracksList();
                                        break;
                                    case 1:
                                        int aspectRatio = mVideoView.toggleAspectRatio();
                                        String aspectRatioText = MeasureHelper.getAspectRatioText(VodIjkPlayerActivity.this, aspectRatio);
                                        mToastTextView.setText(aspectRatioText);
                                        break;
                                }
                            }
                        });
                        packageDlg.show();
                        break;
                }
            }
        }catch (Exception e){

        }
        return super.dispatchKeyEvent(event);
    }

    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            if (mVideoView != null) {
                long totalDuration = mVideoView.getDuration();
                long currentDuration = mVideoView.getCurrentPosition();
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
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        long totalDuration = mVideoView.getDuration();
        int currentPosition = Utils.progressToTimer(seekBar.getProgress(), totalDuration);
        mVideoView.seekTo(currentPosition);
        updateProgressBar();
        updateTimer();
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
                    if(mToastTextView.getVisibility()==View.VISIBLE)
                        mToastTextView.setVisibility(View.GONE);
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ly_audio:
                showAudioTracksList();
                break;
            case R.id.ly_resolution:
                int aspectRatio = mVideoView.toggleAspectRatio();
                String aspectRatioText = MeasureHelper.getAspectRatioText(this, aspectRatio);
                mToastTextView.setText(aspectRatioText);
                mToastTextView.setVisibility(View.VISIBLE);
                updateTimer();
                break;

            case R.id.ly_play:
                if (mVideoView.isPlaying()) {
                    mVideoView.pause();
                    img_play.setImageResource(R.drawable.exo_play);
                } else {
                    mVideoView.start();
                    img_play.setImageResource(R.drawable.exo_pause);
                }
                break;
        }
    }

    private void showAudioTracksList() {
        if (mDrawerLayout.isDrawerOpen(mRightDrawer)) {
            Fragment f = getSupportFragmentManager().findFragmentById(R.id.right_drawer);
            if (f != null) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.remove(f);
                transaction.commit();
            }
            mDrawerLayout.closeDrawer(mRightDrawer);
        } else {
            Fragment f = TracksFragment.newInstance();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.right_drawer, f);
            transaction.commit();
            mDrawerLayout.openDrawer(mRightDrawer);
        }
    }

    @Override
    public ITrackInfo[] getTrackInfo() {
        if (mVideoView == null)
            return null;

        return mVideoView.getTrackInfo();
    }

    @Override
    public int getSelectedTrack(int trackType) {
        if (mVideoView == null)
            return -1;

        return mVideoView.getSelectedTrack(trackType);
    }

    @Override
    public void selectTrack(int stream) {
        mVideoView.selectTrack(stream);
    }

    @Override
    public void deselectTrack(int stream) {
        mVideoView.deselectTrack(stream);
    }
}
