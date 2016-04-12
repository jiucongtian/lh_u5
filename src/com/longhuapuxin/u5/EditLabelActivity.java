package com.longhuapuxin.u5;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lidroid.xutils.BitmapUtils;
import com.longhuapuxin.adapter.U5BaseAdapter;
import com.longhuapuxin.common.ImageUrlLoader;
import com.longhuapuxin.common.Logger;
import com.longhuapuxin.common.MyOkHttpClientManager;
import com.longhuapuxin.common.OkHttpClientManager;
import com.longhuapuxin.common.OkHttpClientManager.Param;
import com.longhuapuxin.common.WaitDialog;
import com.longhuapuxin.entity.ResponseFilePath;
import com.longhuapuxin.entity.ResponseGetAccount.User.Label;
import com.longhuapuxin.entity.ResponseKeptLabel.KeptLabel;
import com.longhuapuxin.entity.ResponseLabelCategory.LabelCategory;
import com.longhuapuxin.entity.ResponseMarkedLabels;
import com.longhuapuxin.entity.ResponseUploadFile;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class EditLabelActivity extends BaseActivity implements View.OnClickListener {
	private static final int REQ_GET_PHOTO = 100;
    private static final int MSG_ShowLabel = 1;
    private static final int MSG_ShowImage = 2;
    private static final int MaxImageSize=1000;
    private static final MediaType MEDIA_TYPE_JPG = MediaType
            .parse("image/jpeg");
    private static final String NAME = "text";
    private static final String CODE = "code";

    private EditText mCommentView, mCategoryEdtTxt, mSkillNameEdtTxt, mEvUnitPrice, mEvUnit;
	private CheckBox mAgreePolicy;
    private List<KeptLabel> mKeptLabels;
    private List<Map<String, String>> mCategoryList, mLabelNameList;
    private Map<String, Object> mSortedLabels;
    private List<LabelCategory> mCategories;
    private String mCurrentLabelCode = "", mCurrentCategory = "";
    private Label mLabel;
    private LinkedList<ImageItem> mItemList;
    private MyImageAdapter mAdapter;
    private SimpleAdapter mCategoryAdapter, mSkillNameAdapter;
    private GridView mGridView, mSelectCategoryGv, mSelectNameGv;
    private View mSlCategoryContainerLv, mSkillNameContainerLv;
	private int fileId;
    private Button mCommitBtn;
    private ExpandStateManager mStateManager;
    private MyHandler handler = new MyHandler(this);

    private enum ExpandState {
        CATEGORY_OPEN,
        NAME_OPEN,
        ALL_CLOSE
    }

    private class ExpandStateManager {
        private ExpandState state = ExpandState.ALL_CLOSE;
        private View mCategoryLayout, mNameLayout;

        public ExpandStateManager(View mCategoryLayout, View mNameLayout) {
            this.mCategoryLayout = mCategoryLayout;
            this.mNameLayout = mNameLayout;
        }

        public void switchCategoryState() {
            if(state == ExpandState.CATEGORY_OPEN) {
                closeAll();
            } else {
                state = ExpandState.CATEGORY_OPEN;
                mCategoryLayout.setVisibility(View.VISIBLE);
                mNameLayout.setVisibility(View.GONE);
            }
        }

        public void switchNameState() {
            if(state == ExpandState.NAME_OPEN) {
                closeAll();
            } else {
                state = ExpandState.NAME_OPEN;
                mCategoryLayout.setVisibility(View.GONE);
                mNameLayout.setVisibility(View.VISIBLE);
            }
        }

        public void closeAll() {
            state = ExpandState.ALL_CLOSE;
            mCategoryLayout.setVisibility(View.GONE);
            mNameLayout.setVisibility(View.GONE);
        }
    }

    static class MyHandler extends Handler {
        WeakReference mActivity;

        public MyHandler(EditLabelActivity obj) {
            mActivity = new WeakReference<EditLabelActivity>(obj);
        }

        public void handleMessage(Message msg) {
            EditLabelActivity theActivity = (EditLabelActivity) mActivity.get();
            switch (msg.what) {
                case MSG_ShowLabel:
                    theActivity.ShowLable();
                    break;
                case MSG_ShowImage:
                    theActivity.GetPhotos();
                    break;
            }
        }
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_label);
		initHeader(R.string.title_edit_label);
		initViews();
        mStateManager = new ExpandStateManager(mSlCategoryContainerLv, mSkillNameContainerLv);
		mKeptLabels = Settings.instance().getKeptLabels();
		mCategories = Settings.instance().getLabelCategories();

		mCategoryList = new ArrayList<Map<String,String>>();
		mLabelNameList = new ArrayList<Map<String,String>>();
		
		mCategoryAdapter = new SimpleAdapter(this, mCategoryList,
				R.layout.item_category, new String[] {NAME}, 
				new int[] {R.id.tv_item_name});
		
		mSkillNameAdapter = new SimpleAdapter(this, mLabelNameList, 
				R.layout.item_category, new String[] {NAME}, 
				new int[] {R.id.tv_item_name});
		
		mSelectCategoryGv.setAdapter(mCategoryAdapter);
		mSelectCategoryGv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Map<String, String> map = mCategoryList.get(position);
				String categoryCode = map.get(CODE);
				
				if(!mCurrentCategory.equals(categoryCode)) {
					mCurrentCategory = categoryCode;
					mCategoryEdtTxt.setText(map.get(NAME));
					updateLabelNameList();

					mSkillNameEdtTxt.setText("");
				}
                mStateManager.closeAll();
                mSkillNameAdapter.notifyDataSetChanged();
			}
		});
		
		mSelectNameGv.setAdapter(mSkillNameAdapter);
		mSelectNameGv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Map<String, String> map = mLabelNameList.get(position);
				
				if(map.get(CODE).equals("0")) {
					mCurrentLabelCode = "";
					setLabelNameEnable(true);
                    mSkillNameEdtTxt.setTag(true);
				} else {
                    mSkillNameEdtTxt.setText(map.get(NAME));
                    mCurrentLabelCode = map.get(CODE);
                    setLabelNameEnable(false);
                    mSkillNameEdtTxt.setTag(false);
                }
                mStateManager.closeAll();
			}
		});

		mItemList = new LinkedList<ImageItem>();
		ImageItem item = new ImageItem();
		item.mIsPlus = true;
		mItemList.add(item);
		mAdapter = new MyImageAdapter(this, mItemList);

		mGridView.setAdapter(mAdapter);
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				initData();
				GetInitialData();
			}

		}, 20);
		mCommitBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				SaveLabel();
			}
		});
		
	}

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch(id) {
            case R.id.iv_skill_category:
            case R.id.edtTxt_skill_category:
                mStateManager.switchCategoryState();
                break;
            case R.id.iv_skill_name:
                mStateManager.switchNameState();
                break;
            case R.id.edtTxt_skill_name:

                Boolean editAble = (Boolean)view.getTag();
                if(editAble != null && !editAble) {
                    mStateManager.switchNameState();
                }
                break;
        }
    }

	private void setLabelNameEnable(boolean enable) {
		if(enable) {
//			if(!mSkillNameEdtTxt.isEnabled()) {
				mSkillNameEdtTxt.setText("");
//				mSkillNameEdtTxt.setEnabled(true);
				mSkillNameEdtTxt.setFocusable(true);
				mSkillNameEdtTxt.setFocusableInTouchMode(true);
				mSkillNameEdtTxt.requestFocus();
				InputMethodManager inputManager =
						(InputMethodManager)mSkillNameEdtTxt.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(mSkillNameEdtTxt, 0);
//			}
		} else {
            mSkillNameEdtTxt.setFocusable(false);
            mSkillNameEdtTxt.setFocusableInTouchMode(false);
		}
	}
	
	
	private void updateLabelNameList() {
		if(mCurrentCategory != null) {
			List<KeptLabel> list = (List<KeptLabel>) mSortedLabels.get(mCurrentCategory);
			if(list != null) {
				mLabelNameList.clear();
				Map<String, String> customMap = new HashMap<String, String>();
				customMap.put(NAME, "自定义");
				customMap.put(CODE, "0");
				mLabelNameList.add(customMap);
				
				for(KeptLabel label : list) {
					Map<String, String> nameMap = new HashMap<String, String>();
					nameMap.put(NAME, label.getName());
					nameMap.put(CODE, label.getCode());
					mLabelNameList.add(nameMap);
				}
			}
		}
	}
	
	private void initViews() {
//		mSkillCategoryBtn = (ImageView) findViewById(R.id.iv_skill_category);
		mCategoryEdtTxt = (EditText) findViewById(R.id.edtTxt_skill_category);
		mCommentView = (EditText) findViewById(R.id.txtComment);
		mAgreePolicy = (CheckBox) findViewById(R.id.cbAgreePolicy);
		mSelectCategoryGv = (GridView) findViewById(R.id.gv_skill_category);
		mSlCategoryContainerLv = findViewById(R.id.lv_skill_category_container);
		mGridView = (GridView) findViewById(R.id.labelImages);
		mCommitBtn = (Button) findViewById(R.id.commitBtn);
//		mSkillNameBtn = (ImageView) findViewById(R.id.iv_skill_name);
		mSkillNameEdtTxt = (EditText) findViewById(R.id.edtTxt_skill_name);
		mSkillNameContainerLv = findViewById(R.id.lv_skill_name_container);
		mSelectNameGv = (GridView) findViewById(R.id.gv_skill_name);
//		mGuidPriceEdtTxt = (EditText) findViewById(R.id.edtTxt_Guid_price);
		mEvUnitPrice = (EditText) findViewById(R.id.ev_unit_price);
		mEvUnit = (EditText) findViewById(R.id.ev_unit);

		findViewById(R.id.tvReleaseProtocol).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(EditLabelActivity.this, ReleaseSkillProtocol.class);
				startActivity(intent);
			}
		});

        // set onClick.
        findViewById(R.id.iv_skill_category).setOnClickListener(this);
        mCategoryEdtTxt.setOnClickListener(this);
        findViewById(R.id.iv_skill_name).setOnClickListener(this);
        mSkillNameEdtTxt.setOnClickListener(this);

        mSkillNameEdtTxt.setTag(false);

		mCommentView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
				return (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER);
			}
		});
    }

	private String getCategoryCode(String lableCode) {
		return lableCode.substring(0, 4);
	}
	
	public void ShowLable() {
		mCategoryEdtTxt.setText(getCategoryNameViaCode(getCategoryCode(mLabel.getLabelCode())/*mLabel.getIndustryCode()*/));
		mSkillNameEdtTxt.setText(mLabel.getLabelName());
		mCommentView.setText(mLabel.getNote());
		mCurrentLabelCode = mLabel.getLabelCode();
//		mCurrentLabelName = mLabel.getLabelName();
		if(!TextUtils.isEmpty(mLabel.getGuidePrice())) {
			String guidPrice = mLabel.getGuidePrice();

			if(!TextUtils.isEmpty(guidPrice)) {
				Map<String, String> priceMap = getPriceAndUnit(guidPrice);

				mEvUnitPrice.setText(priceMap.get("price"));
				mEvUnit.setText(priceMap.get("unit"));
			}
		}

		String photoIds = mLabel.getPhotos();
		if (photoIds != null && photoIds.length() > 0) {
			handler.sendEmptyMessageDelayed(MSG_ShowImage, 20);
		}
	}

	private void initData() {
		makeKetpMap();
	}

	private void GetInitialData() {
		List<Label> myLabelList = Settings.instance().User.getLabels();
		for (Label label : myLabelList) {
			if (label.getIndex().equals("1")) {
				mLabel = label;
				mCurrentCategory = getCategoryCode(mLabel.getLabelCode());//mLabel.getCategoryCode();
				updateLabelNameList();
				
				handler.sendEmptyMessageDelayed(MSG_ShowLabel, 200);
				break;
			}
		}

	}

	private void GetPhotos() {
		Param[] params = new Param[3];
		final Settings setting = Settings.instance();
		params[0] = new Param("UserId", setting.getUserId());
		params[1] = new Param("Token", setting.getToken());
		params[2] = new Param("FileId", mLabel.getPhotos());

		OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
						+ "/basic/getphoto", params,
				new OkHttpClientManager.ResultCallback<ResponseFilePath>() {

					@Override
					public void onError(Request request, Exception e) {

					}

					@Override
					public void onResponse(ResponseFilePath response) {

						if (response.isSuccess()) {
							List<String> idList = new ArrayList<String>();
							for (ResponseFilePath.FileObject file : response
									.getFiles()) {
								ImageItem item = new ImageItem();
								item.mIdUri = file.getId();
								item.mIsLocal = false;
								idList.add(item.mIdUri);
								addItem(item);
							}
							ImageUrlLoader.fetchImageUrl(mAdapter, idList);
//							mAdapter.notifyDataSetChanged();
						}
					}

				});
	}

	private void makeKetpMap() {
		mSortedLabels = new HashMap<String, Object>();
		for (LabelCategory cateory : mCategories) {
			List<KeptLabel> keptList = new ArrayList<KeptLabel>();
			mSortedLabels.put(cateory.getCode(), keptList);
			Map<String, String> map = new HashMap<String, String>();
			map.put(NAME, cateory.getName());
			map.put(CODE, cateory.getCode());
			mCategoryList.add(map);
		}

		for (KeptLabel label : mKeptLabels) {
			String category = label.getCategoryCode();
			@SuppressWarnings("unchecked")
			List<KeptLabel> list = (List<KeptLabel>) mSortedLabels
					.get(category);
			if (list != null) {
				list.add(label);
			}
		}

	}
	
	private String getCategoryNameViaCode(String categoryCode) {
		for(LabelCategory category : mCategories) {
			if(category.getCode().equals(categoryCode)) {
				return category.getName();
			}
		}
		return "";
	}

	public class ImageItem {

		boolean mIsPlus = false;
		boolean mIsLocal = false;
		String mIdUri;
	}

	@SuppressLint("SdCardPath")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			return;

		}
		if (data == null) {  
            return;

        }  
		try {
			//取得返回的Uri,基本上选择照片的时候返回的是以Uri形式，但是在拍照中有得机子呢Uri是空的，所以要特别注意  
            Uri mImageCaptureUri = data.getData();  
            //返回的Uri不为空时，那么图片信息数据都会在Uri中获得。如果为空，那么我们就进行下面的方式获取  
            if (mImageCaptureUri != null) {  
            	BitmapFactory.Options options = new BitmapFactory.Options();
    	        options.inJustDecodeBounds = true;
    	        
    	        String path = getRealPathFromURI(mImageCaptureUri);
    	        BitmapFactory.decodeFile(path, options);
    	        int height = options.outHeight;
    	        int width = options.outWidth; 
    	        int maxline=Math.max(height, width);
    	        if(maxline>MaxImageSize){
    	        	int scale= Math.round((float)maxline/(float) MaxImageSize);
    	        	   options.inSampleSize =scale;
    	               options.inJustDecodeBounds = false;
    	               Bitmap bitmap= BitmapFactory.decodeFile(path, options);
    	               int degree = readPictureDegree(path);
    	               bitmap = rotaingImageView(degree, bitmap);
    	               ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	               bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
    	               byte[] b = baos.toByteArray();
    	               UploadFile(null, b, true);
    	              
    	        }
    	        else{
    	        	UploadFile(path, null, true);
    	        }
       
            } else {  
                Bundle extras = data.getExtras();  
                if (extras != null) {  
                    //这里是有些拍照后的图片是直接存放到Bundle中的所以我们可以从这里面获取Bitmap图片  
                    Bitmap image = extras.getParcelable("data");  
                    if (image != null) {  
                    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    	image.compress(Bitmap.CompressFormat.JPEG, 60, baos);
     	               byte[] b = baos.toByteArray();
     	               UploadFile(null, b, true);
                    }  
                }  
            }  
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	/** 
     * 旋转图片 
     * @param angle 
     * @param bitmap 
     * @return Bitmap 
     */  
    public static Bitmap rotaingImageView(int angle , Bitmap bitmap) {  
        //旋转图片 动作  
        Matrix matrix = new Matrix();;  
        matrix.postRotate(angle);  
        System.out.println("angle2=" + angle);  
        // 创建新的图片  
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,  
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);  
        return resizedBitmap;  
    }
	
    /** 
     * 读取图片属性：旋转的角度 
     * @param path 图片绝对路径 
     * @return degree旋转的角度 
     */  
       public static int readPictureDegree(String path) {  
           int degree  = 0;  
           try {  
                   ExifInterface exifInterface = new ExifInterface(path);  
                   int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);  
                   switch (orientation) {  
                   case ExifInterface.ORIENTATION_ROTATE_90:  
                           degree = 90;  
                           break;  
                   case ExifInterface.ORIENTATION_ROTATE_180:  
                           degree = 180;  
                           break;  
                   case ExifInterface.ORIENTATION_ROTATE_270:  
                           degree = 270;  
                           break;  
                   }  
           } catch (IOException e) {  
                   e.printStackTrace();  
           }  
           return degree;  
       }  
	
	@SuppressWarnings("deprecation")
	public String getRealPathFromURI(Uri contentUri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(contentUri, proj, null, null, null);
		
		if(cursor!=null){
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		}
		else{
			return contentUri.getPath();
			
		}
	}
	

	private void UploadFile(final String path, byte[] content,
			final boolean deleteFileAfterUpload) throws IOException {
		Settings set = Settings.instance();
		// Use the imgur image upload API as documented at
		// https://api.imgur.com/endpoints/image
		WaitDialog.instance().showWaitNote(this);
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
						RequestBody.create(null, "4"));
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
						EditLabelActivity.this,
						"正在上传头像",
						request,
						new com.longhuapuxin.common.OkHttpClientManager.ResultCallback<String>() {

							@Override
							public void onError(Request request, Exception e) {
								// TODO Auto-generated method stub
								Toast.makeText(EditLabelActivity.this,
										e.getMessage(), Toast.LENGTH_LONG)
										.show();
								e.printStackTrace();
								WaitDialog.instance().hideWaitNote();
							}

							@Override
							public void onResponse(String u) {
								WaitDialog.instance().hideWaitNote();
								Gson gson = new GsonBuilder().setDateFormat(
										"yyyy-MM-dd HH:mm:ss").create();
								ResponseUploadFile res = gson.fromJson(u,
										ResponseUploadFile.class);
								if (res.isSuccess()) {

									fileId = res.Photo.Id;
									ImageItem item = new ImageItem();
									item.mIdUri = String.valueOf(fileId);
									item.mIsLocal = false;
									addItem(item);
									ArrayList<String> ids = new ArrayList<String>();
									ids.add(item.mIdUri);
									ImageUrlLoader.addPathCache(String.valueOf(fileId), res.Photo.FileName, res.Photo.SmallFileName);
									ImageUrlLoader.fetchImageUrl(mAdapter, ids);
//									mAdapter.notifyDataSetChanged();

								} else {
									Toast.makeText(EditLabelActivity.this,
											res.getErrorMessage(),
											Toast.LENGTH_LONG).show();
								}
								if (deleteFileAfterUpload && path != null
										&& path.length() > 0) {
									File file = new File(path);
									file.delete();
								}
							}

						});

	}


	private String genPriceString() {
		return mEvUnitPrice.getText() + "元/" + mEvUnit.getText();
	}

	private Map<String, String> getPriceAndUnit(String data) {
		String unit, price;
		int index = data.indexOf("元/");
		if(index > 0 && index < data.length()) {

			price = data.substring(0, index);
			unit = data.substring(index + 2);
		} else {
			price = "";
			unit = "";
		}

		Map<String, String> ret = new HashMap<String, String>();
		ret.put("unit", unit);
		ret.put("price", price);

		return ret;
	}

	private String validateData() {
		String ret = "";
		if(TextUtils.isEmpty(mCategoryEdtTxt.getText())) {
			ret = "请输入技能分类";
		}
		if(TextUtils.isEmpty(mSkillNameEdtTxt.getText())) {
			ret = "请输入技能名称";
		}
		if(TextUtils.isEmpty(mEvUnitPrice.getText())) {
			ret = "请输入单价";
		}
		if(TextUtils.isEmpty(mEvUnit.getText())) {
			ret = "请输入计价方式";
		}
		if(!mAgreePolicy.isChecked()) {
			ret = "同意发布规范才能修改";
		}
		return ret;
	}

	private void SaveLabel() {
		String verify = validateData();
		if(!TextUtils.isEmpty(verify)) {
			Toast.makeText(this, verify, Toast.LENGTH_LONG).show();
			return;
		}

		WaitDialog.instance().showWaitNote(this);
		String ids = "";
		if (mItemList.size() > 1) {
			ids = mItemList.get(0).mIdUri;
 			for (int i = 1; i < mItemList.size(); i++) {
 				ImageItem item = mItemList.get(i);
 				if(!item.mIsPlus) {
 					ids += "," + item.mIdUri;
 				}
			}
		}
		Param[] params = new Param[9];
		params[0] = new Param("UserId", Settings.instance().getUserId());
		params[1] = new Param("Token", Settings.instance().getToken());
		params[2] = new Param("Index", "1");
		params[3] = new Param("LabelCode", mCurrentLabelCode);
		params[4] = new Param("LabelName", mSkillNameEdtTxt.getText().toString());
		params[5] = new Param("CategoryCode", mCurrentCategory);
		params[6] = new Param("Note", mCommentView.getText().toString());
		params[7] = new Param("Photos", ids);
		params[8] = new Param("GuidePrice", genPriceString()/*mGuidPriceEdtTxt.getText().toString()*/);

		OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
				+ "/label/marklabel", params,
				new OkHttpClientManager.ResultCallback<ResponseMarkedLabels>() {

					@Override
					public void onError(Request request, Exception e) {
						Logger.info("editLabel.onError" + e.toString());
						WaitDialog.instance().hideWaitNote();
					}

					@Override
					public void onResponse(ResponseMarkedLabels response) {
						WaitDialog.instance().hideWaitNote();
						if (response.isSuccess()) {
							Settings.instance().User.updateLabels(response.getLabels());
							handler.postDelayed(new Runnable() {

								@Override
								public void run() {
									finish();

								}
							}, 1000);
						} else {
							Logger.info("editLabel onResponse. message is: "
									+ response.getErrorMessage());
						}

					}

				});
	}

	public class MyImageAdapter extends U5BaseAdapter<ImageItem> {
		public MyImageAdapter(Context context, List<ImageItem> list) {
			super(context, list);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageItem item = mDatas.get(position);

			ViewHolder viewHolder;

			if (convertView == null) {
				viewHolder = new ViewHolder();

				convertView = mInflater.inflate(R.layout.item_label_image,
						parent, false);
				viewHolder.labelImageView = (ImageView) convertView
						.findViewById(R.id.labelImage);
				viewHolder.deleteImageView = (ImageView) convertView
						.findViewById(R.id.deleteImage);
				viewHolder.photoLayout = convertView
						.findViewById(R.id.photoLayout);
				viewHolder.addPhotoLayout = convertView
						.findViewById(R.id.addPhotoLayout);
				viewHolder.addPhotoBtn = (ImageView) convertView
						.findViewById(R.id.addLocalImage);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			if (item.mIsPlus) {
				viewHolder.photoLayout.setVisibility(View.GONE);
				viewHolder.addPhotoLayout.setVisibility(View.VISIBLE);
			} else if (item.mIsLocal) {
				viewHolder.photoLayout.setVisibility(View.VISIBLE);
				viewHolder.addPhotoLayout.setVisibility(View.GONE);
				BitmapUtils utils = ((U5Application) getApplication())
						.getBitmapUtils();
				utils.display(viewHolder.labelImageView, item.mIdUri);
			} else {
				viewHolder.photoLayout.setVisibility(View.VISIBLE);
				viewHolder.addPhotoLayout.setVisibility(View.GONE);
				bindImageView(viewHolder.labelImageView, item.mIdUri);
			}

			viewHolder.deleteImageView.setTag(position);
			viewHolder.deleteImageView
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							int index = (Integer) v.getTag();
							deleteItem(index);
							MyImageAdapter.this.notifyDataSetChanged();
						}
					});

			viewHolder.addPhotoBtn
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							Intent intent = new Intent(EditLabelActivity.this, PhotoSelectorActivity.class);
							startActivityForResult(intent, REQ_GET_PHOTO);
						}
					});

			return convertView;

		}

		private class ViewHolder {
			public ImageView labelImageView;
			public ImageView deleteImageView;
			public View photoLayout;
			public View addPhotoLayout;
			public ImageView addPhotoBtn;
		}

	}

	private void deleteItem(int index) {
		if (mItemList.size() > index) {
			ImageItem item = mItemList.get(index);

			if (!item.mIsPlus) {
				mItemList.remove(index);
			}
		}

		if (mItemList.size() < 6) {
			ImageItem lastItem = mItemList.getLast();
			if (!lastItem.mIsPlus) {
				ImageItem imageItem = new ImageItem();
				imageItem.mIsPlus = true;
				mItemList.add(imageItem);
			}
		}
	}

	private void addItem(ImageItem item) {
		ImageItem lastItem = mItemList.getLast();
		int position;

		if (lastItem.mIsPlus) {
			position = mItemList.size() - 1;
		} else {
			position = mItemList.size();
		}

		mItemList.add(position, item);

		// 保证6个图片
		while (mItemList.size() > 6) {
			mItemList.removeLast();
		}

		if (mItemList.size() < 6) {
			lastItem = mItemList.getLast();
			if (!lastItem.mIsPlus) {
				ImageItem imageItem = new ImageItem();
				imageItem.mIsPlus = true;
				mItemList.add(imageItem);
			}

		}
	}
}
