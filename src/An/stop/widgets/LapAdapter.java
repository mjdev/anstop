package An.stop.widgets;

import java.text.NumberFormat;
import java.util.List;

import An.stop.Clock;
import An.stop.Clock.Lap;
import An.stop.R;
import An.stop.util.Util;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * An adapter to show a List of {@link Clock.Lap}.
 * <p>
 * It uses the {@link R.layout#lap_row} layout to display each row.
 * The difference between two sequential laps is calculated on the
 * fly and shown in another little TextView.
 * @see Clock.Lap
 * @see ArrayAdapter
 */
public class LapAdapter extends ArrayAdapter<Lap> {
	
	/**
	 * View Holder for performance reasons. We can
	 * easily use convertView with this class!
	 */
	private static class ViewHolder {
		public TextView lapTextView;
		public TextView previousTextView;
		public TextView numberTextView;
	}

	private static final NumberFormat nf = Util.getTwoDigitFormat();
	private LayoutInflater inflater;
	private List<Lap> laps;

	public LapAdapter(Context context, List<Lap> laps) {
		super(context, R.layout.lap_row, laps);
		this.laps = laps;
		
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		long time = System.currentTimeMillis();
		Lap lap = laps.get(position);
		View rowView = convertView;
		
		if(rowView == null) {
			rowView = inflater.inflate(R.layout.lap_row, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.lapTextView = (TextView) rowView.findViewById(R.id.lap_text_view);
			viewHolder.previousTextView = (TextView) rowView.findViewById(R.id.previous_lap_text_view);
			viewHolder.numberTextView = (TextView) rowView.findViewById(R.id.number_text_view);
			rowView.setTag(viewHolder);
		}
		
		ViewHolder holder = (ViewHolder) rowView.getTag();
		holder.numberTextView.setText((position + 1) + ".");
		holder.lapTextView.setText((lap.hours > 0 ? (lap.hours + "h ") : "") + nf.format(lap.minutes) + ":" + nf.format(lap.seconds) + ":" + lap.deciSeconds);
		
		if(position > 0) {
			Lap previousLap = laps.get(position - 1);
			int now = lap.deciSeconds + 10 * lap.seconds + 60 * 10 * lap.minutes + 60 * 60 * 10 + lap.hours;
			int prev = previousLap.deciSeconds + 10 * previousLap.seconds + 60 * 10 * previousLap.minutes +  60 * 60 * 10 + previousLap.hours;
			int diffTime = now - prev;
			int diffDeciSeconds = diffTime % 10;
			int diffSeconds = (int) (diffTime / 10) % 60;
			int diffMinutes = (int) ((diffTime / (10 * 60)) % 60);
			int diffHours   = (int) (diffTime / (10 * 60 * 60));

			holder.previousTextView.setText((lap.hours > 0 ? (diffHours + "h ") : "") + nf.format(diffMinutes) + ":" + nf.format(diffSeconds) + ":" + diffDeciSeconds);
		} else {
			holder.previousTextView.setText("");
		}
		
		Log.d("LapAdapter", "getView took: " + (System.currentTimeMillis() - time) + " ms");
		return rowView;
	}
}
