package eu.ammw.archerscores;

import static eu.ammw.archerscores.HistoryOpenHelper.DB_RESULT_COL_NAMES;
import static eu.ammw.archerscores.HistoryOpenHelper.DB_RESULT_TABLE_NAME;
import static eu.ammw.archerscores.HistoryOpenHelper.DB_TRAINING_COL_NAMES;
import static eu.ammw.archerscores.HistoryOpenHelper.DB_TRAINING_TABLE_NAME;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.Time;
import android.util.Log;
import android.util.SparseArray;

public class ScoreLogic {
	private static ScoreLogic instance = new ScoreLogic();
	private Activity activity = null;
	
	private static final String LOG_TAG = "AS-L";
	private static String HISTORY_FORMAT;
	private static String DATE_FORMAT;
	
	private List<Integer> currentSeries = new ArrayList<Integer>();
	private List<Integer> currentTraining = new LinkedList<Integer>();
	private List<String> history = null;
	private SparseArray<List<Integer>> results = new SparseArray<List<Integer>>();
	private int totalScore = 0;
	private Time date = new Time(Time.getCurrentTimezone());
	
	private ScoreLogic() {
		if(instance != null)
			Log.e(LOG_TAG, "New logic created!");
		date.setToNow();
	}
	
	public static ScoreLogic getInstance() {
		return instance;
	}
	
	public static ScoreLogic getInstance(Activity a) {
		setActivity(a);
		return instance;
	}
	
	public static void setActivity(Activity a) {
		instance.activity = a;
	}
	
	public int getTotal() {
		return totalScore;
	}
	
	public List<Integer> getCurrentSeries() {
		return currentSeries;
	}
	
	public List<Integer> getCurrentTraining() {
		return currentTraining;
	}
	
	public void hit(CharSequence score) {
		currentSeries.add(Integer.parseInt(score.toString()));
	}
	
	public String [] getHistory() {
		if (history==null) retrieveHistory();
		return (history.size()==0 ? 
				new String[] {activity.getString(R.string.message_empty_history)} : 
				history.toArray(new String[] {null}));
	}
	
	private void retrieveHistory() {
		Log.d(LOG_TAG, "Retrieving history");
		HistoryOpenHelper dbHelper = new HistoryOpenHelper(activity);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		/* 
		 * SELECT StartDate, SUM(Score) 
		 * FROM Training INNER JOIN Score ON Training._id = Score.TrainingID 
		 * GROUP BY TrainingID ORDER BY StartDate DESC
		 */
		String query = "SELECT "+DB_TRAINING_COL_NAMES[1] + ", SUM(" + DB_RESULT_COL_NAMES[3] + 
				") FROM " + DB_TRAINING_TABLE_NAME + " INNER JOIN " + DB_RESULT_TABLE_NAME +
				" ON " + DB_TRAINING_TABLE_NAME+"."+DB_TRAINING_COL_NAMES[0] + " = " +
					DB_RESULT_TABLE_NAME+"."+DB_RESULT_COL_NAMES[1] +
				" GROUP BY " + DB_RESULT_COL_NAMES[1] + " ORDER BY " + DB_TRAINING_COL_NAMES[1] + " DESC ";
		Cursor cursor = db.rawQuery(query, null);
		history = new LinkedList<String>();
		HISTORY_FORMAT = activity.getString(R.string.format_history);
		Log.v(LOG_TAG, "History format is "+HISTORY_FORMAT);
		DATE_FORMAT = activity.getString(R.string.format_date);
		Log.v(LOG_TAG, "Date format is "+DATE_FORMAT);
		while (cursor.moveToNext()) {
			Time t = new Time();
			t.parse(cursor.getString(0));
			history.add(String.format(HISTORY_FORMAT, t.format(DATE_FORMAT), cursor.getInt(1)));
		}
		cursor.close();
		db.close();
		dbHelper.close();
	}
	
	public int endSeries() {
		results.put(results.size(),currentSeries);
		int sum = 0;
		for (int val : currentSeries) 
			sum += val;
		currentTraining.add(sum);
		totalScore += sum;
		currentSeries = new ArrayList<Integer>();
		return sum;
	}
	
	public void finishTraining() {
		if (!currentSeries.isEmpty()) {
			results.put(results.size(), currentSeries);
			for (int val : currentSeries) 
				totalScore += val;
		}
		Time endTime = new Time(Time.getCurrentTimezone());
		endTime.setToNow();
		HistoryOpenHelper dbHelper = new HistoryOpenHelper(activity);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues vals = new ContentValues();
		vals.put(DB_TRAINING_COL_NAMES[1], date.format2445());
		vals.put(DB_TRAINING_COL_NAMES[2], endTime.format2445());
		long id = db.insert(DB_TRAINING_TABLE_NAME, DB_TRAINING_COL_NAMES[3], vals);
		Log.i(LOG_TAG, "Inserted training as row #"+id);
		
		for (int i=0; i<results.size(); i++) {
			for (int shot : results.get(i)) {
				vals = new ContentValues();
				vals.put(DB_RESULT_COL_NAMES[1], id);
				vals.put(DB_RESULT_COL_NAMES[2], i);
				vals.put(DB_RESULT_COL_NAMES[3], shot);
				db.insert(DB_RESULT_TABLE_NAME, null, vals);
			}
		}
		
		db.close();
		dbHelper.close();
		
		if (history != null)
			history.add(0, String.format(HISTORY_FORMAT, date.format(DATE_FORMAT), totalScore));
		
		totalScore = 0;
		currentSeries = new ArrayList<Integer>();
		currentTraining.clear();
		results.clear();
		date.setToNow();
	}
}
