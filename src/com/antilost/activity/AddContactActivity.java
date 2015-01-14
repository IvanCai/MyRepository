package com.antilost.activity;

import com.example.antiLost.R;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AddContactActivity extends Activity {

	private EditText contact1, contact2, contact3;
	SharedPreferences share;
	Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_setting);
		contact1 = (EditText) findViewById(R.id.contact1);
		contact2 = (EditText) findViewById(R.id.contact2);
		contact3 = (EditText) findViewById(R.id.contact3);
		share = getSharedPreferences("contact", MODE_PRIVATE);
		editor = share.edit();
		showContact();
	}

	public void saveContact(View v) {
		String s1=contact1.getText().toString();
		String s2=contact2.getText().toString();
		String s3=contact3.getText().toString();
		
		if (!s1.equals("")) {
			editor.putString("contact1", contact1.getText().toString());
			editor.commit();
			Toast.makeText(getApplicationContext(), "联系人1保存成功",
					Toast.LENGTH_SHORT).show();
		}
		if (!s2.equals("")) {
			editor.putString("contact2", contact2.getText().toString());
			editor.commit();
			Toast.makeText(getApplicationContext(), "联系人2保存成功",
					Toast.LENGTH_SHORT).show();
		}
		if (!s3.equals("")) {
			editor.putString("contact3", contact3.getText().toString());
			editor.commit();
			Toast.makeText(getApplicationContext(), "联系人3保存成功",
					Toast.LENGTH_SHORT).show();
		}
	}

	private void showContact() {
		String contact1 = share.getString("contact1", null);
		String contact2 = share.getString("contact2", null);
		String contact3 = share.getString("contact3", null);
		if (contact1 != null) {
			this.contact1.setText(contact1);
		}
		if (contact2 != null) {
			this.contact2.setText(contact2);
		}
		if (contact3 != null) {
			this.contact3.setText(contact3);
		}
	}
}
