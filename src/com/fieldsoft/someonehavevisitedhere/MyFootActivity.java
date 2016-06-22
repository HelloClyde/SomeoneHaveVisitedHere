package com.fieldsoft.someonehavevisitedhere;

import com.example.someonehavevisitedhere.R;
import com.fieldsoft.someonehavevisitedhere.vo.Note;
import com.google.gson.Gson;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MyFootActivity extends Activity {

	ListView MyFootListView;
	Note[] notes;
	String[] ListStrings;

	/*
	 * （非 Javadoc）
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		String gsonString = this.getIntent().getStringExtra("gson");
		notes = new Gson().fromJson(gsonString, Note[].class);
		this.ListStrings = new String[notes.length];
		for (int i = 0; i < this.notes.length; i++) {
			this.ListStrings[i] = this.notes[i].getContent();
		}

		this.setContentView(R.layout.my_foot_layout);
		this.MyFootListView = (ListView) this.findViewById(R.id.MyFootListView);
		this.MyFootListView
				.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, this.ListStrings));
		this.MyFootListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO 自动生成的方法存根
				//获取id
				int note_id = MyFootActivity.this.notes[position].getNote_Id();
				Intent intent = new Intent(MyFootActivity.this,WebViewActivity.class);
				intent.putExtra("WebUri", MyFootActivity.this.getString(R.string.ServerHostPath) + "/Note.jsp?NoteId=" + note_id);
				MyFootActivity.this.startActivity(intent);
			}
		});
	}

	public void Return(View view) {
		this.finish();
	}

}
