package com.example.trainhero.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.trainhero.R;
import com.example.trainhero.models.Exercise;

public class DetailedExerciseFragment extends Fragment {

    public DetailedExerciseFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detailed_exercise, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            Exercise exercise = (Exercise) bundle.getSerializable("exercise");

            TextView exerciseName = view.findViewById(R.id.exe_title);
            TextView exerciseTarget = view.findViewById(R.id.exe_target);
            TextView exerciseEquipment = view.findViewById(R.id.exe_equipment);
            TextView exerciseBodyPart = view.findViewById(R.id.exe_body_part);
            ImageView exerciseGif = view.findViewById(R.id.exe_image);
            TextView exerciseMuscleGroup = view.findViewById(R.id.exe_secondary_muscles);
            TextView exerciseInstructions = view.findViewById(R.id.exe_instructions);

            if (exercise != null) {
                exerciseName.setText(exercise.getName());
                exerciseTarget.setText(getString(R.string.target) + exercise.getTarget());
                exerciseEquipment.setText(getString(R.string.equipment_txt) + exercise.getEquipment());
                exerciseBodyPart.setText(getString(R.string.body_part_txt) + exercise.getBodyPart());

                if (exercise.getSecondaryMuscles() != null && !exercise.getSecondaryMuscles().isEmpty()) {
                    exerciseMuscleGroup.setText(getString(R.string.secondary_muscle_group_txt) +TextUtils.join(", ", exercise.getSecondaryMuscles()));

                } else {
                    exerciseInstructions.setText(R.string.secondary_muscles_none);
                }

                if (exercise.getInstructions() != null && !exercise.getInstructions().isEmpty()) {
                    exerciseInstructions.setText(TextUtils.join("\n", exercise.getInstructions()));

                } else {
                    exerciseInstructions.setText(R.string.instructions_no_instructions_available);
                }

                Glide.with(getContext())
                        .load(exercise.getGifUrl())
                        .into(exerciseGif);
            }
        }

        return view;
    }
}