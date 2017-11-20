package com.example.vince.proj;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.vince.proj.DB.Role;
import com.example.vince.proj.UI.CardScaleHelper;
import com.example.vince.proj.UI.RoleAdapter;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {
    private static final String TAG = "DetailActivity";
    private RecyclerView rolesView;
    private List<Role> roles = new ArrayList<>();
    private CardScaleHelper cardScaleHelper = null;
    private Runnable runnable;
    private boolean showMaskingAdd = false;
    private boolean showMaskingSearch = false;
    private int Index = 4;
    private RoleAdapter roleAdapter = new RoleAdapter(roles);
    private View masking_add;
    private View masking_search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        masking_add = findViewById(R.id.masking_add);
        masking_add.setVisibility(View.GONE);
        masking_search = findViewById(R.id.masking_search);
        masking_search.setVisibility(View.GONE);

        init();

        Connector.getDatabase();

        final com.getbase.floatingactionbutton.FloatingActionButton fab_add = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.fab_add);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //View masking_add = findViewById(R.id.masking_add);
                showMaskingAdd = !showMaskingAdd;
                if(showMaskingAdd){
                    masking_add.setVisibility(View.VISIBLE);
                }
                else{
                    masking_add.setVisibility(View.GONE);
                }
                //设置fab_menu按钮折叠
                com.getbase.floatingactionbutton.FloatingActionsMenu fab_menu = (com.getbase.floatingactionbutton.FloatingActionsMenu) findViewById(R.id.fab_menu);
                fab_menu.collapseImmediately();
            }
        });

        com.getbase.floatingactionbutton.FloatingActionButton fab_search = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.fab_search);
        fab_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //View masking_search = findViewById(R.id.masking_search);
                showMaskingSearch = !showMaskingSearch;
                if(showMaskingSearch){
                    masking_search.setVisibility(View.VISIBLE);
                }
                else{
                    masking_search.setVisibility(View.GONE);
                }

                //设置fab_menu按钮折叠
                com.getbase.floatingactionbutton.FloatingActionsMenu fab_menu = (com.getbase.floatingactionbutton.FloatingActionsMenu) findViewById(R.id.fab_menu);
                fab_menu.collapseImmediately();
            }
        });

        Button commit_masking_add = (Button) findViewById(R.id.commit_masking_add);
        commit_masking_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Role role_ = new Role();
                role_.setId(Index++);
                role_.setName("zhouyu");
                role_.setImageId(R.mipmap.zhouyu);
                role_.setDescription("Test");
                role_.save();

                roles.add(role_);

                roleAdapter.notifyDataSetChanged();

                masking_add.setVisibility(View.GONE);
            }
        });

    }

    //初始化RecyclerView
    private void init(){
        List<Role> roles_ = DataSupport.findAll(Role.class);
        for(Role role: roles_){
            roles.add(role);
        }
        rolesView = (RecyclerView)findViewById(R.id.recyclerView);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rolesView.setLayoutManager(linearLayoutManager);
        rolesView.setAdapter(roleAdapter);
        cardScaleHelper = new CardScaleHelper();
        cardScaleHelper.setCurrentItemPos(2);
        cardScaleHelper.attachToRecyclerView(rolesView);
    }

}
