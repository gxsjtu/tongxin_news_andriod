package com.tongxin.info.activity;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.tongxin.info.R;
import com.tongxin.info.control.SegmentedGroup;
import com.tongxin.info.global.GlobalContants;
import com.tongxin.info.utils.ToastUtils;
import com.tongxin.info.utils.UserUtils;
import org.json.JSONObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Created by cc on 2015/11/9.
 */
public class SqCatalogItemAdd extends BaseActivity  {
    private EditText tv_ChannelName;
    private EditText tv_ChannelQty;
    private EditText tv_ChannelPrice;
    private EditText tv_ChannelMobile;
    private EditText tv_ChannelContact;
    private EditText tv_ChannelDesc;
    private Button btn_ItemLocation;
    private RadioButton rb_ItemGY;
    private RadioButton rb_ItemCG;
    private RadioButton rb_ItemZT;
    private RadioButton rb_ItemFH;
    private Button btn_Sure;
    private LinearLayout img_Return;
    private LinearLayout img_Options;
    private TextView tv_HeaderText;
    private int channelID;
    private String channelName;
    private String productName;
    private Button btn_GetImgs;
    private static final int RESULT = 1;
    private ImageView iv_imgForSlider;
    private Bitmap bmp;
    private ArrayList<Bitmap> bmps = new ArrayList<Bitmap>();
    private ArrayList<File> files = new ArrayList<File>();
    private ArrayList<String> strUriList = new ArrayList<String>();
    CountDownTimer timer1;
    private String tel;
    private String location_Country;
    private String location_City;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    ToastUtils.Show(SqCatalogItemAdd.this, "添加成功，请等待系统审核！");
                    break;
                case 1:
                    ToastUtils.Show(SqCatalogItemAdd.this, "新增失败！");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sq_channelitemadd);

        UserUtils userUtils = new UserUtils(SqCatalogItemAdd.this);
        tel = userUtils.getTel();
        Intent intent = getIntent();
        channelID = intent.getIntExtra("CATALOGCHANNEL_ID", 0);
        channelName = intent.getStringExtra("CATALOGCHANNEL_NAME");
        productName = intent.getStringExtra("PRODUCT_NAME");


        tv_ChannelName = (EditText)findViewById(R.id.tv_addChannelName);
        tv_ChannelQty = (EditText)findViewById(R.id.tv_addChannelQty);
        tv_ChannelPrice = (EditText)findViewById(R.id.tv_addChannelPrice);
        tv_ChannelMobile = (EditText)findViewById(R.id.tv_addChannelMobile);
        tv_ChannelContact = (EditText)findViewById(R.id.tv_addChannelContact);
        tv_ChannelDesc = (EditText)findViewById(R.id.tv_addChannelDesc);
        iv_imgForSlider = (ImageView)findViewById(R.id.sq_itemAddImgView);
        btn_GetImgs = (Button) findViewById(R.id.btn_sqGetImgs);
        SegmentedGroup segmented2 = (SegmentedGroup) findViewById(R.id.sq_segmented1);
        segmented2.setVisibility(View.GONE);
        tv_HeaderText = (TextView) findViewById(R.id.sq_HeaderText);
        tv_HeaderText.setText("商圈 - " + channelName + " - " + productName);
        btn_Sure = (Button) findViewById(R.id.btn_spHeaderSure);
        btn_Sure.setText("完成");
        btn_Sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkData()) {
                    new Thread() {
                        @Override
                        public void run() {
                            final Message msg = Message.obtain();
                          String result =  AddItemData();
                            if("ok".equals(result))
                            {
                                msg.what = 0;
//                                Intent intent = new Intent(SqCatalogItemAdd.this, sqListFragment.class);
//                                intent.putExtra("CHANNEL_ID",channelID);
//                                intent.putExtra("CHANNEL_NAME",channelName);
//                                startActivity(intent);

                               // Intent intent = new Intent();
//                                intent.putExtra("CHANNEL_ID", channelID);
//                                intent.putExtra("CHANNEL_NAME", channelName);
                               // setResult(6, intent);
                                setResult(10);
                                finish();
                            }
                            else
                            {
                                msg.what = 1;
                            }
                            mHandler.sendMessage(msg);
                        }
                    }.start();
                }
            }
        });
        img_Return = (LinearLayout) findViewById(R.id.sq_ivReturn);
        img_Options = (LinearLayout) findViewById(R.id.iv_sqMenu);
        img_Options.setVisibility(View.GONE);
        img_Return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SqCatalogItemAdd.this,SqCatalogActivity.class);
                intent.putExtra("CATALOGCHANNEL_ID", channelID);
                intent.putExtra("CATALOGCAHNNEL_NAME", channelName);
                startActivityForResult(intent,4);
//                setResult(4, intent);
                finish();
//                startActivity(intent);
            }
        });

        rb_ItemGY = (RadioButton) findViewById(R.id.rb_addChannelGY);
        rb_ItemCG = (RadioButton) findViewById(R.id.rb_addChannelCG);
        rb_ItemZT = (RadioButton) findViewById(R.id.rb_addChannelZT);
        rb_ItemFH = (RadioButton) findViewById(R.id.rb_addChannelFH);
        rb_ItemGY.setChecked(true);
        rb_ItemZT.setChecked(true);
        btn_ItemLocation = (Button) findViewById(R.id.btn_addChannelLocation);

        timer1 = new CountDownTimer(600000, 2000) {
            int j = 0;

            @Override
            public void onTick(long millisUntilFinished) {
                // TODO Auto-generated method stub
//                if (bmps  != null && bmps.size() > 0) {
//                    if(j > bmps.size() - 1)
//                    {
//                        j = 0;
//                    }
//                    iv_imgForSlider.setImageBitmap(bmps.get(j));
//                    iv_imgForSlider.refreshDrawableState();
//                    j++;
//                }
                if(strUriList != null && strUriList.size() > 0)
                {
                    if(j > strUriList.size() - 1)
                    {
                        j = 0;
                    }

                    ImageLoader.getInstance().displayImage(strUriList.get(j),iv_imgForSlider);
                    iv_imgForSlider.refreshDrawableState();
                    j++;
                }
            }

            @Override
            public void onFinish() {
                // TODO Auto-generated method stub
//                if (bmps  != null && bmps.size() > 0) {
//                    if(j > bmps.size() - 1)
//                    {
//                        j = 0;
//                    }
//                    iv_imgForSlider.setImageBitmap(bmps.get(j));
//                    iv_imgForSlider.refreshDrawableState();
//                }
                if(strUriList != null && strUriList.size() > 0)
                {
                    if(j > strUriList.size() - 1)
                    {
                        j = 0;
                    }
                    ImageLoader.getInstance().displayImage(strUriList.get(j),iv_imgForSlider);
                    iv_imgForSlider.refreshDrawableState();
                    j++;
                }
            }
        };

    btn_ItemLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SqCatalogItemAdd.this, CitiesActivity.class);
                startActivityForResult(intent,0);
            }
        });
        btn_ItemLocation.setText("选择交货地");

        rb_ItemGY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rb_ItemCG.setChecked(false);
            }
        });

        rb_ItemCG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rb_ItemGY.setChecked(false);
            }
        });

        rb_ItemZT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rb_ItemFH.setChecked(false);
            }
        });

        rb_ItemFH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rb_ItemZT.setChecked(false);
            }
        });

        btn_GetImgs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer1.cancel();
//                Dialog dialog;
//                dialog = new AlertDialog.Builder(SqCatalogItemAdd.this)
//                        .setTitle("从图库里选择照片").setPositiveButton("确定",
//                                new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        // TODO Auto-generated method stub
//                                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                                        startActivityForResult(intent, RESULT);
//                                    }
//                                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
//
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                // TODO Auto-generated method stub
//                                dialog.cancel();
//                            }
//                        }).create();
//                dialog.show();
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RESULT);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        // mDemoSlider.removeAllSliders();
        if (requestCode == RESULT && resultCode == RESULT_OK && data != null) {
                Uri uri = data.getData();
                strUriList.add(uri.toString());
            String[] proj = {MediaStore.Images.Media.DATA};

            //好像是android多媒体数据库的封装接口，具体的看Android文档
            Cursor cursor = managedQuery(uri, proj, null, null, null);
            //按我个人理解 这个是获得用户选择的图片的索引值
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            //将光标移至开头 ，这个很重要，不小心很容易引起越界
            cursor.moveToFirst();
            //最后根据索引值获取图片路径
            String path = cursor.getString(column_index);
            File file = new File(path);
            files.add(file);
            ImageLoader.getInstance().displayImage(uri.toString(), iv_imgForSlider);
//            Bitmap bmp = ImageLoader.getInstance().loadImageSync(path);
//            iv_imgForSlider.setImageBitmap(bmp);
//                ContentResolver cr = this.getContentResolver();
//                try {
//                    if (bmp != null)//如果不释放的话，不断取图片，将会内存不够
//                        MyRecycle(bmp);
//                    BitmapFactory.Options options = new  BitmapFactory.Options();
//                    options.inSampleSize = 4;//calculateInSampleSize(options,200,200);
//                    bmp = BitmapFactory.decodeStream(cr.openInputStream(uri),null,options);
//                    bmps.add(bmp);
//
//                    iv_imgForSlider.setImageBitmap(bmp);
                    timer1.start();
//                } catch (Exception e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
        }
        else if(resultCode == 0 && data != null)
        {
            location_Country = data.getStringExtra("SQ_ITEMCOUNTRY");
            location_City = data.getStringExtra("SQ_ITEMCITY");
            if (!"".equals(location_Country) && location_Country != null) {
                btn_ItemLocation.setText(location_Country + " " + location_City);
            }
        }else if(resultCode == 10 && data != null)
        {
            channelID = data.getIntExtra("CATALOGCHANNEL_ID", 0);
            channelName = data.getStringExtra("CATALOGCHANNEL_NAME");
            productName = data.getStringExtra("PRODUCT_NAME");
        }

    }
    public static void MyRecycle(Bitmap bmp){
        if(!bmp.isRecycled() && null!=bmp){
//            bmp.recycle();
            bmp=null;
        }
    }

    private String AddItemData()
    {
        String res = "error";
        String sOrP = "1";
        String sOro = "1";

        if(rb_ItemGY.isChecked())
        {
            sOrP = "0";
        }
        else if(rb_ItemCG.isChecked())
        {
            sOrP = "1";
        }

        if(rb_ItemFH.isChecked())
        {
            sOro = "0";
        }
        else if(rb_ItemZT.isChecked())
        {
            sOro = "1";
        }

        //创建okHttpClient对象
        OkHttpClient mOkHttpClient = new OkHttpClient();
        MultipartBuilder multipart = new MultipartBuilder();
        for (int i =0;i<files.size();i++)
        {
            RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), files.get(i));
            multipart.addPart(Headers.of(
                    "Content-Disposition",
                    "form-data; name=\"mFile\";filename=\"a.jpg\""), fileBody);
        }
        RequestBody requestBody = multipart.type(MultipartBuilder.FORM)
                .addPart(Headers.of(
                                "Content-Disposition",
                                "form-data; name=\"method\""),
                        RequestBody.create(null, "create"))
            .addPart(Headers.of(
                            "Content-Disposition",
                            "form-data; name=\"product\""),
                    RequestBody.create(null, tv_ChannelName.getText().toString()))
                .addPart(Headers.of(
                                "Content-Disposition",
                                "form-data; name=\"quantity\""),
                        RequestBody.create(null, tv_ChannelQty.getText().toString()))
                .addPart(Headers.of(
                                "Content-Disposition",
                                "form-data; name=\"mobile\""),
                        RequestBody.create(null, tv_ChannelMobile.getText().toString()))
                .addPart(Headers.of(
                                "Content-Disposition",
                                "form-data; name=\"createdBy\""),
                        RequestBody.create(null, tel))
                .addPart(Headers.of(
                                "Content-Disposition",
                                "form-data; name=\"contact\""),
                        RequestBody.create(null, tv_ChannelContact.getText().toString()))
                .addPart(Headers.of(
                                "Content-Disposition",
                                "form-data; name=\"price\""),
                        RequestBody.create(null, tv_ChannelPrice.getText().toString()))
                .addPart(Headers.of(
                                "Content-Disposition",
                                "form-data; name=\"description\""),
                        RequestBody.create(null, tv_ChannelDesc.getText().toString()))
                .addPart(Headers.of(
                                "Content-Disposition",
                                "form-data; name=\"type\""),
                        RequestBody.create(null, sOrP))
                .addPart(Headers.of(
                                "Content-Disposition",
                                "form-data; name=\"deliveryType\""),
                        RequestBody.create(null, sOro))
                .addPart(Headers.of(
                                "Content-Disposition",
                                "form-data; name=\"catalogID\""),
                        RequestBody.create(null, String.valueOf(channelID)))
                .addPart(Headers.of(
                                "Content-Disposition",
                                "form-data; name=\"province\""),
                        RequestBody.create(null, location_Country))
                .addPart(Headers.of(
                                "Content-Disposition",
                                "form-data; name=\"city\""),
                        RequestBody.create(null, location_City))
                .build();

        Request request = new Request.Builder()
                .url(GlobalContants.GETSPLIST_URL)
                .post(requestBody)
                .build();

        Call call = mOkHttpClient.newCall(request);

        try {
           JSONObject json = new JSONObject(call.execute().body().string());
            res = json.getString("result");
        }catch (Exception ex)
        {

        }
        return res;
    }

    private boolean checkData()
    {
        boolean result = true;
        if("".equals(tv_ChannelName.getText().toString()) || "".equals(tv_ChannelName.getText().toString().replace(" ", "")))
        {
            result = false;
            ToastUtils.Show(SqCatalogItemAdd.this, "货物内容不能为空！");
            return result;
        }
        else if("".equals(tv_ChannelQty.getText().toString()) || "".equals(tv_ChannelQty.getText().toString().replace(" ","")))
        {
            result = false;
            ToastUtils.Show(SqCatalogItemAdd.this, "供需数量不能为空！");
            return result;
        }
        else if("".equals(tv_ChannelMobile.getText().toString()) || "".equals(tv_ChannelMobile.getText().toString().replace(" ","")))
        {
            result = false;
            ToastUtils.Show(SqCatalogItemAdd.this, "联系方式不能为空！");
            return result;
        }else  if("".equals(btn_ItemLocation.getText().toString()) || "选择交货地".equals(btn_ItemLocation.getText().toString()))
        {
            result = false;
            ToastUtils.Show(SqCatalogItemAdd.this, "交货地不能为空！");
            return result;
        }
        else
        {
            return result;
        }
//        return result;
    }

}
