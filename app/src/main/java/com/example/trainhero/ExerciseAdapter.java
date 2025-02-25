package com.example.trainhero;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//import com.bumptech.glide.Glide;
import com.bumptech.glide.Glide;
import com.example.trainhero.R;
import com.example.trainhero.models.Exercise;

import java.util.ArrayList;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {

    private ArrayList<Exercise> exerciseList;

    public ExerciseAdapter(ArrayList<Exercise> exerciseList) {
        this.exerciseList = exerciseList;
    }

    public static class ExerciseViewHolder extends RecyclerView.ViewHolder {

        TextView exerciseName;
        TextView exerciseTarget;
        TextView exerciseEquipment;
        TextView exerciseBodyPart;
        ImageView exerciseGif;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseName = itemView.findViewById(R.id.product_name_title);
            exerciseTarget = itemView.findViewById(R.id.exe_target);
            exerciseEquipment = itemView.findViewById(R.id.exe_equipment);
            exerciseBodyPart = itemView.findViewById(R.id.exe_body_part);
            exerciseGif = itemView.findViewById(R.id.exe_image);
        }
    }

    @NonNull
    @Override
    public ExerciseAdapter.ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_row, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseAdapter.ExerciseViewHolder holder, int position) {
        Exercise exercise = exerciseList.get(position);
        holder.exerciseName.setText(exercise.getName());
        holder.exerciseTarget.setText("Target: " + exercise.getTarget());
        holder.exerciseEquipment.setText("Equipment: " + exercise.getEquipment());
        holder.exerciseBodyPart.setText("Body Part: " + exercise.getBodyPart());

        // Load the exercise gif using Glide
        Glide.with(holder.itemView.getContext())
                .load(exercise.getGifUrl())
                .into(holder.exerciseGif);

        // Handle clicking on the exercise (if needed)
        holder.itemView.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "Clicked on: " + exercise.getName(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return exerciseList.size();
    }
}
