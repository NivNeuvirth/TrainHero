package com.example.trainhero.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import com.example.trainhero.ExerciseAdapter;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentAfterLogin#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentAfterLogin extends Fragment implements ExerciseAdapter.OnFavoriteClickListener   {

    private RecyclerView recyclerView;
    private ExerciseAdapter exerciseAdapter;
    private ArrayList<Exercise> exerciseList;

    public FragmentAfterLogin() {
        // Required empty public constructor
    }
    public static FragmentAfterLogin newInstance(String param1, String param2) {
        FragmentAfterLogin fragment = new FragmentAfterLogin();
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
        View view = inflater.inflate(R.layout.fragment_after_login, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Fetch exercises
        fetchSingleExercise();

        return view;
    }

    private void fetchExercises() {
        DataServices dataServices = new DataServices();
        exerciseList = dataServices.getAllExercises(); // or use getExerciseById() for a single exercise

        if (exerciseList != null && !exerciseList.isEmpty()) {
            exerciseAdapter = new ExerciseAdapter(exerciseList, this);
            recyclerView.setAdapter(exerciseAdapter);
        } else {
            Toast.makeText(getContext(), "No exercises found", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchSingleExercise() {
        DataServices dataServices = new DataServices();
        Exercise exercise = dataServices.getExerciseById("0012"); // Example ID for a single exercise

        if (exercise != null) {
            ArrayList<Exercise> singleExerciseList = new ArrayList<>();
            singleExerciseList.add(exercise);

            // Check if the exercise is already a favorite
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference favoritesRef = database.getReference("favorites");

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
                    exerciseAdapter = new ExerciseAdapter(singleExerciseList, this);
                    recyclerView.setAdapter(exerciseAdapter);
                } else {
                    Toast.makeText(getContext(), "Failed to check favorites", Toast.LENGTH_SHORT).show();
                }
            });

            exerciseAdapter = new ExerciseAdapter(singleExerciseList, this);
            recyclerView.setAdapter(exerciseAdapter);
        } else {
            Toast.makeText(getContext(), "Exercise not found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFavoriteClick(Exercise exercise, ImageView favoriteBtn) {
        addExerciseToFavorites(exercise, favoriteBtn);
    }

    private void addExerciseToFavorites(Exercise exercise, ImageView favoriteBtn) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference favoritesRef = database.getReference("favorites");

        // Use exercise ID (or another unique identifier) for Firebase
        String exerciseId = exercise.getId(); // Make sure your Exercise model has an `id` field

        // Check if it's already in the favorites by checking the reference in Firebase
        favoritesRef.child(exerciseId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    // Exercise already exists in favorites, so remove it
                    favoritesRef.child(exerciseId).removeValue()
                            .addOnSuccessListener(aVoid -> {
                                favoriteBtn.setImageResource(R.drawable.heart_plus_24px); // Set empty heart
                                Toast.makeText(getContext(), "Exercise removed from favorites", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Failed to remove exercise: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                } else {
                    // Exercise is not in favorites, so add it
                    favoritesRef.child(exerciseId).setValue(exercise)
                            .addOnSuccessListener(aVoid -> {
                                favoriteBtn.setImageResource(R.drawable.heart_minus_24px); // Set filled heart
                                Toast.makeText(getContext(), "Exercise added to favorites", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Failed to add exercise: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
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