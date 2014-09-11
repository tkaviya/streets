package net.blaklizt.streets.android;

/**
 * User: tkaviya
 * Date: 9/11/14
 * Time: 6:10 AM
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class PlacesListAdapter extends ArrayAdapter<String>
{
	private final Context context;
	private final String[] values;

	public PlacesListAdapter(Context context, String[] values) {
		super(context, R.layout.drawer_list_item, values);
		this.context = context;
		this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.drawer_list_item, parent, false);
//		TextView textView = (TextView) rowView.findViewById(R.id.label);
//		ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
//		textView.setText(values[position]);
//		// Change the icon for Windows and iPhone
//		String s = values[position];
//		if (s.startsWith("Windows7") || s.startsWith("iPhone")
//			|| s.startsWith("Solaris")) {
//			imageView.setImageResource(R.drawable.checkbox_off_background);
//		} else {
//			imageView.setImageResource(R.drawable.ok);
//		}

		return rowView;
	}
}
