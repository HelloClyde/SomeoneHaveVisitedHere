package com.fieldsoft.someonehavevisitedhere;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.example.someonehavevisitedhere.R;
import com.fieldsoft.someonehavevisitedhere.model.AccountInfo;
import com.fieldsoft.someonehavevisitedhere.tool.HttpHostConnection;
import com.fieldsoft.someonehavevisitedhere.view.MyImageSelect;
import com.fieldsoft.someonehavevisitedhere.view.MyPositionView;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class PublishNoteFragment extends Fragment {

	MyImageSelect myImageSelect;
	Button PublishButton;
	TextView TimeTextView;
	TextView PublishText;
	MyPositionView myPositionView;
	View view;
	static public final int PUBLISH_CODE = 4;

	@SuppressLint("SimpleDateFormat")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		view = inflater.inflate(R.layout.frame_add, container, false);

		this.myImageSelect = (MyImageSelect) view.findViewById(R.id.ImageSelect);

		this.TimeTextView = (TextView) view.findViewById(R.id.Add_Time);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.TimeTextView.setText(simpleDateFormat.format(new Date()));
		this.TimeTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				PublishNoteFragment.this.TimeTextView.setText(simpleDateFormat.format(new Date()));
			}
		});

		this.PublishText = (TextView) view.findViewById(R.id.PublishText);

		this.myPositionView = (MyPositionView) view.findViewById(R.id.PublishPos);

		PublishButton = (Button) view.findViewById(R.id.PublishButton);
		PublishButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				if (PublishNoteFragment.this.PublishText.getText().toString().equals("")) {
					Toast.makeText(PublishNoteFragment.this.getActivity(),
							PublishNoteFragment.this.getString(R.string.PublishTextIsEmpty), Toast.LENGTH_LONG).show();
					return;
				}
				if (PublishNoteFragment.this.myPositionView.getUserLocation() == null) {
					Toast.makeText(PublishNoteFragment.this.getActivity(),
							PublishNoteFragment.this.getString(R.string.netposdisable), Toast.LENGTH_LONG).show();
					return;
				}
				Log.i("publish", String.valueOf(PublishNoteFragment.this.myImageSelect.getLoadingState()));
				if (PublishNoteFragment.this.myImageSelect.getLoadingState() != 0){
					Toast.makeText(PublishNoteFragment.this.getActivity(),
							PublishNoteFragment.this.getString(R.string.ImageUploadFail), Toast.LENGTH_LONG).show();
					return;
				}
				// 上传
				//获取各种资源
				AccountInfo accountInfo = new AccountInfo(PublishNoteFragment.this.getActivity());
				accountInfo.Read();
				String UserEmail = accountInfo.getUserEmail();
				String UserPassword = accountInfo.getUserPassword();
				String Content = PublishNoteFragment.this.PublishText.getText().toString();
				String ImageFilePath = PublishNoteFragment.this.myImageSelect.getFileName();
				String Date = PublishNoteFragment.this.TimeTextView.getText().toString();
				String LocationLatitude = String.valueOf(PublishNoteFragment.this.myPositionView.getUserLocation().getLatitude());
				String LocationLongitude = String.valueOf(PublishNoteFragment.this.myPositionView.getUserLocation().getLongitude());
				Intent LoadingDialogIntent = new Intent(PublishNoteFragment.this.getActivity(),
						LoadingDialogActivity.class);
				LoadingDialogIntent.putExtra("Text", PublishNoteFragment.this.getString(R.string.Publishing));
				LoadingDialogIntent.putExtra("Runnable",
						new PublishThread(UserEmail,UserPassword,Content,ImageFilePath,Date,LocationLatitude,LocationLongitude,PublishNoteFragment.this.getString(R.string.ServerHostPath)));
				PublishNoteFragment.this.startActivityForResult(LoadingDialogIntent, PublishNoteFragment.PUBLISH_CODE);

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
		try {
			Log.i("imageselect", "Fragment:" + String.valueOf(resultCode));
			super.onActivityResult(requestCode, resultCode, data);
			if (resultCode == -1 && (requestCode == MyImageSelect.REQUEST_CODE_CAPTURE_CAMEIA
					|| requestCode == MyImageSelect.REQUEST_CODE_IMAGE_LIBARY)) {
				Uri originalUri = data.getData();
				this.myImageSelect.setUri(originalUri);
				if (originalUri != null) {
					ContentResolver resolver = view.getContext().getContentResolver();
					this.myImageSelect.setBitmap(MediaStore.Images.Media.getBitmap(resolver, originalUri));
				} else {
					Bundle bundle = data.getExtras();
					if (bundle != null) {
						this.myImageSelect.setBitmap((android.graphics.Bitmap) bundle.get("data"));
					}
				}
			}
			else if (requestCode == PublishNoteFragment.PUBLISH_CODE){
				Bundle bundle = data.getExtras();
				String str = bundle.getString("Result");
				// String str = "success";
				Toast.makeText(this.getActivity().getApplicationContext(), str.split(":")[1], Toast.LENGTH_LONG).show();
				//清除数据
				MainFragmentActivity ParentFragmentActivity = (MainFragmentActivity) this.getActivity();
				ParentFragmentActivity.getmViewPager().setCurrentItem(1);
				this.PublishText.setText("");
				this.myImageSelect.setBitmap(null);
			}
		} catch (FileNotFoundException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}

}

class PublishThread implements LoadingThread {

	String UserEmail;
	String UserPassword;
	String Content;
	String ImageFilePath;
	String Date;
	String LocationLatitude;
	String LocationLongitude;

	Handler handler;
	String response;
	String ServerHostPath;

	public PublishThread(String userEmail, String userPassword, String content, String imageFilePath, String date,
			String locationLatitude, String locationLongitude, String serverHostPath) {
		// TODO 自动生成的构造函数存根
		this.UserEmail = userEmail;
		this.UserPassword = userPassword;
		this.Content = content;
		this.ImageFilePath = imageFilePath;
		this.Date = date;
		this.LocationLatitude = locationLatitude;
		this.LocationLongitude = locationLongitude;
		this.ServerHostPath = serverHostPath;
	}

	@Override
	public void run() {
		// TODO 自动生成的方法存根
		try {
			String url = this.ServerHostPath + "/DealWithPulishedNoteServer.service";
			Map<String, String> PostMap = new HashMap<String, String>();
			PostMap.put("UserEmail", UserEmail);
			PostMap.put("UserPassword", UserPassword);
			PostMap.put("Content", Content);
			PostMap.put("ImageFilePath", ImageFilePath);
			PostMap.put("Date", Date);
			PostMap.put("LocationLatitude", LocationLatitude);
			PostMap.put("LocationLongitude", LocationLongitude);
			response = new HttpHostConnection().sendPostData(url, PostMap, "utf-8");
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			Log.d("debug", e.toString());
		}
		Message msgStr = new Message();
		handler.sendMessage(msgStr);
	}

	@Override
	public Handler getHandler() {
		// TODO 自动生成的方法存根
		return handler;
	}

	@Override
	public void setHandler(Handler handler) {
		// TODO 自动生成的方法存根
		this.handler = handler;
	}

	@Override
	public String getResult() {
		// TODO 自动生成的方法存根
		return this.response;
	}

}
