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

    public DetailedExerciseFragment() {
        // Required empty public constructor
    }

    public static DetailedExerciseFragment newInstance(String param1, String param2) {
        DetailedExerciseFragment fragment = new DetailedExerciseFragment();
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
        View view = inflater.inflate(R.layout.fragment_detailed_exercise, container, false);

        // Retrieve the Exercise object from the arguments (Bundle)
        Bundle bundle = getArguments();
        if (bundle != null) {
            Exercise exercise = (Exercise) bundle.getSerializable("exercise");

            // Initialize views
            TextView exerciseName = view.findViewById(R.id.exe_title);
            TextView exerciseTarget = view.findViewById(R.id.exe_target);
            TextView exerciseEquipment = view.findViewById(R.id.exe_equipment);
            TextView exerciseBodyPart = view.findViewById(R.id.exe_body_part);
            ImageView exerciseGif = view.findViewById(R.id.exe_image);
            TextView exerciseMuscleGroup = view.findViewById(R.id.exe_secondary_muscles);
            TextView exerciseInstructions = view.findViewById(R.id.exe_instructions);

            // Set data to the views
            if (exercise != null) {
                exerciseName.setText(exercise.getName());
                exerciseTarget.setText("Target: " + exercise.getTarget());
                exerciseEquipment.setText("Equipment: " + exercise.getEquipment());
                exerciseBodyPart.setText("Body Part: " + exercise.getBodyPart());
                // הצגת השרירים המשניים
                if (exercise.getSecondaryMuscles() != null && !exercise.getSecondaryMuscles().isEmpty()) {
                    exerciseMuscleGroup.setText("Secondary Muscle Group: " +TextUtils.join(", ", exercise.getSecondaryMuscles()));

                } else {
                    exerciseInstructions.setText("Secondary Muscles: None");
                }

                // הצגת הוראות התרגיל
                if (exercise.getInstructions() != null && !exercise.getInstructions().isEmpty()) {
                    exerciseInstructions.setText(TextUtils.join("\n", exercise.getInstructions()));

                } else {
                    exerciseInstructions.setText("Instructions: No instructions available.");
                }

                // Use Glide to load the gif
                Glide.with(getContext())
                        .load(exercise.getGifUrl())
                        .into(exerciseGif);
            }
        }

        return view;
    }
}