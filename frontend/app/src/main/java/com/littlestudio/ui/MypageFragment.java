package com.littlestudio.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.littlestudio.R;
import com.littlestudio.data.datasource.UserLocalDataSource;
import com.littlestudio.data.datasource.UserRemoteDataSource;
import com.littlestudio.data.repository.UserRepository;

public class MypageFragment extends Fragment {
    UserRepository userRepository;

    public MypageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userRepository = UserRepository.getInstance(
                UserRemoteDataSource.getInstance(),
                UserLocalDataSource.getInstance(getContext())
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_my_page, container, false);

        Button logoutButton = view.findViewById(R.id.logout_btn);
        logoutButton.setOnClickListener(button -> {
            userRepository.logout();
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
        });

        return view;
    }
}