package com.example.image;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.image.databinding.FragmentBottomSheetWriteBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;


public class BottomSheetWriteFragment extends BottomSheetDialogFragment {
    FragmentBottomSheetWriteBinding binding;
    private BottomSheetListener mListener;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentBottomSheetWriteBinding.inflate(inflater, container, false);
        View v = binding.getRoot();
        mListener = (BottomSheetListener)getContext();
        binding.gallery.setOnClickListener(view -> {
            mListener.onClickGallery();
        });
        binding.camera.setOnClickListener(view -> {
            mListener.onClickCamera();
        });
        binding.basics.setOnClickListener(view -> {
            mListener.onClickBasics();
        });
        return v;
    }
    public interface BottomSheetListener{
        void onClickGallery();
        void onClickCamera();
        void onClickBasics();
    }
}