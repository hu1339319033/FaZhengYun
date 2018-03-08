package com.example.fazhengyun.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import com.example.fazhengyun.R;
import com.example.fazhengyun.databinding.ActivityUserinfoBinding;
import com.example.fazhengyun.kit.AppKit;
import com.example.fazhengyun.kit.SharedPreferenceUtil;
import com.example.fazhengyun.model.Constants;
import com.example.fazhengyun.model.Event;
import com.example.fazhengyun.ui.view.EmbellishDialog;
import com.example.fazhengyun.xdroid.base.XActivity;
import com.example.fazhengyun.xdroid.event.BusFactory;

import cn.droidlover.xdroidbase.router.Router;

/**
 * Created by fscm-qt on 2017/12/7.
 */

public class UserInfoActivity extends XActivity<ActivityUserinfoBinding> implements View.OnClickListener {

    EmbellishDialog dialog;
    @Override
    public void initData(Bundle savedInstanceState) {
        getBinding().toolbar.toolbarTitle.setText("个人资料");
        setSupportActionBar(getBinding().toolbar.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        getBinding().tvName.setText(SharedPreferenceUtil.getInstance(context).getString(Constants.USERNAEM,"点击设置"));
        getBinding().tvCardState.setText(SharedPreferenceUtil.getInstance(context).getInt(Constants.REALNAME,0)==1?"已认证":"未认证");
    }

    @Override
    public void setListener() {
        super.setListener();
        getBinding().tvCard.setOnClickListener(this);
        getBinding().tvChangepassword.setOnClickListener(this);
        getBinding().tvChangephone.setOnClickListener(this);
        getBinding().linName.setOnClickListener(this);
        getBinding().btnExit.setOnClickListener(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_userinfo;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_card:
                if(SharedPreferenceUtil.getInstance(context).getInt(Constants.REALNAME,0)==0){
                    Router.newIntent(context).to(CardActivity.class).launch();
                }else{
                    getUiDelegate().toastShort("您已经实名认证了");
                }
                break;
            case R.id.tv_changephone:
                Router.newIntent(context).to(ChangePhoneActivity.class).launch();
                break;
            case R.id.lin_name:
                Router.newIntent(context).to(ChangeUserNameActivity.class).launch();
                break;
            case R.id.tv_changepassword:
                dialog = new EmbellishDialog(context, 5, null, new EmbellishDialog.ConfirmButtonOnClickListener() {
                    @Override
                    public void confirmButtonOnClick() {
                        if (null==dialog.getdialog5_ed_text()||dialog.getdialog5_ed_text().equals("")){
                            getUiDelegate().toastShort("请输入原密码");
                            return;
                        }
                        if(!AppKit.MD5Util.encode(dialog.getdialog5_ed_text()).equals(SharedPreferenceUtil.getInstance(context).getString(Constants.PASSWORD,"-1"))){
                            getUiDelegate().toastShort("原密码输入错误");
                            return;
                        }
                        dialog.dismiss();
                        Router.newIntent(context).to(ChangePasswordActivity.class).launch();
                    }
                });
                dialog.setTitle("验证原密码");
                dialog.setEditTextHint("请输入原密码");
                dialog.setNegativeText("取消");
                dialog.setPositiveText("确认");
                dialog.setConfirmdimmiss(false);
                dialog.showDialog();
                break;
            case R.id.btn_exit:
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("确定退出吗？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferenceUtil.getInstance(context).putBoolean(Constants.LOGIN_STATE,false);
                        SharedPreferenceUtil.getInstance(context).putString(Constants.USERNAEM,null);
                        SharedPreferenceUtil.getInstance(context).putString(Constants.PASSWORD,null);
                        SharedPreferenceUtil.getInstance(context).putString(Constants.PHONE,null);
                        SharedPreferenceUtil.getInstance(context).putInt(Constants.USERID,-1);
                        BusFactory.getBus().post(new Event.FinishSelf());
                        finish();
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.create();
                builder.show();
                break;
        }
    }
}
