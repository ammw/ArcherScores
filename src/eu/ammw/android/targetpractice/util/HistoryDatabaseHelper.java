package eu.ammw.android.targetpractice.util;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.Time;
import android.util.Log;
import android.util.SparseArray;

public class HistoryDatabaseHelper extends SQLiteOpenHelper {
	private static final String LOG_TAG = "TP-DB";
	
	private static final String DB_NAME = "targetpractice.db";
	public static final String DB_TRAINING_TABLE_NAME = "Training";
	public static final String [] DB_TRAINING_COL_NAMES = 
		{"_id", "StartDate", "EndDate", "Note"};
	public static final String DB_RESULT_TABLE_NAME = "Score";
	public static final String [] DB_RESULT_COL_NAMES = 
		{"_id", "TrainingID", "Series", "Score"};
	private static final int DB_VERSION = 1;
	
	private static final String QUERY_CREATE_TRAINING = 
			"CREATE TABLE IF NOT EXISTS " + DB_TRAINING_TABLE_NAME + " (" +
			DB_TRAINING_COL_NAMES[0] + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
			DB_TRAINING_COL_NAMES[1] + " TEXT NOT NULL, " +
			DB_TRAINING_COL_NAMES[2] + " TEXT NOT NULL, " +
			DB_TRAINING_COL_NAMES[3] + " TEXT " + ");";
	
	private static final String QUERY_CREATE_RESULT =
			"CREATE TABLE IF NOT EXISTS " + DB_RESULT_TABLE_NAME + " (" +
					DB_RESULT_COL_NAMES[0] + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
					DB_RESULT_COL_NAMES[1] + " INTEGER NOT NULL, " +
					DB_RESULT_COL_NAMES[2] + " INTEGER NOT NULL, " +
					DB_RESULT_COL_NAMES[3] + " INTEGER NOT NULL, " +
					"FOREIGN KEY (" + DB_RESULT_COL_NAMES[1] + ") REFERENCES " +
						DB_TRAINING_TABLE_NAME + " (" + DB_TRAINING_COL_NAMES[0] + ") );";
	
	/* 
	 * SELECT StartDate, SUM(Score) 
	 * FROM Training INNER JOIN Score ON Training._id = Score.TrainingID 
	 * GROUP BY TrainingID ORDER BY StartDate DESC
	 */
	private static final String QUERY_RETRIEVE_HISTORY =
			"SELECT "+DB_TRAINING_COL_NAMES[1] + ", SUM(" + DB_RESULT_COL_NAMES[3] + 
			") FROM " + DB_TRAINING_TABLE_NAME + " INNER JOIN " + DB_RESULT_TABLE_NAME +
			" ON " + DB_TRAINING_TABLE_NAME+"."+DB_TRAINING_COL_NAMES[0] + " = " +
				DB_RESULT_TABLE_NAME+"."+DB_RESULT_COL_NAMES[1] +
			" GROUP BY " + DB_RESULT_COL_NAMES[1] + " ORDER BY " + DB_TRAINING_COL_NAMES[1] + " DESC ";
	
	public HistoryDatabaseHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(QUERY_CREATE_TRAINING);
		db.execSQL(QUERY_CREATE_RESULT);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		throw new UnsupportedOperationException("Database upgrade not implemented!");
	}
	
	/**
	 * Retrieves formatted result history.
	 * 
	 * @param historyFormat	format of a single history entry
	 * @param dateFormat	date format
	 * @return		String list containing formatted history entries.
	 */
	public List<String> retrieveHistory(String historyFormat, String dateFormat) {
		List<String> history = new LinkedList<String>();
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.rawQuery(QUERY_RETRIEVE_HISTORY, null);
		while (cursor.moveToNext()) {
			Time t = new Time();
			t.parse(cursor.getString(0));
			history.add(String.format(historyFormat, t.format(dateFormat), cursor.getInt(1)));
		}
		cursor.close();
		db.close();
		return history;
	}
	
	/**
	 * Saving training results to database.
	 * 
	 * @param startTime	Time when training started.
	 * @param endTime	Time when training finished.
	 * @param results	Training results.
	 * 
	 * @return			Row number of the inserted training.
	 */
	public long saveTraining(Time startTime, Time endTime, SparseArray<List<Integer>> results) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues vals = new ContentValues();
		vals.put(DB_TRAINING_COL_NAMES[1], startTime.format2445());
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
		return id;
	}
}
