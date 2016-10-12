package cn.ucai.fulicenter.dao;

import android.content.Context;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.bean.BoutiqueBean;
import cn.ucai.fulicenter.bean.CategoryChildBean;
import cn.ucai.fulicenter.bean.CategoryGroupBean;
import cn.ucai.fulicenter.bean.GoodsDetailsBean;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.bean.NewGoodsBean;
import cn.ucai.fulicenter.utils.OkHttpUtils;

public class NetDao {
    /**
     * 下载新品首页数据
     * @param pageId
     * @param listener
     */
    public static void downloadGoodsList(Context context, int pageId, OkHttpUtils.OnCompleteListener<NewGoodsBean[]> listener) {
        OkHttpUtils<NewGoodsBean[]> utils=new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_FIND_NEW_BOUTIQUE_GOODS)
                .addParam(I.NewAndBoutiqueGoods.CAT_ID,I.CAT_ID+"")
                .addParam(I.PAGE_ID,pageId+"")
                .addParam(I.PAGE_SIZE,I.PAGE_SIZE_DEFAULT+"")
                .targetClass(NewGoodsBean[].class)
                .execute(listener);
    }

    /**
     * 下载商品详情信息
     * @param goodsId
     * @param listener
     */
    public static void downloadGoodsDetails(Context context, int goodsId, OkHttpUtils.OnCompleteListener<GoodsDetailsBean> listener) {
        OkHttpUtils<GoodsDetailsBean> utils=new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_FIND_GOOD_DETAILS)
                .addParam(I.GoodsDetails.KEY_GOODS_ID,goodsId+"")
                .targetClass(GoodsDetailsBean.class)
                .execute(listener);
    }

    /**
     * 下载精选首页数据
     * @param listener
     */
    public static void downloadBoutique(Context context, OkHttpUtils.OnCompleteListener<BoutiqueBean[]> listener) {
        OkHttpUtils<BoutiqueBean[]> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_FIND_BOUTIQUES)
                .targetClass(BoutiqueBean[].class)
                .execute(listener);
    }

    /**
     * 下载精选二级页面的数据
     * @param catId
     * @param pageId
     * @param listener
     */
    public static void downloadBoutiqueChildList(Context context, int catId, int pageId, OkHttpUtils.OnCompleteListener<NewGoodsBean[]> listener) {
        OkHttpUtils<NewGoodsBean[]> utils=new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_FIND_NEW_BOUTIQUE_GOODS)
                .addParam(I.NewAndBoutiqueGoods.CAT_ID,catId+"")
                .addParam(I.PAGE_ID,pageId+"")
                .addParam(I.PAGE_SIZE,I.PAGE_SIZE_DEFAULT+"")
                .targetClass(NewGoodsBean[].class)
                .execute(listener);

    }

    /**下载分类商品的大类信息
     */
    public static void downloadCategoryGroupList(Context context, OkHttpUtils.OnCompleteListener<CategoryGroupBean[]> listener) {
        OkHttpUtils<CategoryGroupBean[]> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_FIND_CATEGORY_GROUP)
                .targetClass(CategoryGroupBean[].class)
                .execute(listener);
    }

    /**
     * 下载分类中小类商品信息
     * @param groupId
     * @param listener
     */
    public static void downloadCategoryChildList(Context context, int groupId,OkHttpUtils.OnCompleteListener<CategoryChildBean[]> listener) {
        OkHttpUtils<CategoryChildBean[]> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_FIND_CATEGORY_CHILDREN)
                .addParam(I.CategoryChild.PARENT_ID, String.valueOf(groupId))
                .addParam(I.PAGE_ID, String.valueOf(I.PAGE_ID_DEFAULT))
                .addParam(I.PAGE_SIZE, String.valueOf(I.PAGE_SIZE_DEFAULT))
                .targetClass(CategoryChildBean[].class)
                .execute(listener);
    }

    /**
     * 从服务端下载分类列表小列表中的过滤标签中的一组商品信息
     * @param catId
     * @param pageId
     * @param listener
     */
    public static void downloadCatFilterGoodsList(Context context, int catId, int pageId, OkHttpUtils.OnCompleteListener<NewGoodsBean[]> listener) {
        OkHttpUtils<NewGoodsBean[]> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_FIND_GOODS_DETAILS)
                .addParam(I.NewAndBoutiqueGoods.CAT_ID, String.valueOf(catId))
                .addParam(I.PAGE_ID, String.valueOf(pageId))
                .addParam(I.PAGE_SIZE, String.valueOf(I.PAGE_SIZE_DEFAULT))
                .targetClass(NewGoodsBean[].class)
                .execute(listener);
    }

    /**
     * 从服务端下载分类列表小列表中的过滤标签中的一组商品信息
     * @param goodId
     * @param username
     * @param listener
     */
    public static void isCollect(Context context, int goodId, String username, OkHttpUtils.OnCompleteListener<MessageBean> listener) {
        OkHttpUtils<MessageBean> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_IS_COLLECT)
                .addParam(I.Collect.USER_NAME,username)
                .addParam(I.Collect.GOODS_ID, String.valueOf(goodId))
                .targetClass(MessageBean.class)
                .execute(listener);
    }

    /**
     * 从服务端下载分类列表小列表中的过滤标签中的一组商品信息
     * @param goodId
     * @param listener
     */
    public static void delCollect(Context context, int goodId, OkHttpUtils.OnCompleteListener<MessageBean> listener) {
        OkHttpUtils<MessageBean> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_DELETE_COLLECT)
                .addParam(I.Collect.USER_NAME,FuLiCenterApplication.getInstance().getUserName())
                .addParam(I.Collect.GOODS_ID, String.valueOf(goodId))
                .targetClass(MessageBean.class)
                .execute(listener);
    }


    /**
     * 从服务端下载分类列表小列表中的过滤标签中的一组商品信息
     * @param goods
     * @param listener
     */
    public static void addCollect(Context context, GoodsDetailsBean goods, OkHttpUtils.OnCompleteListener<MessageBean> listener) {
        OkHttpUtils<MessageBean> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_ADD_COLLECT)
                .addParam(I.Collect.USER_NAME, FuLiCenterApplication.getInstance().getUserName())
                .addParam(I.Collect.GOODS_ID, String.valueOf(goods.getGoodsId()))
                .addParam(I.Collect.ADD_TIME, String.valueOf(goods.getAddTime()))
                .addParam(I.Collect.GOODS_ENGLISH_NAME,goods.getGoodsEnglishName())
                .addParam(I.Collect.GOODS_IMG,goods.getGoodsImg())
                .addParam(I.Collect.GOODS_THUMB,goods.getGoodsThumb())
                .addParam(I.Collect.GOODS_NAME,goods.getGoodsName())
                .targetClass(MessageBean.class)
                .execute(listener);
    }
}
