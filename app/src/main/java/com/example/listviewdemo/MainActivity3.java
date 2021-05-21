package com.example.listviewdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;


import com.example.listviewdemo.listview.SlideDeleteCancelListView;

import java.util.ArrayList;

public class MainActivity3 extends AppCompatActivity {


    private SlideDeleteCancelListView slideDeleteCancelListView ;
    private MyAdaptor adaptor;
    private ArrayList<String> datalist;
    private Handler mhandle = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
           if(msg.what == 0x123){
               slideDeleteCancelListView.loadFinish();
               adaptor.notifyDataSetChanged();
           }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        initdata();

        slideDeleteCancelListView = findViewById(R.id.slide_ls);
        adaptor = new MyAdaptor(this,datalist);
        slideDeleteCancelListView.setAdapter(adaptor);
        slideDeleteCancelListView.setDelButtonClickListener(new SlideDeleteCancelListView.DelButtonClickListener() {
            @Override
            public void onDelClick(int position) {
                datalist.remove(position);
                adaptor.notifyDataSetChanged();
            }
        });


        slideDeleteCancelListView.setCancelButtonClickListener(new SlideDeleteCancelListView.CancelButtonClickListener() {
            @Override
            public void onCancelClick(int position) {

            }
        });

        slideDeleteCancelListView.setOnILoadListener(new SlideDeleteCancelListView.ILoadListener() {
            @Override
            public void loadData() {
                    new Thread(){
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(3000);
                                getdata();
                                mhandle.sendEmptyMessage(0x123);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
            }
        });
    }



    public void getdata(){
        for (int i = 0 ; i < 100 ;i++){
            datalist.add("aaa" + i);
        }

    }

    public void initdata() {
        datalist = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            datalist.add(i + "");
        }
    }


}