package com.example.whatsappandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);
        ref = FirebaseDatabase.getInstance().getReference().child("users");
        recyclerView = findViewById(R.id.recycle_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        toolbar = findViewById(R.id.find_friends_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Finds Friends");

    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(ref, Contacts.class)
                .build();
        FirebaseRecyclerAdapter<Contacts,FriendsViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, FriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FriendsViewHolder holder, final int position, @NonNull Contacts model) {
                holder.username.setText(model.getName());
                holder.status.setText(model.getStatus());
                Picasso.get().load(model.getImage()).placeholder(R.drawable.profile).into(holder.imageView);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String friend_id = getRef(position).getKey();
                        Intent profileIntent = new Intent(getApplicationContext(),FriendProfileActivity.class);
                        profileIntent.putExtra("id",friend_id);
                        startActivity(profileIntent);
                    }
                });
            }

            @NonNull
            @Override
            public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts_display_layout,parent,false);
                FriendsViewHolder viewHolder = new FriendsViewHolder(view);
                return viewHolder;
            }
        };
        recyclerView.setAdapter(adapter);

        adapter.startListening();

    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder
    {

        TextView username,status;
        CircleImageView imageView;
        public FriendsViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.user_profile_name);
            status = itemView.findViewById(R.id.user_status_value);
            imageView = itemView.findViewById(R.id.find_friends_image);
        }
    }
}
