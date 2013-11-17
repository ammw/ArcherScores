package eu.ammw.archerscores;

import java.util.LinkedList;
import java.util.List;

import android.util.Log;
import android.util.SparseArray;

public class ScoreLogic {
	private static ScoreLogic instance = new ScoreLogic();
	
	private static final String LOG_TAG = "AS-L";
	
	private List<Integer> currentSeries = new LinkedList<Integer>();
	private List<Integer> currentTraining = new LinkedList<Integer>();
	private SparseArray<List<Integer>> results = new SparseArray<List<Integer>>();
	private int totalScore = 0;
	
	private ScoreLogic() {
		if(instance != null)
			Log.e(LOG_TAG, "New logic created!");
	}
	
	public static ScoreLogic getInstance() {
		return instance;
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
	
	public int endSeries() {
		results.put(results.size(),currentSeries);
		int sum = 0;
		for (int val : currentSeries) 
			sum += val;
		currentTraining.add(sum);
		totalScore += sum;
		currentSeries.clear();
		return sum;
	}
	
	public void finishTraining() {
		/* TODO: it should
		 * do everything series button does
		 * save result in the db
		 * clear all training data
		 * clear the input tab
		 * set total to 0
		 */
	}
}
