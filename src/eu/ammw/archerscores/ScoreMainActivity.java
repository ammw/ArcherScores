package eu.ammw.archerscores;


import android.app.ActionBar;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;

public class ScoreMainActivity extends FragmentActivity implements ActionBar.OnNavigationListener {
	
	private static final String LOG_TAG = "AS-Main";
	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current dropdown position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

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

	/**
	 * A fragment representing a section of the app.
	 */
	public static class SectionFragment extends Fragment {
		
		private static final String LOG_TAG = "AS-Fr";
		private static final int BUTTON_COUNT = 12;
		private final Button [] buttons = new Button[BUTTON_COUNT];
		
		public SectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = null;
			FragmentActivity context = getActivity();
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
				for (int i=0; i < BUTTON_COUNT; i++) {
					buttons[i] = new Button(context);
					buttons[i].setText(String.valueOf(10-i));
				}
				buttons[BUTTON_COUNT-1].setText("<");
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
			// TODO
			super.onPause();
		}
	}

}
