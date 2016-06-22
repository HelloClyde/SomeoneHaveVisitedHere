package com.fieldsoft.someonehavevisitedhere;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.someonehavevisitedhere.R;
import com.fieldsoft.someonehavevisitedhere.model.AccountInfo;
import com.fieldsoft.someonehavevisitedhere.tool.HttpHostConnection;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class FindFragment extends Fragment {
	RelativeLayout NearFootButton;
	public static final int NEAR_FOOT_CODE = 6;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		View view = inflater.inflate(R.layout.frame_find, container, false);
		this.NearFootButton = (RelativeLayout) view.findViewById(R.id.NearFootButton);
		this.NearFootButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				Intent LoadingDialogIntent = new Intent(FindFragment.this.getActivity(), LoadingDialogActivity.class);
				LoadingDialogIntent.putExtra("Text", FindFragment.this.getString(R.string.loading));
				AccountInfo accountInfo = new AccountInfo(FindFragment.this.getActivity());
				accountInfo.Read();
				String UserEmail = accountInfo.getUserEmail();
				String UserPassword = accountInfo.getUserPassword();
				// 获取位置
				LocationManager locationManager = (LocationManager) FindFragment.this.getActivity()
						.getSystemService(Context.LOCATION_SERVICE);
				List<String> providers = locationManager.getProviders(true);
				if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
					Location UserLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
					LoadingDialogIntent.putExtra("Runnable",
							new GetNearFootThread(UserEmail, UserPassword, UserLocation.getLatitude(),
									UserLocation.getLongitude(), FindFragment.this.getString(R.string.ServerHostPath)));
					FindFragment.this.startActivityForResult(LoadingDialogIntent, FindFragment.NEAR_FOOT_CODE);
				} else {
					Toast.makeText(FindFragment.this.getActivity(), FindFragment.this.getString(R.string.netposdisable),
							Toast.LENGTH_LONG).show();
				}
			}
		});
		return view;
	}

	/*
	 * （非 Javadoc）
	 * 
	 * @see android.support.v4.app.Fragment#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO 自动生成的方法存根
		// super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == FindFragment.NEAR_FOOT_CODE) {
			Bundle bundle = data.getExtras();
			String str = bundle.getString("Result");
			if (str.startsWith("error")) {
				Toast.makeText(this.getActivity(), str.split(":")[1], Toast.LENGTH_LONG).show();
			} else {
				// 启动显示MyFootActivity
				Intent intent = new Intent(this.getActivity(), MyFootActivity.class);
				intent.putExtra("gson", str);
				startActivity(intent);
			}
		}
	}
}

class GetNearFootThread implements LoadingThread {

	/**
	 * 
	 */
	private static final long serialVersionUID = -555135504152226176L;
	String UserEmail;
	String UserPassword;
	Handler handler;
	String response;
	String ServerHostPath;
	String Latitude;
	String Longitude;

	public GetNearFootThread(String e, String p, double latitude, double longitude, String serverHostPath) {
		// TODO 自动生成的构造函数存根
		this.UserEmail = e;
		this.UserPassword = p;
		this.Latitude = String.valueOf(latitude);
		this.Longitude = String.valueOf(longitude);
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
			PostMap.put("LocationLatitude", this.Latitude);
			PostMap.put("LocationLongitude", this.Longitude);
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