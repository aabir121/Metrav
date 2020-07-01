package com.example.aabir.metravv2.Utility;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.aabir.metravv2.R;

import java.util.ArrayList;

/**
 * Created by aabir on 12/26/2016.
 */

public class CustomAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<String> Title;
    private ArrayList<ArrayList<String>> finallist;
    private int[] imge;

    public CustomAdapter(Context context, ArrayList<String> text1,int[] imageIds,ArrayList<ArrayList<String>> finalL) {
        mContext = context;
        Title = text1;
        imge = imageIds;
        finallist=finalL;

    }

    public int getCount() {
        // TODO Auto-generated method stub
        return Title.size();
    }

    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }


//   @Override
//    public Filter getFilter() {
//        return new Filter() {
//            @SuppressWarnings("unchecked")
//            @Override
//            protected void publishResults(CharSequence constraint, FilterResults results) {
//                Log.d("FILTER", "**** PUBLISHING RESULTS for: " + constraint);
//                finallist = (ArrayList<ArrayList<String>>) results.values;
//                CustomAdapter.this.notifyDataSetChanged();
//            }
//
//            @Override
//            protected FilterResults performFiltering(CharSequence constraint) {
//                Log.d("FILTER", "**** PERFORM FILTERING for: " + constraint);
//                ArrayList<ArrayList<String>> filteredResults = getFilteredResults(constraint);
//
//                FilterResults results = new FilterResults();
//                results.values = filteredResults;
//
//                return results;
//            }
//        };
//    }



    public View getView(int position, View convertView, ViewGroup parent) {

        Context context=mContext;
        LayoutInflater inflater;
        inflater = LayoutInflater.from(mContext);

        View row;
        row = inflater.inflate(R.layout.row, parent, false);
        TextView title;
        ImageView i1;
        LinearLayout l1;
        l1=(LinearLayout)row.findViewById(R.id.trafficListRootLayout);
        i1 = (ImageView) row.findViewById(R.id.trafficIcon);
        title = (TextView) row.findViewById(R.id.txtTitle);
        title.setText(Title.get(position));
        int state=Integer.parseInt(finallist.get(position).get(3));
        if(state==1)
        {
//            l1.setBackgroundColor(Color.GREEN);
            i1.setImageResource(imge[0]);
        }
        else if(state==2)
        {
//            l1.setBackgroundColor(Color.YELLOW);
            i1.setImageResource(imge[1]);
        }
        else if(state==3)
        {
//            l1.setBackgroundColor(Color.RED);
            i1.setImageResource(imge[2]);
        }

        return (row);
    }
}