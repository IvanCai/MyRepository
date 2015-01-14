package com.antilost.activity;

import com.example.antiLost.R;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AddMsgActivity extends Activity {
	private EditText et_msg;
	SharedPreferences share;
	Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.msg_setting);
		et_msg = (EditText) findViewById(R.id.et_msg1);
		share = getSharedPreferences("MsgContent", MODE_PRIVATE);
		editor = share.edit();
		showContact();
	}

	private void showContact() {
		// TODO Auto-generated method stub
		String s1=	share.getString("Content", null);
		if(s1!=null){
			et_msg.setText(s1);
		}
	}

	public void saveMsgContent(View V) {
			String msgContent=et_msg.getText().toString();
			if(!msgContent.equals("")){
						editor.putString("Content", msgContent);
						editor.commit();
						Toast.makeText(getApplicationContext(), "短信保存成功",
								Toast.LENGTH_SHORT).show();
			}
	}
}
