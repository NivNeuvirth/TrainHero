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

public class FragmentRegister extends Fragment {

    public FragmentRegister() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_register, container, false);

        Button registerBtn = view.findViewById(R.id.buttonFromRegFragToLoginFrag);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity mainActivity = (MainActivity) getActivity();
                if (mainActivity != null && mainActivity.register()) {
                    Navigation.findNavController(view).navigate(R.id.action_fragmentRegister_to_fragmentLogin);
                }
            }
        });

        return view;
    }
}