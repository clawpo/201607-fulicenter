package cn.ucai.fulicenter.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.MainActivity;
import cn.ucai.fulicenter.adapter.GoodsAdapter;
import cn.ucai.fulicenter.bean.NewGoodsBean;
import cn.ucai.fulicenter.dao.NetDao;
import cn.ucai.fulicenter.utils.ConvertUtils;
import cn.ucai.fulicenter.utils.ImageLoader;
import cn.ucai.fulicenter.utils.L;
import cn.ucai.fulicenter.utils.OkHttpUtils;
import cn.ucai.fulicenter.views.SpaceItemDecoration;

import static cn.ucai.fulicenter.I.ACTION_PULL_UP;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewGoodsFragment extends Fragment {
    MainActivity mContext;
    ArrayList<NewGoodsBean> mGoodsList;
    GoodsAdapter mAdapter;

    NetDao mNetDao;

    int mPageId;

    GridLayoutManager mLayoutManager;
    @BindView(R.id.tv_refresh_hint)
    TextView mtvRefreshHint;
    @BindView(R.id.rvNewGoods)
    RecyclerView mrvNewGoods;
    @BindView(R.id.srl)
    SwipeRefreshLayout mSwipeRefreshLayout;

    public NewGoodsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("main", "NewGoodsFragment.onCreateView()");
        View layout = inflater.inflate(R.layout.fragment_new_goods, container, false);
        ButterKnife.bind(this, layout);
        mContext = (MainActivity) getContext();
        mNetDao = new NetDao();
        mGoodsList = new ArrayList<>();
        mAdapter = new GoodsAdapter(getContext(), mGoodsList);
        initView();
        setListener();
        mPageId = 1;
        downloadNewGoods(mPageId, I.ACTION_DOWNLOAD);
        return layout;
    }

    private void setListener() {
        setPullDownListener();
        setPullUpListener();
    }

    private void setPullDownListener() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                mtvRefreshHint.setVisibility(View.VISIBLE);
                mPageId = 1;
                downloadNewGoods(mPageId, I.ACTION_PULL_DOWN);
            }
        });
    }

    private void setPullUpListener() {
        mrvNewGoods.setOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastPosition;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                lastPosition = mLayoutManager.findLastVisibleItemPosition();
                mAdapter.setScrollState(newState);
                L.e("lastPosition="+lastPosition+",count="+mAdapter.getItemCount()+",isMore="+mAdapter.isMore());
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastPosition >= mAdapter.getItemCount()-1
                        && mAdapter.isMore()) {
                    mPageId++;
                    downloadNewGoods(mPageId, ACTION_PULL_UP);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int f = mLayoutManager.findFirstVisibleItemPosition();
                int l = mLayoutManager.findLastVisibleItemPosition();
                lastPosition = mLayoutManager.findLastVisibleItemPosition();
                mSwipeRefreshLayout.setEnabled(mLayoutManager.findFirstVisibleItemPosition()==0);
                if(f==-1 || l ==-1){
                    lastPosition = mAdapter.getItemCount()-1;
                }
            }
        });
    }

    private void downloadNewGoods(int pageId, final int action) {
        mNetDao.downloadGoodsList(mContext, pageId, new OkHttpUtils.OnCompleteListener<NewGoodsBean[]>() {
            @Override
            public void onSuccess(NewGoodsBean[] result) {
                mSwipeRefreshLayout.setRefreshing(false);
                mtvRefreshHint.setVisibility(View.GONE);
                mAdapter.setMore(true);
                mAdapter.setFooterText(getResources().getString(R.string.load_more));
                if(result!=null){
                    ArrayList<NewGoodsBean> goodsList = ConvertUtils.array2List(result);
                    if(action==I.ACTION_DOWNLOAD || action==I.ACTION_PULL_DOWN) {
                        mAdapter.initData(goodsList);
                    }else{
                        mAdapter.addData(goodsList);
                    }
                    if(goodsList.size()<I.PAGE_SIZE_DEFAULT){
                        mAdapter.setMore(false);
                        mAdapter.setFooterText(getResources().getString(R.string.no_more));
                    }
                }else{
                    mAdapter.setMore(false);
                    mAdapter.setFooterText(getResources().getString(R.string.no_more));
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        mSwipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(R.color.google_blue),
                getResources().getColor(R.color.google_green),
                getResources().getColor(R.color.google_red),
                getResources().getColor(R.color.google_yellow)
        );

        mLayoutManager = new GridLayoutManager(getContext(), I.COLUM_NUM);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mrvNewGoods.setLayoutManager(mLayoutManager);
        mrvNewGoods.setHasFixedSize(true);
        mrvNewGoods.setAdapter(mAdapter);
        mrvNewGoods.addItemDecoration(new SpaceItemDecoration(10));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OkHttpUtils.release();
        ImageLoader.release();
    }
}
