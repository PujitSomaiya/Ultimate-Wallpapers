package com.dp.ultimatewallpapers.view.home.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.dp.ultimatewallpapers.R;
import com.dp.ultimatewallpapers.api.response.HitsItem;
import com.dp.ultimatewallpapers.interfaces.AllClickListeners;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder>{

    private final List<HitsItem> _dataSource = new ArrayList<>();
    private final AllClickListeners.OnImageClick _listener;
    private Context context;
    public RequestOptions options = new RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.loader_animation)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .priority(Priority.HIGH)
            .dontAnimate()
            .dontTransform();

    public ImageAdapter(Context context, AllClickListeners.OnImageClick _listener) {
        this._listener = _listener;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_images, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.item = _dataSource.get(position);

        Glide.with(context).load(holder.item.getWebformatURL()).apply(options).into(holder.imgWallpaper);


        holder.imgWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _listener.onImageClick(holder.getAdapterPosition(),holder.item,false);
            }
        });

        holder.llDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _listener.onImageClick(holder.getAdapterPosition(),holder.item,true);
            }
        });


    }




    @Override
    public int getItemCount() {
        return _dataSource.size();
    }

    public void refresh(List<HitsItem> list) {
        if (list == null) return;
        _dataSource.clear();
        _dataSource.addAll(list);
        notifyDataSetChanged();
    }

    public void add(List<HitsItem> list) {
        if (list == null) return;
        _dataSource.addAll(list);
        notifyDataSetChanged();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public HitsItem item;
        public String itemString;
        private final ImageView imgWallpaper;
        private final LinearLayout llDownload;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            imgWallpaper = view.findViewById(R.id.imgWallpaper);
            llDownload = view.findViewById(R.id.llDownload);
        }
    }
}
