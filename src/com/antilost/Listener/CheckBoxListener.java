package com.antilost.Listener;

import com.example.antiLost.R;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class CheckBoxListener implements OnCheckedChangeListener {
		
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
				switch (buttonView.getId()) {
				case R.id.cb_near:
						if(isChecked){
							
						}
						else if(!isChecked){
							
						}
					break;

				case R.id.cb_far:
					if(isChecked){
						
					}
					else if(!isChecked){
						
					}
					break;
				}
	}
	

}
