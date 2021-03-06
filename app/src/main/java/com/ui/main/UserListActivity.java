package com.ui.main;

/**
 * Created by baixiaokang on 16/12/1.
 */

import android.support.v7.app.ActionBar;
import android.text.TextUtils;

import com.C;
import com.EventTags;
import com.app.annotation.apt.Router;
import com.app.annotation.javassist.Bus;
import com.apt.ApiFactory;
import com.base.BaseActivity;
import com.base.event.OkBus;
import com.data.entity._User;
import com.view.widget.TabLayout;

import java.util.List;

import butterknife.Bind;
import rx.Observable;


/**
 * 简单页面无需mvp,该咋写还是咋写
 */
@Router(C.USER_LIST)
public class UserListActivity extends BaseActivity {

    @Bind(R.id.tl_user)
    TabLayout tlUser;

    @Override
    public int getLayoutId() {
        return R.layout.activity_users;
    }

    @Override
    public void initView() {
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("用户一览");
        ApiFactory.getAllUser(0, 1000)
                .flatMap(userData -> Observable.from(userData.results))
                .filter(user -> !TextUtils.isEmpty(user.face))
                .buffer(1000)
                .subscribe(
                        //使用OkBus替换Rxjava的线程切换
                        users -> OkBus.getInstance().onEvent(EventTags.ABOUT_INIT_USERS, users)
                        , e -> e.printStackTrace());
    }

    @Bus(value = EventTags.ABOUT_INIT_USERS, thread = Bus.UI)
    private void setUsers(List<_User> users) {
        tlUser.setM_Users(users);
    }
}