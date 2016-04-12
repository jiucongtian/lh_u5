package com.longhuapuxin.u5;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import com.lidroid.xutils.ViewUtils;
import android.widget.AdapterView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.longhuapuxin.adapter.SortGroupMemberAdapter;
import com.longhuapuxin.common.GetFirstLetter;
import com.longhuapuxin.common.Logger;
import com.longhuapuxin.common.OkHttpClientManager;
import com.longhuapuxin.common.WaitDialog;
import com.longhuapuxin.entity.ResponseDad;
import com.longhuapuxin.entity.ResponseGetAccount;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ZH on 2016/1/20.
 * Email zh@longhuapuxin.com
 */
public class SelectContactsActivity extends BaseActivity implements SectionIndexer {

    @ViewInject(R.id.lvSelectContacts)
    private ListView mContactLv;

    @ViewInject(R.id.title_layout)
    private View titleLayout;

    @ViewInject(R.id.title_layout_catalog)
    private TextView title;

    private List<ResponseGetAccount.User.CareWho> careWhos;
    private SortGroupMemberAdapter mAdapter;
    private int lastFirstVisibleItem = -1;
    private String mGroupName, mGroupNote, mGroupId;
    private Set<String> mFrozenIds;

    private Boolean isCreateMode;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contacts);
        ViewUtils.inject(this);
        init();
        initView();
    }

    private void init() {
        careWhos = Settings.instance().User.getCareWho();

        for (ResponseGetAccount.User.CareWho careWho : careWhos) {
            char firstchar = GetFirstLetter.getSpells(careWho.getNickName())
                    .toUpperCase().charAt(0);

            careWho.setFirstChar(String.valueOf(firstchar));
        }

        Intent intent = getIntent();
        mGroupId = intent.getStringExtra("groupId");

        if(TextUtils.isEmpty(mGroupId)) {
            isCreateMode = true;
        } else {
            isCreateMode = false;
        }

        mGroupName = intent.getStringExtra("groupName");
        mGroupNote = intent.getStringExtra("groupNote");
        String[] ids = intent.getStringArrayExtra("members");
        if(ids != null) {
            mFrozenIds = new HashSet<String>(Arrays.asList(ids));
        } else {
            mFrozenIds = null;
        }
    }

    private void initView() {

        mAdapter = new SortGroupMemberAdapter(this, careWhos, true);
        mContactLv.setAdapter(mAdapter);
        mContactLv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                mAdapter.markItem(position);
                mAdapter.notifyDataSetChangedWithImages();

            }
        });




        mContactLv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                // 判断没有数据
                if (totalItemCount == 0) {
                    return;
                }
                int section = getSectionForPosition(firstVisibleItem);
                // 判断只有一个数据
                if (careWhos.size() == 1) {
                    String firstLetter = careWhos.get(
                            getPositionForSection(section)).getFirstChar();
                    Pattern p1 = Pattern.compile("[a-zA-Z]");
                    Matcher m1 = p1.matcher(String.valueOf(firstLetter));
                    Pattern p2 = Pattern.compile("[\u4e00-\u9fa5]");
                    Matcher m2 = p2.matcher(String.valueOf(firstLetter));
                    if (!m1.matches() && !m2.matches()) {
                        firstLetter = "#";
                    }

                    title.setText(firstLetter);
                    return;
                }
                int nextSection = getSectionForPosition(firstVisibleItem + 1);
                int nextSecPosition = getPositionForSection(+nextSection);
                // 看的到完整titleLayout
                if (firstVisibleItem != lastFirstVisibleItem) {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) titleLayout
                            .getLayoutParams();
                    params.topMargin = 0;
                    titleLayout.setLayoutParams(params);

                    String firstLetter = careWhos.get(
                            getPositionForSection(section)).getFirstChar();
                    Pattern p1 = Pattern.compile("[a-zA-Z]");
                    Matcher m1 = p1.matcher(String.valueOf(firstLetter));
                    Pattern p2 = Pattern.compile("[\u4e00-\u9fa5]");
                    Matcher m2 = p2.matcher(String.valueOf(firstLetter));
                    if (!m1.matches() && !m2.matches()) {
                        firstLetter = "#";
                    }

                    title.setText(firstLetter);
                }
                if (nextSecPosition == firstVisibleItem + 1) {
                    View childView = view.getChildAt(0);
                    if (childView != null) {
                        int titleHeight = titleLayout.getHeight();
                        int bottom = childView.getBottom();
                        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) titleLayout
                                .getLayoutParams();
                        if (bottom < titleHeight) {
                            float pushedDistance = bottom - titleHeight;
                            params.topMargin = (int) pushedDistance;
                            titleLayout.setLayoutParams(params);
                        } else {
                            if (params.topMargin != 0) {
                                params.topMargin = 0;
                                titleLayout.setLayoutParams(params);
                            }
                        }
                    }
                }
                lastFirstVisibleItem = firstVisibleItem;
            }
        });
        if (careWhos.size() != 0) {
            titleLayout.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.tvCancel, R.id.rightTextBtn})
    public void onClick(View v) {
        int id = v.getId();
        switch(id) {
            case R.id.tvCancel:
                finish();
                break;
            case R.id.rightTextBtn:
                if(isCreateMode) {
                    createGroup();
                } else {
                    addMembers();
                }
                break;
        }
    }

    private String getStringIds(List<Integer> ids) {
        String ret = "";
        if(ids != null && ids.size() > 0) {
            for (Integer item : ids) {
                ret += careWhos.get(item).getId();
                ret += ",";
            }
            ret = ret.substring(0, ret.length() - 1);
        }

        return ret;
    }

    private void createGroup() {
        WaitDialog.instance().showWaitNote(this);
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[5];
        params[0] = new OkHttpClientManager.Param("UserId", Settings.instance().getUserId());
        params[1] = new OkHttpClientManager.Param("Token", Settings.instance().getToken());
        params[2] = new OkHttpClientManager.Param("Name", mGroupName);
        params[3] = new OkHttpClientManager.Param("Note", mGroupNote);
        params[4] = new OkHttpClientManager.Param("UserIds", getStringIds(mAdapter.getSelectedList()));

        OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
                        + "/im/createCircle", params,
                new OkHttpClientManager.ResultCallback<String>() {

                    @Override
                    public void onError(Request request, Exception e) {
                        Logger.info("----NewSessionFail");
                        WaitDialog.instance().hideWaitNote();
                        finish();
                    }

                    @Override
                    public void onResponse(String response) {
                        WaitDialog.instance();
                        finish();
                    }
                });
    }

    private void addMembers() {
        WaitDialog.instance().showWaitNote(this);
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[4];
        params[0] = new OkHttpClientManager.Param("UserId", Settings.instance().getUserId());
        params[1] = new OkHttpClientManager.Param("Token", Settings.instance().getToken());
        params[2] = new OkHttpClientManager.Param("CircleId", mGroupId);
        params[3] = new OkHttpClientManager.Param("Members", getStringIds(mAdapter.getSelectedList()));

        OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
                        + "/im/addcirclemember", params,
                new OkHttpClientManager.ResultCallback<ResponseDad>() {

                    @Override
                    public void onError(Request request, Exception e) {
                        Logger.info("----NewSessionFail");
                        WaitDialog.instance().hideWaitNote();
                        finish();
                    }

                    @Override
                    public void onResponse(ResponseDad response) {
                        WaitDialog.instance().hideWaitNote();
                        if(response.isSuccess()) {
                            setResult(RESULT_OK);
                        }
                        finish();
                    }
                });
    }

    @Override
    public Object[] getSections() {
        return null;
    }

    @Override
    public int getPositionForSection(int section) {
        for (int i = 0; i < careWhos.size(); i++) {
            String sortStr = careWhos.get(i).getFirstChar();
            Pattern p1 = Pattern.compile("[a-zA-Z]");
            Matcher m1 = p1.matcher(sortStr);
            Pattern p2 = Pattern.compile("[\u4e00-\u9fa5]");
            Matcher m2 = p2.matcher(sortStr);
            if (!m1.matches() && !m2.matches()) {
                sortStr = "#";
            }
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getSectionForPosition(int position) {
        ResponseGetAccount.User.CareWho careWho = careWhos.get(position);
        char firstLetter = careWho.getFirstChar().toUpperCase().charAt(0);
        Pattern p1 = Pattern.compile("[a-zA-Z]");
        Matcher m1 = p1.matcher(String.valueOf(firstLetter));
        Pattern p2 = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m2 = p2.matcher(String.valueOf(firstLetter));
        if (!m1.matches() && !m2.matches()) {
            firstLetter = "#".charAt(0);
        }
        return firstLetter;

    }
}
