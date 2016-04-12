package com.longhuapuxin.u5;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.DatePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.longhuapuxin.common.InputTool;
import com.longhuapuxin.common.Logger;
import com.longhuapuxin.common.MyOkHttpClientManager;
import com.longhuapuxin.common.OkHttpClientManager;
import com.longhuapuxin.common.OkHttpClientManager.Param;
import com.longhuapuxin.common.Utils;
import com.longhuapuxin.common.WaitDialog;
import com.longhuapuxin.entity.ResponseDad;
import com.longhuapuxin.entity.ResponseGetAccount;
import com.longhuapuxin.entity.ResponseUploadFile;
import com.longhuapuxin.service.IMService;
import com.longhuapuxin.u5.MyLocationListener.OnLocation;
import com.longhuapuxin.view.MyDatePickerDialog;
import com.longhuapuxin.view.RoundCornerImageView;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

public class MyProfileActiviy extends BaseActivity implements OnClickListener {
    public static final int GALLERY = 0; // 相册
    public static final int PHOTOTAKE = 1; // 拍照
    public static final int IMAGE_COMPLETE = 2; // 结果
    private static final MediaType MEDIA_TYPE_JPG = MediaType
            .parse("image/jpeg");
    private RoundCornerImageView imgPortrait;
    private PopupWindow popWindow;
    private LayoutInflater layoutInflater;
    private LinearLayout cancel;
    private TextView photo, gallery;
    private String photoSavePath;// 保存路径
    private String photoSaveName;// 图片名
    private String path;// 图片全路径
    private ResponseGetAccount.User user;
    private EditText profileName, locationTx;
    private TextView profileGender, mProfileAge;
//    private IMService myService;
//    private ServiceConnection sc;
    private ImageView locationImg;
    private String codeGender;
    private TextView labelError;
    private RelativeLayout changeConfirm;
    private int fileId;
    private LinearLayout chooseGender;
    private RelativeLayout profileGenderLayout;
    private TextView gender1, gender2, gender3;
    private ImageView genderRound1, genderRound2, genderRound3;
    private Date mBirthday;
//    private OnLocation onlocation = new OnLocation() {
//
//        @Override
//        public void onError(String msg) {
//            locationTx.setText("为找到该地址");
//        }
//
//        @Override
//        public void onLocated(BDLocation location, boolean isGps) {
//            locationTx.setText(Settings.instance().address);
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
//        aboutService();
        initHeader(R.string.profile);
        initViews();
        getProfileGender();
        init();
    }

    private void getProfileGender() {
        codeGender = profileGender.getText().toString();
        if (codeGender.equals("男")) {
            codeGender = "1";
        } else if (codeGender.equals("女")) {
            codeGender = "2";
        } else {
            codeGender = "0";
        }

    }

//    private void aboutService() {
//        if (sc == null) {
//            sc = new ServiceConnection() {
//                @Override
//                public void onServiceConnected(ComponentName name,
//                                               IBinder service) {
//                    myService = ((IMService.LocalBinder) service).getService();
//                    myService.GetLocation(onlocation);
//                }
//
//                @Override
//                public void onServiceDisconnected(ComponentName name) {
//                    sc = null;
//                    Log.i("TAG",
//                            "onServiceDisconnected : ServiceConnection --->"
//                                    + sc);
//                }
//
//            };
//            Intent intent = new Intent(this, IMService.class);
//            bindService(intent, sc, Context.BIND_AUTO_CREATE);
//        }
//    }

    private String locationTxRecentString;
    private int locationTxRecentIndex;
    private boolean isLocationTxRecent = true;
    private String profileNameRecentString;
    private int profileNameRecentIndex;
    private boolean isProfileNameRecent = true;

    private void initViews() {
        gender1 = (TextView) findViewById(R.id.genden_1);
        gender2 = (TextView) findViewById(R.id.genden_2);
        gender3 = (TextView) findViewById(R.id.genden_3);
        genderRound1 = (ImageView) findViewById(R.id.gender_round_1);
        genderRound1.setOnClickListener(this);
        genderRound2 = (ImageView) findViewById(R.id.gender_round_2);
        genderRound2.setOnClickListener(this);
        genderRound3 = (ImageView) findViewById(R.id.gender_round_3);
        genderRound3.setOnClickListener(this);
        profileGenderLayout = (RelativeLayout) findViewById(R.id.profile_gender_layout);
        profileGenderLayout.setOnClickListener(this);
        chooseGender = (LinearLayout) findViewById(R.id.choose_gender);
        changeConfirm = (RelativeLayout) findViewById(R.id.confirm_change);
        changeConfirm.setOnClickListener(this);
        labelError = (TextView) findViewById(R.id.label_error);
        locationImg = (ImageView) findViewById(R.id.location_img);
        locationImg.setOnClickListener(this);
        locationTx = (EditText) findViewById(R.id.location_tx);
        locationTx.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                return (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER);
            }
        });
        locationTx.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (isLocationTxRecent) {
                    locationTxRecentIndex = start;
                }
                locationTxRecentString = String.valueOf(charSequence.toString().substring(start, start + count));
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() != 0) {
                    StringBuffer sb = new StringBuffer(editable.toString());
                    if (InputTool.isContainSpace(editable.toString())) {
                        sb.deleteCharAt(sb.indexOf(locationTxRecentString));
                        isLocationTxRecent = false;
                        locationTx.setText(sb.toString());
                        locationTx.setSelection(locationTxRecentIndex);
                        return;
                    }
                    if (!InputTool.isContainChinese(editable.toString()) && !InputTool.isContainNumber(editable.toString()) && !InputTool.isContainCharacter(editable.toString())) {
                        sb.deleteCharAt(sb.indexOf(locationTxRecentString));
                        isLocationTxRecent = false;
                        locationTx.setText(sb.toString());
                        locationTx.setSelection(locationTxRecentIndex);
                        return;
                    }
                    isLocationTxRecent = true;
                }
            }
        });
        imgPortrait = (RoundCornerImageView) findViewById(R.id.imgPortrait);
        imgPortrait.setOnClickListener(this);
        profileName = (EditText) findViewById(R.id.profile_name);
        profileName.setOnClickListener(this);
        profileGender = (TextView) findViewById(R.id.profile_gender);
        profileGender.setOnClickListener(this);
        mProfileAge = (TextView) findViewById(R.id.profile_age);
        mProfileAge.setOnClickListener(this);
        user = Settings.instance().User;
        app.ObservePortait(imgPortrait);
        profileName.setText(user.getNickName().toString());
        profileName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                return (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER);
            }
        });
        profileName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (isProfileNameRecent) {
                    profileNameRecentIndex = start;
                }
                profileNameRecentString = String.valueOf(charSequence.toString().substring(start, start + count));
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() != 0) {
                    StringBuffer sb = new StringBuffer(editable.toString());
                    if (InputTool.isContainSpace(editable.toString())) {
                        sb.deleteCharAt(sb.indexOf(profileNameRecentString));
                        isProfileNameRecent = false;
                        profileName.setText(sb.toString());
                        profileName.setSelection(profileNameRecentIndex);
                        return;
                    }
                    if (!InputTool.isContainChinese(editable.toString()) && !InputTool.isContainNumber(editable.toString()) && !InputTool.isContainCharacter(editable.toString())) {
                        sb.deleteCharAt(sb.indexOf(profileNameRecentString));
                        isProfileNameRecent = false;
                        profileName.setText(sb.toString());
                        profileName.setSelection(profileNameRecentIndex);
                        return;
                    }
                    isProfileNameRecent = true;
                }
            }
        });
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        try {
            mBirthday = sdf.parse(user.getBirthday());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (mBirthday == null) {
            mBirthday = new Date();
        }

        mProfileAge.setText(String.valueOf(Utils.getAgeByBirthday(mBirthday)));
        locationTx.setText(user.getAddress());
        if (user.getGender().toString().equals("0")) {
            profileGender.setText("保密");
        } else if (user.getGender().toString().equals("1")) {
            profileGender.setText("男");
        } else if (user.getGender().toString().equals("2")) {
            profileGender.setText("女");
        }

    }

    private void init() {
        layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        File file = new File(Environment.getExternalStorageDirectory(),
                "/U5/tmp_pic/");
        if (!file.exists()) {
            file.mkdirs();
        }
        // /tmp_pic_
        photoSavePath = Environment.getExternalStorageDirectory()
                + "/U5/tmp_pic/";
        photoSaveName = System.currentTimeMillis() + ".jpg";
    }

    private boolean isChooseGenderOpen = false;

    @Override
    public void onClick(View v) {
        if (v == imgPortrait) {
            showPopupWindow(imgPortrait);
        } else if (v == locationImg) {
            locationTx.setText(Settings.instance().address);
        } else if (v == changeConfirm) {
            WaitDialog.instance().showWaitNote(this);
            getProfileGender();
            httpRequestUpdateUserInfo();
        } else if (v == profileGenderLayout || v == profileGender) {
            if (isChooseGenderOpen) {
                isChooseGenderOpen = false;
                chooseGender.setVisibility(View.GONE);
            } else {
                getGenderRound();
                isChooseGenderOpen = true;
                chooseGender.setVisibility(View.VISIBLE);

            }
        } else if (v == genderRound1) {
            profileGender.setText("保密");
            genderRound1.setImageResource(R.drawable.login_round_sel);
            genderRound2.setImageResource(R.drawable.login_round);
            genderRound3.setImageResource(R.drawable.login_round);
            isChooseGenderOpen = false;
            chooseGender.setVisibility(View.GONE);
        } else if (v == genderRound2) {
            profileGender.setText("男");
            genderRound1.setImageResource(R.drawable.login_round);
            genderRound2.setImageResource(R.drawable.login_round_sel);
            genderRound3.setImageResource(R.drawable.login_round);
            isChooseGenderOpen = false;
            chooseGender.setVisibility(View.GONE);
        } else if (v == genderRound3) {
            profileGender.setText("女");
            genderRound1.setImageResource(R.drawable.login_round);
            genderRound2.setImageResource(R.drawable.login_round);
            genderRound3.setImageResource(R.drawable.login_round_sel);
            isChooseGenderOpen = false;
            chooseGender.setVisibility(View.GONE);
        } else if (v == mProfileAge) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(mBirthday);

            int year = calendar.get(Calendar.YEAR);
            int monthOfYear = calendar.get(Calendar.MONTH);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new MyDatePickerDialog(MyProfileActiviy.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                    try {
                        mBirthday = sdf.parse("" + year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    mProfileAge.setText(String.valueOf(Utils.getAgeByBirthday(mBirthday)));
                }
            }, year, monthOfYear, dayOfMonth);
            DatePicker picker = datePickerDialog.getDatePicker();
            picker.setMaxDate(new Date().getTime());

            datePickerDialog.show();
        }
    }


    private void getGenderRound() {
        String gender = profileGender.getText().toString();
        if (gender.equals(gender1.getText().toString())) {
            genderRound1.setImageResource(R.drawable.login_round_sel);
        } else {
            genderRound1.setImageResource(R.drawable.login_round);
        }
        if (gender.equals(gender2.getText().toString())) {
            genderRound2.setImageResource(R.drawable.login_round_sel);
        } else {
            genderRound2.setImageResource(R.drawable.login_round);
        }
        if (gender.equals(gender3.getText().toString())) {
            genderRound3.setImageResource(R.drawable.login_round_sel);
        } else {
            genderRound3.setImageResource(R.drawable.login_round);
        }
    }

    @SuppressWarnings("deprecation")
    private void showPopupWindow(View parent) {
        if (popWindow == null) {
            View view = layoutInflater.inflate(R.layout.pop_select_photo, null);
            popWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT, true);
            initPop(view);
        }
        popWindow.setAnimationStyle(android.R.style.Animation_InputMethod);
        popWindow.setFocusable(true);
        popWindow.setOutsideTouchable(true);
        popWindow.setBackgroundDrawable(new BitmapDrawable());
        popWindow
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
    }

    public void initPop(View view) {
        photo = (TextView) view.findViewById(R.id.photo);// 拍照
        gallery = (TextView) view.findViewById(R.id.gallery);// 相册
        cancel = (LinearLayout) view.findViewById(R.id.cancel);// 取消
        photo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                popWindow.dismiss();
                photoSaveName = String.valueOf(System.currentTimeMillis())
                        + ".jpg";
                Uri imageUri = null;
                Intent openCameraIntent = new Intent(
                        MediaStore.ACTION_IMAGE_CAPTURE);
                imageUri = Uri.fromFile(new File(photoSavePath, photoSaveName));
                openCameraIntent.putExtra(MediaStore.Images.Media.ORIENTATION,
                        0);
                openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(openCameraIntent, PHOTOTAKE);
            }
        });
        gallery.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                popWindow.dismiss();
                Intent intent = new Intent(MyProfileActiviy.this,
                        ShowAlbumActivity.class);
                startActivity(intent);
            }
        });
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                popWindow.dismiss();

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        Uri uri = null;
        switch (requestCode) {
            case PHOTOTAKE:// 拍照
                path = photoSavePath + photoSaveName;
                uri = Uri.fromFile(new File(path));
                Intent intent = new Intent(this, ClipActivity.class);
                intent.putExtra("path", path);
                intent.putExtra("type", "photo");
                startActivityForResult(intent, IMAGE_COMPLETE);
                break;
            case IMAGE_COMPLETE:
                final String tempPath = data.getStringExtra("path");
                WaitDialog.instance().showWaitNote(this);
                imgPortrait.setImageBitmap(getLoacalBitmap(tempPath));
                try {
                    UploadFile(tempPath, null, true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getStringExtra("type").equals("gallery")) {
            final String tempPath = intent.getStringExtra("path");
            WaitDialog.instance().showWaitNote(this);
            imgPortrait.setImageBitmap(getLoacalBitmap(tempPath));
            try {
                UploadFile(tempPath, null, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param url
     * @return
     */
    public static Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void httpRequestUpdateUserInfo() {
//        labelError.setText("");
//        if (InputTool.isContainSpace(profileName.getText().toString())) {
//            WaitDialog.instance().hideWaitNote();
//            labelError.setText("昵称不能包含空格");
//            return;
//        }
//        if (InputTool.isContainSpecial(profileName.getText().toString())) {
//            WaitDialog.instance().hideWaitNote();
//            labelError.setText("昵称不能包含特殊字符");
//            return;
//        }
//        if (InputTool.isContainSpace(locationTx.getText().toString())) {
//            WaitDialog.instance().hideWaitNote();
//            labelError.setText("地址不能包含空格");
//            return;
//        }
//        if (InputTool.isContainSpecial(locationTx.getText().toString())) {
//            WaitDialog.instance().hideWaitNote();
//            labelError.setText("地址不能包含特殊字符");
//            return;
//        }
        Param[] params = new Param[6];
        Settings setting = Settings.instance();
        params[0] = new Param("UserId", setting.getUserId());
        params[1] = new Param("Token", setting.getToken());
        params[2] = new Param("NickName", profileName.getText().toString());
        params[3] = new Param("Gender", codeGender);
        params[4] = new Param("Address", locationTx.getText().toString());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        params[5] = new Param("Birthday", df.format(mBirthday));

        OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
                        + "/auth/updateaccount", params,
                new OkHttpClientManager.ResultCallback<ResponseDad>() {

                    @Override
                    public void onError(Request request, Exception e) {
                        WaitDialog.instance().hideWaitNote();
                        labelError.setText("" + e.getMessage());
                    }

                    @Override
                    public void onResponse(ResponseDad response) {
                        if (response.isSuccess()) {
                            httpRequestGetAccount();
                        } else {
                            WaitDialog.instance().hideWaitNote();
                            labelError.setText("" + response.getErrorMessage());
                        }
                    }

                });
    }

    private void UploadFile(final String path, byte[] content,
                            final boolean deleteFileAfterUpload) throws IOException {
        Settings set = Settings.instance();
        // Use the imgur image upload API as documented at
        // https://api.imgur.com/endpoints/image
        MultipartBuilder builder = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addPart(
                        Headers.of("Content-Disposition",
                                "form-data; name=\"UserId\""),
                        RequestBody.create(null,
                                String.valueOf(set.getUserId())))
                .addPart(
                        Headers.of("Content-Disposition",
                                "form-data; name=\"Token\""),
                        RequestBody.create(null, set.getToken()))
                .addPart(
                        Headers.of("Content-Disposition",
                                "form-data; name=\"PhotoType\""),
                        RequestBody.create(null, "1"));
        if (path != null && path.length() > 0) {
            builder.addPart(Headers.of("Content-Disposition",
                            "form-data; name=\"File\" filename=\"portrait.jpg\""),
                    RequestBody.create(MEDIA_TYPE_JPG, new File(path)));
        } else if (content != null) {
            builder.addPart(Headers.of("Content-Disposition",
                            "form-data; name=\"File\"  filename=\"portrait.jpg\""),
                    RequestBody.create(
                            MediaType.parse("application/octet-stream"),
                            content));
        }

        final Request request = new Request.Builder()

                .url(set.getApiUrl() + "/basic/uploadphoto").post(builder.build())
                .build();
        MyOkHttpClientManager
                .deliveryResult(
                        MyProfileActiviy.this,
                        "正在上传头像",
                        request,
                        new com.longhuapuxin.common.OkHttpClientManager.ResultCallback<String>() {

                            @Override
                            public void onError(Request request, Exception e) {
                                WaitDialog.instance().hideWaitNote();
                                labelError.setText("" + e.getMessage());
                                e.printStackTrace();
                            }

                            @Override
                            public void onResponse(String u) {
                                Gson gson = new GsonBuilder().setDateFormat(
                                        "yyyy-MM-dd HH:mm:ss").create();
                                ResponseUploadFile res = gson.fromJson(u,
                                        ResponseUploadFile.class);
                                if (res.isSuccess()) {
                                    WaitDialog.instance().hideWaitNote();
                                    fileId = res.Photo.Id;
                                    // fileUrl =
                                    // res.Photo.FileName.replace("\\",
                                    // "/");
                                    // app.PublishPortraitChange(fileUrl);
                                    app.PublishPortraitChange(String
                                            .valueOf(fileId));

                                } else {
                                    WaitDialog.instance().hideWaitNote();
                                    labelError.setText(""
                                            + res.getErrorMessage());
                                }
                                if (deleteFileAfterUpload && path != null
                                        && path.length() > 0) {
                                    File file = new File(path);
                                    file.delete();
                                }
                            }

                        });
    }

    private void httpRequestGetAccount() {
        Logger.info("GetAccount");
        Param[] params = new Param[3];
        params[0] = new Param("UserId", Settings.instance().getUserId());
        params[1] = new Param("Token", Settings.instance().getToken());
        params[2] = new Param("TargetUserId", Settings.instance().getUserId());

        OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
                        + "/auth/getaccount", params,
                new OkHttpClientManager.ResultCallback<ResponseGetAccount>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        WaitDialog.instance().hideWaitNote();
                        labelError.setText("" + e.getMessage());
                    }

                    @Override
                    public void onResponse(ResponseGetAccount response) {
                        if (response.isSuccess()) {
                            Settings.instance().User = response.getUser();
                            WaitDialog.instance().hideWaitNote();
                        } else {
                            WaitDialog.instance().hideWaitNote();
                            labelError.setText("" + response.getErrorMessage());
                        }
                    }
                });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unbindService(sc);
    }
}
