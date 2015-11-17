package com.tongxin.info.activity;

import com.tongxin.info.R;
import com.tongxin.info.control.SegmentedGroup;
import com.tongxin.info.wheel.OnWheelChangedListener;
import com.tongxin.info.wheel.OnWheelScrollListener;
import com.tongxin.info.wheel.WheelView;
import com.tongxin.info.wheel.adapters.AbstractWheelTextAdapter;
import com.tongxin.info.wheel.adapters.ArrayWheelAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

public class CitiesActivity extends BaseActivity {
	// Scrolling flag
	private boolean scrolling = false;
    private Button btn_Sure;
    private LinearLayout img_Return;
    private LinearLayout img_Options;
    private TextView tv_HeaderText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.cities_layout);
        SegmentedGroup segmented2 = (SegmentedGroup) findViewById(R.id.sq_segmented1);
        tv_HeaderText = (TextView)findViewById(R.id.sq_HeaderText);
        tv_HeaderText.setText("请选择交货地");
        segmented2.setVisibility(View.GONE);
        btn_Sure = (Button)findViewById(R.id.btn_spHeaderSure);
        img_Return = (LinearLayout)findViewById(R.id.sq_ivReturn);
        img_Options = (LinearLayout)findViewById(R.id.iv_sqMenu);
        img_Options.setVisibility(View.GONE);
        img_Return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

		final WheelView country = (WheelView) findViewById(R.id.country);
		country.setVisibleItems(3);
		country.setViewAdapter(new CountryAdapter(this));
//        country.setCurrentItem(0);
//      final  String countries[] =
//                new String[] {"直辖市", "特别行政区", "台湾省", "安徽省","福建省","甘肃省","广东省","广西自治区",
//                        "贵州省","海南省","河北省","河南省","黑龙江省","湖北省","湖南省","吉林省","江苏省",
//                        "江西省","辽宁省","内蒙古","宁夏自治区","青海省","山东省","山西省","陕西省","四川省",
//                        "西藏自治区","新疆","云南省","浙江省"};
		final String cities[][] = new String[][]{
				new String[] {"北京", "上海", "天津", "重庆"},
				new String[] {"香港", "澳门"},
				new String[] {"台北"},
				new String[] {"合肥", "安庆","蚌埠","亳州","巢湖","滁州","阜阳","淮北","淮南","黄山","六安","马鞍山","宿州","铜陵","芜湖","宣州"},
                new String[] {"福州","龙海","龙岩","南平","宁德","莆田","泉州","三明","厦门","漳州"},
                new String[] {"兰州","白银","嘉峪关","金昌","酒泉","平凉","天水","武威","张掖"},
                new String[] {"广州","潮州","东莞","佛山","河源","惠州","江门","茂名","梅州","清远","汕头","汕尾","韶关","深圳","顺德","阳江","云浮","湛江","肇庆","中山","珠海"},
                new String[] {"南宁","北海","百色","防城港","贵港","桂林","河池","柳州","钦州","梧州","玉林"},
                new String[] {"贵阳","安顺","六盘水","兴义","遵义"},
                new String[] {"海口","儋州","琼海","琼山","三亚"},
                new String[] {"石家庄","保定","沧州","承德","邯郸","衡水","廊坊","秦皇岛","唐山","张家口"},
                new String[] {"郑州","安阳","鹤壁","焦作","济源","开封","漯河","洛阳","南阳","平顶山","濮阳","三门峡","商丘","新乡","信仰","许昌","周口","驻马店"},
                new String[] {"哈尔滨","大庆","黑河","佳木斯","鸡西","牡丹江","齐齐哈尔","七台河","双鸭山","绥芬河","绥化"},
                new String[] {"武汉","黄石","荆门","十堰","随州","天门","襄阳","咸宁","仙桃","孝感","宜昌","宜城","枣阳"},
                new String[] {"长沙","常德","郴州","衡阳","怀化","津市","浏阳","娄底","韶山","邵阳","湘潭","永州","岳阳","张家界","株洲"},
                new String[] {"长春","白城","白山","敦化","公主岭","吉林","辽源","通化"},
                new String[] {"南京","常熟","常州","淮安","昆山","连云港","南通","如皋","宿迁","宿州","太仓","泰兴","通州","无锡","徐州","扬州","张家港","镇江"},
                new String[] {"南昌","赣州","吉安","景德镇","井冈山","九江","萍乡","瑞金","上饶","新余","宜春","鹰潭"},
                new String[] {"沈阳","鞍山","本溪","朝阳","大连","丹东","抚顺","阜新","葫芦岛","锦州","辽阳","盘锦","铁岭","营口"},
                new String[] {"呼和浩特","包头","赤峰","满洲里","通辽","乌兰浩特","乌海"},
                new String[] {"银川","青铜峡","石嘴山","吴忠"},
                new String[] {"西宁","德令哈","格尔木"},
                new String[] {"济南","青岛","安丘","滨州","昌邑","德州","东营","菏泽","济宁","莱芜","聊城","临沂","蓬莱","曲阜","日照","荣成","泰安","潍坊","威海","烟台","枣庄","淄博"},
                new String[] {"太原","大同","晋城","临汾","朔州","忻州","阳泉","运城"},
                new String[] {"西安","咸阳","安康","宝鸡","汉中","商州","铜川","渭南","延安","榆林"},
                new String[] {"成都","巴中","崇州","达川","德阳","都江堰","峨眉山","广元","乐山","泸州","绵阳","南充","内江","攀枝花","雅安","宜宾","自贡","资阳"},
                new String[] {"拉萨","日喀则"},
                new String[] {"乌鲁木齐","阿克苏","阿勒泰","阿图什","博乐","昌吉","阜康","哈密","和田","克拉玛依","喀什","库尔勒","奎屯","石河子","塔城","吐鲁番","伊宁"},
                new String[] {"昆明","保山","大理","曲靖","思茅","玉溪","昭通"},
                new String[] {"杭州","奉化","湖州","嘉兴","金华","丽水","宁波","衢州","瑞安","绍兴","台州","温州","义乌"}
		};

		final WheelView city = (WheelView) findViewById(R.id.city);
		city.setVisibleItems(5);

		country.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                if (!scrolling) {
                    updateCities(city, cities, newValue);
                }
            }
        });

		country.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {
                scrolling = true;
            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                scrolling = false;
                updateCities(city, cities, country.getCurrentItem());
            }
        });

		country.setCurrentItem(1);
        country.setCurrentItem(0);

        btn_Sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("SQ_ITEMCOUNTRY", new CountryAdapter(CitiesActivity.this).getItemText(country.getCurrentItem()));
                intent.putExtra("SQ_ITEMCITY",cities[country.getCurrentItem()][city.getCurrentItem()]);
//                startActivity(intent);
                setResult(0,intent);
                finish();
            }
        });
	}

	/**
	 * Updates the city wheel
	 */
	private void updateCities(WheelView city, String cities[][], int index) {
		ArrayWheelAdapter<String> adapter =
				new ArrayWheelAdapter<String>(this, cities[index]);
		adapter.setTextSize(18);
		city.setViewAdapter(adapter);
//		city.setCurrentItem(cities[index].length / 2);
        city.setCurrentItem(0);
	}

	/**
	 * Adapter for countries
	 */
	private class CountryAdapter extends AbstractWheelTextAdapter {
		// Countries names
		private String countries[] =
				new String[] {"直辖市", "特别行政区", "台湾省", "安徽省","福建省","甘肃省","广东省","广西自治区",
						      "贵州省","海南省","河北省","河南省","黑龙江省","湖北省","湖南省","吉林省","江苏省",
						 	  "江西省","辽宁省","内蒙古","宁夏自治区","青海省","山东省","山西省","陕西省","四川省",
							  "西藏自治区","新疆","云南省","浙江省"};
		// Countries flags

		/**
		 * Constructor
		 */
		protected CountryAdapter(Context context) {
			super(context, R.layout.country_layout, NO_RESOURCE);

			setItemTextResource(R.id.country_name);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
			return view;
		}

		@Override
		public int getItemsCount() {
			return countries.length;
		}

		@Override
		protected CharSequence getItemText(int index) {
//            countryText = countries[index];
			return countries[index];
		}
	}
}
