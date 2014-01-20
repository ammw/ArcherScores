package eu.ammw.android.targetpractice.util;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class TargetPracticeUtils {
	private static final String LOG_TAG = "TP-U";
	private static final int SCORE_COLOR = Color.WHITE;
	
	public static List<Integer> layoutToList(LinearLayout layout) {
		if (layout == null) return null;
		LinkedList<Integer> returnedList = new LinkedList<Integer>();
		int count = layout.getChildCount();
		Log.d(LOG_TAG, "Saving "+count+" entries");
		for (int i=0; i<count; i++) 
			returnedList.add(Integer.parseInt(((TextView)layout.getChildAt(i)).getText().toString()));
		return returnedList;
	}
	
	public static Button buttonFromLabel(CharSequence score, Context context) {
		Log.v(LOG_TAG, "Creating element: "+score);
		Button button = new Button(context);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.setMargins(0, 0, 5, 0); // set right margin to 5 to separate elements
		button.setLayoutParams(params);
		button.setBackgroundColor(SCORE_COLOR);
		button.setText(score);
		button.setGravity(Gravity.CENTER);
		return button;
	}
}
