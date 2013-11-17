package eu.ammw.archerscores;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HistoryOpenHelper extends SQLiteOpenHelper {
	// FIXME provide values for all these fields:
	private static final String DB_NAME = "archerscores.db";
	private static final String DB_TRAINING_TABLE_NAME = "Training";
	private static final String DB_RESULT_TABLE_NAME = "Score";
	private static final int DB_VERSION = 1;

	public HistoryOpenHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		validateValues();
		// TODO Auto-generated constructor stub
		// (factory null?)
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Database creation not implemented!");
		/*
		 * db.execSQL("CREATE TABLE " + DB_TABLE_NAME + " (" +
				KEY_WORD + " TEXT, " +
				KEY_DEFINITION + " TEXT);");*/
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		throw new UnsupportedOperationException("Database upgrade not implemented!");
	}
	
	private void validateValues() {
		throw new IllegalArgumentException("DB_NAME is "+DB_NAME+
				", DB_TRAINING_TABLE_NAME is "+DB_TRAINING_TABLE_NAME+
				", DB_RESULT_TABLE_NAME is "+DB_RESULT_TABLE_NAME+
				", version is "+DB_VERSION
				+ ". Are all these correct?");
	}
}
