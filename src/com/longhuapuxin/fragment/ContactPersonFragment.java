package com.longhuapuxin.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.longhuapuxin.adapter.SortGroupMemberAdapter;
import com.longhuapuxin.common.CharacterParser;
import com.longhuapuxin.common.GetFirstLetter;
import com.longhuapuxin.common.Logger;
import com.longhuapuxin.common.OkHttpClientManager;
import com.longhuapuxin.common.OkHttpClientManager.Param;
import com.longhuapuxin.entity.ResponseGetAccount.User.CareWho;
import com.longhuapuxin.entity.ResponseNewSession;
import com.longhuapuxin.u5.ChatActivity;
import com.longhuapuxin.u5.R;
import com.longhuapuxin.u5.Settings;
import com.longhuapuxin.view.LetterSideBar;
import com.longhuapuxin.view.LetterSideBar.OnTouchingLetterChangedListener;
import com.squareup.okhttp.Request;

@SuppressLint({"NewApi", "ValidFragment"})
public class ContactPersonFragment extends Fragment implements SectionIndexer {

    // 排好序的data
    private List<CareWho> careWhos;
    private List<CareWho> copyCareWhos;
    private CareWho copyCareWho;

    private ListView sortListView;
    private LetterSideBar letterSideBar;
    private TextView dialog;
    private SortGroupMemberAdapter adapter;
    private LinearLayout titleLayout;
    // private boolean isFirstOnScroll = true;
    private CharacterParser characterParser = new CharacterParser();
    private TextView title;
    private TextView tvNofriends;
    private EditText searchName;
    /**
     * 上次第一个可见元素，用于滚动时记录标识。
     */
    private int lastFirstVisibleItem = -1;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    public ContactPersonFragment(List<CareWho> careWhos) {
        this.careWhos = careWhos;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_person_contact, null);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SortCareShops();
        init();
        initEdit();
    }

    private void initEdit() {
        searchName = (EditText) getView().findViewById(R.id.search_editor);

        searchName.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // 这个时候不需要挤压效果 就把他隐藏掉
                titleLayout.setVisibility(View.GONE);
                // titleLayout.setVisibility(View.GONE);
                // 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }
        });

    }

    private void init() {
        titleLayout = (LinearLayout) getView().findViewById(R.id.title_layout);
        title = (TextView) getView().findViewById(R.id.title_layout_catalog);
        tvNofriends = (TextView) getView().findViewById(
                R.id.title_layout_no_friends);
        letterSideBar = (LetterSideBar) getView().findViewById(R.id.sidrbar);
        dialog = (TextView) getView().findViewById(R.id.dialog);
        letterSideBar.setTextView(dialog);

        // 设置右侧触摸监听
        letterSideBar
                .setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

                    @Override
                    public void onTouchingLetterChanged(String s) {
                        // 该字母首次出现的位置
                        int position = adapter.getPositionForSection(s
                                .charAt(0));
                        if (position != -1) {
                            sortListView.setSelection(position);
                        }

                    }
                });

        sortListView = (ListView) getView().findViewById(R.id.person_contacts);
        sortListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // 这里要利用adapter.getItem(position)来获取当前position所对应的对象
                CareWho careWho = careWhos.get(position);
                httpRequestNewSession(careWho.getId(), careWho.getNickName(),
                        careWho.getId(), careWho.getPortrait(),
                        careWho.getGender());

            }
        });
        adapter = new SortGroupMemberAdapter(getActivity(), careWhos, false);
        sortListView.setAdapter(adapter);
        sortListView.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                // if (isFirstOnScroll) {
                // isFirstOnScroll = false;
                // return;
                // }
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
                    MarginLayoutParams params = (MarginLayoutParams) titleLayout
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
                        MarginLayoutParams params = (MarginLayoutParams) titleLayout
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

    public void SortCareShops() {
        copyCareWhos = new ArrayList<CareWho>();
        for (CareWho careWho : careWhos) {
            char firstchar = GetFirstLetter.getSpells(careWho.getNickName())
                    .toUpperCase().charAt(0);

            careWho.setFirstChar(String.valueOf(firstchar));
        }
        Collections.sort(careWhos);
        for (Iterator<CareWho> it = careWhos.iterator(); it.hasNext(); ) {
            copyCareWho = it.next();
            char firstchar = GetFirstLetter
                    .getSpells(copyCareWho.getNickName()).toUpperCase()
                    .charAt(0);
            Pattern p1 = Pattern.compile("[a-zA-Z]");
            Matcher m1 = p1.matcher(String.valueOf(firstchar));
            Pattern p2 = Pattern.compile("[\u4e00-\u9fa5]");
            Matcher m2 = p2.matcher(String.valueOf(firstchar));
            if (!m1.matches() && !m2.matches()) {
                it.remove();
                copyCareWhos.add(copyCareWho);
            }
        }
        careWhos.addAll(copyCareWhos);
    }

    @Override
    public void onResume() {

        Log.d("", "-------fuck:onResume");
        super.onResume();
        this.careWhos = Settings.instance().User.getCareWho();
        SortCareShops();
        adapter = new SortGroupMemberAdapter(getActivity(), careWhos, false);
        sortListView.setAdapter(adapter);
    }
    @Override
    public Object[] getSections() {
        // TODO Auto-generated method stub
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
        CareWho careWho = careWhos.get(position);
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

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
        List<CareWho> filterDateList = new ArrayList<CareWho>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = careWhos;
            tvNofriends.setVisibility(View.GONE);
        } else {
            filterDateList.clear();
            for (CareWho sortModel : careWhos) {
                String name = sortModel.getNickName();
                if (name.indexOf(filterStr.toString()) != -1
                        || characterParser.getSelling(name).startsWith(
                        filterStr.toString())) {
                    filterDateList.add(sortModel);
                }
            }
        }
        adapter.updateListView(filterDateList);
        if (filterDateList.size() == 0) {
            tvNofriends.setVisibility(View.VISIBLE);
        }

    }

    private void httpRequestNewSession(String targetUserId,
                                       final String NickName2, final String UserId2,
                                       final String senderPortrait, final String senderGender) {
        Param[] params = new Param[3];
        params[0] = new Param("UserId", Settings.instance().getUserId());
        params[1] = new Param("Token", Settings.instance().getToken());
        params[2] = new Param("TargetUserId", targetUserId);

        OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
                        + "/im/newsession", params,
                new OkHttpClientManager.ResultCallback<ResponseNewSession>() {

                    @Override
                    public void onError(Request request, Exception e) {
                        Logger.info("----NewSessionFail");
                    }

                    @Override
                    public void onResponse(ResponseNewSession response) {

                        if (response.isSuccess()) {
                            Intent intent = new Intent(getActivity(),
                                    ChatActivity.class);
                            intent.putExtra("NickName", Settings.instance()
                                    .getMyName());
                            intent.putExtra("NickName2", NickName2);
                            intent.putExtra("UserId", Settings.instance()
                                    .getUserId());
                            intent.putExtra("UserId2", UserId2);
                            intent.putExtra("Portrait2", senderPortrait);
                            intent.putExtra("SessionId",
                                    response.getSessionId());
                            intent.putExtra("Gender", senderGender);
                            intent.putExtra("isGroup", false);
                            getActivity().startActivity(intent);
                        } else {
                            Logger.info("----NewSession"
                                    + response.getErrorMessage());
                        }
                    }
                });
    }

    public void refreshData() {
        adapter.refresh();
    }

}
