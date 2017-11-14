package com.example.vince.proj;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.vince.proj.DB.Role;
import com.example.vince.proj.UI.CardScaleHelper;
import com.example.vince.proj.UI.RoleAdapter;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private RecyclerView rolesView;
    private List<Role> roles = new ArrayList<>();
    private CardScaleHelper cardScaleHelper = null;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        init();

    }

    private void init(){
        for(int i = 0; i < 4; i++){
            roles.add(new Role(i, R.mipmap.zhouyu, String.valueOf(i),"Test" ));
        }
        rolesView = (RecyclerView)findViewById(R.id.recyclerView);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rolesView.setLayoutManager(linearLayoutManager);
        rolesView.setAdapter(new RoleAdapter(roles));
        cardScaleHelper = new CardScaleHelper();
        cardScaleHelper.setCurrentItemPos(2);
        cardScaleHelper.attachToRecyclerView(rolesView);
    }

}
