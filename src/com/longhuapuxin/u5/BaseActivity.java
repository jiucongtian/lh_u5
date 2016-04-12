package com.longhuapuxin.u5;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.longhuapuxin.senabimage.ImagePagerActivity;
import java.text.DecimalFormat;

public class BaseActivity extends Activity {

    public final static int MODIFY_LABEL_OK = 0x010;
    public final static int RESULT_ERROR = 0x011;
    public final static int RESULT_CITY = 0x012;
    protected DecimalFormat decimalFormat = new DecimalFormat("0.00");
    protected U5Application app;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (U5Application) this.getApplication();

    }


    protected void initHeader(String headerText) {

        ((TextView) findViewById(R.id.headerText)).setText(headerText);
        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                BaseActivity.this.finish();
            }
        });
    }

    protected void initHeader(int resId) {

        ((TextView) findViewById(R.id.headerText)).setText(resId);
        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                BaseActivity.this.finish();
            }
        });
    }

    protected void enableRightImage(OnClickListener listener) {
        enableRightImage(R.drawable.shop_nav_search, listener);
    }

    protected void enableRightImage(int resId, OnClickListener listener) {
        ImageView rightImageBtn = (ImageView) findViewById(R.id.rightImageBtn);
        rightImageBtn.setImageResource(resId);
        rightImageBtn.setVisibility(View.VISIBLE);
        rightImageBtn.setOnClickListener(listener);
    }

    protected void enableRightTextBtn(int resId, boolean clickable, OnClickListener listener) {
        TextView rightTextBtn = (TextView) findViewById(R.id.rightTextBtn);
        rightTextBtn.setVisibility(View.VISIBLE);
        rightTextBtn.setText(resId);
        rightTextBtn.setOnClickListener(listener);

        rightTextBtn.setClickable(clickable);
    }

    public void ShowGalleryActivity(String[] urls, int position) {
        Intent intent = new Intent(BaseActivity.this, ImagePagerActivity.class);
        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls);
        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_in);

// old way to view photos.
//        Intent intent = new Intent(BaseActivity.this,
//                GalleryActivity.class);
//        ArrayList<String> urlArray = new ArrayList<String>();
//        for (int i = 0; i < urls.length; i++) {
//            urlArray.add(urls[i]);
//        }
//        intent.putExtra("Urls", urlArray);
//        intent.putExtra("Position", position);
//        startActivityForResult(intent, 0);
//        Logger.info("ShowGellery");
    }

    public <T> void putPram(Integer key, T param) {
        ((U5Application) getApplication()).putParam(key, param);
    }

    public <T> T getPram(Integer key) {
        Object obj = ((U5Application) getApplication()).getParam(key);
        return (T)obj;
    }



//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
//            if (isFastDoubleClick()) {
//                return true;
//            }
//        }
//        return super.dispatchTouchEvent(ev);
//    }
//
//    static long lastClickTime;
//
//    public static boolean isFastDoubleClick() {
//        long time = System.currentTimeMillis();
//        long timeD = time - lastClickTime;
//        lastClickTime = time;
//        if(timeD <= 1000) {
//            int a =0;
//            a = 1;
//        }
//        return timeD <= 1000;
//    }




}
