package com.littlestudio.ui.gallery;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.littlestudio.ui.DrawAdapter;
import com.littlestudio.R;
import com.littlestudio.data.datasource.DrawingRemoteDataSource;
import com.littlestudio.data.datasource.UserLocalDataSource;
import com.littlestudio.data.datasource.UserRemoteDataSource;
import com.littlestudio.data.mapper.DrawingMapper;
import com.littlestudio.data.mapper.FamilyMapper;
import com.littlestudio.data.model.Drawing;
import com.littlestudio.data.model.User;
import com.littlestudio.data.repository.DrawingRepository;
import com.littlestudio.data.repository.UserRepository;
import com.littlestudio.ui.TutorialActivity;
import com.littlestudio.ui.constant.IntentExtraKey;
import com.littlestudio.ui.DrawAdapter;
import com.littlestudio.ui.constant.ErrorMessage;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GalleryFragment extends Fragment {
    DrawAdapter adapter;
    DrawingRepository drawingRepository;
    RecyclerView rcv;
    @Nullable User user;

    public GalleryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        drawingRepository = DrawingRepository.getInstance(
                DrawingRemoteDataSource.getInstance(),
                new DrawingMapper(new ObjectMapper(), new FamilyMapper(new ObjectMapper()))
        );
        user = UserRepository.getInstance(
                UserRemoteDataSource.getInstance(),
                UserLocalDataSource.getInstance(getContext())
        ).getUser();
    }

    @Override
    public void onResume() {
        super.onResume();
        syncGalleryList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_gallery, container, false);

        adapter = new DrawAdapter(getContext(), new ArrayList<>());

        rcv = view.findViewById(R.id.recycler_view);
        rcv.setAdapter(adapter);
        AppCompatImageView infoBtn = view.findViewById(R.id.info);

        infoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TutorialActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    public void syncGalleryList() {
        if (user == null) {
            return;
        }
        drawingRepository.getDrawings(user.id, new Callback<List<Drawing>>() {
            @Override
            public void onResponse(Call<List<Drawing>> call, Response<List<Drawing>> response) {
                adapter.updateItems(new ArrayList<>(response.body()));
            }

            @Override
            public void onFailure(Call<List<Drawing>> call, Throwable t) {
                Toast.makeText(getContext(), ErrorMessage.DEFAULT, Toast.LENGTH_SHORT).show();
            }
        });
    }
}