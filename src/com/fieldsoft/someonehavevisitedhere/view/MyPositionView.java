package com.fieldsoft.someonehavevisitedhere.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.someonehavevisitedhere.R;
import com.fieldsoft.someonehavevisitedhere.tool.DensityUtil;
import com.fieldsoft.someonehavevisitedhere.tool.HttpHostConnection;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetricsInt;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class MyPositionView extends View {

	private static final int GETNETENABLE = 0;
	private static final int GETPOSTITUDE = 1;
	private static final int GETPOSSTRING = 2;

	Bitmap Icon;
	Rect IconSrcRect;
	Rect IconDesRect;
	Point TextPos;
	int TextPosDx;
	Point MovePointOld;
	int TextWidth;

	Paint TextPaint;
	Paint NormalPaint;

	// 位置信息
	Location UserLocation;
	LocationManager locationManager;

	// Text
	String ShowText;

	@SuppressLint("NewApi")
	public MyPositionView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		// TODO 自动生成的构造函数存根
		init();
	}

	public MyPositionView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO 自动生成的构造函数存根
		init();
	}

	public MyPositionView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO 自动生成的构造函数存根
		init();
	}

	public MyPositionView(Context context) {
		super(context);
		// TODO 自动生成的构造函数存根
		init();
	}

	private void init() {
		this.TextPaint = new Paint();
		this.TextPaint.setColor(Color.BLACK);
		this.TextPaint.setAntiAlias(true);
		this.TextPaint.setTextAlign(Align.LEFT);
		this.NormalPaint = new Paint();
		this.NormalPaint.setAntiAlias(true);

		this.Icon = BitmapFactory.decodeResource(getResources(), R.drawable.location);
		ParameterInit();

		// 获取位置线程
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO 自动生成的方法存根
				// 通过消息通知刷新控件
				locationManager = (LocationManager) MyPositionView.this.getContext()
						.getSystemService(Context.LOCATION_SERVICE);
				// 获取所有可用的位置提供器
				List<String> providers = locationManager.getProviders(true);
				if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
					ShowText = MyPositionView.this.getContext().getString(R.string.netposenable);
					Message msgNet = new Message();
					msgNet.what = GETNETENABLE;
					handler.sendMessage(msgNet);
					UserLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
					ShowText = UserLocation.getLatitude() + "," + UserLocation.getLongitude();
					Message msgNum = new Message();
					msgNum.what = GETPOSTITUDE;
					handler.sendMessage(msgNum);
					// 从谷歌地图获取位置
					String url = "http://maps.google.cn/maps/api/geocode/json";
					Map<String, String> GetMap = new HashMap<String, String>();
					GetMap.put("latlng", UserLocation.getLatitude() + "," + UserLocation.getLongitude());
					GetMap.put("sensor", "true");
					GetMap.put("language", "zh-CN");
					String response = new HttpHostConnection().sendGetData(url, GetMap, "utf-8");
					if (response != null) {
						try {
							JSONObject jsonObject = new JSONObject(response);
							// 获取results节点下的位置信息
							JSONArray resultArray = jsonObject.getJSONArray("results");
							if (resultArray.length() > 0) {
								JSONObject obj = resultArray.getJSONObject(0);
								// 取出格式化后的位置数据
								String address = obj.getString("formatted_address");
								ShowText = address;
								Message msgStr = new Message();
								msgStr.what = GETPOSSTRING;
								handler.sendMessage(msgStr);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				} else {
					UserLocation = null;
					ShowText = MyPositionView.this.getContext().getString(R.string.netposdisable);
					Message msgNet = new Message();
					msgNet.what = GETNETENABLE;
					handler.sendMessage(msgNet);
				}
			}
		}).start();

		// 只使用网络定位

	}

	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh) {
		ParameterInit();
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == GETNETENABLE || msg.what == GETPOSSTRING || msg.what == GETPOSTITUDE) {
				TextPosDx = 0;
				GetTextWidth();
				MyPositionView.this.invalidate();
			}
		}
	};

	private void ParameterInit() {
		this.TextPaint.setTextSize(this.getHeight() * 6 / 10);
		IconSrcRect = new Rect(0, 0, this.Icon.getWidth() - 1, this.Icon.getHeight() - 1);
		IconDesRect = new Rect(0, 0, this.getHeight(), this.getHeight());
		this.TextPos = new Point();
		this.TextPos.x = this.getHeight() + DensityUtil.dip2px(this.getContext(), 10);
		FontMetricsInt fm = this.TextPaint.getFontMetricsInt();
		this.TextPos.y = this.getHeight() / 2 - (fm.bottom - fm.top) / 2 - fm.top;
		this.TextPosDx = 0;
		this.MovePointOld = null;
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawText(this.ShowText, this.TextPos.x + this.TextPosDx, this.TextPos.y, this.TextPaint);
		this.NormalPaint.setColor(Color.WHITE);
		canvas.drawRect(this.IconDesRect, this.NormalPaint);
		canvas.drawBitmap(this.Icon, this.IconSrcRect, this.IconDesRect, this.NormalPaint);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int PointerCount = event.getPointerCount();
		if (PointerCount == 1) {
			if (event.getAction() == MotionEvent.ACTION_MOVE) {
				if (this.MovePointOld != null) {
					if (this.getWidth() - this.TextPos.x < this.TextWidth){
						int newPosDx = this.TextPosDx + (int) (event.getX() - this.MovePointOld.x);
						if (newPosDx <= 0 && newPosDx >= (this.getWidth() - this.TextPos.x) - this.TextWidth){
							this.TextPosDx = newPosDx;
							this.invalidate();
						}
					}
				}
				this.MovePointOld = new Point((int) event.getX(), (int) event.getY());
			}
			else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_OUTSIDE){
				this.MovePointOld = null;
			}
		}
		return true;
	}
	
	private int GetTextWidth(){
		this.TextWidth = (int) this.TextPaint.measureText(this.ShowText);
		Log.i("pos", String.valueOf(this.TextWidth));
		return this.TextWidth;
	}

	/**
	 * @return userLocation
	 */
	public Location getUserLocation() {
		return UserLocation;
	}
}
