package eu.ammw.android.targetpractice.util;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.text.format.Time;
import android.util.Log;
import eu.ammw.android.targetpractice.R;

public class TargetPracticeLogic implements Serializable {
	private static final long serialVersionUID = 1972542623306334288L;

	private transient Activity activity = null;
	
	private static final String LOG_TAG = "TP-L";
	
	private List<Integer> currentSeries = new ArrayList<Integer>();
	private List<Integer> currentTraining = new LinkedList<Integer>();
	private List<String> history = null;
	private List<List<Integer>> results = new LinkedList<List<Integer>>();
	private int totalScore = 0;
	private Time date = new Time(Time.getCurrentTimezone());
	
	public TargetPracticeLogic(Activity a) {
		Log.i(LOG_TAG, "Logic created!");
		date.setToNow();
		this.activity = a;
	}
	
	public void setActivity(Activity a) {
		activity = a;
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
		// TODO decide whether this should be retrieved every time
		//if (history==null) 
			retrieveHistory();
		return (history.size()==0 ? 
				new String[] {activity.getString(R.string.message_empty_history)} : 
				history.toArray(new String[] {null}));
	}
	
	private void retrieveHistory() {
		Log.d(LOG_TAG, "Retrieving history");
		HistoryDatabaseHelper dbHelper = new HistoryDatabaseHelper(activity);
		history = dbHelper.retrieveHistory(activity.getString(R.string.format_history), 
											activity.getString(R.string.format_date));
		dbHelper.close();
	}
	
	public int endSeries() {
		results.add(currentSeries);
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
			results.add(currentSeries);
			for (int val : currentSeries) 
				totalScore += val;
		}
		if (results.size() > 0) {
			Time endTime = new Time(Time.getCurrentTimezone());
			endTime.setToNow();
			HistoryDatabaseHelper dbHelper = new HistoryDatabaseHelper(activity);
			dbHelper.saveTraining(date, endTime, results);
			dbHelper.close();
			/*
			if (history != null)
				history.add(0, String.format(activity.getString(R.string.format_history), 
						date.format(activity.getString(R.string.format_date)), totalScore));
			 */
		}
		
		totalScore = 0;
		currentSeries = new ArrayList<Integer>();
		currentTraining.clear();
		results.clear();
		date.setToNow();
	}
	
	public void remove(int index, boolean compound) {
		Log.v(LOG_TAG, "Removing "+ (compound? "record" : "hit") +" at @"+index);
		if (compound) {
			totalScore -= currentTraining.get(index);
			currentTraining.remove(index);
			// Load series
			currentSeries = results.get(index);
			results.remove(index);
		}
		else currentSeries.remove(index);
	}
	
	public boolean canSubmitSeries() {
		return (currentSeries!=null && !currentSeries.isEmpty());
	}
	
	public boolean canSubmitTraining() {
		return canSubmitSeries() || !currentTraining.isEmpty();
	}
}
