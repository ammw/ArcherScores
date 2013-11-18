package eu.ammw.archerscores;


import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ScoreMainActivity extends FragmentActivity implements ActionBar.OnNavigationListener {
	
	private static final String LOG_TAG = "AS-Main";
	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current dropdown position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	
	private final ScoreLogic logic = ScoreLogic.getInstance(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_score_main);

		// Set up the action bar to show a dropdown list.
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		// Set up the dropdown list navigation in the action bar.
		actionBar.setListNavigationCallbacks(
				// Specify a SpinnerAdapter to populate the dropdown list.
				new ArrayAdapter<String>(
						actionBar.getThemedContext(),
						android.R.layout.simple_list_item_1,
						android.R.id.text1,
						new String[] {
							getString(R.string.title_section_input),	// 0
							getString(R.string.title_section_history),	// 1
						}),
						this);
	}


	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current dropdown position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current dropdown position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM,
				getActionBar().getSelectedNavigationIndex());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.score_main, menu);
		return true;
	}

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		// When the given dropdown item is selected, show its contents in the
		// container view.
		Fragment fragment = new ScoreFragment();
		getSupportFragmentManager().beginTransaction()
			.replace(R.id.container, fragment)
			.commit();
		return true;
	}
	
	public void onInputSeriesButtonClicked(View view) {
		Log.d(LOG_TAG, "series button clicked");
		int sum = logic.endSeries();
		((TextView)findViewById(R.id.scoreTotalValueView)).setText(String.valueOf(logic.getTotal()));
		((LinearLayout)findViewById(R.id.seriesInternalLayout)).removeAllViews();
		((LinearLayout)findViewById(R.id.resultsInternalLayout)).addView(
				ScoreUtils.textViewFromLabel(String.valueOf(sum), this), ScoreUtils.getScoreViewParams());
	}
	
	public void onInputFinishButtonClicked(View view) {
		Log.d(LOG_TAG, "end training button clicked");
		logic.finishTraining();
		((TextView)findViewById(R.id.scoreTotalValueView)).setText(R.string.zero);
		((LinearLayout)findViewById(R.id.seriesInternalLayout)).removeAllViews();
		((LinearLayout)findViewById(R.id.resultsInternalLayout)).removeAllViews();
		Toast.makeText(ScoreMainActivity.this, getString(R.string.message_training_saved),
				Toast.LENGTH_SHORT).show();
	}

}
