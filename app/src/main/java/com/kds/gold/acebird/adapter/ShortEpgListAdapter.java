package com.kds.gold.acebird.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kds.gold.acebird.R;
import com.kds.gold.acebird.apps.Constants;
import com.kds.gold.acebird.apps.MyApp;
import com.kds.gold.acebird.models.EpgModel;
import com.kds.gold.acebird.utils.Utils;

import java.util.List;

public class ShortEpgListAdapter extends BaseAdapter {
    Context context;
    List<EpgModel> datas;
    LayoutInflater inflater;
    TextView title,time;
    LinearLayout main_lay;
    public ShortEpgListAdapter(Context context, List<EpgModel> datas) {
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
            convertView = inflater.inflate(R.layout.item_epg_short_list, parent, false);
        }
        main_lay = (LinearLayout) convertView.findViewById(R.id.main_lay);
        title = (TextView) convertView.findViewById(R.id.main_epg_txt);
        time = (TextView)convertView.findViewById(R.id.main_date_num);
        title.setText(datas.get(position).getName());
        time.setText(datas.get(position).getT_time());
        if(position==0){
            time.setTextColor(context.getResources().getColor(R.color.yellow));
            title.setTextColor(context.getResources().getColor(R.color.yellow));
        }
        if(MyApp.instance.getPreference().get(Constants.IS_PHONE)!=null){
            main_lay.setPadding(Utils.dp2px(context, 3), Utils.dp2px(context, 3), Utils.dp2px(context, 3), Utils.dp2px(context, 3));
        }else {
            main_lay.setPadding(Utils.dp2px(context, 5), Utils.dp2px(context, 5), Utils.dp2px(context, 5), Utils.dp2px(context, 5));
        }
        return convertView;
    }
    @Override
    public boolean isEnabled(int position) {
        return false;
    }
}
