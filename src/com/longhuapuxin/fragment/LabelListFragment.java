package com.longhuapuxin.fragment;

import java.util.ArrayList;
import java.util.List;
import com.longhuapuxin.adapter.U5BaseAdapter;
import com.longhuapuxin.common.ImageUrlLoader;
import com.longhuapuxin.common.Logger;
import com.longhuapuxin.common.OkHttpClientManager;
import com.longhuapuxin.common.Utils;
import com.longhuapuxin.common.OkHttpClientManager.Param;
import com.longhuapuxin.entity.ResponsePhoto;
import com.longhuapuxin.entity.ResponseSearchLabel;
import com.longhuapuxin.entity.ResponseSearchLabel.User;
import com.longhuapuxin.u5.LabelDetailActivity;
import com.longhuapuxin.u5.R;
import com.longhuapuxin.u5.Settings;
import com.longhuapuxin.u5.U5Application;
import com.longhuapuxin.view.PullToRefreshListView;
import com.longhuapuxin.view.PullToRefreshListView.IOnLoadMoreListener;
import com.longhuapuxin.view.RoundCornerImageView;
import com.squareup.okhttp.Request;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

@SuppressLint("NewApi")
public class LabelListFragment extends Fragment implements IOnLoadMoreListener {

    private static final int DATA_PAGE_SIZE = 20;
    //	private static final int CURRENT_CITY = -2;
    private static final int WHOLE_COUNTRY = -1;
//	private static final int TEN_KM = 10;

    private PullToRefreshListView mListView;
    private SearchListAdapter mAdapter;
    private List<User> mContentList;
    private int mDiatance = WHOLE_COUNTRY, mCurrentIndex = 1;
    private String mSearchText = "", mLabelName = "";
    private View mNoDataContainer;
    private List<ResponsePhoto.Photo> mPhotos;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lable_list, null);
        mContentList = new ArrayList<User>();
        init(view);
        return view;
    }

    public void reFetchUserDate(String words, String labelName, int searchType) {
        if (null != words) {
            mSearchText = words;
        }

        if (null != labelName) {
            mLabelName = labelName;
        }

        mDiatance = searchType;
        mCurrentIndex = 1;
        mContentList.clear();
        mAdapter.notifyDataSetChanged();
        fetchUserViaLabel();
    }

    private void init(View view) {
        mListView = (PullToRefreshListView) view.findViewById(R.id.searchListView);
        mAdapter = new SearchListAdapter(getActivity(), mContentList);

        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(mAdapter);
        mListView.setOnLoadMoreListener(this);
        mNoDataContainer = view.findViewById(R.id.noDataNote);
    }

    private void checkNoData() {
        if (mContentList.size() == 0) {
            mNoDataContainer.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.INVISIBLE);
        } else {
            mNoDataContainer.setVisibility(View.INVISIBLE);
            mListView.setVisibility(View.VISIBLE);
        }
    }

    private void fetchUserViaLabel() {
        Param[] params = new Param[10];
        params[0] = new Param("UserId", Settings.instance().getUserId());
        params[1] = new Param("Token", Settings.instance().getToken());
        params[2] = new Param("LabelName", mLabelName);
        params[3] = new Param("PageIndex", String.valueOf(mCurrentIndex));
        params[4] = new Param("PageSize", String.valueOf(DATA_PAGE_SIZE));
        params[5] = new Param("Distance", String.valueOf(mDiatance));
        params[6] = new Param("CityCode", "");//String.valueOf(Settings.instance().getCityCode()));
        params[7] = new Param("Word", mSearchText);
        params[8] = new Param("Longitude", String.valueOf(Settings.instance().getLontitude()));
        params[9] = new Param("Latitude", String.valueOf(Settings.instance().getLatitude()));

        OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
                        + "/label/searchlabel", params,
                new OkHttpClientManager.ResultCallback<ResponseSearchLabel>() {

                    @Override
                    public void onError(Request request, Exception e) {
                        Logger.info("ShopFragment.fetchTopLabels.onError"
                                + e.toString());

                        mAdapter.notifyDataSetChanged();
                        checkNoData();
                    }

                    @Override
                    public void onResponse(ResponseSearchLabel response) {
                        Logger.info("onResponse is: "
                                + response.toString());

                        if (response.isSuccess()) {

                            if (response.getUsers().size() < DATA_PAGE_SIZE) {
                                mListView.onLoadMoreComplete(true);
                            } else {
                                mListView.onLoadMoreComplete(false);
                                mCurrentIndex++;
                            }
                            mPhotos=response.getFiles();
                            mContentList.addAll(response.getUsers());

                            mAdapter.notifyDataSetChangedWithImages();
                        }

                        mAdapter.notifyDataSetChanged();
                        checkNoData();
                    }

                });
    }

    public class PhotoAdapter extends U5BaseAdapter<ResponsePhoto.Photo> implements OnItemClickListener {

        private User mUser;

        public void  setUser(User user){
            mUser=user;
        }

        public PhotoAdapter(Context context, List<ResponsePhoto.Photo> list) {
            super(context, list);
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if(i>=mDatas.size()) {
                return  null;
            }
            ResponsePhoto.Photo photo=mDatas.get(i);

            if(view==null){
                view=  mInflater.inflate(R.layout.item_image_label,
                        viewGroup, false);
                LinearLayout lyt=(LinearLayout)view.findViewById(R.id.lyt);

                lyt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((U5Application) getActivity().getApplication()).putParam(U5Application.USER_DETAIL, mUser);
                        Intent intent = new Intent(getActivity(),
                                LabelDetailActivity.class);
                        startActivity(intent);
                    }
                });
            }
            ImageView  img=(ImageView)view.findViewById(R.id.img);

            bmpUtils.display(img, Settings.instance().getImageUrl() + photo.getSmallFileName());

            return view;
        }


        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            ((U5Application) getActivity().getApplication()).putParam(U5Application.USER_DETAIL, mUser);

            Intent intent = new Intent(getActivity(),
                    LabelDetailActivity.class);
            startActivity(intent);

        }
    }

    public class SearchListAdapter extends U5BaseAdapter<User> implements OnItemClickListener {

        public SearchListAdapter(Context context, List<User> list) {
            super(context, list);
        }

        @Override
        public String getImageId(User item) {
            return item.getPortrait();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (position >= mDatas.size()) {
                return null;
            }

            User obj = mDatas.get(position);
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();

                convertView = mInflater.inflate(R.layout.item_search,
                        parent, false);
                viewHolder.photoView = (RoundCornerImageView) convertView.findViewById(R.id.imageView1);
                viewHolder.sexView = (ImageView) convertView.findViewById(R.id.iv_sex);
                viewHolder.name = (TextView) convertView.findViewById(R.id.itemName);
                viewHolder.diatance = (TextView) convertView.findViewById(R.id.itemDistance);
                viewHolder.labelName = (TextView) convertView.findViewById(R.id.itemLabelName);
                viewHolder.price = (TextView) convertView.findViewById(R.id.itemPrice);
                viewHolder.note = (TextView) convertView.findViewById(R.id.itemDescript);
                viewHolder.age = (TextView) convertView.findViewById(R.id.tv_age);
                viewHolder.status = (TextView) convertView.findViewById(R.id.tv_online);
                viewHolder.txtNoPhoto=(TextView)convertView.findViewById(R.id.txtNoPhoto);
                viewHolder.gridPhotos=(com.longhuapuxin.view.MyGridView)convertView.findViewById(R.id.gridPhotos);
                viewHolder.gridPhotos.setSelector(new ColorDrawable(Color.TRANSPARENT));
                viewHolder.txtIdNo=(TextView)convertView.findViewById(R.id.txtIdNo);
                viewHolder.txtBankAccount=(TextView)convertView.findViewById(R.id.txtBankAccount);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
//			bindImageView(viewHolder.photoView, obj.getPortrait());
            viewHolder.name.setText(obj.getNickName());
            if (obj.getStatus() == null || obj.getStatus().equals("1")) {
                viewHolder.status.setText("在线");
                viewHolder.status.setTextAppearance(mContext, R.style.normal_green);
            } else if (obj.getStatus().equals("3")) {
                viewHolder.status.setText("忙碌");
                viewHolder.status.setTextAppearance(mContext, R.style.normal_orange);
            }
            Double shopLatitude = obj.getLatitude();
            Double shopLongitude = obj.getLongitude();
            Float myFloatLatitude = Settings.instance().getLatitude();
            Float myFlostLongitude = Settings.instance().getLontitude();
            if(obj.getIdNo()==null || obj.getIdNo().equals("")){
                viewHolder.txtIdNo.setText("未验证");
            }
            else{
                viewHolder.txtIdNo.setText("已验证");
            }
            if(obj.getBankAccount()==null || obj.getBankAccount().equals("")){
                viewHolder.txtBankAccount.setText("未验证");
            }
            else{
                viewHolder.txtBankAccount.setText("已验证");
            }
            if (shopLatitude != null && shopLongitude != null &&
                    myFloatLatitude != null && myFlostLongitude != null) {

                Double myDoubleLatitude = Double.parseDouble(Float.toString(myFloatLatitude));
                Double myDoubleLongitude = Double.parseDouble(Float.toString(myFlostLongitude));
                String distance = Utils.getStrDistance(myDoubleLongitude, myDoubleLatitude, shopLongitude, shopLatitude);
                viewHolder.diatance.setText(String.format(mContext.getString(R.string.shop_list_diatance), distance));
            } else {
                viewHolder.diatance.setText(R.string.notSure);
            }

            viewHolder.labelName.setText(obj.getLabelName());
            viewHolder.price.setText(obj.getGuidePrice());

            if (TextUtils.isEmpty(obj.getNote())) {
                viewHolder.note.setText(R.string.noDescript);
            } else {
                viewHolder.note.setText(obj.getNote());
            }

            viewHolder.age.setText(String.valueOf(Utils.getAgeByBirthday(obj.getBirthday())));

            int sexIcon;
            int defaultPortraitIcon;
            switch (Integer.valueOf(obj.getGender())) {
                case 0:
                    sexIcon = R.drawable.label_sex_unknown;
                    defaultPortraitIcon = R.drawable.avatar_default_secrecy;
                    break;
                case 1:
                    sexIcon = R.drawable.label_sex_man;
                    defaultPortraitIcon = R.drawable.avatar_default_man;
                    break;
                case 2:
                    sexIcon = R.drawable.label_sex_woman;
                    defaultPortraitIcon = R.drawable.avatar_default_woman;
                    break;
                default:
                    sexIcon = R.drawable.label_sex_man;
                    defaultPortraitIcon = R.drawable.avatar_default_man;
                    break;
            }
            viewHolder.sexView.setImageResource(sexIcon);


            String portraitId = obj.getPortrait();

            if (TextUtils.isEmpty(portraitId)) {
                viewHolder.photoView.setImageResource(defaultPortraitIcon);
            } else {
                bindImageView(viewHolder.photoView, obj.getPortrait());
            }
            if(obj.getPhotos()!=null &! obj.getPhotos().equals("")){
                List<ResponsePhoto.Photo> photos=new ArrayList<ResponsePhoto.Photo>();
                String[] ids=obj.getPhotos().split(",");
                for(String id :ids){
                    for(int i=0;i<mPhotos.size();i++){
                        if(mPhotos.get(i).getId().equals(id)){
                            photos.add(mPhotos.get(i));
                            break;
                        }
                    }
                }
                PhotoAdapter adapter=new PhotoAdapter(mContext,photos);
                viewHolder.gridPhotos.setAdapter(adapter);
                adapter.setUser(obj);
                viewHolder.gridPhotos.setVisibility(View.VISIBLE);

                viewHolder.txtNoPhoto.setVisibility(View.GONE);

                adapter.notifyDataSetChanged();
            }
            else{
                viewHolder.gridPhotos.setVisibility(View.GONE);
                viewHolder.txtNoPhoto.setVisibility(View.VISIBLE);
            }
            return convertView;
        }

        public class ViewHolder {
            public ImageView  sexView;
            public RoundCornerImageView photoView;
            public TextView name, diatance, labelName, price, note, age, status,txtNoPhoto,txtIdNo,txtBankAccount;
            public com.longhuapuxin.view.MyGridView gridPhotos;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            User obj = (User) parent.getAdapter().getItem(position);
            ((U5Application) getActivity().getApplication()).putParam(U5Application.USER_DETAIL, obj);

            Intent intent = new Intent(getActivity(),
                    LabelDetailActivity.class);
            startActivity(intent);

        }

    }

    @Override
    public void OnLoadMore() {
        fetchUserViaLabel();
    }
}
