package com.example.simurghadmin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;


import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.Holder>{

    private Context context;
    private ArrayList<Blog> list;
    private OnEditClickListener onEditClickListener;
    private OnDeleteClickListener onDeleteClickListener;

    public Adapter(Context context, ArrayList<Blog> list) {
        this.context = context;
        this.list = list;
    }

    // Setters for the listeners
    public void setOnEditClickListener(OnEditClickListener onEditClickListener) {
        this.onEditClickListener = onEditClickListener;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener onDeleteClickListener) {
        this.onDeleteClickListener = onDeleteClickListener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_news, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Blog blog = list.get(position);

        holder.txtMainHeading.setText(blog.getTitle());
        holder.txtContent.setText(blog.getDescription());
        Glide.with(context)
                .load(blog.getImageUrl())
                .placeholder(R.drawable.simuico)
                .into(holder.imgNews);

        // Handle the click events for the buttons
        holder.btnEdit.setOnClickListener(v -> {
            if (onEditClickListener != null) {
                onEditClickListener.onEditClick(position);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (onDeleteClickListener != null) {
                onDeleteClickListener.onDeleteClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        private TextView txtMainHeading, txtContent;
        private ImageView imgNews;
        private Button btnEdit, btnDelete;

        public Holder(@NonNull View itemView) {
            super(itemView);

            imgNews = itemView.findViewById(R.id.imgNews);
            txtMainHeading = itemView.findViewById(R.id.txtMainHeading);
            txtContent = itemView.findViewById(R.id.txtContent);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    // Interface for Edit button click
    public interface OnEditClickListener {
        void onEditClick(int position);
    }

    // Interface for Delete button click
    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }
}
