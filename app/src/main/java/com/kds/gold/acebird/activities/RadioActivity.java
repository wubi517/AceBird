package com.kds.gold.acebird.activities;

import android.app.Dialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kds.gold.acebird.adapter.RadioListAdapter;
import com.kds.gold.acebird.apps.Constants;
import com.kds.gold.acebird.apps.MyApp;
import com.kds.gold.acebird.async.GetDataService;
import com.kds.gold.acebird.dialog.SearchDlg;
import com.kds.gold.acebird.listner.SimpleGestureFilter;
import com.kds.gold.acebird.listner.SimpleGestureFilter.SimpleGestureListener;

import com.kds.gold.acebird.R;
import com.kds.gold.acebird.models.ChannelModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RadioActivity extends AppCompatActivity implements SimpleGestureListener,GetDataService.OnGetResultsListener
    ,AdapterView.OnItemClickListener,View.OnClickListener{
    MediaPlayer mp;
    RelativeLayout main_lay;
    LinearLayout ly_buttons,ly_view,ly_sort,ly_fav,ly_search,ly_bottom,ly_color
            ,ly_sortDlg;
    ImageView radio_logo,btn_back;
    TextView txt_order,txt_time,txt_progress,txt_title,txt_dec;
    ListView radio_list;
    Button btn_num,btn_title,btn_fav;
    RadioListAdapter adapter;

    private SimpleGestureFilter detector;
    Context context = null;
    SimpleDateFormat time = new SimpleDateFormat("HH:mm a");
    List<String> favModelIds;
    List<ChannelModel> channels;
    Handler mHandler = new Handler();
    Runnable  mTicker;
    String sortby,url,contentUri;
    int sort,fav,page=1,max_items,total_items,total_page,sub_pos = 0;
    boolean is_up = false,is_dlg=false,is_fav = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio);

        main_lay = findViewById(R.id.main_lay);

        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);
        radio_logo = findViewById(R.id.radio_logo);

        txt_order = findViewById(R.id.txt_order);
        txt_time = findViewById(R.id.txt_time);
        txt_progress = findViewById(R.id.txt_progress);
        txt_title = findViewById(R.id.txt_title);
        txt_dec = findViewById(R.id.txt_dec);

        radio_list = findViewById(R.id.radio_list);
        radio_list.setOnItemClickListener(this);

        ly_buttons = findViewById(R.id.ly_buttons);
        ly_view = findViewById(R.id.ly_view);
        ly_sort = findViewById(R.id.ly_sort);
        ly_fav = findViewById(R.id.ly_fav);
        ly_search = findViewById(R.id.ly_search);
        ly_bottom = findViewById(R.id.ly_bottom);
        ly_color = findViewById(R.id.ly_color);
        ly_sortDlg = findViewById(R.id.ly_sortdlg);
        ly_sort.setOnClickListener(this);
        ly_fav.setOnClickListener(this);
        ly_search.setOnClickListener(this);

        btn_num = findViewById(R.id.btn_num);
        btn_title = findViewById(R.id.btn_title);
        btn_fav = findViewById(R.id.btn_fav);
        btn_num.setOnClickListener(this);
        btn_title.setOnClickListener(this);
        btn_fav.setOnClickListener(this);

        MyApp.is_welcome = false;
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        detector = new SimpleGestureFilter(RadioActivity.this, RadioActivity.this);
        context = this;
        favModelIds = MyApp.favRadioModelIds;
        if(MyApp.instance.getPreference().get(Constants.SORT)==null){
            sort = 0;
            fav = 0;
            sortby = "number";
        }else {
            sort = (int)MyApp.instance.getPreference().get(Constants.RADIO_SORT);
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

        Thread myThread = null;
        Runnable runnable = new CountDownRunner();
        myThread = new Thread(runnable);
        myThread.start();

        getRadio();
        FullScreencall();
    }

    private void getRadio(){
        txt_progress.setVisibility(View.VISIBLE);
        if(MyApp.is_search){
            MyApp.is_search = false;
        }else {
            url = Constants.GetUrl(this)+"portal.php?type=radio&action=get_ordered_list&all=0&p="+page+"&JsHttpRequest=1-xml&fav="+fav+"&sortby="+sortby;
        }
        GetDataService getDataService = new GetDataService(RadioActivity.this);
        getDataService.getData(RadioActivity.this,url,100);
        getDataService.onGetResultsData(RadioActivity.this);
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
                    JSONArray channels_array = channels_obj.getJSONArray("data");
                    if(channels_array.length()>0){
                        for(int i = 0;i<channels_array.length();i++){
                            JSONObject channel_obj = channels_array.getJSONObject(i);
                            try {
                                ChannelModel channelModel = new ChannelModel();
                                channelModel.setId(channel_obj.getString("id"));
                                channelModel.setNumber(String .valueOf(channel_obj.getInt("number")));
                                channelModel.setName(channel_obj.getString("name"));
                                channelModel.setCmd(channel_obj.getString("cmd"));
                                channelModel.setFav(channel_obj.getInt("fav"));
                                channels.add(channelModel);
                            }catch (Exception e){
                                Log.e("error","parse_error1");
                            }
                        }
                    }
                    adapter = new RadioListAdapter(this,channels);
                    radio_list.setAdapter(adapter);
                    radio_list.requestFocus();
                    adapter.selectItem(sub_pos);
                    radio_list.setSelection(sub_pos);
                    txt_order.setText(channels.get(sub_pos).getName()+"/BY " + sortby.toUpperCase());
                }catch (Exception e){
                    channels = new ArrayList<>();
                    adapter = new RadioListAdapter(this,channels);
                    radio_list.setAdapter(adapter);
                    Toast.makeText(this,"Network error",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(adapterView == radio_list && !is_dlg){
            sub_pos = i;
            txt_order.setText(channels.get(sub_pos).getName()+"/BY " + sortby.toUpperCase());
            adapter.selectItem(sub_pos);
            contentUri = channels.get(sub_pos).getCmd();
            if(contentUri.contains("ffmpeg")){
                contentUri = channels.get(sub_pos).getCmd().replace("ffmpeg ","");
            }else if(contentUri.contains("auto")){
                contentUri = channels.get(sub_pos).getCmd().replace("auto ","");
            }
            Log.e("url",contentUri);
            if (mp != null && mp.isPlaying()) {
                mp.stop();
                mp.release();
                mp = null;
            }
            Uri link = Uri.parse(contentUri);
            playRadio(link);
            txt_title.setText(channels.get(sub_pos).getName());
            ly_bottom.setVisibility(View.VISIBLE);
            listTimer();
        }
    }
    private void playRadio (Uri uri) {
        mp = new MediaPlayer();
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mp.setDataSource(this,  uri);
            mp.prepareAsync();
            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
        } catch (Exception e) {
            //     Log.e("Exception", e.getMessage());
            Toast.makeText(this, "Não pode jogar este rádio", Toast.LENGTH_SHORT).show();
        }

    }

    int maxTime;
    private void listTimer() {
        maxTime = 4;
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
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ly_sort:
                if(ly_sortDlg.getVisibility()==View.INVISIBLE){
                    is_dlg = true;
                    if(is_up){
                        is_up = false;
                    }
                    ly_sortDlg.setVisibility(View.VISIBLE);
                    ly_sortDlg.requestFocus();
                    radio_list.setFocusable(false);
                    if(sort==0){
                        btn_num.requestFocus();
                    }else if(sort==1){
                        btn_title.requestFocus();
                    }else {
                        btn_fav.requestFocus();
                    }
                }else {
                    radio_list.setFocusable(true);
                    radio_list.requestFocus();
                    ly_sortDlg.setFocusable(false);
                    is_dlg = false;
                    ly_sortDlg.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.btn_num:
                sort = 0;
                MyApp.instance.getPreference().put(Constants.RADIO_SORT,sort);
                sortby = "number";
                page = 1;
                MyApp.instance.is_first = true;
                fav = 0;
                getRadio();
                break;
            case R.id.btn_title:
                if(txt_progress.getVisibility()==View.GONE){
                    sort = 1;
                    MyApp.instance.getPreference().put(Constants.RADIO_SORT,sort);
                    sortby = "name";
                    page = 1;
                    fav = 0;
                    getRadio();
                }
                break;
            case R.id.btn_fav:
                if(txt_progress.getVisibility()==View.GONE){
                    sort = 2;
                    MyApp.instance.getPreference().put(Constants.RADIO_SORT,sort);
                    sortby = "fav";
                    page = 1;
                    MyApp.instance.is_first = true;
                    fav = 1;
                    getRadio();
                }
                break;
            case R.id.ly_fav:
                if(txt_progress.getVisibility()==View.GONE && channels.size()>0){
                    is_fav = true;
                    String mStream_id = channels.get(sub_pos).getId();
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
                        if(favModelIds.size()>0){
                            res = buffer.toString().substring(0,buffer.toString().length()-1);
                        }
                    }
                    if(favModelIds.size()==0){
                        res = mStream_id;
                        favModelIds = new ArrayList<>();
                        favModelIds.add(res);
                    }
//                Log.e("res",res);
                    MyApp.favRadioModelIds = favModelIds;
                    if(channels.get(sub_pos).getFav()==0){
                        channels.get(sub_pos).setFav(1);
                    }else {
                        channels.get(sub_pos).setFav(0);
                    }
                    radio_list.requestFocus();
                    adapter = new RadioListAdapter(this,channels);
                    radio_list.setAdapter(adapter);
                    adapter.selectItem(sub_pos);
//                    url = Constants.GetUrl(this)+"portal.php?type=radio&action=set_fav&fav_radio="+res+"&JsHttpRequest=1-xml";
                    GetDataService getDataService = new GetDataService(RadioActivity.this);
                    getDataService.getData(RadioActivity.this,Constants.GetUrl(this)+"portal.php?type=radio&action=set_fav&fav_radio="+res+"&JsHttpRequest=1-xml",600);
                    getDataService.onGetResultsData(RadioActivity.this);
                }
                break;
            case R.id.btn_back:
                if(ly_sortDlg.getVisibility()==View.VISIBLE){
                    radio_list.setFocusable(true);
                    radio_list.requestFocus();
                    ly_sortDlg.setFocusable(false);
                    is_dlg = false;
                    ly_sortDlg.setVisibility(View.INVISIBLE);
                    return;
                }
                MyApp.is_search = false;
                if (mp != null && mp.isPlaying()) {
                    mp.stop();
                    mp.release();
                    mp = null;
                }
//                    releaseMediaPlayer();
                finish();
                break;
            case R.id.ly_search:
                SearchDlg searchDlg = new SearchDlg(RadioActivity.this, new SearchDlg.DlgPinListener() {
                    @Override
                    public void OnYesClick(Dialog dialog, String pin_code) {
                        dialog.dismiss();
                        MyApp.is_search = true;
                        url = Constants.GetUrl(getApplicationContext())+"portal.php?type=radio&action=get_ordered_list&all=0&p=0&JsHttpRequest=1-xml&fav=0&sortby=number&search="+pin_code;
                        getRadio();
                    }
                    @Override
                    public void OnCancelClick(Dialog dialog, String pin_code) {
                        dialog.dismiss();
                    }
                });
                searchDlg.show();
                break;
        }
    }

    class CountDownRunner implements Runnable {
//         @Override
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
    @Override
    public void onSwipe(int direction) {
        switch (direction){
            case SimpleGestureFilter.SWIPE_DOWN:
                if(txt_progress.getVisibility()==View.GONE && channels.size()>0){
                    if(radio_list.getFirstVisiblePosition()==0){
                        if(page>1){
                            page--;
                            sub_pos = max_items-1;
                            getRadio();
                            return;
                        }
                        if(page==1){
                            page = total_page;
                            sub_pos = total_items%max_items-1;
                            getRadio();
                        }
                    }
                }
                break;
            case SimpleGestureFilter.SWIPE_UP:
                if(txt_progress.getVisibility()==View.GONE && channels.size()>0){
                    if(page<total_page && radio_list.getLastVisiblePosition()==max_items-1){
                        page++;
                        sub_pos=0;
                        getRadio();
                        return;
                    }
                    if(page==total_page && radio_list.getLastVisiblePosition() == (total_items%max_items-1)){
                        page=1;
                        sub_pos=0;
                        getRadio();
                    }
                }
                break;
        }
    }

    @Override
    public void onDoubleTap() {

    }

    @Override
    protected void onUserLeaveHint()
    {
        if (mp != null && mp.isPlaying()) {
            mp.stop();
            mp.release();
            mp = null;
        }
        super.onUserLeaveHint();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent me) {
        this.detector.onTouchEvent(me);
        return super.dispatchTouchEvent(me);
    }

    private void Is_up(){
        View view = getCurrentFocus();
        if(txt_progress.getVisibility()==View.GONE){
            if(is_up && !is_dlg && view == radio_list){
                if(sub_pos>0){
                    sub_pos--;
                    adapter.selectItem(sub_pos);
                    contentUri = channels.get(sub_pos).getCmd();
                    if(contentUri.contains("ffmpeg")){
                        contentUri = channels.get(sub_pos).getCmd().replace("ffmpeg ","");
                    }else if(contentUri.contains("auto")){
                        contentUri = channels.get(sub_pos).getCmd().replace("auto ","");
                    }

                    return;
                }
                if(sub_pos==0){
                    if(page>1){
                        page--;
                        sub_pos = max_items-1;
                        getRadio();
                        return;
                    }
                    if(page==1){
                        page = total_page;
                        sub_pos = total_items%max_items-1;
                        getRadio();
                    }
                }
            }
        }
    }

    private void Is_down(){
        View view = getCurrentFocus();
        if(txt_progress.getVisibility()==View.GONE){
            if(!is_dlg && view == radio_list){
                if(page<total_page){
                    if(sub_pos<max_items-1){
                        sub_pos++;
                        adapter.selectItem(sub_pos);
                        contentUri = channels.get(sub_pos).getCmd();
                        if(contentUri.contains("ffmpeg")){
                            contentUri = channels.get(sub_pos).getCmd().replace("ffmpeg ","");
                        }else if(contentUri.contains("auto")){
                            contentUri = channels.get(sub_pos).getCmd().replace("auto ","");
                        }
                        return;
                    }
                    if(sub_pos==max_items-1){
                        page++;
                        sub_pos=0;
                        getRadio();
                    }
                    return;
                }
                if(page==total_page){
                    if(sub_pos<(total_items%max_items-1)){
                        sub_pos++;
                        adapter.selectItem(sub_pos);
                        contentUri = channels.get(sub_pos).getCmd();
                        if(contentUri.contains("ffmpeg")){
                            contentUri = channels.get(sub_pos).getCmd().replace("ffmpeg ","");
                        }else if(contentUri.contains("auto")){
                            contentUri = channels.get(sub_pos).getCmd().replace("auto ","");
                        }
                        return;
                    }
                    if(sub_pos==(total_items%max_items-1)){
                        page = 1;
                        sub_pos=0;
                        getRadio();
                    }
                }
            }
        }
    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN){
            switch (event.getKeyCode()){
                case KeyEvent.KEYCODE_BACK:
                    MyApp.is_search = false;
                    if(ly_sortDlg.getVisibility()==View.VISIBLE){
                        radio_list.setFocusable(true);
                        radio_list.requestFocus();
                        ly_sortDlg.setFocusable(false);
                        is_dlg = false;
                        ly_sortDlg.setVisibility(View.INVISIBLE);
                        return true;
                    }
                    if (mp != null && mp.isPlaying()) {
                        mp.stop();
                        mp.release();
                        mp = null;
                    }
                    finish();
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    is_up = false;
                    Is_down();
                    break;
                case KeyEvent.KEYCODE_DPAD_UP:
                    is_up = true;
                    Is_up();
                    break;
                case KeyEvent.KEYCODE_DPAD_CENTER:
                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if (mp != null && mp.isPlaying()) {
                        mp.stop();
                        mp.release();
                        mp = null;
                    }
                    MyApp.is_search = false;
                    finish();
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
