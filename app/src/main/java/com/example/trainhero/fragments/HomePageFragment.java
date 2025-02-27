package com.example.trainhero.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.trainhero.R;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class HomePageFragment extends Fragment {

    public HomePageFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home_page, container, false);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference database = FirebaseDatabase.getInstance().getReference();

            database.child("users").child(userId).child("name").get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String userName = task.getResult().getValue(String.class);
                            if (userName == null || userName.isEmpty()) {
                                userName = "User";
                            }

                            int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                            String timeOfDayGreeting;

                            if (currentHour >= 5 && currentHour < 12) {
                                timeOfDayGreeting = "Good Morning";
                            } else if (currentHour >= 12 && currentHour < 17) {
                                timeOfDayGreeting = "Good Afternoon";
                            } else {
                                timeOfDayGreeting = "Good Night";
                            }

                            TextView greetingTextView = view.findViewById(R.id.greetingTextView);
                            greetingTextView.setText(timeOfDayGreeting + ", " + userName + "!");
                        }
                    });
        }

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