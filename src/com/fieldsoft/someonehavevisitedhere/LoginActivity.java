package com.fieldsoft.someonehavevisitedhere;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.example.someonehavevisitedhere.R;
import com.fieldsoft.someonehavevisitedhere.model.AccountInfo;
import com.fieldsoft.someonehavevisitedhere.tool.HttpHostConnection;
import com.fieldsoft.someonehavevisitedhere.view.MyPositionView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

	static public final int REQUEST_CODE = 1;

	EditText editTextUserEmail;
	EditText editTextUserPassword;

	AccountInfo AccountInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		AccountInfo = new AccountInfo(this);
		AccountInfo.Read();
		if (AccountInfo.IsValue()) {
			setContentView(R.layout.ad_layout);
			new Thread(new Runnable() {
				@SuppressWarnings("static-access")
				public void run() {
					try {
						Thread.currentThread().sleep(2*1000);
						Message msgend = new Message();
						handler.sendMessage(msgend);
					} catch (InterruptedException e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}
				}
			}).start();
		} else {
			setContentView(R.layout.login_layout);
			this.editTextUserEmail = (EditText) findViewById(R.id.EditTextUserEmail);
			this.editTextUserPassword = (EditText) findViewById(R.id.EditTextUserPassword);
		}
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			LoginActivity.this.finish();
			Intent intent = new Intent(LoginActivity.this, MainFragmentActivity.class);
			startActivity(intent);
		}
	};

	public void Login(View view) {
		String UserEmail = this.editTextUserEmail.getText().toString();
		String UserPassword = this.editTextUserPassword.getText().toString();
		if (UserEmail.equals("") || UserPassword.equals("")) {
			Toast.makeText(this, this.getString(R.string.EmailOrPasswordIsEmpty), Toast.LENGTH_SHORT).show();
			return;
		} else {
			Intent LoadingDialogIntent = new Intent(this, LoadingDialogActivity.class);
			LoadingDialogIntent.putExtra("Text", this.getString(R.string.loading));
			LoadingDialogIntent.putExtra("Runnable",
					new LoginThread(UserEmail, UserPassword, this.getString(R.string.ServerHostPath)));
			this.startActivityForResult(LoadingDialogIntent, LoginActivity.REQUEST_CODE);
		}
	}

	public void Register(View view) {
		Intent registerIntent = new Intent(this, RegisterActivity.class);
		this.startActivity(registerIntent);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == LoginActivity.REQUEST_CODE) {
			if (resultCode == LoadingDialogActivity.RESULT_CODE) {
				Bundle bundle = data.getExtras();
				String str = bundle.getString("Result");
				// String str = "success";
				Toast.makeText(this.getApplicationContext(), str.split(":")[1], Toast.LENGTH_SHORT).show();
				if (str.startsWith("success")) {
					this.AccountInfo = new AccountInfo(this, this.editTextUserEmail.getText().toString(),
							this.editTextUserPassword.getText().toString());
					this.AccountInfo.Save();
					this.finish();
					Intent intent = new Intent(this, MainFragmentActivity.class);
					startActivity(intent);
				}
			}
		}
	}

}

class LoginThread implements LoadingThread {

	String UserEmail;
	String UserPassword;
	Handler handler;
	String response;
	String ServerHostPath;
	/**
	 * 
	 */
	private static final long serialVersionUID = 5349033350420486771L;

	public LoginThread(String e, String p, String serverHostPath) {
		// TODO 自动生成的构造函数存根
		this.UserEmail = e;
		this.UserPassword = p;
		this.ServerHostPath = serverHostPath;
	}

	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	@Override
	public void run() {
		// TODO 自动生成的方法存根
		try {
			String url = this.ServerHostPath + "AccountValidateService.service";
			Map<String, String> PostMap = new HashMap<String, String>();
			PostMap.put("UserEmail", UserEmail);
			PostMap.put("UserPassword", UserPassword);
			response = new HttpHostConnection().sendPostData(url, PostMap, "utf-8");
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			Log.d("debug", e.toString());
		}
		Message msgStr = new Message();
		handler.sendMessage(msgStr);
	}

	@Override
	public String getResult() {
		// TODO 自动生成的方法存根
		return this.response;
	}

}