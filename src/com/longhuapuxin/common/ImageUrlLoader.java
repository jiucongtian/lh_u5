package com.longhuapuxin.common;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.lidroid.xutils.BitmapUtils;
import com.longhuapuxin.adapter.U5FetchImageAdapter;
import com.longhuapuxin.common.OkHttpClientManager.Param;
import com.longhuapuxin.db.bean.ImagePath;
import com.longhuapuxin.entity.ResponseFilePath;
import com.longhuapuxin.entity.ResponseFilePath.FileObject;
import com.longhuapuxin.senabimage.ImagePagerActivity;
import com.longhuapuxin.u5.R;
import com.longhuapuxin.u5.Settings;
import com.longhuapuxin.u5.U5Application;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


public class ImageUrlLoader {

    public interface OnUrlFetched {
        void success(String[] urlList);

        void failed();
    }

    private static HashSet<String> mPreImagesId;
    private static Map<String, ImagePath> mCachedImagePathMap;

    static {
        mPreImagesId = new HashSet<String>();
        mCachedImagePathMap = new HashMap<String, ImagePath>();
    }

    public static void addPathCache(String id, String origin, String small) {
        if (notNull(id) && notNull(origin) && notNull(small)) {
            mCachedImagePathMap.put(id, new ImagePath(origin, small));
        }
    }

    private static void updateAdapter(U5FetchImageAdapter adapter, Map<String, ImagePath> map) {
        adapter.setmImageMap(map);
        adapter.notifyDataSetChanged();
    }

    private static void addImage(String imageId) {
        if (notNull(imageId)) {
            if (mCachedImagePathMap.get(imageId) == null) {
                mPreImagesId.add(imageId);
            }
        }
    }

    public static void fetchImageUrl(String imageId, final ImageView view, final Context context) {
        fetchImageUrl(imageId, view, context, false);
    }

    public static void fetchImageUrl(String imageId, final ImageView view, final Context context, final boolean useOrigin) {
//        Logger.info("fetchImageUrl image id is:" + imageId);
        if (notNull(imageId)) {
            String cachedImagePath = null;
            if(mCachedImagePathMap.get(imageId) != null) {
                cachedImagePath = useOrigin? mCachedImagePathMap.get(imageId).origin : mCachedImagePathMap.get(imageId).small;
            }

            if (cachedImagePath != null) {
                BitmapUtils utils = ((U5Application) context.getApplicationContext()).getBitmapUtils();
                utils.display(view, Settings.instance().getImageUrl() + cachedImagePath);
            } else {
                Param[] params = new Param[3];
                params[0] = new Param("UserId", Settings.instance().getUserId());
                params[1] = new Param("Token", Settings.instance().getToken());
                params[2] = new Param("FileId", imageId);

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
                                FileObject object = response.getFiles().get(0);

                                String origin = object.getOriginal();
                                String small = object.getSmall();
                                if(small.equals("")) {
                                    small = origin;
                                }

                                if(notNull(origin) && notNull(small)) {
                                    addPathCache(object.getId(), origin, small);
                                }

                                BitmapUtils utils = ((U5Application) context.getApplicationContext()).getBitmapUtils();
                                String path = useOrigin? origin : small;
//                                if(TextUtils.isEmpty(path)) {
//                                    path = origin;
//                                }
                                utils.display(view, Settings.instance().getImageUrl() + path);
                            } else {
                                view.setImageResource(R.drawable.photo_error);
                            }
                        } else {
                            view.setImageResource(R.drawable.photo_error);
                        }
                    }

                });
            }
        } else {
            view.setImageResource(R.drawable.photo_error);
        }
    }

    public static void fetchImageUrl(final U5FetchImageAdapter adapter, List<String> idList) {
        if (idList != null && idList.size() > 0) {
            for (String id : idList) {
                addImage(id);
            }
            fetchImageUrl(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    public static void fetchImageUrl(final List<String> idList, final OnUrlFetched callBack) {
        if (callBack != null) {
            if (idList != null && idList.size() > 0) {
                for (String id : idList) {
                    addImage(id);
                }
                if (mPreImagesId.size() != 0) {

                    List<String> photos = new ArrayList<String>(mPreImagesId);
                    fetchUrls(photos, new OnUrlFetched() {
                        @Override
                        public void success(String[] urlList) {
                            callBack.success(urlList);
                        }

                        @Override
                        public void failed() {
                            callBack.failed();
                        }
                    });

                    mPreImagesId.clear();
                }
            } else {
                callBack.failed();
            }
        }
    }

    private static void fetchImageUrl(final U5FetchImageAdapter adapter) {
        if (mPreImagesId.size() != 0) {

            List<String> photos = new ArrayList<String>(mPreImagesId);
            fetchUrls(photos, new OnUrlFetched() {
                @Override
                public void success(String[] urlList) {
                    updateAdapter(adapter, mCachedImagePathMap);
                }

                @Override
                public void failed() {
                    updateAdapter(adapter, mCachedImagePathMap);
                }
            });

            mPreImagesId.clear();
        } else {
            updateAdapter(adapter, mCachedImagePathMap);
        }
    }


    private static void fetchUrls(final List<String> idList, final OnUrlFetched callBack) {

        if (callBack != null) {
            if (idList != null && idList.size() > 0) {
                String fileIds = "";

                for (String id : mPreImagesId) {
                    fileIds += id;
                    fileIds += ",";
                }

                fileIds = fileIds.substring(0, fileIds.length() - 1);
                Logger.info("fetchImageUrl image id is:" + fileIds);
                Param[] params = new Param[3];
                params[0] = new Param("UserId", Settings.instance().getUserId());
                params[1] = new Param("Token", Settings.instance().getToken());
                params[2] = new Param("FileId", fileIds);
                OkHttpClientManager.postAsyn(Settings.instance().getApiUrl() + "/basic/getphoto", params, new OkHttpClientManager.ResultCallback<ResponseFilePath>() {

                    @Override
                    public void onError(Request request, Exception e) {
                        Logger.info("load image onError: " + e);
                        callBack.failed();
                    }

                    @Override
                    public void onResponse(ResponseFilePath response) {
                        Logger.info("load image onResponse: " + response.getErrorMessage());
                        if (response.isSuccess()) {
                            for (FileObject item : response.getFiles()) {
                                String origin = item.getOriginal();
                                String small = item.getSmall();

                                if(small.equals("")) {
                                    small = origin;
                                }

                                if(notNull(origin) && notNull(small)) {
                                    addPathCache(item.getId(), origin, small);
                                }
                            }
                            String[] urls = new String[idList.size()];
                            for (int index = 0; index < idList.size(); index++) {
                                String id = idList.get(index);
                                if(mCachedImagePathMap.get(id) == null) {
                                    urls[index] = null;
                                } else {
                                    urls[index] = mCachedImagePathMap.get(id).small;
                                }
                            }

                            callBack.success(urls);
                        }
                        callBack.failed();
                    }
                });
            } else {
                callBack.failed();
            }
        }
    }


    private static boolean notNull(String string) {
        if (string != null && !string.isEmpty() && string != "null") {
            return true;
        } else {
            return false;
        }
    }
}
