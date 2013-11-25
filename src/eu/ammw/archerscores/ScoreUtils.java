package eu.ammw.archerscores;

import java.util.LinkedList;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ScoreUtils {
	private static final String LOG_TAG = "AS-U";
	private static final int SCORE_COLOR = Color.WHITE;
	
	public static LinkedList<Integer> layoutToList(LinearLayout layout) {
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
		button.setBackgroundColor(SCORE_COLOR);
		button.setText(score);
		button.setGravity(Gravity.CENTER);
		return button;
	}
}
