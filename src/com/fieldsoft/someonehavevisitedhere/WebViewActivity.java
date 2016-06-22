package com.fieldsoft.someonehavevisitedhere;

import com.example.someonehavevisitedhere.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

public class WebViewActivity extends Activity {
	
	WebView WebView;

	/* （非 Javadoc）
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.web_layout);
		this.WebView = (WebView) this.findViewById(R.id.WebView);
		String webUrl = this.getIntent().getStringExtra("WebUri");
		this.WebView.loadUrl(webUrl);
	}
	
	public void Return(View view) {
		this.finish();
	}
}
