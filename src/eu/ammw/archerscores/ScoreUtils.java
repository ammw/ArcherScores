package eu.ammw.archerscores;

import java.util.LinkedList;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class ScoreUtils {
	private static final String LOG_TAG = "AS-U";
	
	public static LinkedList<Integer> layoutToList(LinearLayout layout) {
		if (layout == null) return null;
		LinkedList<Integer> returnedList = new LinkedList<Integer>();
		int count = layout.getChildCount();
		Log.d(LOG_TAG, "Saving "+count+" entries");
		for (int i=0; i<count; i++) 
			returnedList.add(Integer.parseInt(((TextView)layout.getChildAt(i)).getText().toString()));
		return returnedList;
	}
	
	public static TextView textViewFromLabel(CharSequence score, Context context) {
		Log.v(LOG_TAG, "Creating element: "+score);
		TextView scoreView = new TextView(context);
		scoreView.setBackgroundColor(Color.LTGRAY);
		scoreView.setText(score);
		scoreView.setGravity(Gravity.CENTER);
		return scoreView;
	}
	
	public static LayoutParams getScoreViewParams() {
		LayoutParams params = 
				new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		params.setMargins(2, 2, 2, 2);
		return params;
	}
}
