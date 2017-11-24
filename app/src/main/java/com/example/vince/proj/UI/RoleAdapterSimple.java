package com.example.vince.proj.UI;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vince.proj.DB.Role;
import com.example.vince.proj.R;

import java.util.List;

/**
 * Created by vince on 2017/11/13.
 */


public class RoleAdapter extends RecyclerView.Adapter<RoleAdapter.ViewHolder> {

    final private List<Role> roleLists;
    private RoleAdapterHelper roleAdapterHelper = new RoleAdapterHelper();
    private static final String TAG = "RoleAdapter";
    OnItemClickListener mOnItemClickListener;

    protected OnItemClickListener myOnItemClickListener;

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView  nameField;
        ImageView portraitField;
        TextView  lifeTimeField;
        ImageView nationalityField;
        TextView  nativePlace;

        public ViewHolder(View view){
            super(view);
            //nameField = (TextView) view.findViewById(R.id.name_main_list);
            portraitField = (ImageView) view.findViewById(R.id.role_content);
            nameField = (TextView) view.findViewById(R.id.name_role_list);
            lifeTimeField = (TextView) view.findViewById(R.id.life_time_role_list);
            nationalityField = (ImageView) view.findViewById(R.id.nationality_label);
            nativePlace = (TextView) view.findViewById(R.id.native_place_role_list);
            //nameField.setTypeface(Typeface.createFromAsset(getAssets(), "SIMLI.TTF"));
        }
    }

    public RoleAdapter(List<Role> roleLists){
        this.roleLists = roleLists;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.role_list, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        roleAdapterHelper.onCreateViewHolder(parent, view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        roleAdapterHelper.onBindViewHolder(holder.itemView, position, getItemCount());
        Role role = roleLists.get(position);
//        holder.nameField.setText(character.getName());

        holder.portraitField.setImageResource(role.getImageId());
        if(myOnItemClickListener!=null){
            holder.itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    myOnItemClickListener.onClick(holder.getAdapterPosition());
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View v){
                    myOnItemClickListener.onLongClick(holder.getAdapterPosition());
                    return false;
                }
            });
        }



        holder.nameField.setText(role.getName());
        holder.lifeTimeField.setText(role.getLifeTime());

        if(role.getNationality().equals("κ")){
            holder.nationalityField.setImageResource(R.mipmap.wei);
        }
        else if(role.getNationality().equals("��")){
            holder.nationalityField.setImageResource(R.mipmap.shu);
        }
        else{
            holder.nationalityField.setImageResource(R.mipmap.wu);
        }

        if(role.isDefault()){
            holder.portraitField.setImageResource(role.getImageId());
        }
        else{
            Bitmap orc_bitmap = BitmapFactory.decodeFile(role.getImagePath());//��ȡͼƬ // orc_bitmap = comp(BitmapFactory.decodeFile(imagePath)); //ѹ��ͼ
            holder.portraitField.setImageBitmap(orc_bitmap);
        }

        holder.nativePlace.setText(role.getNativePlace());
        Log.i(TAG, "onBindViewHolder: "+role.getLifeTime());

        if(mOnItemClickListener != null){
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View v){
                    mOnItemClickListener.onLongClick((holder.getAdapterPosition()));
                    return  false;
                }

            });
        }
    }

    @Override
    public int getItemCount() {
        return roleLists.size();
    }


    public interface OnItemClickListener{
        void onLongClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        mOnItemClickListener = onItemClickListener;
    }
    public void removeItem(int position){
        roleLists.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(0, roleLists.size());

    public interface  OnItemClickListener{//�ӿ�
        void onClick(int position);
        void onLongClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.myOnItemClickListener=onItemClickListener;
    }
}