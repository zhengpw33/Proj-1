package com.example.vince.proj.UI;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.vince.proj.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/11/25 0025.
 */

public class OddEvenAdapter extends SimpleAdapter{
    private Context context; /*运行环境*/
    List<HashMap<String, String>> listItem;  /*数据源*/
    private LayoutInflater listContainer; // 视图容器
    private int resource;
    class ListItemView { // 自定义控件集合
        public TextView txtName;
    }
    /*construction function*/
    public OddEvenAdapter(Context context, List<HashMap<String, String>> data, int resource,
                          String[] from, int[] to){
        super(context, data, resource, from, to);
        this.resource=resource;
        this.listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
        this.context=context;
        listItem=data;
    }
    /**
     * @see android.widget.Adapter#getCount()
     */
    public int getCount() {

        return listItem.size();
    }

    /**
     * @see android.widget.Adapter#getItem(int)
     */
    public Object getItem(int position) {

        return listItem.get(position);
    }

    /**
     * @see android.widget.Adapter#getItemId(int)
     */
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int mPosition = position;
        ListItemView listItemView = null;
        if (convertView == null) {
            convertView = listContainer.inflate(resource, null);//加载布局
            listItemView = new ListItemView();
                /*初始化控件容器集合*/
            listItemView.txtName=(TextView) convertView
                    .findViewById(R.id.description);
            // 设置控件集到convertView
            convertView.setTag(listItemView);

        }else{
            listItemView=(ListItemView)convertView.getTag();//利用缓存的View
        }
        if(mPosition%2==0)
            listItemView.txtName.setTextColor(Color.GREEN);
        else
            listItemView.txtName.setTextColor(Color.RED);
        listItemView.txtName.
                setText(listItem.get(mPosition).get("content"));
        return convertView;
    }
}

