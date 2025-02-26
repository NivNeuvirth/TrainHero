package com.example.trainhero.adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.trainhero.R;
import com.example.trainhero.models.Exercise;

import java.util.ArrayList;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {

    private ArrayList<Exercise> exerciseList;
    private ArrayList<Exercise> filteredFavoritesList;
    private OnFavoriteClickListener favoriteClickListener;
    private boolean isFavoriteList;

    public ExerciseAdapter(ArrayList<Exercise> exerciseList, OnFavoriteClickListener favoriteClickListener, boolean isFavoriteList) {
        this.exerciseList = exerciseList;
        this.favoriteClickListener = favoriteClickListener;
        this.isFavoriteList = isFavoriteList;
        this.filteredFavoritesList = new ArrayList<>(exerciseList);
    }

    public static class ExerciseViewHolder extends RecyclerView.ViewHolder {

        TextView exerciseName;
        TextView exerciseTarget;
        TextView exerciseEquipment;
        TextView exerciseBodyPart;
        ImageView exerciseGif;
        TextView exerciseSecondaryMuscles;
        TextView exerciseInstructions;
        ImageView exerciseFavoriteBtn;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseName = itemView.findViewById(R.id.product_name_title);
            exerciseTarget = itemView.findViewById(R.id.exe_target);
            exerciseEquipment = itemView.findViewById(R.id.exe_equipment);
            exerciseBodyPart = itemView.findViewById(R.id.exe_body_part);
            exerciseGif = itemView.findViewById(R.id.exe_image);
            exerciseSecondaryMuscles = itemView.findViewById(R.id.exe_secondary_muscles);
            exerciseInstructions = itemView.findViewById(R.id.exe_instructions);
            exerciseFavoriteBtn = itemView.findViewById(R.id.favorite_icon);
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
        // Ensure you're working with the filtered list
        Exercise exercise = filteredFavoritesList.get(position);

        holder.exerciseName.setText(exercise.getName());
        holder.exerciseTarget.setText(exercise.getTarget());
        holder.exerciseEquipment.setText(exercise.getEquipment());
        holder.exerciseBodyPart.setText(exercise.getBodyPart());

        // Load the exercise gif using Glide
        Glide.with(holder.itemView.getContext())
                .load(exercise.getGifUrl())
                .into(holder.exerciseGif);

        // Navigate to exercise details
        holder.itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("exercise", exercise);
            Navigation.findNavController(v).navigate(R.id.action_global_detailedExerciseFragment, bundle);
        });

        // If it's a favorite list, we don't need to change favorite state
        if (!isFavoriteList) {
            holder.exerciseFavoriteBtn.setImageResource(exercise.isFavorite() ? R.drawable.heart_minus_24px : R.drawable.heart_plus_24px);
            holder.exerciseFavoriteBtn.setOnClickListener(v -> {
                exercise.setFavorite(!exercise.isFavorite());
                holder.exerciseFavoriteBtn.setImageResource(exercise.isFavorite() ? R.drawable.heart_minus_24px : R.drawable.heart_plus_24px);
                if (favoriteClickListener != null) {
                    favoriteClickListener.onFavoriteClick(exercise, holder.exerciseFavoriteBtn);
                }
            });
        } else {
            // For favorites list, the heart is filled and clicking removes from the database
            holder.exerciseFavoriteBtn.setImageResource(R.drawable.heart_minus_24px); // Filled heart
            holder.exerciseFavoriteBtn.setOnClickListener(v -> {
                // Remove from Firebase and the UI
                if (favoriteClickListener != null) {
                    favoriteClickListener.onFavoriteClick(exercise, holder.exerciseFavoriteBtn);
                }
                // Remove the exercise from filteredFavoritesList
                filteredFavoritesList.remove(position);
                notifyItemRemoved(position);  // Update the adapter to reflect the change
            });
        }
    }

    @Override
    public int getItemCount() {
        return filteredFavoritesList.size();  // Use filteredFavoritesList here
    }

    public interface OnFavoriteClickListener {
        void onFavoriteClick(Exercise exercise, ImageView favoriteBtn);
    }

    public void filter(String query) {
        filteredFavoritesList.clear();

        if (query.isEmpty()) {
            filteredFavoritesList.addAll(exerciseList);
        } else {
            for (Exercise item : exerciseList) {
                if (item.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredFavoritesList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }
}