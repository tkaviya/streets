package net.blaklizt.streets.android.adapter;

/**
* User: tkaviya
* Date: 9/13/14
* Time: 1:56 PM
*/

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import net.blaklizt.streets.android.R;
import net.blaklizt.streets.android.activity.AppContext;
import net.blaklizt.streets.android.common.Group;

public class NavigationListAdapter extends BaseExpandableListAdapter
{
	private final SparseArray<Group> groups;
	public LayoutInflater inflater;

	public NavigationListAdapter(LayoutInflater inflater, SparseArray<Group> groups) {
		this.inflater = inflater;
		this.groups = groups;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return groups.get(groupPosition).children.get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public View getChildView(int groupPosition, final int childPosition,
	                         boolean isLastChild, View convertView, ViewGroup parent) {
		final String children = (String) getChild(groupPosition, childPosition);
		TextView text;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.listrow_details, null);
		}
		text = (TextView) convertView.findViewById(R.id.navigationChildItemText);
		text.setText(children);
		convertView.setOnClickListener(v -> AppContext.getStreetsCommon().speak(children));
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return groups.get(groupPosition).children.size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return groups.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return groups.size();
	}

	@Override
	public void onGroupCollapsed(int groupPosition) {
		super.onGroupCollapsed(groupPosition);
	}

	@Override
	public void onGroupExpanded(int groupPosition) {
		super.onGroupExpanded(groupPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.drawer_list_item, null);
		}
		Group group = (Group) getGroup(groupPosition);
		((TextView) convertView).setText(group.string);
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}
}
