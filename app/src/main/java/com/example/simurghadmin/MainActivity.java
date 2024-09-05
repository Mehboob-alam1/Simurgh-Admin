package com.example.simurghadmin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.simurghadmin.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {


    private ActivityMainBinding binding;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        setSpinner();
        binding.btnManagePost.setOnClickListener(v -> {

            startActivity(new Intent(MainActivity.this, ManagePostActivity.class));
        });

        binding.btnAddEmail.setOnClickListener(v -> {

            startActivity(new Intent(MainActivity.this, AddEmailActivity.class));
        });
        binding.btnAddPost.setOnClickListener(v -> {


 uploadData();

        });

        binding.imageView.setOnClickListener(v -> openFileChooser());

    }

    private void setSpinner() {

        // Data for the first spinner
        String[] firstSpinnerItems = {"Home", "About", "Services", "Contact"};
        ArrayAdapter<String> firstAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, firstSpinnerItems);
        firstAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.firstSpinner.setAdapter(firstAdapter);

        // Data for the second spinner
                String[] servicesItems = {"Cyber", "Web3", "XR","IoT","Charity"};
        ArrayAdapter<String> secondAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, servicesItems);
        secondAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        // Apply the adapter to the spinner
        // Listener for the first spinner
        binding.firstSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if (selectedItem.equals("Services")) {
                    binding.secondSpinner.setVisibility(View.VISIBLE);
                    binding.secondSpinner.setAdapter(secondAdapter);
                } else {
                    binding.secondSpinner.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                binding.secondSpinner.setVisibility(View.GONE);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            binding.imageView.setImageURI(imageUri);
        }
    }


    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
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

        if (binding.firstSpinner.getSelectedItem() ==null){
            Toast.makeText(this, "Please select the category to add a post", Toast.LENGTH_SHORT).show();
return;
        }
        if (binding.firstSpinner.getSelectedItem().toString().equals("Services") && binding.secondSpinner.getSelectedItem() == null){
            Toast.makeText(this, "Please select sub category for services", Toast.LENGTH_SHORT).show();
          return;
        }
        dialog= new ProgressDialog(this);
        dialog.setMessage("Please wait....");
        dialog.show();

        String pushId= UUID.randomUUID().toString();
        // Proceed with the upload if all fields are valid
        final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + ".jpg");

        fileReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();

                        // Create an instance of the Blog model class
                        Blog blog = new Blog(title, description, downloadUri.toString(), pushId,binding.firstSpinner.getSelectedItem().toString());


                        if (binding.secondSpinner.getSelectedItem()!=null){
                            databaseReference.child("blogs").child(binding.firstSpinner.getSelectedItem().toString()).child(binding.secondSpinner.getSelectedItem().toString()).child(pushId).setValue(blog)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Toast.makeText(MainActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                                            binding.etDesc.setText("");
                                            binding.etEmail.setText("");
                                            binding.imageView.setImageURI(null);
                                            imageUri=null;
                                            dialog.dismiss();
                                        } else {
                                            dialog.dismiss();
                                            Toast.makeText(MainActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }else{
                            databaseReference.child("blogs").child(binding.firstSpinner.getSelectedItem().toString()).child(pushId).setValue(blog)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Toast.makeText(MainActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                                            binding.etDesc.setText("");
                                            binding.etEmail.setText("");
                                            binding.imageView.setImageURI(null);
                                            imageUri=null;
                                            dialog.dismiss();
                                        } else {
                                            dialog.dismiss();
                                            Toast.makeText(MainActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                        // Push the Blog object to Firebase Realtime Database

                    }
                }))
                .addOnFailureListener(e -> {
                    dialog.dismiss();
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                } );


    }
}