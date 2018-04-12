package com.example.liangfeng.work;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cn.bmob.v3.Bmob;

public class MainActivity extends AppCompatActivity {

    private android.app.FragmentManager fragmentManager;
    private android.app.FragmentTransaction fragmentTransaction;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            fragmentTransaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Fargment1 fargment1 = new Fargment1();
                    fragmentTransaction.replace(R.id.fargment,fargment1,"f1");
                    fragmentTransaction.addToBackStack("f1");
                    fragmentTransaction.commit();
                    return true;
                case R.id.navigation_dashboard:
                    Fargment2 fargment2 = new Fargment2();
                    fragmentTransaction.replace(R.id.fargment,fargment2,"f2");
                    fragmentTransaction.addToBackStack("f2");
                    fragmentTransaction.commit();
                    return true;
                case R.id.navigation_notifications:
                    Fargment3 fargment3 = new Fargment3();
                    fragmentTransaction.replace(R.id.fargment,fargment3,"f3");
                    fragmentTransaction.addToBackStack("f3");
                    fragmentTransaction.commit();
                    return true;
            }

            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bmob.initialize(this, "4152371bd942fe58b6346e676de11c48");
        fragmentManager = getFragmentManager();
        Button button = (Button) findViewById(R.id.menu);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,SubmitActivity.class);
                startActivity(intent);
            }
        });
    }

}
