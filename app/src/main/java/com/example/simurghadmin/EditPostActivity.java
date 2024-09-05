package com.example.simurghadmin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.simurghadmin.databinding.ActivityEditPostBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.util.UUID;

public class EditPostActivity extends AppCompatActivity {

    private String PN,CN,data;
    private ActivityEditPostBinding binding;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private ProgressDialog dialog;
    Blog blog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityEditPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        PN= getIntent().getStringExtra("PN");
        CN= getIntent().getStringExtra("CN");
        data= getIntent().getStringExtra("data");



        if (CN.equals("null")){
            databaseReference = FirebaseDatabase.getInstance().getReference("blogs").child(PN);

        }else{
            databaseReference = FirebaseDatabase.getInstance().getReference("blogs").child(PN).child(CN);

        }


        Gson gson = new Gson();
         blog = gson.fromJson(data, Blog.class);

        // Now you can use the User object
        if (blog != null) {
            // Example: Display the user's name

            imageUri= Uri.parse(blog.getImageUrl());


            binding.etEmail.setText(blog.getTitle());
            binding.etDesc.setText(blog.getDescription());
            Glide.with(this)
                    .load(blog.getImageUrl())
                    .into(binding.imageView);

            // Do something with the user object
        }


        binding.btnAddPost.setOnClickListener(v -> {

            uploadData();
        });

        binding.imageView.setOnClickListener(v -> openFileChooser());








    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            binding.imageView.setImageURI(imageUri);
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + ".jpg");


            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                       fileReference.getDownloadUrl().addOnCompleteListener(task -> {
                           if (task.isSuccessful()) {
                               Uri downloadUri = task.getResult();
                               databaseReference.child(blog.getPushId()).child("imageUrl").setValue(downloadUri);
                               Toast.makeText(this, "Image updated", Toast.LENGTH_SHORT).show();
                           }
                       });
                    });

        }
    }

    private void uploadData() {
        // Get the title and description entered by the user



        final String title = binding.etEmail.getText().toString().trim();
        final String description = binding.etDesc.getText().toString().trim();

        // Check if the title is empty
        if (title.isEmpty()) {
            binding.etEmail.setError("Title is required");
            binding.etEmail.requestFocus();
            return;
        }

        // Check if the description is empty
        if (description.isEmpty()) {
            binding.etDesc.setError("Description is required");
            binding.etDesc.requestFocus();
            return;
        }

        // Check if an image is selected
        if (imageUri == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }


        if (imageUri !=null) {
            dialog = new ProgressDialog(this);
            dialog.setMessage("Please wait....");
            dialog.show();
            databaseReference.child(blog.getPushId()).child("description").setValue(binding.etDesc.getText().toString());
            databaseReference.child(blog.getPushId()).child("title").setValue(binding.etEmail.getText().toString());
        }
        dialog.dismiss();
        Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
        finish();




    }
}