package com.example.listviewdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MyAdaptor extends BaseAdapter {

    private ArrayList<String> datalist;
    private Context mContext;


    public MyAdaptor(Context mContext,ArrayList<String> datalist){
        this.datalist = datalist;
        this.mContext =mContext;
    }

    @Override
    public int getCount() {
        return datalist.size();
    }

    @Override
    public Object getItem(int position) {
        return datalist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null ;

     if(convertView ==null){
         viewHolder = new ViewHolder();
         convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item,parent,false);
         viewHolder.textView = convertView.findViewById(R.id.textView1);
         viewHolder.textView.setText(datalist.get(position));
         convertView.setTag(viewHolder);
     }else {
        viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.textView.setText(datalist.get(position));

     }


        return convertView;
    }

    class ViewHolder{

        private TextView textView;
        public ViewHolder(){

        }

    }
}
