package com.example.vince.proj;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.media.Image;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.vince.proj.DB.Role;
import com.example.vince.proj.UI.CardScaleHelper;
import com.example.vince.proj.UI.OddEvenAdapter;
import com.example.vince.proj.UI.RoleAdapter;
import com.example.vince.proj.UI.RoleAdapterSimple;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentNavigableMap;

import static android.R.attr.finishOnTaskLaunch;
import static android.R.attr.type;

public class Battle extends AppCompatActivity {
    private SimpleAdapter sim_aAdapter; // 1. 新建一个数据适配器
    private HashMap<String,String> map;
    private List<HashMap<String, String>> dataList; // 数据源
    private ListView listView;
    private RecyclerView rolesView1,rolesView2;
    private List<Role> roles1 = new ArrayList<>(),roles2 = new ArrayList<>();
    private CardScaleHelper cardScaleHelper1,cardScaleHelper2;
    private RoleAdapterSimple roleadapter1,roleadapter2;
    private LinearLayout relativeLayout;
    private  AlertDialog.Builder dialog;
    private ImageView player1,blurView,VS;
    private TextView player1_blood,player2_blood,player1_description,player2_description,action1,action2;
    private int player1_blood_num,player2_blood_num,player1_card_num,player2_card_num;
    private boolean TURN;
    private boolean DONE;
    private boolean PREPARE;
    private List<Role> dbrole;
    private int CURRENT;

    private MusicService.MusicBinder musicBinder;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBinder = null;
        }
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (musicBinder == null) {
                musicBinder = (MusicService.MusicBinder) service;
                //启动音乐
                musicBinder.startPlay(0,0);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle);
        init();
        prepare();
        player1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeLayout.setVisibility(View.VISIBLE);
                //RelativeLayout.LayoutParams my = (RelativeLayout.LayoutParams)player1.getLayoutParams();
                //my.width = (int)(my.width*1.1);

                dialog.setMessage("游戏结束，你赢了!!!");
                //dialog.show();
                map = new LinkedHashMap<>();
                map.put("content","3333333");
                dataList.add(0,map);
                sim_aAdapter.notifyDataSetChanged();
                listView.smoothScrollToPositionFromTop(0, 0);
            }
        });
    }

    private  void init(){
        findview();
        TURN = false;
        DONE = false;

        PREPARE = true;
        player1_blood_num=player2_blood_num=30;
        player1_card_num=player2_card_num=0;
        dbrole =  DataSupport.findAll(Role.class);
        relativeLayout.setVisibility(View.INVISIBLE);
        rolesView1.setVisibility(View.VISIBLE);
        rolesView2.setVisibility(View.INVISIBLE);


        relativeLayout.setVisibility(View.INVISIBLE);
        rolesView1.setVisibility(View.VISIBLE);

        blurView.setVisibility(View.VISIBLE);
        init_listview();
        init_recyclerview();
        init_laugh();
        init_dialog();
    }
    private void findview(){
        relativeLayout = (LinearLayout) findViewById(R.id.laugh);
        player1_blood = (TextView)findViewById(R.id.player1_blood);
        player2_blood = (TextView)findViewById(R.id.player2_blood);
        player1_description = (TextView)findViewById(R.id.player1_card);
        player2_description = (TextView)findViewById(R.id.player2_card);
        blurView = (ImageView)findViewById(R.id.blurView);
        listView = (ListView)findViewById(R.id.battle_description);
        rolesView1 = (RecyclerView)findViewById(R.id.battle_recycler1);
        rolesView2 = (RecyclerView)findViewById(R.id.battle_recycler2);
        player1 = (ImageView) findViewById(R.id.player1);
        action1 = (TextView) findViewById(R.id.action1);
        action2 = (TextView) findViewById(R.id.action2);
        VS = (ImageView)findViewById(R.id.vs);
        //player2 = (ImageView) findViewById(R.id.player2);
    }
    private void init_listview(){
        dataList = new ArrayList<>();

        sim_aAdapter = new OddEvenAdapter(this,dataList, R.layout.item, new String[]{"content"}, new int[]{R.id.description});
        listView.setAdapter(sim_aAdapter);
    }

    private void init_recyclerview(){
        rolesView1.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        roleadapter1 = new RoleAdapterSimple(roles1);
        rolesView1.setAdapter(roleadapter1);
        cardScaleHelper1 = new CardScaleHelper();
        cardScaleHelper1.attachToRecyclerView(rolesView1);
        roleadapter1.setOnItemClickListener(new RoleAdapterSimple.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                map = new LinkedHashMap<>();
                map.put("content",roles1.get(position).getName());
                dataList.add(0,map);
                sim_aAdapter.notifyDataSetChanged();
                roles1.remove(position);
                player1_card_num-=1;
                roleadapter1.notifyDataSetChanged();
                rolesView1.setVisibility(View.INVISIBLE);
                blurView.setVisibility(View.INVISIBLE);
                player1_description.setText(String.valueOf(player1_card_num));
                player2_blood_num-=(int)(Math.random()*10);
                player2_blood.setText(String.valueOf(player2_blood_num));
                TURN = true;
                isgameover();
               // listView.smoothScrollToPositionFromTop(0, 0);
            }
            @Override
            public void onLongClick(int position) {
            }
        });
        rolesView2.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        roleadapter2 = new RoleAdapterSimple(roles2);
        rolesView2.setAdapter(roleadapter2);

    }
    void init_laugh(){
        action1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent();
                intent.setAction("action1");
                sendBroadcast(intent);
                relativeLayout.setVisibility(View.INVISIBLE);
            }
        });
        action2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent();
                intent.setAction("action2");
                sendBroadcast(intent);
                relativeLayout.setVisibility(View.INVISIBLE);
            }
        });
    }
    private void prepare(){
        final Handler mHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch(msg.what)
                {
                    case 33:
                        if(PREPARE){
                            deal();
                        }
                        else{
                            AI_turn();
                        }
                }
            }
        };
        Thread mThread = new Thread(){
            @Override
            public void run(){
                try {
                    Thread.sleep(500); // 设置更新间隔
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mHandler.sendEmptyMessage(33);
                if(!DONE){
                    run();
                }
            }
        };
        mThread.start();
    }
    void deal()
    {
        if(player1_card_num<8){
            RelativeLayout.LayoutParams my = (RelativeLayout.LayoutParams)VS.getLayoutParams();
            my.width = (int)(my.width*1.2);
            my.height=(int)(my.height*1.2);
            VS.setLayoutParams(my);
            roles1.add( 0,dbrole.get((int) (Math.random() * dbrole.size())));
            roleadapter1.notifyDataSetChanged();

            player1_card_num+=1;
            player1_description.setText(String.valueOf(player1_card_num));

            roles2.add( dbrole.get((int) (Math.random() * dbrole.size())));
            roleadapter2.notifyDataSetChanged();
            player2_card_num+=1;
            player2_description.setText(String.valueOf(player2_card_num));
        }
        else{
            VS.setVisibility(View.INVISIBLE);
            PREPARE = false;
        }
    }
    void AI_turn(){
        if(TURN){
            if(CURRENT<6){
                CURRENT+=1;
            }
            else{
                CURRENT=0;
                TURN = false;
                int position = (int)Math.random()*player2_card_num;
                map = new LinkedHashMap<>();
                map.put("content",roles2.get(position).getName());
                dataList.add(0,map);
                sim_aAdapter.notifyDataSetChanged();
                roles2.remove(position);
                player2_card_num-=1;
                roleadapter2.notifyDataSetChanged();
                player2_description.setText(String.valueOf(player2_card_num));
                player1_blood_num-=(int)(Math.random()*10);
                player1_blood.setText(String.valueOf(player1_blood_num));
                rolesView1.setVisibility(View.VISIBLE);
                blurView.setVisibility(View.VISIBLE);
                isgameover();
            }
        }

    }
    void isgameover(){
        if(player1_blood_num<=0) gameover(false);
        else if(player2_blood_num<=0) gameover(true);
    }
    void init_dialog(){
        dialog = new AlertDialog.Builder(Battle.this);
        dialog.setTitle("游戏结果");
        dialog.setCancelable(true);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
    }
    void gameover(boolean outcome){
        TURN = false;
        if(outcome){
            dialog.setMessage("游戏结束，你赢了!!!");
            DONE = true;
        }
        else{
            dialog.setMessage("游戏结束，你输了!!!");
            DONE = true;
        }
        dialog.show();
    }
    private int dip2px(Context context, float dipValue)
    {
        Resources r = context.getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dipValue, r.getDisplayMetrics());
    }
}
