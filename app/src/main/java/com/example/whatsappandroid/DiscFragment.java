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
public class DiscFragment extends Fragment {
    private View Cview;
    private RecyclerView recyclerView;
    private FirebaseAuth mAuth;
    private DatabaseReference ref,userRef;
    private  String currentUserID;

    public DiscFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       Cview =  inflater.inflate(R.layout.fragment_disc, container, false);
       recyclerView = Cview.findViewById(R.id.chats_list);
       recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
       mAuth = FirebaseAuth.getInstance();
       currentUserID = mAuth.getCurrentUser().getUid();
       ref = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
       userRef = FirebaseDatabase.getInstance().getReference().child("users");
       return Cview;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Contacts> options = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(ref,Contacts.class)
                .build();
        FirebaseRecyclerAdapter<Contacts, DiscFragment.ChatsViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, DiscFragment.ChatsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final DiscFragment.ChatsViewHolder holder, final int position, @NonNull Contacts model)
            {
                final String list_id = getRef(position).getKey();
                final String[] image = {"Default_image"};
                userRef.child(list_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.exists())
                        {
                            if (dataSnapshot.hasChild("image"))
                            {
                                image[0] = dataSnapshot.child("image").getValue().toString();
                                Picasso.get().load(image[0]).into(holder.imageView);

                            }
                            final String username = dataSnapshot.child("name").getValue().toString();
                            final String status = dataSnapshot.child("status").getValue().toString();

                            holder.username.setText(username);
                            holder.status.setText("Last seen "+ "\n"+ " Date "+ " Time");

                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent chatActivity = new Intent(getContext(),ChatActivity.class);
                                    chatActivity.putExtra("id",list_id);
                                    chatActivity.putExtra("username",username);
                                    chatActivity.putExtra("image", image[0]);
                                    startActivity(chatActivity);
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public DiscFragment.ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts_display_layout,parent,false);
                DiscFragment.ChatsViewHolder viewHolder = new DiscFragment.ChatsViewHolder(view);
                return viewHolder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class ChatsViewHolder extends RecyclerView.ViewHolder
    {
        TextView username,status;
        CircleImageView imageView;
        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.user_profile_name);
            status = itemView.findViewById(R.id.user_status_value);
            imageView =itemView.findViewById(R.id.find_friends_image);
        }
    }
}
