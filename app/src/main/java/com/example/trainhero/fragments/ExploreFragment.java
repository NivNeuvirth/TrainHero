package com.example.trainhero.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import com.example.trainhero.adapters.ExerciseAdapter;

import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
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
import com.example.trainhero.services.DataServices;
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
    private boolean isDataLoaded = false; // This flag will be used for first-time data loading

    public ExploreFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Handle restoration of isDataLoaded if needed
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

        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Shuffle the existing exercise list and update the adapter
            shuffleAndRefreshExercises();
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Only fetch data if it's not already loaded
        if (!isDataLoaded) {
            fetchRandomExercises();  // Fetch exercises only once
        } else {
            // If data is already loaded, just set the adapter with the existing list
            exerciseAdapter = new ExerciseAdapter(exerciseList, this, false);
            recyclerView.setAdapter(exerciseAdapter);
        }
    }

    private void fetchRandomExercises() {
        DataServices dataServices = new DataServices();
        exerciseList = dataServices.getAllExercises();  // Store exercises in class-level variable
        Toast.makeText(getContext(), "Fetched", Toast.LENGTH_SHORT).show();

        if (exerciseList != null && !exerciseList.isEmpty()) {
            // Shuffle the list to randomize the exercises
            shuffleAndRefreshExercises();
            isDataLoaded = true;  // Mark the data as loaded
        } else {
            Toast.makeText(getContext(), "No exercises found", Toast.LENGTH_SHORT).show();
        }
    }

    private void shuffleAndRefreshExercises() {
        if (exerciseList != null && !exerciseList.isEmpty()) {
            // Shuffle the list to randomize the exercises
            Collections.shuffle(exerciseList);

            // Select a subset of random exercises (e.g., 5 random exercises)
            List<Exercise> randomExercises = exerciseList.subList(0, Math.min(5, exerciseList.size()));

            // Check if the exercise is already a favorite for each of the selected random exercises
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference favoritesRef = database.getReference("favorites");

            for (Exercise exercise : randomExercises) {
                favoritesRef.child(exercise.getId()).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            // Exercise is a favorite, set filled heart
                            exercise.setFavorite(true);
                        } else {
                            // Exercise is not a favorite, set empty heart
                            exercise.setFavorite(false);
                        }

                        // Now set the adapter with updated favorite state
                        exerciseAdapter = new ExerciseAdapter(new ArrayList<>(randomExercises), this, false);
                        recyclerView.setAdapter(exerciseAdapter);

                        // Stop the refreshing animation
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

        String userId = user.getUid(); // Get current user's ID
        String exerciseId = exercise.getId(); // Ensure Exercise model has `id`

        // Reference to user's favorites
        DatabaseReference userFavoritesRef = usersRef.child(userId).child("favorites").child(exerciseId);

        // Check if exercise exists in user's favorites
        userFavoritesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                // Exercise exists, remove it
                userFavoritesRef.removeValue()
                        .addOnSuccessListener(aVoid -> {
                            favoriteBtn.setImageResource(R.drawable.heart_plus_24px); // Empty heart
                            Toast.makeText(getContext(), "Removed from favorites", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Failed to remove: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                // Exercise doesn't exist, add it
                userFavoritesRef.setValue(exercise)
                        .addOnSuccessListener(aVoid -> {
                            favoriteBtn.setImageResource(R.drawable.heart_minus_24px); // Filled heart
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
        outState.putBoolean("isDataLoaded", isDataLoaded); // Save state of dataLoaded flag
    }
}