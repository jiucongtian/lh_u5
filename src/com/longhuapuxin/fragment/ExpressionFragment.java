package com.longhuapuxin.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.longhuapuxin.adapter.U5BaseAdapter;
import com.longhuapuxin.entity.Expression.ExpressionNode;
import com.longhuapuxin.u5.R;
import com.longhuapuxin.u5.U5Application;
import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by ZH on 2016/1/26.
 * Email zh@longhuapuxin.com
 */
@SuppressLint("ValidFragment")
public class ExpressionFragment extends Fragment {

    public interface Expressionlistener {
        void onExperssionSelected(String code);
    }

    private static final int COLUNM = 6;
    private static final int ROW = 3;
    private ViewPager mVp;
    private List<ExpressionNode> mExpressionList;
    private Expressionlistener lintener;
    private LinearLayout mTipsBox;//tipsBox
    private int currentPage = 0;
    private ImageView[] tips;

    @SuppressLint("ValidFragment")
    public ExpressionFragment(Expressionlistener lintener) {
        this.lintener = lintener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expression, null);
        mVp = (ViewPager) view.findViewById(R.id.vpExpression);
        U5Application u5App = (U5Application) getActivity().getApplication();
        mExpressionList = u5App.getExpressionList();
        ExpressionPageAdapter adapter = new ExpressionPageAdapter(getActivity(), mExpressionList);
        mVp.setAdapter(adapter);
        mTipsBox = (LinearLayout) view.findViewById(R.id.tipsBox);
        tips = new ImageView[mExpressionList.size() / (COLUNM * ROW) + 1];
        for(int i = 0; i < tips.length; i++) {
            ImageView img = new ImageView(getActivity()); //实例化一个点点
            img.setLayoutParams(new ViewGroup.LayoutParams(10,10));
            tips[i]=img;
            if(i==0)
            {
                img.setBackgroundResource(R.drawable.attention_smilies_round_sel);
            } else {
                img.setBackgroundResource(R.drawable.attention_smilies_round);
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            params.leftMargin=5;
            params.rightMargin=5;
            mTipsBox.addView(img, params); //把点点添加到容器中
        }
        mVp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {
                tips[currentPage].setBackgroundResource(R.drawable.attention_smilies_round);
                currentPage = position;
                tips[position].setBackgroundResource(R.drawable.attention_smilies_round_sel);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        return view;
    }


    //    PagerAdapter adapter=new PagerAdapter()
    class ExpressionPageAdapter extends PagerAdapter {
        private Context mContext;
        private List<ExpressionNode> mList;

        public ExpressionPageAdapter(Context context, List<ExpressionNode> list) {
            mContext = context;
            mList = list;
        }

        @Override

        public int getCount() {
            return mList.size() / (COLUNM * ROW) + 1;
        }


        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;

        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object o) {
            //container.removeViewAt(position);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            GridView gv = new GridView(mContext);
            int begin = position * (COLUNM * ROW);
            int end = mList.size() < (position + 1) * (COLUNM * ROW) ? mList.size() : (position + 1) * (COLUNM * ROW);
            ExpressionAdapter expressionAdapter = new ExpressionAdapter(mContext, mList.subList(begin, end));
            gv.setAdapter(expressionAdapter);
            gv.setNumColumns(COLUNM);
            gv.setSelector(new ColorDrawable(getResources().getColor(R.color.transparent)));
            gv.setVerticalSpacing(5);
            container.addView(gv);
            return gv;
        }

    }

    class ExpressionAdapter extends U5BaseAdapter<ExpressionNode> {

        public ExpressionAdapter(Context context, List<ExpressionNode> list) {
            super(context, list);
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            ExpressionNode item = mDatas.get(i);
            view = mInflater.inflate(R.layout.item_image, null);
            ImageView icon = (ImageView) view.findViewById(R.id.icon);
            try {
                Field f = R.drawable.class.getDeclaredField(item.getName());
                int resId = f.getInt(R.drawable.class);
                icon.setImageResource(resId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            icon.setTag(item);
            icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ExpressionNode tag = (ExpressionNode) view.getTag();
                    ExpressionFragment.this.lintener.onExperssionSelected("[" + tag.getName() + "]");
                }
            });

            return view;
        }
    }
}
