package com.longhuapuxin.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.longhuapuxin.adapter.ProductAmountAdapter;
import com.longhuapuxin.adapter.U5BaseAdapter;
import com.longhuapuxin.common.ImageUrlLoader;
import com.longhuapuxin.common.Utils;
import com.longhuapuxin.entity.ResponseGetProducts;
import com.longhuapuxin.entity.ResponseGetProducts.Product;
import com.longhuapuxin.u5.BookProductDialogActivity;
import com.longhuapuxin.u5.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by ZH on 2016/1/5.
 * Email zh@longhuapuxin.com
 */
@SuppressLint("ValidFragment")
public class SubscribeProductFragment extends Fragment {

    @ViewInject(R.id.lvProducts)
    private ListView mProductsLv;

    private View mView;
    private BookProductDialogActivity mContext;
    private Map<Product, Integer> mBookedMap;
    private List<Product> mProductslist;
    private ProductAmountAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_subscribe_product, null);
        ViewUtils.inject(this, mView);
        init();
        return mView;
    }

    private void init() {
        mContext = (BookProductDialogActivity) getActivity();
        mBookedMap = mContext.getmBookedMap();
        mProductslist = new ArrayList<Product>();
        Iterator it = mBookedMap.keySet().iterator();
        while (it.hasNext()) {
            Product product = (Product) it.next();
            mProductslist.add(product);
        }
        mAdapter = new ProductAmountAdapter(mContext, mProductslist, mBookedMap);
        mProductsLv.setAdapter(mAdapter);
        mAdapter.notifyDataSetChangedWithImages();
    }

    @OnClick({R.id.tvCancel, R.id.tvNextStep})
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.tvCancel:
                mContext.cancel();
                break;
            case R.id.tvNextStep:
                mContext.nextStep();
                break;
        }

    }

//    public class ProductAmountAdapter extends U5BaseAdapter<Product> {
//
//        public ProductAmountAdapter(Context context, List<Product> list) {
//            super(context, list);
//        }
//
//        @Override
//        public String getImageId(Product item) {
//            return item.getPhotos();
//        }
//
//        @Override
//        public View getView(int i, View view, ViewGroup viewGroup) {
//            Product item = mDatas.get(i);
//            ViewHolder holder = null;
//            if (view == null) {
//                holder = new ViewHolder();
//                view = mInflater.inflate(R.layout.item_product_amount, null);
//                holder.name = (TextView) view.findViewById(R.id.tvName);
//                holder.photos = (ImageView) view.findViewById(R.id.ivProductPhoto);
//                holder.desc = (TextView) view.findViewById(R.id.tvDesc);
//                holder.price = (TextView) view.findViewById(R.id.tvPrice);
//                holder.unit = (TextView) view.findViewById(R.id.tvUnit);
//                holder.decrease = (ImageView) view.findViewById(R.id.ivDecrease);
//                holder.increase = (ImageView) view.findViewById(R.id.ivIncrease);
//                holder.amount = (TextView) view.findViewById(R.id.tvAmount);
//
//                view.setTag(holder);
//            } else {
//                holder = (ViewHolder) view.getTag();
//            }
//
//            holder.name.setText(item.getName());
//            holder.desc.setText(item.getDescription());
//            holder.price.setText(item.getPrice());
//            holder.unit.setText(item.getUnit());
//            bindImageView(holder.photos, Utils.getFirstId(item.getPhotos()));
//            holder.amount.setText(String.valueOf(mBookedMap.get(item)));
//
//            holder.decrease.setTag(item);
//            holder.increase.setTag(item);
//            holder.decrease.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Product obj = (Product) view.getTag();
//                    if(mBookedMap.containsKey(obj)) {
//                        int amount = mBookedMap.get(obj);
//                        if(amount > 0) {
//                            amount--;
//                        }
//                        mBookedMap.put(obj, amount);
//                    }
//                    mAdapter.notifyDataSetChanged();
//                }
//            });
//            holder.increase.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Product obj = (Product) view.getTag();
//                    if(mBookedMap.containsKey(obj)) {
//                        int amount = mBookedMap.get(obj);
//                        amount++;
//                        mBookedMap.put(obj, amount);
//                    }
//                    mAdapter.notifyDataSetChanged();
//                }
//            });
//
//
//            return view;
//        }
//        public class ViewHolder {
//            TextView price, unit, name, desc, amount;
//            ImageView photos, decrease, increase;
//        }
//    }
}
