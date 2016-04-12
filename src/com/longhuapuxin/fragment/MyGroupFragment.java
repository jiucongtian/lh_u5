package com.longhuapuxin.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.longhuapuxin.adapter.U5BaseAdapter;
import com.longhuapuxin.common.Logger;
import com.longhuapuxin.common.OkHttpClientManager;
import com.longhuapuxin.common.WaitDialog;
import com.longhuapuxin.entity.ResponseMyGroup;
import com.longhuapuxin.entity.ResponseMyGroup.Circle;
import com.longhuapuxin.u5.ChatActivity;
import com.longhuapuxin.u5.GroupDetailActivity;
import com.longhuapuxin.u5.R;
import com.longhuapuxin.u5.Settings;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZH on 2016/1/21.
 * Email zh@longhuapuxin.com
 */
public class MyGroupFragment extends Fragment implements AdapterView.OnItemClickListener {

    View mView;
    ListView mMyGroupLv;
    List<Circle> mGroups;
    MyGroupAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Logger.debug("MainLabelFragment->onCreateView");
        mView = inflater.inflate(R.layout.fragment_my_group, null);
        init();
        return mView;
    }

    private void init() {
        mGroups = new ArrayList<Circle>();
        mMyGroupLv = (ListView) mView.findViewById(R.id.lvMyGroup);
        mAdapter = new MyGroupAdapter(getActivity(), mGroups);
        mMyGroupLv.setAdapter(mAdapter);
        mMyGroupLv.setOnItemClickListener(this);

        fetchMyGroup();
    }

    private void fetchMyGroup() {
        WaitDialog.instance().showWaitNote(getActivity());
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[2];
        params[0] = new OkHttpClientManager.Param("UserId", Settings.instance().getUserId());
        params[1] = new OkHttpClientManager.Param("Token", Settings.instance().getToken());

        OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
                        + "/im/getmycircles", params,
                new OkHttpClientManager.ResultCallback<ResponseMyGroup>() {

                    @Override
                    public void onError(Request request, Exception e) {
                        Logger.info("ShopFragment.fetchTopLabels.onError"
                                + e.toString());
                        WaitDialog.instance().hideWaitNote();
                    }

                    @Override
                    public void onResponse(ResponseMyGroup response) {
                        Logger.info("onResponse is: "
                                + response.toString());
                        WaitDialog.instance().hideWaitNote();
                        if (response.isSuccess()) {
                            mGroups.clear();
                            mGroups.addAll(response.getCircles());
                            mAdapter.notifyDataSetChanged();
                        }
                    }

                });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("SessionId", mGroups.get(i).getId());
        intent.putExtra("OwnerId", mGroups.get(i).getOwnerId());
        intent.putExtra("GroupName", mGroups.get(i).getName());
        intent.putExtra("GroupNote", mGroups.get(i).getNote());
        intent.putExtra("isGroup", true);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchMyGroup();
    }

    public class MyGroupAdapter extends U5BaseAdapter<Circle> {

        public MyGroupAdapter(Context context, List<Circle> list) {
            super(context, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (position >= mDatas.size()) {
                return null;
            }

            Circle obj = mDatas.get(position);
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();

                convertView = mInflater.inflate(R.layout.item_simple_with_image,
                        parent, false);
                viewHolder.name = (TextView) convertView.findViewById(R.id.tvName);
                viewHolder.photo = (ImageView) convertView.findViewById(R.id.ivIcon);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.photo.setImageResource(R.drawable.attention_picture);
            viewHolder.name.setText(obj.getName());

            return convertView;
        }

        public class ViewHolder {
            public TextView name;
            public ImageView photo;
        }
    }
}
