package com.example.simurghadmin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
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

public class ServicesBlogActivity extends AppCompatActivity implements Adapter.OnEditClickListener, Adapter.OnDeleteClickListener {
    private Adapter adapter;
    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private ArrayList<Blog> list;
    private String PN,CN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services_blog);

        PN= getIntent().getStringExtra("PN");
        CN= getIntent().getStringExtra("CN");
        recyclerView=findViewById(R.id.recyclerServices);
        databaseReference= FirebaseDatabase.getInstance().getReference("blogs").child(PN).child(CN);
        list=new ArrayList<>();




        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    list.clear();

                    for (DataSnapshot snap: snapshot.getChildren()){

                        Blog blog =snap.getValue(Blog.class);
                        list.add(blog);
                    }

                    adapter= new Adapter(ServicesBlogActivity.this,list);
                    adapter.setOnDeleteClickListener(ServicesBlogActivity.this);
                    adapter.setOnEditClickListener(ServicesBlogActivity.this);
                    recyclerView.setLayoutManager(new LinearLayoutManager(ServicesBlogActivity.this));
                    recyclerView.setAdapter(adapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    @Override
    public void onEditClick(int position) {
        Gson gson = new Gson();
        String data = gson.toJson(list.get(position));
        Intent i = new Intent(ServicesBlogActivity.this, EditPostActivity.class);
        i.putExtra("PN",PN);
        i.putExtra("CN",CN);
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