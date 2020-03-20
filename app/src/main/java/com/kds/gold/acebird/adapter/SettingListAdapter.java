package com.kds.gold.acebird.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kds.gold.acebird.R;

import java.util.List;

public class SettingListAdapter extends BaseAdapter{
    Context context;
    List<String> datas;
    LayoutInflater inflater;

    public SettingListAdapter(Context context, List<String> datas) {
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
            convertView = inflater.inflate(R.layout.item_setting_list, parent, false);
        }
        TextView textView = (TextView) convertView.findViewById(R.id.setting_item);
        textView.setText(datas.get(position));
        ImageView imageView = (ImageView)convertView.findViewById(R.id.setting_image);
        switch (position){
            case 0:
                imageView.setBackgroundResource(R.drawable.icon_lock);
                break;
            case 1:
                imageView.setBackgroundResource(R.drawable.icon_reload);
                break;
            case 2:
                imageView.setBackgroundResource(R.drawable.icon_reboot);
                break;
            case 3:
                imageView.setBackgroundResource(R.drawable.icon_osd);
                break;
            case 4:
                imageView.setBackgroundResource(R.drawable.icon_osd);
                break;
            case 5:
                imageView.setBackgroundResource(R.drawable.icon_log_out);
                break;

        }
        return convertView;
    }
}
