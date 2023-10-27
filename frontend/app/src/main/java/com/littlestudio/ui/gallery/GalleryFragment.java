package com.littlestudio.ui.gallery;

import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.littlestudio.DrawAdapter;
import com.littlestudio.R;

import java.io.File;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GalleryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GalleryFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private DrawAdapter adapter;
    public GalleryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment com.example.littlestudio.ui.galleryList.GalleryFragment.
     */
    // TODO: Rename and change types and number of parameters

    public static GalleryFragment newInstance(String param1, String param2) {
        GalleryFragment fragment = new GalleryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_gallery, container, false);

        RecyclerView rcv = (RecyclerView) view.findViewById(R.id.recycler_view);
        adapter = new DrawAdapter(getActivity(), getFilesPath());
        rcv.setAdapter(adapter);

        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        rcv.setLayoutManager(layoutManager);

        return view;
    }

    public ArrayList<String> getFilesPath() {
        ArrayList<String> resultList = new ArrayList<>();
        String imageDir = Environment.DIRECTORY_PICTURES + "/Android Draw/";
        File path = Environment.getExternalStoragePublicDirectory(imageDir);
        path.mkdirs();
        File[] imageList = path.listFiles();
        if (imageList != null) {
            for (File imagePath : imageList) {
                resultList.add(imagePath.getAbsolutePath());
            }
        }
        return resultList;
    }
}