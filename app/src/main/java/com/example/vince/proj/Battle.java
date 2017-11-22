package com.example.vince.proj;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Handler;
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
import com.example.vince.proj.UI.RoleAdapter;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static android.R.attr.finishOnTaskLaunch;
import static android.R.attr.type;

public class Battle extends AppCompatActivity {
    private SimpleAdapter sim_aAdapter; // 1. 新建一个数据适配器
    private  Map<String,Object> map;
    private List<Map<String, Object>> dataList; // 数据源
    private ListView listView;
    private RecyclerView rolesView1,rolesView2;
    private List<Role> roles1 = new ArrayList<>(),roles2 = new ArrayList<>();
    private CardScaleHelper cardScaleHelper1,cardScaleHelper2;
    private RoleAdapter roleadapter1,roleadapter2;
    private LinearLayout relativeLayout;
    private  AlertDialog.Builder dialog;
    private ImageView player1,blurView;
    private TextView player1_blood,player2_blood,player1_description,player2_description,action1,action2;
    private int player1_blood_num,player2_blood_num,player1_card_num,player2_card_num;
    private boolean TURN;
    private boolean DONE;
    private boolean PREPARE;
    private List<Role> dbrole;

    private boolean SIGNAL;

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
        dbrole =  DataSupport.findAll(Role.class);
        relativeLayout.setVisibility(View.INVISIBLE);
        rolesView1.setVisibility(View.VISIBLE);
        rolesView2.setVisibility(View.INVISIBLE);

        SIGNAL = false;
        relativeLayout.setVisibility(View.INVISIBLE);
        rolesView1.setVisibility(View.VISIBLE);

        blurView.setVisibility(View.VISIBLE);
        init_listview();
        init_recyclerview();
        init_laugh();
        init_dialog();
        player1_blood_num=player2_blood_num=30;
        player1_card_num=player2_card_num=0;
        player1_card_num=player2_card_num=10;

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
        //player2 = (ImageView) findViewById(R.id.player2);
    }
    private void init_listview(){
        dataList = new ArrayList<>();
        for(int i=0;i<10;++i){
            map = new LinkedHashMap<>();
            map.put("content","23333");
            dataList.add(map);
        }
        sim_aAdapter = new SimpleAdapter(this, dataList, R.layout.item, new String[]{"content"}, new int[]{R.id.description});
        listView.setAdapter(sim_aAdapter);
    }

    private void init_recyclerview(){
        rolesView1.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        roleadapter1 = new RoleAdapter(roles1);
        rolesView1.setAdapter(roleadapter1);
        cardScaleHelper1 = new CardScaleHelper();
        cardScaleHelper1.attachToRecyclerView(rolesView1);
        roleadapter1.setOnItemClickListener(new RoleAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                roles1.remove(position);
                player1_card_num-=1;
                roleadapter1.notifyDataSetChanged();
                rolesView1.setVisibility(View.INVISIBLE);
                blurView.setVisibility(View.INVISIBLE);
                TURN = true;
                map = new LinkedHashMap<>();
                map.put("content","33");
                dataList.add(0,map);
                sim_aAdapter.notifyDataSetChanged();
                listView.smoothScrollToPositionFromTop(0, 0);
            }
            @Override
            public void onLongClick(int position) {
            }
        });
        rolesView2.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        roleadapter2 = new RoleAdapter(roles2);
        rolesView2.setAdapter(roleadapter2);
        cardScaleHelper2 = new CardScaleHelper();
        cardScaleHelper2.attachToRecyclerView(rolesView2);

    }
    void init_laugh(){
        action1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //todo 称赞事件
                relativeLayout.setVisibility(View.INVISIBLE);
            }
        });
        action2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //todo 嘲讽事件
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
                            // AI_turn();
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
            roles1.add( dbrole.get((int) (Math.random() * dbrole.size())));
            roleadapter1.notifyDataSetChanged();
            cardScaleHelper1.setCurrentItemPos(player1_card_num);
            player1_card_num+=1;
            player1_description.setText(String.valueOf(player1_card_num));

            roles2.add( dbrole.get((int) (Math.random() * dbrole.size())));
            roleadapter2.notifyDataSetChanged();
            cardScaleHelper2.setCurrentItemPos(player2_card_num);
            player2_card_num+=1;
            player2_description.setText(String.valueOf(player2_card_num));
        }
        else{
            PREPARE = false;
        }
    }
    void AI_turn(){
        TURN = false;
        map = new LinkedHashMap<>();
        map.put("content","66666666666");
        dataList.add(0,map);
        sim_aAdapter.notifyDataSetChanged();
        listView.smoothScrollToPositionFromTop(0, 0);
        rolesView1.setVisibility(View.VISIBLE);
        blurView.setVisibility(View.VISIBLE);
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
    private int dip2px(Context context, float dipValue)
    {
        Resources r = context.getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dipValue, r.getDisplayMetrics());
    }
}
