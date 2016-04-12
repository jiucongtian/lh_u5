package com.longhuapuxin.common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Types;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Created by zhy on 15/8/17.
 */
public class OkHttpClientManager
{
    private static final String TAG = "OkHttpClientManager";

    private static OkHttpClientManager mInstance;
    private OkHttpClient mOkHttpClient;
    private Handler mDelivery;
    private Gson mGson;

    private HttpsDelegate mHttpsDelegate = new HttpsDelegate();
    private DownloadDelegate mDownloadDelegate = new DownloadDelegate();
    private DisplayImageDelegate mDisplayImageDelegate = new DisplayImageDelegate();
    private GetDelegate mGetDelegate = new GetDelegate();
    private UploadDelegate mUploadDelegate = new UploadDelegate();
    private PostDelegate mPostDelegate = new PostDelegate();

    private OkHttpClientManager()
    {
        mOkHttpClient = new OkHttpClient();
        //cookie enabled
        mOkHttpClient.setCookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER));
        mDelivery = new Handler(Looper.getMainLooper());
        mGson = new Gson();

        /*just for test !!!*/
        mOkHttpClient.setHostnameVerifier(new HostnameVerifier()
        {
            @Override
            public boolean verify(String hostname, SSLSession session)
            {
                return true;
            }
        });

    }

    public static OkHttpClientManager getInstance()
    {
        if (mInstance == null)
        {
            synchronized (OkHttpClientManager.class)
            {
                if (mInstance == null)
                {
                    mInstance = new OkHttpClientManager();
                }
            }
        }
        return mInstance;
    }

    public GetDelegate getGetDelegate()
    {
        return mGetDelegate;
    }

    public PostDelegate getPostDelegate()
    {
        return mPostDelegate;
    }

    private HttpsDelegate _getHttpsDelegate()
    {
        return mHttpsDelegate;
    }

    private DownloadDelegate _getDownloadDelegate()
    {
        return mDownloadDelegate;
    }

    private DisplayImageDelegate _getDisplayImageDelegate()
    {
        return mDisplayImageDelegate;
    }

    private UploadDelegate _getUploadDelegate()
    {
        return mUploadDelegate;
    }


    public static DisplayImageDelegate getDisplayImageDelegate()
    {
        return getInstance()._getDisplayImageDelegate();
    }

    public static DownloadDelegate getDownloadDelegate()
    {
        return getInstance()._getDownloadDelegate();
    }

    public static UploadDelegate getUploadDelegate()
    {
        return getInstance()._getUploadDelegate();
    }

    public static HttpsDelegate getHttpsDelegate()
    {
        return getInstance()._getHttpsDelegate();
    }

    /**
     * ============Get閺傞�涚┒閻ㄥ嫯顔栭梻顔芥煙瀵拷============
     */

    public static void getAsyn(String url, ResultCallback callback)
    {
        getInstance().getGetDelegate().getAsyn(url, callback, null);
    }

    public static void getAsyn(String url, ResultCallback callback, Object tag)
    {
        getInstance().getGetDelegate().getAsyn(url, callback, tag);
    }

    /**
     * ============POST閺傞�涚┒閻ㄥ嫯顔栭梻顔芥煙瀵拷===============
     */
    public static void postAsyn(String url, Param[] params, final ResultCallback callback)
    {
        getInstance().getPostDelegate().postAsyn(url, params, callback, null);
    }

    public static void postAsyn(String url, Map<String, String> params, final ResultCallback callback)
    {
        getInstance().getPostDelegate().postAsyn(url, params, callback, null);
    }

    public static void postAsyn(String url, String bodyStr, final ResultCallback callback)
    {
        getInstance().getPostDelegate().postAsyn(url, bodyStr, callback, null);
    }

    public static void postAsyn(String url, Param[] params, final ResultCallback callback, Object tag)
    {
        getInstance().getPostDelegate().postAsyn(url, params, callback, tag);
    }

    public static void postAsyn(String url, Map<String, String> params, final ResultCallback callback, Object tag)
    {
        getInstance().getPostDelegate().postAsyn(url, params, callback, tag);
    }

    public static void postAsyn(String url, String bodyStr, final ResultCallback callback, Object tag)
    {
        getInstance().getPostDelegate().postAsyn(url, bodyStr, callback, tag);
    }


    //=============娓氬灝鍩勯惃鍕問闂傤喗鏌熷蹇曠波閺夛拷===============


    private String guessMimeType(String path)
    {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null)
        {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }


    private Param[] validateParam(Param[] params)
    {
        if (params == null)
            return new Param[0];
        else return params;
    }

    private Param[] map2Params(Map<String, String> params)
    {
        if (params == null) return new Param[0];
        int size = params.size();
        Param[] res = new Param[size];
        Set<Map.Entry<String, String>> entries = params.entrySet();
        int i = 0;
        for (Map.Entry<String, String> entry : entries)
        {
            res[i++] = new Param(entry.getKey(), entry.getValue());
        }
        return res;
    }

    public  void deliveryResult(ResultCallback callback, Request request)
    {
        if (callback == null) callback = DEFAULT_RESULT_CALLBACK;
        final ResultCallback resCallBack = callback;
        //UI thread
        callback.onBefore(request);
        mOkHttpClient.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onFailure(final Request request, final IOException e)
            {
                sendFailedStringCallback(request, e, resCallBack);
            }

            @Override
            public void onResponse(final Response response)
            {
                try
                {
                    final String string = response.body().string();
                    if (resCallBack.mType == String.class)
                    {
                        sendSuccessResultCallback(string, resCallBack);
                    } else
                    {
                        Object o = mGson.fromJson(string, resCallBack.mType);
                        sendSuccessResultCallback(o, resCallBack);
                    }


                } catch (IOException e)
                {
                    sendFailedStringCallback(response.request(), e, resCallBack);
                } catch (com.google.gson.JsonParseException e)//Json鐟欙絾鐎介惃鍕晩鐠囷拷
                {
                    sendFailedStringCallback(response.request(), e, resCallBack);
                }

            }
        });
    }

    private void sendFailedStringCallback(final Request request, final Exception e, final ResultCallback callback)
    {
        mDelivery.post(new Runnable()
        {
            @Override
            public void run()
            {
                callback.onError(request, e);
                callback.onAfter();
            }
        });
    }

    private void sendSuccessResultCallback(final Object object, final ResultCallback callback)
    {
        mDelivery.post(new Runnable()
        {
            @Override
            public void run()
            {
                callback.onResponse(object);
                callback.onAfter();
            }
        });
    }

    private String getFileName(String path)
    {
        int separatorIndex = path.lastIndexOf("/");
        return (separatorIndex < 0) ? path : path.substring(separatorIndex + 1, path.length());
    }

    private Request buildPostFormRequest(String url, Param[] params, Object tag)
    {
        if (params == null)
        {
            params = new Param[0];
        }
        FormEncodingBuilder builder = new FormEncodingBuilder();
        for (Param param : params)
        {
            builder.add(param.key, param.value);
        }
        RequestBody requestBody = builder.build();


        Request.Builder reqBuilder = new Request.Builder();
        reqBuilder.url(url)
                .post(requestBody);

        if (tag != null)
        {
            reqBuilder.tag(tag);
        }
        return reqBuilder.build();
    }

    public static void cancelTag(Object tag)
    {
        getInstance()._cancelTag(tag);
    }

    private void _cancelTag(Object tag)
    {
        mOkHttpClient.cancel(tag);
    }

    public static OkHttpClient getClinet()
    {
        return getInstance().client();
    }

    public OkHttpClient client()
    {
        return mOkHttpClient;
    }


    public static abstract class ResultCallback<T>
    {
        Type mType;

        public ResultCallback()
        {
            mType = getSuperclassTypeParameter(getClass());
        }

        static Type getSuperclassTypeParameter(Class<?> subclass)
        {
            Type superclass = subclass.getGenericSuperclass();
            if (superclass instanceof Class)
            {
                throw new RuntimeException("Missing type parameter.");
            }
            ParameterizedType parameterized = (ParameterizedType) superclass;
            return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
        }

        public void onBefore(Request request)
        {
        }

        public void onAfter()
        {
        }

        public abstract void onError(Request request, Exception e);

        public abstract void onResponse(T response);
    }

    private final ResultCallback<String> DEFAULT_RESULT_CALLBACK = new ResultCallback<String>()
    {
        @Override
        public void onError(Request request, Exception e)
        {

        }

        @Override
        public void onResponse(String response)
        {

        }
    };


    public static class Param
    {
        public Param()
        {
        }

        public Param(String key, String value)
        {
            this.key = key;
            this.value = value;
        }

        String key;
        String value;
    }

    //====================PostDelegate=======================
    public class PostDelegate
    {
        private final MediaType MEDIA_TYPE_STREAM = MediaType.parse("application/octet-stream;charset=utf-8");
        private final MediaType MEDIA_TYPE_STRING = MediaType.parse("text/plain;charset=utf-8");

        public Response post(String url, Param[] params) throws IOException
        {
            return post(url, params, null);
        }

        /**
         * 閸氬本顒為惃鍑眔st鐠囬攱鐪�
         */
        public Response post(String url, Param[] params, Object tag) throws IOException
        {
            Request request = buildPostFormRequest(url, params, tag);
            Response response = mOkHttpClient.newCall(request).execute();
            return response;
        }

        public String postAsString(String url, Param[] params) throws IOException
        {
            return postAsString(url, params, null);
        }

        /**
         * 閸氬本顒為惃鍑眔st鐠囬攱鐪�
         */
        public String postAsString(String url, Param[] params, Object tag) throws IOException
        {
            Response response = post(url, params, tag);
            return response.body().string();
        }

        public void postAsyn(String url, Map<String, String> params, final ResultCallback callback)
        {
            postAsyn(url, params, callback, null);
        }

        public void postAsyn(String url, Map<String, String> params, final ResultCallback callback, Object tag)
        {
            Param[] paramsArr = map2Params(params);
            postAsyn(url, paramsArr, callback, tag);
        }

        public void postAsyn(String url, Param[] params, final ResultCallback callback)
        {
            postAsyn(url, params, callback, null);
        }

        /**
         * 瀵倹顒為惃鍒緊st鐠囬攱鐪�
         */
        public void postAsyn(String url, Param[] params, final ResultCallback callback, Object tag)
        {
            Request request = buildPostFormRequest(url, params, tag);
            deliveryResult(callback, request);
        }

        /**
         * 閸氬本顒為惃鍑眔st鐠囬攱鐪�:閻╁瓨甯寸亸鍝筼dyStr娴犮儱鍟撻崗銉嚞濮瑰倷缍�
         */
        public Response post(String url, String bodyStr) throws IOException
        {
            return post(url, bodyStr,null);
        }

        public Response post(String url, String bodyStr, Object tag) throws IOException
        {
            RequestBody body = RequestBody.create(MEDIA_TYPE_STRING, bodyStr);
            Request request = buildPostRequest(url, body, tag);
            Response response = mOkHttpClient.newCall(request).execute();
            return response;
        }

        /**
         * 閸氬本顒為惃鍑眔st鐠囬攱鐪�:閻╁瓨甯寸亸鍝筼dyFile娴犮儱鍟撻崗銉嚞濮瑰倷缍�
         */
        public Response post(String url, File bodyFile) throws IOException
        {
            return post(url, bodyFile,null);
        }

        public Response post(String url, File bodyFile, Object tag) throws IOException
        {
            RequestBody body = RequestBody.create(MEDIA_TYPE_STREAM, bodyFile);
            Request request = buildPostRequest(url, body, tag);
            Response response = mOkHttpClient.newCall(request).execute();
            return response;
        }

        /**
         * 閸氬本顒為惃鍑眔st鐠囬攱鐪�
         */
        public Response post(String url, byte[] bodyBytes) throws IOException
        {
            return post(url, bodyBytes,null);
        }

        public Response post(String url, byte[] bodyBytes, Object tag) throws IOException
        {
            RequestBody body = RequestBody.create(MEDIA_TYPE_STREAM, bodyBytes);
            Request request = buildPostRequest(url, body, tag);
            Response response = mOkHttpClient.newCall(request).execute();
            return response;
        }

        /**
         * 閻╁瓨甯寸亸鍝筼dyStr娴犮儱鍟撻崗銉嚞濮瑰倷缍�
         */
        public void postAsyn(String url, String bodyStr, final ResultCallback callback)
        {
            postAsyn(url, bodyStr, callback,null);
        }

        public void postAsyn(String url, String bodyStr, final ResultCallback callback, Object tag)
        {
            postAsynWithMediaType(url, bodyStr, MediaType.parse("text/plain;charset=utf-8"), callback, tag);
        }

        /**
         * 閻╁瓨甯寸亸鍝筼dyBytes娴犮儱鍟撻崗銉嚞濮瑰倷缍�
         */
        public void postAsyn(String url, byte[] bodyBytes, final ResultCallback callback)
        {
            postAsyn(url, bodyBytes, callback, null);
        }

        public void postAsyn(String url, byte[] bodyBytes, final ResultCallback callback, Object tag)
        {
            postAsynWithMediaType(url, bodyBytes, MediaType.parse("application/octet-stream;charset=utf-8"), callback, tag);
        }

        /**
         * 閻╁瓨甯寸亸鍝筼dyFile娴犮儱鍟撻崗銉嚞濮瑰倷缍�
         */
        public void postAsyn(String url, File bodyFile, final ResultCallback callback)
        {
            postAsyn(url, bodyFile, callback, null);
        }

        public void postAsyn(String url, File bodyFile, final ResultCallback callback, Object tag)
        {
            postAsynWithMediaType(url, bodyFile, MediaType.parse("application/octet-stream;charset=utf-8"), callback, tag);
        }

        /**
         * 閻╁瓨甯寸亸鍝筼dyStr娴犮儱鍟撻崗銉嚞濮瑰倷缍�
         */
        public void postAsynWithMediaType(String url, String bodyStr, MediaType type, final ResultCallback callback, Object tag)
        {
            RequestBody body = RequestBody.create(type, bodyStr);
            Request request = buildPostRequest(url, body, tag);
            deliveryResult(callback, request);
        }

        /**
         * 閻╁瓨甯寸亸鍝筼dyBytes娴犮儱鍟撻崗銉嚞濮瑰倷缍�
         */
        public void postAsynWithMediaType(String url, byte[] bodyBytes, MediaType type, final ResultCallback callback, Object tag)
        {
            RequestBody body = RequestBody.create(type, bodyBytes);
            Request request = buildPostRequest(url, body, tag);
            deliveryResult(callback, request);
        }

        /**
         * 閻╁瓨甯寸亸鍝筼dyFile娴犮儱鍟撻崗銉嚞濮瑰倷缍�
         */
        public void postAsynWithMediaType(String url, File bodyFile, MediaType type, final ResultCallback callback, Object tag)
        {
            RequestBody body = RequestBody.create(type, bodyFile);
            Request request = buildPostRequest(url, body, tag);
            deliveryResult(callback, request);
        }


        /**
         * post閺嬪嫰锟界嚝equest閻ㄥ嫭鏌熷▔锟�
         *
         * @param url
         * @param body
         * @return
         */
        private Request buildPostRequest(String url, RequestBody body, Object tag)
        {
            Request.Builder builder = new Request.Builder()
                    .url(url)
                    .post(body);
            if (tag != null)
            {
                builder.tag(tag);
            }
            Request request = builder.build();
            return request;
        }


    }

    //====================GetDelegate=======================
    public class GetDelegate
    {

        private Request buildGetRequest(String url, Object tag)
        {
            Request.Builder builder = new Request.Builder()
                    .url(url);

            if (tag != null)
            {
                builder.tag(tag);
            }

            return builder.build();
        }

        /**
         * 闁氨鏁ら惃鍕煙濞夛拷
         */
        public Response get(Request request) throws IOException
        {
            Call call = mOkHttpClient.newCall(request);
            Response execute = call.execute();
            return execute;
        }

        /**
         * 閸氬本顒為惃鍑t鐠囬攱鐪�
         */
        public Response get(String url) throws IOException
        {
            return get(url,null);
        }

        public Response get(String url, Object tag) throws IOException
        {
            final Request request = buildGetRequest(url, tag);
            return get(request);
        }


        /**
         * 閸氬本顒為惃鍑t鐠囬攱鐪�
         */
        public String getAsString(String url) throws IOException
        {
            return getAsString(url,null);
        }

        public String getAsString(String url, Object tag) throws IOException
        {
            Response execute = get(url, tag);
            return execute.body().string();
        }

        /**
         * 闁氨鏁ら惃鍕煙濞夛拷
         */
        public void getAsyn(Request request, ResultCallback callback)
        {
            deliveryResult(callback, request);
        }

        /**
         * 瀵倹顒為惃鍒t鐠囬攱鐪�
         */
        public void getAsyn(String url, final ResultCallback callback)
        {
            getAsyn(url, callback,null);
        }

        public void getAsyn(String url, final ResultCallback callback, Object tag)
        {
            final Request request = buildGetRequest(url, tag);
            getAsyn(request, callback);
        }
    }


    //====================UploadDelegate=======================

    /**
     * 娑撳﹣绱堕惄绋垮彠閻ㄥ嫭膩閸э拷
     */
    public class UploadDelegate
    {
        /**
         * 閸氬本顒為崺杞扮艾post閻ㄥ嫭鏋冩禒鏈电瑐娴硷拷:娑撳﹣绱堕崡鏇氶嚋閺傚洣娆�
         */
        public Response post(String url, String fileKey, File file, Object tag) throws IOException
        {
            return post(url, new String[]{fileKey}, new File[]{file}, null, tag);
        }

        /**
         * 閸氬本顒為崺杞扮艾post閻ㄥ嫭鏋冩禒鏈电瑐娴硷拷:娑撳﹣绱舵径姘嚋閺傚洣娆㈡禒銉ュ挤閹煎搫鐢玨ey-value鐎电櫢绱版稉缁樻煙濞夛拷
         */
        public Response post(String url, String[] fileKeys, File[] files, Param[] params, Object tag) throws IOException
        {
            Request request = buildMultipartFormRequest(url, files, fileKeys, params, tag);
            return mOkHttpClient.newCall(request).execute();
        }

        /**
         * 閸氬本顒為崡鏇熸瀮娴犳湹绗傛导锟�
         */
        public Response post(String url, String fileKey, File file, Param[] params, Object tag) throws IOException
        {
            return post(url, new String[]{fileKey}, new File[]{file}, params, tag);
        }

        /**
         * 瀵倹顒為崺杞扮艾post閻ㄥ嫭鏋冩禒鏈电瑐娴硷拷:娑撶粯鏌熷▔锟�
         */
        public void postAsyn(String url, String[] fileKeys, File[] files, Param[] params, ResultCallback callback, Object tag)
        {
            Request request = buildMultipartFormRequest(url, files, fileKeys, params, tag);
            deliveryResult(callback, request);
        }

        /**
         * 瀵倹顒為崺杞扮艾post閻ㄥ嫭鏋冩禒鏈电瑐娴硷拷:閸楁洘鏋冩禒鏈电瑝鐢箑寮弫棰佺瑐娴硷拷
         */
        public void postAsyn(String url, String fileKey, File file, ResultCallback callback, Object tag) throws IOException
        {
            postAsyn(url, new String[]{fileKey}, new File[]{file}, null, callback, tag);
        }

        /**
         * 瀵倹顒為崺杞扮艾post閻ㄥ嫭鏋冩禒鏈电瑐娴肩媴绱濋崡鏇熸瀮娴犳湹绗栭幖鍝勭敨閸忔湹绮琭orm閸欏倹鏆熸稉濠佺炊
         */
        public void postAsyn(String url, String fileKey, File file, Param[] params, ResultCallback callback, Object tag)
        {
            postAsyn(url, new String[]{fileKey}, new File[]{file}, params, callback, tag);
        }

        private Request buildMultipartFormRequest(String url, File[] files,
                                                  String[] fileKeys, Param[] params, Object tag)
        {
            params = validateParam(params);

            MultipartBuilder builder = new MultipartBuilder()
                    .type(MultipartBuilder.FORM);

            for (Param param : params)
            {
                builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + param.key + "\""),
                        RequestBody.create(null, param.value));
            }
            if (files != null)
            {
                RequestBody fileBody = null;
                for (int i = 0; i < files.length; i++)
                {
                    File file = files[i];
                    String fileName = file.getName();
                    fileBody = RequestBody.create(MediaType.parse(guessMimeType(fileName)), file);
                    //TODO 閺嶈宓侀弬鍥︽閸氬秷顔曠純鐢ntentType
                    builder.addPart(Headers.of("Content-Disposition",
                                    "form-data; name=\"" + fileKeys[i] + "\"; filename=\"" + fileName + "\""),
                            fileBody);
                }
            }

            RequestBody requestBody = builder.build();
            return new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .tag(tag)
                    .build();
        }

    }

    //====================DisplayImageDelegate=======================

    /**
     * 閸旂姾娴囬崶鍓у閻╃鍙�
     */
    public class DisplayImageDelegate
    {
        /**
         * 閸旂姾娴囬崶鍓у
         */
        public void displayImage(final ImageView view, final String url, final int errorResId, final Object tag)
        {
            final Request request = new Request.Builder()
                    .url(url)
                    .build();
            Call call = mOkHttpClient.newCall(request);
            call.enqueue(new Callback()
            {
                @Override
                public void onFailure(Request request, IOException e)
                {
                    setErrorResId(view, errorResId);
                }

                @Override
                public void onResponse(Response response)
                {
                    InputStream is = null;
                    try
                    {
                        is = response.body().byteStream();
                        ImageUtils.ImageSize actualImageSize = ImageUtils.getImageSize(is);
                        ImageUtils.ImageSize imageViewSize = ImageUtils.getImageViewSize(view);
                        int inSampleSize = ImageUtils.calculateInSampleSize(actualImageSize, imageViewSize);
                        try
                        {
                            is.reset();
                        } catch (IOException e)
                        {
                            response = mGetDelegate.get(url, tag);
                            is = response.body().byteStream();
                        }

                        BitmapFactory.Options ops = new BitmapFactory.Options();
                        ops.inJustDecodeBounds = false;
                        ops.inSampleSize = inSampleSize;
                        final Bitmap bm = BitmapFactory.decodeStream(is, null, ops);
                        mDelivery.post(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                view.setImageBitmap(bm);
                            }
                        });
                    } catch (Exception e)
                    {
                        setErrorResId(view, errorResId);

                    } finally
                    {
                        if (is != null) try
                        {
                            is.close();
                        } catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            });


        }

        public void displayImage(final ImageView view, String url)
        {
            displayImage(view, url, -1, null);
        }

        public void displayImage(final ImageView view, String url, Object tag)
        {
            displayImage(view, url, -1, tag);
        }

        private void setErrorResId(final ImageView view, final int errorResId)
        {
            mDelivery.post(new Runnable()
            {
                @Override
                public void run()
                {
                    view.setImageResource(errorResId);
                }
            });
        }
    }


    //====================DownloadDelegate=======================

    /**
     * 娑撳娴囬惄绋垮彠閻ㄥ嫭膩閸э拷
     */
    public class DownloadDelegate
    {
        /**
         * 瀵倹顒炴稉瀣祰閺傚洣娆�
         *
         * @param url
         * @param destFileDir 閺堫剙婀撮弬鍥︽鐎涙ê鍋嶉惃鍕瀮娴犺泛銇�
         * @param callback
         */
        public void downloadAsyn(final String url, final String destFileDir, final ResultCallback callback, Object tag)
        {
            final Request request = new Request.Builder()
                    .url(url)
                    .tag(tag)
                    .build();
            final Call call = mOkHttpClient.newCall(request);
            call.enqueue(new Callback()
            {
                @Override
                public void onFailure(final Request request, final IOException e)
                {
                    sendFailedStringCallback(request, e, callback);
                }

                @Override
                public void onResponse(Response response)
                {
                    InputStream is = null;
                    byte[] buf = new byte[2048];
                    int len = 0;
                    FileOutputStream fos = null;
                    try
                    {
                        is = response.body().byteStream();

                        File dir = new File(destFileDir);
                        if (!dir.exists())
                        {
                            dir.mkdirs();
                        }
                        File file = new File(dir, getFileName(url));
                        fos = new FileOutputStream(file);
                        while ((len = is.read(buf)) != -1)
                        {
                            fos.write(buf, 0, len);
                        }
                        fos.flush();
                        //婵″倹鐏夋稉瀣祰閺傚洣娆㈤幋鎰閿涘瞼顑囨稉锟芥稉顏勫棘閺侀璐熼弬鍥︽閻ㄥ嫮绮风�电鐭惧锟�
                        sendSuccessResultCallback(file.getAbsolutePath(), callback);
                    } catch (IOException e)
                    {
                        sendFailedStringCallback(response.request(), e, callback);
                    } finally
                    {
                        try
                        {
                            if (is != null) is.close();
                        } catch (IOException e)
                        {
                        }
                        try
                        {
                            if (fos != null) fos.close();
                        } catch (IOException e)
                        {
                        }
                    }

                }
            });
        }


        public void downloadAsyn(final String url, final String destFileDir, final ResultCallback callback)
        {
            downloadAsyn(url, destFileDir, callback, null);
        }
    }


    //====================HttpsDelegate=======================

    /**
     * Https閻╃鍙уΟ鈥虫健
     */
    public class HttpsDelegate
    {

        public void setCertificates(InputStream... certificates)
        {
            setCertificates(certificates, null, null);
        }

        public TrustManager[] prepareTrustManager(InputStream... certificates)
        {
            if (certificates == null || certificates.length <= 0) return null;
            try
            {

                CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                keyStore.load(null);
                int index = 0;
                for (InputStream certificate : certificates)
                {
                    String certificateAlias = Integer.toString(index++);
                    keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));
                    try
                    {
                        if (certificate != null)
                            certificate.close();
                    } catch (IOException e)

                    {
                    }
                }
                TrustManagerFactory trustManagerFactory = null;

                trustManagerFactory = TrustManagerFactory.
                        getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init(keyStore);

                TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

                return trustManagers;
            } catch (NoSuchAlgorithmException e)
            {
                e.printStackTrace();
            } catch (CertificateException e)
            {
                e.printStackTrace();
            } catch (KeyStoreException e)
            {
                e.printStackTrace();
            } catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;

        }

        public KeyManager[] prepareKeyManager(InputStream bksFile, String password)
        {
            try
            {
                if (bksFile == null || password == null) return null;

                KeyStore clientKeyStore = KeyStore.getInstance("BKS");
                clientKeyStore.load(bksFile, password.toCharArray());
                KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                keyManagerFactory.init(clientKeyStore, password.toCharArray());
                return keyManagerFactory.getKeyManagers();

            } catch (KeyStoreException e)
            {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e)
            {
                e.printStackTrace();
            } catch (UnrecoverableKeyException e)
            {
                e.printStackTrace();
            } catch (CertificateException e)
            {
                e.printStackTrace();
            } catch (IOException e)
            {
                e.printStackTrace();
            } catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }

        public void setCertificates(InputStream[] certificates, InputStream bksFile, String password)
        {
            try
            {
                TrustManager[] trustManagers = prepareTrustManager(certificates);
                KeyManager[] keyManagers = prepareKeyManager(bksFile, password);
                SSLContext sslContext = SSLContext.getInstance("TLS");

                sslContext.init(keyManagers, new TrustManager[]{new MyTrustManager(chooseTrustManager(trustManagers))}, new SecureRandom());
                mOkHttpClient.setSslSocketFactory(sslContext.getSocketFactory());
            } catch (NoSuchAlgorithmException e)
            {
                e.printStackTrace();
            } catch (KeyManagementException e)
            {
                e.printStackTrace();
            } catch (KeyStoreException e)
            {
                e.printStackTrace();
            }
        }

        private X509TrustManager chooseTrustManager(TrustManager[] trustManagers)
        {
            for (TrustManager trustManager : trustManagers)
            {
                if (trustManager instanceof X509TrustManager)
                {
                    return (X509TrustManager) trustManager;
                }
            }
            return null;
        }


        public class MyTrustManager implements X509TrustManager
        {
            private X509TrustManager defaultTrustManager;
            private X509TrustManager localTrustManager;

            public MyTrustManager(X509TrustManager localTrustManager) throws NoSuchAlgorithmException, KeyStoreException
            {
                TrustManagerFactory var4 = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                var4.init((KeyStore) null);
                defaultTrustManager = chooseTrustManager(var4.getTrustManagers());
                this.localTrustManager = localTrustManager;
            }


            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException
            {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException
            {
                try
                {
                    defaultTrustManager.checkServerTrusted(chain, authType);
                } catch (CertificateException ce)
                {
                    localTrustManager.checkServerTrusted(chain, authType);
                }
            }


            @Override
            public X509Certificate[] getAcceptedIssuers()
            {
                return new X509Certificate[0];
            }
        }

    }

    //====================ImageUtils=======================
    public static class ImageUtils
    {
        /**
         * 閺嶈宓両nputStream閼惧嘲褰囬崶鍓у鐎圭偤妾惃鍕啍鎼达箑鎷版妯哄
         *
         * @param imageStream
         * @return
         */
        public static ImageSize getImageSize(InputStream imageStream)
        {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(imageStream, null, options);
            return new ImageSize(options.outWidth, options.outHeight);
        }

        public static class ImageSize
        {
            int width;
            int height;

            public ImageSize()
            {
            }

            public ImageSize(int width, int height)
            {
                this.width = width;
                this.height = height;
            }

            @Override
            public String toString()
            {
                return "ImageSize{" +
                        "width=" + width +
                        ", height=" + height +
                        '}';
            }
        }

        public static int calculateInSampleSize(ImageSize srcSize, ImageSize targetSize)
        {
            // 濠ф劕娴橀悧鍥╂畱鐎硅棄瀹�
            int width = srcSize.width;
            int height = srcSize.height;
            int inSampleSize = 1;

            int reqWidth = targetSize.width;
            int reqHeight = targetSize.height;

            if (width > reqWidth && height > reqHeight)
            {
                // 鐠侊紕鐣婚崙鍝勭杽闂勫懎顔旀惔锕�鎷伴惄顔界垼鐎硅棄瀹抽惃鍕槷閻滐拷
                int widthRatio = Math.round((float) width / (float) reqWidth);
                int heightRatio = Math.round((float) height / (float) reqHeight);
                inSampleSize = Math.max(widthRatio, heightRatio);
            }
            return inSampleSize;
        }

        /**
         * 閺嶈宓両mageView閼剧兘锟藉倸缍嬮惃鍕竾缂傗晝娈戠�硅棄鎷版锟�
         *
         * @param view
         * @return
         */
        public static ImageSize getImageViewSize(View view)
        {

            ImageSize imageSize = new ImageSize();

            imageSize.width = getExpectWidth(view);
            imageSize.height = getExpectHeight(view);

            return imageSize;
        }

        /**
         * 閺嶈宓乿iew閼惧嘲绶遍張鐔告箿閻ㄥ嫰鐝惔锟�
         *
         * @param view
         * @return
         */
        private static int getExpectHeight(View view)
        {

            int height = 0;
            if (view == null) return 0;

            final ViewGroup.LayoutParams params = view.getLayoutParams();
            //婵″倹鐏夐弰鐤籖AP_CONTENT閿涘本顒濋弮璺烘禈閻楀洩绻曞▽鈥冲鏉炴枻绱漡etWidth閺嶈婀伴弮鐘虫櫏
            if (params != null && params.height != ViewGroup.LayoutParams.WRAP_CONTENT)
            {
                height = view.getWidth(); // 閼惧嘲绶辩�圭偤妾惃鍕啍鎼达拷
            }
            if (height <= 0 && params != null)
            {
                height = params.height; // 閼惧嘲绶辩敮鍐ㄧ湰閺傚洣娆㈡稉顓犳畱婢圭増妲戦惃鍕啍鎼达拷
            }

            if (height <= 0)
            {
                height = getImageViewFieldValue(view, "mMaxHeight");// 閼惧嘲绶辩拋鍓х枂閻ㄥ嫭娓舵径褏娈戠�硅棄瀹�
            }

            //婵″倹鐏夌�硅棄瀹虫潻妯绘Ц濞屸剝婀侀懢宄板絿閸掑府绱濋幉瀣亣閹锋冻绱濇担璺ㄦ暏鐏炲繐绠烽惃鍕啍鎼达拷
            if (height <= 0)
            {
                DisplayMetrics displayMetrics = view.getContext().getResources()
                        .getDisplayMetrics();
                height = displayMetrics.heightPixels;
            }

            return height;
        }

        /**
         * 閺嶈宓乿iew閼惧嘲绶遍張鐔告箿閻ㄥ嫬顔旀惔锟�
         *
         * @param view
         * @return
         */
        private static int getExpectWidth(View view)
        {
            int width = 0;
            if (view == null) return 0;

            final ViewGroup.LayoutParams params = view.getLayoutParams();
            //婵″倹鐏夐弰鐤籖AP_CONTENT閿涘本顒濋弮璺烘禈閻楀洩绻曞▽鈥冲鏉炴枻绱漡etWidth閺嶈婀伴弮鐘虫櫏
            if (params != null && params.width != ViewGroup.LayoutParams.WRAP_CONTENT)
            {
                width = view.getWidth(); // 閼惧嘲绶辩�圭偤妾惃鍕啍鎼达拷
            }
            if (width <= 0 && params != null)
            {
                width = params.width; // 閼惧嘲绶辩敮鍐ㄧ湰閺傚洣娆㈡稉顓犳畱婢圭増妲戦惃鍕啍鎼达拷
            }

            if (width <= 0)

            {
                width = getImageViewFieldValue(view, "mMaxWidth");// 閼惧嘲绶辩拋鍓х枂閻ㄥ嫭娓舵径褏娈戠�硅棄瀹�
            }
            //婵″倹鐏夌�硅棄瀹虫潻妯绘Ц濞屸剝婀侀懢宄板絿閸掑府绱濋幉瀣亣閹锋冻绱濇担璺ㄦ暏鐏炲繐绠烽惃鍕啍鎼达拷
            if (width <= 0)

            {
                DisplayMetrics displayMetrics = view.getContext().getResources()
                        .getDisplayMetrics();
                width = displayMetrics.widthPixels;
            }

            return width;
        }

        /**
         * 闁俺绻冮崣宥呯殸閼惧嘲褰噄mageview閻ㄥ嫭鐓囨稉顏勭潣閹冿拷锟�
         *
         * @param object
         * @param fieldName
         * @return
         */
        private static int getImageViewFieldValue(Object object, String fieldName)
        {
            int value = 0;
            try
            {
                Field field = ImageView.class.getDeclaredField(fieldName);
                field.setAccessible(true);
                int fieldValue = field.getInt(object);
                if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE)
                {
                    value = fieldValue;
                }
            } catch (Exception e)
            {
            }
            return value;

        }
    }


}

