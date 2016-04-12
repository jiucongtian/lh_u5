package com.longhuapuxin.u5;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.longhuapuxin.common.Logger;
import com.longhuapuxin.view.LetterView;
import com.longhuapuxin.view.LetterView.OnLetterChangeListener;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

public class CitiesActivity extends BaseActivity implements OnScrollListener, OnLetterChangeListener, OnItemClickListener {

    private ListView mListView;
    private CityListAdapter mCityAdapter;
    private View mOverlayLayout;
    private TextView mOverlayTv;
    private ListAlphabetIndexer mIndexer;

    private LetterView mLetterView;
    private List<City> mCitys;

    /**
     * 显示字母的Toast
     **/
    private Toast mToast;
    private TextView mToastTv;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cities);
        init();
    }

    private void init() {
        initHeader(R.string.title_current_city);

        mListView = (ListView) findViewById(R.id.listView);
        mListView.setOnItemClickListener(this);
        mLetterView = (LetterView) findViewById(R.id.letterView);
        mOverlayLayout = findViewById(R.id.overlayLayout);
        mOverlayTv = (TextView) findViewById(R.id.overlayTv);

        String jsonStr = readAssetFile("citys.json");
        Gson gson = new Gson();
        mCitys = gson.fromJson(jsonStr,
                new TypeToken<List<City>>() {
                }.getType());
        //创建Adapter
        mCityAdapter = new CityListAdapter(this, mCitys);
        mListView.setAdapter(mCityAdapter);

        mIndexer = new ListAlphabetIndexer(mCitys);

        //给ListView设置滚动监听
        mListView.setOnScrollListener(this);
        mLetterView.setOnLetterChangeListener(this);
    }

    /**
     * 读取Assets文件夹里面的文件
     *
     * @param path
     * @return
     */
    private String readAssetFile(String path) {
        AssetManager assetManager = getAssets();
        BufferedReader br = null;
        try {
            InputStream is = assetManager.open(path);
            br = new BufferedReader(new InputStreamReader(is));
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public class City {
        private String name;
        private String pinyin;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPinyin() {
            return pinyin;
        }

        public void setPinyin(String pinyin) {
            this.pinyin = pinyin;
        }

        public City(String name, String pinyin) {
            super();
            this.name = name;
            this.pinyin = pinyin;
        }

        public City() {
            super();
        }

    }

    public class ListAlphabetIndexer {
        private List<City> citys;

        public ListAlphabetIndexer(List<City> citys) {
            super();
            this.citys = citys;
        }

        /**
         * 根据位置返回对应的分组
         *
         * @param position
         * @return
         */
        public int getSectionFromPosition(int position) {
            City city = citys.get(position);
            return city.getPinyin().toUpperCase().charAt(0);
        }

        /**
         * 返回指定分组中的第一个item对应的position
         *
         * @param position
         * @return
         */
        public int getPositionFromSection(int section) {
            for (int i = 0; i < citys.size(); i++) {
                if (citys.get(i).getPinyin().toUpperCase().charAt(0) == section) {
                    return i;
                }
            }
            return -1;
        }

        /**
         * 判断指定位置的对象是否是分组的第一个元素
         *
         * @param position
         * @return
         */
        public boolean isGroupFirst(int position) {
            int section = getSectionFromPosition(position);
            return getPositionFromSection(section) == position;
        }
    }

    public class CityListAdapter extends BaseAdapter {
        private List<City> citys;
        private LayoutInflater inflater;
        private ListAlphabetIndexer indexer;

        public CityListAdapter(Context context, List<City> citys) {
            super();
            this.citys = citys;
            inflater = LayoutInflater.from(context);
            indexer = new ListAlphabetIndexer(citys);
        }

        @Override
        public int getCount() {
            return citys.size();
        }

        @Override
        public City getItem(int position) {
            return citys.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_city, null);
                holder = new ViewHolder();
                holder.nameTv = (TextView) convertView.findViewById(R.id.item_nameTv);
                holder.groupTv = (TextView) convertView.findViewById(R.id.item_groupTv);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            City city = citys.get(position);
            //设置分组TextView的显示或隐藏
            //获取当前position对应的分组
            int section = indexer.getSectionFromPosition(position);

            int firstPosition = indexer.getPositionFromSection(section);
            if (firstPosition == position) {
                holder.groupTv.setVisibility(View.VISIBLE);
                holder.groupTv.setText(city.getPinyin().toUpperCase().charAt(0) + "");
            } else {
                holder.groupTv.setVisibility(View.GONE);
                ;
            }

            holder.nameTv.setText(city.getName());
            return convertView;
        }

        class ViewHolder {
            TextView groupTv;
            TextView nameTv;
        }
    }

    @Override
    public void onLetterChange(int letter) {
        mListView.setSelection(mIndexer.getPositionFromSection(letter));
        //showToast((char)letter+"");
        //不加这行代码会出现效果上的bug
        mOverlayLayout.setPadding(0, 0, 0, 0);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
//		Log.d("View", "first="+firstVisibleItem+",visibleItemCount="+visibleItemCount+",totalItemCount="+totalItemCount);
        Logger.info("first=" + firstVisibleItem + ",visibleItemCount=" + visibleItemCount + ",totalItemCount=" + totalItemCount);
        if (mIndexer.isGroupFirst(firstVisibleItem + 1)) {
            View v = view.getChildAt(0);
            //由于调用mListView.setOnScrollListener会立即回调一次onScroll
            //这时候得不到child
            if (v == null) {
                return;
            }
            //获取视图底部距离父容器最顶部的距离
            int offset = v.getBottom() - mOverlayTv.getHeight();
            if (offset <= 0) {
                //为覆盖的那个容器设置内边距(负的内上边距)
                mOverlayLayout.setPadding(0, offset, 0, 0);
            }
        } else {
            mOverlayLayout.setPadding(0, 0, 0, 0);
        }
        mOverlayTv.setText((char) mIndexer.getSectionFromPosition(firstVisibleItem) + "");
        mLetterView.setSelectLetter(mIndexer.getSectionFromPosition(firstVisibleItem));

    }

    /**
     * Toast显示字母
     *
     * @param text
     */
    private void showToast(String text) {
        if (mToast == null) {
            mToast = new Toast(this);
            View view = LayoutInflater.from(this).inflate(R.layout.charactor_toast, null);
            mToast.setView(view);//设置视图
            mToast.setDuration(Toast.LENGTH_SHORT);
            mToastTv = (TextView) view.findViewById(R.id.toastTv);
            mToast.setGravity(Gravity.CENTER, 100, 100);
        }
        mToastTv.setText(text);
        mToast.show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {

        City city = mCitys.get(position);
        if (city != null) {
            Intent intent = new Intent();
            intent.putExtra("name", city.name);
            setResult(RESULT_CITY, intent);
            finish();
        } else {
            finish();
        }
    }
}
