package com.example.trainhero.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.trainhero.R;
import com.google.android.material.card.MaterialCardView;

public class HomePageFragment extends Fragment {

    public HomePageFragment() {
        // Required empty public constructor
    }
    public static HomePageFragment newInstance(String param1, String param2) {
        return new HomePageFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_home_page, container, false);

        MaterialCardView exploreCard = view.findViewById(R.id.exploreCard);
        exploreCard.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_homePageFragment_to_fragmentAfterLogin);
        });

        MaterialCardView favoritesCard = view.findViewById(R.id.favoritesCard);
        favoritesCard.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_homePageFragment_to_favoritesFragment);
        });

        MaterialCardView gymsNearByCard = view.findViewById(R.id.nearByCard);
        gymsNearByCard.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_homePageFragment_to_gymsNearByFragment);
        });

        MaterialCardView equipmentCard = view.findViewById(R.id.equipmentCard);
        equipmentCard.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_homePageFragment_to_byEquipmentFragment);
        });

        return view;
    }
}