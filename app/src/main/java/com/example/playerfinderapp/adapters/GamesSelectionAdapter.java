package com.example.playerfinderapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.playerfinderapp.R;

import java.util.List;

public class GamesSelectionAdapter extends RecyclerView.Adapter<GamesSelectionAdapter.ViewHolder> {
    private List<String> allGames;
    private List<String> selectedGames;

    public GamesSelectionAdapter(List<String> allGames, List<String> selectedGames) {
        this.allGames = allGames;
        this.selectedGames = selectedGames;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_game_selection, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String game = allGames.get(position);
        holder.checkBox.setText(game);
        holder.checkBox.setChecked(selectedGames.contains(game));

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!selectedGames.contains(game)) {
                    selectedGames.add(game);
                }
            } else {
                selectedGames.remove(game);
            }
        });
    }

    @Override
    public int getItemCount() {
        return allGames.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;

        ViewHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.game_checkbox);
        }
    }
}