package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.GoodsDetailsActivity;
import cn.ucai.fulicenter.bean.NewGoodsBean;
import cn.ucai.fulicenter.utils.ImageLoader;
import cn.ucai.fulicenter.views.FooterViewHolder;

public class GoodsAdapter extends RecyclerView.Adapter {
    Context mContext;
    ArrayList<NewGoodsBean> mGoodsList;
    boolean misMore;
    String mFooterText;
    View.OnClickListener mOnItemClickListener;

    int mScrollState;
    int sortBy;
    public GoodsAdapter(Context context, ArrayList<NewGoodsBean> goodsList) {
        this.mContext = context;
        this.mGoodsList= new ArrayList<>();
        this.mGoodsList.addAll(goodsList);
        //列表项单击事件监听对象只需创建一个
        mOnItemClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int goodsId= (int) v.getTag();
                mContext.startActivity(new Intent(mContext, GoodsDetailsActivity.class)
                .putExtra(I.GoodsDetails.KEY_GOODS_ID,goodsId));
            }
        };

        sortBy = I.SORT_BY_ADDTIME_DESC;
        soryBy();
    }

    public void setScrollState(int scrollState) {
        mScrollState=scrollState;
        notifyDataSetChanged();
    }

    public boolean isMore() {
        return misMore;
    }

    public void setMore(boolean isMore) {
        this.misMore = isMore;
    }

    public String getFooterText() {
        return mFooterText;
    }

    public void setFooterText(String footerText) {
        this.mFooterText = footerText;
        notifyDataSetChanged();
    }

    public void initData(ArrayList<NewGoodsBean> goodsList) {
        this.mGoodsList.clear();
        mGoodsList.addAll(goodsList);
        notifyDataSetChanged();
    }

    public void addData(ArrayList<NewGoodsBean> goodsList) {
        mGoodsList.addAll(goodsList);
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder=null;
        switch (viewType) {
            case I.TYPE_FOOTER:
                holder = new FooterViewHolder(View.inflate(mContext, R.layout.item_footer, null));
                break;
            case I.TYPE_ITEM:
                holder = new GoodsViewHolder(View.inflate(mContext, R.layout.item_goods, null));
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder parentHolder, int position) {
        if (getItemViewType(position) == I.TYPE_FOOTER) {
            FooterViewHolder footerViewHolder= (FooterViewHolder) parentHolder;
            footerViewHolder.tvFooter.setText(getFooterText());
            return;
        }
        NewGoodsBean goods = mGoodsList.get(position);
        GoodsViewHolder holder= (GoodsViewHolder) parentHolder;
        holder.tvGoodsPrice.setText(goods.getCurrencyPrice());
        holder.tvGoodsName.setText(goods.getGoodsName());

        ImageLoader.downloadImg(mContext,holder.ivGoodsThumb,
                goods.getGoodsThumb(),mScrollState!=RecyclerView.SCROLL_STATE_DRAGGING);
        //给单击事件监听对象传递数据
        holder.layoutItem.setTag(goods.getGoodsId());
    }

    @Override
    public int getItemCount() {
        return mGoodsList==null?1:mGoodsList.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return I.TYPE_FOOTER;
        }
        return I.TYPE_ITEM;
    }

    class GoodsViewHolder extends RecyclerView.ViewHolder {
        View layoutItem;
        ImageView ivGoodsThumb;
        TextView tvGoodsName;
        TextView tvGoodsPrice;

        public GoodsViewHolder(View itemView) {
            super(itemView);
            layoutItem = itemView.findViewById(R.id.layout_goods);
            ivGoodsThumb = (ImageView) itemView.findViewById(R.id.ivGoodsThumb);
            tvGoodsName = (TextView) itemView.findViewById(R.id.tvGoodsName);
            tvGoodsPrice = (TextView) itemView.findViewById(R.id.tvGoodsPrice);
            //设置列表项单击事件监听
            layoutItem.setOnClickListener(mOnItemClickListener);
        }
    }

    public void setSortBy(int sortBy) {
        this.sortBy = sortBy;
        soryBy();
        notifyDataSetChanged();
    }
    private void soryBy(){
        Collections.sort(mGoodsList, new Comparator<NewGoodsBean>() {
            @Override
            public int compare(NewGoodsBean goodLeft, NewGoodsBean goodRight) {
                int result=0;
                switch (sortBy){
                    case I.SORT_BY_ADDTIME_DESC:
                        result = (int)(Long.valueOf(goodRight.getAddTime())-Long.valueOf(goodLeft.getAddTime()));
                        break;
                    case I.SORT_BY_ADDTIME_ASC:
                        result = (int)(Long.valueOf(goodLeft.getAddTime())-Long.valueOf(goodRight.getAddTime()));
                        break;
                    case I.SORT_BY_PRICE_DESC:
                        result = convertPrice(goodRight.getCurrencyPrice())-convertPrice(goodLeft.getCurrencyPrice());
                        break;
                    case I.SORT_BY_PRICE_ASC:
                        result = convertPrice(goodLeft.getCurrencyPrice())-convertPrice(goodRight.getCurrencyPrice());
                        break;
                }
                return result;
            }
            //price = ￥140
            private int convertPrice(String price){
                price = price.substring(price.indexOf("￥")+1);
                return Integer.valueOf(price);
            }
        });
    }
}
