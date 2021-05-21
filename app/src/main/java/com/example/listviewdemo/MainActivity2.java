package com.example.listviewdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Switch;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.util.ArrayList;

public class MainActivity2 extends AppCompatActivity {

    private SwipeMenuListView swipeMenuListView ;
    private MyAdaptor adaptor;
    private ArrayList<String> datalist;
    private SwipeMenuCreator creator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        initdata();
        swipeMenuListView = findViewById(R.id.swip_ls);
        adaptor = new MyAdaptor(this,datalist);
        swipeMenuListView.setMenuCreator(creator);
        swipeMenuListView.setAdapter(adaptor);
        swipeMenuListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch(index){
                    case 0 :
                        Log.d("luojie","open");
                        break;
                    case 1 :
                        datalist.remove(position);
                        adaptor.notifyDataSetChanged();
                        Log.d("luojie","delete");
                        break;
                }
                return false;
            }
        });
    }

    public void initdata(){
        datalist = new ArrayList<>();
        for(int i = 0 ; i <100 ; i++){
            datalist.add(i + "");
        }

         creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                        0xCE)));
                // set item width
                openItem.setWidth(dp2px(90));
                // set item title
                openItem.setTitle("Open");
                // set item title fontsize
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                deleteItem.setTitle("删除");
                // set a icon
                deleteItem.setIcon(R.mipmap.ic_launcher);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }


}