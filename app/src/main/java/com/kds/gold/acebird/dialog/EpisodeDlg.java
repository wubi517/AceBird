package com.kds.gold.acebird.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kds.gold.acebird.R;

public class EpisodeDlg extends Dialog implements View.OnClickListener{
    private ImageButton btn_up,btn_down,btn_left,btn_right;
    private TextView txt_current,txt_all,txt_one;
    private LinearLayout ly_click;
    private int episode_num=1,all_episode;
    EpisodeDlgListener listener;
    Context context;
    private boolean all = true;
    public EpisodeDlg(@NonNull final Context context,int all_episode_num,final EpisodeDlgListener listener){
        super(context);
        this.context = context;
        this.listener = listener;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dlg_episode);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        all_episode = all_episode_num;
        btn_up = (ImageButton)findViewById(R.id.btn_up);
        btn_down = (ImageButton)findViewById(R.id.btn_down);
        btn_left = (ImageButton)findViewById(R.id.btn_left);
        btn_right = (ImageButton)findViewById(R.id.btn_right);
        btn_left .setOnClickListener(this);
        btn_right.setOnClickListener(this);
        btn_down.setOnClickListener(this);
        btn_up.setOnClickListener(this);

        txt_current = (TextView)findViewById(R.id.txt_current);
        txt_all = (TextView)findViewById(R.id.txt_total);
        txt_one = (TextView)findViewById(R.id.txt_one);
        txt_all.setText(String .valueOf(all_episode));

        ly_click = (LinearLayout)findViewById(R.id.ly_click);
        ly_click.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_left:
                if(episode_num==1){
                    episode_num = all_episode;
                }else {
                    episode_num--;
                }
                txt_current.setText(String .valueOf(episode_num));
                break;
            case R.id.btn_right:
                if(episode_num<all_episode){
                    episode_num++;
                }else {
                    episode_num=1;
                }
                txt_current.setText(String .valueOf(episode_num));
                break;
            case R.id.btn_up:
                if(txt_one.getText().toString().equalsIgnoreCase("one series")){
                    txt_one.setText("continuously");
                    all = true;
                }else {
                    txt_one.setText("one series");
                    all = false;
                }
                break;
            case R.id.btn_down:
                if(txt_one.getText().toString().equalsIgnoreCase("one series")){
                    txt_one.setText("continuously");
                    all = true;
                }else {
                    txt_one.setText("one series");
                    all = false;
                }
                break;
            case R.id.ly_click:
                listener.OnYesClick(EpisodeDlg.this,episode_num,all);
                break;
        }
    }

    public interface EpisodeDlgListener{
        public void OnYesClick(Dialog dialog,int episode_num,boolean all);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN){
            switch (event.getKeyCode()){
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if(episode_num==1){
                        episode_num = all_episode;
                    }else {
                        episode_num--;
                    }
                    txt_current.setText(String .valueOf(episode_num));
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if(episode_num<all_episode){
                        episode_num++;
                    }else {
                        episode_num=1;
                    }
                    txt_current.setText(String .valueOf(episode_num));
                    break;
                case KeyEvent.KEYCODE_DPAD_CENTER:
                    listener.OnYesClick(EpisodeDlg.this,episode_num,all);
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }
}

