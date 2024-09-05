package com.example.simurghadmin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;

public class ManagePostActivity extends AppCompatActivity implements Adapter.OnEditClickListener, Adapter.OnDeleteClickListener{
private Adapter adapter;
private RecyclerView recyclerView;
private DatabaseReference databaseReference;
private LinearLayout btnCyber,btnWeb3,btnXR,btnIOT,btnCharity;
private ArrayList<Blog> list;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_post);

        recyclerView=findViewById(R.id.recyclerHome);
        databaseReference= FirebaseDatabase.getInstance().getReference("blogs").child("Home");
        list=new ArrayList<>();
        btnCharity=findViewById(R.id.btnCharity);
        btnIOT=findViewById(R.id.btnIOT);
        btnXR=findViewById(R.id.btnXR);
        btnWeb3=findViewById(R.id.btnWeb3);
        btnCyber=findViewById(R.id.btnCyber);





        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    list.clear();

                    for (DataSnapshot snap: snapshot.getChildren()){

                        Blog blog =snap.getValue(Blog.class);
                        list.add(blog);
                    }

                    adapter= new Adapter(ManagePostActivity.this,list);
                    adapter.setOnDeleteClickListener(ManagePostActivity.this);
                    adapter.setOnEditClickListener(ManagePostActivity.this);
                    recyclerView.setLayoutManager(new LinearLayoutManager(ManagePostActivity.this));
                    recyclerView.setAdapter(adapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





        btnCharity.setOnClickListener(v -> {

            Intent i = new Intent(ManagePostActivity.this,ServicesBlogActivity.class);
            i.putExtra("PN","Services");
            i.putExtra("CN","Charity");
            startActivity(i);

        });

        btnIOT.setOnClickListener(v -> {
            Intent i = new Intent(ManagePostActivity.this,ServicesBlogActivity.class);
            i.putExtra("PN","Services");
            i.putExtra("CN","IoT");
            startActivity(i);
        });

        btnXR.setOnClickListener(v -> {
            Intent i = new Intent(ManagePostActivity.this,ServicesBlogActivity.class);
            i.putExtra("PN","Services");
            i.putExtra("CN","XR");
            startActivity(i);
        });
        btnWeb3.setOnClickListener(v -> {
            Intent i = new Intent(ManagePostActivity.this,ServicesBlogActivity.class);
            i.putExtra("PN","Services");
            i.putExtra("CN","Web3");
            startActivity(i);
        });
        btnCyber.setOnClickListener(v -> {
            Intent i = new Intent(ManagePostActivity.this,ServicesBlogActivity.class);
            i.putExtra("PN","Services");
            i.putExtra("CN","Cyber");
            startActivity(i);
        });


    }

    @Override
    public void onEditClick(int position) {
        Gson gson = new Gson();
        String data = gson.toJson(list.get(position));
        Intent i = new Intent(ManagePostActivity.this, EditPostActivity.class);
        i.putExtra("PN","Home");
        i.putExtra("CN","null");
        i.putExtra("data",data);
        startActivity(i);
    }

    @Override
    public void onDeleteClick(int position) {
        databaseReference.child(list.get(position).getPushId()).removeValue((error, ref) -> {
            adapter.notifyDataSetChanged();

            Toast.makeText(this, "Blog removed", Toast.LENGTH_SHORT).show();
        });
    }
}