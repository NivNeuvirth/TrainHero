package com.example.trainhero.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.trainhero.R;
import com.example.trainhero.adapters.ExerciseAdapter;
import com.example.trainhero.models.Exercise;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment {

    private RecyclerView recyclerView;
    private ExerciseAdapter favoriteAdapter;
    private ArrayList<Exercise> favoriteExercises = new ArrayList<>();
    private DatabaseReference userFavoritesRef;
    private FirebaseAuth auth;

    public FavoritesFragment() {
        // Required empty public constructor
    }
    public static FavoritesFragment newInstance() {
        return new FavoritesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewFavorites);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        favoriteAdapter = new ExerciseAdapter(favoriteExercises, null, true);
        recyclerView.setAdapter(favoriteAdapter);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            String userId = user.getUid();
            userFavoritesRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("favorites");
            fetchFavorites();
        } else {
            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void fetchFavorites() {
        // Fetch favorite exercises from the database for the current user
        userFavoritesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                favoriteExercises.clear();
                DataSnapshot dataSnapshot = task.getResult();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Exercise exercise = snapshot.getValue(Exercise.class);
                    if (exercise != null) {
                        favoriteExercises.add(exercise);
                    }
                }
                favoriteAdapter = new ExerciseAdapter(favoriteExercises, this::onFavoriteClick, true);
                recyclerView.setAdapter(favoriteAdapter);
            }
        });
    }

    private void onFavoriteClick(Exercise exercise, ImageView favoriteBtn) {
        // Remove the exercise from the user's favorites in Firebase
        userFavoritesRef.child(exercise.getId()).removeValue()
                .addOnSuccessListener(aVoid -> {
                    // Exercise removed from favorites successfully
                    Toast.makeText(getContext(), "Exercise removed from favorites", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to remove exercise: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}