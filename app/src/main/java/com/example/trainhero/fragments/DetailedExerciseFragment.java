package com.example.trainhero.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.trainhero.R;

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

        return inflater.inflate(R.layout.fragment_detailed_exercise, container, false);
    }
}