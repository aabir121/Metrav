package com.example.aabir.metravv2;


import android.app.SearchManager;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.SearchView;

import com.example.aabir.metravv2.Utility.BusOffline;
import com.example.aabir.metravv2.Utility.BusOfflineGroup;
import com.example.aabir.metravv2.Utility.BusOfflineListAdapter;

import java.util.ArrayList;

/**
 * Created by abir on 4/26/2017.
 */

public class TwoFragment extends Fragment
        implements SearchView.OnQueryTextListener, SearchView.OnCloseListener{

    private SearchView search,destSearch;
    private BusOfflineListAdapter listAdapter;
    private ExpandableListView myList;
    private ArrayList<BusOfflineGroup> busOfflineGroupArrayList = new ArrayList<BusOfflineGroup>();

    @Override
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    Context context;

    public static View root;
    public TwoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root=inflater.inflate(R.layout.fragment_inter_city, container, false);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        search = (SearchView) root.findViewById(R.id.filterSearchBusOfflineTextInterCity);
        search.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        search.setIconifiedByDefault(false);
        search.setOnQueryTextListener(this);
        search.setOnCloseListener(this);
        destSearch=(SearchView)root.findViewById(R.id.filterSearchBusOfflineDestTextInterCity);
        destSearch.setIconifiedByDefault(false);
        destSearch.setOnCloseListener(this);
//        destSearch.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        destSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(!search.getQuery().toString().isEmpty())
                {
                    String qr=query+","+search.getQuery();
                    listAdapter.filterData(qr);
                }
                else
                    listAdapter.filterData(query);
                expandAll();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!search.getQuery().toString().isEmpty())
                {
                    String qr=newText+","+search.getQuery();
                    listAdapter.filterData(qr);
                }
                else
                    listAdapter.filterData(newText);

                expandAll();
                return false;
            }
        });
        destSearch.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                if(search.getQuery().toString().isEmpty())
                    listAdapter.filterData("");
                else
                    listAdapter.filterData(search.getQuery().toString());

                expandAll();
                return false;
            }
        });


        displayList(root);
        return root;
    }


    //method to expand all groups
    private void expandAll() {
        int count = listAdapter.getGroupCount();
        for (int i = 0; i < count; i++){
            myList.expandGroup(i);
        }
    }


    private void displayList(View root) {

        //display the list
        loadSomeData();

        //get reference to the ExpandableListView
        myList = (ExpandableListView) root.findViewById(R.id.expandableListViewBusInter);
        //create the adapter by passing your ArrayList data
        listAdapter = new BusOfflineListAdapter(getActivity(), busOfflineGroupArrayList);
        //attach the adapter to the list
        myList.setAdapter(listAdapter);

    }



    private void loadSomeData()
    {
        ArrayList<BusOffline> busOfflineArrayList=new ArrayList<>();
        BusOffline busOffline;
        BusOfflineGroup busOfflineGroup;

        busOffline=new BusOffline("","Hanif Enterprise\n480 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Shyamoli Paribahan\n480 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Unique Paribahan\n480 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","TR Travels\n480 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Saudia\n480 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Ena Transport \n480 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","S Alam Paribahan \n480 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Tuba Line \n480 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Year 71 Express  \n480 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Hanif Enterprise  \n1250 TK (Volvo Business Class)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Shyamoli Paribahan  \n1200 TK (Economy Class)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","TR Travels  \n1250 TK (Hyundai Universe)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Tuba Line  \n850 TK (Economy Class)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Tuba Line  \n1050 TK (Business Class)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Yellow Line  \n1250 TK (Business Class)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Desh Travels   \n1250 TK (Business Class)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Relax Transport   \n1250 TK (Hyundai Universe)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Saintmartin Paribahan   \n850 TK (Economy Class)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Saintmartin Paribahan   \n1250 TK (Business Class)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Shohagh Paribahan   \n1050 TK (Scania Economy Class)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Shohagh Paribahan   \n1250 TK (Scania Economy Class)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Saudia Air Con   \n1250 TK (Mercedes Benz Business Class)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Green Line Paribahan   \n1000 TK (Scania Economy Class)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Green Line Paribahan   \n1250 TK (Scania Economy Class)");
        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("DHAKA - CHITTAGONG",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);

        busOfflineArrayList=new ArrayList<>();


        busOffline=new BusOffline("","Hanif Enterprise\n900 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Shyamoli Paribahan\n900 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Unique Paribahan\n900 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Tuba Line\n900 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Year 71\n900 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Saintmartin Travels\n1600 TK (Hino Economy Class)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Saintmartin Paribahan\n1600 TK (Hino Economy Class)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Green Line Paribahan\n1700 TK (Scania Economy Class)");
        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("DHAKA - TEKNAF",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);

        busOfflineArrayList=new ArrayList<>();

        busOffline=new BusOffline("","Hanif Enterprise\n800 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Shyamoli Paribahan\n800 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Unique Paribahan\n800 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","TR Travels\n800 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Desh Travels\n800 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Saudia\n800 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Ena Transport\n800 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","S Alam Paribahan\n800 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Tuba Line\n800 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Year 71 Express\n800 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Hanif Enterprise\n2000 TK (Volvo Business Class)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Shyamoli Paribahan\n1500 TK (Economy Class)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Shyamoli Paribahan\n1700 TK (Business Class)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","TR Travels\n2000 TK (Hyundai Universe)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Desh Travels\n2000 TK (Business Class)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Relax Transport\n2000 TK (Hyundai Universe)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Ena Transport\n2000 TK (Hyundai Universe)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Tuba Line\n1450 TK (Economy Class)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Tuba Line\n1750 TK (Business Class)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Royal Coach\n1700 TK (Hino Business Class)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Royal Coach\n1500 TK (Hino Economy Class)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Saintmartin Travels\n1500 TK (Hino Economy Class)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Shohagh Paribahan\n1700 TK (Scania Economy Class)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Shohagh Paribahan\n2000 TK (Scania Business Class)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Saintmartin Paribahan\n1450 TK (Economy Class)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Saintmartin Paribahan\n2000 TK (Hyundai Universe)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Soudia Air Con\n2000 TK (Mercedes Benz Business Class)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Green Line Paribahan\n1500 TK (Scania Economy Class)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Green Line Paribahan\n2000 TK (Scania Business Class)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Green Line Paribahan\n2500 TK (Scania Sleeper Coach)");
        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("DHAKA - COXSBAZAR",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);

        busOfflineArrayList=new ArrayList<>();

        busOffline=new BusOffline("","Desh Travels\n250 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","S Alam Paribahan\n350 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Hanif Enterprise\n800 TK (Volvo Business Class)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Shyamoli Paribahan\n800 TK (Business Class)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Desh Travels\n800 TK (Business Class)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Relax Transport\n800 TK (Hyundai Universe)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Shohagh Paribahan\n800 TK (Scania Business Class)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Saintmartin Paribahan\n800 TK (Business Class)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Soudia Air Con\n650 TK (Mercedes Benz Economy Class)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Soudia Air Con\n800 TK (Mercedes Benz Business Class)");
        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("CHITTAGONG-COXSBAZAR",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);

        busOfflineArrayList=new ArrayList<>();


        busOffline=new BusOffline("","S Alam Paribahan\n520 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Hanif Enterprise\n620 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Shyamoli Paribahan\n620 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Unique Paribahan\n620 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Eagle Paribahan\n620 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Eagle Paribahan\n900 TK (Economy Class)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Shyamoli Paribahan\n950 TK (Economy Class)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Saintmartin Paribahan\n950 TK (Economy Class)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Saintmartin Paribahan\n1500 TK (Hyundai Universe)");
        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("DHAKA - BANDARBAN",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);

        busOfflineArrayList=new ArrayList<>();

        busOffline=new BusOffline("","Hanif Enterprise\n620 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Shyamoli Paribahan\n620 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Unique Paribahan\n620 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","S Alam Paribahan\n620 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Dolphin Paribahan\n600 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Shyamoli Paribahan\n800 TK (Economy Class)");
        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("DHAKA - RANGAMATI",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);

        busOfflineArrayList=new ArrayList<>();

        busOffline=new BusOffline("","Shyamoli Paribahan\n520 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Saudia\n520 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Eagle Paribahan\n520 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","S Alam Paribahan\n520 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Shanti Paribahan\n520 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Econo Service\n520 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Himachol Express\n520 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Saintmartin Paribahan\n900 TK (Economy Class)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Hanif Enterprise\n1000 TK (RM2 Business Class)");
        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("DHAKA - KHAGRACHARI",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);

        busOfflineArrayList=new ArrayList<>();


        busOffline=new BusOffline("","Shyamoli Paribahan\n550 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Saudia\n550 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Dolphin Paribahan\n550 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","S Alam Paribahan\n550 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("DHAKA - KAPTAI",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);

        busOfflineArrayList=new ArrayList<>();

        busOffline=new BusOffline("","Tisha Exclusive\n200 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Tisha Plus\n200 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Asia Line\n200 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Asia Transport\n200 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","BRTC\n200 TK (AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Asia Line\n250 TK (AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Asia Transport\n250 TK (AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Royal Platinum\n250 TK (Economy Class)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Royal Platinum\n330 TK (Business Class)");
        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("DHAKA - COMILLA",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);

        busOfflineArrayList=new ArrayList<>();

        busOffline=new BusOffline("","Himachol Express\n350 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Ekushe Express\n350 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Moon Line Enterprise\n350 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Himachol Express\n400 TK (Economy Class)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Ekushe Express\n400 TK (Economy Class)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Moon Line Enterprise\n400 TK (Economy Class)");
        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("DHAKA - NOAKHALI",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);

        busOfflineArrayList=new ArrayList<>();

        busOffline=new BusOffline("","Grameen Paribahan\n250 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Jonaki Service\n250 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Dhaka Express\n350 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Econo Service\n350 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("DHAKA - LAKSMIPUR",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);

        busOfflineArrayList=new ArrayList<>();

        busOffline=new BusOffline("","Tisha Enterprise\n200 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Shohag Paribahan\n200 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Royal Coach\n200 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","BRTC\n200 TK (AC)");
        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("DHAKA - BRAHMANBARIA",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);

        busOfflineArrayList=new ArrayList<>();

        busOffline=new BusOffline("","Hanif Enterprise\n470 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Shyamoli Paribahan\n470 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Unique Paribahan\n470 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Ena Transport\n470 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Ena Transport\n1200 TK (Hyundai Universe)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Green Line Paribahan\n950 TK (Scania Economy Class)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Green Line Paribahan\n1200 TK (Scania Business Class)");
        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("DHAKA - SYLHET",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);

        busOfflineArrayList=new ArrayList<>();

        busOffline=new BusOffline("","Hanif Enterprise\n380 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Shyamoli Paribahan\n380 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Ena Transport\n400 TK (Hino AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Ena Transport\n600 TK (Hino AC)");
        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("DHAKA - MOULVIBAZAR",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);

        busOfflineArrayList=new ArrayList<>();

        busOffline=new BusOffline("","Hazrat Shah Jalal (R) Paribahan\n270 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("DHAKA - NETROKONA",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);

        busOfflineArrayList=new ArrayList<>();

        busOffline=new BusOffline("","Ena Transport\n220 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("DHAKA - MYMENSINGH",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);

        busOfflineArrayList=new ArrayList<>();

        busOffline=new BusOffline("","Ananna Paribahan\n190 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOffline=new BusOffline("","Ananna Classic\n190 TK (Non AC)");
        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("DHAKA - KISHOREGANJ",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();

    }



    @Override
    public boolean onClose() {
        if(destSearch.getQuery().toString().isEmpty())
            listAdapter.filterData("");
        else
            listAdapter.filterData(destSearch.getQuery().toString());
        expandAll();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        if(destSearch.getQuery().toString().isEmpty())
            listAdapter.filterData(query);
        else
        {
            String q=query+","+destSearch;
            listAdapter.filterData(q);
        }
        expandAll();
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if(destSearch.getQuery().toString().isEmpty())
            listAdapter.filterData(query);
        else
        {
            String q=query+","+destSearch;
            listAdapter.filterData(q);
        }
        expandAll();
        return false;
    }

}
