package com.example.listviewdemo.listview;


import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.example.listviewdemo.R;

/**
 * 仿微信删除/取消关注列表
 *
 * @author 马文涛 技术群：317922608
 */
public class SlideDeleteCancelListView extends ListView implements AbsListView.OnScrollListener {

    private static final String TAG = "SlideDelListView";

    private LayoutInflater mInflater = null;

    /**
     * 用户滑动的最小距离
     */
    private int touchSlop;
    /**
     * 是否响应滑动
     */
    private boolean isSliding;
    /**
     * 手指按下时的x坐标
     */
    private int xDown;
    /**
     * 手指按下时的y坐标
     */
    private int yDown;
    /**
     * 手指移动时的x坐标
     */
    private int xMove;
    /**
     * 手指移动时的y坐标
     */
    private int yMove;

    /**
     * 当前手指触摸的View
     */
    private View mCurrentView;
    /**
     * 单签手指触摸的位置
     */
    private int mCurrentViewPos;

    /**
     * 为删除按钮提供一个回调接口
     */
    private DelButtonClickListener mDelListener = null;
    private CancelButtonClickListener mCancelListener = null;

    private PopupWindow mPopupWindow = null;
    private Button mDelBtn = null, mCancelBtn = null;

    private int mPopupWindowWidth, mPopupWindowHeight;

    private int lastVisibleItem;//最后一个可见的item
    private int totalItemCount; // 总的item
    private boolean isLoading; // 是否正在加载数据
    private ILoadListener mListener;

    private View footer;//底布局

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        //如果最后一个可见item等于总的item，且当前滚动状态为滚动停止，就应该开始加加载数据了
        if (lastVisibleItem == totalItemCount && scrollState == SCROLL_STATE_IDLE) {

            if (!isLoading) {
                isLoading = true;

                //加载数据
                mListener.loadData();
                //设置底布局可见
                footer.findViewById(R.id.load_layout).setVisibility(View.VISIBLE);
            }
        }
    }


    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.lastVisibleItem = firstVisibleItem + visibleItemCount;
        this.totalItemCount = totalItemCount;
        Log.d("luojie",visibleItemCount+"");
        Log.d("luojie",totalItemCount + "");
    }


    //加载数据完成后，需要执行的操作
    public void loadFinish() {

        isLoading = false;//不再加载了
        //底布局也要隐藏
        footer.findViewById(R.id.load_layout).setVisibility(View.GONE);

    }


    //定义一个回调接口，用来获得要加载的数据
    public interface ILoadListener {
        void loadData();
    }

    public void setOnILoadListener(ILoadListener listener) {

        this.mListener = listener;
    }


    /**
     * 自定义ListView的构造方法 在里面做一些必要的一些初始化
     *
     * @param context
     * @param attrs
     */
    public SlideDeleteCancelListView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mInflater = LayoutInflater.from(context);
//用户手指移动的最小距离，用来判断是否响应触发移动事件
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        View view = mInflater.inflate(R.layout.layout_del_cancel_btn, null);
//删除按钮
        mDelBtn = (Button) view.findViewById(R.id.id_item_btn);
//取消按钮
        mCancelBtn = (Button) view.findViewById(R.id.id_item_cancel_btn);
        mPopupWindow = new PopupWindow(view,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
/**
 * 先调用下measure,否则拿不到宽和高
 */
        mPopupWindow.getContentView().measure(0, 0);
        mPopupWindowHeight = mPopupWindow.getContentView().getMeasuredHeight();
        mPopupWindowWidth = mPopupWindow.getContentView().getMeasuredWidth();

        //初始化view
        initView(context);
    }


    //初始化view
    private void initView(Context context) {

        footer = LayoutInflater.from(context).inflate(R.layout.foot_custom_listview, null);

        //注意，这句代码的意思是给自定义的ListView加上底布局
        this.addFooterView(footer);

        //首先需要隐藏这个底部布局
        footer.findViewById(R.id.load_layout).setVisibility(View.GONE);

        this.setOnScrollListener(this);//千万别忘记设定监听器

    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        int action = ev.getAction();
        int x = (int) ev.getX();
        int y = (int) ev.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
//手指按下时水平方向x的位置
                xDown = x;
//手指按下时垂直方向y的位置
                yDown = y;
                /*
                 * 如果当前popupWindow显示，则直接隐藏，然后屏蔽ListView的Touch事件的下传
                 * */
                if (mPopupWindow.isShowing()) {
                    dismissPopWindow();
                    return false;
                }
// 获得当前手指按下时的item的位置
                mCurrentViewPos = pointToPosition(xDown, yDown);
// 获得当前手指按下时的ListView的item项
                mCurrentView = getChildAt(mCurrentViewPos - getFirstVisiblePosition());
                break;
            case MotionEvent.ACTION_MOVE:
//手指移动时x的位置
                xMove = x;
//手指一动时y的位置
                yMove = y;
//水平滑动的距离（可能为负值）
                int dx = xMove - xDown;
//垂直滑动的距离（可能为负值）
                int dy = yMove - yDown;
                /*
                 * 判断是否是从右到左的滑动
                 * */
                if (xMove < xDown && Math.abs(dx) > touchSlop && Math.abs(dy) < touchSlop) {
                    Log.e(TAG, "touchslop = " + touchSlop + " , dx = " + dx + " , dy = " + dy);
                    isSliding = true;
                }
                break;
        }

        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        /*
         * 如果是从右到左的滑动才响应，之前已在 dispatchTouchEvent 中获得了是否是从右向左滑动
         * */
        if (isSliding) {
            switch (action) {
                case MotionEvent.ACTION_MOVE:
                    int[] location = new int[2];
//获得当前item的位置x与y
                    mCurrentView.getLocationOnScreen(location);
//设置PopupWindow的动画
                    mPopupWindow.setAnimationStyle(R.style.popwindow_delete_btn_anim_style);

                    mPopupWindow.update();
                    Log.e(TAG, "width location[0]: " + location[0]);
                    Log.e(TAG, "height location[1]: " + location[1]);
                    Log.e(TAG, "mCurrentView.getWidth(): " + mCurrentView.getWidth());
                    Log.e(TAG, "mCurrentView.getHeight(): " + mCurrentView.getHeight());
                    Log.e(TAG, "mPopupWindowHeight: " + mPopupWindowHeight);
//设置“取消关注”、“删除”按钮PopWindow的显示位置
//相对于父控件的位置（例如正中央Gravity.CENTER，下方Gravity.BOTTOM等），可以设置偏移或无偏移
//相对某个控件的位置，有偏移;xoff表示x轴的偏移，正值表示向左，负值表示向右；yoff表示相对y轴的偏移，正值是向下，负值是向上；
                    mPopupWindow.showAtLocation(mCurrentView,
                            Gravity.LEFT | Gravity.TOP,
                            location[0] + mCurrentView.getWidth(),
                            location[1] + mCurrentView.getHeight() / 2 - mPopupWindowHeight / 2);
//设置删除按钮的回调
                    mDelBtn.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
//通过接口对象调用当前Item上删除按钮的点击方法
                            mDelListener.onDelClick(mCurrentViewPos);
                            mPopupWindow.dismiss();
                        }
                    });
//设置取消按钮的回调
                    mCancelBtn.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
//通过接口对象调用当前Item上取消按钮的点击方法
                            mCancelListener.onCancelClick(mCurrentViewPos);
                            mPopupWindow.dismiss();
                        }
                    });
                    Log.e(TAG, "mPopupWindow.getHeight()=" + mPopupWindowHeight);
                    break;
                case MotionEvent.ACTION_UP:
//设置侧滑关闭
                    isSliding = false;
                    break;
            }
// 相应滑动期间屏幕itemClick事件，避免发生冲突
            return true;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 隐藏popupWindow
     */
    private void dismissPopWindow() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }

    /**
     * 设置删除按钮点击事件监听
     *
     * @param listener DelButtonClickListener 删除按钮监听接口对象
     */
    public void setDelButtonClickListener(DelButtonClickListener listener) {
        mDelListener = listener;
    }

    /**
     * 设置取消按钮点击事件监听
     *
     * @param listener CancelButtonClickListener 按钮点击事件监听接口对象
     */
    public void setCancelButtonClickListener(CancelButtonClickListener listener) {
        mCancelListener = listener;
    }

    /**
     * 删除按钮监听接口
     */
    public interface DelButtonClickListener {
        void onDelClick(int position);
    }

    /**
     * 取消按钮监听接口
     */
    public interface CancelButtonClickListener {
        void onCancelClick(int position);
    }
}

