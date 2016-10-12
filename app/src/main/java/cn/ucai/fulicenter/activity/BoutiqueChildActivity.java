package cn.ucai.fulicenter.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.adapter.GoodsAdapter;
import cn.ucai.fulicenter.bean.NewGoodsBean;
import cn.ucai.fulicenter.dao.NetDao;
import cn.ucai.fulicenter.utils.OkHttpUtils;
import cn.ucai.fulicenter.utils.Utils;
import cn.ucai.fulicenter.views.DisplayUtils;
import cn.ucai.fulicenter.views.SpaceItemDecoration;

public class BoutiqueChildActivity extends BaseActivity {
    private final static String TAG = BoutiqueChildActivity.class.getSimpleName();
    BoutiqueChildActivity mContext;
    ArrayList<NewGoodsBean> mGoodList;

    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    GridLayoutManager mGridLayoutManager;
    GoodsAdapter mAdapter;
    TextView tvHint;

    int pageId = 1;
    int action = I.ACTION_DOWNLOAD;

    int catId = 0;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        mContext = this;
        setContentView(R.layout.activity_boutique_child);
        mGoodList = new ArrayList<NewGoodsBean>();
        initView();
        initData();
        setListener();
    }

    private void setListener() {
        setPullDownRefreshListener();
        setPullUpRefreshListener();
    }

    private void setPullDownRefreshListener() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                action = I.ACTION_PULL_DOWN;
                pageId = 0;
                mSwipeRefreshLayout.setRefreshing(true);
                tvHint.setVisibility(View.VISIBLE);
                initData();
            }
        });
    }

    private void setPullUpRefreshListener() {
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastItemPosition;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int a = RecyclerView.SCROLL_STATE_DRAGGING;//1
                int b = RecyclerView.SCROLL_STATE_IDLE;//0
                int c = RecyclerView.SCROLL_STATE_SETTLING;//2
                Log.e(TAG,"newState="+newState);
                if(newState==RecyclerView.SCROLL_STATE_IDLE
                        && lastItemPosition==mAdapter.getItemCount()-1){
                    if(mAdapter.isMore()) {
                        action = I.ACTION_PULL_UP;
                        pageId += I.PAGE_SIZE_DEFAULT;
                        initData();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int f = mGridLayoutManager.findFirstVisibleItemPosition();
                int l = mGridLayoutManager.findLastVisibleItemPosition();
                Log.e(TAG,"f="+f+",l="+l);
                lastItemPosition = mGridLayoutManager.findLastVisibleItemPosition();
                mSwipeRefreshLayout.setEnabled(mGridLayoutManager.findFirstVisibleItemPosition()==0);
                if(f==-1 || l ==-1){
                    lastItemPosition = mAdapter.getItemCount()-1;
                }
            }
        });
    }

    private void initData() {
        catId = getIntent().getIntExtra(I.Boutique.KEY_GOODS_ID,0);
        Log.e(TAG,"catId="+catId);
        if(catId<0)finish();
        NetDao.downloadBoutiqueChildList(mContext, catId, pageId, new OkHttpUtils.OnCompleteListener<NewGoodsBean[]>() {
            @Override
            public void onSuccess(NewGoodsBean[] result) {
                mSwipeRefreshLayout.setRefreshing(false);
                tvHint.setVisibility(View.GONE);
                mAdapter.setMore(true);
                mAdapter.setFooterText(getResources().getString(R.string.load_more));
                Log.e(TAG,"result="+result);
                if(result!=null){
                    Log.e(TAG,"result.length="+result.length);
                    ArrayList<NewGoodsBean> goodBeanArrayList = Utils.array2List(result);
                    if(action==I.ACTION_DOWNLOAD || action==I.ACTION_PULL_DOWN) {
                        mAdapter.initData(goodBeanArrayList);
                    }else{
                        mAdapter.addData(goodBeanArrayList);
                    }
                    if(goodBeanArrayList.size()<I.PAGE_SIZE_DEFAULT){
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
                mSwipeRefreshLayout.setRefreshing(false);
                tvHint.setVisibility(View.GONE);
                Toast.makeText(mContext,error,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        String name = getIntent().getStringExtra(I.Boutique.NAME);
        DisplayUtils.initBackWithTitle(mContext,name);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_boutique_child);
        mSwipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(R.color.google_blue),
                getResources().getColor(R.color.google_green),
                getResources().getColor(R.color.google_red),
                getResources().getColor(R.color.google_yellow)
        );
        mGridLayoutManager = new GridLayoutManager(mContext, I.COLUM_NUM);
        mGridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_boutique_child);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(10));
        mAdapter = new GoodsAdapter(mContext,mGoodList);
        mRecyclerView.setAdapter(mAdapter);
        tvHint = (TextView) findViewById(R.id.tv_refresh_hint);
    }

}
