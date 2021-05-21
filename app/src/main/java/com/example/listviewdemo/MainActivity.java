package com.example.listviewdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.example.listviewdemo.listview.PullRefreshView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements PullRefreshView.OnRefreshListener {

   private ArrayList<String> datalist;
   private PullRefreshView mylistview;
   private MyAdaptor adaptor;
   private Handler myHandle = new Handler(){
       @Override
       public void handleMessage(@NonNull Message msg) {
           if(msg.what  == 0x123){
               mylistview.completeRefresh();
           }
       }
   };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initdata();
        mylistview.setAdapter(adaptor);
        mylistview.setOnRefreshListener(this);


    }


    public void initdata(){

        mylistview = findViewById(R.id.listview);

        datalist = new ArrayList<>();
        for(int i = 0 ; i <100 ; i++){
            datalist.add(i + "");
        }
        adaptor =new MyAdaptor(this,datalist);

    }

    @Override
    public void onPullRefresh() {
        new Thread(){

            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                    myHandle.sendEmptyMessage(0x123);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }.start();

    }

    @Override
    public void onLoadingMore() {

    }
}