package cn.ucai.fulicenter;

import android.app.Application;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import cn.ucai.fulicenter.bean.CartBean;


public class FuLiCenterApplication extends Application {
    public static Context applicationContext;
    private static FuLiCenterApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = this;
        instance = this;
    }

    public static FuLiCenterApplication getInstance(){
        return instance;
    }

//    /**全局的当前登录用户信息*/
//    private UserAvatar user;
//    /**全局的当前登录用户的好友集合*/
//    private List<UserAvatar> userList = new ArrayList<UserAvatar>();
//    /**全局的当前登录用户的好友MAP集合*/
//    private Map<String,UserAvatar> userMap = new HashMap<String, UserAvatar>();
    /**全局的当前登录用户的收藏商品的数量*/
    private int collectCount;
    /**全局的当前登录用户的购物车集合*/
    private List<CartBean> cartList = new ArrayList<CartBean>();

//    public UserAvatar getUser() {
//        return user;
//    }
//
//    public void setUser(UserAvatar user) {
//        this.user = user;
//    }
//
//    public List<UserAvatar> getUserList() {
//        return userList;
//    }
//
//    public void setUserList(List<UserAvatar> userList) {
//        this.userList = userList;
//    }
//
//    public Map<String, UserAvatar> getUserMap() {
//        return userMap;
//    }
//
//    public void setUserMap(Map<String, UserAvatar> userMap) {
//        this.userMap = userMap;
//    }

    public int getCollectCount() {
        return collectCount;
    }

    public void setCollectCount(int collectCount) {
        this.collectCount = collectCount;
    }

    public List<CartBean> getCartList() {
        return cartList;
    }

    public void setCartList(List<CartBean> cartList) {
        this.cartList = cartList;
    }

    boolean isLogined;
    public boolean isLogined() {
        return isLogined;
    }

    String username;
    public String getUserName() {
        return username;
    }
}
