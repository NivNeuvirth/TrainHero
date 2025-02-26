package com.example.trainhero.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import com.example.trainhero.adapters.ExerciseAdapter;

import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class ExploreFragment extends Fragment implements ExerciseAdapter.OnFavoriteClickListener   {

    private RecyclerView recyclerView;
    private ExerciseAdapter exerciseAdapter;
    private ArrayList<Exercise> exerciseList;

    public ExploreFragment() {
        // Required empty public constructor
    }
    public static ExploreFragment newInstance(String param1, String param2) {
        ExploreFragment fragment = new ExploreFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Fetch exercises
        //fetchSingleExercise();
        fetchRandomExercises();

        return view;
    }

    private void fetchRandomExercises() {
        DataServices dataServices = new DataServices();
        ArrayList<Exercise> exerciseList = dataServices.getAllExercises();

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
                    } else {
                        Toast.makeText(getContext(), "Failed to check favorites", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else {
            Toast.makeText(getContext(), "No exercises found", Toast.LENGTH_SHORT).show();
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


    private void checkIfFavorite(Exercise exercise) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference favoritesRef = database.getReference("favorites").child(exercise.getId());

        favoritesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    // Exercise is in favorites, so set the filled heart
                    exercise.setFavorite(true); // Assuming you have a 'favorite' field in Exercise model
                } else {
                    // Exercise is not in favorites, so set the empty heart
                    exercise.setFavorite(false); // Assuming you have a 'favorite' field in Exercise model
                }
                // Notify adapter that the favorite status has changed (to update heart icon)
                exerciseAdapter.notifyDataSetChanged();
            }
        });
    }


}