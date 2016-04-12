package com.longhuapuxin.u5;

import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class AgreementActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_agreetment);
		TextView txtAgreement=(TextView)findViewById(R.id.txtAgreement);
		initHeader(R.string.agreement);
		txtAgreement.setText(Html.fromHtml(getResources().getString(R.string.agreementContent)));
		 
		
	}
}
