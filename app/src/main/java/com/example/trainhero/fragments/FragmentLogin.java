package com.example.trainhero.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.trainhero.R;
import com.example.trainhero.activities.MainActivity;

public class FragmentLogin extends Fragment {

    public FragmentLogin() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        Button loginBtn = view.findViewById(R.id.buttonFromLoginFragToAfterLoginFrag);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.login(view);
            }
        });

        Button registerBtn = view.findViewById(R.id.buttonFromLoginFragToRegFrag);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_fragmentLogin_to_fragmentRegister);
            }
        });

        return view;
    }
}