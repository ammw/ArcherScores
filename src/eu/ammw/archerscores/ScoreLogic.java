package eu.ammw.archerscores;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.Time;
import android.util.Log;
import android.util.SparseArray;

public class ScoreLogic {
	private static ScoreLogic instance = new ScoreLogic();
	private Activity activity = null;
	
	private static final String LOG_TAG = "AS-L";
	
	private List<Integer> currentSeries = new ArrayList<Integer>();
	private List<Integer> currentTraining = new LinkedList<Integer>();
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
		List<String> history = new ArrayList<String>();
		/*
		HistoryOpenHelper dbHelper = new HistoryOpenHelper(activity);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		
		
		db.close();
		dbHelper.close();
		*/
		if(history.isEmpty())
			history.add("Empty. Go shoot.");
		return history.toArray(new String[1]);
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
		if (!currentSeries.isEmpty()) 
			results.put(results.size(), currentSeries);
		Time endTime = new Time(Time.getCurrentTimezone());
		endTime.setToNow();
		HistoryOpenHelper dbHelper = new HistoryOpenHelper(activity);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues vals = new ContentValues();
		vals.put(HistoryOpenHelper.DB_TRAINING_COL_NAMES[1], date.format2445());
		vals.put(HistoryOpenHelper.DB_TRAINING_COL_NAMES[2], endTime.format2445());
		long id = db.insert(HistoryOpenHelper.DB_TRAINING_TABLE_NAME, HistoryOpenHelper.DB_TRAINING_COL_NAMES[3], vals);
		Log.i(LOG_TAG, "Inserted training as row #"+id);
		
		for (int i=0; i<results.size(); i++) {
			for (int shot : results.get(i)) {
				vals = new ContentValues();
				vals.put(HistoryOpenHelper.DB_RESULT_COL_NAMES[1], id);
				vals.put(HistoryOpenHelper.DB_RESULT_COL_NAMES[2], i);
				vals.put(HistoryOpenHelper.DB_RESULT_COL_NAMES[3], shot);
				db.insert(HistoryOpenHelper.DB_RESULT_TABLE_NAME, null, vals);
			}
		}
		
		db.close();
		dbHelper.close();
		
		totalScore = 0;
		currentSeries = new ArrayList<Integer>();
		currentTraining.clear();
		results.clear();
		date.setToNow();
	}
}
