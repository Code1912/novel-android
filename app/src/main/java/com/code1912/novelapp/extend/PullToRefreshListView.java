package com.code1912.novelapp.extend;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.code1912.novelapp.R;

/**
 * Created by Code1912 on 2016/12/1.
 */

public class PullToRefreshListView extends ListView implements AbsListView.OnScrollListener {
    //底部View
    private View footerView;
    //ListView item个数
    int totalItemCount = 0;
    //最后可见的Item
    int lastVisibleItem = 0;
    //是否加载标示
    boolean isLoading = false;

    public PullToRefreshListView(Context context) {
        super(context);
        initView(context);
    }

    public PullToRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);

    }

    public PullToRefreshListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    /**
     * 初始化ListView
     */
    private void initView(Context context){
        LayoutInflater mInflater = LayoutInflater.from(context);
        footerView = mInflater.inflate(R.layout.listview_footer, null);
        footerView.setVisibility(View.GONE);
        this.setOnScrollListener(PullToRefreshListView.this);
        //添加底部View
        this.addFooterView(footerView);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        //当滑动到底端，并滑动状态为 not scrolling
        if(totalItemCount<=1){
            return;
        }
        if(lastVisibleItem == totalItemCount && scrollState == SCROLL_STATE_IDLE){
            if(!isLoading){
                isLoading = true;
                //设置可见
                footerView.setVisibility(View.VISIBLE);
                //加载数据
                onLoadListener.onLoad();
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        this.lastVisibleItem = firstVisibleItem + visibleItemCount;
        this.totalItemCount = totalItemCount;
    }

    private OnLoadListener onLoadListener;
    public void setOnLoadListener(OnLoadListener onLoadListener){
        this.onLoadListener = onLoadListener;
    }

    /**
     * 加载数据接口
     * @author Administrator
     *
     */
    public interface OnLoadListener{
        void onLoad();
    }

    /**
     * 数据加载完成
     */
    public void loadComplete(){
        footerView.setVisibility(View.GONE);
        isLoading = false;
        this.invalidate();
    }
}
