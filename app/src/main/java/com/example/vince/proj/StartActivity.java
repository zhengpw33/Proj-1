package com.example.vince.proj;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.vince.proj.DB.Role;

import org.litepal.tablemanager.Connector;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class StartActivity extends AppCompatActivity {
    private static final String TAG = "StartActivity";

//    String Picture;
//    String Music ;
//    Context context;
    private String[] rolesName = {"曹操", "曹仁", "大乔", "甘宁", "关羽", "郭嘉", "黄盖", "黄月英", "黄忠", "华佗", "刘备", "陆逊", "吕布", "吕蒙", "马超", "司马懿",
            "孙权", "孙尚香", "魏延", "夏侯惇", "夏侯渊", "小乔", "许褚", "张飞", "张辽", "赵云", "甄姬", "周瑜", "诸葛亮"};
    private int[]   rolesImageId = new int[]{R.mipmap.caocao,R.mipmap.caoren,R.mipmap.daqiao,
            R.mipmap.ganning,R.mipmap.guanyu,R.mipmap.guojia,R.mipmap.huanggai,R.mipmap.huangyueying,
            R.mipmap.huangzhong,R.mipmap.huatuo,R.mipmap.liubei,R.mipmap.luxun,R.mipmap.lvbu,R.mipmap.lvmeng,R.mipmap.machao,
            R.mipmap.simayi,R.mipmap.sunquan,R.mipmap.sunshangxiang,R.mipmap.weiyan,R.mipmap.xiahoudun,R.mipmap.xiahouyuan,R.mipmap.xiaoqiao,
            R.mipmap.xuzhe,R.mipmap.zhangfei,R.mipmap.zhangliao,R.mipmap.zhaoyun,R.mipmap.zhenji,R.mipmap.zhouyu,R.mipmap.zhugeliang};


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

        initRoleData();

    }

    private void initRoleData(){

        for(int i = 0;i<29 ;i++){
            Role role = new Role();
            role.setId(i);
            role.setName(rolesName[i]);
            role.setImageId(rolesImageId[i]);
            role.setDescription("Test");
            role.save();

////            try {
////                Bitmap orc_bitmap = (Bitmap) BitmapFactory.decodeResource(context.getResources(),pictId[i]);
////                saveFile(orc_bitmap,name[i]+".jpg",Picture,context);
////            } catch (IOException e) {
////                e.printStackTrace();
////            }
        }
        Log.i(TAG, "initRoleData: Done");

    }

//    public static void saveFile(Bitmap bm, String filename, String path, Context context) throws IOException {
//        String subForder = path;
//        Log.i("cunchulujing", subForder);
//        File foder = new File(subForder);
//        if (!foder.exists()) {
//            foder.mkdirs();
//        }
//        File myCaptureFile = new File(subForder, filename);
//        if (!myCaptureFile.exists()) {
//            myCaptureFile.createNewFile();
//        }
//        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
//        bm.compress(Bitmap.CompressFormat.PNG, 100, bos);
//        bos.flush();
//        bos.close();
//        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        Uri uri = Uri.fromFile(foder);
//        intent.setData(uri);
//        context.sendBroadcast(intent);
//    }
}
