package com.example.whatsappandroid;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
public class RequestFragment extends Fragment {

    private View mRequest;
    private RecyclerView mRecycleview;
    private DatabaseReference ref,reference1,contactsRef;
    private FirebaseAuth mAuth;
    private String currentUserID;

    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRequest= inflater.inflate(R.layout.fragment_request, container, false);
        mRecycleview = mRequest.findViewById(R.id.request_fragment);
        mRecycleview.setLayoutManager(new LinearLayoutManager(getContext()));
        ref = FirebaseDatabase.getInstance().getReference().child("Chat Request");
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        mAuth = FirebaseAuth.getInstance();
        reference1 = FirebaseDatabase.getInstance().getReference().child("users");
        currentUserID = mAuth.getCurrentUser().getUid();
        return mRequest;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Contacts> options = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(ref.child(currentUserID),Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts, RequestFragment.RequestsViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, RequestFragment.RequestsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final RequestFragment.RequestsViewHolder holder, final int position, @NonNull Contacts model) {
                holder.itemView.findViewById(R.id.request_accept_button).setVisibility(View.VISIBLE);
                holder.itemView.findViewById(R.id.request_cancel_button).setVisibility(View.VISIBLE);
                final String list_id = getRef(position).getKey();
                DatabaseReference reference = getRef(position).child("request_type").getRef();

                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.exists())
                        {
                            String type = dataSnapshot.getValue().toString();
                            if (type.equals("received"))
                            {
                                reference1.child(list_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild("image"))
                                        {

                                            final String image = dataSnapshot.child("image").getValue().toString();
                                            Picasso.get().load(image).into(holder.imageView);
                                        }
                                        final String request_username = dataSnapshot.child("name").getValue().toString();
                                        final String request_status = dataSnapshot.child("status").getValue().toString();
                                        holder.username.setText(request_username);
                                        holder.status.setText("Veut connecter avec toi");


                                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                CharSequence sequence[] = new CharSequence[]
                                                        {
                                                             "Accept",
                                                             "Cancel"
                                                        };
                                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                builder.setTitle(request_username +" chat request");
                                                builder.setItems(sequence, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, final int i)
                                                    {
                                                        if (i == 0)
                                                        {
                                                            contactsRef.child(currentUserID).child(list_id).child("Contact").
                                                                    setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task)
                                                                {
                                                                    if (task.isSuccessful())
                                                                    {
                                                                        contactsRef.child(list_id).child(currentUserID).child("Contact").
                                                                                setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task)
                                                                            {
                                                                                if (task.isSuccessful())
                                                                                {
                                                                                    ref.child(currentUserID).child(list_id).
                                                                                            removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task)
                                                                                        {
                                                                                            if (task.isSuccessful())
                                                                                            {
                                                                                                ref.child(list_id).child(currentUserID).
                                                                                                        removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onComplete(@NonNull Task<Void> task)
                                                                                                    {
                                                                                                        if (task.isSuccessful())
                                                                                                        {
                                                                                                            Toast.makeText(getContext(),"Contacts saved",Toast.LENGTH_LONG).show();
                                                                                                        }


                                                                                                    }
                                                                                                });
                                                                                            }

                                                                                        }
                                                                                    });
                                                                                }
                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            });
                                                        }
                                                        if (i == 1)
                                                        {
                                                            ref.child(currentUserID).child(list_id).
                                                                    removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task)
                                                                {
                                                                    if (task.isSuccessful())
                                                                    {
                                                                        ref.child(list_id).child(currentUserID).
                                                                                removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task)
                                                                            {
                                                                                if (task.isSuccessful())
                                                                                {
                                                                                    Toast.makeText(getContext(),"Contacts declined",Toast.LENGTH_LONG).show();
                                                                                }
                                                                            }
                                                                        });
                                                                    }

                                                                }
                                                            });

                                                        }
                                                    }
                                                });
                                                builder.show();
                                            }
                                        });

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            @NonNull
            @Override
            public RequestFragment.RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts_display_layout,parent,false);
                RequestsViewHolder viewHolder = new RequestsViewHolder(view);
                return viewHolder;
            }
        };
        mRecycleview.setAdapter(adapter);
        adapter.startListening();
    }

    public static class RequestsViewHolder extends RecyclerView.ViewHolder
    {
        TextView username,status;
        CircleImageView imageView;
        Button accept,cancel;
        public RequestsViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.user_profile_name);
            status = itemView.findViewById(R.id.user_status_value);
            imageView =itemView.findViewById(R.id.find_friends_image);
            accept = itemView.findViewById(R.id.request_accept_button);
            cancel = itemView.findViewById(R.id.request_cancel_button);
        }
    }
}
