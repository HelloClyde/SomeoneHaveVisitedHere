package com.fieldsoft.someonehavevisitedhere;

import java.util.ArrayList;
import java.util.List;

import com.example.someonehavevisitedhere.R;
import com.fieldsoft.someonehavevisitedhere.view.MyImageSelect;
import com.fieldsoft.someonehavevisitedhere.view.MyViewPager;

import android.R.integer;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainFragmentActivity extends FragmentActivity {
	private MyViewPager mViewPager;
	private FragmentPagerAdapter mAdapter;
	private List<Fragment> mFragments = new ArrayList<Fragment>();

	/**
	 * 底部三个按钮和文字
	 */
	private Button ButtonPublishNote;
	private Button ButtonFind;
	private Button ButtonMe;

	/**
	 * @return mViewPager
	 */
	public MyViewPager getmViewPager() {
		return mViewPager;
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);

		this.mViewPager = (MyViewPager) findViewById(R.id.MainViewPager);
		Log.i("viewpagerinit", String.valueOf(this.mViewPager.getOffscreenPageLimit()));
		this.mViewPager.setOffscreenPageLimit(2);

		this.ButtonPublishNote = (Button) findViewById(R.id.ButtonPublishNote);
		this.ButtonFind = (Button) findViewById(R.id.ButtonFind);
		this.ButtonMe = (Button) findViewById(R.id.ButtonMe);

		mFragments.add(new PublishNoteFragment());
		mFragments.add(new FindFragment());
		mFragments.add(new MeFragment());

		this.mAdapter = new FragmentPagerAdapter(this.getSupportFragmentManager()) {

			@Override
			public int getCount() {
				return mFragments.size();
			}

			@Override
			public Fragment getItem(int arg0) {
				return mFragments.get(arg0);
			}
		};

		mViewPager.setAdapter(mAdapter);
		mViewPager.setCurrentItem(1);

		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			private int currentIndex;

			@SuppressLint("NewApi")
			@Override
			public void onPageSelected(int position) {
				resetTabBtn();
				MainFragmentActivity.this.mViewPager.setPagingEnabled(true);
				switch (position) {
				case 0:
					MainFragmentActivity.this.ButtonPublishNote.setCompoundDrawablesWithIntrinsicBounds(null,
							getDrawable(MainFragmentActivity.this, R.drawable.add_b), null, null);
					MainFragmentActivity.this.ButtonPublishNote.setTextColor(Color.rgb(69, 192, 27));
					MainFragmentActivity.this.mViewPager.setPagingEnabled(false);
					break;
				case 1:
					MainFragmentActivity.this.ButtonFind.setCompoundDrawablesWithIntrinsicBounds(null,
							getDrawable(MainFragmentActivity.this, R.drawable.find_b), null, null);
					MainFragmentActivity.this.ButtonFind.setTextColor(Color.rgb(69, 192, 27));
					break;
				case 2:
					MainFragmentActivity.this.ButtonMe.setCompoundDrawablesWithIntrinsicBounds(null,
							getDrawable(MainFragmentActivity.this, R.drawable.me_b), null, null);
					MainFragmentActivity.this.ButtonMe.setTextColor(Color.rgb(69, 192, 27));
					break;
				}

				currentIndex = position;
			}

			@SuppressWarnings("deprecation")
			@SuppressLint("NewApi")
			private Drawable getDrawable(Context context, int id) {
				if (Build.VERSION.SDK_INT >= 21) {
					return context.getDrawable(id);
				} else {
					return context.getResources().getDrawable(id);
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
			
			private void resetTabBtn() {
				MainFragmentActivity.this.ButtonPublishNote.setCompoundDrawablesWithIntrinsicBounds(null,
						getDrawable(MainFragmentActivity.this, R.drawable.add), null, null);
				MainFragmentActivity.this.ButtonPublishNote.setTextColor(Color.rgb(153, 153, 153));
				MainFragmentActivity.this.ButtonFind.setCompoundDrawablesWithIntrinsicBounds(null,
						getDrawable(MainFragmentActivity.this, R.drawable.find), null, null);
				MainFragmentActivity.this.ButtonFind.setTextColor(Color.rgb(153, 153, 153));
				MainFragmentActivity.this.ButtonMe.setCompoundDrawablesWithIntrinsicBounds(null,
						getDrawable(MainFragmentActivity.this, R.drawable.me), null, null);
				MainFragmentActivity.this.ButtonMe.setTextColor(Color.rgb(153, 153, 153));
			}
		});

	}


	/* （非 Javadoc）
	 * @see android.support.v4.app.FragmentActivity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO 自动生成的方法存根
		Log.i("imageselect", "FragmentActivity:" + arg1);
		super.onActivityResult(arg0, arg1, arg2);
		mFragments.get(0).onActivityResult(arg0, arg1, arg2);
	}


	public void ButtonPublishNoteOnClick(View view) {
		this.mViewPager.setCurrentItem(0);
	}

	public void ButtonFindOnClick(View view) {
		this.mViewPager.setCurrentItem(1);
	}

	public void ButtonMeOnClick(View view) {
		this.mViewPager.setCurrentItem(2);
	}

}
