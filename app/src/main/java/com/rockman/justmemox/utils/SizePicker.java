package com.rockman.justmemox.utils;



import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.rockman.justmemox.R;

public class SizePicker extends RelativeLayout{
	public boolean isLoaded=false;
	public Button b1,b2,b3,b4,b5,b6;
	public SizePicker(Context context, LinearLayout tools) {
		super(context);
		float scale = context.getResources().getDisplayMetrics().density;
		LayoutParams param = new LayoutParams(LayoutParams.MATCH_PARENT, (int) (80 * scale + 0.5f) );
		param.addRule(RelativeLayout.ABOVE, tools.getId()); 
		setLayoutParams(param);
		View sizes=View.inflate(context, R.layout.size_picker, null);
		addView(sizes, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
		b1=(Button)sizes.findViewById(R.id.button1);
		b2=(Button)sizes.findViewById(R.id.button2);
		b3=(Button)sizes.findViewById(R.id.button3);
		b4=(Button)sizes.findViewById(R.id.button4);
		b5=(Button)sizes.findViewById(R.id.button5);
		b6=(Button)sizes.findViewById(R.id.button6);
	}
}
