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

        Exercise exercise = filteredFavoritesList.get(position);
        holder.exerciseName.setText(exercise.getName());
        holder.exerciseTarget.setText(exercise.getTarget());
        holder.exerciseEquipment.setText(exercise.getEquipment());
        holder.exerciseBodyPart.setText(exercise.getBodyPart());
        Glide.with(holder.itemView.getContext())
                .load(exercise.getGifUrl())
                .into(holder.exerciseGif);
        holder.itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("exercise", exercise);
            Navigation.findNavController(v).navigate(R.id.action_global_detailedExerciseFragment, bundle);
        });

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
            holder.exerciseFavoriteBtn.setImageResource(R.drawable.heart_minus_24px);
            holder.exerciseFavoriteBtn.setOnClickListener(v -> {
                if (favoriteClickListener != null) {
                    favoriteClickListener.onFavoriteClick(exercise, holder.exerciseFavoriteBtn);
                }
                if (position < filteredFavoritesList.size()) {
                    filteredFavoritesList.remove(position);
                    exerciseList.remove(exercise); // Remove from original list as well
                    notifyItemRemoved(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return filteredFavoritesList.size();
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