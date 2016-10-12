package cn.ucai.fulicenter.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.List;

import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.MainActivity;
import cn.ucai.fulicenter.adapter.CategoryAdapter;
import cn.ucai.fulicenter.bean.CategoryChildBean;
import cn.ucai.fulicenter.bean.CategoryGroupBean;
import cn.ucai.fulicenter.dao.NetDao;
import cn.ucai.fulicenter.utils.OkHttpUtils;
import cn.ucai.fulicenter.utils.Utils;

public class CategoryFragment extends Fragment {
    private final static String TAG = CategoryFragment.class.getSimpleName();
    MainActivity mContext;
    ExpandableListView mExpandableListView;
    List<CategoryGroupBean> mGroupList;
    List<ArrayList<CategoryChildBean>> mChildList;
    CategoryAdapter mAdapter;
    int groupCount;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = (MainActivity) getContext();
        View layout = View.inflate(mContext, R.layout.fragment_category,null);
        mGroupList = new ArrayList<CategoryGroupBean>();
        mChildList = new ArrayList<ArrayList<CategoryChildBean>>();
        mAdapter = new CategoryAdapter(mContext,mGroupList,mChildList);
        initView(layout);
        initData();
        return layout;
    }

    private void initData() {
        NetDao.downloadCategoryGroupList(mContext, new OkHttpUtils.OnCompleteListener<CategoryGroupBean[]>() {
            @Override
            public void onSuccess(CategoryGroupBean[] result) {
                if(result!=null){
                    ArrayList<CategoryGroupBean> groupList = Utils.array2List(result);
                    if(groupList!=null){
                        Log.e(TAG,"groupList="+groupList.size());
                        mGroupList = groupList;
                        int i=0;
                        for (CategoryGroupBean g:groupList){
                            mChildList.add(new ArrayList<CategoryChildBean>());
                            findCategoryChildList(g.getId(),i);
                            i++;
                        }
                    }
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    private void findCategoryChildList(int parentId,final int index){
        NetDao.downloadCategoryChildList(mContext, parentId, new OkHttpUtils.OnCompleteListener<CategoryChildBean[]>() {
            @Override
            public void onSuccess(CategoryChildBean[] result) {
                groupCount++;
                if(result!=null){
                    ArrayList<CategoryChildBean> childList = Utils.array2List(result);
                    if(childList!=null){
                        mChildList.set(index,childList);
                    }
                }
                if(groupCount==mGroupList.size()){
                    mAdapter.addAll(mGroupList,mChildList);
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    private void initView(View layout) {
        mExpandableListView = (ExpandableListView) layout.findViewById(R.id.elvCategory);
        mExpandableListView.setGroupIndicator(null);
        mExpandableListView.setAdapter(mAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OkHttpUtils.release();
    }
}
