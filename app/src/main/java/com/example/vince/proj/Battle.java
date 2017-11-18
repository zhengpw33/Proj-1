package com.example.vince.proj;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class Battle extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle);
        final LinearLayout relativeLayout = (LinearLayout) findViewById(R.id.laugh);
        relativeLayout.setVisibility(View.INVISIBLE);
        final ImageView player1 = (ImageView) findViewById(R.id.player1);
        player1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeLayout.setVisibility(View.VISIBLE);
                RelativeLayout.LayoutParams my = (RelativeLayout.LayoutParams)player1.getLayoutParams();
                my.width = dip2px(Battle.this, 150);
                my.height= dip2px(Battle.this, 225);
                player1.setLayoutParams(my);
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
