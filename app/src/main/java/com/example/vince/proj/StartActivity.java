package com.example.vince.proj;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.vince.proj.DB.Role;

import org.litepal.tablemanager.Connector;

public class StartActivity extends AppCompatActivity {
    private static final String TAG = "StartActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Button toDetail =  (Button) findViewById(R.id.to_detail);
        Button startGame = (Button) findViewById(R.id.start_game);

        toDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, DetailActivity.class);
                startActivity(intent);
            }
        });
        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, Battle.class);
                startActivity(intent);
            }
        });

        //连接数据库
        Connector.getDatabase();

        //判断是否是第一次启动
        SharedPreferences shared = getSharedPreferences("launchChecker", MODE_PRIVATE);
        boolean isFirstLaunch = shared.getBoolean("isFirstLaunch",true);
        SharedPreferences.Editor editor=shared.edit();
        //若是第一次启动，则初始化数据库数据
        if(isFirstLaunch){
            initRoleData();
            editor.putBoolean("isFirstLaunch",false);
            editor.commit();
        }

    }

    private void initRoleData(){

        Role role0 = new Role();
        role0.setId(0);
        Log.i(TAG, "init: "+role0.getId());
        role0.setName("zhouyu0");
        role0.setImageId(R.mipmap.zhouyu);
        role0.setDescription("Test");
        role0.save();

        Role role1 = new Role();
        role1.setId(1);
        Log.i(TAG, "init: "+role1.getId());
        role1.setName("zhouyu1");
        role1.setImageId(R.mipmap.zhouyu);
        role1.setDescription("Test");
        role1.save();

        Role role2 = new Role();
        role2.setId(2);
        Log.i(TAG, "init: "+role2.getId());
        role2.setName("zhouyu2");
        role2.setImageId(R.mipmap.zhouyu);
        role2.setDescription("Test");
        role2.save();

        Role role3 = new Role();
        role3.setId(3);
        Log.i(TAG, "init: "+role3.getId());
        role3.setName("zhouyu3");
        role3.setImageId(R.mipmap.zhouyu);
        role3.setDescription("Test");
        role3.save();

        Log.i(TAG, "initRoleData: Done");
    }
}
