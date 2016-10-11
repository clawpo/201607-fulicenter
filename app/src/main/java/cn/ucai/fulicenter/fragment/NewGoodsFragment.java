package cn.ucai.fulicenter.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.MainActivity;
import cn.ucai.fulicenter.adapter.GoodsAdapter;
import cn.ucai.fulicenter.bean.NewGoodsBean;
import cn.ucai.fulicenter.dao.NetDao;
import cn.ucai.fulicenter.utils.ConvertUtils;
import cn.ucai.fulicenter.utils.ImageLoader;
import cn.ucai.fulicenter.utils.OkHttpUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewGoodsFragment extends Fragment {
    static final int ACTION_DOWNLOAD=0;
    static final int ACTION_PULL_DOWN=1;
    static final int ACTION__PULL_UP=2;
    MainActivity mContext;
    RecyclerView mrvNewGoods;
    ArrayList<NewGoodsBean> mGoodsList;
    GoodsAdapter mAdapter;

    SwipeRefreshLayout mSwipeRefreshLayout;
    TextView mtvRefreshHint;

    NetDao mNetDao;

    int mPageId;

    GridLayoutManager mLayoutManager;

    public NewGoodsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("main", "NewGoodsFragment.onCreateView()");
        mContext = (MainActivity) getContext();
        mNetDao = new NetDao();
        View layout = inflater.inflate(R.layout.fragment_new_goods, container, false);
        initView(layout);
        setListener();
        mPageId = 1;
        downloadNewGoods(mPageId, ACTION_DOWNLOAD);
        return layout;
    }

    private void setListener() {
        setPullDownListener();
        setPullUpListener();
    }

    private void setPullUpListener() {
        mrvNewGoods.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int lastPosition=mLayoutManager.findLastVisibleItemPosition();
                mAdapter.setScrollState(newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastPosition >= mAdapter.getItemCount()
                        && mAdapter.isMore()) {
                    mPageId++;
                    downloadNewGoods(mPageId,ACTION__PULL_UP);
                }
            }
        });
    }

    private void setPullDownListener() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                mtvRefreshHint.setVisibility(View.VISIBLE);
                mPageId=1;
                downloadNewGoods(mPageId,ACTION_PULL_DOWN);
            }
        });
    }

    private void downloadNewGoods(int pageId, final int action) {
        mNetDao.downloadGoodsList(mContext,pageId, new OkHttpUtils.OnCompleteListener<NewGoodsBean[]>() {
            @Override
            public void onSuccess(NewGoodsBean[] result) {
                ArrayList<NewGoodsBean> goodsList = ConvertUtils.array2List(result);
                if (goodsList == null) {
                    mAdapter.setMore(false);
                    if (action == ACTION__PULL_UP) {
                        Log.i("main", "没有加载到数据");
                        mAdapter.setFooterText("没有更多数据...");
                    }
                    return;
                }
                switch (action) {
                    case ACTION_DOWNLOAD:
                        mAdapter.initData(goodsList);
                        break;
                    case ACTION_PULL_DOWN:
                        mAdapter.initData(goodsList);
                        mtvRefreshHint.setVisibility(View.GONE);
                        mSwipeRefreshLayout.setRefreshing(false);
                        break;
                    case ACTION__PULL_UP:
                        mAdapter.addData(goodsList);
                        mAdapter.setFooterText("加载更多数据...");
                        break;
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView(View layout) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.srl);
        mtvRefreshHint = (TextView) layout.findViewById(R.id.tv_refresh_hint);

        mrvNewGoods = (RecyclerView) layout.findViewById(R.id.rvNewGoods);
        mLayoutManager = new GridLayoutManager(getContext(), 2);
        mrvNewGoods.setLayoutManager(mLayoutManager);

        mGoodsList = new ArrayList<>();
        mAdapter = new GoodsAdapter(getContext(), mGoodsList);
        mrvNewGoods.setAdapter(mAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OkHttpUtils.release();
        ImageLoader.release();
    }
}
