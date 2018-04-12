package com.example.liangfeng.work;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class Fargment2 extends Fragment {
    private ArrayAdapter<String> arrayAdapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        arrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,getdata());
    }

    public List<String> getdata(){
        List<String> list = new ArrayList<String>();
        for (int i = 0;i<30;i++){
            list.add("东西" + i);
        }
        return list;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View  view = inflater.inflate(R.layout.f1,null);
        ListView listView = (ListView) view.findViewById(R.id.list);
        listView.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
    }


}
