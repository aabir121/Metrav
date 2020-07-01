package com.example.aabir.metravv2.Utility;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.aabir.metravv2.R;

import java.util.ArrayList;

/**
 * Created by abir on 4/6/2017.
 */

public class BusOfflineListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private ArrayList<BusOfflineGroup> busOfflineGroupArrayList;
    private ArrayList<BusOfflineGroup> originalList;

    public BusOfflineListAdapter(Context context, ArrayList<BusOfflineGroup> busOfflineGroupArrayList) {
        this.context = context;
        this.busOfflineGroupArrayList = new ArrayList<BusOfflineGroup>();
        this.busOfflineGroupArrayList.addAll(busOfflineGroupArrayList);
        this.originalList=new ArrayList<BusOfflineGroup>();
        this.originalList.addAll(busOfflineGroupArrayList);
    }

    @Override
    public int getGroupCount() {
        return busOfflineGroupArrayList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        ArrayList<BusOffline> busOfflineArrayList = busOfflineGroupArrayList.get(groupPosition).getBusOfflineArrayList();
        return busOfflineArrayList.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return busOfflineGroupArrayList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        ArrayList<BusOffline> busOfflineArrayList=busOfflineGroupArrayList.get(groupPosition).getBusOfflineArrayList();
        return busOfflineArrayList.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View view, ViewGroup parent) {
        BusOfflineGroup BusOfflineGroup = (BusOfflineGroup) getGroup(groupPosition);
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.emergency_group_row, null);
        }

        TextView heading = (TextView) view.findViewById(R.id.emergencyHeading);
        heading.setText(BusOfflineGroup.getBusName().trim());

        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        BusOffline emergency = (BusOffline) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.bus_offline_child_row, null);
        }

        TextView name = (TextView) convertView.findViewById(R.id.emergencyMainNameText);
        TextView numbers = (TextView) convertView.findViewById(R.id.emergencyMainNumberText);
        name.setText(emergency.getBusName().trim());
        numbers.setText(emergency.getBusRoute().trim());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void filterData(String query){

        Log.e("Nav",query);
        String[] qs={""};
        boolean multiple=false;
        query = query.toLowerCase();
        if(query.contains(","))
        {
            qs=query.split(",");
            multiple=true;
        }
        Log.v("MyListAdapter", String.valueOf(busOfflineGroupArrayList.size()));
        busOfflineGroupArrayList.clear();

        if(query.isEmpty()){
            busOfflineGroupArrayList.addAll(originalList);
        }
        else {

            for(BusOfflineGroup BusOfflineGroup: originalList){

                ArrayList<BusOffline> busOfflineArrayList = BusOfflineGroup.getBusOfflineArrayList();
                ArrayList<BusOffline> newList = new ArrayList<BusOffline>();
                for(BusOffline emergency: busOfflineArrayList){
                    if(multiple)
                    {
                        if(emergency.getBusRoute().toLowerCase().contains(qs[0]) && emergency.getBusRoute().toLowerCase().contains(qs[1]))
                        {
                            newList.add(emergency);
                        }
                    }
                    else if(emergency.getBusName().toLowerCase().contains(query) ||
                            emergency.getBusRoute().toLowerCase().contains(query)){
                        newList.add(emergency);
                    }
                }
                if(newList.size() > 0){
                    BusOfflineGroup nContinent = new BusOfflineGroup(BusOfflineGroup.getBusName(),newList);
                    busOfflineGroupArrayList.add(nContinent);
                }
            }
        }

        Log.v("MyListAdapter", String.valueOf(busOfflineGroupArrayList.size()));
        notifyDataSetChanged();

    }

}
