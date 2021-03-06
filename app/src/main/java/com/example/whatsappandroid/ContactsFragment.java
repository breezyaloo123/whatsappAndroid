package com.example.whatsappandroid;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {

    private View contactView;
    private RecyclerView recyclerView;
    private DatabaseReference ref,userRef;
    private FirebaseAuth mAuth;
    private String currentUserId;

    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        contactView= inflater.inflate(R.layout.fragment_contacts, container, false);
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        ref = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserId);
        userRef = FirebaseDatabase.getInstance().getReference().child("users");
        recyclerView = contactView.findViewById(R.id.contacts_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return contactView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(ref,Contacts.class)
                .build();
        FirebaseRecyclerAdapter<Contacts, ContactsViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, ContactsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ContactsViewHolder holder, final int position, @NonNull Contacts model) {

                String userID = getRef(position).getKey();
                userRef.child(userID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.hasChild("image"))
                        {
                            String profileimage = dataSnapshot.child("image").getValue().toString();
                            String username = dataSnapshot.child("name").getValue().toString();
                            String status = dataSnapshot.child("status").getValue().toString();
                            holder.username.setText(username);
                            holder.status.setText(status);
                            Picasso.get().load(profileimage).placeholder(R.drawable.profile).into(holder.imageView);
                        }
                        else
                        {
                            String username = dataSnapshot.child("name").getValue().toString();
                            String status = dataSnapshot.child("status").getValue().toString();
                            holder.username.setText(username);
                            holder.status.setText(status);

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts_display_layout,parent,false);
                ContactsViewHolder viewHolder = new ContactsViewHolder(view);
                return viewHolder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class ContactsViewHolder extends RecyclerView.ViewHolder
    {
        TextView username,status;
        CircleImageView imageView;
        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.user_profile_name);
            status = itemView.findViewById(R.id.user_status_value);
            imageView =itemView.findViewById(R.id.find_friends_image);
        }
    }
}
