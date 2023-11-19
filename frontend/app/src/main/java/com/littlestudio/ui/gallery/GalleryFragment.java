package com.littlestudio.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GalleryFragment extends Fragment {
    DrawAdapter adapter;
    DrawingRepository drawingRepository;
    RecyclerView rcv;
    User user;

    public GalleryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        drawingRepository = new DrawingRepository(
                new DrawingRemoteDataSource(),
                new DrawingMapper(new ObjectMapper(), new FamilyMapper(new ObjectMapper()))
        );
        user = new UserRepository(
                new UserRemoteDataSource(),
                new UserLocalDataSource(getContext())
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

        rcv = (RecyclerView) view.findViewById(R.id.recycler_view);
        rcv.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        rcv.setAdapter(adapter);

        return view;
    }

    public void syncGalleryList() {
        drawingRepository.getDrawings(user.id, new Callback<List<Drawing>>() {
            @Override
            public void onResponse(Call<List<Drawing>> call, Response<List<Drawing>> response) {
                adapter.updateItems(new ArrayList<>(response.body()));
            }

            @Override
            public void onFailure(Call<List<Drawing>> call, Throwable t) {
                // 에러 시 처리
            }
        });
    }
}