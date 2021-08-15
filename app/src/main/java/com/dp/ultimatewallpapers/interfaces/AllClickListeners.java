package com.dp.ultimatewallpapers.interfaces;

import android.widget.RadioButton;

import com.dp.ultimatewallpapers.api.response.HitsItem;

public interface AllClickListeners {


    interface OnImageClick{
        void onImageClick(int position, HitsItem item, boolean isForDownload);
    }


    interface SetOnBottomDialogButtonClick {
        void setFilter(int number, RadioButton rbImageType, RadioButton rbOrientation);
    }

}
