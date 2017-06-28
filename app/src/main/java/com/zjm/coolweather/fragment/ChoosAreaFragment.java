package com.zjm.coolweather.fragment;


import android.app.Application;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.zjm.coolweather.R;

import java.util.ArrayList;
import java.util.List;


/**
 *
 */
public class ChoosAreaFragment extends Fragment {
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


    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        //初始化控件
        View view = inflater.inflate(R.layout.choose_area,container,false);
        titleText = view.findViewById(R.id.titleText);
        btn_back = view.findViewById(R.id.back_burron);
        listView = view.findViewById(R.id.list_view);
        //初始化adapter
        adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        /**
         * 给listview设置监听
         */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }
}
