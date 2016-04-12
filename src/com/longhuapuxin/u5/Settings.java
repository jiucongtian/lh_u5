package com.longhuapuxin.u5;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;

import com.longhuapuxin.common.Logger;
import com.longhuapuxin.db.bean.VerifyBankCard;
import com.longhuapuxin.entity.ResponseGetAccount;
import com.longhuapuxin.entity.ResponseKeptLabel.KeptLabel;
import com.longhuapuxin.entity.ResponseLabelCategory.LabelCategory;

/**
 * @author zh
 * @date 2015-9-21;
 */
public class Settings {
    private static Settings self = null;
    private final static String MSG_CHANNEL_ID = "msgChannelId";
    private final static String MSG_USER_ID = "msgUserId";
    private final static String SETTINGS_FILE = "settings.xml";
    private final static String ACCOUNT_USERID = "AccountId";
    private final static String TOKEN = "Token";
    private final static String FIRST_USE = "FirstUsed";
    private final static String VERIFYBankCard = "VerifyBankCard";
    private final static String SERVER_URL = BuildConfig.API_HOST;
    private final static String FILE_SERVER_URL = BuildConfig.ASSET_HOST;
    public final static String MQTTServer = BuildConfig.MQTT_HOST;
    private final static String BANK_SERVER_URL = "https://bank.longhuapuxin.com/notify_url.aspx";

    public final static int MQTTPort = 6002;
    public ResponseGetAccount.User User;
    public final static int MaxImageSize = 2000;
    public final static int MaxPortraitSize = 1000;
    public final static String ALBUM_PATH = Environment
            .getExternalStorageDirectory() + "/U5/";

    public static String getBankServerUrl() {
        return BANK_SERVER_URL;
    }

    private boolean loaded;
    private VerifyBankCard mVerify;

    // RAM contents
    private String userId = "";
    private String token = "";
    private Boolean isFirstUsed = true;
    private String apiUrl = SERVER_URL;
    private String imageUrl = FILE_SERVER_URL;
    private String mqttUrl = MQTTServer;
    private String msgUserId;
    private boolean manualywithdraw;
    private String msgChannelId;
    private String securityCode;
    private HashMap<String, String> info;
    private List<String> discountId;
    private List<LabelCategory> labelCategories;
    private List<KeptLabel> keptLabels;
    private String Portrait;

    public String getMqttUrl() {
        return mqttUrl;
    }

    public void setMqttUrl(String mqttUrl) {
        this.mqttUrl = mqttUrl;
    }

    public String getPortrait() {
        return Portrait;
    }

    public void setPortrait(String portrait) {
        Portrait = portrait;
    }

    public String City;
    public String CityCode;
    public Float Lontitude;
    public Float Latitude;
    public String address;
    private String myName;

    public String getMyName() {
        return myName;
    }

    public void setMyName(String myName) {
        this.myName = myName;
    }

    public List<LabelCategory> getLabelCategories() {
        return labelCategories;
    }

    public void setLabelCategories(List<LabelCategory> labelCategories) {
        this.labelCategories = labelCategories;
    }

    public List<KeptLabel> getKeptLabels() {
        return keptLabels;
    }

    public void setKeptLabels(List<KeptLabel> keptLabels) {
        this.keptLabels = keptLabels;
    }

    public List<String> getDiscountId() {
        return discountId;
    }

    public void setDiscountId(List<String> discountId) {
        this.discountId = discountId;
    }

    public HashMap<String, String> getInfo() {
        return info;
    }

    public void setInfo(HashMap<String, String> info) {
        this.info = info;
    }

    // generate private constructor.
    private Settings() {
    }

    public static Settings instance() {
        if (self == null) {
            self = new Settings();
        }
        return self;
    }

    // load contents from SharedPreferences.
    public void load(Context context) {
        Logger.debug("Settings->load");
        if (!loaded) {
            SharedPreferences preferences = context.getSharedPreferences(
                    SETTINGS_FILE, Context.MODE_PRIVATE);
            msgChannelId = preferences.getString(MSG_CHANNEL_ID, "");
            msgUserId = preferences.getString(MSG_USER_ID, "");
            userId = preferences.getString(ACCOUNT_USERID, "");
            token = preferences.getString(TOKEN, "");
            isFirstUsed = preferences.getBoolean(FIRST_USE, true);
            String txtVerifyBankCard = preferences.getString(VERIFYBankCard, "");
            if (!txtVerifyBankCard.equals("")) {
                this.mVerify = VerifyBankCard.fromString(txtVerifyBankCard);
            } else {
                this.mVerify = null;
            }
            this.Lontitude = preferences.getFloat("Lontitude", 0);
            this.Latitude = preferences.getFloat("Latitude", 0);
            this.City = preferences.getString("City", "");
            this.CityCode = preferences.getString("CityCode", "");
            loaded = true;
        }
    }

    // save contents from SharedPreferences.
    public void save(Context context) {
        Logger.debug("Settings->save");
        SharedPreferences preferences = context.getSharedPreferences(
                SETTINGS_FILE, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putString(MSG_CHANNEL_ID, msgChannelId);
        editor.putString(MSG_USER_ID, msgUserId);
        editor.putString(MSG_CHANNEL_ID, msgChannelId);
        editor.putString(MSG_USER_ID, msgUserId);

        editor.putString(ACCOUNT_USERID, userId);
        editor.putString(TOKEN, token);
        editor.putBoolean(FIRST_USE, isFirstUsed);
        editor.putString("City", City);
        editor.putFloat("Lontitude", Lontitude);
        editor.putFloat("Latitude", Latitude);
        editor.putString("CityCode", CityCode);

        editor.commit();
    }

    public void saveLocation(Context context) {
        Logger.debug("Settings->savelocation");
        SharedPreferences preferences = context.getSharedPreferences(
                SETTINGS_FILE, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putString("City", City);
        editor.putFloat("Lontitude", Lontitude);
        editor.putFloat("Latitude", Latitude);
        editor.putString("CityCode", CityCode);
        editor.commit();
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        Logger.debug("U5 token: " + token);
        this.token = token;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMsgChannelId() {
        return msgChannelId;
    }

    public void setMsgChannelId(String msgChannelId) {
        this.msgChannelId = msgChannelId;
    }

    public String getMsgUserId() {
        return msgUserId;
    }

    public void setMsgUserId(String msgUserId) {
        this.msgUserId = msgUserId;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getCityCode() {
        return CityCode;
    }

    public void setCityCode(String cityCode) {
        CityCode = cityCode;
    }

    public Float getLontitude() {
        return Lontitude;
    }

    public void setLontitude(Float lontitude) {
        Lontitude = lontitude;
    }

    public Float getLatitude() {
        return Latitude;
    }

    public void setLatitude(Float latitude) {
        Latitude = latitude;
    }

    public VerifyBankCard getmVerify() {
        return mVerify;
    }

    public Boolean getIsFirstUsed() {
        return isFirstUsed;
    }

    public void setIsFirstUsed(Boolean isFirstUsed) {
        this.isFirstUsed = isFirstUsed;
    }

    public void setmVerify(Context context, VerifyBankCard mVerify) {
        this.mVerify = mVerify;
        SharedPreferences preferences = context.getSharedPreferences(
                SETTINGS_FILE, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        if (mVerify == null) {
            editor.putString(VERIFYBankCard, "");
        } else {
            editor.putString(VERIFYBankCard, mVerify.toString());
        }


        editor.commit();
    }

    public void setManualyWithDraw(boolean manualyWithDraw) {
        manualywithdraw = manualyWithDraw;
    }

    public boolean IsManualyWithdraw() {
        return manualywithdraw;
    }
}
