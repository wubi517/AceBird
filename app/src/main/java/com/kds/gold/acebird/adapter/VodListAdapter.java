package com.kds.gold.acebird.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kds.gold.acebird.R;
import com.kds.gold.acebird.apps.Constants;
import com.kds.gold.acebird.apps.MyApp;
import com.kds.gold.acebird.models.MovieModel;
import com.kds.gold.acebird.utils.Utils;

import java.util.List;

/**
 * Created by RST on 7/19/2017.
 */

public class VodListAdapter extends BaseAdapter{

    Context context;
    List<MovieModel> datas;
    LayoutInflater inflater;
    int selected_pos;
    TextView title,hd,vod_date;
    LinearLayout main_lay,ly_info;
    ImageView image_star;
    View vew;
    public VodListAdapter(Context context, List<MovieModel> datas) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.datas = datas;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_vod_list, parent, false);
        }
        main_lay = (LinearLayout) convertView.findViewById(R.id.main_lay);
        title = (TextView) convertView.findViewById(R.id.vod_list_name);
        hd = (TextView)convertView.findViewById(R.id.vod_list_hd);
        image_star = (ImageView)convertView.findViewById(R.id.image_star);
        vod_date = (TextView)convertView.findViewById(R.id.vod_date);
        vew = (View)convertView.findViewById(R.id.view);
        ly_info = (LinearLayout)convertView.findViewById(R.id.ly_info);
        title.setText(datas.get(position).getName());
        if(datas.get(position).getHd()==1){
            hd.setText("HD");
        }
        if(datas.get(position).getFav()==1){
            image_star.setVisibility(View.VISIBLE);
        }else {
            image_star.setVisibility(View.GONE);
        }
        vod_date.setText(datas.get(position).getWeek_and_more());
        main_lay.setBackgroundResource(R.drawable.list_item_channel_draw);
        if(MyApp.instance.getPreference().get(Constants.IS_PHONE)!=null){
            main_lay.setPadding(Utils.dp2px(context, 5), Utils.dp2px(context, 5), Utils.dp2px(context, 5), Utils.dp2px(context, 5));
        }else {
            main_lay.setPadding(Utils.dp2px(context, 5), Utils.dp2px(context, 5), Utils.dp2px(context, 5), Utils.dp2px(context, 5));
        }
        if(selected_pos==position && MyApp.touch){
            main_lay.setBackgroundResource(R.drawable.list_yellow_bg);
            hd.setBackgroundResource(R.drawable.white_btn_border);
            title.setTextColor(Color.parseColor("#000000"));
            vod_date.setTextColor(Color.parseColor("#000000"));
        }
        return convertView;
    }

    public void selectItem(int pos) {
        selected_pos = pos;
        notifyDataSetChanged();
    }
}
