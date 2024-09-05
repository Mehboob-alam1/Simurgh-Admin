package com.example.simurghadmin;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.simurghadmin.databinding.ActivityAddEmailBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddEmailActivity extends AppCompatActivity {

    private ActivityAddEmailBinding binding;

    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityAddEmailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        databaseReference= FirebaseDatabase.getInstance().getReference("contact");




        binding.btnAdd.setOnClickListener(v -> {

            if (binding.etEmail.getText().toString().isEmpty()){
                Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show();
            }else{
                databaseReference.setValue(binding.etEmail.getText().toString())
                        .addOnCompleteListener(task -> {
                            if (task.isComplete() && task.isSuccessful()){
                                Toast.makeText(this, "Email address added", Toast.LENGTH_SHORT).show();
                            }
                        });
                finish();
            }
        });




        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    String value = snapshot.getValue(String.class);
                    binding.etEmail.setText(value);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}