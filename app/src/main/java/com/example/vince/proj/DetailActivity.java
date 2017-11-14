package com.example.vince.proj;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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
    private boolean isMasked = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        View masking_add = findViewById(R.id.masking_add);
        masking_add.setVisibility(View.GONE);

        init();

        com.getbase.floatingactionbutton.FloatingActionButton fab_add = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.fab_add);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View masking_add = findViewById(R.id.masking_add);
                isMasked = !isMasked;
                if(isMasked){
                    masking_add.setVisibility(View.VISIBLE);
                }
                else{
                    masking_add.setVisibility(View.GONE);
                }

            }
        });

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
