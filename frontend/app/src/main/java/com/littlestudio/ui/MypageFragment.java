package com.littlestudio.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.littlestudio.R;
import com.littlestudio.data.datasource.UserLocalDataSource;
import com.littlestudio.data.datasource.UserRemoteDataSource;
import com.littlestudio.data.dto.FamilyListResponseDto;
import com.littlestudio.data.model.User;
import com.littlestudio.data.repository.UserRepository;
import com.littlestudio.ui.constant.ErrorMessage;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MypageFragment extends Fragment {
    UserRepository userRepository;

    List<User> familyMembers;
    FamilyMembersAdapter familyMembersAdapter;
    private RecyclerView familyRecyclerView;


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

        familyMembersAdapter = new FamilyMembersAdapter(familyMembers, userRepository.getUser());

        userRepository.getFamily(new Callback<FamilyListResponseDto>() {
            @Override
            public void onResponse(Call<FamilyListResponseDto> call, Response<FamilyListResponseDto> response) {
                familyMembers = response.body().users;
                familyMembersAdapter.setFamilyMembers(familyMembers);
            }

            @Override
            public void onFailure(Call<FamilyListResponseDto> call, Throwable t) {
                Toast.makeText(getContext(), ErrorMessage.DEFAULT, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_my_page, container, false);

        this.familyRecyclerView = view.findViewById(R.id.family_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        this.familyRecyclerView.setLayoutManager(linearLayoutManager);
        this.familyRecyclerView.setAdapter(familyMembersAdapter);

        Button logoutButton = view.findViewById(R.id.logout_btn);
        logoutButton.setOnClickListener(button -> {
            userRepository.logout();
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
        });

        return view;
    }
}