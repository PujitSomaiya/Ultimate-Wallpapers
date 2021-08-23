package com.pmggroup.ultimatewallpapers.interfaces;

import android.widget.RadioButton;

import com.pmggroup.ultimatewallpapers.api.response.HitsItem;

public interface AllClickListeners {


    interface OnImageClick{
        void onImageClick(int position, HitsItem item, boolean isForDownload);
    }
    interface OnSuggestionClick{
        void onSuggestionClick(int position, String text);
    }


    interface SetOnBottomDialogButtonClick {
        void setFilter(int number, RadioButton rbImageType, RadioButton rbOrientation);
    }

}
