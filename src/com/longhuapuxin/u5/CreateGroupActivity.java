package com.longhuapuxin.u5;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * Created by ZH on 2016/1/20.
 * Email zh@longhuapuxin.com
 */
public class CreateGroupActivity extends BaseActivity {
    @ViewInject(R.id.etGroupNote)
    private EditText mGroupNoteEt;
    @ViewInject(R.id.etGroupName)
    private EditText mGroupNameEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        init();
    }

    private void init() {
        initHeader(R.string.setGroupDetail);
        ViewUtils.inject(this);
    }

    @OnClick(R.id.tvNextStep)
    public void onClick(View v) {
        String name = mGroupNameEt.getText().toString();
        String note = mGroupNoteEt.getText().toString();
        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(note)) {
            Intent intent = new Intent(this, SelectContactsActivity.class);
            intent.putExtra("groupName", mGroupNameEt.getText().toString());
            intent.putExtra("groupNote", mGroupNoteEt.getText().toString());
            startActivity(intent);
            finish();
        }


    }
}
