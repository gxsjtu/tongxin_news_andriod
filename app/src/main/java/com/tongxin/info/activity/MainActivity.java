package com.tongxin.info.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.tongxin.info.R;
import com.tongxin.info.domain.MyApp;
import com.tongxin.info.page.boxFragment;
import com.tongxin.info.page.hqFragment;
import com.tongxin.info.page.meFragment;
import com.tongxin.info.page.plFragment;
import com.tongxin.info.page.sqFragment;

/**
 * Created by Administrator on 2015/9/21.
 */
public class MainActivity extends BaseFragmentActivity {
    private FrameLayout main_fl_content;
    private RadioGroup main_rg_group;
    private RadioButton main_rb_inbox;
    private RadioButton main_rb_hq;
    private RadioButton main_rb_pl;
    private RadioButton main_rb_sq;
    private RadioButton main_rb_me;
    private FragmentManager fragmentManager;
    FragmentTransaction tran;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        ((MyApp)getApplication()).getActivityList().add(this);

        main_fl_content = (FrameLayout) findViewById(R.id.main_fl_content);
        main_rg_group = (RadioGroup) findViewById(R.id.main_rg_group);
        main_rb_inbox = (RadioButton) findViewById(R.id.main_rb_inbox);
        main_rb_hq = (RadioButton) findViewById(R.id.main_rb_hq);
        main_rb_pl = (RadioButton) findViewById(R.id.main_rb_pl);
        main_rb_sq = (RadioButton) findViewById(R.id.main_rb_sq);
        main_rb_me = (RadioButton) findViewById(R.id.main_rb_me);

        initViews();
    }

    private void initViews()
    {

        fragmentManager = getSupportFragmentManager();

        main_rg_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId)
                {
                    case R.id.main_rb_inbox:
                        //收件箱
                        showPage(new boxFragment());
                        break;
                    case R.id.main_rb_hq:
                        //行情
                        showPage(new hqFragment());
                        break;
                    case R.id.main_rb_pl:
                        //评论
                        showPage(new plFragment());
                        break;
                    case R.id.main_rb_sq:
                        //商圈
                        showPage(new sqFragment());
                        break;
                    case R.id.main_rb_me:
                        //我
                        showPage(new meFragment());
                        break;
                }
            }
        });


        //默认选中收件箱
        main_rg_group.check(R.id.main_rb_inbox);
    }

    private void showPage(Fragment fragment)
    {
        tran = fragmentManager.beginTransaction();
        tran.replace(R.id.main_fl_content,fragment);
        tran.commit();
    }
}
