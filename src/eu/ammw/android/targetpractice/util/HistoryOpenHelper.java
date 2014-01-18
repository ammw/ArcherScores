package eu.ammw.android.targetpractice.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HistoryOpenHelper extends SQLiteOpenHelper {
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
	
	public HistoryOpenHelper(Context context) {
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
}
