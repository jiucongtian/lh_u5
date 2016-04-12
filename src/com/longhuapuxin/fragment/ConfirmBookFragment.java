package com.longhuapuxin.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.longhuapuxin.adapter.U5BaseAdapter;
import com.longhuapuxin.dao.MyAddressDao;
import com.longhuapuxin.db.bean.MyAddress;
import com.longhuapuxin.u5.BookProductDialogActivity;
import com.longhuapuxin.u5.MyAddressActivity;
import com.longhuapuxin.u5.R;
import com.longhuapuxin.view.HorizontalListView;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ZH on 2016/1/6.
 * Email zh@longhuapuxin.com
 */
public class ConfirmBookFragment extends Fragment implements AdapterView.OnItemClickListener {

    public enum PopStatus {
        DATE,
        ADDRESS,
        NONE
    }

    private static int MAX_DAY_COUNT = 7;

    @ViewInject(R.id.timeSelectorContainer)
    private View mTimeSelector;

    @ViewInject(R.id.addressSelectorContainer)
    private View mAddressSelector;

    @ViewInject(R.id.gvTime)
    private GridView mTimeGv;

    @ViewInject(R.id.hlvDate)
    private HorizontalListView mDateLv;

    @ViewInject(R.id.tvDate)
    private TextView mDateTv;

    @ViewInject(R.id.tvNote)
    private EditText mNoteTv;

    @ViewInject(R.id.lvAddress)
    private ListView mAddressLv;

    @ViewInject(R.id.tvAddress)
    private TextView mAddressTv;

    private BookProductDialogActivity mContext;
    private View mView;
    private Boolean mIsTimeShow;
    private int mDateIndex;
    private List<Date> mDateList;
    private List<MyAddress> mMyAddress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_confirm_order, null);
        ViewUtils.inject(this, mView);
        init();
        return mView;
    }

    private void init() {
        mIsTimeShow = false;
        mDateIndex = 0;
        mDateList = new ArrayList<Date>();
        mContext = (BookProductDialogActivity) getActivity();

        MyAddressDao dao = new MyAddressDao(mContext);
        mMyAddress = dao.fetchAll();

        AddressAdapter adapter = new AddressAdapter(mContext, mMyAddress);
        mAddressLv.setAdapter(adapter);
        mAddressLv.setOnItemClickListener(this);
    }

    @OnClick({R.id.tvCancel, R.id.addressContainer, R.id.timeContainer, R.id.tvConfirmOrder, R.id.setupAddress})
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.tvCancel:
                mContext.cancel();
                break;
            case R.id.tvConfirmOrder:
                mContext.mOrder.address = (String) mAddressTv.getText();
                mContext.mOrder.note = mNoteTv.getText().toString();

                DateFormat df = new SimpleDateFormat("yy-MM-dd HH:mm");
                Calendar cal = Calendar.getInstance();
                try {
                    cal.setTime(df.parse((String) mDateTv.getText()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                mContext.mOrder.fromDate = cal.getTime();
                cal.add(Calendar.HOUR, 1);
                mContext.mOrder.toDate = cal.getTime();

                mContext.confirmOrder();
                break;
            case R.id.timeContainer:
                switchPopupStatus(PopStatus.DATE);
                break;
            case R.id.addressContainer:
                switchPopupStatus(PopStatus.ADDRESS);
                break;
            case R.id.setupAddress:
                Intent intent = new Intent(getActivity(), MyAddressActivity.class);
                startActivity(intent);
                break;
        }

    }

    private void switchPopupStatus(PopStatus status) {
        switch (status) {
            case DATE:
                showTimeSelector(true);
                showAddress(false);
                hideSystemKeyBoard(mContext, mNoteTv);
                break;
            case ADDRESS:
                showTimeSelector(false);
                showAddress(true);
                hideSystemKeyBoard(mContext, mNoteTv);
                break;
            case NONE:
            default:
                showTimeSelector(false);
                showAddress(false);
                break;
        }
    }

    public static void hideSystemKeyBoard(Context mcontext,View v) {
        InputMethodManager imm = (InputMethodManager) mcontext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private void showTimeSelector(Boolean show) {
        if (show) {
            mTimeSelector.setVisibility(View.VISIBLE);
            renderTimeSelector();
        } else {
            mTimeSelector.setVisibility(View.GONE);
        }
    }

    private void showAddress(Boolean show) {
        if (show) {
            mAddressSelector.setVisibility(View.VISIBLE);
        } else {
            mAddressSelector.setVisibility(View.GONE);
        }
    }

    private void renderTimeSelector() {
        if(!mIsTimeShow) {
            renderDateView();
            renderTimeView();
            mIsTimeShow = true;
        }
//        mDateIndex = 0;
    }

    private void renderDateView() {
        String[] weekDays = new String[] {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};

        DateFormat df = new SimpleDateFormat("MM-dd");
        Calendar cal = Calendar.getInstance();
        String[] date = new String[MAX_DAY_COUNT];
        String[] title = new String[MAX_DAY_COUNT];
        title[0] = "今天";
        title[1] = "明天";
        title[2] = "后天";
        for(int i = 0; i < MAX_DAY_COUNT; i++) {
            mDateList.add(cal.getTime());
            if(i >= 3) {
                int dayOfWeek =cal.get(Calendar.DAY_OF_WEEK) - 1;
                if (dayOfWeek < 0) {
                    dayOfWeek = 0;
                }
                title[i] = weekDays[dayOfWeek];
            }
            date[i] = df.format(cal.getTime());
            cal.add(Calendar.DATE, 1);
        }

        List<DateItem> listItems = new ArrayList<DateItem>();

        for(int i = 0; i < title.length; i++) {
            DateItem item = new DateItem();
            item.title = title[i];
            item.date = date[i];
            listItems.add(item);
        }

        DateAdapter adapter = new DateAdapter(mContext, listItems);
        mDateLv.setAdapter(adapter);
        mDateLv.setOnItemClickListener(this);
    }

    private void renderTimeView() {
        String[] array = getResources().getStringArray(R.array.timeSelector);

        List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
        for(String item : array) {
            Map<String, Object> listItem = new HashMap<String, Object>();
            listItem.put("text", item);
            listItems.add(listItem);
        }

        SimpleAdapter adapter = new SimpleAdapter(mContext, listItems,
                R.layout.item_simple, new String[]{"text"}, new int[]{R.id.tvItemText});
        mTimeGv.setAdapter(adapter);
        mTimeGv.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (adapterView == mDateLv) {
            mDateIndex = i;
            BaseAdapter test = (BaseAdapter)adapterView.getAdapter();
            test.notifyDataSetChanged();
        } else if (adapterView == mTimeGv) {
            Date date = mDateList.get(mDateIndex);
            DateFormat df = new SimpleDateFormat("yy-MM-dd");
            String txt = df.format(date) + " ";
            Map<String, String> obj = (Map<String, String>) adapterView.getAdapter().getItem(i);
            txt += obj.get("text");
            mDateTv.setText(txt);
            switchPopupStatus(PopStatus.NONE);
        } else if (adapterView == mAddressLv) {
            String address = "";
            MyAddress myAddress = mMyAddress.get(i);
            address = myAddress.getAddress() + myAddress.getDetailAddress();
            mAddressTv.setText(address);
            switchPopupStatus(PopStatus.NONE);
        }
    }

    public class DateItem {
        public String title;
        public String date;
    }

    public class DateAdapter extends U5BaseAdapter<DateItem> {

        public DateAdapter(Context context, List<DateItem> list) {
            super(context, list);
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            DateItem obj = mDatas.get(i);
            ViewHolder viewHolder = null;
            if (view == null) {
                viewHolder = new ViewHolder();

                view = mInflater.inflate(R.layout.item_service_date,
                        viewGroup, false);
                viewHolder.titleTv = (TextView) view.findViewById(R.id.tvTitle);
                viewHolder.dateTv = (TextView) view.findViewById(R.id.tvDate);
                viewHolder.bg = view.findViewById(R.id.container);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            viewHolder.titleTv.setText(obj.title);
            viewHolder.dateTv.setText(obj.date);

            if(mDateIndex == i) {
                viewHolder.bg.setBackgroundResource(R.drawable.shop_content_date_orange);
                viewHolder.titleTv.setTextAppearance(mContext, R.style.normal_orange);
                viewHolder.dateTv.setTextAppearance(mContext, R.style.normal_orange);
            } else {
                viewHolder.bg.setBackgroundResource(R.drawable.shop_content_date_gray);
                viewHolder.titleTv.setTextAppearance(mContext, R.style.normal_deep_gray);
                viewHolder.dateTv.setTextAppearance(mContext, R.style.normal_deep_gray);
            }

            return view;
        }

        public class ViewHolder {
            public TextView titleTv, dateTv;
            public View bg;
        }
    }

    public class AddressAdapter extends U5BaseAdapter<MyAddress> {

        public AddressAdapter(Context context, List<MyAddress> list) {
            super(context, list);
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            MyAddress obj = mDatas.get(i);
            ViewHolder viewHolder = null;
            if (view == null) {
                viewHolder = new ViewHolder();

                view = mInflater.inflate(R.layout.item_address,
                        viewGroup, false);
                viewHolder.name = (TextView) view.findViewById(R.id.tvAddressName);
                viewHolder.phone = (TextView) view.findViewById(R.id.tvAddressPhone);
                viewHolder.detail = (TextView) view.findViewById(R.id.tvAddressDetail);
                viewHolder.bg = view.findViewById(R.id.addressContainer);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            viewHolder.name.setText(obj.getName());
            viewHolder.phone.setText(obj.getPhone());
            viewHolder.detail.setText(obj.getAddress() + obj.getDetailAddress());
            viewHolder.name.setTextAppearance(mContext, R.style.normal_deep_gray);
            viewHolder.phone.setTextAppearance(mContext, R.style.normal_deep_gray);
            viewHolder.detail.setTextAppearance(mContext, R.style.normal_deep_gray);
            viewHolder.bg.setBackgroundResource(R.color.white);

//            if(obj.isDefault()) {
//                viewHolder.name.setTextAppearance(mContext, R.style.normal_white);
//                viewHolder.phone.setTextAppearance(mContext, R.style.normal_white);
//                viewHolder.detail.setTextAppearance(mContext, R.style.normal_white);
//                viewHolder.bg.setBackgroundResource(R.color.shallow_gray);
//            } else {
//                viewHolder.name.setTextAppearance(mContext, R.style.normal_deep_gray);
//                viewHolder.phone.setTextAppearance(mContext, R.style.normal_deep_gray);
//                viewHolder.detail.setTextAppearance(mContext, R.style.normal_deep_gray);
//                viewHolder.bg.setBackgroundResource(R.color.white);
//            }

            return view;
        }

        public class ViewHolder {
            TextView name, phone, detail;
            View bg;
        }
    }
}
