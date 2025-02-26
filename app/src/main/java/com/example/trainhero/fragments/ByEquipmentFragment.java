package com.example.trainhero.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trainhero.R;
import com.example.trainhero.adapters.ExerciseAdapter;
import com.example.trainhero.models.Exercise;
import com.example.trainhero.services.DataServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ByEquipmentFragment extends Fragment implements ExerciseAdapter.OnFavoriteClickListener {

    private RecyclerView recyclerView;
    private ExerciseAdapter exerciseAdapter;
    private ArrayList<Exercise> equipmentExercisesList;
    private String selectedEquipment; // Store the selected equipment type

    public ByEquipmentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_by_equipment, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.equipmentExeRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize the button and set an OnClickListener to show the dialog
        Button selectButton = view.findViewById(R.id.selectButton);
        selectButton.setOnClickListener(v -> showEquipmentDialog());

        return view;
    }

    private void showEquipmentDialog() {
        String[] equipmentList = getResources().getStringArray(R.array.equipment_list);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select Equipment")
                .setItems(equipmentList, (dialog, which) -> {
                    selectedEquipment = equipmentList[which]; // Store the selected equipment
                    fetchExercisesByEquipment(selectedEquipment); // Fetch exercises based on selection
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void fetchExercisesByEquipment(String equipment) {
        DataServices dataServices = new DataServices();
        ArrayList<Exercise> exerciseList = dataServices.getExercisesByEquipment(equipment);

        if (exerciseList != null && !exerciseList.isEmpty()) {
            exerciseAdapter = new ExerciseAdapter(exerciseList, this, false);
            recyclerView.setAdapter(exerciseAdapter);
        } else {
            Toast.makeText(getContext(), "No exercises found for " + equipment, Toast.LENGTH_SHORT).show();
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
}
