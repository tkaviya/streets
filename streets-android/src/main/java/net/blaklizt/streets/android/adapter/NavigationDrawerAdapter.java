package net.blaklizt.streets.android.adapter;

/**
 * Created with IntelliJ IDEA.
 * User: photon
 * Date: 2015/06/15
 * Time: 12:59 AM
 */

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import net.blaklizt.streets.android.R;
import net.blaklizt.streets.android.model.NavDrawerItem;

import java.util.Collections;
import java.util.List;

/**
  * Created by Ravi Tamada on 12-03-2015.
  */
public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.MyViewHolder> {
	List<NavDrawerItem> data = Collections.emptyList();
	private LayoutInflater inflater;
	private Context context;

	public NavigationDrawerAdapter(Context context, List<NavDrawerItem> data) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		this.data = data;
	}

	public void delete(int position) {
		data.remove(position);
		notifyItemRemoved(position);
	}

	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = inflater.inflate(R.layout.nav_drawer_row, parent, false);
		MyViewHolder holder = new MyViewHolder(view);
		return holder;
	}

	@Override
	public void onBindViewHolder(MyViewHolder holder, int position) {
		NavDrawerItem current = data.get(position);
		holder.title.setText(current.getTitle());
	}

	@Override
	public int getItemCount() { return data.size(); }

	class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		CheckedTextView title;

		private final Drawable checkBoxOff = ContextCompat.getDrawable(context, android.R.drawable.checkbox_off_background);
		private final Drawable checkBoxOn  = ContextCompat.getDrawable(context, android.R.drawable.checkbox_on_background);

		public MyViewHolder(View itemView) {
			super(itemView);
			title = (CheckedTextView) itemView.findViewById(R.id.title);
			title.setOnClickListener(this);
		}

		@Override
		public void onClick(View view) {

			try {
				CheckedTextView checkedTextView = (CheckedTextView) view;
				boolean checked = !checkedTextView.isChecked();
				checkedTextView.setChecked(checked);

				if (checkedTextView.isChecked()) {
					checkedTextView.setCheckMarkDrawable(checkBoxOn);
				} else {
					checkedTextView.setCheckMarkDrawable(checkBoxOff);
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}

	}
}