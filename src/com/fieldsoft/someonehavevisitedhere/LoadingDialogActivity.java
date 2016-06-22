package com.fieldsoft.someonehavevisitedhere;

import com.example.someonehavevisitedhere.R;
import com.fieldsoft.someonehavevisitedhere.view.MyPositionView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;

public class LoadingDialogActivity extends Activity {
	static public final int THREAD_FINISHED = 1;
	static public final int RESULT_CODE = 2;
	TextView textViewText;
	LoadingThread runableThread;
	
	@Override  
    protected void onCreate(Bundle savedInstanceState)  
    {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.loading_layout);
        Intent intent = getIntent();
        this.textViewText = (TextView)findViewById(R.id.TextViewLoadingDialog);
        this.textViewText.setText(intent.getStringExtra("Text"));
        this.runableThread = (LoadingThread) intent.getSerializableExtra("Runnable");

        Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Intent intent = new Intent();
                intent.putExtra("Result", LoadingDialogActivity.this.runableThread.getResult()); 
                setResult(RESULT_CODE, intent);
                LoadingDialogActivity.this.finish();
            }
        };
        
        new Handler().postDelayed(new Runnable(){  
            @Override  
            public void run(){  
            	Intent intent = new Intent();
            	intent.putExtra("Result", "error:³¬Ê±"); 
                setResult(RESULT_CODE, intent);
                LoadingDialogActivity.this.finish();
            }
        }, 8000); 
        
        this.runableThread.setHandler(handler);
        
        new Thread(LoadingDialogActivity.this.runableThread).start();
    } 
}
