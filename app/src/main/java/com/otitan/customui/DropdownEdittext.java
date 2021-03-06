package com.otitan.customui;


import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.AutoCompleteTextView.OnDismissListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.otitan.gyslfh.R;
import com.titan.model.UserInfo;

import java.util.List;

public class DropdownEdittext extends LinearLayout {
	
	private AutoCompleteTextView tv;
	//private Button btn;
	ImageView btn;
	Context mcontext;
	long dismissTime;//设置标记防止事件重复执行
	View DropdownEditText;

	public DropdownEdittext(Context context, AttributeSet attrs) {
		super(context, attrs);
		 if(!isInEditMode())
		 {

			 DropdownEditText=LayoutInflater.from(context).inflate(R.layout.dropdown_edittextview, this);
				this.mcontext = context;
				//DropdownEditText.setBackgroundResource(R.drawable.dropdown_edittext_nomal);
				//
				tv = (AutoCompleteTextView) findViewById(R.id.autotextview);
				tv.setDropDownBackgroundResource(R.color.white);
				//tv.setDropDownHeight()
				btn = (ImageView) findViewById(R.id.btn_dropdown);
				//设置匹配字符
				tv.setThreshold(100);
				//设置下拉宽度
				tv.setDropDownAnchor(this.getId());
				//Drawable dr=tv.getDropDownBackground();
				btn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
					  
						   if(!tv.isPopupShowing()&& (System.currentTimeMillis() - dismissTime > 100)){
								
								//btn.setBackgroundResource(R.drawable.btn_down);
							   btn.setImageResource(R.drawable.btn_down);
							   tv.showDropDown();
						   }
					}
				});

			 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
				 tv.setOnDismissListener(new OnDismissListener() {

                     @Override
                     public void onDismiss() {
                         //btn.setBackgroundResource(R.drawable.btn_up);
                          btn.setImageResource(R.drawable.btn_up);
                         dismissTime=System.currentTimeMillis();

                     }
                 });
			 }
		 }
		
		//
		/*tv.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				if(arg1)
				{
					DropdownEditText.setBackgroundResource(R.drawable.dropdown_edittext_select);
				}else {
					DropdownEditText.setBackgroundResource(R.drawable.dropdown_edittext_nomal);
				}
			}
		});*/
	 
	

	}

	public void setAdapter(String[] datalist) {
		//android.R.layout.simple_dropdown_item_1line
		if(datalist!=null&&datalist.length>0)
			
		{
	    btn.setEnabled(true);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(mcontext,   R.layout.dropdown_item,
				datalist);
		
		tv.setAdapter(adapter);
		}
		else {
			btn.setEnabled(false);
		}
	}
	public void setAdapter(List<String> datalist) {
		/*ArrayAdapter<String> adapter = new ArrayAdapter<String>(mcontext,  android.R.layout.simple_list_item_1,
				datalist);*/
		if(datalist!=null&&datalist.size()>0)
		{
			btn.setEnabled(true);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(mcontext, R.layout.dropdown_item,datalist);
			tv.setAdapter(adapter);	
		}
		else {
			btn.setEnabled(false);
		}
	}
	public void setMapAdapter(List<UserInfo> datalist) {
		/*ArrayAdapter<String> adapter = new ArrayAdapter<String>(mcontext,  android.R.layout.simple_list_item_1,
				datalist);*/
		if(datalist!=null&&datalist.size()>0)
		{
			btn.setEnabled(true);
			/*ArrayAdapter<String> adapter = new ArrayAdapter<String>(mcontext, R.layout.dropdown_item,datalist);
			tv.setAdapter(adapter);*/
		}
		else {
			btn.setEnabled(false);
		}
	}
	public void setCompletionHint(int number)
	{
		tv.setCompletionHint("最近登录用户"+number+"个");
	}
	/*@Override
	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
		if(gainFocus)
		{
			setBackgroundResource(R.drawable.dropdown_edittext_select);
		}else {
			setBackgroundResource(R.drawable.dropdown_edittext_nomal);
		}
	}*/
	//获取数据
	public String getText() {
		String tv_text = tv.getText().toString();
		if (tv_text == null) {
			tv_text = "";
		}
		return tv_text;
	}
	//设置数据
	public void setText(CharSequence text) {
		tv.setText(text);
	}


}
