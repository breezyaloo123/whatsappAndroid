package com.example.whatsappandroid;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.telephony.mbms.MbmsErrors;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsFragment extends Fragment {

    private View groupFragmentView;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> groupsItem = new ArrayList<>();
    private DatabaseReference ref;
    private MessageHelper helper;

    public GroupsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        groupFragmentView =  inflater.inflate(R.layout.fragment_groups, container, false);
        ref = FirebaseDatabase.getInstance().getReference().child("Groups");

        Initialization();
        getGroupsName();
        getGroup();
        readData();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String groupName = adapterView.getItemAtPosition(i).toString();
                Intent groupchat = new Intent(getContext(),GroupChatActivity.class);
                groupchat.putExtra("groupName",groupName);
                groupchat.putExtra("userID",readData());
                startActivity(groupchat);

            }
        });
        return groupFragmentView;
    }

    private String readData() {
        helper = new MessageHelper(getContext());
        String id="";
        SQLiteDatabase db = helper.getReadableDatabase();
        String[] request = {
                Message._ID,
                Message.EMAIL,
                Message.PASSWORD
        };

        Cursor cursor = db.query(Message.TABLE,request,null,null,null,null,null);

        while (cursor.moveToNext())
        {
            id = cursor.getString(0);
            String nn = cursor.getString(1);
            String pp = cursor.getString(2);
            Log.d("IDD",id);
            Log.d("EMAIL",nn);
//            Log.d("PWD",pp);
        }

        cursor.close();
        return id;
    }



    private void Initialization() {

        listView = groupFragmentView.findViewById(R.id.group_listview);
        adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,groupsItem);
        listView.setAdapter(adapter);
    }
    private void getGroupsName() {

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    Set<String> set = new HashSet<>();
                    Iterator iterator = dataSnapshot.getChildren().iterator();
                    while (iterator.hasNext())
                    {
                        set.add(((DataSnapshot)iterator.next()).getKey());
                    }
                    groupsItem.clear();
                    groupsItem.addAll(set);
                    adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void getGroup()
    {
        helper = new MessageHelper(getContext());
        Set<String> set = new HashSet<>();
        SQLiteDatabase db = helper.getReadableDatabase();

        String[] request = {
                Message._ID,
                Message.GROUPS
        };

        Cursor cursor = db.query(Message.TABLE1,request,null,null,null,null,null);

        while (cursor.moveToNext())
        {
            set.add(cursor.getString(1));
        }
        groupsItem.clear();
        groupsItem.addAll(set);
        adapter.notifyDataSetChanged();
    }
}
