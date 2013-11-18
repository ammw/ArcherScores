package eu.ammw.archerscores;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * A fragment representing a section of the app.
 */
public class ScoreFragment extends Fragment {
	
	private static final String LOG_TAG = "AS-Fr";
	private static final int BUTTON_COUNT = 12;
	private static final int [] BG_COLORS =
		{ Color.YELLOW, Color.RED,   Color.BLUE,  Color.BLACK, Color.LTGRAY, Color.WHITE };
	private static final int [] TX_COLORS =
		{ Color.BLACK,  Color.BLACK, Color.WHITE, Color.WHITE, Color.BLACK, Color.BLACK };
	private final Button [] buttons = new Button[BUTTON_COUNT];
	
	private FragmentActivity context;
	private final ScoreLogic logic = ScoreLogic.getInstance();
	private LinearLayout.LayoutParams params = ScoreUtils.getScoreViewParams();
	
	public ScoreFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View rootView = null;
		context = getActivity();
		int current = context.getActionBar().getSelectedNavigationIndex();
		switch (current) {
		case 0: // INPUT
			Log.d(LOG_TAG, "creating INPUT fragment");
			rootView = inflater.inflate(R.layout.fragment_score_input, container, false);
			GridView grid = (GridView)rootView.findViewById(R.id.inputGridView);
			final boolean isPortrait = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
			if (isPortrait) grid.setNumColumns(3);
			else grid.setNumColumns(4);
			TextView total = (TextView)rootView.findViewById(R.id.scoreTotalValueView);
			total.setText(String.valueOf(logic.getTotal()));
			
			// series field
			final LinearLayout seriesLayout = (LinearLayout)rootView.findViewById(R.id.seriesInternalLayout);
			for (Integer value : logic.getCurrentSeries())
				seriesLayout.addView(ScoreUtils.textViewFromLabel(value.toString(), context), params);
			Log.d(LOG_TAG, "Adding "+logic.getCurrentSeries().size()+" elements to series ");
			
			// training results field
			LinearLayout trainingLayout = (LinearLayout)rootView.findViewById(R.id.resultsInternalLayout);
			for (Integer value : logic.getCurrentTraining())
				trainingLayout.addView(ScoreUtils.textViewFromLabel(value.toString(), context), params);
			Log.d(LOG_TAG, "Adding "+logic.getCurrentTraining().size()+" elements to training");
			
			for (int i=0; i < BUTTON_COUNT; i++) {
				buttons[i] = new Button(context);
				buttons[i].setText(String.valueOf(10-i));
				buttons[i].setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						CharSequence value = ((Button)v).getText();
						Log.d(LOG_TAG, "button "+value+" clicked");
						seriesLayout.addView(ScoreUtils.textViewFromLabel(value, context),params);
						logic.hit(value);
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
					return isPortrait ? (pos%3)*4 + pos/3 : pos;
				}
			};
			grid.setAdapter(adapter);
			break;
			
		case 1: // HISTORY
			rootView = inflater.inflate(R.layout.fragment_score_history, container, false);
			Log.d(LOG_TAG, "creating HISTORY fragment");
			ListView view = (ListView)rootView.findViewById(R.id.historyListView);
			String [] history = logic.getHistory();
			view.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, history));
			break;
		}
		
		return rootView;
	}
	
	@Override
	public void onPause() {
		// preserve results (if any)
		super.onPause();
		Log.d(LOG_TAG, "PAUSED");
	}
}