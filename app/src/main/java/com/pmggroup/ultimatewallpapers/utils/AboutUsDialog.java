package com.pmggroup.ultimatewallpapers.utils;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;


import com.pmggroup.ultimatewallpapers.R;

import org.jetbrains.annotations.NotNull;

public class AboutUsDialog extends DialogFragment {

    public static final String TAG = "example_dialog";
    FragmentManager fragmentManager;
    private ImageView imgCancel;

    public AboutUsDialog(FragmentManager fragmentManagerRec) {
        fragmentManager = fragmentManagerRec;
    }

    public AboutUsDialog display() {
        AboutUsDialog exampleDialog = new AboutUsDialog(fragmentManager);
        exampleDialog.show(fragmentManager, TAG);
        return exampleDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.layout_info, container, false);
        imgCancel = view.findViewById(R.id.imgCancel);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imgCancel.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                }
        );
    }

    @Override
    public void onDismiss(@NonNull @NotNull DialogInterface dialog) {
        super.onDismiss(dialog);

    }
}