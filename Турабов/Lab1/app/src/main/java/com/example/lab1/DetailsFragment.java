package com.example.lab1;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailsFragment extends Fragment {

    public DetailsFragment() {
        // Required empty public constructor
    }

    public static DetailsFragment newInstance(String param1, String param2) {
        DetailsFragment fragment = new DetailsFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        Bundle arguments = getActivity().getIntent().getExtras();
        Benchmark selected = (Benchmark) arguments.getSerializable(Benchmark.class.getSimpleName());
        ImageView imageView = view.findViewById(R.id.imageView);
        TextView fullNameView = view.findViewById(R.id.fullname);
        TextView vendorView = view.findViewById(R.id.vendor);
        TextView typeView = view.findViewById(R.id.type);
        TextView medianView = view.findViewById(R.id.median);
        TextView numView = view.findViewById(R.id.num);

        imageView.setImageResource(selected.getVendor());
        fullNameView.setText(selected.getDevice());
        vendorView.setText(selected.getDevice().contains("NVIDIA")?"NVIDIA":"AMD");
        typeView.setText(selected.getDevice().contains("Processor")?"Processor(CPU)":"Graphic card(GPU)");
        medianView.setText(selected.getScore());
        numView.setText(selected.getNum_of_benchmarks());

    }
}