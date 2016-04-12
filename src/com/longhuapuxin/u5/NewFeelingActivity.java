package com.longhuapuxin.u5;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lidroid.xutils.BitmapUtils;
import com.longhuapuxin.adapter.U5BaseAdapter;
import com.longhuapuxin.common.ImageUrlLoader;
import com.longhuapuxin.common.Logger;
import com.longhuapuxin.common.MyOkHttpClientManager;
import com.longhuapuxin.common.OkHttpClientManager;
import com.longhuapuxin.common.WaitDialog;
import com.longhuapuxin.common.OkHttpClientManager.Param;
import com.longhuapuxin.entity.ResponseNewFeeling;
import com.longhuapuxin.entity.ResponseUploadFile;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

 

public class NewFeelingActivity extends BaseActivity {
	private LinkedList<ImageItem> mItemList;
	private MyImageAdapter mAdapter;
	private GridView mGridView;
	private int mOwnerType=1;
	private static final int MSG_FeelingSaved = 1;
	private static final int MSG_Cancel = 2;
	 private int fileId;
	private static final int MaxImageSize=1000;
	private static final MediaType MEDIA_TYPE_JPG = MediaType
			.parse("image/jpeg");
	private TextView txtContent,txtSend,txtCancel,txtMsg;
	 
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@SuppressLint("HandlerLeak")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_FeelingSaved:
				Intent mIntent = new Intent();  
		        mIntent.putExtra("FeelingId",msg.obj.toString());  
		        NewFeelingActivity.this.setResult(1, mIntent);  
				NewFeelingActivity.this.finish();  
				break;
			case MSG_Cancel:
				NewFeelingActivity.this.setResult(0, null);  
				NewFeelingActivity.this.finish();  
				break;
			 
			}
		}
	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newfeeling);
		initHeader(R.string.newFeeling);
		enableRightTextBtn(R.string.Publish,true,new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				SaveFeeling();
				
			}
			  
		});
		mGridView = (GridView) findViewById(R.id.feelingImages);
		txtContent=(TextView)findViewById(R.id.txtContent);
		txtMsg=(TextView)findViewById(R.id.txtMsg);
 
		
		ImageItem item = new ImageItem();
		item.mIsPlus = true;
		mItemList = new LinkedList<ImageItem>();
		mItemList.add(item);
		mAdapter = new MyImageAdapter(this, mItemList);
		mGridView.setAdapter(mAdapter);
		
		Intent intent=getIntent();
		if(intent!=null){
			mOwnerType=intent.getIntExtra("OwnerType",1);
		}
	 
	}
	public class ImageItem {

		boolean mIsPlus = false;
		boolean mIsLocal = false;
		String mIdUri;
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
	

	private void SaveFeeling() {
	
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
		txtMsg.setText("");
		if(ids.length()<=0 && txtContent.getText().toString().length()<=0){
			txtMsg.setText("请说点啥或者上传至少一张图片");
			
			return;
		}
		WaitDialog.instance().showWaitNote(this);
		Param[] params = new Param[5];
		params[0] = new Param("UserId", Settings.instance().getUserId());
		params[1] = new Param("Token", Settings.instance().getToken());
		params[2] = new Param("Content",txtContent.getText().toString());
		params[3] = new Param("OwnerType","1");
		params[4] = new Param("PhotoIds", ids);

		OkHttpClientManager.postAsyn(Settings.instance().getApiUrl()
				+ "/feeling/writefeeling", params,
				new OkHttpClientManager.ResultCallback<ResponseNewFeeling>() {

					@Override
					public void onError(Request request, Exception e) {
						Logger.info("editLabel.onError" + e.toString());
						WaitDialog.instance().hideWaitNote();
					}

					@Override
					public void onResponse(ResponseNewFeeling response) {
						WaitDialog.instance().hideWaitNote();
						if (response.isSuccess()) {
							Message msg=new Message();
							msg.what=MSG_FeelingSaved;
							msg.obj=response.getFeelingId();
							handler.sendMessage(msg);
						} else {
							txtMsg.setText(response.getErrorMessage());
							Logger.info("editLabel onResponse. message is: "
									+ response.getErrorMessage());
						}

					}

				});
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
						RequestBody.create(null, "6"));
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
						NewFeelingActivity.this,
						"正在上传",
						request,
						new com.longhuapuxin.common.OkHttpClientManager.ResultCallback<String>() {

							@Override
							public void onError(Request request, Exception e) {
								Toast.makeText(NewFeelingActivity.this,
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
									res.Photo.FileName.replace("\\",
											"/");
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
									Toast.makeText(NewFeelingActivity.this,
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
    	               if(degree!=0){
    	               bitmap = rotaingImageView(degree, bitmap);
    	               }
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
	
	public class MyImageAdapter extends U5BaseAdapter<ImageItem> {
		public MyImageAdapter(Context context, List<ImageItem> list) {
			super(context, list);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageItem item = mDatas.get(position);

			ViewHolder viewHolder = null;

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
							Intent intent = new Intent(
									"android.intent.action.PICK");
							intent.setDataAndType(
									MediaStore.Images.Media.INTERNAL_CONTENT_URI,
									"image/*");
							//intent.putExtra("output",Uri.fromFile(sdcardTempFile));
							//intent.putExtra("crop", "true");
							//intent.putExtra("aspectX", 1);// 裁剪框比例
							//intent.putExtra("aspectY", 1);
							//intent.putExtra("outputX", 2000);// 输出图片大小
							//intent.putExtra("outputY", 2000);
							startActivityForResult(intent, 101);

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
