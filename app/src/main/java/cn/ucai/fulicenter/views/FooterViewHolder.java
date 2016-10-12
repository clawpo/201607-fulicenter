package cn.ucai.fulicenter.views;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import cn.ucai.fulicenter.R;

/**
 * Created by yao on 2016/10/2.
 */

public class FooterViewHolder extends RecyclerView.ViewHolder{
    public TextView tvFooter;
    public FooterViewHolder(View itemView) {
        super(itemView);
        tvFooter = (TextView) itemView.findViewById(R.id.tvFooter);
    }
}
