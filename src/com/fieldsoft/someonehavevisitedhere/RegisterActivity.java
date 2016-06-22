package com.fieldsoft.someonehavevisitedhere;

import java.util.HashMap;
import java.util.Map;

import com.example.someonehavevisitedhere.R;
import com.fieldsoft.someonehavevisitedhere.tool.HttpHostConnection;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity {

	EditText editTextUserEmail;
	EditText editTextUserPassword;
	EditText editTextUserPasswordAgain;
	static public final int REQUEST_CODE = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_layout);
		this.editTextUserEmail = (EditText) findViewById(R.id.EditTextUserEmail);
		this.editTextUserPassword = (EditText) findViewById(R.id.EditTextUserPassword);
		this.editTextUserPasswordAgain = (EditText) findViewById(R.id.EditTextUserPasswordAgain);
	}
	
	public void Return(View view){
		this.finish();
	}

	public void Register(View view) {
		String UserEmail = this.editTextUserEmail.getText().toString();
		String UserPassword = this.editTextUserPassword.getText().toString();
		String UserPasswordAgain = this.editTextUserPasswordAgain.getText().toString();
		if (UserEmail.equals("") || UserPassword.equals("")) {
			Toast.makeText(this, this.getString(R.string.EmailOrPasswordIsEmpty), Toast.LENGTH_SHORT).show();
			return;
		}
		if (UserPassword.length() < 6) {
			Toast.makeText(this, this.getString(R.string.PasswordTooShort), Toast.LENGTH_LONG).show();
			return;
		}
		if (!UserPasswordAgain.equals(UserPassword)) {
			Toast.makeText(this, this.getString(R.string.PasswordNotSame), Toast.LENGTH_LONG).show();
			return;
		}

		Intent LoadingDialogIntent = new Intent(this, LoadingDialogActivity.class);
		LoadingDialogIntent.putExtra("Text", this.getString(R.string.loading));
		LoadingDialogIntent.putExtra("Runnable", new RegisterThread(UserEmail, UserPassword,this.getString(R.string.ServerHostPath)));
		this.startActivityForResult(LoadingDialogIntent, RegisterActivity.REQUEST_CODE);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RegisterActivity.REQUEST_CODE) {
			if (resultCode == LoadingDialogActivity.RESULT_CODE) {
				Bundle bundle = data.getExtras();
				String str = bundle.getString("Result");
				Toast.makeText(this, str.split(":")[1],Toast.LENGTH_LONG).show();
				if (str.startsWith("success")) {
					this.finish();
				}
			}
		}
	}

}

class RegisterThread implements LoadingThread {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3119926124570666665L;
	String UserEmail;
	String UserPassword;
	Handler handler;
	String response;
	String ServerHostPath;

	public RegisterThread(String e, String p,String serverHostPath) {
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
			String url = ServerHostPath + "UserRegisterService.service";
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