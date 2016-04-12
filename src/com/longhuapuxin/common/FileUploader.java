package com.longhuapuxin.common;

import java.io.File;
import java.util.List;

import android.content.Context;

import com.google.gson.Gson;
import com.longhuapuxin.common.MyOkHttpClientManager;
import com.longhuapuxin.common.OkHttpClientManager.ResultCallback;
import com.longhuapuxin.entity.ResponsePhoto;
import com.longhuapuxin.entity.ResponsePhoto.Photo;
import com.longhuapuxin.u5.Settings;
import com.longhuapuxin.u5.U5Application;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

public class FileUploader extends ResultCallback<String>{

	public interface UploadCallBack {
		public void uploadError();
		public void uploadSuccess();
	}
	
	private Context mContext;
	private List<String> mPathList;
	private int mIndex;
	private String mLabelCode;
	private UploadCallBack mListener;
	
	private static final MediaType MEDIA_TYPE_JPG = MediaType
			.parse("image/jpeg");
	
	public FileUploader(Context mContext, UploadCallBack listener) {
		super();
		this.mContext = mContext;
		mListener = listener;
	}

	
	public void uploadFiles(List<String> pathList, String labelCode) {
		mPathList = pathList;
		mLabelCode = labelCode;
		resetUploadIndex();
		uploadNext();
	} 
	
	private void resetUploadIndex() {
		mIndex = 0;
	}
	
	private void uploadNext() {
		int size = mPathList.size();
		if(mIndex < size) {
			String path = mPathList.get(mIndex);
			uploadFile(path, mLabelCode);
			mIndex++;
		} else {
			mListener.uploadSuccess();
		}
	}

	private void uploadFile(String path, String labelCode) {
		MultipartBuilder builder = new MultipartBuilder()
		.type(MultipartBuilder.FORM)
		.addPart(
				Headers.of("Content-Disposition",
						"form-data; name=\"UserId\""),
				RequestBody.create(null,
						String.valueOf(Settings.instance().getUserId())))
		.addPart(
				Headers.of("Content-Disposition",
						"form-data; name=\"Token\""),
				RequestBody.create(null, Settings.instance().getToken()))
		.addPart(
				Headers.of("Content-Disposition",
						"form-data; name=\"PhotoType\""),
				RequestBody.create(null, "4"))
		.addPart(
				Headers.of("Content-Disposition",
						"form-data; name=\"LabelCode\""),
				RequestBody.create(null, labelCode))
		.addPart(Headers.of("Content-Disposition",
					"form-data; name=\"File\" filename=\"label.jpg\""),
					RequestBody.create(MEDIA_TYPE_JPG, new File(path)));

		final Request request = new Request.Builder()
				.header("Authorization", "Client-ID " + "...")
				.url(Settings.instance().getApiUrl() + "/basic/uploadphoto")
				.post(builder.build()).build();

		MyOkHttpClientManager.deliveryResult(mContext, "正在上传图片", request, this);
	}

	@Override
	public void onError(Request request, Exception e) {
		Logger.info("FileUploader.onError" + e);
		
	}

	@Override
	public void onResponse(String response) {
		Logger.info("FileUploader onResponse. message is: " + response);
		
		U5Application app = (U5Application) mContext.getApplicationContext();
		Gson gson = app.getGson();	
		ResponsePhoto uploadResult = gson.fromJson(response, ResponsePhoto.class);
		
		if(uploadResult.isSuccess()) {
			Photo photo = uploadResult.getPhoto();
			String id = photo.getId();
			String filePath = photo.getFileName();
			String smallFilePath = photo.getSmallFileName();
			ImageUrlLoader.addPathCache(id, filePath, smallFilePath);
		}
		
		uploadNext();
	}

}
