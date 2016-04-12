package com.longhuapuxin.u5;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.longhuapuxin.adapter.U5BaseAdapter;
import com.longhuapuxin.common.Logger;
import com.longhuapuxin.common.OkHttpClientManager;
import com.longhuapuxin.common.WaitDialog;
import android.os.Handler;
import com.longhuapuxin.entity.ResponseDad;
import com.longhuapuxin.entity.ResponseGetCircle;
import com.longhuapuxin.entity.ResponseGroupMembers;
import com.longhuapuxin.entity.ResponseGroupMembers.Member;
import com.squareup.okhttp.Request;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by ZH on 2016/1/22.
 * Email zh@longhuapuxin.com
 */
public class GroupDetailActivity extends BaseActivity {

    private final static int REQUEST_EDIT_NAME = 0x01;
    private final static int REQUEST_EDIT_NOTE = 0x02;
    private final static int REQUEST_ADD_MEMBER = 0x03;
    private final static int REQUEST_GET_Circle=0x04;
    private final  static  int REQUEST_INIT_VIEW=0x05;
    private final  static  int RESULT_QUIT=-2;
    @ViewInject(R.id.etGroupName)
    private TextView mGroupNameTv;

    @ViewInject(R.id.etGroupNote)
    private TextView mGroupNoteTv;

    @ViewInject(R.id.gvMembers)
    private GridView mMemberGv;

    private String mGroupId;
    private String mGroupOwnerId;
    private String mGroupName;
    private String mGroupNote;
    private List<Member> mMembers;
    private GroupMemberAdapter mAdapter;

    @Override
    protected void onResume() {
        super.onResume();
//        fetchGroupMember();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);
        init();


    }
 private Handler myHandler=new Handler() {
     @SuppressLint("HandlerLeak")
     public void handleMessage(Message msg) {
         switch (msg.what) {
             case REQUEST_GET_Circle:
                 fetchGroup();
                 break;
             case REQUEST_INIT_VIEW:
                 initView();
                 break;
         }
     }

 };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {

            switch (requestCode) {
                case REQUEST_EDIT_NAME:
                    String name = data.getStringExtra("name");
                    mGroupNameTv.setText(name);
                    break;
                case REQUEST_EDIT_NOTE:
                    String note = data.getStringExtra("note");
                    mGroupNoteTv.setText(note);
                    break;
                case REQUEST_ADD_MEMBER:
                    fetchGroupMember();
                    break;

            }
        }

    }

    private void initView() {
        initHeader(mGroupName);
        mGroupNameTv.setText(mGroupName);
        mGroupNoteTv.setText(mGroupNote);
        fetchGroupMember();
        if(mGroupOwnerId.equals(Settings.instance().User.getId())) {
            mGroupNameTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(GroupDetailActivity.this, EditGroupActivity.class);
                    intent.putExtra(EditGroupActivity.EDIT_MODE, EditGroupActivity.EDIT_NAME);
                    intent.putExtra(EditGroupActivity.EDIT_GROUP_NAME, mGroupName);
                    intent.putExtra(EditGroupActivity.EDIT_GROUP_NOTE, mGroupNote);
                    intent.putExtra(EditGroupActivity.EDIT_GROUP_ID, mGroupId);
                    startActivityForResult(intent, REQUEST_EDIT_NAME);
                }
            });
            mGroupNoteTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(GroupDetailActivity.this, EditGroupActivity.class);
                    intent.putExtra(EditGroupActivity.EDIT_MODE, EditGroupActivity.EDIT_NOTE);
                    intent.putExtra(EditGroupActivity.EDIT_GROUP_NAME, mGroupName);
                    intent.putExtra(EditGroupActivity.EDIT_GROUP_NOTE, mGroupNote);
                    intent.putExtra(EditGroupActivity.EDIT_GROUP_ID, mGroupId);
                    startActivityForResult(intent, REQUEST_EDIT_NOTE);
                }
            });
        }
    }

    private void init() {
        ViewUtils.inject(this);
        Intent intent = getIntent();
        mGroupId = intent.getStringExtra("groupId");
        myHandler.sendEmptyMessage(REQUEST_GET_Circle);
        mMembers = new ArrayList<Member>();
        mAdapter = new GroupMemberAdapter(this, mMembers);
        mMemberGv.setAdapter(mAdapter);
    }


    @OnClick(R.id.tvQuitGroup)
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tvQuitGroup:

                new AlertDialog.Builder(this).setTitle("确认").setMessage("退出后你将不能再继续收到群消息？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        quitGroup();
                    }
                }).setNegativeButton("取消", null).show();
                break;
        }

    }

    private void quitGroup() {
        WaitDialog.instance().showWaitNote(this);
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[3];
        params[0] = new OkHttpClientManager.Param("UserId", Settings.instance().getUserId());
        params[1] = new OkHttpClientManager.Param("Token", Settings.instance().getToken());
        params[2] = new OkHttpClientManager.Param("CircleId", mGroupId);

        OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
                        + "/im/quitCircle", params,
                new OkHttpClientManager.ResultCallback<ResponseDad>() {

                    @Override
                    public void onError(Request request, Exception e) {
                        Logger.info("ShopFragment.fetchTopLabels.onError"
                                + e.toString());
                        WaitDialog.instance().hideWaitNote();
                    }

                    @Override
                    public void onResponse(ResponseDad response) {
                        Logger.info("onResponse is: "
                                + response.toString());
                        WaitDialog.instance().hideWaitNote();

                        if (response.isSuccess()) {
                            setResult(RESULT_QUIT);
                            finish();
                        }
                    }

                });
    }




    private void fetchGroupMember() {
        WaitDialog.instance().showWaitNote(this);
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[3];
        params[0] = new OkHttpClientManager.Param("UserId", Settings.instance().getUserId());
        params[1] = new OkHttpClientManager.Param("Token", Settings.instance().getToken());
        params[2] = new OkHttpClientManager.Param("CircleId", mGroupId);

        OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
                        + "/im/getcirclemembers", params,
                new OkHttpClientManager.ResultCallback<ResponseGroupMembers>() {

                    @Override
                    public void onError(Request request, Exception e) {
                        Logger.info("ShopFragment.fetchTopLabels.onError"
                                + e.toString());
                        WaitDialog.instance().hideWaitNote();
                    }

                    @Override
                    public void onResponse(ResponseGroupMembers response) {
                        Logger.info("onResponse is: "
                                + response.toString());
                        WaitDialog.instance().hideWaitNote();

                        if (response.isSuccess()) {
                            addAllMembers(response.getMembers());
                            mAdapter.notifyDataSetChangedWithImages();
                        }
                    }

                });
    }
    private void fetchGroup() {
        WaitDialog.instance().showWaitNote(this);
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[3];
        params[0] = new OkHttpClientManager.Param("UserId", Settings.instance().getUserId());
        params[1] = new OkHttpClientManager.Param("Token", Settings.instance().getToken());
        params[2] = new OkHttpClientManager.Param("CircleId", mGroupId);

        OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
                        + "/im/GetCircle", params,
                new OkHttpClientManager.ResultCallback<ResponseGetCircle>() {

                    @Override
                    public void onError(Request request, Exception e) {
                        Logger.info("ShopFragment.fetchTopLabels.onError"
                                + e.toString());
                        WaitDialog.instance().hideWaitNote();
                    }

                    @Override
                    public void onResponse(ResponseGetCircle response) {
                        Logger.info("onResponse is: "
                                + response.toString());
                        WaitDialog.instance().hideWaitNote();

                        if (response.isSuccess()) {

                            mGroupOwnerId = response.getCircle().getOwnerId();
                            mGroupName = response.getCircle().getName();
                            mGroupNote = response.getCircle().getNote();
                            myHandler.sendEmptyMessage(REQUEST_INIT_VIEW);
                        }
                    }

                });
    }

    private void addAllMembers(List<Member> members) {
        mMembers.clear();
        mMembers.addAll(members);

        ResponseGroupMembers newer = new ResponseGroupMembers();
        Member nullObj = newer.new Member();
        nullObj.setUserId("0");

        mMembers.add(nullObj);
    }

    public class GroupMemberAdapter extends U5BaseAdapter<Member> {

        @Override
        public String getImageId(Member item) {
            return item.getPortrait();
        }

        public GroupMemberAdapter(Context context, List<Member> list) {
            super(context, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
//            if (position >= mDatas.size()) {
//                return null;
//            }

            Member obj = mDatas.get(position);
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();

                convertView = mInflater.inflate(R.layout.item_round_image,
                        parent, false);
                viewHolder.photo = (ImageView) convertView.findViewById(R.id.ivPhoto);
                viewHolder.nickName = (TextView) convertView.findViewById(R.id.tvNickName);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            if(obj.getUserId().equals("0")) {
                viewHolder.photo.setImageResource(R.drawable.attention_icon_add);
            } else {
                String portraitId = obj.getPortrait();

                if(TextUtils.isEmpty(portraitId)) {
                    viewHolder.photo.setImageResource(R.drawable.photo_error);
                } else {
                    bindImageView(viewHolder.photo, obj.getPortrait());
                }
            }
            viewHolder.nickName.setText(obj.getNickName());
            viewHolder.photo.setTag(obj);
            viewHolder.photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Member member = (Member) view.getTag();

                    if (member.getUserId().equals("0")) {
                        Intent intent = new Intent(GroupDetailActivity.this, SelectContactsActivity.class);
                        intent.putExtra("groupId", mGroupId);
                        intent.putExtra("groupName", mGroupName);
                        intent.putExtra("groupNote", mGroupNote);
                        String[] ids = new String[mMembers.size()];
                        for(int i = 0; i < mMembers.size(); i++) {
                            ids[i] = mMembers.get(i).getUserId();
                        }
                        intent.putExtra("members", ids);
                        startActivityForResult(intent, REQUEST_ADD_MEMBER);
                    }
                }
            });

            return convertView;
        }

        public class ViewHolder {
            public ImageView photo;
            public TextView nickName;
        }
    }
}
