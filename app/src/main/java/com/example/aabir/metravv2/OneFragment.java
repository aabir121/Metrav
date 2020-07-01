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

public class OneFragment extends Fragment
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
    public OneFragment() {
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
        root=inflater.inflate(R.layout.fragment_intra_city, container, false);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        search = (SearchView) root.findViewById(R.id.filterSearchBusOfflineText);
        search.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        search.setIconifiedByDefault(false);
        search.setOnQueryTextListener(this);
        search.setOnCloseListener(this);
        destSearch=(SearchView)root.findViewById(R.id.filterSearchBusOfflineDestText);
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
        myList = (ExpandableListView) root.findViewById(R.id.expandableListViewBus);
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
        busOffline=new BusOffline("AKIK PORIBOHON","Gabtoli->Mirpur1->Mirpur10->Kalshi->JamunaFuturePark->NatunBazar->Badda");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("AKIK PORIBOHON",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("TRUSTLINE","Mirpur10->Cantonment->Banani->NotunBazar");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("TRUSTLINE",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("TETULIA PORIBPHON","ShiyaMasjid->Shyamoli->Agargaon->Mirpur10->Kalshi->Bisshoroad->Airport->Uttara->Abdullahpur");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("TETULIA PORIBPHON",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("JABALENOOR PORIBOHON,OVIJATPORIBOHON","Agargaon->Mirpur10->Kalshi->Bisshoroad->Airport->Uttara->Abdullahpur");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("JABALENOOR PORIBOHON,OVIJAT PORIBOHON",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("ALIF PORIBOHON,ROIS PORIBOHON","Mirpur Sony Cinema Hall->Mirpur10->Kazipara->Shewrapara->Mohakhali->Gulshan1->Badda->Rampura->Bonoshri");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("ALIF PORIBOHON,ROIS PORIBOHON",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("HIMACHOL PORIBOHON","Mirpur Sony Cinema Hall->Mirpur10->Kazipara->Shewrapara->Mohakhali->Gulshan1->Badda->Rampura->KhilgaonKhidmaHospital");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("HIMACHOL PORIBOHON",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("PROJAPOTI PORIBOHON,NEWPALLAB IEXPRESS,KONOK PORIBOHON","Gabtoli->Mirpur1->Mirpur10->Kalshi->Bisshoroad->Airport->Uttara->Abdullahpur");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("PROJAPOTI PORIBOHON,NEW PALLABI EXPRESS,KONOK PORIBOHON",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("BOSUMOTI PORIBOHON,BRTC","Gabtoli->Mirpur1->Mirpur10->Kalshi->Bisshoroad->Airport->Uttara->Abdullahpur->Tongi->Gazipur");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("BOSUMOTI PORIBOHON,BRTC",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("ROBROB PORIBOHON","Gabtoli->Mirpur1->Mirpur10->Kalshi->Banani->Gulshan2->Gulshan1->BaddaLinkRoad");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("ROBROB PORIBOHON",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("JABALE NOOR PORIBOHON 2","Gabtoli->Mirpur1->Mirpur10->Kalshi->KurilFlyover->NatunBazar");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("JABALE NOOR PORIBOHON 2",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("BRTC","Gabtoli->Mirpur1->Mirpur10->Kazipara->Shewrapara->Mohakhali->Kakoli->Banani->Gulshan2->NatunBazar->Badda->Rampura");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("BRTC",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("EVEREST PORIBOHON","Roopnagar Abasik->Mirpur2->Mirpur1->Khamarbari->Farmgate->Gulistan->Keraniganj");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("EVEREST PORIBOHON",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("MIRPUR MISSION PORIBOHON LIMITED","Chiriyakhana->Mirpur1->Khamarbari->Farmgate->PressClub->Motijhil");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("MIRPUR MISSION PORIBOHON LIMITED",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("ITIHAS PORIBOHON","Mirpur10->Mirpur2->Mirpur1->Gabtoli->Savar->Nobinogor->Chondra");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("ITIHAS PORIBOHON",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("BIHONGO PORIBOHON","Mirpur12->Mirpur11->Mirpur10->Kazipara->Shewrapara->Mohakhali->Gushan1->Badda->NatunBazar");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("BIHONGO PORIBOHON",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("DHAKA METRO SERVICE","Mirpur1->Kolyanpur->Shyamoli->Asadgate->Shukrabad->Kolabagan->ScienceLab->NewMarket->Nilkhet->Azimpur");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("DHAKA METRO SERVICE",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("ASHIRBAD PORIBOHON,BIHONGO PORIBOHON","RoopnagarAbasik->Mirpur2->Mirpur1->Kolyanpur->Shyamoli->Asadgate->Shukrabad->Kolabagan->ScienceLab->NewMarket->Nilkhet->Azimpur");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("ASHIRBAD PORIBOHON,BIHONGO PORIBOHON",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("NEW VISION","Chiriyakhana->Mirpur1->Khamarbari->Farmgate->PressClub->Motijhil");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("NEW VISION",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("DISHARI PORIBOHON","Chiriyakhana->Mirpur1->Gulistan->Keraniganj->BabubazarBridge");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("DISHARI PORIBOHON",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("TRANS SILVA LIMITED","Mirpur1->Technical->Kolyanpur->Asadgate->Kolabagan->ScienceLab->Shahbag->PressClub->Gulistan->Motijhil->Jatrabari");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("TRANS SILVA LIMITED",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("BIKOLPO SERVICE 1/E","Mirpur14->Mirpur1->Technical->Kolyanpur->Asadgate->Kolabagan->ScienceLab->NewMarket->Nilkhet->Azimpur->TSC->Shahbag->PressClub->Gulistan->Tikatuli->Jatrabari");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("BIKOLPO SERVICE 1/E",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("ALIF (BENGAL) PORIBOHON","Mirpur10->Mirpur1->MazarRoad->BeriBadh->Ashuliya->FantasyKingdom->NondonPark");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("ALIF (BENGAL) PORIBOHON",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("POLLOBI LOCAL SERVICE","Asadgate->Shyamoli->Kolyanpur->Technical->Mirpur1->Mirpur2->Mirpur6->CholontikaMor->Mirpur7->Mirpur11->Mirpur12");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("POLLOBI LOCAL SERVICE",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("BRIHOTTOR MIRPUR,TITAS PORIBOHON","Chiriyakhana,->Mirpur1->Gabtoli->Savar->Nobinogor->Chondra");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("BRIHOTTOR MIRPUR,TITAS PORIBOHON",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("ETC PORIBOHON","Kalshi->Mirpur Sare 11->Mirpur10->Kazipara->Shewrapara->IDB->Khamarbari->Farmgate->Karwanbazar->Shahbag->Gulistan->Sadarghat");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("ETC PORIBOHON",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("SHOKOL POPORIBOHON","Chiriyakhana->Mirpur1->Mirpur10->Agargaon->BijoySarani->Farmgate->BanglaMotor->Mogbazar->Malibag->Komlapur");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("SHOKOL POPORIBOHON",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("NOBOKOLI PORIBOHON","Mirpur1->Kolyanpur->Shyamoli->Agargaon->NotunRasta->MohakhaliFlyover->Banani->Gulshan2->NotunBazar");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("NOBOKOLI PORIBOHON",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("BOISHAKHI PORIBOHON","Savar->Gabtoli->Kolyanpur->Shyamoli->Agargaon->NotunRasta->Mohakhali->Gulshan1->BaddaLinkRoad->NotunBazar");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("BOISHAKHI PORIBOHON",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("DESHBANGLA PORIBOHON,RUPKOTHA PORIBOHON","Gabtoli->Abdullahpur");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("DESHBANGLA PORIBOHON,RUPKOTHA PORIBOHON",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("KONOK PORIBOHON","Mirpur12->Mirpur10->Kakoli->Airport->Uttara->Abdullahpur");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("KONOK PORIBOHON",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("PROVATI BONOSHRI PORIBOHON LIMITED","Gulistan->Polton->Malibag->Mogbazar->Saatrasta->Nabisko->Mohakhali->Bonani->Airport->Uttara->Abdullahpur->Tongi->Gazipur->Kaliyakoir");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("PROVATI BONOSHRI PORIBOHON LIMITED",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("4 NUMBER ALIKE TRANSPORT & BRTC ARTICULATED(WI-FI BUS)","Balughat->Cantonment->BijoySarani->Farmgate->Banglamotor->Shahbag->Polton->Gulistan->Motijhil");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("4 NUMBER ALIKE TRANSPORT & BRTC ARTICULATED(WI-FI BUS)",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("3 NUMBER AIRPORT BANGABANDHUA VENUE PORIBOHON(LOCAL)","BongoBazar->HighCourt->Shahbag->Farmgate->Mohakhali->Banani->BissoRoad->Airport->Uttara->Abdullahpur");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("3 NUMBER AIRPORT BANGABANDHUA VENUE PORIBOHON(LOCAL)",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("9 NUMBER BUS","CollegeGate->Shyamoli->Technical->Mirpur1->Mirpur12");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("9 NUMBER BUS",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("TORONGO BUS COMPANY,BRTC","Mohammadpur->Asadgate->Farmgate->Mohakhali->TitumirCollege->Gulshan1->Badda->NotunBazar");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("TORONGO BUS COMPANY,BRTC",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("TORONGO PLUS TRANSPORT LIMITED","MohammadpurBusStand->Shankar->Dhanmondi15->Jhigatola->ScienceLab->Shahbag->Kakrail->MalibagRailgate->RampuraBazar->SouthBanashri");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("TORONGO PLUS TRANSPORT LIMITED",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("BAHON PORIBOHON LIMITED","Mirpur14->Mirpur10->Mirpur1->Technical->Kolyanpur->Asadgate->ScienceLab->Shahbag->PressClub->Motijhil->Komlapur->Mugda->Khilgaon");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("BAHON PORIBOHON LIMITED",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("NISORGO PORIBOHON LIMITED","Mirpur14BusStand->Mirpur10->Kazipara->Agargaon->Shyamoli->Asadgate->Mohammadpur->Dhakmondi15->Jhigatola->ScienceLab->DhakaCollege->NewMarket->Nilkhet->EdenCollege->Azimpur");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("NISORGO PORIBOHO NLIMITED",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("SHOTABDI PORIBOHON LIMITED","Mirpur14BusStand->Mirpur14->Mirpur1->Shyamoli->Asadgate->Mohammadpur->Shankar->Dhanmondi15->Jhigatola->Katabon->Shahbag->Gulistan->Motijhil->NotreDameCollege");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("SHOTABDI PORIBOHON LIMITED",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("CANTONMENT MINI SERVICE","Mirpur14->Kochukhet->SoinikClub->Kakoli->Banani");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("CANTONMENT MINI SERVICE",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("RONGDHONU EXPRESS","Adabor->ShiyaMasjid->Shyamoli->CollegeGate->Asadgate->Kolabagan->ScienceLab->Shahbag->Kakrail->Fokirapul->Motijhil->Doyaganj");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("RONGDHONU EXPRESS",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("BORAK PORIBOHON","Polashi->MeghnaGhat");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("BORAK PORIBOHON",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("RAJDHANI EXPRESS","Shaymoli->Asadgate->Kolabagan->ScienceLab->Katabon->Shahbag->Gulistan");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("RAJDHANI EXPRESS",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("SUPER BUS","Gulistan->Shahbag->Farmgate->Shyamoli->Gabtoli->Savar->Nobinogor->NandanPark");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("SUPER BUS",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("DHAKA PORIBOHON","Gulistan->Shahbag->Farmgate->Banani->Uttara->Gazipur->ShibBari");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("DHAKA PORIBOHON",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("BOLAKA PORIBOHON","Motijhil->Komlapur->Malibag->Mogbazar->Nabisko->Mohakhali->Banani->Khilkhet->Airport->Uttara->Tongi->BoardBazar->Gazipur->ShibBari");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("BOLAKA PORIBOHON",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("HIMALOY TRANSPORT","Modonpur(Narayanganj)->Jatrabari->BangladeshBank->Mogbazar->Mohakhali->TongiBridge");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("HIMALOY TRANSPORT",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("LABBAYEK PORIBOHON","Jatrabari->Sayedabad->Mugda->Khilgaon->Malibag->Mogbazar->KarwanBazar->Farmgate->Asadgate->Shyamoli->Gabtoli->Savar");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("LABBAYEK PORIBOHON",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("MIRPUR PORIBOHON SERVICE LIMITED","Mirpur12->Shewrapara->GulistanGolapShahMazar");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("MIRPUR PORIBOHON SERVICE LIMITED",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("MIRPUR UNITED SERVICE LIMITED,BIHONGO PORIBOHON LIMITED","Mirpur12->SadarghatVictoriaPark");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("MIRPUR UNITED SERVICE LIMITED,BIHONGO PORIBOHON LIMITED",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("BRTC","Mirpur12->Mirpur10->Agargaon->Farmgate->Shahbag->Motijhil");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("BRTC",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("BIKOLPO AUTO SERVICE","Mirpur12->Mirpur10->Agargaon->Farmgate->Shahbag->Motijhil");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("BIKOLPOAUTOSERVICE",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("HAZI TRANSPORT","Mirpur12->Mirpur10->Agargaon->Farmgate->Shahbag->Motijhil->NotreDameCollege");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("HAZI TRANSPORT",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("WINNER TRANSPORT CO LTD","EdenCollege(Azimpur)->Kolabagan->KarwanBazar->Nabisko->Mohakhali->Gulshan1->BaddaLinkRoad->KurilBisshoRoad");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("WINNERTRANSPORTCOLTD",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("DIP BANGLA PORIBOHON LTD","Azimpur->CityCollege->Kolabagan->Panthopoth->KarwanBazar->Nabisko->GulshanLinkRoad->Gulshan1->KurilBisshoRoad");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("DIP BANGLA PORIBOHON LTD",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("SUCHONA BRF","Azimpur Etimkhana Mor->Kolabagan->Asadgate->Farmgate->Mohakhali->Banani->UttaraHouseBuilding");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("SUCHONA BRF",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("FALGUN ART TRANSPORT PRIVATE LIMITED","AzimpurGirlsSchool&College->ScienceLab->Shahbag->Kakrail->Mouchak->Malibag->Rampura->Badda->KurilBisshoRoad->UttaraHouseBuilding");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("FALGUN ART TRANSPORT PRIVATE LIMITED",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("BRTC","EdenCollege->ScienceLab->Kolabagan->Asadgate->Khamarbari->Kakoli->Bonani->Abdullahpur");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("BRTC",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("SUPROVAT PORIBOHON","SadarghatVictoriaPark->Kakrail->Mouchak->MalibagRailgate->Badda->NotunBazar->Basundhara->Kuril->Uttara->Tongi->Gazipur");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("SUPROVAT PORIBOHON",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("AZMERY GLORY LIMITED,SKY LINE EXPRESS LIMITED","SadarghatVictorialPark->Gulistan->Kakrail->Malibag->Mogbazar->Mohakhali->Airport->Abdullahpur->GazipurBypass->Chondra");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("AZMERY GLORY LIMITED,SKY LINE EXPRESS LIMITED",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("RAJA CITY PORIBOHON LIMITED,FTCL,MOITRI PORIBOHON LIMITED","Mohammadpur->Shankar->StarKabab->Jhigatola->CityCollege->ScienceLab->Shahbag->PressClub->Gulistan->Arambag->NotreDameCollege");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("RAJA CITY PORIBOHON LIMITED,FTCL,MOITRI PORIBOHON LIMITED",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();
        busOffline=new BusOffline("ATCL","Mohammadpur->Asadgate->Shukrabad->Kolabagan->CityCollege->ScienceLab->Katabon->Shahbag->PressClub->Gulistan->Arambag->NotreDameCollege");

        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("ATCL",busOfflineArrayList);
        busOfflineGroupArrayList.add(busOfflineGroup);
        busOfflineArrayList=new ArrayList<>();

        busOffline=new BusOffline("6 NUMBER MOTIJHIL BANANI TRANSPORT LIMITED (LOCAL)","Route 1: Motijhil -> Gulistan -> Polton -> Malibag -> Mogbazar -> Karwanbazar -> Farmgate ->Bijoy Sarani -> Mohakhali -> Gulshan 1 -> Gulshan 2 -> Notun Bazar \n Route 2: Motijhil -> Gulistan -> Polton -> Malibag -> Mogbazar -> Saatrasta -> Nabisko -> Mohakhali -> Gulshan 1 -> Badda Link Road -> Notun Bazar");
        busOfflineArrayList.add(busOffline);
        busOfflineGroup=new BusOfflineGroup("6 NUMBER MOTIJHIL BANANI TRANSPORT LIMITED (LOCAL)",busOfflineArrayList);
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
