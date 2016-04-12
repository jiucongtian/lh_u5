package com.longhuapuxin.u5;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.longhuapuxin.common.Logger;
import com.longhuapuxin.common.OkHttpClientManager;
import com.longhuapuxin.common.WaitDialog;
import com.longhuapuxin.entity.ResponseDad;
import com.longhuapuxin.entity.ResponseGroupMembers;
import com.squareup.okhttp.Request;

/**
 * Created by ZH on 2016/1/25.
 * Email zh@longhuapuxin.com
 */
public class EditGroupActivity extends BaseActivity {

    public final static String EDIT_MODE = "edit_mode";
    public final static String EDIT_GROUP_NAME = "group_name";
    public final static String EDIT_GROUP_NOTE = "group_note";
    public final static String EDIT_GROUP_ID = "group_id";
    public final static int EDIT_NAME = 1;
    public final static int EDIT_NOTE = 2;
    private int mEditMode = 0;
    private String mGroupName, mGroupNote, mGroupId;

    @ViewInject(R.id.etGroupName)
    private EditText mGroupNameEt;

    @ViewInject(R.id.etGroupNote)
    private EditText mGroupNoteEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        initView();
    }

    private void init() {
        Intent intent = getIntent();
        mEditMode = intent.getIntExtra(EDIT_MODE, 0);
        mGroupName = intent.getStringExtra(EDIT_GROUP_NAME);
        mGroupNote = intent.getStringExtra(EDIT_GROUP_NOTE);
        mGroupId = intent.getStringExtra(EDIT_GROUP_ID);
    }

    private void initView() {
        setContentView(R.layout.activity_edit_group);
        ViewUtils.inject(this);
        if(mEditMode == EDIT_NAME) {
            initHeader(R.string.groupName);
            mGroupNameEt.setVisibility(View.VISIBLE);
            mGroupNoteEt.setVisibility(View.GONE);
        } else if (mEditMode == EDIT_NOTE) {
            initHeader(R.string.groupNote);
            mGroupNameEt.setVisibility(View.GONE);
            mGroupNoteEt.setVisibility(View.VISIBLE);
        }

        mGroupNameEt.setText(mGroupName);
        mGroupNoteEt.setText(mGroupNote);

        enableRightTextBtn(R.string.complete, true, new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!mGroupName.equals(mGroupNameEt.getText().toString()) ||
                        !mGroupNote.equals(mGroupNoteEt.getText().toString())) {
                    editGroup();
                } else {
                    setResult(RESULT_CANCELED);
                    finish();
                }
            }
        });
    }

    private void editGroup() {

        WaitDialog.instance().showWaitNote(this);
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[5];
        params[0] = new OkHttpClientManager.Param("UserId", Settings.instance().getUserId());
        params[1] = new OkHttpClientManager.Param("Token", Settings.instance().getToken());
        params[2] = new OkHttpClientManager.Param("CircleId", mGroupId);
        params[3] = new OkHttpClientManager.Param("Name", mGroupNameEt.getText().toString());
        params[4] = new OkHttpClientManager.Param("Note", mGroupNoteEt.getText().toString());

        OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
                        + "/im/UpdateCircle", params,
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
                            setResult(RESULT_OK);
                            Intent intent = getIntent();
                            intent.putExtra("name", mGroupNameEt.getText().toString());
                            intent.putExtra("note", mGroupNoteEt.getText().toString());
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }

                });
    }
}
