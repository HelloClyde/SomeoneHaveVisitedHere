package com.fieldsoft.someonehavevisitedhere.view;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.example.someonehavevisitedhere.R;
import com.fieldsoft.someonehavevisitedhere.tool.DensityUtil;

import android.R.color;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Bitmap.CompressFormat;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class MyImageSelect extends View {

	int LineBold;
	int CrossBold;
	int NormalColor;
	int ProcessColor;
	int PressColor;
	int FailColor;
	Paint NormalPaint;
	Paint ProcessPaint;
	Paint FailPaint;

	Bitmap bitmap;
	Uri uri;
	String FileName;
	byte[] bytes;
	volatile int LoadingState;

	/**
	 * @return fileName
	 */
	public String getFileName() {
		return FileName;
	}

	/**
	 * @return loadingState
	 */
	public int getLoadingState() {
		return LoadingState;
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			MyImageSelect.this.invalidate();
		}
	};

	/**
	 * @return uri
	 */
	public Uri getUri() {
		return uri;
	}

	/**
	 * @param uri
	 *            要设置的 uri
	 */
	public void setUri(Uri uri) {
		this.uri = uri;
	}

	private static final String IMAGE_TYPE = "image/*";
	public static final int REQUEST_CODE_IMAGE_LIBARY = 0;
	public static final int REQUEST_CODE_CAPTURE_CAMEIA = 1;

	@SuppressLint("NewApi")
	public MyImageSelect(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		// TODO 自动生成的构造函数存根
		init();
	}

	public MyImageSelect(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO 自动生成的构造函数存根
		init();
	}

	public MyImageSelect(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO 自动生成的构造函数存根
		init();
	}

	public MyImageSelect(Context context) {
		super(context);
		// TODO 自动生成的构造函数存根
		init();
	}

	public void init() {
		this.NormalColor = Color.rgb(223, 224, 224);
		this.PressColor = Color.rgb(99, 99, 99);
		this.ProcessColor = Color.rgb(0, 255, 0);
		this.FailColor = Color.rgb(255, 0, 0);
		this.NormalPaint = new Paint();
		this.NormalPaint.setColor(this.NormalColor);
		this.ProcessPaint = new Paint();
		this.ProcessPaint.setColor(this.ProcessColor);
		this.FailPaint = new Paint();
		this.FailPaint.setColor(this.FailColor);
		this.bitmap = null;
		this.LoadingState = 0;
		ParameterInit();
	}

	private void ParameterInit() {
		this.LineBold = this.getWidth() / 20;
		if (this.LineBold < 1) {
			this.LineBold = 1;
		}
		this.CrossBold = this.getWidth() * 3 / 10;
		if (this.CrossBold < 1) {
			this.CrossBold = 1;
		}
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
		if (this.bitmap != null) {
			// 开始上传
			this.CalFileName();
			this.LoadingState = 1;
			this.invalidate();
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO 自动生成的方法存根
					MyImageSelect.this.UploadImage();
				}
			}).start();
		}
		else{
			this.invalidate();
		}
	}

	private void CalFileName() {
		// 使用内存流临时保存图片资源
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		this.bitmap.compress(CompressFormat.JPEG, 50, byteArrayOutputStream);
		this.bytes = byteArrayOutputStream.toByteArray();
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.bytes);
		String md5String = DensityUtil.getFileMD5(byteArrayInputStream);
		this.FileName = md5String;
	}

	private void UploadImage() {
		try {
			URL url = new URL(this.getContext().getString(R.string.ServerHostPath) + "/ImageUploadService.service");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			con.setRequestMethod("POST");
			con.setRequestProperty("FileName", this.FileName);
			/* 设置DataOutputStream */
			DataOutputStream ds = new DataOutputStream(con.getOutputStream());
			/* 取得文件的FileInputStream */
			ByteArrayInputStream fStream = new ByteArrayInputStream(this.bytes);
			/* 设置每次写入1024bytes */
			int bufferSize = 1024;
			byte[] buffer = new byte[bufferSize];
			int length = -1;
			/* 从文件读取数据至缓冲区 */
			while ((length = fStream.read(buffer)) != -1) {
				ds.write(buffer, 0, length);
			}
			fStream.close();
			ds.flush();
			ds.close();
			/* 取得Response内容 */
			InputStream is = con.getInputStream();
			int ch;
			StringBuffer b = new StringBuffer();
			while ((ch = is.read()) != -1) {
				b.append((char) ch);
			}
			is.close();
			Log.i("response", b.toString());
			if (b.toString().startsWith("success")) {
				this.LoadingState = 0;
			} else {
				this.LoadingState = -1;
			}
			Message msgRefresh = new Message();
			handler.sendMessage(msgRefresh);
		} catch (Exception e) {
			e.printStackTrace();
			this.LoadingState = -1;
			Message msgRefresh = new Message();
			handler.sendMessage(msgRefresh);
		}
	}

	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh) {
		ParameterInit();
	}

	@SuppressLint("DrawAllocation")
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (this.bitmap == null) {
			// 绘制中心十字
			for (int i = 0; i < this.LineBold; i++) {
				canvas.drawLine(this.getWidth() / 2 - this.CrossBold, this.getHeight() / 2 - this.LineBold / 2 + i,
						this.getWidth() / 2 + this.CrossBold, this.getHeight() / 2 - this.LineBold / 2 + i,
						this.NormalPaint);
				canvas.drawLine(this.getWidth() / 2 - this.LineBold / 2 + i, this.getHeight() / 2 - this.CrossBold,
						this.getWidth() / 2 - this.LineBold / 2 + i, this.getHeight() / 2 + this.CrossBold,
						this.NormalPaint);
			}
		} else {
			canvas.drawBitmap(this.bitmap, new Rect(0, 0, this.bitmap.getWidth() - 1, this.bitmap.getHeight() - 1),
					new Rect(0, 0, this.getWidth() - 1, this.getHeight() - 1), this.NormalPaint);
		}

		// 绘制边框
		for (int i = 0; i < this.LineBold; i++) {
			canvas.drawLine(0, i, this.getWidth() - 1, i, this.NormalPaint);
			canvas.drawLine(i, 0, i, this.getHeight() - 1, this.NormalPaint);
			canvas.drawLine(0, this.getHeight() - 1 - i, this.getWidth() - 1, this.getHeight() - 1 - i,
					this.NormalPaint);
			canvas.drawLine(this.getWidth() - 1 - i, 0, this.getWidth() - 1 - i, this.getHeight() - 1,
					this.NormalPaint);
		}

		// 绘制状态
		if (LoadingState == 1) {
			// 上传中绘制3个圆
			canvas.drawCircle(this.getWidth() / 4, this.getHeight() / 2, this.LineBold, this.ProcessPaint);
			canvas.drawCircle(this.getWidth() / 2, this.getHeight() / 2, this.LineBold, this.ProcessPaint);
			canvas.drawCircle(this.getWidth() * 3 / 4, this.getHeight() / 2, this.LineBold, this.ProcessPaint);
		} else if (LoadingState == -1) {
			// 上传失败绘制横杠
			for (int i = 0; i < this.LineBold; i++) {
				canvas.drawLine(this.getWidth() / 2 - this.CrossBold, this.getHeight() / 2 - this.LineBold / 2 + i,
						this.getWidth() / 2 + this.CrossBold, this.getHeight() / 2 - this.LineBold / 2 + i,
						this.FailPaint);
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int PointerCount = event.getPointerCount();
		if (PointerCount == 1) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				this.NormalPaint.setColor(this.PressColor);
				this.invalidate();
			} else if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
				this.NormalPaint.setColor(this.NormalColor);
				this.invalidate();
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				this.NormalPaint.setColor(this.NormalColor);
				this.invalidate();
				if (event.getX() >= 0 && event.getX() <= this.getWidth() - 1 && event.getY() >= 0
						&& event.getY() <= this.getHeight() - 1) {
					AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
					// builder.setIcon(R.drawable.ic_launcher);
					builder.setTitle(this.getContext().getString(R.string.selectwaytogetpicture));
					// 指定下拉列表的显示数据
					final String[] choose = { this.getContext().getString(R.string.camera),
							this.getContext().getString(R.string.picturelibary) };
					// 设置一个下拉的列表选择项
					builder.setItems(choose, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (which == 0) {
								// 使用相机获取照片
								String state = Environment.getExternalStorageState();
								if (state.equals(Environment.MEDIA_MOUNTED)) {
									Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
									((Activity) MyImageSelect.this.getContext())
											.startActivityForResult(getImageByCamera, REQUEST_CODE_CAPTURE_CAMEIA);
								} else {
									Toast.makeText(MyImageSelect.this.getContext(),
											MyImageSelect.this.getContext().getString(R.string.entersdcard),
											Toast.LENGTH_LONG).show();
								}
							} else if (which == 1) {
								// 使用图库获取照片
								Intent getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
								getAlbum.setType(IMAGE_TYPE);
								((Activity) MyImageSelect.this.getContext()).startActivityForResult(getAlbum,
										MyImageSelect.this.REQUEST_CODE_IMAGE_LIBARY);
							}
						}
					});
					builder.show();
				}
			}
		}
		return true;
	}
}
