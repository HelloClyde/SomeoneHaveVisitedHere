package com.fieldsoft.someonehavevisitedhere;

import java.util.HashMap;
import java.util.Map;

import com.example.someonehavevisitedhere.R;
import com.fieldsoft.someonehavevisitedhere.model.AccountInfo;
import com.fieldsoft.someonehavevisitedhere.tool.HttpHostConnection;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MeFragment extends Fragment {
	RelativeLayout LogoutButton;
	RelativeLayout AboutButton;
	RelativeLayout MyFootButton;
	TextView UserNameTextView;
	TextView UserTypeTextView;
	public static final int MY_FOOT_CODE = 5;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		View view = inflater.inflate(R.layout.frame_me, container, false); 
		this.LogoutButton = (RelativeLayout)view.findViewById(R.id.LogoutButton);
		this.LogoutButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				new AccountInfo(v.getContext()).Delete();
				Toast.makeText(v.getContext().getApplicationContext(), v.getContext().getString(R.string.AlreadyLogout), Toast.LENGTH_LONG).show();
				getActivity().finish();
				Intent intent = new Intent(getActivity(), LoginActivity.class);
				startActivity(intent);
			}
		});
		
		this.AboutButton = (RelativeLayout)view.findViewById(R.id.AboutButton);
		this.AboutButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				//Toast.makeText(v.getContext(), v.getContext().getString(R.string.About), Toast.LENGTH_LONG).show();
				Intent intent = new Intent(MeFragment.this.getActivity(),WebViewActivity.class);
				intent.putExtra("WebUri", MeFragment.this.getString(R.string.ServerHostPath) + "/About.html");
				MeFragment.this.startActivity(intent);
			}
		});
		
		this.MyFootButton = (RelativeLayout) view.findViewById(R.id.MyFootButton);
		this.MyFootButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				//Toast.makeText(v.getContext(), v.getContext().getString(R.string.MyFoot), Toast.LENGTH_LONG).show();
				Intent LoadingDialogIntent = new Intent(MeFragment.this.getActivity(), LoadingDialogActivity.class);
				LoadingDialogIntent.putExtra("Text", MeFragment.this.getString(R.string.loading));
				AccountInfo accountInfo = new AccountInfo(MeFragment.this.getActivity());
				accountInfo.Read();
				String UserEmail = accountInfo.getUserEmail();
				String UserPassword = accountInfo.getUserPassword();
				LoadingDialogIntent.putExtra("Runnable",
						new GetMyFootThread(UserEmail, UserPassword, MeFragment.this.getString(R.string.ServerHostPath)));
				MeFragment.this.startActivityForResult(LoadingDialogIntent, MeFragment.MY_FOOT_CODE);
			}
		});
		
		this.UserNameTextView = (TextView)view.findViewById(R.id.UserName);
		this.UserTypeTextView = (TextView)view.findViewById(R.id.UserType);
		AccountInfo accountInfo = new AccountInfo(view.getContext());
		accountInfo.Read();
		if (accountInfo.IsValue()){
			this.UserNameTextView.setText(accountInfo.getUserEmail());
		}
		this.UserTypeTextView.setText(view.getContext().getString(R.string.User));
		return  view;
	}

	/* （非 Javadoc）
	 * @see android.support.v4.app.Fragment#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO 自动生成的方法存根
		//super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == MeFragment.MY_FOOT_CODE){
			Bundle bundle = data.getExtras();
			String str = bundle.getString("Result");
			if (str.startsWith("error")){
				Toast.makeText(this.getActivity(), str.split(":")[1], Toast.LENGTH_LONG).show();
			}
			else{
				//启动显示MyFootActivity
				Intent intent = new Intent(this.getActivity(), MyFootActivity.class);
				intent.putExtra("gson", str);
				startActivity(intent);
			}
		}
	}
	
	
}

class GetMyFootThread implements LoadingThread {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3592215912321764747L;
	
	String UserEmail;
	String UserPassword;
	Handler handler;
	String response;
	String ServerHostPath;

	public GetMyFootThread(String e, String p, String serverHostPath) {
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
			String url = this.ServerHostPath + "/GetPublishedNote.service";
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
