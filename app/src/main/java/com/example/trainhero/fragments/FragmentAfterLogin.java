package com.example.trainhero.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import com.example.trainhero.ExerciseAdapter;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.trainhero.R;
import com.example.trainhero.models.Exercise;
import com.example.trainhero.services.DataServices;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentAfterLogin#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentAfterLogin extends Fragment {

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
            exerciseAdapter = new ExerciseAdapter(exerciseList);
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

            exerciseAdapter = new ExerciseAdapter(singleExerciseList);
            recyclerView.setAdapter(exerciseAdapter);
        } else {
            Toast.makeText(getContext(), "Exercise not found", Toast.LENGTH_SHORT).show();
        }
    }
}