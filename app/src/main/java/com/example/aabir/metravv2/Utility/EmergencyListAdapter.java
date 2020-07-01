package com.example.aabir.metravv2.Utility;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.aabir.metravv2.R;

import java.util.ArrayList;

/**
 * Created by abir on 4/6/2017.
 */

public class EmergencyListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<EmergencyGroup> emergencyGroupArrayList;
    private ArrayList<EmergencyGroup> originalList;

    public EmergencyListAdapter(Context context, ArrayList<EmergencyGroup> emergencyGroupArrayList) {
        this.context = context;
        this.emergencyGroupArrayList = new ArrayList<EmergencyGroup>();
        this.emergencyGroupArrayList.addAll(emergencyGroupArrayList);
        this.originalList=new ArrayList<EmergencyGroup>();
        this.originalList.addAll(emergencyGroupArrayList);
    }

    @Override
    public int getGroupCount() {
        return emergencyGroupArrayList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        ArrayList<Emergency> emergencyArrayList = emergencyGroupArrayList.get(groupPosition).getEmergencyArrayList();
        return emergencyArrayList.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return emergencyGroupArrayList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        ArrayList<Emergency> emergencyArrayList=emergencyGroupArrayList.get(groupPosition).getEmergencyArrayList();
        return emergencyArrayList.get(childPosition);
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
        EmergencyGroup emergencyGroup = (EmergencyGroup) getGroup(groupPosition);
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.emergency_group_row, null);
        }

        TextView heading = (TextView) view.findViewById(R.id.emergencyHeading);
        heading.setText(emergencyGroup.getName().trim());

        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Emergency emergency = (Emergency) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.emergency_child_row, null);
        }

        TextView name = (TextView) convertView.findViewById(R.id.emergencyMainNameText);
        TextView numbers = (TextView) convertView.findViewById(R.id.emergencyMainNumberText);
        name.setText(emergency.getName().trim());
        numbers.setText(emergency.getNumbers().trim());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void filterData(String query){

        query = query.toLowerCase();
        Log.v("MyListAdapter", String.valueOf(emergencyGroupArrayList.size()));
        emergencyGroupArrayList.clear();

        if(query.isEmpty()){
            emergencyGroupArrayList.addAll(originalList);
        }
        else {

            for(EmergencyGroup emergencyGroup: originalList){

                ArrayList<Emergency> emergencyArrayList = emergencyGroup.getEmergencyArrayList();
                ArrayList<Emergency> newList = new ArrayList<Emergency>();
                for(Emergency emergency: emergencyArrayList){
                    if(emergency.getName().toLowerCase().contains(query) ||
                            emergency.getNumbers().toLowerCase().contains(query)){
                        newList.add(emergency);
                    }
                }
                if(newList.size() > 0){
                    EmergencyGroup nContinent = new EmergencyGroup(emergencyGroup.getName(),newList);
                    emergencyGroupArrayList.add(nContinent);
                }
            }
        }

        Log.v("MyListAdapter", String.valueOf(emergencyGroupArrayList.size()));
        notifyDataSetChanged();

    }

}

