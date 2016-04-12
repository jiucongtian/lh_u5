package com.longhuapuxin.u5;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.longhuapuxin.adapter.U5BaseAdapter;
import com.longhuapuxin.common.Logger;
import com.longhuapuxin.common.OkHttpClientManager;
import com.longhuapuxin.common.Utils;
import com.longhuapuxin.entity.ResponseFilePath;
import com.longhuapuxin.entity.ResponseGetProducts;
import com.longhuapuxin.entity.ResponseGetProducts.Product;
import com.longhuapuxin.view.PullToRefreshListView;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ZH on 2016/1/5.
 * Email zh@longhuapuxin.com
 */
public class ProductListActivity extends BaseActivity implements PullToRefreshListView.IOnLoadMoreListener {

    public final static int BOOKED_PRODUCTS = 0x01;
    public final static int SHOP_CODE = 0x02;
    private final static int PAGE_SIZE = 20;
    private List<Product> mProductList;
    private Map<Product, Integer> mBookedMap;
    private PullToRefreshListView mProductLv;
    private ProductListAdapter mAdapter;
    private String mShopCode;
    private int mPageIndex;
    private String[] mPhotoUrls = null;

    @ViewInject(R.id.tv_order_amount)
    private TextView mAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        ViewUtils.inject(this);
        init();
        Intent intent = getIntent();
        mShopCode = intent.getStringExtra("shopCode");
        initHeader(intent.getStringExtra("shopName"));
        fetchProducts();
    }

    private void init() {
        mPageIndex = 1;
        mProductLv = (PullToRefreshListView) findViewById(R.id.lvProductList);
        mProductList = new ArrayList<Product>();
        mBookedMap = new HashMap<Product, Integer>();
        mAdapter = new ProductListAdapter(this, mProductList);
        mProductLv.setAdapter(mAdapter);
        mProductLv.setOnLoadMoreListener(this);
    }

    @Override
    public void OnLoadMore() {
        fetchProducts();
    }

    public class ProductListAdapter extends U5BaseAdapter<Product> {

        public ProductListAdapter(Context context, List<Product> list) {
            super(context, list);
        }

        @Override
        public String getImageId(Product product) {
            return product.getPhotos();
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            Product item = mDatas.get(i);
            ViewHolder holder = null;
            if (view == null) {
                holder = new ViewHolder();
                view = mInflater.inflate(R.layout.item_product, null);

                holder.name = (TextView) view.findViewById(R.id.tvProductName);
                holder.photos = (ImageView) view.findViewById(R.id.ivProduct);
                holder.photoCount = (TextView) view.findViewById(R.id.tvPhotoCount);
                holder.bookOrder = (TextView) view.findViewById(R.id.tvBookOrder);
                holder.desc = (TextView) view.findViewById(R.id.tvProductDesc);
                holder.price = (TextView) view.findViewById(R.id.tvPrice);
                holder.amount = (TextView) view.findViewById(R.id.tvAmount);
                holder.mark = (ImageView) view.findViewById(R.id.ivMark);

                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            holder.name.setText(item.getName());
            holder.desc.setText(item.getDescription());
            holder.price.setText(item.getPrice() + "/" + item.getUnit());
            holder.amount.setText("20");
            bindImageView(holder.photos, Utils.getFirstId(item.getPhotos()), true);
            holder.photos.setTag(i);
            holder.photos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Integer index = (Integer) view.getTag();
                    Product product = mDatas.get(index);
                    fetchPhotoUrls(product.getPhotos());
                }
            });


            String[] photos = item.getPhotos().split(",");
            holder.photoCount.setText(String.valueOf(photos.length));
            holder.bookOrder.setTag(i);

            if(mBookedMap.containsKey(item)) {

                holder.bookOrder.setTextColor(getResources().getColor(R.color.orange));
                holder.mark.setImageResource(R.drawable.shop_product_select_sel);
            } else {
                holder.bookOrder.setTextColor(getResources().getColor(R.color.deep_gray));
                holder.mark.setImageResource(R.drawable.shop_product_select);
            }
            holder.bookOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Integer index = (Integer) view.getTag();

                    Product product = mProductList.get(index);
                    if (mBookedMap.containsKey(product)) {
                        mBookedMap.remove(product);
                    } else {
                        mBookedMap.put(product, 1);
                    }
                    mAmount.setText(String.valueOf(mBookedMap.size()));
                    mAdapter.notifyDataSetChanged();
                }
            });

            return view;
        }

        public class ViewHolder {
            TextView price, amount, name, desc, bookOrder, photoCount;
            ImageView photos, mark;
        }
    }

    @OnClick(R.id.tv_book_order)
    public void onClick(View v) {

        Intent intent = new Intent(this, BookProductDialogActivity.class);
        putPram(BOOKED_PRODUCTS, mBookedMap);
        putPram(SHOP_CODE, mShopCode);
        startActivity(intent);
    }

    private void fetchProducts() {
        OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[5];
        params[0] = new OkHttpClientManager.Param("UserId", Settings.instance().getUserId());
        params[1] = new OkHttpClientManager.Param("Token", Settings.instance().getToken());
        params[2] = new OkHttpClientManager.Param("ShopCode", mShopCode);
        params[3] = new OkHttpClientManager.Param("PageIndex", String.valueOf(mPageIndex));
        params[4] = new OkHttpClientManager.Param("PageSize", String.valueOf(PAGE_SIZE));

        OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
                        + "/shop/getproducts", params,
                new OkHttpClientManager.ResultCallback<ResponseGetProducts>() {

                    @Override
                    public void onError(Request request, Exception e) {
                        Logger.info("getproducts.onError"
                                + e.toString());
                    }

                    @Override
                    public void onResponse(ResponseGetProducts response) {
                        Logger.info("onResponse is: "
                                + response.toString());

                        if (response.isSuccess()) {
                            List<Product> products = response.getProducts();

                            if (products.size() < PAGE_SIZE) {
                                mProductLv.onLoadMoreComplete(true);
                            } else {
                                mProductLv.onLoadMoreComplete(false);
                                mPageIndex++;
                            }

                            mProductList.addAll(response.getProducts());
                            mAdapter.notifyDataSetChangedWithImages();
                        }
                    }

                });
    }


    private void fetchPhotoUrls(String ids) {
        if (!TextUtils.isEmpty(ids)) {
            OkHttpClientManager.Param[] params = new OkHttpClientManager.Param[3];
            params[0] = new OkHttpClientManager.Param("UserId", Settings.instance().getUserId());
            params[1] = new OkHttpClientManager.Param("Token", Settings.instance().getToken());
            params[2] = new OkHttpClientManager.Param("FileId", ids);

            OkHttpClientManager.postAsyn(Settings.instance().getApiUrl() + "/basic/getphoto", params, new OkHttpClientManager.ResultCallback<ResponseFilePath>() {

                @Override
                public void onError(Request request, Exception e) {
                    Logger.info("load image onError: " + e);
                }

                @Override
                public void onResponse(ResponseFilePath response) {
                    Logger.info("load image error code is: " + response.getErrorCode());
                    Logger.info("load image error msg is: " + response.getErrorMessage());
                    if (response.isSuccess()) {
                        if (response.getFiles().size() > 0) {
                            List<String> tmpUrls = new ArrayList<String>();
                            for (ResponseFilePath.FileObject object : response.getFiles()) {
                                tmpUrls.add(object.getOriginal());
                            }
                            int size = tmpUrls.size();
                            String[] photoUrls = (String[]) tmpUrls.toArray(new String[size]);
                            ShowGalleryActivity(photoUrls, 0);
                        }
                    }
                }

            });
        }
    }
}
