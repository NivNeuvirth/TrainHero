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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.trainhero.R;
import com.example.trainhero.adapters.ExerciseAdapter;
import com.example.trainhero.models.Exercise;
import com.example.trainhero.services.ExercisesDataServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;


public class ByEquipmentFragment extends Fragment implements ExerciseAdapter.OnFavoriteClickListener {

    private RecyclerView recyclerView;
    private ExerciseAdapter exerciseAdapter;
    private ArrayList<Exercise> equipmentExercisesList;
    private String selectedEquipment;

    public ByEquipmentFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_by_equipment, container, false);
        recyclerView = view.findViewById(R.id.equipmentExeRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Button selectButton = view.findViewById(R.id.selectButton);
        selectButton.setOnClickListener(v -> showEquipmentDialog());

        return view;
    }

    private void showEquipmentDialog() {
        String[] equipmentList = getResources().getStringArray(R.array.equipment_list);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select Equipment")
                .setItems(equipmentList, (dialog, which) -> {
                    selectedEquipment = equipmentList[which];
                    fetchExercisesByEquipment(selectedEquipment);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void fetchExercisesByEquipment(String equipment) {
        ExercisesDataServices exercisesDataServices = new ExercisesDataServices();
        ArrayList<Exercise> exerciseList = exercisesDataServices.getExercisesByEquipment(equipment);

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
}
