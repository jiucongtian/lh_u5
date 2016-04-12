/**
 * Created by lsy on 2016/1/21.
 */
package com.longhuapuxin.adapter;

import android.annotation.SuppressLint;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.longhuapuxin.db.bean.ContactBean;

import com.longhuapuxin.u5.R;

import java.util.List;


public class ContactAdapter  extends U5BaseAdapter<ContactBean> {
    private static String lastSpell="";
    public interface OnClickListner {
         void OnClick(ContactBean item);
    }



    private OnClickListner callback;
    public ContactAdapter (Context context,List<ContactBean> list,OnClickListner callback){
        super(context,list);
        this.callback=callback;
    }
 @Override
    public String getImageId(ContactBean item) {
        if(item.getPortrait()!=0){
            return  String.valueOf(item.getPortrait());
        }
     else{
            return  "";
        }
    }

    View.OnClickListener listener=new View.OnClickListener(){

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            ContactBean item=(ContactBean)arg0.getTag();
            if(callback!=null){
                callback.OnClick(item);
            }
        }

    };
    public void updateListView(List<ContactBean> list) {
        this.mDatas = list;
        notifyDataSetChanged();
    }
    @SuppressLint("NewApi") @Override
    public View getView(int position, View view, ViewGroup parent) {
        ContactBean item = mDatas.get(position);
        String spell=item.getSpell().substring(0,1);
        if(position==0){
            lastSpell="";
        }
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_addressbook, null);
        }
        TextView txtPhone=(TextView)view.findViewById(R.id.txtPhone);
        TextView txtNickName = (TextView) view.findViewById(R.id.txtNickName);
        TextView txtCared = (TextView) view.findViewById(R.id.txtCared);
        TextView catalog=(TextView)view.findViewById(R.id.catalog);

        ImageButton imageButton=(ImageButton)view.findViewById(R.id.imageButton);
        ImageView img=(ImageView)view.findViewById(R.id.img);
        if(lastSpell.equals(spell)){
            catalog.setVisibility(View.GONE);
        }
        else{
            catalog.setVisibility(View.VISIBLE);
            catalog.setText(spell);
            lastSpell=spell;
        }

        txtPhone.setText(item.getPhone());
        txtNickName.setText(item.getNickName());
        bindImageView(img,String.valueOf( item.getPortrait()));
        if(item.getCared()){
            txtCared.setVisibility(View.VISIBLE);
            imageButton.setVisibility(View.GONE);

        }
        else{
            txtCared.setVisibility(View.GONE);
            imageButton.setVisibility(View.VISIBLE);
            imageButton.setTag(item);
            imageButton.setOnClickListener(listener);
        }

        return view;
    }


}
