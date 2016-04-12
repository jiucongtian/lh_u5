package com.longhuapuxin.u5;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

/**
 * Created by ZH on 2016/4/1.
 * Email zh@longhuapuxin.com
 */
public class ReleaseSkillProtocol extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_agreetment);
        TextView txtAgreement=(TextView)findViewById(R.id.txtAgreement);
        initHeader(R.string.releaseSkillProtocol);
        txtAgreement.setText(Html.fromHtml(getResources().getString(R.string.ReleasePolicy)));

    }
}
