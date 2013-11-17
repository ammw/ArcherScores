package eu.ammw.archerscores;


import java.util.LinkedList;
import java.util.List;

import android.app.ActionBar;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ScoreMainActivity extends FragmentActivity implements ActionBar.OnNavigationListener {
	
	private static final String LOG_TAG = "AS-Main";
	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current dropdown position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	
	private static List<Integer> currentSeries = new LinkedList<Integer>();
	private static List<Integer> trainingResults = new LinkedList<Integer>();
	private static List<List<Integer>> results = new LinkedList<List<Integer>>();
	private static int totalScore = 0;

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
		Fragment fragment = new SectionFragment();
		getSupportFragmentManager().beginTransaction()
			.replace(R.id.container, fragment)
			.commit();
		return true;
	}
	
	public void onInputSeriesButtonClicked(View view) {
		Log.d(LOG_TAG, "series button clicked");
		
		LinkedList<Integer> series = SectionFragment.layoutToList((LinearLayout)findViewById(R.id.seriesInternalLayout));
		results.add(series);
		int sum = 0;
		for (int val : series) 
			sum += val;
		((LinearLayout)findViewById(R.id.resultsInternalLayout)).addView(
				SectionFragment.textViewFromLabel(String.valueOf(sum), this));// FIXME params
		trainingResults.add(sum);
		totalScore += sum;
		((TextView)findViewById(R.id.scoreTotalValueView)).setText(String.valueOf(totalScore));
		((LinearLayout)findViewById(R.id.seriesInternalLayout)).removeAllViews();
		if (currentSeries != null)
			currentSeries.clear();
	}
	
	public void onInputFinishButtonClicked(View view) {
		Log.d(LOG_TAG, "end training button clicked");
		/* TODO: it should
		 * do everything series button does
		 * save result in the db
		 * clear all training data
		 * clear the input tab
		 * set total to 0
		 */
	}

	/**
	 * A fragment representing a section of the app.
	 */
	public static class SectionFragment extends Fragment {
		
		private static final String LOG_TAG = "AS-Fr";
		private static final int BUTTON_COUNT = 12;
		private static final int [] BG_COLORS =
			{ Color.YELLOW, Color.RED,   Color.BLUE,  Color.BLACK, Color.LTGRAY, Color.WHITE };
		private static final int [] TX_COLORS =
			{ Color.BLACK,  Color.BLACK, Color.WHITE, Color.WHITE, Color.BLACK, Color.BLACK };
		private final Button [] buttons = new Button[BUTTON_COUNT];
		
		public SectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = null;
			final FragmentActivity context = getActivity();
			int current = context.getActionBar().getSelectedNavigationIndex();
			switch (current) {
			case 0: // INPUT
				Log.d(LOG_TAG, "creating INPUT fragment");
				rootView = inflater.inflate(R.layout.fragment_score_input, container, false);
				GridView grid = (GridView)rootView.findViewById(R.id.inputGridView);
				final boolean isPortrait = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
				if (isPortrait)
					grid.setNumColumns(3);
				else grid.setNumColumns(4);
				TextView total = (TextView)context.findViewById(R.id.scoreTotalValueView);
				if(total != null)
					total.setText(String.valueOf(totalScore));
				else Log.w(LOG_TAG, "No total element!");
				
				// series field
				final LinearLayout seriesLayout = (LinearLayout)rootView.findViewById(R.id.seriesInternalLayout);
				final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(seriesLayout.getLayoutParams());
				params.setMargins(2, 2, 2, 2);
				if(currentSeries != null) {
					Log.d(LOG_TAG, "Restoring "+currentSeries.size()+" values");
					for (Integer value : currentSeries)
						seriesLayout.addView(textViewFromLabel(value.toString(), context), params);
				} else Log.d(LOG_TAG, "Nothing to restore in this series");
				
				// training results field
				LinearLayout trainingLayout = (LinearLayout)rootView.findViewById(R.id.resultsInternalLayout);
				if(trainingResults != null) {
					Log.d(LOG_TAG, "Restoring "+currentSeries.size()+" values");
					for (Integer value : trainingResults)
						trainingLayout.addView(textViewFromLabel(value.toString(), context), params);
				} else Log.d(LOG_TAG, "Nothing to restore in this training");
				
				for (int i=0; i < BUTTON_COUNT; i++) {
					buttons[i] = new Button(context);
					buttons[i].setText(String.valueOf(10-i));
					buttons[i].setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							Log.d(LOG_TAG, "button "+((Button)v).getText()+" clicked");
							seriesLayout.addView(textViewFromLabel(((Button)v).getText(), context),params);
						}
					});
					buttons[i].setBackgroundColor(BG_COLORS[i/2]);
					buttons[i].setTextColor(TX_COLORS[i/2]);
				}
				// no idea for the button after 0, make it 0 too
				buttons[BUTTON_COUNT-1].setText("0");
				buttons[BUTTON_COUNT-1].setTextColor(BG_COLORS[BUTTON_COUNT/2-1]);
				BaseAdapter adapter = new BaseAdapter() {
					
					@Override
					public View getView(int position, View convertView, ViewGroup parent) {
						return convertView==null ? buttons[orientedPosition(position)] : convertView;
					}
					
					@Override
					public long getItemId(int position) {
						return buttons[orientedPosition(position)].getId();
					}
					
					@Override
					public Object getItem(int position) {
						return buttons[orientedPosition(position)];
					}
					
					@Override
					public int getCount() {
						return buttons.length;
					}
					
					// button position should depend on screen orientation/size
					private int orientedPosition(int pos) {
						return 
								isPortrait ?
										(pos%3)*4 + pos/3 : pos;
					}
				};
				grid.setAdapter(adapter);
				break;
				
			case 1: // HISTORY
				rootView = inflater.inflate(R.layout.fragment_score_history, container, false);
				Log.d(LOG_TAG, "creating HISTORY fragment");
				ListView view = (ListView)rootView.findViewById(R.id.historyListView);
				view.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, new String [] {"Nothing :("}));
				// TODO
				break;
			}
			
			return rootView;
		}
		
		@Override
		public void onPause() {
			// preserve results
			super.onPause();
			Log.d(LOG_TAG, "PAUSED");
			currentSeries = layoutToList((LinearLayout)getActivity().findViewById(R.id.seriesInternalLayout));
		}
		
		private static LinkedList<Integer> layoutToList(LinearLayout layout) {
			if (layout == null) return null;
			LinkedList<Integer> returnedList = new LinkedList<Integer>();
			int count = layout.getChildCount();
			Log.d(LOG_TAG, "Saving "+count+" entries");
			for (int i=0; i<count; i++) 
				returnedList.add(Integer.parseInt(((TextView)layout.getChildAt(i)).getText().toString()));
			return returnedList;
		}
		
		private static TextView textViewFromLabel(CharSequence score, Context context) {
			Log.v(LOG_TAG, "Creating element: "+score);
			TextView scoreView = new TextView(context);
			scoreView.setBackgroundColor(Color.LTGRAY);
			scoreView.setText(score);
			scoreView.setGravity(Gravity.CENTER);
			return scoreView;
		}
	}

}
