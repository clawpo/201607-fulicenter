package cn.ucai.fulicenter.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.AlbumsBean;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.GoodsDetailsBean;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.dao.NetDao;
import cn.ucai.fulicenter.utils.L;
import cn.ucai.fulicenter.utils.OkHttpUtils;
import cn.ucai.fulicenter.utils.Utils;
import cn.ucai.fulicenter.views.DisplayUtils;
import cn.ucai.fulicenter.views.FlowIndicator;
import cn.ucai.fulicenter.views.SlideAutoLoopView;


public class GoodsDetailsActivity extends BaseActivity {
    private final static String TAG = GoodsDetailsActivity.class.getSimpleName();
    GoodsDetailsActivity mContext;
    ImageView ivShare;
    ImageView ivCollect;
    ImageView ivCart;
    TextView tvCartCount;

    TextView tvGoodEnglishName;
    TextView tvGoodName;
    TextView tvGoodPriceCurrent;
    TextView tvGoodPriceShop;

    SlideAutoLoopView mSlideAutoLoopView;
    FlowIndicator mFlowIndicator;
    WebView wvGoodBrief;

    int mGoodId;
    GoodsDetailsBean mGoodDetail;

    boolean isCollect;
    updateCartNumReceiver mReceiver;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_good_details);
        mContext = this;
        initView();
        initData();
        setListener();
    }

    private void setListener() {
        MyOnClickListener listener = new MyOnClickListener();
        ivCollect.setOnClickListener(listener);
        ivShare.setOnClickListener(listener);
        ivCart.setOnClickListener(listener);
        setUpdateCartCountListener();
    }

    private void initData() {
        mGoodId = getIntent().getIntExtra(I.GoodsDetails.KEY_GOODS_ID,0);
        Log.e(TAG,"mGoodId="+mGoodId);
        if(mGoodId>0){
            NetDao.downloadGoodsDetails(mContext, mGoodId, new OkHttpUtils.OnCompleteListener<GoodsDetailsBean>() {
                @Override
                public void onSuccess(GoodsDetailsBean result) {
                    if(result!=null){
                        mGoodDetail = result;
                        showGoodDetails();
                    }
                }

                @Override
                public void onError(String error) {
                    finish();
                    Toast.makeText(mContext,"获取商品详情数据失败！", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            finish();
            Toast.makeText(mContext,"获取商品详情数据失败！", Toast.LENGTH_SHORT).show();
        }
    }

    private void showGoodDetails() {
        tvGoodEnglishName.setText(mGoodDetail.getGoodsEnglishName());
        tvGoodName.setText(mGoodDetail.getGoodsName());
        tvGoodPriceCurrent.setText(mGoodDetail.getCurrencyPrice());
        tvGoodPriceShop.setText(mGoodDetail.getShopPrice());
        mSlideAutoLoopView.startPlayLoop(mFlowIndicator,
                getAlbumImageUrl(),getAlbumImageSize());
        wvGoodBrief.loadDataWithBaseURL(null,mGoodDetail.getGoodsBrief(),I.TEXT_HTML,I.UTF_8,null);
    }

    private String[] getAlbumImageUrl() {
        String[] albumImageUrl = new String[]{};
        if(mGoodDetail.getProperties()!=null && mGoodDetail.getProperties().length>0){
            AlbumsBean[] albums = mGoodDetail.getProperties()[0].getAlbums();
            albumImageUrl = new String[albums.length];
            for (int i=0;i<albumImageUrl.length;i++){
                albumImageUrl[i] = albums[i].getImgUrl();
            }
        }
        return albumImageUrl;
    }

    private int getAlbumImageSize() {
        if(mGoodDetail.getProperties()!=null && mGoodDetail.getProperties().length>0){
            return mGoodDetail.getProperties()[0].getAlbums().length;
        }
        return 0;
    }

    private void initView() {
        DisplayUtils.initBack(mContext);
        ivShare = (ImageView) findViewById(R.id.iv_good_share);
        ivCollect = (ImageView) findViewById(R.id.iv_good_collect);
        ivCart = (ImageView) findViewById(R.id.iv_good_cart);
        tvCartCount = (TextView) findViewById(R.id.tv_cart_count);
        tvGoodEnglishName = (TextView) findViewById(R.id.tv_good_name_english);
        tvGoodName = (TextView) findViewById(R.id.tv_good_name);
        tvGoodPriceCurrent = (TextView) findViewById(R.id.tv_good_price_current);
        tvGoodPriceShop = (TextView) findViewById(R.id.tv_good_price_shop);
        mSlideAutoLoopView = (SlideAutoLoopView) findViewById(R.id.salv);
        mFlowIndicator = (FlowIndicator) findViewById(R.id.indicator);
        wvGoodBrief = (WebView) findViewById(R.id.wv_good_brief);
        WebSettings settings = wvGoodBrief.getSettings();
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setBuiltInZoomControls(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initCollecStatus();
        updateCartNum();
    }

    private void updateCartNum() {
        int count = Utils.sumCartCount();
        Log.e(TAG,"count="+count);
        if(!FuLiCenterApplication.getInstance().isLogined() || count ==0){
            tvCartCount.setText(String.valueOf(0));
            tvCartCount.setVisibility(View.GONE);
        }else{
            tvCartCount.setText(String.valueOf(count));
            tvCartCount.setVisibility(View.VISIBLE);
        }
    }

    private void initCollecStatus() {
        if(FuLiCenterApplication.getInstance().isLogined()){
            String userName = FuLiCenterApplication.getInstance().getUserName();
            NetDao.isCollect(mContext, mGoodId, userName, new OkHttpUtils.OnCompleteListener<MessageBean>() {
                @Override
                public void onSuccess(MessageBean result) {
                    if(result!=null && result.isSuccess()){
                        isCollect = true;
                    }else{
                        isCollect = false;
                    }
                    updateCollectStatus();
                }

                @Override
                public void onError(String error) {

                }
            });
        }
    }

    class MyOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.iv_good_collect:
                    goodColect();
                    break;
                case R.id.iv_good_share:
//                    showShare();
                    break;
                case R.id.iv_good_cart:
                    addCart();
                    break;
            }
        }
    }

    private void addCart() {
        Log.e(TAG,"addCart...");
        List<CartBean> cartList = FuLiCenterApplication.getInstance().getCartList();
        CartBean cart = new CartBean();
        boolean isExits = false;
        for (CartBean cartBean:cartList){
            if(cartBean.getGoodsId()==mGoodId){
                cart.setId(cartBean.getId());
                cart.setGoodsId(mGoodId);
                cart.setChecked(cartBean.isChecked());
                cart.setCount(cartBean.getCount()+1);
                cart.setGoods(mGoodDetail);
                cart.setUserName(cartBean.getUserName());
                isExits = true;
            }
        }
        Log.e(TAG,"addCart...isExits="+isExits);
        if(!isExits){
            cart.setGoodsId(mGoodId);
            cart.setChecked(true);
            cart.setCount(1);
            cart.setGoods(mGoodDetail);
            cart.setUserName(FuLiCenterApplication.getInstance().getUserName());
        }
//        new UpdateCartTask(mContext,cart).execute();
    }

    //取消或者添加商品收藏
    private void goodColect() {
        if(FuLiCenterApplication.getInstance().isLogined()){
            if(isCollect){
                //取消收藏
                NetDao.delCollect(mContext, mGoodId,
                        new OkHttpUtils.OnCompleteListener<MessageBean>() {
                            @Override
                            public void onSuccess(MessageBean result) {
                                if(result!=null && result.isSuccess()){
                                    isCollect = false;
//                                    new DownloadCollectCountTask(mContext,FuLiCenterApplication.getInstance().getUserName()).execute();
                                    sendStickyBroadcast(new Intent("update_collect_list"));
                                }else{
                                    Log.e(TAG,"delete fail");
                                }
                                updateCollectStatus();
                                Toast.makeText(mContext,result.getMsg(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(String error) {
                                L.e(TAG,"error="+error);
                            }
                        });
            }else{
                //添加收藏
                NetDao.addCollect(mContext, mGoodDetail, new OkHttpUtils.OnCompleteListener<MessageBean>() {
                    @Override
                    public void onSuccess(MessageBean result) {
                        if(result!=null && result.isSuccess()){
                            isCollect  = true;
//                            new DownloadCollectCountTask(mContext,FuLiCenterApplication.getInstance().getUserName()).execute();
                            sendStickyBroadcast(new Intent("update_collect_list"));
                        }else{
                            Log.e(TAG,"delete fail");
                        }
                        updateCollectStatus();
                        Toast.makeText(mContext,result.getMsg(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String error) {
                        L.e(TAG,"error="+error);
                    }
                });
            }
        }else{
//            startActivity(new Intent(mContext,LoginActivity.class));
        }
    }

    private void updateCollectStatus(){
        if(isCollect){
            ivCollect.setImageResource(R.mipmap.bg_collect_out);
        }else{
            ivCollect.setImageResource(R.mipmap.bg_collect_in);
        }
    }

    class updateCartNumReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            updateCartNum();
        }
    }

    private void setUpdateCartCountListener(){
        mReceiver = new updateCartNumReceiver();
        IntentFilter filter = new IntentFilter("update_cart_list");
        registerReceiver(mReceiver,filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.release();
        if(mReceiver!=null){
            unregisterReceiver(mReceiver);
        }
    }
}
