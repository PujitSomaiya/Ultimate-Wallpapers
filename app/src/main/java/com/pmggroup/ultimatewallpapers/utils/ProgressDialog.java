package com.pmggroup.ultimatewallpapers.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import androidx.core.content.ContextCompat;

import com.pmggroup.ultimatewallpapers.R;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class ProgressDialog {

    public static Dialog dialog;

    public static void showProgress(Context context) {
        try {
            if (context == null) {
                return;
            }
            if (dialog != null) dismissProgress();

            dialog = new Dialog(context);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(context, android.R.color.transparent));
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.progress_dialog, null);
            dialog.setContentView(layout);
            dialog.show();


        } catch (Exception e) {

        }
    }

    public static Boolean checkProgressOpen() {
        if (dialog != null) {
            return dialog.isShowing();
        } else {
            return false;
        }
    }

    public static void dismissProgress() {
        if (checkProgressOpen()) {
            try {
                dialog.dismiss();
            } catch (Exception e) {

            }
        }
    }
}
