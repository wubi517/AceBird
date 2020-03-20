package com.kds.gold.acebird.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.kds.gold.acebird.R;

import java.util.List;

/**
 * Created by RST on 7/23/2017.
 */

public class ListDataAdapter extends BaseAdapter{

    Context context;
    List<String> datas;
    LayoutInflater inflater;
    int selected_pos;
    LinearLayout main_lay;
    public ListDataAdapter(Context context, List<String> datas) {
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
            convertView = inflater.inflate(R.layout.item_list, parent, false);
        }
        main_lay = (LinearLayout) convertView.findViewById(R.id.main_lay);
        if(selected_pos==0){
            main_lay.setBackgroundResource(R.drawable.list_info);
        }else {
            main_lay.setBackgroundResource(R.drawable.list);
        }
        return convertView;
    }
    public void selectItem(int pos) {
        selected_pos = pos;
        notifyDataSetChanged();
    }
}
