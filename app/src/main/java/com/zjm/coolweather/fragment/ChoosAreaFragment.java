package com.zjm.coolweather.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zjm.coolweather.MainActivity;
import com.zjm.coolweather.R;
import com.zjm.coolweather.WeartherActivity;
import com.zjm.coolweather.db.City;
import com.zjm.coolweather.db.County;
import com.zjm.coolweather.db.Province;
import com.zjm.coolweather.util.HttpUtil;
import com.zjm.coolweather.util.Untility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 *
 */
public class ChoosAreaFragment extends Fragment {
    /**
     * 行政级别为省
     */
    public static final int LEVEL_PROVINCE = 0;
    /**
     * 行政级别为市
     */
    public static final int LEVEL_CITY = 1;
    /**
     * 行政级别为县
     */
    public static final int LEVEL_COUNTY = 2;
    /**
     * 声明控件
     */
    private Button btn_back;
    private TextView titleText;
    private ListView listView;
    /**
     * 声明listview的adapter
     */
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();
    /**
     * 省份列表
     */
    private List<Province> provinces;
    /**
     * 城市列表
     */
    private List<City> cities;
    /**
     * 县区列表
     */
    private List<County> counties;
    /**
     * 当前显示的行政级别
     */
    private int currentlevel;
    /**
     * 当前选中的省份
     */
    private Province selectedProvince;
    /**
     * 当前选中的城市列表
     */
    private City selectedCity;
    private ProgressDialog progressDialog;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //初始化控件
        View view = inflater.inflate(R.layout.choose_area, container, false);
        titleText = view.findViewById(R.id.titleText);
        btn_back = view.findViewById(R.id.back_burron);
        listView = view.findViewById(R.id.list_view);
        //初始化adapter
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        return view;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /**
         * 给listview设置监听
         */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentlevel == LEVEL_PROVINCE) {
                    selectedProvince = provinces.get(position);
                    queryCities();
                } else if (currentlevel == LEVEL_CITY) {
                    selectedCity = cities.get(position);
                    quryCounties();
                }else if(currentlevel==LEVEL_COUNTY){
                    String weatherId = counties.get(position).getWeatherId();
                    if (getActivity() instanceof MainActivity){
                        Intent intent = new Intent(getActivity(), WeartherActivity.class);
                        intent.putExtra("weather_id",weatherId);
                        startActivity(intent);
                        getActivity().finish();
                    }else if(getActivity() instanceof WeartherActivity){
                        WeartherActivity activity = (WeartherActivity) getActivity();
                        activity.drawerLayout.closeDrawers();
                        activity.swipeRefresh.setRefreshing(true);
                        activity.requestWeather(weatherId);

                    }


                }
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentlevel == LEVEL_COUNTY) {
                    queryCities();
                } else if (currentlevel == LEVEL_CITY) {
                    quryProvinces();
                }
            }
        });
        quryProvinces();
    }


    /**
     * 查询全国所有的省，优先从数据库查询，如果数据库中查询不到，再到服务器查询
     */
    private void quryProvinces() {
        //将标题设置为中国
        titleText.setText("中国");
        //将返回按钮隐藏
        btn_back.setVisibility(View.GONE);
        //查询数据库
        provinces = DataSupport.findAll(Province.class);
        //判断从数据中查询到的省份是否为空，
        if (provinces.size() > 0) {
            //如果provinces不为空，说明查询到了省份数据，清空datalist集合，将查询到的省份名称添加到集合中
            dataList.clear();
            for (Province province : provinces) {
                dataList.add(province.getProvinceNmae());
            }
            //listview更新数据
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            //将当前的行政级别设置为省
            currentlevel = LEVEL_PROVINCE;
        } else {
            //如果provinces为空，则从网络获取省份列表
            String address = "http://guolin.tech/api/china";
            queryFromServer(address, "province");
        }
    }

    /**
     * 查询县区列表
     */
    private void quryCounties() {
        titleText.setText(selectedCity.getCityName());
        btn_back.setVisibility(View.VISIBLE);
        counties = DataSupport.where("cityid =?", String.valueOf(selectedCity.getId())).find(County.class);
        if (counties.size() > 0) {
            dataList.clear();
            for (County county : counties) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentlevel = LEVEL_COUNTY;
        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/"+provinceCode+"/"+cityCode;
            queryFromServer(address,"county");
        }
    }

    /**
     * 查询城市列表
     */
    private void queryCities() {
        titleText.setText(selectedProvince.getProvinceNmae());
        btn_back.setVisibility(View.VISIBLE);
        cities = DataSupport.where("provinceid =?", String.valueOf(selectedProvince.getId())).find(City.class);
        if (cities.size() > 0) {
            dataList.clear();
            for (City city : cities) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentlevel = LEVEL_CITY;
        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            queryFromServer(address, "city");
        }
    }

    /**
     * 根据传入的地址和类型从服务器上获取数据
     *
     * @param address：地址
     */
    private void queryFromServer(String address, final String type) {
        //显示进度条对话框
        showProgressDialog();
        //联网获取数据
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if ("province".equals(type)) {
                    result = Untility.handleProcinceResponse(responseText);
                    Log.i("zjm", "查询省份完成");
                }else if("city".equals(type)){
                    result = Untility.handleCityResponse(responseText,selectedProvince.getId());
                    Log.i("zjm", "查询城市完成");
                }else if ("county".equals(type)){
                    result = Untility.handleCountiesResponse(responseText,selectedCity.getId());
                    Log.i("zjm", "查询县区完成");
                }
                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                quryProvinces();
                            }else if("city".equals(type)){
                                queryCities();
                            }else if ("county".equals(type)){
                                quryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
//通过runOnUiThread()方法回到主线程处理逻辑
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(), "加载失败！", Toast.LENGTH_SHORT).show();
                    }
                });

            }


        });
    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    /**
     * 加载数据时显示对话框
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在获取数据...!");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

}
