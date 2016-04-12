package com.longhuapuxin.u5;

import android.annotation.SuppressLint;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.longhuapuxin.adapter.ContactAdapter;
import com.longhuapuxin.common.CharacterParser;
import com.longhuapuxin.common.ImageUrlLoader;
import com.longhuapuxin.common.Logger;
import com.longhuapuxin.common.OkHttpClientManager;
import com.longhuapuxin.common.WaitDialog;
import com.longhuapuxin.db.bean.ContactBean;
import com.longhuapuxin.entity.ResponseCare;
import com.longhuapuxin.entity.ResponseDad;
import com.longhuapuxin.entity.ResponseFindoutFromAddressBook;
import com.longhuapuxin.entity.ResponseGetAccount;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lsy on 2016/1/21.
 */
public class FindFromaddressActivity extends BaseActivity {
    private AsyncQueryHandler asyncQueryHandler; // 异步查询数据库类对象
    private CharacterParser characterParser = new CharacterParser();
    private List<ContactBean> list;
    private Map<Integer, String> contactIdMap = null;
    private final static  int MSG_Findout=1;
    private final  static int MSG_ShowList=2;
    private final  static int MSG_Care=3;
    private EditText searchName;
    private ListView person_contacts;
    private TextView title_layout_no_friends;
    private ContactAdapter adapter;
    @SuppressLint("HandlerLeak")
    private  Handler handler=new Handler() {
        @SuppressLint("HandlerLeak")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_Findout:
                    FindOutFromAddressBok(msg.obj.toString());
                    break;
                case MSG_ShowList:
                    setAdapter();
                    break;
                case MSG_Care:
                    CarePerson((ContactBean) msg.obj);
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findfromaddress);
        person_contacts=(ListView) findViewById(R.id.person_contacts);
        title_layout_no_friends=(TextView) findViewById(R.id.title_layout_no_friends);

        init() ;


    }
    private  void init(){
        initHeader(R.string.findOutFromAddress);
        asyncQueryHandler = new MyAsyncQueryHandler(getContentResolver());
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI; // 联系人Uri；
        // 查询的字段
        String[] projection = { ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.DATA1, "sort_key",
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.PHOTO_ID,
                ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY };
        // 按照sort_key升序查詢
        asyncQueryHandler.startQuery(0, null, uri, projection, null, null,
                "sort_key COLLATE LOCALIZED asc");
        searchName = (EditText) findViewById(R.id.search_editor);

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
                title_layout_no_friends.setVisibility(View.GONE);
                // titleLayout.setVisibility(View.GONE);
                // 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }
        });


    }

    private void filterData(String filterStr) {
        List<ContactBean> filterDateList = new ArrayList<ContactBean>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = list;

        } else {
            filterDateList.clear();
            for (ContactBean sortModel : list) {
                String name = sortModel.getNickName();
                if (name.indexOf(filterStr.toString()) != -1
                        || sortModel.getSpell().startsWith(
                        filterStr.toString())) {
                    filterDateList.add(sortModel);
                }
            }
        }
        Collections.sort(filterDateList);
        adapter.updateListView(filterDateList);
        title_layout_no_friends.setVisibility(list.size() == 0 ? View.VISIBLE : View.GONE);

    }
    private void FindOutFromAddressBok(String addressbooks) {
        WaitDialog.instance().showWaitNote(this);
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[3];
        params[0] = new OkHttpClientManager.Param("UserId", Settings.instance().getUserId());
        params[1] = new OkHttpClientManager.Param("Token", Settings.instance().getToken());
        params[2] = new OkHttpClientManager.Param("AddressBook", addressbooks);

        OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
                        + "/im/FindOutFromAddressBook", params,
                new OkHttpClientManager.ResultCallback<ResponseFindoutFromAddressBook>() {

                    @Override
                    public void onError(Request request, Exception e) {
                        Logger.info("----NewSessionFail");
                        WaitDialog.instance().hideWaitNote();
                    }

                    @Override
                    public void onResponse(ResponseFindoutFromAddressBook response) {

                        if (response.isSuccess()) {
                            list = new ArrayList<ContactBean>();

                            if(response.getUsers()!=null) {
                                String[] lines = response.getUsers().split(";");
                                for (String line : lines) {
                                    if(line.isEmpty() || line.equals("")) continue;
                                    String[] parts = line.split(",");
                                    ContactBean bean = new ContactBean();
                                    bean.setPhone(parts[0]);
                                    bean.setUserId(Integer.valueOf(parts[1]));
                                    if(parts[0]!=null &!parts[2].equals("")){
                                        bean.setPortrait(Integer.valueOf(parts[2]));

                                    }
                                    bean.setSpell(characterParser.getSelling(parts[4].trim()));
                                    bean.setCared(parts[3].equals("Y"));
                                    bean.setNickName(parts[4]);
                                    list.add(bean);

                                }
                            }
                            Collections.sort(list);
                            setAdapter();

                        } else {
                            Logger.info("----NewSession"
                                    + response.getErrorMessage());
                        }
                        WaitDialog.instance().hideWaitNote();
                    }
                });
    }
    private class MyAsyncQueryHandler extends AsyncQueryHandler {
        public MyAsyncQueryHandler(ContentResolver cr) {
            super(cr);
        }
        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            if (cursor != null && cursor.getCount() > 0) {
                contactIdMap = new HashMap<Integer, String>();

                String addressBooks="";
                cursor.moveToFirst(); // 游标移动到第一项
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToPosition(i);
                    String name = cursor.getString(1);
                    String number = cursor.getString(2);
                    String sortKey = cursor.getString(3);
                    int contactId = cursor.getInt(4);
                    Long photoId = cursor.getLong(5);
                    String lookUpKey = cursor.getString(6);
                    if(number.isEmpty()) continue;
                    number=number.replace(" ","").replace("(+86)","").replace("+86","");

                    if (contactIdMap.containsKey(contactId)) {
                        // 无操作
                    } else {
                        if(i>0){
                            addressBooks+=",";
                        }
                        addressBooks+=number;
                        contactIdMap.put(contactId, number);
                    }

                }
                Message msg=new Message();
                msg.what=MSG_Findout;
                msg.obj=addressBooks;
                handler.sendMessage(msg);
            }

            super.onQueryComplete(token, cookie, cursor);
        }

    }
    ContactAdapter.OnClickListner listener=new ContactAdapter.OnClickListner(){

        @Override
        public void OnClick(ContactBean item) {
            Message msg=new Message();
            msg.what=MSG_Care;
            msg.obj=item;
            handler.sendMessage(msg);
        }
    };

    private void CarePerson(final ContactBean bean){
        WaitDialog.instance().showWaitNote(this);
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[6];
        params[0] = new OkHttpClientManager.Param("UserId", Settings.instance().getUserId());
        params[1] = new OkHttpClientManager.Param("Token", Settings.instance().getToken());
        params[2] = new OkHttpClientManager.Param("TargetUserId", String.valueOf(bean.getUserId()));
        params[3] = new OkHttpClientManager.Param("TargetName", bean.getNickName());
        params[4] = new OkHttpClientManager.Param("LabelCode", "");
        params[5] = new OkHttpClientManager.Param("LabelName", "");


        OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
                        + "/label/careperson", params,
                new OkHttpClientManager.ResultCallback<ResponseCare>() {

                    @Override
                    public void onError(Request request, Exception e) {
                        Logger.info("----NewSessionFail");
                        WaitDialog.instance().hideWaitNote();
                    }

                    @Override
                    public void onResponse(ResponseCare response) {

                        if (response.isSuccess()) {
                            List<ResponseGetAccount.User.CareWho> who = response.getCareWho();

                            List<ResponseGetAccount.User.CareWho> careWho = Settings.instance().User.getCareWho();
                            careWho.clear();
                            careWho.addAll(who);
                            bean.setCared(true);
                            adapter.notifyDataSetChangedWithImages();
                        } else {
                            Logger.info("----NewSession"
                                    + response.getErrorMessage());
                        }
                        WaitDialog.instance().hideWaitNote();
                    }
                });
    }

    private void setAdapter() {
        adapter=new ContactAdapter(this.getBaseContext(), list,listener);

        person_contacts.setAdapter(adapter);
        if(list.size()==0){
            title_layout_no_friends.setVisibility(View.VISIBLE);
        }
        else{
            title_layout_no_friends.setVisibility(View.GONE);
        }
        adapter.notifyDataSetChangedWithImages();
    }


}
