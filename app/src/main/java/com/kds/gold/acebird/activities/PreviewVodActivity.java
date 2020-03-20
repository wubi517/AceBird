package com.kds.gold.acebird.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
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

import com.aigestudio.wheelpicker.WheelPicker;

import com.kds.gold.acebird.R;
import com.kds.gold.acebird.adapter.VodListAdapter;
import com.kds.gold.acebird.apps.Constants;
import com.kds.gold.acebird.apps.MyApp;
import com.kds.gold.acebird.async.GetDataService;
import com.kds.gold.acebird.dialog.PackageDlg;
import com.kds.gold.acebird.dialog.SearchDlg;
import com.kds.gold.acebird.models.ABCModel;
import com.kds.gold.acebird.models.MovieModel;
import com.kds.gold.acebird.models.TvGenreModel;
import com.kds.gold.acebird.listner.SimpleGestureFilter;
import com.kds.gold.acebird.listner.SimpleGestureFilter.SimpleGestureListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PreviewVodActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemClickListener,
        GetDataService.OnGetResultsListener,WheelPicker.OnItemSelectedListener,SimpleGestureListener{
    private SimpleGestureFilter detector;
    Context context = null;

    List<MovieModel> movieModelList;
    List<TvGenreModel> vodGenreModels;
    List<String >abcModelList;
    List<ABCModel>genreModelList;
    List<String >yearModelList;
    List<String >pkg_datas;
    WheelPicker wheelLeft,wheelCenter,wheelRight;

    VodListAdapter vodListAdapter;
    RelativeLayout main_lay;
    LinearLayout ly_left,ly_right,ly_buttons,ly_view,ly_sort,ly_fav,ly_find,ly_viewDlg,ly_sortDlg,
            ly_searchDlg,pickingDlg;
    Button btn_list_info,btn_list,btn_by_addtime,btn_byrating,btn_bytitle,btn_rating
          ,btn_byhd,btn_byfav,btn_byended,btn_search,btn_picking;
    ImageView btn_back,image_movie,btn_about_movie;
    TextView txt_category,txt_time,txt_progress,txt_rating,txt_age,txt_year,txt_length,txt_director,txt_genre;
    ListView vod_list;

    String Url,genre,sortby,category_id,video_id,abc,genre_search,year,vod_title,vod_image,vod_id,vod_url,vod_description;
    int movie_id,season_id,episode_id,page,fav,not_ended,hd,total_items,max_page_items,selected_num=0,
            category_pos,total_page,sort,row,abc_pos=0,year_pos=0,genre_pos=0;
    boolean is_up = false,is_first = true,is_dlg = false;
    boolean is_vod = true;
    Handler mEpgHandler = new Handler();
    Runnable EpgTicker;

    SimpleDateFormat time = new SimpleDateFormat("HH:mm a");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_video);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        detector = new SimpleGestureFilter(PreviewVodActivity.this, PreviewVodActivity.this);
        context = this;

        pkg_datas = new ArrayList<>();
        for (int i = 0; i < getResources().getStringArray(R.array.package_list1).length; i++) {
            pkg_datas.add(getResources().getStringArray(R.array.package_list1)[i]);
        }

        txt_category = (TextView)findViewById(R.id.txt_category);

        category_pos = (int) MyApp.instance.getPreference().get(Constants.VOD_POS);
        vodGenreModels = MyApp.vodGenreModelList;
        category_id = vodGenreModels.get(category_pos).getId();
        genre = vodGenreModels.get(category_pos).getAlias();
        page = 0;
        movie_id = 0;
        season_id = 0;
        episode_id = 0;
        row=0;
        if(MyApp.instance.getPreference().get(Constants.VOD_SORT)==null){
            sort = 0;
        }else {
            sort = (int) MyApp.instance.getPreference().get(Constants.VOD_SORT);
        }
        switch (sort){
            case 0:
                sortby = "added";
                hd = 0;
                not_ended = 0;
                fav = 0;
                txt_category.setText(vodGenreModels.get(category_pos).getTitle().toUpperCase()+" /BY ADDED");
                break;
            case 1:
                sortby = "rating";
                fav = 0;hd=0;not_ended=0;
                txt_category.setText(vodGenreModels.get(category_pos).getTitle().toUpperCase()+" /BY RATING");
                break;
            case 2:
                sortby = "name";
                fav=0;hd=0;not_ended=0;
                txt_category.setText(vodGenreModels.get(category_pos).getTitle().toUpperCase()+" /BY TITLE");
                break;
            case 3:
                sortby = "top";
                fav=0;
                hd=0;
                not_ended = 0;
                txt_category.setText(vodGenreModels.get(category_pos).getTitle().toUpperCase()+" /BY RATING");
                break;
            case 4:
                sortby = "added";
                hd=1;
                fav=0;
                not_ended=0;
                txt_category.setText(vodGenreModels.get(category_pos).getTitle().toUpperCase()+" /BY HD ONLY");
                break;
            case 5:
                sortby = "name";
                fav=1;
                hd=0;
                not_ended = 0;
                txt_category.setText(vodGenreModels.get(category_pos).getTitle().toUpperCase()+" /BY FAVORITE ONLY");
                break;
            case 6:
                sortby = "last_ended";
                hd = 0;
                not_ended = 1;
                fav=0;
                txt_category.setText(vodGenreModels.get(category_pos).getTitle().toUpperCase()+" /BY NOT ENDED");
                break;
        }
        abc = "*";
        genre_search = "*";
        year = "*";

        main_lay = findViewById(R.id.main_lay);
        ly_left = findViewById(R.id.ly_left);
        ly_right = findViewById(R.id.ly_right);
        ly_buttons = findViewById(R.id.ly_buttons);
        ly_view = findViewById(R.id.ly_view);
        ly_view.setOnClickListener(this);
        ly_sort = findViewById(R.id.ly_sort);
        ly_sort.setOnClickListener(this);
        ly_fav = findViewById(R.id.ly_fav);
        ly_fav.setOnClickListener(this);
        ly_find = findViewById(R.id.ly_find);
        ly_find.setOnClickListener(this);
        ly_viewDlg = findViewById(R.id.ly_viewdlg);
        ly_sortDlg = findViewById(R.id.ly_sortdlg);
        ly_searchDlg = findViewById(R.id.ly_searchdlg);
        pickingDlg = findViewById(R.id.pickingDlg);

        btn_list_info = findViewById(R.id.btn_list_info);
        btn_list_info.setOnClickListener(this);
        btn_list = findViewById(R.id.btn_list);
        btn_list.setOnClickListener(this);
        btn_by_addtime = findViewById(R.id.btn_addtime);
        btn_by_addtime.setOnClickListener(this);
        btn_byrating = findViewById(R.id.btn_by_rating);
        btn_byrating.setOnClickListener(this);
        btn_bytitle = findViewById(R.id.btn_by_title);
        btn_bytitle.setOnClickListener(this);
        btn_rating = findViewById(R.id.btn_rating);
        btn_rating.setOnClickListener(this);
        btn_byhd = findViewById(R.id.btn_by_hd);
        btn_byhd.setOnClickListener(this);
        btn_byfav = findViewById(R.id.btn_by_fav);
        btn_byfav.setOnClickListener(this);
        btn_byended = findViewById(R.id.btn_by_ended);
        btn_byended.setOnClickListener(this);
        btn_search = findViewById(R.id.btn_search);
        btn_search.setOnClickListener(this);
        btn_picking = findViewById(R.id.btn_pick);
        btn_picking.setOnClickListener(this);

        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);
        btn_about_movie = findViewById(R.id.btn_about_movie);
        btn_about_movie.setOnClickListener(this);

        image_movie = findViewById(R.id.image_movie);

        txt_time = findViewById(R.id.txt_time);
        txt_progress = findViewById(R.id.txt_progress);
        txt_rating = findViewById(R.id.txt_rating);
        txt_age = findViewById(R.id.txt_age);
        txt_year = findViewById(R.id.txt_year);
        txt_length = findViewById(R.id.txt_length);
        txt_director = findViewById(R.id.txt_director);
        txt_genre = findViewById(R.id.txt_genre);

        vod_list = findViewById(R.id.vod_list);
        vod_list.setOnItemClickListener(this);
        vod_list.requestFocus();

        wheelLeft = findViewById(R.id.wheel_left);
        wheelLeft.setOnItemSelectedListener(this);
        wheelRight = findViewById(R.id.wheel_right);
        wheelRight.setOnItemSelectedListener(this);
        wheelCenter = findViewById(R.id.wheel_center);
        wheelCenter.setOnItemSelectedListener(this);

        Thread myThread = null;
        Runnable runnable = new CountDownRunner();
        myThread = new Thread(runnable);
        myThread.start();
//        if(!getApplicationContext().getPackageName().equalsIgnoreCase(new String(Base64.decode(new String (Base64.decode("cy5nb2xkLY3k1bmIyeGtMWTI5dExtdGtjeTVuYjJ4a0xuUnlaWGc9".substring(9),Base64.DEFAULT)).substring(9),Base64.DEFAULT)))){
//            return;
//        }
        getMovies();
        FullScreencall();
    }

    int epg_time;
    int i = 0;
    private void EpgTimer(){
        epg_time = 1;
        EpgTicker = new Runnable() {
            public void run() {
                if (epg_time < 1) {
                    i++;
                    Log.e("count",String .valueOf(i));
//                    getEpg();
                    return;
                }
                runNextEpgTicker();
            }
        };
        EpgTicker.run();
    }
    private void runNextEpgTicker() {
        epg_time--;
        long next = SystemClock.uptimeMillis() + 1000;
        mEpgHandler.postAtTime(EpgTicker, next);
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(!is_vod){
            is_vod = true;
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_about_movie:
                Intent intent = new Intent(PreviewVodActivity.this,VodInFoActivity.class);
                intent.putExtra("title",movieModelList.get(selected_num).getName());
                intent.putExtra("year",movieModelList.get(selected_num).getYear());
                intent.putExtra("genre",movieModelList.get(selected_num).getGenres_str());
                intent.putExtra("length",movieModelList.get(selected_num).getTime());
                intent.putExtra("director",movieModelList.get(selected_num).getDirector());
                intent.putExtra("cast",movieModelList.get(selected_num).getActors());
                intent.putExtra("age",movieModelList.get(selected_num).getAge());
                intent.putExtra("dec",movieModelList.get(selected_num).getDescription());
                intent.putExtra("img",movieModelList.get(selected_num).getScreenshot_uri());
                startActivity(intent);
                break;
            case R.id.ly_view:
                if(ly_viewDlg.getVisibility()==View.INVISIBLE){
                    is_dlg = true;
                    ly_sortDlg.setVisibility(View.INVISIBLE);
                    ly_searchDlg.setVisibility(View.INVISIBLE);
                    ly_viewDlg.setVisibility(View.VISIBLE);
                    pickingDlg.setVisibility(View.GONE);
                    vod_list.setFocusable(false);
                    ly_viewDlg.requestFocus();
                    ly_viewDlg.setFocusable(true);
                    if(ly_right.getVisibility()==View.VISIBLE){
                        btn_list_info.requestFocus();
                    }else {
                        btn_list.requestFocus();
                    }
                }else {
                    vod_list.requestFocus();
                    vod_list.setFocusable(true);
                    ly_viewDlg.setFocusable(false);
                    is_dlg = false;
                    ly_viewDlg.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.ly_sort:
                if(ly_sortDlg.getVisibility()==View.INVISIBLE){
                    is_dlg = true;
                    if(is_up){
                        is_up = false;
                    }
                    pickingDlg.setVisibility(View.GONE);
                    ly_viewDlg.setVisibility(View.INVISIBLE);
                    ly_searchDlg.setVisibility(View.INVISIBLE);
                    ly_sortDlg.setVisibility(View.VISIBLE);
                    ly_sortDlg.requestFocus();
                    ly_sortDlg.setFocusable(true);
                    vod_list.setFocusable(false);
                    if(sort==0){
                        btn_by_addtime.requestFocus();
                    }else if(sort==1){
                        btn_byrating.requestFocus();
                    }else if(sort==2){
                        btn_bytitle.requestFocus();
                    }else if(sort==3){
                        btn_rating.requestFocus();
                    }else if(sort==4){
                        btn_byhd.requestFocus();
                    }else if(sort==5){
                        btn_byfav.requestFocus();
                    }else {
                        btn_byended.requestFocus();
                    }
                }else {
                    vod_list.setFocusable(true);
                    vod_list.requestFocus();
                    ly_sortDlg.setFocusable(false);
                    is_dlg = false;
                    ly_sortDlg.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.ly_fav:
                pickingDlg.setVisibility(View.GONE);
                ly_sortDlg.setVisibility(View.INVISIBLE);
                ly_searchDlg.setVisibility(View.INVISIBLE);
                ly_viewDlg.setVisibility(View.INVISIBLE);
                video_id = movieModelList.get(selected_num).getId();
                if(movieModelList.get(selected_num).getFav()==0){
                    Url = Constants.GetUrl(this)+"portal.php?type=vod&action=set_fav&video_id="+video_id+"&JsHttpRequest=1-xml";
                    movieModelList.get(selected_num).setFav(1);
                }else {
                    Url = Constants.GetUrl(this)+"portal.php?type=vod&action=del_fav&video_id="+video_id+"&JsHttpRequest=1-xml";
                    movieModelList.get(selected_num).setFav(0);
                }
                vodListAdapter = new VodListAdapter(PreviewVodActivity.this,movieModelList);
                vod_list.setAdapter(vodListAdapter);
                vod_list.post(new Runnable() {
                    @Override
                    public void run() {
                        vod_list.setSelection(selected_num);
                        vod_list.smoothScrollToPosition(selected_num);
                    }
                });
                vod_list.setFocusable(true);
                vod_list.requestFocus();
                GetDataService dataService = new GetDataService(PreviewVodActivity.this);
                dataService.getData(PreviewVodActivity.this,Url,400);
                dataService.onGetResultsData(PreviewVodActivity.this);
                break;
            case R.id.ly_find:
                if(ly_searchDlg.getVisibility()==View.INVISIBLE){
                    is_dlg = true;
                    ly_sortDlg.setVisibility(View.INVISIBLE);
                    ly_viewDlg.setVisibility(View.INVISIBLE);
                    ly_searchDlg.setVisibility(View.VISIBLE);
                    ly_searchDlg.requestFocus();
                    ly_searchDlg.setFocusable(true);
                    vod_list.setFocusable(false);
                    if(MyApp.instance.getPreference().get(Constants.VOD_SEARCH)==null){
                        btn_picking.requestFocus();
                    }else {
                        btn_search.requestFocus();
                    }
                }else {
                    if(pickingDlg.getVisibility()==View.VISIBLE){
                        pickingDlg.setVisibility(View.GONE);
                    }else {
                        vod_list.setFocusable(true);
                        vod_list.requestFocus();
                        ly_searchDlg.setFocusable(false);
                        is_dlg = false;
                        ly_searchDlg.setVisibility(View.INVISIBLE);
                    }
                }
                break;
            case R.id.btn_list:
                ly_right.setVisibility(View.GONE);
                break;
            case R.id.btn_list_info:
                ly_right.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_addtime:
                sort = 0;
                MyApp.instance.getPreference().put(Constants.VOD_SORT,sort);
                sortby = "added";
                txt_category.setText(vodGenreModels.get(category_pos).getTitle().toUpperCase()+" /BY "+sortby.toUpperCase());
                hd = 0;
                fav = 0;
                not_ended = 0;
                getMovies();
                vod_list.requestFocus();
                break;
            case R.id.btn_by_rating:
                sort = 1;
                MyApp.instance.getPreference().put(Constants.VOD_SORT,sort);
                sortby = "rating";
                txt_category.setText(vodGenreModels.get(category_pos).getTitle().toUpperCase()+" /BY "+sortby.toUpperCase());
                hd=0;
                fav = 0;
                not_ended = 0;
                row = 0;
                getMovies();
                vod_list.requestFocus();
                break;
            case R.id.btn_by_title:
                sort = 2;
                MyApp.instance.getPreference().put(Constants.VOD_SORT,sort);
                sortby = "name";
                txt_category.setText(vodGenreModels.get(category_pos).getTitle().toUpperCase()+" /BY TITLE");
                hd = 0;
                fav = 0;
                not_ended = 0;
                row = 0;
                getMovies();
                vod_list.requestFocus();
                break;
            case R.id.btn_rating:
                sort = 3;
                MyApp.instance.getPreference().put(Constants.VOD_SORT,sort);
                sortby = "top";
                txt_category.setText(vodGenreModels.get(category_pos).getTitle().toUpperCase()+" /BY RATING");
                hd = 0;
                fav = 0;
                not_ended = 0;
                row = 0;
                getMovies();
                vod_list.requestFocus();
                break;
            case R.id.btn_by_hd:
                sort = 4;
                MyApp.instance.getPreference().put(Constants.VOD_SORT,sort);
                sortby = "added";
                txt_category.setText(vodGenreModels.get(category_pos).getTitle().toUpperCase()+" /BY HD ONLY");
                hd=1;
                fav = 0;
                not_ended = 0;
                row = 0;
                getMovies();
                vod_list.requestFocus();
                break;
            case R.id.btn_by_fav:
                sort = 5;
                MyApp.instance.getPreference().put(Constants.VOD_SORT,sort);
                sortby = "name";
                txt_category.setText(vodGenreModels.get(category_pos).getTitle().toUpperCase()+" /BY FAVORITE ONLY");
                fav = 1;
                hd = 0;
                not_ended = 0;
                row = 0;
                getMovies();
                vod_list.requestFocus();
                break;
            case R.id.btn_by_ended:
                sort = 6;
                MyApp.instance.getPreference().put(Constants.VOD_SORT,sort);
                sortby = "last_ended";
                txt_category.setText(vodGenreModels.get(category_pos).getTitle().toUpperCase()+" /BY NOT ENDED");
                fav=0;
                row=0;
                hd = 0;
                not_ended = 1;
                getMovies();
                vod_list.requestFocus();
                break;
            case R.id.btn_search:
                MyApp.instance.getPreference().put(Constants.VOD_SEARCH,"vod_search");
                ly_searchDlg.setVisibility(View.INVISIBLE);
                ly_searchDlg.setFocusable(false);
                SearchDlg searchDlg = new SearchDlg(PreviewVodActivity.this, new SearchDlg.DlgPinListener() {
                    @Override
                    public void OnYesClick(Dialog dialog, String pin_code) {
                        if(!MyApp.is_after){
                            dialog.dismiss();
                        }
                        MyApp.is_search = true;
                        Url = Constants.GetUrl(getApplicationContext())+"portal.php?type=vod&action=get_ordered_list&movie_id="+movie_id+"&season_id="+season_id+"&episode_id="+episode_id+"&row="+row+"&category="+category_id+"&fav="+fav+
                                "&sortby="+sortby+"&hd="+hd+"&not_ended="+not_ended+"&p="+page+"&JsHttpRequest=1-xml&abc="+abc+"&genre="+genre_search+"&years="+year+"&search="+pin_code;
                        getMovies();
                    }
                    @Override
                    public void OnCancelClick(Dialog dialog, String pin_code) {
                        dialog.dismiss();
                    }
                });
                searchDlg.show();
                break;
            case R.id.btn_pick:
                MyApp.instance.getPreference().remove(Constants.VOD_SEARCH);
                ly_searchDlg.setVisibility(View.INVISIBLE);
                ly_searchDlg.setFocusable(false);
                pickingDlg.setVisibility(View.VISIBLE);
                wheelLeft.requestFocus();
                wheelLeft.setFocusable(true);
                wheelLeft.setItemTextColor(getResources().getColor(R.color.black));
                wheelCenter.setItemTextColor(getResources().getColor(R.color.wheel_color));
                break;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent me) {
//        Log.e("last_pos",String .valueOf(channel_list.getLastVisiblePosition()));
        this.detector.onTouchEvent(me);
        return super.dispatchTouchEvent(me);
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
                    if(vod_list.getFirstVisiblePosition()==0){
                        if(page==1){
                            is_up = true;
                            MyApp.touch = true;
                            page = total_page;
                            getMovies();
                        }else if(page>1){
                            page--;
                            is_up = true;
                            MyApp.touch = true;
                            getMovies();
                        }
                    }
                }
                break;
            case SimpleGestureFilter.SWIPE_UP:
                if(txt_progress.getVisibility()==View.GONE){
                    if(vod_list.getLastVisiblePosition()==max_page_items-1){
                        page++;
                        is_up = false;
                        MyApp.touch = true;
                        getMovies();
                    }else if(vod_list.getLastVisiblePosition()==(total_items%max_page_items)-1){
                        page=1;
                        is_up = false;
                        MyApp.touch = true;
                        getMovies();
                    }
                }
                break;
        }
    }

    @Override
    public void onDoubleTap() {

    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        View view = getCurrentFocus();
        if (event.getAction() == KeyEvent.ACTION_DOWN){
            switch (event.getKeyCode()){
                case KeyEvent.KEYCODE_MENU:
                    PackageDlg packageDlg = new PackageDlg(this, pkg_datas, new PackageDlg.DialogPackageListener() {
                        @Override
                        public void OnItemClick(Dialog dialog, int position) {
                            switch (position){
                                case 0:
                                    if(ly_viewDlg.getVisibility()==View.INVISIBLE){
                                        is_dlg = true;
                                        ly_sortDlg.setVisibility(View.INVISIBLE);
                                        ly_searchDlg.setVisibility(View.INVISIBLE);
                                        ly_viewDlg.setVisibility(View.VISIBLE);
                                        pickingDlg.setVisibility(View.GONE);
                                        vod_list.setFocusable(false);
                                        ly_viewDlg.requestFocus();
                                        ly_viewDlg.setFocusable(true);
                                        if(ly_right.getVisibility()==View.VISIBLE){
                                            btn_list_info.requestFocus();
                                        }else {
                                            btn_list.requestFocus();
                                        }
                                    }else {
                                        vod_list.requestFocus();
                                        vod_list.setFocusable(true);
                                        ly_viewDlg.setFocusable(false);
                                        is_dlg = false;
                                        ly_viewDlg.setVisibility(View.INVISIBLE);
                                    }
                                    break;
                                case 1:
                                    if(ly_sortDlg.getVisibility()==View.INVISIBLE){
                                        is_dlg = true;
                                        if(is_up){
                                            is_up = false;
                                        }
                                        pickingDlg.setVisibility(View.GONE);
                                        ly_viewDlg.setVisibility(View.INVISIBLE);
                                        ly_searchDlg.setVisibility(View.INVISIBLE);
                                        ly_sortDlg.setVisibility(View.VISIBLE);
                                        ly_sortDlg.requestFocus();
                                        ly_sortDlg.setFocusable(true);
                                        vod_list.setFocusable(false);
                                        if(sort==0){
                                            btn_by_addtime.requestFocus();
                                        }else if(sort==1){
                                            btn_byrating.requestFocus();
                                        }else if(sort==2){
                                            btn_bytitle.requestFocus();
                                        }else if(sort==3){
                                            btn_rating.requestFocus();
                                        }else if(sort==4){
                                            btn_byhd.requestFocus();
                                        }else if(sort==5){
                                            btn_byfav.requestFocus();
                                        }else {
                                            btn_byended.requestFocus();
                                        }
                                    }else {
                                        vod_list.setFocusable(true);
                                        vod_list.requestFocus();
                                        ly_sortDlg.setFocusable(false);
                                        is_dlg = false;
                                        ly_sortDlg.setVisibility(View.INVISIBLE);
                                    }
                                    break;
                                case 2:
                                    pickingDlg.setVisibility(View.GONE);
                                    ly_sortDlg.setVisibility(View.INVISIBLE);
                                    ly_searchDlg.setVisibility(View.INVISIBLE);
                                    ly_viewDlg.setVisibility(View.INVISIBLE);
                                    video_id = movieModelList.get(selected_num).getId();
                                    if(movieModelList.get(selected_num).getFav()==0){
                                        Url = Constants.GetUrl(PreviewVodActivity.this)+"portal.php?type=vod&action=set_fav&video_id="+video_id+"&JsHttpRequest=1-xml";
                                        movieModelList.get(selected_num).setFav(1);
                                    }else {
                                        Url = Constants.GetUrl(PreviewVodActivity.this)+"portal.php?type=vod&action=del_fav&video_id="+video_id+"&JsHttpRequest=1-xml";
                                        movieModelList.get(selected_num).setFav(0);
                                    }
                                    vodListAdapter = new VodListAdapter(PreviewVodActivity.this,movieModelList);
                                    vod_list.setAdapter(vodListAdapter);
                                    vod_list.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            vod_list.setSelection(selected_num);
                                            vod_list.smoothScrollToPosition(selected_num);
                                        }
                                    });
                                    vod_list.setFocusable(true);
                                    vod_list.requestFocus();
                                    GetDataService dataService = new GetDataService(PreviewVodActivity.this);
                                    dataService.getData(PreviewVodActivity.this,Url,400);
                                    dataService.onGetResultsData(PreviewVodActivity.this);
                                    break;
                                case 3:
                                    if(ly_searchDlg.getVisibility()==View.INVISIBLE){
                                        is_dlg = true;
                                        ly_sortDlg.setVisibility(View.INVISIBLE);
                                        ly_viewDlg.setVisibility(View.INVISIBLE);
                                        ly_searchDlg.setVisibility(View.VISIBLE);
                                        ly_searchDlg.requestFocus();
                                        ly_searchDlg.setFocusable(true);
                                        vod_list.setFocusable(false);
                                        if(MyApp.instance.getPreference().get(Constants.VOD_SEARCH)==null){
                                            btn_picking.requestFocus();
                                        }else {
                                            btn_search.requestFocus();
                                        }
                                    }else {
                                        if(pickingDlg.getVisibility()==View.VISIBLE){
                                            pickingDlg.setVisibility(View.GONE);
                                        }else {
                                            vod_list.setFocusable(true);
                                            vod_list.requestFocus();
                                            ly_searchDlg.setFocusable(false);
                                            is_dlg = false;
                                            ly_searchDlg.setVisibility(View.INVISIBLE);
                                        }
                                    }
                                    break;
                            }
                        }
                    });
                    packageDlg.show();
                    break;
                case KeyEvent.KEYCODE_BACK:
                    if(ly_viewDlg.getVisibility()==View.VISIBLE){
                        is_dlg = false;
                        vod_list.requestFocus();
                        vod_list.setFocusable(true);
                        ly_viewDlg.setVisibility(View.GONE);
                        return true;
                    }
                    if(ly_sortDlg.getVisibility()==View.VISIBLE){
                        is_dlg = false;
                        vod_list.requestFocus();
                        vod_list.setFocusable(true);
                        ly_sortDlg.setVisibility(View.GONE);
                        return true;
                    }
                    if(ly_searchDlg.getVisibility()==View.VISIBLE){
                        MyApp.is_search = false;
                        is_dlg = false;
                        vod_list.requestFocus();
                        vod_list.setFocusable(true);
                        ly_searchDlg.setVisibility(View.GONE);
                        return true;
                    }
                    if(pickingDlg.getVisibility()==View.VISIBLE){
                        MyApp.is_search = false;
                        pickingDlg.setVisibility(View.GONE);
                        is_dlg = false;
                        vod_list.requestFocus();
                        vod_list.setFocusable(true);
                        return true;
                    }
                    finish();
                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if(view==wheelCenter){
                        wheelCenter.setFocusable(false);
                        wheelLeft.requestFocus();
                        wheelLeft.setFocusable(true);
                        wheelLeft.setItemTextColor(getResources().getColor(R.color.black));
                        wheelCenter.setItemTextColor(getResources().getColor(R.color.wheel_color));
                    }else if(view==wheelRight){
                        wheelRight.setFocusable(false);
                        wheelCenter.setFocusable(true);
                        wheelCenter.requestFocus();
                        wheelCenter.setItemTextColor(getResources().getColor(R.color.black));
                        wheelRight.setItemTextColor(getResources().getColor(R.color.wheel_color));
                    }else if(!is_dlg){
                        finish();
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if(view==wheelLeft){
                        wheelLeft.setFocusable(false);
                        wheelCenter.requestFocus();
                        wheelCenter.setFocusable(true);
                        wheelLeft.setItemTextColor(getResources().getColor(R.color.wheel_color));
                        wheelCenter.setItemTextColor(getResources().getColor(R.color.black));
                    }else if(view==wheelCenter){
                        wheelCenter.setFocusable(false);
                        wheelRight.setFocusable(true);
                        wheelRight.requestFocus();
                        wheelCenter.setItemTextColor(getResources().getColor(R.color.wheel_color));
                        wheelRight.setItemTextColor(getResources().getColor(R.color.black));
                    }else if(!is_dlg){
                        if(movieModelList!=null && movieModelList.size()>0){
                            Intent intent = new Intent(PreviewVodActivity.this,VodInFoActivity.class);
                            intent.putExtra("title",movieModelList.get(selected_num).getName());
                            intent.putExtra("year",movieModelList.get(selected_num).getYear());
                            intent.putExtra("genre",movieModelList.get(selected_num).getGenres_str());
                            intent.putExtra("length",movieModelList.get(selected_num).getTime());
                            intent.putExtra("director",movieModelList.get(selected_num).getDirector());
                            intent.putExtra("cast",movieModelList.get(selected_num).getActors());
                            intent.putExtra("age",movieModelList.get(selected_num).getAge());
                            intent.putExtra("dec",movieModelList.get(selected_num).getDescription());
                            intent.putExtra("img",movieModelList.get(selected_num).getScreenshot_uri());
                            startActivity(intent);
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_UP:
                   if(view==vod_list){
                       is_up = true;
                       if(txt_progress.getVisibility()==View.GONE){
                           if(page==1 && selected_num==0){
                               page = total_page;
                               getMovies();
                           }else if(page>1 && selected_num==0){
                               page--;
                               getMovies();
                           }else if(selected_num>0){
                               selected_num--;
                               if(MyApp.touch){
                                   MyApp.touch = false;
                                   vodListAdapter = new VodListAdapter(PreviewVodActivity.this,movieModelList);
                                   vod_list.setAdapter(vodListAdapter);
                                   vod_list.setSelection(selected_num);
                               }
                               printDetailData();
                           }
                       }
                   }
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if(view==vod_list){
                        is_up = false;
                        if(txt_progress.getVisibility()==View.GONE){
                            if(page<total_page && selected_num<max_page_items-1){
                                selected_num++;
                                if(MyApp.touch){
                                    MyApp.touch = false;
                                    vodListAdapter = new VodListAdapter(PreviewVodActivity.this,movieModelList);
                                    vod_list.setAdapter(vodListAdapter);
                                    vod_list.setSelection(selected_num);
                                }
                                printDetailData();
                            }else if(page==total_page && selected_num<(total_items%max_page_items-1)){
                                selected_num++;
                                if(MyApp.touch){
                                    MyApp.touch = false;
                                    vodListAdapter = new VodListAdapter(PreviewVodActivity.this,movieModelList);
                                    vod_list.setAdapter(vodListAdapter);
                                    vod_list.setSelection(selected_num);
                                }
                                printDetailData();
                            }else if(page<total_page && selected_num==max_page_items-1){
                                page++;
                                getMovies();
                            }else if(page==total_page && selected_num==(total_items%max_page_items-1)){
                                page=1;
                                getMovies();
                            }
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_CENTER:
                    if(view==wheelLeft){
                        MyApp.is_search = true;
                        abc_pos = wheelLeft.getCurrentItemPosition();
                        Url = Constants.GetUrl(this)+"portal.php?type=vod&action=get_ordered_list&movie_id="+movie_id+"&season_id="+season_id+"&episode_id="+episode_id+"&row="+row+"&category="+category_id
                                +"&fav="+fav+"&sortby="+sortby+"&hd="+hd+"&not_ended="+not_ended+"&p="+page+"&JsHttpRequest=1-xml&abc="+abcModelList.get(abc_pos)+"&genre="+genreModelList.get(genre_pos).getId()+"&years="+yearModelList.get(year_pos);
                        getMovies();
                        if(abc_pos!=0){
                            if(genre_pos!=0){
                                if(year_pos!=0){
                                    txt_category.setText(vodGenreModels.get(category_pos).getTitle().toUpperCase()+" /BY /" + abcModelList.get(abc_pos) +" /"+ genreModelList.get(genre_pos).getName().toUpperCase()+"/ "+yearModelList.get(year_pos).toUpperCase());
                                }else {
                                    txt_category.setText(vodGenreModels.get(category_pos).getTitle().toUpperCase()+" /BY /" + abcModelList.get(abc_pos) +" /"+ genreModelList.get(genre_pos).getName().toUpperCase());
                                }
                            }else {
                                if(year_pos!=0){
                                    txt_category.setText(vodGenreModels.get(category_pos).getTitle().toUpperCase()+" /BY /"+ abcModelList.get(abc_pos) +" /" + yearModelList.get(year_pos).toUpperCase());
                                }else {
                                    txt_category.setText(vodGenreModels.get(category_pos).getTitle().toUpperCase()+" /BY /"+ abcModelList.get(abc_pos) +" /");
                                }
                            }
                        }else {
                            if(genre_pos!=0){
                                if(year_pos!=0){
                                    txt_category.setText(vodGenreModels.get(category_pos).getTitle().toUpperCase()+" /BY /" + genreModelList.get(genre_pos).getName().toUpperCase()+"/ "+yearModelList.get(year_pos).toUpperCase());
                                }else {
                                    txt_category.setText(vodGenreModels.get(category_pos).getTitle().toUpperCase()+" /BY /" + genreModelList.get(genre_pos).getName().toUpperCase());
                                }
                            }else {
                                if(year_pos!=0){
                                    txt_category.setText(vodGenreModels.get(category_pos).getTitle().toUpperCase()+" /BY /" + yearModelList.get(year_pos).toUpperCase());
                                }else {
                                    txt_category.setText(vodGenreModels.get(category_pos).getTitle().toUpperCase()+" /BY / PICKING");
                                }
                            }
                        }

                    }else if(view==wheelCenter){
                        MyApp.is_search = true;
                        genre_pos = wheelCenter.getCurrentItemPosition();
                        Url = Constants.GetUrl(this)+"portal.php?type=vod&action=get_ordered_list&movie_id="+movie_id+"&season_id="+season_id+"&episode_id="+episode_id+"&row="+row+"&category="+category_id
                                +"&fav="+fav+"&sortby="+sortby+"&hd="+hd+"&not_ended="+not_ended+"&p="+page+"&JsHttpRequest=1-xml&abc="+abcModelList.get(abc_pos)+"&genre="+genreModelList.get(genre_pos).getId()+"&years="+yearModelList.get(year_pos);
                        getMovies();
                        if(genre_pos!=0){
                            if(abc_pos!=0){
                                if(year_pos!=0){
                                    txt_category.setText(vodGenreModels.get(category_pos).getTitle().toUpperCase()+" /BY /" + abcModelList.get(abc_pos).toUpperCase() +" /"+ genreModelList.get(genre_pos).getName().toUpperCase()+"/ "+yearModelList.get(year_pos).toUpperCase());
                                }else {
                                    txt_category.setText(vodGenreModels.get(category_pos).getTitle().toUpperCase()+" /BY /" + abcModelList.get(abc_pos) +" /"+ genreModelList.get(genre_pos).getName().toUpperCase());
                                }
                            }else {
                                if(year_pos!=0){
                                    txt_category.setText(vodGenreModels.get(category_pos).getTitle().toUpperCase()+" /BY /*/"+ genreModelList.get(genre_pos).getName().toUpperCase() +" /" + yearModelList.get(year_pos).toUpperCase());
                                }else {
                                    txt_category.setText(vodGenreModels.get(category_pos).getTitle().toUpperCase()+" /BY /*/"+ genreModelList.get(genre_pos).getName().toUpperCase() +" /");
                                }
                            }
                        }else {
                            if(abc_pos!=0){
                                if(year_pos!=0){
                                    txt_category.setText(vodGenreModels.get(category_pos).getTitle().toUpperCase()+" /BY /" + abcModelList.get(abc_pos).toUpperCase()+"/*/ "+yearModelList.get(year_pos).toUpperCase());
                                }else {
                                    txt_category.setText(vodGenreModels.get(category_pos).getTitle().toUpperCase()+" /BY /" + abcModelList.get(abc_pos).toUpperCase());
                                }
                            }else {
                                if(year_pos!=0){
                                    txt_category.setText(vodGenreModels.get(category_pos).getTitle().toUpperCase()+" /BY /*/*/" + yearModelList.get(year_pos).toUpperCase());
                                }else {
                                    txt_category.setText(vodGenreModels.get(category_pos).getTitle().toUpperCase()+" /BY / PICKING");
                                }
                            }
                        }
                    }else if(view==wheelRight){
                        MyApp.is_search = true;
                        year_pos = wheelRight.getCurrentItemPosition();
                        Url = Constants.GetUrl(this)+"portal.php?type=vod&action=get_ordered_list&movie_id="+movie_id+"&season_id="+season_id+"&episode_id="+episode_id+"&row="+row+"&category="+category_id
                                +"&fav="+fav+"&sortby="+sortby+"&hd="+hd+"&not_ended="+not_ended+"&p="+page+"&JsHttpRequest=1-xml&abc="+abcModelList.get(abc_pos)+"&genre="+genreModelList.get(genre_pos).getId()+"&years="+yearModelList.get(year_pos);
                        getMovies();
                        txt_category.setText(vodGenreModels.get(category_pos).getTitle().toUpperCase()+" /BY /" + abcModelList.get(abc_pos).toUpperCase() +" /"+ genreModelList.get(genre_pos).getName().toUpperCase()+"/ "+yearModelList.get(year_pos).toUpperCase());
                    }else if(view==vod_list){
                        String cmd = movieModelList.get(selected_num).getCmd();
                        vod_title = movieModelList.get(selected_num).getName();
                        vod_description = movieModelList.get(selected_num).getDescription();
                        vod_image = movieModelList.get(selected_num).getScreenshot_uri();
                        Url = Constants.GetUrl(this)+"portal.php?type=vod&action=create_link&cmd="+cmd+"&series=&forced_storage=&disable_ad=0&download=0&force_ch_link_check=0&JsHttpRequest=1-xml";
                        GetDataService dataService = new GetDataService(PreviewVodActivity.this);
                        dataService.getData(PreviewVodActivity.this,Url,600);
                        dataService.onGetResultsData(PreviewVodActivity.this);
                    }
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(adapterView==vod_list){
            if(txt_progress.getVisibility()==View.VISIBLE){
                return;
            }
            if(movieModelList.get(selected_num).getName().equalsIgnoreCase(movieModelList.get(i).getName())){
                String cmd = movieModelList.get(i).getCmd().replace("=","");
                vod_title = movieModelList.get(i).getName();
                vod_description = movieModelList.get(i).getDescription();
                vod_image = movieModelList.get(i).getScreenshot_uri();
//                Url = Constants.GetUrl(this)+"portal.php?type=vod&action=create_link&cmd="+cmd+"&series=&forced_storage=&disable_ad=0&download=0&force_ch_link_check=0&JsHttpRequest=1-xml";
                GetDataService dataService = new GetDataService(PreviewVodActivity.this);
                dataService.getData(PreviewVodActivity.this,Constants.GetUrl(this)+"portal.php?type=vod&action=create_link&cmd="+cmd+"&series=&forced_storage=&disable_ad=0&download=0&force_ch_link_check=0&JsHttpRequest=1-xml",600);
                dataService.onGetResultsData(PreviewVodActivity.this);
            }else {
                selected_num = i;
                printDetailData();
                MyApp.touch = true;
                vodListAdapter = new VodListAdapter(PreviewVodActivity.this,movieModelList);
                vod_list.setAdapter(vodListAdapter);
                vodListAdapter.selectItem(selected_num);
                vod_list.setSelection(selected_num);
            }
            ly_searchDlg.setVisibility(View.GONE);
            ly_viewDlg.setVisibility(View.GONE);
            ly_sortDlg.setVisibility(View.GONE);
        }
    }
    @Override
    public void onItemSelected(WheelPicker picker, Object data, int position) {
        String text = "";
        switch (picker.getId()) {
            case R.id.wheel_left:
                abc_pos = position;
                break;
            case R.id.wheel_center:
                genre_pos = position;
                break;
            case R.id.wheel_right:
                year_pos = position;
                break;
        }
//        Toast.makeText(this, text + String.valueOf(data), Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onGetResultsData(JSONObject object, int request_code) {
        if(object!=null){
            if(request_code==100){
                try {
                    movieModelList = new ArrayList<>();
                    JSONObject all_movie_obj = object.getJSONObject("js");
                    total_items = all_movie_obj.getInt("total_items");
                    max_page_items = all_movie_obj.getInt("max_page_items");
                    if(total_items%max_page_items==0){
                        total_page = total_items/max_page_items;
                    }else {
                        total_page = total_items/max_page_items +1;
                    }
                    if(!is_up){
                        selected_num = all_movie_obj.getInt("selected_item")-1;
                        if(selected_num<0){
                            selected_num=0;
                        }
                    }else {
                        if(page<total_page){
                            selected_num=max_page_items-1;
                        }else {
                            selected_num = total_items%max_page_items-1;
                        }
                    }
                    if(page==0){
                        page = all_movie_obj.getInt("cur_page");
                    }
                    JSONArray movie_array = all_movie_obj.getJSONArray("data");
                    for(int i = 0;i<movie_array.length();i++){
                        JSONObject movie_obj = movie_array.getJSONObject(i);
                        try {
                            MovieModel movieModel = new MovieModel();
                            movieModel.setId(movie_obj.getString("id"));
                            movieModel.setName(movie_obj.getString("name"));
                            movieModel.setDescription(movie_obj.getString("description"));
                            movieModel.setTime(movie_obj.getInt("time"));
                            movieModel.setCategory_id(movie_obj.getString("category_id"));
                            movieModel.setHd(movie_obj.getInt("hd"));
                            movieModel.setDirector(movie_obj.getString("director"));
                            movieModel.setActors(movie_obj.getString("actors"));
                            movieModel.setYear(movie_obj.getString("year"));
                            try {
                                movieModel.setRate(movie_obj.getString("rate"));
                            }catch (Exception e1){
                                movieModel.setRate("0");
                            }
//                            movieModel.setRating_last_update(movie_obj.getString("rating_last_update"));
                            movieModel.setAge(movie_obj.getString("age"));
                            movieModel.setRating_kinopoisk(movie_obj.getString("rating_kinopoisk"));
//                            movieModel.setIs_movie(movie_obj.getInt("is_movie"));
                            movieModel.setLock(movie_obj.getInt("lock"));
                            try {
                                movieModel.setFav(movie_obj.getInt("fav"));
                            }catch (Exception e2){
                                movieModel.setFav(0);
                            }
                            movieModel.setScreenshot_uri(movie_obj.getString("screenshot_uri"));
                            movieModel.setCmd(movie_obj.getString("cmd"));
                            try {
                                movieModel.setWeek_and_more(movie_obj.getString("week_and_more"));
                            }catch (Exception e3){
                                movieModel.setWeek_and_more("n/A");
                            }
                            movieModel.setGenres_str(movie_obj.getString("genres_str"));
                            movieModelList.add(movieModel);
                        }catch (Exception e1){
                            Log.e("error","error");
                        }
                    }
                    txt_progress.setVisibility(View.GONE);
                    vodListAdapter = new VodListAdapter(PreviewVodActivity.this,movieModelList);
                    vod_list.setAdapter(vodListAdapter);
                    vod_list.setSelection(selected_num);
                    if(MyApp.touch){
                        vodListAdapter.selectItem(selected_num);
                        MyApp.touch = false;
                    }
                }catch (Exception e){
                    Log.e("error","error1");
                }
                printDetailData();
                if(is_first){
                    is_first = false;
                    GetDataService dataService = new GetDataService(PreviewVodActivity.this);
//                    Url = Constants.GetUrl(this)+"portal.php?type=vod&action=get_abc&JsHttpRequest=1-xml";
                    dataService.getData(PreviewVodActivity.this,Constants.GetUrl(this)+"portal.php?type=vod&action=get_abc&JsHttpRequest=1-xml",200);
                    dataService.onGetResultsData(PreviewVodActivity.this);
                }
            }else if(request_code==200){
                abcModelList = new ArrayList<>();
                try {
                    JSONArray abc_array = object.getJSONArray("js");
                    for(int i = 0;i<abc_array.length();i++){
                        JSONObject abc_obj = abc_array.getJSONObject(i);
                        abcModelList.add(abc_obj.getString("title"));
                    }
                }catch (Exception e){
                }
                if(abcModelList.size()<1){
                    abcModelList.add("*");
                }
                wheelLeft.setData(abcModelList);
//                Url = Constants.GetUrl(this)+"/portal.php?type=vod&action=get_genres_by_category_alias&cat_alias="+genre+"&JsHttpRequest=1-xml";
                GetDataService dataService = new GetDataService(PreviewVodActivity.this);
                dataService.getData(PreviewVodActivity.this,Constants.GetUrl(this)+"/portal.php?type=vod&action=get_genres_by_category_alias&cat_alias="+genre+"&JsHttpRequest=1-xml",300);
                dataService.onGetResultsData(PreviewVodActivity.this);
            }else if(request_code==300){
                genreModelList = new ArrayList<>();
                try {
                    JSONArray abc_array = object.getJSONArray("js");
                    for(int i = 0;i<abc_array.length();i++){
                        JSONObject abc_obj = abc_array.getJSONObject(i);
                        ABCModel abcModel = new ABCModel();
                        abcModel.setId(abc_obj.getString("id"));
                        abcModel.setName(abc_obj.getString("title"));
                        genreModelList.add(abcModel);
                    }
                }catch (Exception e){
                }
                List<String >genreTitle = new ArrayList<>();
                for(int i = 0;i<genreModelList.size();i++){
                    genreTitle.add(genreModelList.get(i).getName());
                }
                if(genreTitle.size()<1){
                    genreTitle.add("*");
                }
                wheelCenter.setData(genreTitle);
//                Url = Constants.GetUrl(this)+"portal.php?type=vod&action=get_years&category="+category_id+"&JsHttpRequest=1-xml";
                GetDataService dataService = new GetDataService(PreviewVodActivity.this);
                dataService.getData(PreviewVodActivity.this,Constants.GetUrl(this)+"portal.php?type=vod&action=get_years&category="+category_id+"&JsHttpRequest=1-xml",500);
                dataService.onGetResultsData(PreviewVodActivity.this);
            }else if(request_code==400){
            }else if(request_code==500){
                yearModelList = new ArrayList<>();
                try {
                    JSONArray year_array = object.getJSONArray("js");
                    for(int i = 0;i<year_array.length();i++){
                        JSONObject abc_obj = year_array.getJSONObject(i);
                        yearModelList.add(abc_obj.getString("title"));
                    }
                }catch (Exception e){
                }
                if(yearModelList.size()<1){
                    yearModelList.add("*");
                }
                wheelRight.setData(yearModelList);
            }else if(request_code==600){
                try {
                    JSONObject vod_obj = object.getJSONObject("js");
                    vod_url = vod_obj.getString("cmd").replace("ffmpeg ","");
                    vod_id = vod_obj.getString("id");
                }catch (Exception e){
                }
                if(is_vod){
                    if(MyApp.instance.getPreference().get(Constants.IS_PHONE)==null){
                        is_vod = false;
                        Intent intent = new Intent(PreviewVodActivity.this,VodPlayerActivity.class);
                        intent.putExtra("title",vod_title);
                        intent.putExtra("content_Uri",vod_url);
                        intent.putExtra("image_Url",vod_image);
                        intent.putExtra("description",vod_description);
//                    Log.e("vod_url",vod_url);
                        startActivity(intent);
                    }else {
                        is_vod = false;
                        Intent intent = new Intent(PreviewVodActivity.this,VodPlayerActivity.class);
                        intent.putExtra("title",vod_title);
                        intent.putExtra("content_Uri",vod_url);
                        intent.putExtra("image_Url",vod_image);
                        intent.putExtra("description",vod_description);
//                    Log.e("vod_url",vod_url);
                        startActivity(intent);
                    }

                }
            }
        }
    }

    private void getMovies(){
        txt_progress.setVisibility(View.VISIBLE);
        if(!MyApp.is_search){
            Url = Constants.GetUrl(this)+"portal.php?type=vod&action=get_ordered_list&movie_id="+movie_id+"&season_id="+season_id+"&episode_id="+episode_id+"&row="+row+"&category="+category_id+"&fav="+fav+"&sortby="+sortby+"&hd="+hd+"&not_ended="+not_ended+"&p="+page+"&JsHttpRequest=1-xml";
        }else {
            MyApp.is_search = false;
        }
        GetDataService dataService = new GetDataService(PreviewVodActivity.this);
        dataService.getData(PreviewVodActivity.this,Url,100);
        dataService.onGetResultsData(PreviewVodActivity.this);
    }

    private void printDetailData(){
        if(movieModelList.size()>0){
            txt_rating.setText(movieModelList.get(selected_num).getRating_kinopoisk());
            txt_age.setText(movieModelList.get(selected_num).getAge());
            txt_genre.setText(movieModelList.get(selected_num).getGenres_str());
            txt_year.setText(movieModelList.get(selected_num).getYear());
            txt_length.setText(String .valueOf(movieModelList.get(selected_num).getTime()) + "min");
            txt_director.setText(movieModelList.get(selected_num).getDirector());
            try {
                Picasso.with(PreviewVodActivity.this).load(movieModelList.get(selected_num).getScreenshot_uri())
                        .placeholder(R.drawable.icon_default)
                        .error(R.drawable.icon_default)
                        .into(image_movie);
            }catch (Exception e){
                Picasso.with(PreviewVodActivity.this).load(R.drawable.icon_default).into(image_movie);
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
    public void FullScreencall() {
        //for new api versions.
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }
}
