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
import com.kds.gold.acebird.models.ChannelModel;
import com.kds.gold.acebird.utils.Utils;

import java.util.List;

/**
 * Created by RST on 7/19/2017.
 */

public class RadioListAdapter extends BaseAdapter{

    Context context;
    List<ChannelModel> datas;
    LayoutInflater inflater;
    int selected_pos;
    TextView title,num;
    LinearLayout main_lay,ly_info;
    ImageView image_play,image_clock,image_star,channel_logo;
    View vew;
    public RadioListAdapter(Context context, List<ChannelModel> datas) {
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
            convertView = inflater.inflate(R.layout.item_main_list, parent, false);
        }
        main_lay = (LinearLayout) convertView.findViewById(R.id.main_lay);
        title = (TextView) convertView.findViewById(R.id.main_list_txt);
        num = (TextView)convertView.findViewById(R.id.main_list_num);
        image_play = (ImageView)convertView.findViewById(R.id.image_play);
        image_star = (ImageView)convertView.findViewById(R.id.image_star);
        image_clock = (ImageView)convertView.findViewById(R.id.image_clock);
        channel_logo = (ImageView)convertView.findViewById(R.id.channel_logo);
        channel_logo.setVisibility(View.GONE);
        image_clock.setVisibility(View.GONE);
        image_play.setVisibility(View.GONE);
        vew = (View)convertView.findViewById(R.id.view);
        ly_info = (LinearLayout)convertView.findViewById(R.id.ly_info);
        title.setText(datas.get(position).getName());
        num.setText(datas.get(position).getNumber());
        if(datas.get(position).getFav()==1){
            image_star.setVisibility(View.VISIBLE);
        }else {
            image_star.setVisibility(View.GONE);
        }

        vew.setVisibility(View.GONE);
        ly_info.setVisibility(View.GONE);
        num.setTextColor(Color.parseColor("#18477f"));
        num.setBackgroundResource(R.drawable.yelloback);
        main_lay.setBackgroundResource(R.drawable.list_item_channel_draw);
        if(MyApp.instance.getPreference().get(Constants.IS_PHONE)!=null){
            main_lay.setPadding(Utils.dp2px(context, 5), Utils.dp2px(context, 5), Utils.dp2px(context, 5), Utils.dp2px(context, 5));
        }else {
            main_lay.setPadding(Utils.dp2px(context, 5), Utils.dp2px(context, 5), Utils.dp2px(context, 5), Utils.dp2px(context, 5));
        }

        if(selected_pos == position){
            main_lay.setBackgroundResource(R.drawable.list_yellow_bg);
            title.setTextColor(Color.parseColor("#000000"));
            num.setBackgroundResource(R.drawable.white_btn_border);
        }else {
            title.setTextColor(Color.parseColor("#ffffff"));
        }

        return convertView;
    }

    public void selectItem(int pos) {
        selected_pos = pos;
        notifyDataSetChanged();
    }
}
