package com.pmggroup.ultimatewallpapers.view.home.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.pmggroup.ultimatewallpapers.R;
import com.pmggroup.ultimatewallpapers.api.response.SuggestionItem;
import com.pmggroup.ultimatewallpapers.interfaces.AllClickListeners;

import java.util.ArrayList;
import java.util.List;

public class SuggestionsAdapter extends RecyclerView.Adapter<SuggestionsAdapter.ViewHolder>{

    private final List<SuggestionItem> _dataSource = new ArrayList<>();
    private final AllClickListeners.OnSuggestionClick _listener;
    private Context context;

    public SuggestionsAdapter(Context context, AllClickListeners.OnSuggestionClick _listener) {
        this._listener = _listener;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_top_suggestions, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.item = _dataSource.get(position);

        holder.tvSuggestion.setText(holder.item.getSuggestion());

        if (holder.item.isSelected()){
            holder.tvSuggestion.setTextColor(ContextCompat.getColor(context,R.color.color_main_background));
            holder.llSuggestions.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_select_suggestion));
        }else {
            holder.tvSuggestion.setTextColor(ContextCompat.getColor(context,R.color.color_theme_00FFFF));
            holder.llSuggestions.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_unselect_suggestion));
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i=0;i<_dataSource.size();i++){
                    _dataSource.get(i).setSelected(false);
                }
                _dataSource.get(position).setSelected(true);
                _listener.onSuggestionClick(holder.getAdapterPosition(),holder.item.getSuggestion());
                notifyDataSetChanged();
            }
        });


    }

    @Override
    public int getItemCount() {
        return _dataSource.size();
    }

    public void refresh(List<SuggestionItem> list) {
        if (list == null) return;
        _dataSource.clear();
        _dataSource.addAll(list);
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public SuggestionItem item;
        public String itemString;
        private final TextView tvSuggestion;
        private final LinearLayout llSuggestions;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            tvSuggestion = view.findViewById(R.id.tvSuggestion);
            llSuggestions = view.findViewById(R.id.llSuggestions);
        }
    }
}
