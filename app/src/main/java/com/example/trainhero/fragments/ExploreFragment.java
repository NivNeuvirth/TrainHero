package com.example.trainhero.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import com.example.trainhero.adapters.ExerciseAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.trainhero.R;
import com.example.trainhero.models.Exercise;
import com.example.trainhero.services.ExercisesDataServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExploreFragment extends Fragment implements ExerciseAdapter.OnFavoriteClickListener {

    private RecyclerView recyclerView;
    private ExerciseAdapter exerciseAdapter;
    private ArrayList<Exercise> exerciseList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isDataLoaded = false;

    public ExploreFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            isDataLoaded = savedInstanceState.getBoolean("isDataLoaded", false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore, container, false);
        recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(() -> {shuffleAndRefreshExercises(); });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!isDataLoaded) {
            fetchRandomExercises();
        } else {
            exerciseAdapter = new ExerciseAdapter(exerciseList, this, false);
            recyclerView.setAdapter(exerciseAdapter);
        }
    }

    private void fetchRandomExercises() {
        ExercisesDataServices exercisesDataServices = new ExercisesDataServices();
        exerciseList = exercisesDataServices.getAllExercises();
        Toast.makeText(getContext(), "Exercises Fetched Successfully", Toast.LENGTH_SHORT).show();

        if (exerciseList != null && !exerciseList.isEmpty()) {
            shuffleAndRefreshExercises();
            isDataLoaded = true;
        } else {
            Toast.makeText(getContext(), "No exercises found", Toast.LENGTH_SHORT).show();
        }
    }

    private void shuffleAndRefreshExercises() {
        if (exerciseList != null && !exerciseList.isEmpty()) {

            Collections.shuffle(exerciseList);
            List<Exercise> randomExercises = exerciseList.subList(0, Math.min(5, exerciseList.size()));

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference favoritesRef = database.getReference("favorites");

            for (Exercise exercise : randomExercises) {
                favoritesRef.child(exercise.getId()).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            exercise.setFavorite(true);
                        } else {
                            exercise.setFavorite(false);
                        }

                        exerciseAdapter = new ExerciseAdapter(new ArrayList<>(randomExercises), this, false);
                        recyclerView.setAdapter(exerciseAdapter);
                        swipeRefreshLayout.setRefreshing(false);
                    } else {
                        Toast.makeText(getContext(), "Failed to check favorites", Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }
    }

    @Override
    public void onFavoriteClick(Exercise exercise, ImageView favoriteBtn) {
        addExerciseToFavorites(exercise, favoriteBtn);
    }

    private void addExerciseToFavorites(Exercise exercise, ImageView favoriteBtn) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");

        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();
        String exerciseId = exercise.getId();

        DatabaseReference userFavoritesRef = usersRef.child(userId).child("favorites").child(exerciseId);

        userFavoritesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                userFavoritesRef.removeValue()
                        .addOnSuccessListener(aVoid -> {
                            favoriteBtn.setImageResource(R.drawable.heart_plus_24px);
                            Toast.makeText(getContext(), "Removed from favorites", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Failed to remove: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                userFavoritesRef.setValue(exercise)
                        .addOnSuccessListener(aVoid -> {
                            favoriteBtn.setImageResource(R.drawable.heart_minus_24px);
                            Toast.makeText(getContext(), "Added to favorites", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Failed to add: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isDataLoaded", isDataLoaded);
    }
}