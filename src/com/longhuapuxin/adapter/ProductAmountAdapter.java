package com.longhuapuxin.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.longhuapuxin.common.Utils;
import com.longhuapuxin.entity.ResponseGetProducts.Product;
import com.longhuapuxin.u5.R;

import java.util.List;
import java.util.Map;

/**
 * Created by ZH on 2016/1/15.
 * Email zh@longhuapuxin.com
 */
public class ProductAmountAdapter extends U5BaseAdapter<Product> {


    private Map<Product, Integer> mBookedMap;

    public ProductAmountAdapter(Context context, List<Product> list, Map<Product, Integer> map) {
        super(context, list);
        mBookedMap = map;
    }

    @Override
    public String getImageId(Product item) {
        return Utils.getFirstId(item.getPhotos());//item.getPhotos();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Product item = mDatas.get(i);
        ViewHolder holder = null;
        if (view == null) {
            holder = new ViewHolder();
            view = mInflater.inflate(R.layout.item_product_amount, null);
            holder.name = (TextView) view.findViewById(R.id.tvName);
            holder.photos = (ImageView) view.findViewById(R.id.ivProductPhoto);
            holder.desc = (TextView) view.findViewById(R.id.tvDesc);
            holder.price = (TextView) view.findViewById(R.id.tvPrice);
            holder.unit = (TextView) view.findViewById(R.id.tvUnit);
            holder.decrease = (ImageView) view.findViewById(R.id.ivDecrease);
            holder.increase = (ImageView) view.findViewById(R.id.ivIncrease);
            holder.amount = (TextView) view.findViewById(R.id.tvAmount);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.name.setText(item.getName());
        holder.desc.setText(item.getDescription());
        holder.price.setText(item.getPrice());
        holder.unit.setText(item.getUnit());
        bindImageView(holder.photos, Utils.getFirstId(item.getPhotos()));
        holder.amount.setText(String.valueOf(mBookedMap.get(item)));

        holder.decrease.setTag(item);
        holder.increase.setTag(item);
        holder.decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Product obj = (Product) view.getTag();
                if (mBookedMap.containsKey(obj)) {
                    int amount = mBookedMap.get(obj);
                    if (amount > 0) {
                        amount--;
                    }
                    mBookedMap.put(obj, amount);
                }
                notifyDataSetChanged();
                if (mRefreshMapLinstener != null) {
                    mRefreshMapLinstener.refreshMap();
                }
            }
        });
        holder.increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Product obj = (Product) view.getTag();
                if (mBookedMap.containsKey(obj)) {
                    int amount = mBookedMap.get(obj);
                    amount++;
                    mBookedMap.put(obj, amount);
                }
                notifyDataSetChanged();
                if (mRefreshMapLinstener != null) {
                    mRefreshMapLinstener.refreshMap();
                }
            }
        });


        return view;
    }

    public class ViewHolder {
        TextView price, unit, name, desc, amount;
        ImageView photos, decrease, increase;
    }

    public interface RefreshMapLinstener {
        public void refreshMap();
    }

    public RefreshMapLinstener mRefreshMapLinstener;

    public void setRefreshMapLinstener(RefreshMapLinstener mRefreshMapLinstener) {
        this.mRefreshMapLinstener = mRefreshMapLinstener;
    }
}