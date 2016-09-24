package com.vise.bluetoothchat.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;

import com.vise.common_base.activity.BaseActivity;
import com.vise.common_base.manager.AppManager;

/**
 * @Description:
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 16/9/24 16:27.
 */
public abstract class BaseChatActivity extends BaseActivity implements AppCompatCallback {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
    }

    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        AppCompatDelegate.create(this, this).setSupportActionBar(toolbar);
    }

    @Override
    public void onSupportActionModeStarted(ActionMode mode) {
    }

    @Override
    public void onSupportActionModeFinished(ActionMode mode) {
    }

    @Nullable
    @Override
    public ActionMode onWindowStartingSupportActionMode(ActionMode.Callback callback) {
        return null;
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        initWidget();
        initData();
        initEvent();
    }

    protected abstract void initWidget();
    protected abstract void initData();
    protected abstract void initEvent();
}
