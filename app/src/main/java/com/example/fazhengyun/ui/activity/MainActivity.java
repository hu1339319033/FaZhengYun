package com.example.fazhengyun.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fazhengyun.R;
import com.example.fazhengyun.adapter.MyGridAdapter;
import com.example.fazhengyun.databinding.ActivityMainBinding;
import com.example.fazhengyun.kit.SharedPreferenceUtil;
import com.example.fazhengyun.model.Constants;
import com.example.fazhengyun.model.Event;
import com.example.fazhengyun.xdroid.base.XActivity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.droidlover.xdroidbase.log.XLog;
import cn.droidlover.xdroidbase.router.Router;

public class MainActivity extends XActivity<ActivityMainBinding>
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private Toolbar toolbar;
    private GridView gridView;
    private ViewPager viewPager;
    private int[] pages = new int[] {R.mipmap.banner_01,R.mipmap.banner_02,R.mipmap.banner_03};
    @Override
    public void initData(Bundle savedInstanceState) {
        toolbar = getBinding().appBarMain2.toolbar.toolbar;
        toolbar.setNavigationIcon(R.drawable.ic_person);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getBinding().appBarMain2.toolbar.toolbarTitle.setText("法证云");
        viewPager = getBinding().appBarMain2.contentMain2.viewpager;
        gridView = getBinding().appBarMain2.contentMain2.gridview;
        DrawerLayout drawer = getBinding().drawerLayout;
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setHomeAsUpIndicator(R.drawable.ic_person);
        toggle.setDrawerIndicatorEnabled(true);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        viewPager.setPageMargin(15);
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return 300;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
//            @Override
//            public float getPageWidth(int position) {
//                return 0.8f;
//            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View view = View.inflate(container.getContext(),R.layout.widget_gallery_view, null);

//                view.setLayoutParams(getPageLayoutParams());
                ImageView iv = (ImageView) view.findViewById(R.id.headRIV);
                position = position % 3;
                iv.setImageResource(pages[position]);
                container.addView(view);
                return view;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }
        });
        viewPager.setCurrentItem(1);
        startAutomation();
        ((TextView)getBinding().navView.getHeaderView(0).findViewById(R.id.tv_userinfo)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(SharedPreferenceUtil.getInstance(context).getBoolean(Constants.LOGIN_STATE,false)){
                    Router.newIntent(context).to(UserInfoActivity.class).launch();
                }else{
                    getUiDelegate().toastShort("请先登录");
                    Router.newIntent(context).to(LoginActivity.class).launch();
                }
            }
        });
        NavigationView navigationView = getBinding().navView;
        navigationView.setBackgroundColor(Color.parseColor("#ffffff"));
        navigationView.setItemIconTintList(null);
        navigationView.setItemTextAppearance(R.style.MenuItemTextStyle);
        navigationView.setNavigationItemSelectedListener(this);

        gridView.setAdapter(new MyGridAdapter(context));
        gridView.setFocusable(false);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(!SharedPreferenceUtil.getInstance(context).getBoolean(Constants.LOGIN_STATE,false)){
                    getUiDelegate().toastShort("请先登录");
                    Router.newIntent(context).to(LoginActivity.class).launch();
                    return;
                }
                switch (i){
                    case 0://来电录音
                        Router.newIntent(context).to(CallActivity.class).launch();
                        break;
                    case 4: //录像
                        Router.newIntent(context).to(VideoActivity.class).launch();
                        break;
                    case 2://拍照
                        Router.newIntent(context).to(PhotoActivity.class).launch();
                        break;
                    case 1: //文件上传
                        Router.newIntent(context).to(FileUploadActivity.class).launch();
                        break;
                    case 3://录音
                        Router.newIntent(context).to(AudioActivity.class).launch();
                        break;
                    case 5://秒帮上传
                        Router.newIntent(context).to(DangerInformationActivity.class).launch();
                        break;
                    default:
                        getUiDelegate().toastShort("本功能敬请期待！");
                        break;
                }
            }
        });
        if(!SharedPreferenceUtil.getInstance(context).getBoolean(Constants.LOGIN_STATE,false)){
            ((TextView)getBinding().navView.getHeaderView(0).findViewById(R.id.tv_name)).setText("登录/注册");
        }else{
            //未实名认证时姓名为空默认显示手机号
            ((TextView)getBinding().navView.getHeaderView(0).findViewById(R.id.tv_name))
                    .setText(SharedPreferenceUtil.getInstance(context).getString(Constants.USERNAEM,"").equals("")?SharedPreferenceUtil.getInstance(context).getString(Constants.PHONE,""):SharedPreferenceUtil.getInstance(context).getString(Constants.USERNAEM,""));
        }
        ((TextView)getBinding().navView.getHeaderView(0).findViewById(R.id.tv_name)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((((TextView) getBinding().navView.getHeaderView(0).findViewById(R.id.tv_name)).getText().toString().equals("登录/注册"))){
                    Router.newIntent(context).to(LoginActivity.class).launch();
                }
            }
        });
//        ILFactory.getLoader().loadNet((RoundImageView)getBinding().navView.getHeaderView(0).findViewById(R.id.imageView),"https://www.edns.com/Template/t/images/small_41.png",null);
    }

    private boolean isLooper = false;

    private Handler handler = new Handler();
    /**
     * 开启自动轮播
     */
    private void startAutomation(){
        handler.postDelayed(runnable,5000);
    }
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(viewPager.getCurrentItem() < 30){
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            }
            handler.postDelayed(this,2000);
        }
    };

    private ViewPager.LayoutParams getPageLayoutParams() {
        ViewPager.LayoutParams layoutParams = new ViewPager.LayoutParams();
        layoutParams.width = (int) (viewPager.getMeasuredWidth() * 0.85);
        layoutParams.height = viewPager.getMeasuredHeight();
        return layoutParams;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_login) {
            Router.newIntent(context).to(LoginActivity.class).launch();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_help) {
            Router.newIntent(context).to(HelpActivity.class).launch();
        } else if (id == R.id.nav_msg) {
            Router.newIntent(context).to(MessageActivity.class).launch();
        } else if (id == R.id.nav_setting) {
            Router.newIntent(context).to(SettingActivity.class).launch();
        } else if(id == R.id.nav_assestlist){
            Router.newIntent(context).to(AssestListActivity.class).launch();
        } else if (id == R.id.nav_exit) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("确定退出吗？");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                    System.exit(0);
                }
            });
            builder.setNegativeButton("取消", null);
            builder.create();
            builder.show();
        }

//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private long exitTime = 0;

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            exit();
            return;
        }
//        super.onBackPressed();
    }

    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }


    /**
     * 本处订阅者作用是执行关闭自身的任务
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void helloEventBus(Event.FinishSelf event){
        XLog.e("threadmode:shou");
        finish();
    }

    @Override
    public boolean useEventBus() {
        return true;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }
}
