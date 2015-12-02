package com.tongxin.info.page;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.tongxin.info.R;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/9/24.
 */
public class sqFragment extends Fragment implements Serializable {
    private Activity mActivity;
    private TextView tv_headerTitle;
    private ImageView iv_return;
    private ImageView iv_ref;
    private ImageButton ib_jbjs;
    private ImageButton ib_fjys;
    private ImageButton ib_gjs;
    private ImageButton ib_fg;
    private ImageButton ib_jc;
    private ImageButton ib_gp;
    private ImageButton ib_fbxg;
    private ImageButton ib_bxg;
    private ImageButton ib_st;
    private ImageButton ib_tks;
    private ImageButton ib_thj;
    private ImageButton ib_xyxjs;
    private ImageButton ib_fz;
    private ImageButton ib_zssl;
    private ImageButton ib_qt;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
//        setContentView(R.layout.sqcontent);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        container.removeAllViews();
        View view = View.inflate(mActivity, R.layout.sqcontent, null);
        tv_headerTitle = (TextView) view.findViewById(R.id.tv_headerTitle);
        tv_headerTitle.setText("商圈频道");

        ib_jbjs = (ImageButton)view.findViewById(R.id.imgJBJS);
        ib_jbjs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqClick(1, "基本金属");
            }
        });

        ib_fjys = (ImageButton)view.findViewById(R.id.imgFJYS);
        ib_fjys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqClick(2,"废旧有色");
            }
        });

        ib_gjs = (ImageButton)view.findViewById(R.id.imgGJS);
        ib_gjs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqClick(3,"贵金属");
            }
        });

        ib_fg = (ImageButton)view.findViewById(R.id.imgFG);
        ib_fg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqClick(4,"废钢");
            }
        });

        ib_jc = (ImageButton)view.findViewById(R.id.imgJC);
        ib_jc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqClick(5,"建材");
            }
        });

        ib_gp = (ImageButton)view.findViewById(R.id.imgGP);
        ib_gp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqClick(6,"钢坯");
            }
        });

        ib_fbxg = (ImageButton)view.findViewById(R.id.imgFBXG);
        ib_fbxg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqClick(7,"废不锈钢");
            }
        });

        ib_bxg = (ImageButton)view.findViewById(R.id.imgBXG);
        ib_bxg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqClick(8,"不锈钢");
            }
        });

        ib_st = (ImageButton)view.findViewById(R.id.imgST);
        ib_st.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqClick(9,"生铁");
            }
        });

        ib_tks = (ImageButton)view.findViewById(R.id.imgTKS);
        ib_tks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqClick(10, "铁矿石");
            }
        });

        ib_thj = (ImageButton)view.findViewById(R.id.imgTHJ);
        ib_thj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqClick(11,"铁合金");
            }
        });

        ib_xyxjs = (ImageButton)view.findViewById(R.id.imgXYXJS);
        ib_xyxjs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqClick(12,"稀有小金属");
            }
        });

        ib_fz = (ImageButton)view.findViewById(R.id.imgFZ);
        ib_fz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqClick(13,"废纸");
            }
        });

        ib_zssl = (ImageButton)view.findViewById(R.id.imgZSSL);
        ib_zssl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqClick(14,"再生塑料");
            }
        });

        ib_qt = (ImageButton)view.findViewById(R.id.imgQT);
        ib_qt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqClick(15,"其他");
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void sqClick(int channelId,String channelName)
    {
        Intent intent = new Intent(mActivity,sqListFragment.class);
        intent.putExtra("CHANNEL_ID",channelId);
        intent.putExtra("CHANNEL_NAME", channelName);
        startActivity(intent);
    }
}
