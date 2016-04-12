package com.longhuapuxin.u5;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;

import java.io.File;

/**
 * Created by ZH on 2016/1/4.
 * Email zh@longhuapuxin.com
 */
public class PhotoSelectorActivity extends Activity implements View.OnClickListener {

    private final int RESULT_CAMERA = 101;
    private final int RESULT_PHOTO = 102;
    private String photoSaveName;// 图片名
    private String photoSavePath;// 保存路径

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pop_select_photo);
        init();
    }

    private void init() {
        photoSavePath = Environment.getExternalStorageDirectory()+ "/U5/tmp_pic/";
        findViewById(R.id.photo).setOnClickListener(this);
        findViewById(R.id.gallery).setOnClickListener(this);
        findViewById(R.id.cancel).setOnClickListener(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case RESULT_CAMERA:
                if(resultCode == RESULT_OK) {
                    String path = photoSavePath + photoSaveName;
                    Uri uri = Uri.fromFile(new File(path));
                    Intent intent = new Intent();
                    intent.setData(uri);
                    setResult(RESULT_OK, intent);
                    finish();
                }

                break;
            case RESULT_PHOTO:
                if(resultCode == RESULT_OK) {
                    setResult(RESULT_OK, data);
                    finish();
                }
                break;
        }
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.photo:
                photoSaveName = String.valueOf(System.currentTimeMillis())
                        + ".jpg";
                Uri imageUri = null;
                Intent openCameraIntent = new Intent(
                        MediaStore.ACTION_IMAGE_CAPTURE);
                imageUri = Uri.fromFile(new File(photoSavePath, photoSaveName));
                openCameraIntent.putExtra(MediaStore.Images.Media.ORIENTATION,
                        0);
                openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(openCameraIntent, RESULT_CAMERA);
                break;
            case R.id.gallery:
                Intent intent = new Intent(
                    "android.intent.action.PICK");
                intent.setDataAndType(
                    MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                    "image/*");
                startActivityForResult(intent, RESULT_PHOTO);
                break;
            case R.id.cancel:
                setResult(RESULT_CANCELED);
                this.finish();
                break;
        }
    }
}
