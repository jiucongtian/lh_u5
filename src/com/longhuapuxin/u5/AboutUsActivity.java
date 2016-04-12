package com.longhuapuxin.u5;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

public class AboutUsActivity extends BaseActivity {
	private TextView txtAboutUs,txtFunction,txtVersion,currentVersion;
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_us);
		init();
	}

	private void init() {
		initHeader(R.string.title_about_us);
		
		currentVersion = (TextView)this.findViewById(R.id.currentVersion);
		String sVersionFormat = getResources().getString(R.string.about_us_current_version);  
		String version = getAppVersionName();
		String sFinalVersion = String.format(sVersionFormat, version); 
		currentVersion.setText(sFinalVersion);
		txtAboutUs=(TextView)this.findViewById(R.id.txtAboutUs);
		txtFunction=(TextView)this.findViewById(R.id.txtFunction);
		txtVersion=(TextView)this.findViewById(R.id.txtVersion);
		String html="<big>名称：</big>U5(有我)";
		CharSequence charSequence=Html.fromHtml(html); 
		txtAboutUs.setText(charSequence);
		html="<big>功能介绍：</big>用户通过U5建立直接联系，开展价值互动、合作创新、协同消费，释放个人知识、经验、技能和需求，通过技能交互货币化，创造社会价值、提高闲置资源配置和使用效率。"
				+"<p>"
				+"U5通过线下实体平台展示，销售商品和服务，提升线下体验、配送和售后等服务，着力线上线下互动，促进线上线下融合，优化消费路径、打破场景限制、提高服务水平，实现业绩增长。";
		charSequence=Html.fromHtml(html); 
		txtFunction.setText(charSequence);
		html="<big>版本日志：</big>2015-11-15产品上线，目前功能：线上提供技能交换平台，线下实体店消费，线上交友等。";
		charSequence=Html.fromHtml(html); 
		txtVersion.setText(charSequence);
		
	}
	

    /** 
     * 返回当前程序版本名 
     */  
    private String getAppVersionName() {  
        String versionName = "";  
        try {  
            // ---get the package info---  
            PackageManager pm = this.getPackageManager();  
            PackageInfo pi = pm.getPackageInfo(this.getPackageName(), 0);  
            versionName = pi.versionName;  
            if (versionName == null || versionName.length() <= 0) {  
                return "";  
            }  
        } catch (Exception e) {  
        }  
        return versionName;
    }  

}
