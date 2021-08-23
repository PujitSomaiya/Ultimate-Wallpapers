package com.pmggroup.ultimatewallpapers.view.home.view;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.pmggroup.ultimatewallpapers.R;
import com.pmggroup.ultimatewallpapers.interfaces.AllClickListeners;
import com.pmggroup.ultimatewallpapers.utils.AnimationUtils;


public class BottomDialog extends DialogFragment implements View.OnClickListener {

    private AllClickListeners.SetOnBottomDialogButtonClick setOnBottomDialogButtonClick;
    private RadioGroup rgImageType,rgOrientation;
    private View view;

    public BottomDialog() {
    }

    @SuppressLint("ValidFragment")
    public BottomDialog(AllClickListeners.SetOnBottomDialogButtonClick setOnBottomDialogButtonClick) {
        this.setOnBottomDialogButtonClick = setOnBottomDialogButtonClick;
    }

    private View mRootView;

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.BOTTOM;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(params);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.getDecorView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                dismiss();
                return true;
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        mRootView = inflater.inflate(R.layout.bottom_filter_layout, container, false);
        AnimationUtils.slideToUp(mRootView);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    private void init(View view) {
        /*rgFilterType = view.findViewById(R.id.rgFilterType);*/
        this.view=view;
        view.findViewById(R.id.btnReset).setOnClickListener(this);
        view.findViewById(R.id.btnSave).setOnClickListener(this);
        rgImageType = view.findViewById(R.id.rgImageType);
        rgOrientation = view.findViewById(R.id.rgOrientation);
        rgImageType.setOnClickListener(this);

    }

    public void dismissDialog() {
        AnimationUtils.slideToDown(mRootView, new AnimationUtils.AnimationListener() {
            @Override
            public void onFinish() {
                BottomDialog.super.dismiss();
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnReset:
                rgImageType.check(R.id.rbAll);
                setOnBottomDialogButtonClick.setFilter(1, new RadioButton(getContext()), new RadioButton(getContext()));
                dismissDialog();
                break;
            case R.id.btnSave:
                int selectedId = rgImageType.getCheckedRadioButtonId();
                RadioButton rbImageType = (RadioButton) view.findViewById(selectedId);

                int selectedId2 = rgOrientation.getCheckedRadioButtonId();
                RadioButton rbOrientation = (RadioButton) view.findViewById(selectedId2);
                setOnBottomDialogButtonClick.setFilter(2,rbImageType,rbOrientation);
                dismissDialog();
                break;
            default:
                break;
        }
    }
}